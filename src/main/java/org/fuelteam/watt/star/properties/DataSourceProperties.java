package org.fuelteam.watt.star.properties;

import java.util.List;

import com.google.common.collect.Lists;

import io.shardingjdbc.core.api.algorithm.masterslave.MasterSlaveLoadBalanceAlgorithmType;

public class DataSourceProperties {

    // 主库
    private DruidProperties master;

    // 多个丛库，空表示没有主从配置
    private List<DruidProperties> slaves = Lists.newLinkedList();

    // 主从负载均衡算法类型
    private MasterSlaveLoadBalanceAlgorithmType loadBalanceAlgorithmType;

    // 主从数据库负载均衡算法类名
    private String loadBalanceAlgorithmClassName;

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

    public MasterSlaveLoadBalanceAlgorithmType getLoadBalanceAlgorithmType() {
        return loadBalanceAlgorithmType;
    }

    public void setLoadBalanceAlgorithmType(MasterSlaveLoadBalanceAlgorithmType loadBalanceAlgorithmType) {
        this.loadBalanceAlgorithmType = loadBalanceAlgorithmType;
    }

    public String getLoadBalanceAlgorithmClassName() {
        return loadBalanceAlgorithmClassName;
    }

    public void setLoadBalanceAlgorithmClassName(String loadBalanceAlgorithmClassName) {
        this.loadBalanceAlgorithmClassName = loadBalanceAlgorithmClassName;
    }
}