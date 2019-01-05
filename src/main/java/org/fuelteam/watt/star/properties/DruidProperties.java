package org.fuelteam.watt.star.properties;

import java.sql.SQLException;
import java.util.Properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.alibaba.druid.pool.DruidDataSource;

@ConfigurationProperties(DruidProperties.Prefix)
public class DruidProperties {

    public final static String Prefix = "spring.druid.default";

    private String url;

    private String username;

    private String password;

    private String driverClassName;

    private Properties connectProperties;

    private Integer initialSize;

    private Integer minIdle;

    private Integer maxActive;

    private Long maxWait;

    private String filters = "wall,stat";

    private Boolean defaultAutoCommit;

    private Long timeBetweenConnectErrorMillis;

    private String validationQuery;

    private Boolean testWhileIdle;

    private Boolean testOnBorrow;

    private Boolean testOnReturn;

    private Boolean poolPreparedStatements;

    private Boolean clearFiltersEnable;

    private Boolean defaultReadOnly;

    private Boolean asyncCloseConnectionEnable;

    private Integer connectionErrorRetryAttempts;

    private Boolean breakAfterAcquireFailure;

    private Boolean dupCloseLogEnable;

    private Boolean enable;

    private Boolean logAbandoned;

    private Boolean logDifferentThread;

    private Integer loginTimeout;

    private Boolean accessToUnderlyingConnectionAllowed;

    private Integer maxPoolPreparedStatementPerConnectionSize;

    private Integer queryTimeout;

    private Boolean failFast;

    private Integer maxCreateTaskCount;

    private Boolean removeAbandoned;

    private Long removeAbandonedTimeoutMillis;

    private Integer defaultTransactionIsolation;

    private Long timeBetweenEvictionRunsMillis;

    private Long minEvictableIdleTimeMillis;

    private Long maxEvictableIdleTimeMillis;

    private Integer maxOpenPreparedStatements;

    private Integer notFullTimeoutRetryCount;

    private Long timeBetweenLogStatsMillis;

    private Integer validationQueryTimeout;

    public DruidProperties() {}

    public DruidDataSource createDataSource() throws SQLException {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setDriverClassName(driverClassName);
        dataSource.setConnectProperties(connectProperties);
        dataSource.setInitialSize(initialSize);
        dataSource.setMinIdle(minIdle);
        dataSource.setMaxActive(maxActive);
        dataSource.setMaxWait(maxWait);
        dataSource.setFilters(filters);
        dataSource.setDefaultAutoCommit(defaultAutoCommit);
        dataSource.setTimeBetweenConnectErrorMillis(timeBetweenConnectErrorMillis);
        dataSource.setValidationQuery(validationQuery);
        dataSource.setValidationQueryTimeout(validationQueryTimeout);
        dataSource.setTestWhileIdle(testWhileIdle);
        dataSource.setTestOnBorrow(testOnBorrow);
        dataSource.setTestOnReturn(testOnReturn);
        dataSource.setPoolPreparedStatements(poolPreparedStatements);
        dataSource.setClearFiltersEnable(clearFiltersEnable);
        dataSource.setDefaultReadOnly(defaultReadOnly);
        dataSource.setAsyncCloseConnectionEnable(asyncCloseConnectionEnable);
        dataSource.setConnectionErrorRetryAttempts(connectionErrorRetryAttempts);
        dataSource.setBreakAfterAcquireFailure(breakAfterAcquireFailure);
        dataSource.setDupCloseLogEnable(dupCloseLogEnable);
        dataSource.setEnable(enable);
        dataSource.setLogAbandoned(logAbandoned);
        dataSource.setLogDifferentThread(logDifferentThread);
        dataSource.setLoginTimeout(loginTimeout);
        dataSource.setAccessToUnderlyingConnectionAllowed(accessToUnderlyingConnectionAllowed);
        dataSource.setMaxPoolPreparedStatementPerConnectionSize(maxPoolPreparedStatementPerConnectionSize);
        dataSource.setQueryTimeout(queryTimeout);
        dataSource.setFailFast(failFast);
        dataSource.setMaxCreateTaskCount(maxCreateTaskCount);
        dataSource.setRemoveAbandoned(removeAbandoned);
        dataSource.setRemoveAbandonedTimeoutMillis(removeAbandonedTimeoutMillis);
        dataSource.setDefaultTransactionIsolation(defaultTransactionIsolation);
        dataSource.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
        dataSource.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
        dataSource.setMaxEvictableIdleTimeMillis(maxEvictableIdleTimeMillis);
        dataSource.setMaxOpenPreparedStatements(maxOpenPreparedStatements);
        dataSource.setNotFullTimeoutRetryCount(notFullTimeoutRetryCount);
        dataSource.setTimeBetweenLogStatsMillis(timeBetweenLogStatsMillis);
        return dataSource;
    }

