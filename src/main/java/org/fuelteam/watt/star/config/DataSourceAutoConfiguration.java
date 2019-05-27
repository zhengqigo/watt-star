package org.fuelteam.watt.star.config;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.ibatis.session.Configuration;
import org.fuelteam.watt.star.annotation.MasterSlaveAspect;
import org.fuelteam.watt.star.core.AbstractDataBase;
import org.fuelteam.watt.star.core.DataSourceInvalidRetry;
import org.fuelteam.watt.star.core.Dialect;
import org.fuelteam.watt.star.core.Mappers;
import org.fuelteam.watt.star.core.Order;
import org.fuelteam.watt.star.properties.DruidProperties;
import org.fuelteam.watt.star.properties.MybatisProperties;
import org.fuelteam.watt.star.properties.NodesProperties;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.util.StringUtils;

@EnableTransactionManagement(proxyTargetClass = true, order = Ordered.HIGHEST_PRECEDENCE)
public class DataSourceAutoConfiguration extends AbstractDataBase
        implements BeanDefinitionRegistryPostProcessor, EnvironmentAware {

    private ConfigurableEnvironment environment;

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = (ConfigurableEnvironment) environment;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {}

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {

        MybatisProperties mybatisProperties = this.getConfig(MybatisProperties.Prefix, MybatisProperties.class);

        DruidProperties druidProperties = this.getConfig(DruidProperties.Prefix, DruidProperties.class);

        Map<String, NodesProperties> mapOfNodesProperties = mybatisProperties.getNodes();
        String message = "At least one should be configured on %s.nodes";
        if (mapOfNodesProperties == null || mapOfNodesProperties.isEmpty()) {
            throw new RuntimeException(String.format(message, MybatisProperties.Prefix));
        }
        Iterator<Entry<String, NodesProperties>> it = this.setPrimary(mapOfNodesProperties).entrySet().iterator();
        Configuration configuration = mybatisProperties.getConfiguration();
        while (it.hasNext()) {
            Map.Entry<String, NodesProperties> entry = (Map.Entry<String, NodesProperties>) it.next();
            String nodesName = entry.getKey();
            NodesProperties nodesProperties = entry.getValue();
            try {
                Configuration config = (configuration == null) ? new Configuration()
                        : super.cloneConfiguration(configuration);
                this.registryBean(nodesName, nodesProperties, nodesProperties.isPrimary(), druidProperties, config,
                        registry);
            } catch (Throwable ex) {
                throw new RuntimeException(ex);
            }
        }
    }
    
    /**
    private <T> T getConfig(String prefix, Class<T> clazz) {
        PropertiesConfigurationFactory<T> factory = new PropertiesConfigurationFactory<T>(clazz);
        factory.setPropertySources(environment.getPropertySources());
        factory.setConversionService(environment.getConversionService());
        factory.setIgnoreInvalidFields(false);
        factory.setIgnoreUnknownFields(true);
        factory.setIgnoreNestedProperties(false);
        factory.setTargetName(prefix);
        try {
            factory.bindPropertiesToTarget();
            return factory.getObject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    */

    private Map<String, NodesProperties> setPrimary(Map<String, NodesProperties> mapOfNodesProperties) {
        int primarys = 0;
        NodesProperties nodesProperties = null;
        for (Entry<String, NodesProperties> entry : mapOfNodesProperties.entrySet()) {
            NodesProperties nodes = entry.getValue();
            if (nodes != null && nodes.isPrimary()) {
                primarys++;
                if (primarys > 1) nodes.setPrimary(false);
            }
            if (nodes != null && nodesProperties == null) nodesProperties = nodes;
        }
        if (primarys == 0 && nodesProperties != null) nodesProperties.setPrimary(true);
        return mapOfNodesProperties;
    }

    private <T> T getConfig(String prefix, Class<T> clazz) {
        T existingValue = Binder.get(environment).bind(prefix, clazz).get();
        return existingValue;
    }

    private void registryBean(String nodeName, NodesProperties nodeProperties, boolean primary,
            DruidProperties druidProperties, Configuration configuration, BeanDefinitionRegistry registry) {
        if (nodeProperties == null) return;
        String mapperPackage = nodeProperties.getMapperPackage();
        String typeAliasesPackage = nodeProperties.getTypeAliasesPackage();
        String dbType = super.getDbType(nodeProperties.getMaster(), druidProperties);
        Order order = nodeProperties.getOrder();
        Dialect dialect = nodeProperties.getDialect();
        if (dialect == null) dialect = Dialect.of(dbType);
        Mappers mappers = Mappers.of(dialect);
        String basePackage = nodeProperties.getBasePackage();
        if (StringUtils.isEmpty(basePackage)) {
            basePackage = "";
        }
        String dataSourceName = nodeName + "DataSource";
        String jdbcTemplateName = nodeName + "JdbcTemplate";
        String transactionManagerName = nodeName;
        String sqlSessionFactoryBeanName = nodeName + "SqlSessionFactoryBean";
        String scannerConfigurerName = nodeName + "ScannerConfigurer";

        AbstractBeanDefinition dataSource = super.createDataSource(nodeProperties, druidProperties, dataSourceName);
        AbstractBeanDefinition jdbcTemplate = super.createJdbcTemplate(dataSourceName);
        AbstractBeanDefinition transactionManager = super.createTransactionManager(dataSourceName);

        AbstractBeanDefinition sqlSessionFactoryBean = super.createSqlSessionFactoryBean(dataSourceName, mapperPackage,
                nodeProperties.getExecludedIds(), typeAliasesPackage, dialect, configuration);
        AbstractBeanDefinition scannerConfigurer = super.createScannerConfigurerBean(sqlSessionFactoryBeanName,
                basePackage, mappers, order, nodeProperties.getProperties());

        dataSource.setLazyInit(true);
        dataSource.setPrimary(primary);
        dataSource.setScope(BeanDefinition.SCOPE_SINGLETON);
        jdbcTemplate.setLazyInit(true);
        jdbcTemplate.setPrimary(primary);
        jdbcTemplate.setScope(BeanDefinition.SCOPE_SINGLETON);
        transactionManager.setLazyInit(true);
        transactionManager.setPrimary(primary);
        transactionManager.setScope(BeanDefinition.SCOPE_SINGLETON);
        sqlSessionFactoryBean.setLazyInit(true);
        sqlSessionFactoryBean.setPrimary(primary);
        sqlSessionFactoryBean.setScope(BeanDefinition.SCOPE_SINGLETON);
        scannerConfigurer.setLazyInit(true);
        scannerConfigurer.setPrimary(primary);
        scannerConfigurer.setScope(BeanDefinition.SCOPE_SINGLETON);

        registry.registerBeanDefinition(dataSourceName, dataSource);
        registry.registerBeanDefinition(jdbcTemplateName, jdbcTemplate);
        registry.registerBeanDefinition(transactionManagerName, transactionManager);
        registry.registerBeanDefinition(sqlSessionFactoryBeanName, sqlSessionFactoryBean);
        registry.registerBeanDefinition(scannerConfigurerName, scannerConfigurer);

        if (primary) {
            registry.registerAlias(dataSourceName, "dataSource");
            registry.registerAlias(jdbcTemplateName, "jdbcTemplate");
            registry.registerAlias(transactionManagerName, "transactionManager");
        }
    }

    @Bean
    public MasterSlaveAspect masterSlaveAspect() {
        return new MasterSlaveAspect();
    }

    @Bean
    public DataSourceInvalidRetry dataSourceInvalidRetry() {
        return new DataSourceInvalidRetry();
    }
}