package org.fuelteam.watt.star.properties;

import java.util.List;
import java.util.Properties;

import org.fuelteam.watt.star.core.Dialect;
import org.fuelteam.watt.star.core.Order;

public class NodesProperties {

    private String basePackage;

    private String mapperPackage;

    private String typeAliasesPackage;

    private Order order = Order.BEFORE;

    private Dialect dialect = null;

    private Properties properties;

    private boolean primary = false;

    private DruidProperties master;

    private List<DruidProperties> slaves;

    private String execludedIds;

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

    public Dialect getDialect() {
        return dialect;
    }

    public void setDialect(Dialect dialect) {
        this.dialect = dialect;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public boolean isPrimary() {
        return primary;
    }

    public void setPrimary(boolean primary) {
        this.primary = primary;
    }

    public DruidProperties getMaster() {
        return master;
    }

    public void setMaster(DruidProperties master) {
        this.master = master;
    }

    public List<DruidProperties> getSlaves() {
        return slaves;
    }

    public void setSlaves(List<DruidProperties> slaves) {
        this.slaves = slaves;
    }

    public String getExecludedIds() {
        return execludedIds;
    }

    public void setExecludedIds(String execludedIds) {
        this.execludedIds = execludedIds;
    }
}