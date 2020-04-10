package com.alec.spring.ioc;

/**
 * @Author: alec
 * Description: 定义spring bean factory
 * @date: 09:17 2020-04-10
 */
public interface BeanFactory {

    /**
     * 获取Bean
     * @param beanName  bean名称
     * @return Bean 实例
     * */
    Object getBean(String beanName);


}
