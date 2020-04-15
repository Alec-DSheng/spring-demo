package com.alec.spring;

import com.alec.spring.bean.ABean;
import com.alec.spring.bean.BBean;
import com.alec.spring.bean.CBean;
import com.alec.spring.bean.DBean;
import com.alec.spring.di.BeanReference;
import com.alec.spring.di.PropertyValue;
import com.alec.spring.ioc.BeanDefinition;
import com.alec.spring.ioc.DefaultBeanFactory;
import com.alec.spring.ioc.GeneralBeanDefinition;
import com.alec.spring.ioc.PreInstanceBeanFactory;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

/**
 * @Author: alec
 * Description:
 * @date: 20:46 2020-04-15
 */
public class TestSpirngDi {



    @Test
    public void testDi() throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {

        GeneralBeanDefinition generalBeanDefinition = new GeneralBeanDefinition();
        generalBeanDefinition.setBeanClass(ABean.class);
        generalBeanDefinition.setScope(BeanDefinition.SCOPE_SINGLE);
        generalBeanDefinition.setConstructParamsValue(Arrays.asList(new BeanReference("bBean")));

        PreInstanceBeanFactory preInstanceBeanFactory = new PreInstanceBeanFactory();
        preInstanceBeanFactory.registerBeanDefinition("aBean", generalBeanDefinition);

        generalBeanDefinition = new GeneralBeanDefinition();
        generalBeanDefinition.setBeanClass(BBean.class);
        generalBeanDefinition.setScope(BeanDefinition.SCOPE_SINGLE);
        generalBeanDefinition.setConstructParamsValue(Arrays.asList(new BeanReference("aBean")));

        preInstanceBeanFactory.registerBeanDefinition("bBean", generalBeanDefinition);
        preInstanceBeanFactory.preInstanceSingletons();
    }

    @Test
    public void testProperty() {

        GeneralBeanDefinition generalBeanDefinition = new GeneralBeanDefinition();
        generalBeanDefinition.setBeanClass(DBean.class);
        generalBeanDefinition.setScope(BeanDefinition.SCOPE_SINGLE);

        DefaultBeanFactory defaultBeanFactory = new DefaultBeanFactory();
        defaultBeanFactory.registerBeanDefinition("dBean", generalBeanDefinition);

        System.out.println("dBean 注册完成");
        DBean dBean = (DBean) defaultBeanFactory.getBean("dBean");
        System.out.println(dBean.getBean());

        generalBeanDefinition = new GeneralBeanDefinition();
        generalBeanDefinition.setBeanClass(CBean.class);
        generalBeanDefinition.setScope(BeanDefinition.SCOPE_SINGLE);
        generalBeanDefinition.setConstructParamsValue(Arrays.asList("cBean name ", 12));
        PropertyValue propertyValue = new PropertyValue();
        propertyValue.setName("dBean");
        propertyValue.setValue(new BeanReference("dBean"));
        generalBeanDefinition.setPropertyValue(Arrays.asList(propertyValue));
        generalBeanDefinition.setInitMethod("init");

        defaultBeanFactory.registerBeanDefinition("cBean",generalBeanDefinition);

        defaultBeanFactory.getBean("cBean");

    }
}
