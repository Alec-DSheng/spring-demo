package com.alec.spring.ioc;

/**
 * @Author: alec
 * Description: 定义Bean definition 注册器
 * 作用将bean definition 注册，获取等
 * @date: 09:25 2020-04-10
 */
public interface BeanDefinitionRegister {

    /**
     * 注册bean definition
     * */
    void registerBeanDefinition(String beanName, BeanDefinition beanDefinition);

    /**
     * 获取bean definition
     * */
    BeanDefinition getBeanDefinition(String beanName);

    /**
     * 是否包含bean
     * */
    boolean containBeanDefinition(String beanName);




























}
