package org.fuelteam.watt.star.core;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.Configuration;
import org.fuelteam.watt.star.plugins.CustomPageInterceptor;
import org.fuelteam.watt.star.plugins.MyBatisSqlInterceptor;
import org.fuelteam.watt.star.plugins.ShardingInterceptor;
import org.fuelteam.watt.star.properties.DruidProperties;
import org.fuelteam.watt.star.properties.NodesProperties;
import org.mybatis.spring.SqlSessionFactoryBean;
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

import com.alibaba.druid.util.JdbcUtils;
import com.google.common.collect.Lists;

import tk.mybatis.spring.mapper.MapperScannerConfigurer;

public abstract class AbstractDataBase {

    protected final AbstractBeanDefinition createDataSource(NodesProperties nodesProperties,
            DruidProperties druidProperties, String dataSourceName) {
        Assert.notNull(nodesProperties,
                String.format("initialized dynamicDataSource failed as %s configured with problem", dataSourceName));
        BeanDefinitionBuilder definitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(DynamicDataSource.class);
        definitionBuilder.addConstructorArgValue(nodesProperties);
        definitionBuilder.addConstructorArgValue(druidProperties);
        definitionBuilder.addConstructorArgValue(dataSourceName);
        definitionBuilder.setInitMethodName("init");
        definitionBuilder.setDestroyMethodName("close");
        return definitionBuilder.getRawBeanDefinition();
    }

    protected AbstractBeanDefinition createDataSourceMaster(String dataSourceName) {
        return BeanDefinitionBuilder.genericBeanDefinition(DynamicDataSource.class)
                .setFactoryMethodOnBean("masterDataSource", dataSourceName).getRawBeanDefinition();
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
        Configuration Configuration = new Configuration();
        Configuration.setEnvironment(configuration.getEnvironment());
        Configuration.setSafeRowBoundsEnabled(configuration.isSafeRowBoundsEnabled());
        Configuration.setSafeResultHandlerEnabled(configuration.isSafeResultHandlerEnabled());
        Configuration.setMapUnderscoreToCamelCase(configuration.isMapUnderscoreToCamelCase());
        Configuration.setAggressiveLazyLoading(configuration.isAggressiveLazyLoading());
        Configuration.setMultipleResultSetsEnabled(configuration.isMultipleResultSetsEnabled());
        Configuration.setUseGeneratedKeys(configuration.isUseGeneratedKeys());
        Configuration.setUseColumnLabel(configuration.isUseColumnLabel());
        Configuration.setCacheEnabled(configuration.isCacheEnabled());
        Configuration.setCallSettersOnNulls(configuration.isCallSettersOnNulls());
        Configuration.setUseActualParamName(configuration.isUseActualParamName());
        Configuration.setReturnInstanceForEmptyRow(configuration.isReturnInstanceForEmptyRow());
        Configuration.setLogPrefix(configuration.getLogPrefix());
        Configuration.setLogImpl(configuration.getLogImpl());
        Configuration.setVfsImpl(configuration.getVfsImpl());
        Configuration.setLocalCacheScope(configuration.getLocalCacheScope());
        Configuration.setJdbcTypeForNull(configuration.getJdbcTypeForNull());
        Configuration.setLazyLoadTriggerMethods(configuration.getLazyLoadTriggerMethods());
        Configuration.setDefaultStatementTimeout(configuration.getDefaultStatementTimeout());
        Configuration.setDefaultFetchSize(configuration.getDefaultFetchSize());
        Configuration.setDefaultExecutorType(configuration.getDefaultExecutorType());
        Configuration.setAutoMappingBehavior(configuration.getAutoMappingBehavior());
        Configuration.setAutoMappingUnknownColumnBehavior(configuration.getAutoMappingUnknownColumnBehavior());
        Configuration.setVariables(configuration.getVariables());
        Configuration.setReflectorFactory(configuration.getReflectorFactory());
        Configuration.setObjectFactory(configuration.getObjectFactory());
        Configuration.setObjectWrapperFactory(configuration.getObjectWrapperFactory());
        Configuration.setLazyLoadingEnabled(configuration.isLazyLoadingEnabled());
        Configuration.setProxyFactory(configuration.getProxyFactory());
        Configuration.setDatabaseId(configuration.getDatabaseId());
        Configuration.setConfigurationFactory(configuration.getConfigurationFactory());
        return Configuration;
    }

