package org.fuelteam.watt.star.plugins;

import java.sql.Connection;
import java.util.Properties;
import java.util.Set;

import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Intercepts({
        @Signature(type = StatementHandler.class, method = "prepare", args = { Connection.class, Integer.class }) })
public class ShardingInterceptor implements Interceptor {

    private static final Logger logger = LogManager.getLogger();

    protected static ThreadLocal<String> suffix = new ThreadLocal<String>();

    public Object intercept(Invocation invocation) throws Throwable {
        if (suffix.get() == null) return invocation.proceed();
        if (invocation.getTarget() instanceof StatementHandler) {
            StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
            MetaObject metaStatementHandler = SystemMetaObject.forObject(statementHandler);
            BoundSql boundSql = (BoundSql) metaStatementHandler.getValue("delegate.boundSql");
            String sql = boundSql.getSql();
            String rewritedSql = rewriteSql(sql);
            metaStatementHandler.setValue("delegate.boundSql.sql", rewritedSql);
        }
        return invocation.proceed();
    }

    public String rewriteSql(String sql) {
        String tobeRewritedSql = sql;
        Set<String> table = ShardingUtils.getTable(tobeRewritedSql);
        String str = suffix.get();
        if (str != null && str.length() > 0) {
            for (String name : table) {
                tobeRewritedSql = tobeRewritedSql.replace(name, name + "_" + str);
                logger.debug("Origin table {} ==> Rewrited {}", name, name + "_" + str);
            }
        }
        suffix.remove();
        return tobeRewritedSql;
    }

    public Object plugin(Object target) {
        if (target instanceof StatementHandler) return Plugin.wrap(target, this);
        return target;
    }

    public void setProperties(Properties properties) {}
}
