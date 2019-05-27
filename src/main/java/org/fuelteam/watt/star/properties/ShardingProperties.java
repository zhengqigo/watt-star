package org.fuelteam.watt.star.properties;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import io.shardingjdbc.core.yaml.sharding.YamlShardingStrategyConfiguration;
import io.shardingjdbc.core.yaml.sharding.YamlTableRuleConfiguration;

public class ShardingProperties {

    // 默认数据源，将通过默认数据源定位未配置分片规则表
    private String defaultDataSourceName;

    // 分库分表配置，可配置多个logic_table_name
    private Map<String, YamlTableRuleConfiguration> tables = Maps.newHashMap();

    // 绑定表列表 - 逻辑表名列表，多个logic_table_name以逗号分隔
    private List<String> bindingTables = Lists.newArrayList();

    // 默认数据库分片策略
    private YamlShardingStrategyConfiguration defaultDatabaseStrategy;

    // 默认数据表分片策略，同分库策略
    private YamlShardingStrategyConfiguration defaultTableStrategy;

    // 默认的主键生成类
    private String defaultKeyGeneratorClass;

    public String getDefaultDataSourceName() {
        return defaultDataSourceName;
    }

    public void setDefaultDataSourceName(String defaultDataSourceName) {
        this.defaultDataSourceName = defaultDataSourceName;
    }

    public Map<String, YamlTableRuleConfiguration> getTables() {
        return tables;
    }

    public void setTables(Map<String, YamlTableRuleConfiguration> tables) {
        this.tables = tables;
    }

    public List<String> getBindingTables() {
        return bindingTables;
    }

    public void setBindingTables(List<String> bindingTables) {
        this.bindingTables = bindingTables;
    }

    public YamlShardingStrategyConfiguration getDefaultDatabaseStrategy() {
        return defaultDatabaseStrategy;
    }

    public void setDefaultDatabaseStrategy(YamlShardingStrategyConfiguration defaultDatabaseStrategy) {
        this.defaultDatabaseStrategy = defaultDatabaseStrategy;
    }

    public YamlShardingStrategyConfiguration getDefaultTableStrategy() {
        return defaultTableStrategy;
    }

    public void setDefaultTableStrategy(YamlShardingStrategyConfiguration defaultTableStrategy) {
        this.defaultTableStrategy = defaultTableStrategy;
    }

    public String getDefaultKeyGeneratorClass() {
        return defaultKeyGeneratorClass;
    }

    public void setDefaultKeyGeneratorClass(String defaultKeyGeneratorClass) {
        this.defaultKeyGeneratorClass = defaultKeyGeneratorClass;
    }
}