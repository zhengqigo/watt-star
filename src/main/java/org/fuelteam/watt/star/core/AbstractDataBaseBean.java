package org.fuelteam.watt.star.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.Configuration;
import org.fuelteam.watt.star.properties.DruidProperties;
import org.fuelteam.watt.star.properties.NodesProperties;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import io.shardingjdbc.core.constant.DatabaseType;
import tk.mybatis.mapper.code.Style;
import tk.mybatis.spring.mapper.MapperScannerConfigurer;

public abstract class AbstractDataBaseBean implements DisposableBean {

    private static final Logger logger = LoggerFactory.getLogger(AbstractDataBaseBean.class);

    protected final AbstractBeanDefinition createDataSource(NodesProperties nodesProperties, DruidProperties defaultProperties,
            String dataSourceName) {
        Assert.notNull(nodesProperties, String.format("createDataSource %s failed as wrong properties", dataSourceName));
        BeanDefinitionBuilder definitionBuilder = BeanDefinitionBuilder.rootBeanDefinition(DataSourceBulider.class, "bulid");
        definitionBuilder.addConstructorArgValue(nodesProperties);
        definitionBuilder.addConstructorArgValue(defaultProperties);
        return definitionBuilder.getRawBeanDefinition();
    }

    protected final AbstractBeanDefinition createTransactionManager(String dataSourceName) {
        BeanDefinitionBuilder bdb = BeanDefinitionBuilder.genericBeanDefinition(DataSourceTransactionManager.class);
        bdb.addConstructorArgReference(dataSourceName);
        return bdb.getRawBeanDefinition();
    }

    protected final AbstractBeanDefinition createJdbcTemplate(String dataSourceName) {
        BeanDefinitionBuilder bdb = BeanDefinitionBuilder.genericBeanDefinition(JdbcTemplate.class);
        bdb.addConstructorArgReference(dataSourceName);
        return bdb.getRawBeanDefinition();
    }

    protected Configuration cloneConfiguration(Configuration configuration) {
        Configuration result = new Configuration();
        if (configuration != null) {
            result.setEnvironment(configuration.getEnvironment());
            result.setSafeRowBoundsEnabled(configuration.isSafeRowBoundsEnabled());
            result.setSafeResultHandlerEnabled(configuration.isSafeResultHandlerEnabled());
            result.setMapUnderscoreToCamelCase(configuration.isMapUnderscoreToCamelCase());
            result.setAggressiveLazyLoading(configuration.isAggressiveLazyLoading());
            result.setMultipleResultSetsEnabled(configuration.isMultipleResultSetsEnabled());
            result.setUseGeneratedKeys(configuration.isUseGeneratedKeys());
            result.setUseColumnLabel(configuration.isUseColumnLabel());
            result.setCacheEnabled(configuration.isCacheEnabled());
            result.setCallSettersOnNulls(configuration.isCallSettersOnNulls());
            result.setUseActualParamName(configuration.isUseActualParamName());
            result.setReturnInstanceForEmptyRow(configuration.isReturnInstanceForEmptyRow());
            result.setLogPrefix(configuration.getLogPrefix());
            result.setLogImpl(configuration.getLogImpl());
            result.setVfsImpl(configuration.getVfsImpl());
            result.setLocalCacheScope(configuration.getLocalCacheScope());
            result.setJdbcTypeForNull(configuration.getJdbcTypeForNull());
            result.setLazyLoadTriggerMethods(configuration.getLazyLoadTriggerMethods());
            result.setDefaultStatementTimeout(configuration.getDefaultStatementTimeout());
            result.setDefaultFetchSize(configuration.getDefaultFetchSize());
            result.setDefaultExecutorType(configuration.getDefaultExecutorType());
            result.setAutoMappingBehavior(configuration.getAutoMappingBehavior());
            result.setAutoMappingUnknownColumnBehavior(configuration.getAutoMappingUnknownColumnBehavior());
            result.setVariables(configuration.getVariables());
            result.setReflectorFactory(configuration.getReflectorFactory());
            result.setObjectFactory(configuration.getObjectFactory());
            result.setObjectWrapperFactory(configuration.getObjectWrapperFactory());
            result.setLazyLoadingEnabled(configuration.isLazyLoadingEnabled());
            result.setProxyFactory(configuration.getProxyFactory());
            result.setDatabaseId(configuration.getDatabaseId());
            result.setConfigurationFactory(configuration.getConfigurationFactory());
        }
        return result;
    }

