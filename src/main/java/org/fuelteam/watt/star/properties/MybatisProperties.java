package org.fuelteam.watt.star.properties;

import java.util.Map;

import org.apache.ibatis.session.Configuration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = MybatisProperties.prefix)
public class MybatisProperties {

    public final static String prefix = "spring.mybatis";

    private Map<String, NodesProperties> nodes;

    private Configuration configuration;

    public Map<String, NodesProperties> getNodes() {
        return nodes;
    }

    public void setNodes(Map<String, NodesProperties> nodes) {
        this.nodes = nodes;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }
}