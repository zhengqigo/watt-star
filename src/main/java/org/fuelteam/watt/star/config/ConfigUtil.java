package org.fuelteam.watt.star.config;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySources;
import org.springframework.util.Assert;
import org.springframework.util.PropertyPlaceholderHelper;
import org.springframework.util.SystemPropertyUtils;

public class ConfigUtil {

    private static final Logger logger = LoggerFactory.getLogger(ConfigUtil.class);

    private static PropertySources getPropertySources(Environment environment) {
        Assert.notNull(environment, "environment cannot be null");
        Assert.isInstanceOf(ConfigurableEnvironment.class, environment, "environment should be ConfigurableEnvironment");
        return ((ConfigurableEnvironment) environment).getPropertySources();
    }

    @SuppressWarnings("unchecked")
    public static <T> T getDruidConfig(Environment environment, String prefix, Class<T> clazz) {
        String strBinder = "org.springframework.boot.context.properties.bind.Binder";
        String strPlaceholdersResolver = "org.springframework.boot.context.properties.bind.PlaceholdersResolver";
        String strPropertySourcesPlaceholdersResolver = "org.springframework.boot.context.properties.bind.PropertySourcesPlaceholdersResolver";
        String strConfigurationPropertySources = "org.springframework.boot.context.properties.source.ConfigurationPropertySources";

        Class<?> binderClass = null, placeholdersResolverClass = null;
        Class<?> propertySourcesPlaceholdersResolverClass = null, configurationPropertySourcesClass = null;
        try {
            binderClass = Class.forName(strBinder);
            placeholdersResolverClass = Class.forName(strPlaceholdersResolver);
            propertySourcesPlaceholdersResolverClass = Class.forName(strPropertySourcesPlaceholdersResolver);
            configurationPropertySourcesClass = Class.forName(strConfigurationPropertySources);
        } catch (ClassNotFoundException cnfe) {
            logger.error(cnfe.getMessage(), cnfe);
            return get(environment, prefix, clazz);
        }

        PropertyPlaceholderHelper propertyPlaceholderHelper = new PropertyPlaceholderHelper(
                SystemPropertyUtils.PLACEHOLDER_PREFIX, SystemPropertyUtils.PLACEHOLDER_SUFFIX,
                SystemPropertyUtils.VALUE_SEPARATOR, true);
        PropertySources propertySources = getPropertySources(environment);

        Constructor<?> propertyPlaceholderHelperConstructor = null;
        try {
            propertyPlaceholderHelperConstructor = propertySourcesPlaceholdersResolverClass.getConstructor(Iterable.class,
                    propertyPlaceholderHelper.getClass());
        } catch (NoSuchMethodException | SecurityException ex) {
            logger.error(ex.getMessage(), ex);
        }
        if (propertyPlaceholderHelperConstructor == null) return null;

        Object placeholdersResolver = null;
        try {
            placeholdersResolver = propertyPlaceholderHelperConstructor.newInstance(propertySources, propertyPlaceholderHelper);
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            logger.error(ex.getMessage(), ex);
        }
        if (placeholdersResolver == null) return null;

        Constructor<?> placeholdersResolverConstructor = null;
        try {
            placeholdersResolverConstructor = binderClass.getConstructor(Iterable.class, placeholdersResolverClass);
        } catch (NoSuchMethodException | SecurityException ex) {
            logger.error(ex.getMessage(), ex);
        }
        if (placeholdersResolverConstructor == null) return null;

        Object object = null;
        try {
            Method method = configurationPropertySourcesClass.getMethod("get", Environment.class);
            object = method.invoke(configurationPropertySourcesClass, environment);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
                | SecurityException ex) {
            logger.error(ex.getMessage(), ex);
        }
        if (object == null) return null;

        Object bindObject = null;
        try {
            bindObject = placeholdersResolverConstructor.newInstance(object, placeholdersResolver);
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            logger.error(ex.getMessage(), ex);
        }
        if (bindObject == null) return null;

        Object bindResult = null;
        try {
            Method method = bindObject.getClass().getMethod("bind", String.class, Class.class);
            bindResult = method.invoke(bindObject, prefix, clazz);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
                | SecurityException ex) {
            logger.error(ex.getMessage(), ex);
        }
        if (bindResult == null) return null;

        try {
            return (T) bindResult.getClass().getMethod("get").invoke(bindResult);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
                | SecurityException ex) {
            logger.error(ex.getMessage(), ex);
        }
        return null;
    }

    private static <T> T get(Environment environment, String prefix, Class<T> clazz) {
        String strPropertySourcesBinder = "org.springframework.boot.bind.PropertySourcesBinder";
        Class<?> propertySourcesBinderClass = null;
        try {
            propertySourcesBinderClass = Class.forName(strPropertySourcesBinder);
        } catch (ClassNotFoundException cnfe) {
            logger.error(cnfe.getMessage(), cnfe);
        }
        if (propertySourcesBinderClass == null) return null;

        Constructor<?> constructor = null;
        try {
            constructor = propertySourcesBinderClass.getConstructor(ConfigurableEnvironment.class);
        } catch (NoSuchMethodException | SecurityException ex) {
            logger.error(ex.getMessage(), ex);
        }
        if (constructor == null) return null;

        Object propertySourcesBinderObject = null;
        try {
            propertySourcesBinderObject = constructor.newInstance(environment);
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            logger.error(ex.getMessage(), ex);
        }
        if (propertySourcesBinderObject == null) return null;

        T t = null;
        try {
            t = clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException ex) {
            logger.error(ex.getMessage(), ex);
        }
        if (t == null) return null;

        Method method = null;
        try {
            method = propertySourcesBinderClass.getMethod("bindTo", String.class, Object.class);
        } catch (NoSuchMethodException | SecurityException ex) {
            logger.error(ex.getMessage(), ex);
        }
        if (method == null) return null;

        try {
            method.invoke(propertySourcesBinderObject, prefix, t);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            logger.error(ex.getMessage(), ex);
            return null;
        }
        return t;
    }
}