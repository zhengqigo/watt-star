package org.fuelteam.watt.star.core;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fuelteam.watt.star.properties.DruidProperties;
import org.fuelteam.watt.star.properties.NodesProperties;
import org.springframework.jdbc.datasource.AbstractDataSource;
import org.springframework.scheduling.annotation.Async;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.util.JdbcUtils;
import com.google.common.collect.Maps;

public class DynamicDataSource extends AbstractDataSource {

    private static final Logger logger = LogManager.getLogger();

    private final Dialect dialect;

    private volatile int select = 0;

    private final String dataSourceName;

    private final DruidDataSource masterDataSource;

    private final Map<String, DruidDataSource> slaveDataSources = Maps.newConcurrentMap();

    private final List<String> slavesDataSourceNames = Collections.synchronizedList(new ArrayList<String>());

    private final List<String> slavesFailureDataSourceNames = Collections.synchronizedList(new ArrayList<String>());

    private static final ThreadLocal<Stack<Boolean>> master = new ThreadLocal<Stack<Boolean>>() {
        protected Stack<Boolean> initialValue() {
            return new Stack<Boolean>();
        }
    };

    protected static void useMaster() {
        master.get().push(true);
    }

    protected static void useSlave() {
        master.get().push(false);
    }

    protected static void reset() {
        master.get().pop();
        if (master.get().size() == 0) master.remove();
    }

    @Override
    public Connection getConnection() throws SQLException {
        try {
            return determineTargetDataSourceConnection();
        } catch (SQLException e) {
            return determineTargetDataSourceConnection();
        }
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        try {
            return determineTargetDataSourceConnection(username, password);
        } catch (SQLException e) {
            return determineTargetDataSourceConnection(username, password);
        }
    }

    private Connection determineTargetDataSourceConnection() throws SQLException {
        String lookupKey = determineCurrentLookupKey();
        DataSource datasource = determineTargetDataSource(lookupKey);
        try {
            return datasource.getConnection();
        } catch (SQLException sqle) {
            this.recordFailure(datasource, lookupKey, sqle);
            throw sqle;
        }
    }

    private Connection determineTargetDataSourceConnection(String username, String password) throws SQLException {
        String lookupKey = determineCurrentLookupKey();
        DataSource datasource = determineTargetDataSource(lookupKey);
        try {
            return datasource.getConnection(username, password);
        } catch (SQLException sqle) {
            this.recordFailure(datasource, lookupKey, sqle);
            throw sqle;
        }
    }

    private void recordFailure(DataSource datasource, String lookupKey, SQLException e) {
        if (this.masterDataSource.equals(datasource)) {
            logger.error("master {} with error {} {} {}", this.masterDataSource, e.getMessage(), e.getSQLState(),
                    e.getErrorCode());
            return;
        }
        slavesDataSourceNames.remove(lookupKey);
        slavesFailureDataSourceNames.add(lookupKey);
        logger.warn("slave {} shutdown already with exception {} {} {}", lookupKey, e.getMessage(), e.getSQLState(),
                e.getErrorCode());
    }

    @Async
    public void retryFailureSlavesDataSource() {
        if (slavesFailureDataSourceNames.isEmpty()) return;
        Iterator<String> it = slavesFailureDataSourceNames.iterator();
        while (it.hasNext()) {
            String lookupKey = (String) it.next();
            try {
                determineTargetDataSource(lookupKey).getConnection();
                slavesFailureDataSourceNames.remove(lookupKey);
                slavesDataSourceNames.add(lookupKey);
                logger.info("slave {} revovered back", lookupKey);
            } catch (SQLException sqle) {
                logger.error("slave {} yet failed with exception {}", lookupKey, sqle);
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T unwrap(Class<T> clazz) throws SQLException {
        if (clazz.isInstance(this)) return (T) this;
        String lookupKey = determineCurrentLookupKey();
        return determineTargetDataSource(lookupKey).unwrap(clazz);
    }

    @Override
    public boolean isWrapperFor(Class<?> clazz) throws SQLException {
        String lookupKey = determineCurrentLookupKey();
        return (clazz.isInstance(this) || determineTargetDataSource(lookupKey).isWrapperFor(clazz));
    }

    protected DataSource determineTargetDataSource(String lookupKey) {
        if (lookupKey == null) return this.masterDataSource;
        DataSource dataSource = this.slaveDataSources.get(lookupKey);
        if (dataSource != null) return dataSource;
        return this.masterDataSource;
    }

    protected String determineCurrentLookupKey() {
        if (master.get().isEmpty() || master.get().peek()) return null;
        if (!slavesDataSourceNames.isEmpty()) {
            int slaveCount = slavesDataSourceNames.size();
            String slavesDataSourceName = slavesDataSourceNames.get(select % slaveCount);
            select = (++select % slaveCount);
            logger.debug("switch to and used slave {}", slavesDataSourceName);
            return slavesDataSourceName;
        } else {
            logger.info("used master as slave {} not available", this.dataSourceName);
        }
        return null;
    }

    public static DynamicDataSource create(NodesProperties druidNode, DruidProperties defaultDruidProperties,
            String dataSourceName) throws SQLException {
        return new DynamicDataSource(druidNode, defaultDruidProperties, dataSourceName);
    }

    public DynamicDataSource(NodesProperties nodesProperties, DruidProperties druidProperties, String dataSourceName)
            throws SQLException {
        this.dataSourceName = dataSourceName;
        DruidProperties master = nodesProperties.getMaster();
        if (master == null) master = new DruidProperties();
        master.defaultEmpty().merge(druidProperties).setDefaultReadOnly(false);
        this.masterDataSource = master.createDataSource();
        this.masterDataSource.setName(dataSourceName + "Master");
        List<DruidProperties> slaves = nodesProperties.getSlaves();
        if (slaves != null && !slaves.isEmpty()) {
            for (int i = 0; i < slaves.size(); i++) {
                DruidProperties slave = slaves.get(i);
                if (slave == null) continue;
                slave.defaultEmpty().merge(druidProperties).setDefaultReadOnly(true);
                String slaveDatasourceName = dataSourceName + "Slave-" + i;
                this.slavesDataSourceNames.add(slaveDatasourceName);
                DruidDataSource datasource = slave.createDataSource();
                datasource.setName(slaveDatasourceName);
                this.slaveDataSources.put(slaveDatasourceName, datasource);
            }
        }
        String rawUrl = master.getUrl();
        String dbType = JdbcUtils.getDbType(rawUrl, null);
        this.dialect = Dialect.of(dbType);
    }

    public void init() throws SQLException {
        this.masterDataSource.init();
        Iterator<DruidDataSource> it = this.slaveDataSources.values().iterator();
        while (it.hasNext()) {
            DruidDataSource druidDataSource = (DruidDataSource) it.next();
            try {
                druidDataSource.init();
            } catch (SQLException sqle) {
                logger.error("initialized {} with exception {}", druidDataSource.getName(), sqle);
            }
        }
    }

    public void close() {
        this.masterDataSource.close();
        Iterator<DruidDataSource> it = this.slaveDataSources.values().iterator();
        while (it.hasNext()) {
            DruidDataSource druidDataSource = (DruidDataSource) it.next();
            try {
                druidDataSource.close();
            } catch (Exception e) {
                logger.error("closed {} with exception {}", druidDataSource.getName(), e);
            }
        }
    }

    public DruidDataSource masterDataSource() {
        return masterDataSource;
    }

    public String getDataSourceName() {
        return dataSourceName;
    }

    public Dialect getDialect() {
        return dialect;
    }
}
