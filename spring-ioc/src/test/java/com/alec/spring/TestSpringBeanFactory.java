package com.alec.spring;

import com.alec.spring.bean.People;
import com.alec.spring.bean.PeopleFactory;
import com.alec.spring.ioc.BeanDefinition;
import com.alec.spring.ioc.DefaultBeanFactory;
import com.alec.spring.ioc.GenralBeanDefinition;
import org.junit.AfterClass;
import org.junit.Test;

/**
 * @Author: alec
 * Description:
 * @date: 10:44 2020-04-10
 */
public class TestSpringBeanFactory {

    private static DefaultBeanFactory beanFactory = new DefaultBeanFactory();

    @Test
    public void testConstructor() {
        GenralBeanDefinition beanDefinition = new GenralBeanDefinition();
        beanDefinition.setBeanClass(People.class);
        beanDefinition.setInitMethod("init");
        beanDefinition.setDestroyMethod("destory");
        beanDefinition.setScope(BeanDefinition.SCOPE_SINGLE);
        beanFactory.registerBeanDefinition("people", beanDefinition);
    }

    @Test
    public void testStaticFactory() {
        GenralBeanDefinition beanDefinition = new GenralBeanDefinition();
        beanDefinition.setBeanClass(PeopleFactory.class);
        beanDefinition.setBeanFactoryMethodName("createPeople");
        beanFactory.registerBeanDefinition("staticBean", beanDefinition);
    }

    @Test
    public void testFactory() {
        GenralBeanDefinition beanDefinition = new GenralBeanDefinition();
        beanDefinition.setBeanClass(PeopleFactory.class);
        String name = "pf";
        beanFactory.registerBeanDefinition(name, beanDefinition);

        beanDefinition = new GenralBeanDefinition();
        beanDefinition.setBeanFactoryName(name);
        beanDefinition.setBeanFactoryMethodName("createInstance");
        beanDefinition.setScope(BeanDefinition.SCOPE_SINGLE);
        beanFactory.registerBeanDefinition("factoryBean", beanDefinition);
    }

    @AfterClass
    public static void test () {
        System.out.println("构造方法---------------------------------");
        for (int i = 0; i <3 ; i++) {
            People people = (People) beanFactory.getBean("people");
            people.work();
        }
        System.out.println("静态工厂---------------------------------");
        for (int i = 0; i <3 ; i++) {
            People people = (People) beanFactory.getBean("staticBean");
            people.work();
        }
        System.out.println("工厂方法---------------------------------");
        for (int i = 0; i <3 ; i++) {
            People people = (People) beanFactory.getBean("factoryBean");
            people.work();
        }
        beanFactory.close();
    }
}
