package com.alec.spring;

import com.alec.spring.bean.Car;
import com.alec.spring.bean.People;
import com.alec.spring.di.BeanReference;
import com.alec.spring.ioc.BeanDefinition;
import com.alec.spring.ioc.BeanFactory;
import com.alec.spring.ioc.DefaultBeanFactory;
import com.alec.spring.ioc.GeneralBeanDefinition;
import org.junit.Test;

import java.util.Arrays;

/**
 * @Author: alec
 * Description:
 * @date: 22:39 2020-04-14
 */
public class TestSpringBeanDi {

    @Test
    public void testBean() {

        GeneralBeanDefinition generalBeanDefinition = new GeneralBeanDefinition();
        generalBeanDefinition.setBeanClass(People.class);
        generalBeanDefinition.setScope(BeanDefinition.SCOPE_SINGLE);
        generalBeanDefinition.setInitMethod("init");

        DefaultBeanFactory beanFactory = new DefaultBeanFactory();
        beanFactory.registerBeanDefinition("people", generalBeanDefinition);

        People p = (People) beanFactory.getBean("people");
        p.work();

        generalBeanDefinition = new GeneralBeanDefinition();
        generalBeanDefinition.setBeanClass(Car.class);
        generalBeanDefinition.setScope(BeanDefinition.SCOPE_SINGLE);
        generalBeanDefinition.setConstructParamsValue(Arrays.asList(new BeanReference("people")));
//
//
        beanFactory.registerBeanDefinition("car", generalBeanDefinition);
//
//
        Car car = (Car) beanFactory.getBean("car");
        car.running();
    }
}
