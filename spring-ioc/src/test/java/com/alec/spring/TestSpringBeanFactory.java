package com.alec.spring;

import com.alec.spring.bean.People;
import com.alec.spring.bean.PeopleFactory;
import com.alec.spring.ioc.BeanDefinition;
import com.alec.spring.ioc.DefaultBeanFactory;
import com.alec.spring.ioc.GeneralBeanDefinition;
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
        GeneralBeanDefinition beanDefinition = new GeneralBeanDefinition();
        beanDefinition.setBeanClass(People.class);
        beanDefinition.setInitMethod("init");
        beanDefinition.setDestroyMethod("destroy");
        beanDefinition.setScope(BeanDefinition.SCOPE_SINGLE);
        beanFactory.registerBeanDefinition("people", beanDefinition);
    }

    @Test
    public void testStaticFactory() {
        GeneralBeanDefinition beanDefinition = new GeneralBeanDefinition();
        beanDefinition.setBeanClass(PeopleFactory.class);
        beanDefinition.setBeanFactoryMethodName("createPeople");
        beanDefinition.setScope(BeanDefinition.SCOPE_PROTOTYPE);
        beanFactory.registerBeanDefinition("staticBean", beanDefinition);
    }

    @Test
    public void testFactory() {
        GeneralBeanDefinition beanDefinition = new GeneralBeanDefinition();
        beanDefinition.setBeanClass(PeopleFactory.class);
        String name = "pf";
        beanFactory.registerBeanDefinition(name, beanDefinition);

        beanDefinition = new GeneralBeanDefinition();
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
