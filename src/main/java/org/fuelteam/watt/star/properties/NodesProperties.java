package org.fuelteam.watt.star.properties;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.fuelteam.watt.star.core.Order;

import tk.mybatis.mapper.code.Style;

public class NodesProperties {

    private String basePackage;

    private String mapperPackage;

    private String typeAliasesPackage;

    private Order order = Order.BEFORE;

    private Style style = null;

    private Properties properties;

    private boolean primary = false;

    private Map<String, DataSourceProperties> dataSources;

    private ShardingProperties sharding;

    /** sql.show: 开启SQL显示(false)，executor.size: 工作线程数量(CPU核数) */
    private Properties props = new Properties();

    private Map<String, Object> configMap = new ConcurrentHashMap<>();

    public ShardingProperties getSharding() {
        return sharding;
    }

    public void setSharding(ShardingProperties sharding) {
        this.sharding = sharding;
    }

    public Map<String, Object> getConfigMap() {
        return configMap;
    }

    public void setConfigMap(Map<String, Object> configMap) {
        this.configMap = configMap;
    }

    public Properties getProps() {
        return props;
    }

    public void setProps(Properties props) {
        this.props = props;
    }

    public Map<String, DataSourceProperties> getDataSources() {
        return dataSources;
    }

    public void setDataSources(Map<String, DataSourceProperties> dataSources) {
        this.dataSources = dataSources;
    }

    public Style getStyle() {
        return style;
    }

    public void setStyle(Style style) {
        this.style = style;
    }

    public boolean isPrimary() {
        return primary;
    }

    public void setPrimary(boolean primary) {
        this.primary = primary;
    }

    public String getBasePackage() {
        return basePackage;
    }

    public void setBasePackage(String basePackage) {
        this.basePackage = basePackage;
    }

    public String getMapperPackage() {
        return mapperPackage;
    }

    public void setMapperPackage(String mapperPackage) {
        this.mapperPackage = mapperPackage;
    }

    public String getTypeAliasesPackage() {
        return typeAliasesPackage;
    }

    public void setTypeAliasesPackage(String typeAliasesPackage) {
        this.typeAliasesPackage = typeAliasesPackage;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }
}