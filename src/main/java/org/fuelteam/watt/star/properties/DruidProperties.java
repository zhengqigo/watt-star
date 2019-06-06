package org.fuelteam.watt.star.properties;

import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.ScheduledExecutorService;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

import com.alibaba.druid.pool.DruidDataSource;

@ConfigurationProperties(DruidProperties.druidDefault)
public class DruidProperties {

    public final static String druidDefault = "spring.druid.default";

    private String name;

    // 应用连接池名称
    private String refName;

    // 数据库名称
    private String dbName;
    
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

    public DruidProperties name(String name) {
        this.name = name;
        return this;
    }

    public DruidDataSource createDataSource(ScheduledExecutorService executorService) throws SQLException {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setName(name);
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setDriverClassName(driverClassName);
        if (connectProperties != null) dataSource.setConnectProperties(connectProperties);
        if (initialSize != null) dataSource.setInitialSize(initialSize);
        if (minIdle != null) dataSource.setMinIdle(minIdle);
        if (maxActive != null) dataSource.setMaxActive(maxActive);
        if (maxWait != null) dataSource.setMaxWait(maxWait);
        if (filters != null) dataSource.setFilters(filters);
        if (defaultAutoCommit != null) dataSource.setDefaultAutoCommit(defaultAutoCommit);
        if (timeBetweenConnectErrorMillis != null) dataSource.setTimeBetweenConnectErrorMillis(timeBetweenConnectErrorMillis);
        if (StringUtils.hasText(validationQuery)) dataSource.setValidationQuery(validationQuery);
        if (validationQueryTimeout != null) dataSource.setValidationQueryTimeout(validationQueryTimeout);
        if (testWhileIdle != null) dataSource.setTestWhileIdle(testWhileIdle);
        if (testOnBorrow != null) dataSource.setTestOnBorrow(testOnBorrow);
        if (testOnReturn != null) dataSource.setTestOnReturn(testOnReturn);
        if (poolPreparedStatements != null) dataSource.setPoolPreparedStatements(poolPreparedStatements);
        if (clearFiltersEnable != null) dataSource.setClearFiltersEnable(clearFiltersEnable);
        if (defaultReadOnly != null) dataSource.setDefaultReadOnly(defaultReadOnly);
        if (asyncCloseConnectionEnable != null) dataSource.setAsyncCloseConnectionEnable(asyncCloseConnectionEnable);
        if (connectionErrorRetryAttempts != null) dataSource.setConnectionErrorRetryAttempts(connectionErrorRetryAttempts);
        if (breakAfterAcquireFailure != null) dataSource.setBreakAfterAcquireFailure(breakAfterAcquireFailure);
        if (dupCloseLogEnable != null) dataSource.setDupCloseLogEnable(dupCloseLogEnable);
        if (enable != null) dataSource.setEnable(enable);
        if (logAbandoned != null) dataSource.setLogAbandoned(logAbandoned);
        if (logDifferentThread != null) dataSource.setLogDifferentThread(logDifferentThread);
        if (loginTimeout != null) dataSource.setLoginTimeout(loginTimeout);
        if (accessToUnderlyingConnectionAllowed != null) dataSource.setAccessToUnderlyingConnectionAllowed(accessToUnderlyingConnectionAllowed);
        if (maxPoolPreparedStatementPerConnectionSize != null) dataSource.setMaxPoolPreparedStatementPerConnectionSize(maxPoolPreparedStatementPerConnectionSize);
        if (queryTimeout != null) dataSource.setQueryTimeout(queryTimeout);
        if (failFast != null) dataSource.setFailFast(failFast);
        if (maxCreateTaskCount != null) dataSource.setMaxCreateTaskCount(maxCreateTaskCount);
        if (removeAbandoned != null) dataSource.setRemoveAbandoned(removeAbandoned);
        if (removeAbandonedTimeoutMillis != null) dataSource.setRemoveAbandonedTimeoutMillis(removeAbandonedTimeoutMillis);
        if (defaultTransactionIsolation != null) dataSource.setDefaultTransactionIsolation(defaultTransactionIsolation);
        if (timeBetweenEvictionRunsMillis != null) dataSource.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
        if (minEvictableIdleTimeMillis != null) dataSource.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
        if (maxEvictableIdleTimeMillis != null) dataSource.setMaxEvictableIdleTimeMillis(maxEvictableIdleTimeMillis);
        if (maxOpenPreparedStatements != null) dataSource.setMaxOpenPreparedStatements(maxOpenPreparedStatements);
        if (notFullTimeoutRetryCount != null) dataSource.setNotFullTimeoutRetryCount(notFullTimeoutRetryCount);
        if (timeBetweenLogStatsMillis != null) dataSource.setTimeBetweenLogStatsMillis(timeBetweenLogStatsMillis);
        if (executorService != null) {
            dataSource.setDestroyScheduler(executorService);
            dataSource.setCreateScheduler(executorService);
        }
        return dataSource;
    }

    public DruidProperties defaultEmpty() {
        DruidProperties druidProperties = new DruidProperties();
        druidProperties.initialSize = 5;
        druidProperties.minIdle = 10;
        druidProperties.maxActive = 500;
        druidProperties.validationQuery = "SELECT 0 ;";
        druidProperties.validationQueryTimeout = 28000;
        druidProperties.enable = true;
        druidProperties.timeBetweenEvictionRunsMillis = 60000L;
        druidProperties.minEvictableIdleTimeMillis = 300000L;
        return this.merge(druidProperties);
    }