    public DruidProperties defaultEmpty() {
        this.initialSize = 5;
        this.minIdle = 10;
        this.maxActive = 20;
        this.maxWait = 60000L;
        this.timeBetweenEvictionRunsMillis = 60000L;
        this.minEvictableIdleTimeMillis = 300000L;
        this.validationQuery = "SELECT 0;";
        this.validationQueryTimeout = 28000;
        this.testWhileIdle = true;
        this.testOnBorrow = false;
        this.testOnReturn = false;
        this.poolPreparedStatements = true;
        this.maxPoolPreparedStatementPerConnectionSize = 20;
        this.timeBetweenConnectErrorMillis = 6000L;
        this.filters = "wall,stat";
        this.defaultAutoCommit = true;
        this.clearFiltersEnable = false;
        this.defaultReadOnly = false;
        this.asyncCloseConnectionEnable = true;
        this.connectionErrorRetryAttempts = 3;
        this.breakAfterAcquireFailure = false;
        this.dupCloseLogEnable = true;
        this.enable = true;
        this.logAbandoned = true;
        this.logDifferentThread = true;
        this.loginTimeout = 5000;
        this.accessToUnderlyingConnectionAllowed = true;
        this.queryTimeout = 3000;
        this.failFast = true;
        this.maxCreateTaskCount = 4;
        this.removeAbandoned = true;
        this.removeAbandonedTimeoutMillis = 3600000L;
        this.defaultTransactionIsolation = 1;
        this.maxEvictableIdleTimeMillis = 3000000L;
        this.maxOpenPreparedStatements = 200;
        this.notFullTimeoutRetryCount = 500;
        this.timeBetweenLogStatsMillis = 300000L;
        return this;
    }

