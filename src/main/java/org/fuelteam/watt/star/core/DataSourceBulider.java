package org.fuelteam.watt.star.core;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import javax.sql.DataSource;

import org.fuelteam.watt.star.properties.DataSourceProperties;
import org.fuelteam.watt.star.properties.DruidProperties;
import org.fuelteam.watt.star.properties.NodesProperties;
import org.fuelteam.watt.star.properties.ShardingProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.alibaba.druid.pool.DruidDataSource;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import io.shardingjdbc.core.api.MasterSlaveDataSourceFactory;
import io.shardingjdbc.core.api.ShardingDataSourceFactory;
import io.shardingjdbc.core.api.config.MasterSlaveRuleConfiguration;
import io.shardingjdbc.core.api.config.ShardingRuleConfiguration;
import io.shardingjdbc.core.yaml.sharding.YamlShardingStrategyConfiguration;
import io.shardingjdbc.core.yaml.sharding.YamlTableRuleConfiguration;

public class DataSourceBulider {

    private static final Logger logger = LoggerFactory.getLogger(DataSourceBulider.class);
    private static final Map<String, DruidDataSource> dataSourceMap = Maps.newConcurrentMap();

    private final static Integer cores = Runtime.getRuntime().availableProcessors();
    private static ScheduledExecutorService executorService = Executors.newScheduledThreadPool(cores, new ThreadFactory() {
        @Override
        public Thread newThread(Runnable runnable) {
            Thread thread = new Thread(runnable);
            thread.setName("Druid-Scheduled-" + thread.getId());
            return thread;
        }
    });

    private static final long waitMaxTime = 30l;

    public static final void stop() throws InterruptedException {
        Iterator<DruidDataSource> it = dataSourceMap.values().iterator();
        while (it.hasNext()) {
            DruidDataSource dataSource = it.next();
            dataSource.close();
        }
        executorService.shutdownNow();
        if (executorService.awaitTermination(waitMaxTime, TimeUnit.SECONDS)) {
            if (!executorService.isShutdown()) {
                logger.error("druid scheduledThreadPool 30seconds shutdown failed");
                executorService.shutdown();
            }
        }
    }

    public static final DataSource bulid(NodesProperties nodesProperties, DruidProperties defaultProperties) throws SQLException {
        Map<String, DataSourceProperties> dataSourcePropertiesMap = nodesProperties.getDataSources();
        Assert.notEmpty(dataSourcePropertiesMap, "dataSource properties can not be empty");
        Map<String, DataSource> dataSourceMap = getDataSourceMap(dataSourcePropertiesMap, defaultProperties);
        Map<String, Object> configMap = nodesProperties.getConfigMap();
        if (nodesProperties.getSharding() == null && dataSourcePropertiesMap.size() == 1) {
            if (dataSourceMap.size() == 1) return dataSourceMap.values().iterator().next();
            Collection<MasterSlaveRuleConfiguration> masterSlaveRuleConfigs = getMasterSlaveRuleConfigs(dataSourcePropertiesMap);
            return MasterSlaveDataSourceFactory.createDataSource(dataSourceMap, masterSlaveRuleConfigs.iterator().next(), configMap);
        } else {
            ShardingRuleConfiguration shardingRuleConfiguration = getShardingRuleConfiguration(nodesProperties);
            return ShardingDataSourceFactory.createDataSource(dataSourceMap, shardingRuleConfiguration, configMap, nodesProperties.getProps());
        }
    }

    private static final ShardingRuleConfiguration getShardingRuleConfiguration(NodesProperties nodesProperties)
            throws SQLException {
        ShardingRuleConfiguration shardingRuleConfiguration = new ShardingRuleConfiguration();
        ShardingProperties shardingProperties = nodesProperties.getSharding();
        if (shardingProperties != null) {
            shardingRuleConfiguration.setDefaultDataSourceName(shardingProperties.getDefaultDataSourceName());
            for (Map.Entry<String, YamlTableRuleConfiguration> entry : shardingProperties.getTables().entrySet()) {
                YamlTableRuleConfiguration tableRuleConfig = entry.getValue();
                tableRuleConfig.setLogicTable(entry.getKey());
                shardingRuleConfiguration.getTableRuleConfigs().add(tableRuleConfig.build());
            }
            shardingRuleConfiguration.getBindingTableGroups().addAll(shardingProperties.getBindingTables());
            if (shardingProperties.getDefaultDatabaseStrategy() != null) {
                YamlShardingStrategyConfiguration yamlShardingStrategyConfiguration = shardingProperties.getDefaultDatabaseStrategy();
                shardingRuleConfiguration.setDefaultDatabaseShardingStrategyConfig(yamlShardingStrategyConfiguration.build());
            }
            if (shardingProperties.getDefaultTableStrategy() != null) {
                YamlShardingStrategyConfiguration yamlShardingStrategyConfiguration = shardingProperties.getDefaultTableStrategy();
                shardingRuleConfiguration.setDefaultTableShardingStrategyConfig(yamlShardingStrategyConfiguration.build());
            }
            shardingRuleConfiguration.setDefaultKeyGeneratorClass(shardingProperties.getDefaultKeyGeneratorClass());
        }
        Collection<MasterSlaveRuleConfiguration> masterSlaveRuleConfigs = getMasterSlaveRuleConfigs(
                nodesProperties.getDataSources());
        shardingRuleConfiguration.setMasterSlaveRuleConfigs(masterSlaveRuleConfigs);
        return shardingRuleConfiguration;
    }

