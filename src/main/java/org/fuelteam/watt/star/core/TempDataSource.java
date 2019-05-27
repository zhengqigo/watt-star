package org.fuelteam.watt.star.core;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.util.Assert;

public class TempDataSource implements DataSource {

    private final Map<String, ? extends DataSource> dataSourceMap;
    private DataSource dataSource;
    private final String dbName;
    private final String dataSourceName;
    private final SqlHandler sqlHandler;
    private final String useDb;

    public TempDataSource(Map<String, ? extends DataSource> dataSourceMap, String dataSourceName, String dbName) {
        super();
        this.dataSourceMap = dataSourceMap;
        this.dataSourceName = dataSourceName;
        this.dbName = dbName;
        this.useDb = "use " + dbName + ";";
        this.sqlHandler = new TempSqlHandler();
    }

    public TempDataSource(DataSource dataSource, String dbName) {
        super();
        this.dataSource = dataSource;
        this.dbName = dbName;
        this.useDb = "use " + dbName + ";";
        this.dataSourceName = null;
        this.dataSourceMap = null;
        this.sqlHandler = new TempSqlHandler();
    }

    public DataSource getDataSource() {
        if (dataSource != null) return dataSource;
        synchronized (dataSourceMap) {
            if (dataSource != null) return dataSource;
            Assert.isTrue(dataSourceMap.containsKey(dataSourceName), "dataSource " + dataSourceName + " not exist");
            dataSource = dataSourceMap.get(dataSourceName);
            return dataSource;
        }
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return getDataSource().getLogWriter();
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        getDataSource().setLogWriter(out);
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        getDataSource().setLoginTimeout(seconds);
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return getDataSource().getLoginTimeout();
    }

    @Override
    public java.util.logging.Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return getDataSource().getParentLogger();
    }

    @Override
    public <T> T unwrap(Class<T> clazz) throws SQLException {
        return getDataSource().unwrap(clazz);
    }

    @Override
    public boolean isWrapperFor(Class<?> clazz) throws SQLException {
        return getDataSource().isWrapperFor(clazz);
    }

    @Override
    public Connection getConnection() throws SQLException {
        return this.useDb(getDataSource().getConnection());
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return this.useDb(getDataSource().getConnection(username, password));
    }

    public String getDbName() {
        return dbName;
    }

    private Connection useDb(final Connection connection) throws SQLException {
        connection.prepareCall(this.useDb).executeQuery();
        return new TempConnection(connection, sqlHandler);
    }
}