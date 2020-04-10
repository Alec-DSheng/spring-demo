package com.alec.spring.ioc;

import org.apache.commons.lang3.StringUtils;

/**
 * @Author: alec
 * Description: 定义bean 实例的 定义
 *
 * @date: 09:20 2020-04-10
 */
public interface BeanDefinition {

    String SCOPE_SINGLE = "single";

    String SCOPE_PROTOTYPE = "ptototype";

    /**
     * Bean 的class
     * */
    Class<?> getBeanClass();

    /**
     * 获取生成Bean的 bean Factory 名
     * */
    String getBeanFactoryName();

    /**
     * 获取生成bean 的  static Factory 方法名
     * */
    String getBeanFactoryMethodName();

    String getInitMethod();

    String getDestroyMethod();
    /**
     * 获取bean 的作用域
     * */
    String getScope();

    boolean isSingle();

    boolean isPrototype();

    default boolean validate() {
        if (getBeanClass() == null) {
            return !StringUtils.isEmpty(getBeanFactoryName()) && !StringUtils.isEmpty(getBeanFactoryMethodName());
        }
        if (getBeanClass() != null) {
            return StringUtils.isEmpty(getBeanFactoryName());
        }
        return true;
    }
}