    public DruidProperties merge(DruidProperties druidProperties) {
        if (druidProperties == null) return this;

        if (this.connectProperties == null) connectProperties = new Properties();
        if (druidProperties.connectProperties != null) this.connectProperties.putAll(druidProperties.connectProperties);

        this.url = merge(this.url, druidProperties.url);
        this.username = merge(this.username, druidProperties.username);
        this.password = merge(this.password, druidProperties.password);
        this.driverClassName = merge(this.driverClassName, druidProperties.driverClassName);
        this.initialSize = merge(this.initialSize, druidProperties.initialSize);
        this.minIdle = merge(this.minIdle, druidProperties.minIdle);
        this.maxActive = merge(this.maxActive, druidProperties.maxActive);
        this.maxWait = merge(this.maxWait, druidProperties.maxWait);
        this.filters = merge(this.filters, druidProperties.filters);
        this.defaultAutoCommit = merge(this.defaultAutoCommit, druidProperties.defaultAutoCommit);
        this.timeBetweenConnectErrorMillis = merge(this.timeBetweenConnectErrorMillis,
                druidProperties.timeBetweenConnectErrorMillis);
        this.validationQuery = merge(this.validationQuery, druidProperties.validationQuery);
        this.testWhileIdle = merge(this.testWhileIdle, druidProperties.testWhileIdle);
        this.testOnBorrow = merge(this.testOnBorrow, druidProperties.testOnBorrow);
        this.testOnReturn = merge(this.testOnReturn, druidProperties.testOnReturn);
        this.poolPreparedStatements = merge(this.poolPreparedStatements, druidProperties.poolPreparedStatements);
        this.clearFiltersEnable = merge(this.clearFiltersEnable, druidProperties.clearFiltersEnable);
        this.defaultReadOnly = merge(this.defaultReadOnly, druidProperties.defaultReadOnly);
        this.asyncCloseConnectionEnable = merge(this.asyncCloseConnectionEnable,
                druidProperties.asyncCloseConnectionEnable);
        this.connectionErrorRetryAttempts = merge(this.connectionErrorRetryAttempts,
                druidProperties.connectionErrorRetryAttempts);
        this.breakAfterAcquireFailure = merge(this.breakAfterAcquireFailure, druidProperties.breakAfterAcquireFailure);
        this.dupCloseLogEnable = merge(this.dupCloseLogEnable, druidProperties.dupCloseLogEnable);
        this.enable = merge(this.enable, druidProperties.enable);
        this.logAbandoned = merge(this.logAbandoned, druidProperties.logAbandoned);
        this.logDifferentThread = merge(this.logDifferentThread, druidProperties.logDifferentThread);
        this.loginTimeout = merge(this.loginTimeout, druidProperties.loginTimeout);
        this.accessToUnderlyingConnectionAllowed = merge(this.accessToUnderlyingConnectionAllowed,
                druidProperties.accessToUnderlyingConnectionAllowed);
        this.maxPoolPreparedStatementPerConnectionSize = merge(this.maxPoolPreparedStatementPerConnectionSize,
                druidProperties.maxPoolPreparedStatementPerConnectionSize);
        this.queryTimeout = merge(this.queryTimeout, druidProperties.queryTimeout);
        this.failFast = merge(this.failFast, druidProperties.failFast);
        this.maxCreateTaskCount = merge(this.maxCreateTaskCount, druidProperties.maxCreateTaskCount);
        this.removeAbandoned = merge(this.removeAbandoned, druidProperties.removeAbandoned);
        this.removeAbandonedTimeoutMillis = merge(this.removeAbandonedTimeoutMillis,
                druidProperties.removeAbandonedTimeoutMillis);
        this.defaultTransactionIsolation = merge(this.defaultTransactionIsolation,
                druidProperties.defaultTransactionIsolation);
        this.timeBetweenEvictionRunsMillis = merge(this.timeBetweenEvictionRunsMillis,
                druidProperties.timeBetweenEvictionRunsMillis);
        this.minEvictableIdleTimeMillis = merge(this.minEvictableIdleTimeMillis,
                druidProperties.minEvictableIdleTimeMillis);
        this.maxEvictableIdleTimeMillis = merge(this.maxEvictableIdleTimeMillis,
                druidProperties.maxEvictableIdleTimeMillis);

        this.maxOpenPreparedStatements = merge(this.maxOpenPreparedStatements,
                druidProperties.maxOpenPreparedStatements);
        this.notFullTimeoutRetryCount = merge(this.notFullTimeoutRetryCount, druidProperties.notFullTimeoutRetryCount);
        this.timeBetweenLogStatsMillis = merge(this.timeBetweenLogStatsMillis,
                druidProperties.timeBetweenLogStatsMillis);
        this.validationQueryTimeout = merge(this.validationQueryTimeout, druidProperties.validationQueryTimeout);
        return this;
    }

