package com.alec.spring.di;

/**
 * @Author: alec
 * Description: 定义构造参数中bean 引用的接口
 * @date: 09:53 2020-04-13
 */
public class BeanReference {

    private String beanName;

    public BeanReference (String beanName) {
        super();
        this.beanName = beanName;
    }

    public String getBeanName(){
        return this.beanName;
    }
}
