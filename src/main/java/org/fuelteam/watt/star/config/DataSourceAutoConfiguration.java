package org.fuelteam.watt.star.config;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.ibatis.session.Configuration;
import org.fuelteam.watt.star.core.AbstractDataBaseBean;
import org.fuelteam.watt.star.core.Mapper;
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
import org.springframework.context.EnvironmentAware;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import io.shardingjdbc.core.constant.DatabaseType;
import tk.mybatis.mapper.code.Style;

@EnableTransactionManagement(proxyTargetClass = true, order = Ordered.HIGHEST_PRECEDENCE)
public class DataSourceAutoConfiguration extends AbstractDataBaseBean implements BeanDefinitionRegistryPostProcessor, EnvironmentAware {

    private Environment environment;

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {}

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        MybatisProperties druidConfig = ConfigUtil.getDruidConfig(environment, MybatisProperties.prefix, MybatisProperties.class);

        DruidProperties defaultConfig = ConfigUtil.getDruidConfig(environment, DruidProperties.druidDefault,
                DruidProperties.class);

        Configuration configuration = druidConfig.getConfiguration();
        Map<String, NodesProperties> druidNodeConfigs = druidConfig.getNodes();
        if (druidNodeConfigs == null || druidNodeConfigs.isEmpty()) {
            String message = String.format("At least one should be configured on %s.nodes", MybatisProperties.prefix);
            throw new RuntimeException(message);
        }
        Iterator<Entry<String, NodesProperties>> it = this.setPrimary(druidNodeConfigs).entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, NodesProperties> entry = (Map.Entry<String, NodesProperties>) it.next();
            String druidNodeName = entry.getKey();
            NodesProperties druidNodeConfig = entry.getValue();
            try {
                Configuration _configuration = super.cloneConfiguration(configuration);
                this.registryBean(druidNodeName, druidNodeConfig, defaultConfig, _configuration, registry);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }
    }

    private Map<String, NodesProperties> setPrimary(Map<String, NodesProperties> druidNodeConfigs) {
        int primarys = 0;
        NodesProperties defDruidNode = null;
        for (Entry<String, NodesProperties> entry : druidNodeConfigs.entrySet()) {
            NodesProperties druidNode = entry.getValue();
            if (druidNode != null && druidNode.isPrimary()) {
                primarys++;
                if (primarys > 1) druidNode.setPrimary(false);
            }
            if (druidNode != null && defDruidNode == null) defDruidNode = druidNode;
        }
        if (primarys == 0 && defDruidNode != null) defDruidNode.setPrimary(true);
        return druidNodeConfigs;
    }

    private void registryBean(String druidNodeName, NodesProperties nodeProperties, DruidProperties defaultProperties,
            Configuration configuration, BeanDefinitionRegistry registry) {
        if (nodeProperties == null) return;
        Assert.notEmpty(nodeProperties.getDataSources(), "dataSources cannot be empty");
        String mapperPackage = nodeProperties.getMapperPackage();
        String typeAliasesPackage = nodeProperties.getTypeAliasesPackage();
        DatabaseType databaseType = super.getDbType(nodeProperties.getDataSources().values().iterator().next().getMaster(), defaultProperties);
        Order order = nodeProperties.getOrder();
        Style style = nodeProperties.getStyle();
        Mapper mappers = Mapper.valueOfDialect(databaseType);
        String basepackage = nodeProperties.getBasePackage();
        if (StringUtils.isEmpty(basepackage)) basepackage = "";
        boolean primary = nodeProperties.isPrimary();
        String dataSourceName = druidNodeName + "DataSource";
        String jdbcTemplateName = druidNodeName + "JdbcTemplate";
        String transactionManagerName = druidNodeName;
        String sqlSessionFactoryBeanName = druidNodeName + "SqlSessionFactoryBean";
        String scannerConfigurerName = druidNodeName + "ScannerConfigurer";

        AbstractBeanDefinition dataSource = super.createDataSource(nodeProperties, defaultProperties, dataSourceName);
        AbstractBeanDefinition jdbcTemplate = super.createJdbcTemplate(dataSourceName);
        AbstractBeanDefinition transactionManager = super.createTransactionManager(dataSourceName);
        AbstractBeanDefinition sqlSessionFactoryBean = super.createSqlSessionFactoryBean(dataSourceName, mapperPackage, typeAliasesPackage, databaseType, configuration);
        AbstractBeanDefinition scannerConfigurer = super.createScannerConfigurerBean(sqlSessionFactoryBeanName, basepackage, mappers, order, style, nodeProperties.getProperties());

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
}