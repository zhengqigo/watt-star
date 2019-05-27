package org.fuelteam.watt.star.config;

import java.util.Map;

import org.fuelteam.watt.star.properties.DruidStatProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import com.google.common.collect.Maps;

@Configuration
@ConditionalOnWebApplication
@EnableConfigurationProperties(DruidStatProperties.class)
@ConditionalOnProperty(name = "spring.druid.stat.enable", havingValue = "true")
public class DruidStatAutoConfiguration {

    public Map<String, String> druidStatParameters(DruidStatProperties druidStatProperties) {
        Map<String, String> druidStatParameters = Maps.newHashMap();
        druidStatParameters.put("allow", druidStatProperties.getAllow());
        druidStatParameters.put("deny", druidStatProperties.getDeny());
        druidStatParameters.put("resetEnable", "" + druidStatProperties.getResetEnable());
        druidStatParameters.put("exclusions", druidStatProperties.getExclusions());
        druidStatParameters.put("loginUsername", druidStatProperties.getLoginUsername());
        druidStatParameters.put("loginPassword", druidStatProperties.getLoginPassword());
        return druidStatParameters;
    }

    @Bean
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public ServletRegistrationBean servletRegistration(DruidStatProperties druidStatProperties) {
        ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(new StatViewServlet());
        servletRegistrationBean.setAsyncSupported(true);
        servletRegistrationBean.setEnabled(true);
        servletRegistrationBean.addUrlMappings("/druid/*");
        servletRegistrationBean.setInitParameters(druidStatParameters(druidStatProperties));
        return servletRegistrationBean;
    }

    @Bean
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public FilterRegistrationBean filterRegistration(DruidStatProperties druidStatProperties) {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean(new WebStatFilter());
        filterRegistrationBean.setAsyncSupported(true);
        filterRegistrationBean.setEnabled(true);
        filterRegistrationBean.addUrlPatterns(druidStatProperties.getFilterUrlPatterns());
        filterRegistrationBean.setInitParameters(druidStatParameters(druidStatProperties));
        return filterRegistrationBean;
    }
}