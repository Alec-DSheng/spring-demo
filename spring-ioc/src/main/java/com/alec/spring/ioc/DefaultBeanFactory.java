package com.alec.spring.ioc;

import org.apache.commons.lang3.StringUtils;

import java.io.Closeable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: alec
 * Description: 定义ioc bean factory 1.0
 * @date: 09:58 2020-04-10
 */
public class DefaultBeanFactory implements BeanDefinitionRegister, BeanFactory, Closeable {

    private final Integer INIT_LENGTH = 64;

    private Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>(INIT_LENGTH);

    private Map<String, Object>  beanMap = new ConcurrentHashMap<>(INIT_LENGTH);

    @Override
    public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) {
        Objects.requireNonNull(beanName, "bean name is not null");
        Objects.requireNonNull(beanDefinition, "bean definition is not null");
        if (!beanDefinition.validate()) {
            throw new BeanDefinitionException("validate error");
        }
        if (containBeanDefinition(beanName)) {
            throw new BeanDefinitionException("bean definition exist");
        }
        beanDefinitionMap.put(beanName, beanDefinition);
    }

    @Override
    public BeanDefinition getBeanDefinition(String beanName) {
        Objects.requireNonNull(beanName, "bean name is not null");
        return beanDefinitionMap.get(beanName);
    }

    @Override
    public boolean containBeanDefinition(String beanName) {
        Objects.requireNonNull(beanName, "bean name is not null");
        return beanDefinitionMap.containsKey(beanName);
    }

    @Override
    public Object getBean(String beanName) {
        try {
            return this.doGetBean(beanName);
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected Object doGetBean(String beanName) throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Objects.requireNonNull(beanName, "bean name is not null");
        Object instance = beanMap.get(beanName);
        if (instance != null) {
            return instance;
        }
        BeanDefinition beanDefinition = getBeanDefinition(beanName);
        Class<?> beanClass = beanDefinition.getBeanClass();
        if (beanClass != null) {
            if (StringUtils.isEmpty(beanDefinition.getBeanFactoryMethodName())) {
                /**
                 * class 不为空， bean factory method 为空 通过构造器生成实例
                 * */
                instance = createBeanByConstruct(beanDefinition);
            } else {
                instance = createBeanByStaticBeanFactory(beanDefinition);
            }
        } else {
            instance = createBeanByBeanFactory(beanDefinition);
        }
        this.doInit(instance, beanDefinition);
        if (beanDefinition.isSingle()) {
            beanMap.put(beanName, instance);
        }
        return instance;
    }



    @Override
    public void close() {
        for (Map.Entry<String, BeanDefinition> entry : this.beanDefinitionMap.entrySet()) {
            String key = entry.getKey();
            BeanDefinition beanDefinition = entry.getValue();
            if (beanDefinition.isSingle() && !StringUtils.isEmpty(beanDefinition.getDestroyMethod())) {
                Object instance = this.beanMap.get(key);
                Method method ;
                try {
                    method = instance.getClass().getMethod(beanDefinition.getDestroyMethod(), null);
                    method.invoke(instance, null);
                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void doInit(Object instance, BeanDefinition beanDefinition) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        if (!StringUtils.isEmpty(beanDefinition.getInitMethod())) {
            Method method = instance.getClass().getMethod(beanDefinition.getInitMethod());
            method.invoke(instance, null);
        }
    }

    private Object createBeanByConstruct(BeanDefinition beanDefinition) throws IllegalAccessException, InstantiationException {
        return  beanDefinition.getBeanClass().newInstance();
    }

    private Object createBeanByStaticBeanFactory(BeanDefinition beanDefinition) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = beanDefinition.getBeanClass().getMethod(beanDefinition.getBeanFactoryMethodName(), null);
        return method.invoke(beanDefinition.getBeanClass(), null);
    }

    private Object createBeanByBeanFactory(BeanDefinition beanDefinition) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {
        Object beanFactory = this.doGetBean(beanDefinition.getBeanFactoryName());
        Method method = beanFactory.getClass().getMethod(beanDefinition.getBeanFactoryMethodName());
        return method.invoke(beanFactory, null);
    }
}