    public DruidProperties merge(DruidProperties druidProperties) {
        if (druidProperties == null) return this;
        if (!StringUtils.hasText(this.url)) this.url = druidProperties.url;
        if (!StringUtils.hasText(this.username)) this.username = druidProperties.username;
        if (!StringUtils.hasText(this.password)) this.password = druidProperties.password;
        if (!StringUtils.hasText(this.driverClassName)) this.driverClassName = druidProperties.driverClassName;
        if (this.initialSize == null) this.initialSize = druidProperties.initialSize;
        if (this.minIdle == null) this.minIdle = druidProperties.minIdle;
        if (this.maxActive == null) this.maxActive = druidProperties.maxActive;
        if (this.maxWait == null) this.maxWait = druidProperties.maxWait;
        if (!StringUtils.hasText(this.filters)) this.filters = druidProperties.filters;
        if (druidProperties.connectProperties != null) {
            if (this.connectProperties == null) this.connectProperties = druidProperties.connectProperties;
            if (this.connectProperties != null) this.connectProperties.putAll(druidProperties.connectProperties);
        }
        if (this.defaultAutoCommit == null) this.defaultAutoCommit = druidProperties.defaultAutoCommit;
        if (this.timeBetweenConnectErrorMillis == null) this.timeBetweenConnectErrorMillis = druidProperties.timeBetweenConnectErrorMillis;
        if (!StringUtils.hasText(this.validationQuery)) this.validationQuery = druidProperties.validationQuery;
        if (this.testWhileIdle == null) this.testWhileIdle = druidProperties.testWhileIdle;
        if (this.testOnBorrow == null) this.testOnBorrow = druidProperties.testOnBorrow;
        if (this.testOnReturn == null) this.testOnReturn = druidProperties.testOnReturn;
        if (this.poolPreparedStatements == null) this.poolPreparedStatements = druidProperties.poolPreparedStatements;
        if (this.clearFiltersEnable == null) this.clearFiltersEnable = druidProperties.clearFiltersEnable;
        if (this.asyncCloseConnectionEnable == null) this.asyncCloseConnectionEnable = druidProperties.asyncCloseConnectionEnable;
        if (this.connectionErrorRetryAttempts == null) this.connectionErrorRetryAttempts = druidProperties.connectionErrorRetryAttempts;
        if (this.breakAfterAcquireFailure == null) this.breakAfterAcquireFailure = druidProperties.breakAfterAcquireFailure;
        if (this.dupCloseLogEnable == null) this.dupCloseLogEnable = druidProperties.dupCloseLogEnable;
        if (this.enable == null) this.enable = druidProperties.enable;
        if (this.logAbandoned == null) this.logAbandoned = druidProperties.logAbandoned;
        if (this.logDifferentThread == null) this.logDifferentThread = druidProperties.logDifferentThread;
        if (this.loginTimeout == null) this.loginTimeout = druidProperties.loginTimeout;
        if (this.accessToUnderlyingConnectionAllowed == null) this.accessToUnderlyingConnectionAllowed = druidProperties.accessToUnderlyingConnectionAllowed;
        if (this.maxPoolPreparedStatementPerConnectionSize == null) this.maxPoolPreparedStatementPerConnectionSize = druidProperties.maxPoolPreparedStatementPerConnectionSize;
        if (this.queryTimeout == null) this.queryTimeout = druidProperties.queryTimeout;
        if (this.maxCreateTaskCount == null) this.maxCreateTaskCount = druidProperties.maxCreateTaskCount;
        if (this.removeAbandoned == null) this.removeAbandoned = druidProperties.removeAbandoned;
        if (this.removeAbandonedTimeoutMillis == null) this.removeAbandonedTimeoutMillis = druidProperties.removeAbandonedTimeoutMillis;
        if (this.defaultTransactionIsolation == null) this.defaultTransactionIsolation = druidProperties.defaultTransactionIsolation;
        if (this.timeBetweenEvictionRunsMillis == null) this.timeBetweenEvictionRunsMillis = druidProperties.timeBetweenEvictionRunsMillis;
        if (this.minEvictableIdleTimeMillis == null) this.minEvictableIdleTimeMillis = druidProperties.minEvictableIdleTimeMillis;
        if (this.maxEvictableIdleTimeMillis == null) this.maxEvictableIdleTimeMillis = druidProperties.maxEvictableIdleTimeMillis;
        if (this.maxOpenPreparedStatements == null) this.maxOpenPreparedStatements = druidProperties.maxOpenPreparedStatements;
        if (this.notFullTimeoutRetryCount == null) this.notFullTimeoutRetryCount = druidProperties.notFullTimeoutRetryCount;
        if (this.timeBetweenLogStatsMillis == null) this.timeBetweenLogStatsMillis = druidProperties.timeBetweenLogStatsMillis;
        if (this.validationQueryTimeout == null) this.validationQueryTimeout = druidProperties.validationQueryTimeout;
        return this;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getRefName() {
        return refName;
    }

    public void setRefName(String refName) {
        this.refName = refName;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }
}