    private static Map<String, DataSource> getDataSourceMap(Map<String, DataSourceProperties> dataSources,
            DruidProperties defaultProperties) throws SQLException {
        Iterator<Entry<String, DataSourceProperties>> it = dataSources.entrySet().iterator();
        Map<String, DataSource> dataSourceMap = new HashMap<String, DataSource>();
        while (it.hasNext()) {
            Map.Entry<String, DataSourceProperties> entry = (Map.Entry<String, DataSourceProperties>) it.next();
            String dataSourceName = entry.getKey();
            String masterDataSourceName = dataSourceName + "-master";
            DataSource created = createDataSource(entry.getValue().getMaster(), masterDataSourceName, defaultProperties);
            dataSourceMap.put(masterDataSourceName, created);
            List<DruidProperties> slaves = entry.getValue().getSlaves();
            if (slaves == null) continue;
            for (int i = 0; i < slaves.size(); i++) {
                String slaveDataSourceName = dataSourceName + "-slave-" + i;
                DruidProperties slaveProperties = slaves.get(i);
                dataSourceMap.put(slaveDataSourceName, createDataSource(slaveProperties, slaveDataSourceName, defaultProperties));
            }
        }
        return dataSourceMap;
    }

    private static DataSource createDataSource(DruidProperties druidProperties, String defaultName,
            DruidProperties defaultProperties) throws SQLException {
        if (!StringUtils.isEmpty(druidProperties.getRefName())) {
            Assert.hasText(druidProperties.getDbName(), "dbName should be specified");
            return new TempDataSource(dataSourceMap, druidProperties.getRefName(), druidProperties.getDbName());
        }
        if (StringUtils.isEmpty(druidProperties.getName())) druidProperties.name(defaultName);
        DruidDataSource dataSource = druidProperties.merge(defaultProperties).defaultEmpty().createDataSource(executorService);
        dataSourceMap.put(dataSource.getName(), dataSource);
        if (StringUtils.isEmpty(druidProperties.getDbName())) return dataSource;
        return new TempDataSource(dataSource, druidProperties.getDbName());
    }

    private static final Collection<MasterSlaveRuleConfiguration> getMasterSlaveRuleConfigs(
            Map<String, DataSourceProperties> dataSources) {
        Collection<MasterSlaveRuleConfiguration> collection = Lists.newLinkedList();
        for (Map.Entry<String, DataSourceProperties> entry : dataSources.entrySet()) {
            MasterSlaveRuleConfiguration ruleConfiguration = new MasterSlaveRuleConfiguration();
            String dataSourceName = entry.getKey();
            DataSourceProperties dataSourceProperties = entry.getValue();
            ruleConfiguration.setName(dataSourceName);
            String masterDataSourceName = dataSourceName + "-master";
            ruleConfiguration.setMasterDataSourceName(masterDataSourceName);
            List<DruidProperties> slaves = dataSourceProperties.getSlaves();
            Set<String> slaveDataSourceNames = Sets.newHashSet();
            if (slaves != null && !slaves.isEmpty()) {
                for (int i = 0; i < slaves.size(); i++) {
                    slaveDataSourceNames.add(dataSourceName + "-slave-" + i);
                }
            } else {
                slaveDataSourceNames.add(masterDataSourceName);
            }
            ruleConfiguration.setSlaveDataSourceNames(slaveDataSourceNames);
            ruleConfiguration.setLoadBalanceAlgorithmType(dataSourceProperties.getLoadBalanceAlgorithmType());
            ruleConfiguration.setLoadBalanceAlgorithmClassName(dataSourceProperties.getLoadBalanceAlgorithmClassName());
            collection.add(ruleConfiguration);
        }
        return collection;
    }
}