    public static final DatabaseType getDbType(DruidProperties druidProperties, DruidProperties defaultProperties) {
        return DatabaseType.MySQL;
    }

    protected final AbstractBeanDefinition createSqlSessionFactoryBean(String dataSourceName, String mapperPackage,
            String typeAliasesPackage, DatabaseType databaseType, Configuration configuration) {
        configuration.setDatabaseId(dataSourceName);
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.rootBeanDefinition(SqlSessionFactoryBean.class);
        beanDefinitionBuilder.addPropertyValue("configuration", configuration);
        beanDefinitionBuilder.addPropertyValue("failFast", true);
        beanDefinitionBuilder.addPropertyValue("typeAliases", this.saenTypeAliases(typeAliasesPackage));
        beanDefinitionBuilder.addPropertyReference("dataSource", dataSourceName);
        beanDefinitionBuilder.addPropertyValue("plugins", new Interceptor[] { new CustomPageInterceptor(databaseType) });
        if (StringUtils.isEmpty(mapperPackage)) return beanDefinitionBuilder.getBeanDefinition();
        try {
            mapperPackage = new StandardEnvironment().resolveRequiredPlaceholders(mapperPackage);
            String mapperPackages = ClassUtils.convertClassNameToResourcePath(mapperPackage);
            String mapperPackagePath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + mapperPackages + "/*.xml";
            Resource[] resources = new PathMatchingResourcePatternResolver().getResources(mapperPackagePath);
            beanDefinitionBuilder.addPropertyValue("mapperLocations", resources);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            throw new RuntimeException(String.format("init sqlSessionFactory failed as mapperPackage %s", mapperPackage));
        }
        return beanDefinitionBuilder.getBeanDefinition();
    }

    private static final String DEFAULT_RESOURCE_PATTERN = "**/*.class";
    private static final String SEPERATOR = ",";

    private Class<?>[] saenTypeAliases(String typeAliasesPackage) {
        if (typeAliasesPackage == null || typeAliasesPackage.trim().isEmpty()) return new Class<?>[] {};
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(resolver);

        String[] aliasList = typeAliasesPackage.split(SEPERATOR);
        List<Class<?>> result = new ArrayList<Class<?>>();
        for (String alias : aliasList) {
            if (alias == null || (alias = alias.trim()).isEmpty()) continue;
            String aliasesPackages = ClassUtils.convertClassNameToResourcePath(alias);
            alias = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + aliasesPackages + "/" + DEFAULT_RESOURCE_PATTERN;
            try {
                Resource[] resources = resolver.getResources(alias);
                if (resources == null || (resources != null && resources.length <= 0)) continue;
                MetadataReader metadataReader = null;
                for (Resource resource : resources) {
                    if (!resource.isReadable()) continue;
                    metadataReader = metadataReaderFactory.getMetadataReader(resource);
                    try {
                        result.add(Class.forName(metadataReader.getClassMetadata().getClassName()));
                    } catch (ClassNotFoundException cnfe) {
                        cnfe.printStackTrace();
                    }
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
        return result.toArray(new Class<?>[0]);
    }

    protected final AbstractBeanDefinition createScannerConfigurerBean(String sqlSessionFactoryName, String basepackage,
            Mapper mapper, Order order, Style style, Properties properties) {
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(MapperScannerConfigurer.class);
        if (properties == null) properties = new Properties();
        if (style != null) properties.setProperty("style", style.name());
        if (order != null) properties.setProperty("ORDER", order.getOrder());
        if (mapper != null) properties.setProperty("mappers", mapper.getMapper());
        beanDefinitionBuilder.addPropertyValue("properties", properties);
        beanDefinitionBuilder.addPropertyValue("sqlSessionFactoryBeanName", sqlSessionFactoryName);
        beanDefinitionBuilder.addPropertyValue("basePackage", basepackage);
        return beanDefinitionBuilder.getRawBeanDefinition();
    }

    @Override
    public void destroy() throws Exception {
        DataSourceBulider.stop();
    }
}