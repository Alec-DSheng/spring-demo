package com.alec.spring.ioc;

import com.alec.spring.di.PropertyValue;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;

/**
 * @Author: alec
 * Description: 定义bean 实例的 定义
 *
 * @date: 09:20 2020-04-10
 */
public interface BeanDefinition {

    String SCOPE_SINGLE = "single";

    String SCOPE_PROTOTYPE = "prototype";

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

    /**
     * 定义初始化方法
     * */
    String getInitMethod();

    /**
     * 定义销毁方法
     * */
    String getDestroyMethod();

    /**
     * 定义构造参数
     * */
    List<?> getConstructorParamsValue();

    List<PropertyValue> getPropertyValue();

    /**
     * 以下四个方法用于为bean factory 提供缓存 原型bean的构造方法和 工厂方法参数
     * */

    Constructor<?> getConstructor();

    void setConstructor(Constructor<?> constructor);

    Method getFactoryMethod();

    void setFactoryMethod(Method method);

    boolean isSingle();

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