    private <T> T merge(T thisField, T mergefield) {
        if (thisField == null && mergefield != null) return mergefield;
        return thisField;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDriverClassName() {
        return driverClassName;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    public Integer getInitialSize() {
        return initialSize;
    }

    public void setInitialSize(Integer initialSize) {
        this.initialSize = initialSize;
    }

    public Integer getMinIdle() {
        return minIdle;
    }

    public void setMinIdle(Integer minIdle) {
        this.minIdle = minIdle;
    }

    public Integer getMaxActive() {
        return maxActive;
    }

    public void setMaxActive(Integer maxActive) {
        this.maxActive = maxActive;
    }

    public Long getMaxWait() {
        return maxWait;
    }

    public void setMaxWait(Long maxWait) {
        this.maxWait = maxWait;
    }

    public String getFilters() {
        return filters;
    }

    public void setFilters(String filters) {
        this.filters = filters;
    }

    public Properties getConnectProperties() {
        return connectProperties;
    }

    public void setConnectProperties(Properties connectProperties) {
        this.connectProperties = connectProperties;
    }

    public Boolean isDefaultAutoCommit() {
        return defaultAutoCommit;
    }

    public void setDefaultAutoCommit(Boolean defaultAutoCommit) {
        this.defaultAutoCommit = defaultAutoCommit;
    }

    public Long getTimeBetweenConnectErrorMillis() {
        return timeBetweenConnectErrorMillis;
    }

    public void setTimeBetweenConnectErrorMillis(Long timeBetweenConnectErrorMillis) {
        this.timeBetweenConnectErrorMillis = timeBetweenConnectErrorMillis;
    }

    public String getValidationQuery() {
        return validationQuery;
    }

    public void setValidationQuery(String validationQuery) {
        this.validationQuery = validationQuery;
    }

    public Boolean isTestWhileIdle() {
        return testWhileIdle;
    }

    public void setTestWhileIdle(Boolean testWhileIdle) {
        this.testWhileIdle = testWhileIdle;
    }

    public Boolean isTestOnBorrow() {
        return testOnBorrow;
    }

    public void setTestOnBorrow(Boolean testOnBorrow) {
        this.testOnBorrow = testOnBorrow;
    }

    public Boolean isTestOnReturn() {
        return testOnReturn;
    }

    public void setTestOnReturn(Boolean testOnReturn) {
        this.testOnReturn = testOnReturn;
    }

    public Boolean isPoolPreparedStatements() {
        return poolPreparedStatements;
    }

    public void setPoolPreparedStatements(Boolean poolPreparedStatements) {
        this.poolPreparedStatements = poolPreparedStatements;
    }

    public Boolean isClearFiltersEnable() {
        return clearFiltersEnable;
    }

    public void setClearFiltersEnable(Boolean clearFiltersEnable) {
        this.clearFiltersEnable = clearFiltersEnable;
    }

    public Boolean isDefaultReadOnly() {
        return defaultReadOnly;
    }

    public void setDefaultReadOnly(Boolean defaultReadOnly) {
        this.defaultReadOnly = defaultReadOnly;
    }

    public Boolean isAsyncCloseConnectionEnable() {
        return asyncCloseConnectionEnable;
    }

    public void setAsyncCloseConnectionEnable(Boolean asyncCloseConnectionEnable) {
        this.asyncCloseConnectionEnable = asyncCloseConnectionEnable;
    }

    public Integer getConnectionErrorRetryAttempts() {
        return connectionErrorRetryAttempts;
    }

    public void setConnectionErrorRetryAttempts(Integer connectionErrorRetryAttempts) {
        this.connectionErrorRetryAttempts = connectionErrorRetryAttempts;
    }

    public Boolean isBreakAfterAcquireFailure() {
        return breakAfterAcquireFailure;
    }

    public void setBreakAfterAcquireFailure(Boolean breakAfterAcquireFailure) {
        this.breakAfterAcquireFailure = breakAfterAcquireFailure;
    }

    public Boolean isDupCloseLogEnable() {
        return dupCloseLogEnable;
    }

    public void setDupCloseLogEnable(Boolean dupCloseLogEnable) {
        this.dupCloseLogEnable = dupCloseLogEnable;
    }

    public Boolean isEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public Boolean isLogAbandoned() {
        return logAbandoned;
    }

    public void setLogAbandoned(Boolean logAbandoned) {
        this.logAbandoned = logAbandoned;
    }

    public Boolean isLogDifferentThread() {
        return logDifferentThread;
    }

    public void setLogDifferentThread(Boolean logDifferentThread) {
        this.logDifferentThread = logDifferentThread;
    }

    public Integer getLoginTimeout() {
        return loginTimeout;
    }

    public void setLoginTimeout(Integer loginTimeout) {
        this.loginTimeout = loginTimeout;
    }

    public Boolean isAccessToUnderlyingConnectionAllowed() {
        return accessToUnderlyingConnectionAllowed;
    }

    public void setAccessToUnderlyingConnectionAllowed(Boolean accessToUnderlyingConnectionAllowed) {
        this.accessToUnderlyingConnectionAllowed = accessToUnderlyingConnectionAllowed;
    }

    public Integer getMaxPoolPreparedStatementPerConnectionSize() {
        return maxPoolPreparedStatementPerConnectionSize;
    }

    public void setMaxPoolPreparedStatementPerConnectionSize(Integer maxPoolPreparedStatementPerConnectionSize) {
        this.maxPoolPreparedStatementPerConnectionSize = maxPoolPreparedStatementPerConnectionSize;
    }

    public Integer getQueryTimeout() {
        return queryTimeout;
    }

    public void setQueryTimeout(Integer queryTimeout) {
        this.queryTimeout = queryTimeout;
    }

    public Boolean isFailFast() {
        return failFast;
    }

    public void setFailFast(Boolean failFast) {
        this.failFast = failFast;
    }

    public Integer getMaxCreateTaskCount() {
        return maxCreateTaskCount;
    }

    public void setMaxCreateTaskCount(Integer maxCreateTaskCount) {
        this.maxCreateTaskCount = maxCreateTaskCount;
    }

    public Boolean isRemoveAbandoned() {
        return removeAbandoned;
    }

    public void setRemoveAbandoned(Boolean removeAbandoned) {
        this.removeAbandoned = removeAbandoned;
    }

    public Long getRemoveAbandonedTimeoutMillis() {
        return removeAbandonedTimeoutMillis;
    }

    public void setRemoveAbandonedTimeoutMillis(Long removeAbandonedTimeoutMillis) {
        this.removeAbandonedTimeoutMillis = removeAbandonedTimeoutMillis;
    }

    public Integer getDefaultTransactionIsolation() {
        return defaultTransactionIsolation;
    }

    public void setDefaultTransactionIsolation(Integer defaultTransactionIsolation) {
        this.defaultTransactionIsolation = defaultTransactionIsolation;
    }

    public Long getTimeBetweenEvictionRunsMillis() {
        return timeBetweenEvictionRunsMillis;
    }

    public void setTimeBetweenEvictionRunsMillis(Long timeBetweenEvictionRunsMillis) {
        this.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
    }

    public Long getMinEvictableIdleTimeMillis() {
        return minEvictableIdleTimeMillis;
    }

    public void setMinEvictableIdleTimeMillis(Long minEvictableIdleTimeMillis) {
        this.minEvictableIdleTimeMillis = minEvictableIdleTimeMillis;
    }

    public Long getMaxEvictableIdleTimeMillis() {
        return maxEvictableIdleTimeMillis;
    }

    public void setMaxEvictableIdleTimeMillis(Long maxEvictableIdleTimeMillis) {
        this.maxEvictableIdleTimeMillis = maxEvictableIdleTimeMillis;
    }

    public Integer getMaxOpenPreparedStatements() {
        return maxOpenPreparedStatements;
    }

    public void setMaxOpenPreparedStatements(Integer maxOpenPreparedStatements) {
        this.maxOpenPreparedStatements = maxOpenPreparedStatements;
    }

    public Integer getNotFullTimeoutRetryCount() {
        return notFullTimeoutRetryCount;
    }

    public void setNotFullTimeoutRetryCount(Integer notFullTimeoutRetryCount) {
        this.notFullTimeoutRetryCount = notFullTimeoutRetryCount;
    }

    public Long getTimeBetweenLogStatsMillis() {
        return timeBetweenLogStatsMillis;
    }

    public void setTimeBetweenLogStatsMillis(Long timeBetweenLogStatsMillis) {
        this.timeBetweenLogStatsMillis = timeBetweenLogStatsMillis;
    }

    public Integer getValidationQueryTimeout() {
        return validationQueryTimeout;
    }

    public void setValidationQueryTimeout(Integer validationQueryTimeout) {
        this.validationQueryTimeout = validationQueryTimeout;
    }
}
