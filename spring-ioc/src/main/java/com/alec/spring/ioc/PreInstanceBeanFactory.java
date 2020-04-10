package com.alec.spring.ioc;


import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: alec
 * Description: 提前实例化bean, 容器启动即加载Bean。
 * @date: 10:55 2020-04-10
 */
public class PreInstanceBeanFactory extends DefaultBeanFactory {

    private List<String> beanNames = new ArrayList<>();

    @Override
    public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) {
        super.registerBeanDefinition(beanName, beanDefinition);
        synchronized (beanNames) {
            beanNames.add(beanName);
        }
    }

    public void preInstanceSingletons() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        synchronized (beanNames) {
            for (String name: beanNames) {
                BeanDefinition beanDefinition = this.getBeanDefinition(name);
                if (beanDefinition.isSingle()) {
                    this.doGetBean(name);
                }
            }
        }
    }
}
