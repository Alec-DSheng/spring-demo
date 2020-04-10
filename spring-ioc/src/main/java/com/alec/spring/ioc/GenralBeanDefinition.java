package com.alec.spring.ioc;

import org.apache.commons.lang3.StringUtils;

/**
 * @Author: alec
 * Description: 通用的bean definition  用于生成 bean 工厂生成bean时的
 * 相关bean 的定义信息
 * @date: 09:53 2020-04-10
 */
public class GenralBeanDefinition implements BeanDefinition {

    private Class<?> beanClass;

    private String beanFactoryName;


    private String beanFactoryMethodName;


    private String scope;

    private String initMethod;

    private String destroyMethod;


    public void setBeanClass(Class<?> beanClass) {
        this.beanClass = beanClass;
    }

    public void setBeanFactoryName(String beanFactoryName) {
        this.beanFactoryName = beanFactoryName;
    }

    public void setBeanFactoryMethodName(String beanFactoryMethodName) {
        this.beanFactoryMethodName = beanFactoryMethodName;
    }

    public void setScope(String scope) {
        if (StringUtils.isEmpty(scope)) {
            this.scope = BeanDefinition.SCOPE_SINGLE;
        } else {
            this.scope = scope;
        }
    }
    public void setInitMethod(String initMethod) {
        this.initMethod = initMethod;
    }

    public void setDestroyMethod(String destroyMethod) {
        this.destroyMethod = destroyMethod;
    }

    @Override
    public Class<?> getBeanClass() {
        return this.beanClass;
    }

    @Override
    public String getBeanFactoryName() {
        return this.beanFactoryName;
    }

    @Override
    public String getBeanFactoryMethodName() {
        return this.beanFactoryMethodName;
    }

    @Override
    public String getInitMethod() {
        return this.initMethod;
    }

    @Override
    public String getDestroyMethod() {
        return this.destroyMethod;
    }

    @Override
    public String getScope() {
        return this.scope;
    }

    @Override
    public boolean isSingle() {
        return BeanDefinition.SCOPE_SINGLE.equals(this.scope);
    }

    @Override
    public boolean isPrototype() {
        return BeanDefinition.SCOPE_PROTOTYPE.equals(this.scope);
    }
}