    protected String getDbType(DruidProperties nodeProperties, DruidProperties defaultProperties) {
        String rawUrl = nodeProperties.getUrl();
        if (StringUtils.isEmpty(nodeProperties.getUrl())) rawUrl = defaultProperties.getUrl();
        return JdbcUtils.getDbType(rawUrl, null);
    }

    protected final AbstractBeanDefinition createSqlSessionFactoryBean(String dataSourceName, String mapperPackage,
            String execludedIds, String typeAliasesPackage, Dialect dialect, Configuration configuration) {
        configuration.setDatabaseId(dataSourceName);
        BeanDefinitionBuilder bdb = BeanDefinitionBuilder.rootBeanDefinition(SqlSessionFactoryBean.class);
        bdb.addPropertyValue("configuration", configuration);
        bdb.addPropertyValue("failFast", true);
        bdb.addPropertyValue("typeAliases", this.scanTypeAliases(typeAliasesPackage));
        bdb.addPropertyReference("dataSource", dataSourceName);

        MyBatisSqlInterceptor myBatisSqlInterceptor = new MyBatisSqlInterceptor();
        Properties properties = new Properties();
        properties.put("execludedIds", StringUtils.isEmpty(execludedIds) ? "" : execludedIds);
        myBatisSqlInterceptor.setProperties(properties);

        bdb.addPropertyValue("plugins", new Interceptor[] { new CustomPageInterceptor(dialect), myBatisSqlInterceptor,
                new ShardingInterceptor() });
        if (mapperPackage == null || StringUtils.isEmpty(mapperPackage)) return bdb.getBeanDefinition();
        try {
            mapperPackage = new StandardEnvironment().resolveRequiredPlaceholders(mapperPackage);
            String mapperPackages = ClassUtils.convertClassNameToResourcePath(mapperPackage);
            String mapperPackagePath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + mapperPackages + "/*.xml";
            Resource[] resources = new PathMatchingResourcePatternResolver().getResources(mapperPackagePath);
            bdb.addPropertyValue("mapperLocations", resources);
        } catch (Exception e) {
            throw new RuntimeException(
                    String.format("initialized sqlSessionFactory failed, mapperPackage %s", mapperPackage));
        }
        return bdb.getBeanDefinition();
    }

    private static final String DEFAULT_RESOURCE_PATTERN = "**/*.class";

    private static final String SEPERATOR = ",";

    private Class<?>[] scanTypeAliases(String typeAliasesPackage) {
        if (typeAliasesPackage == null || typeAliasesPackage.trim().isEmpty()) return new Class<?>[] {};
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(resolver);

        String[] aliases = typeAliasesPackage.split(SEPERATOR);
        List<Class<?>> result = Lists.newArrayList();
        for (String alias : aliases) {
            if (alias == null || (alias = alias.trim()).isEmpty()) continue;
            String aliasesPackages = ClassUtils.convertClassNameToResourcePath(alias);
            alias = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + aliasesPackages + "/" + DEFAULT_RESOURCE_PATTERN;
            try {
                Resource[] resources = resolver.getResources(alias);
                if (resources == null || resources.length <= 0) continue;
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
            Mappers mappers, Order order, Properties properties) {
        BeanDefinitionBuilder bdb = BeanDefinitionBuilder.genericBeanDefinition(MapperScannerConfigurer.class);
        if (properties == null) properties = new Properties();
        if (order != null) properties.setProperty("ORDER", order.getOrder());
        if (mappers != null) properties.setProperty("mappers", mappers.getMappers());
        bdb.addPropertyValue("properties", properties);
        bdb.addPropertyValue("sqlSessionFactoryBeanName", sqlSessionFactoryName);
        bdb.addPropertyValue("basePackage", basepackage);
        return bdb.getRawBeanDefinition();
    }
}
