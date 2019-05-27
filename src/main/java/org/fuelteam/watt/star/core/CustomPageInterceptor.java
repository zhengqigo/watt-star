package org.fuelteam.watt.star.core;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import com.github.pagehelper.PageInterceptor;

import io.shardingjdbc.core.constant.DatabaseType;
import io.shardingjdbc.core.jdbc.core.datasource.ShardingDataSource;

@Intercepts({
        @Signature(type = Executor.class, method = "query", args = { MappedStatement.class, Object.class, RowBounds.class,
                ResultHandler.class }),
        @Signature(type = Executor.class, method = "query", args = { MappedStatement.class, Object.class, RowBounds.class,
                ResultHandler.class, CacheKey.class, BoundSql.class }) })
public class CustomPageInterceptor implements Interceptor {

    private static final Map<DatabaseType, PageInterceptor> pageHelpers = new HashMap<DatabaseType, PageInterceptor>();

    static {
        pageHelpers.put(DatabaseType.H2, new PageInterceptor());
        pageHelpers.put(DatabaseType.MySQL, new PageInterceptor());
        pageHelpers.put(DatabaseType.Oracle, new PageInterceptor());
        pageHelpers.put(DatabaseType.SQLServer, new PageInterceptor());
        pageHelpers.put(DatabaseType.PostgreSQL, new PageInterceptor());
        Iterator<Entry<DatabaseType, PageInterceptor>> it = pageHelpers.entrySet().iterator();
        while (it.hasNext()) {
            Entry<DatabaseType, PageInterceptor> entry = (Entry<DatabaseType, PageInterceptor>) it.next();
            Properties properties = new Properties();
            entry.getValue().setProperties(properties);
        }
    }

    private final Interceptor pageHelper;
    private final DatabaseType databaseType;

    public CustomPageInterceptor(DatabaseType databaseType) {
        this.databaseType = databaseType;
        this.pageHelper = pageHelpers.get(databaseType);
        Properties properties = new Properties();
        properties.setProperty("helperDialect", databaseType.name());
        this.pageHelper.setProperties(properties);
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object object = invocation.getArgs()[0];
        if (!(object instanceof MappedStatement)) return pageHelper.intercept(invocation);
        MappedStatement statement = (MappedStatement) object;
        Configuration config = statement.getConfiguration();
        DataSource dataSource = config.getEnvironment().getDataSource();
        if (!(dataSource instanceof ShardingDataSource)) return pageHelper.intercept(invocation);
        DatabaseType databaseType = ((ShardingDataSource) dataSource).getDatabaseType();
        if (!pageHelpers.containsKey(databaseType)) return pageHelper.intercept(invocation);
        return pageHelpers.get(databaseType).intercept(invocation);
    }

    @Override
    public Object plugin(Object target) {
        if (target instanceof Executor) return Plugin.wrap(target, this);
        return target;
    }

    @Override
    public void setProperties(Properties properties) {
        pageHelper.setProperties(properties);
    }

    public DatabaseType getDatabaseType() {
        return databaseType;
    }
}