package org.fuelteam.watt.star.core;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.annotation.Scheduled;

public class DataSourceInvalidRetry implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    private Map<String, DynamicDataSource> dataSources;

    private static final int ONE_MINUTE = 60 * 1000;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Scheduled(initialDelay = ONE_MINUTE * 3, fixedDelay = ONE_MINUTE)
    public void run() {
        dataSources = (dataSources != null) ? dataSources : applicationContext.getBeansOfType(DynamicDataSource.class);
        Iterator<Entry<String, DynamicDataSource>> it = dataSources.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, DynamicDataSource> entry = (Map.Entry<String, DynamicDataSource>) it.next();
            entry.getValue().retryFailureSlavesDataSource();
        }
    }
}
