package com.alec.spring.ioc;

import com.alec.spring.di.BeanReference;
import com.alec.spring.di.PropertyValue;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.Closeable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: alec
 * Description: 定义ioc bean factory 1.0
 * @date: 09:58 2020-04-10
 */
public class DefaultBeanFactory implements BeanDefinitionRegister, BeanFactory, Closeable {

    private final Integer INIT_LENGTH = 64;

    private Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>(INIT_LENGTH);

    private Map<String, String> asixNameMap = new ConcurrentHashMap<>(INIT_LENGTH);

    private Map<String, Object>  beanMap = new ConcurrentHashMap<>(INIT_LENGTH);

    private ThreadLocal<Set<String>> buildBean = new ThreadLocal<>();

    @Override
    public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) {
        Objects.requireNonNull(beanName, "bean name is not null");
        Objects.requireNonNull(beanDefinition, "bean definition is not null");
        if (!beanDefinition.validate()) {
            throw new BeanDefinitionException("validate error");
        }
        if (containBeanDefinition(beanName)) {
            throw new BeanDefinitionException("bean definition exist");
        }
        beanDefinitionMap.put(beanName, beanDefinition);
    }

    @Override
    public BeanDefinition getBeanDefinition(String beanName) {
        Objects.requireNonNull(beanName, "bean name is not null");
        return beanDefinitionMap.get(beanName);
    }

    @Override
    public boolean containBeanDefinition(String beanName) {
        Objects.requireNonNull(beanName, "bean name is not null");
        return beanDefinitionMap.containsKey(beanName);
    }

    @Override
    public Object getBean(String beanName) {
        try {
            return this.doGetBean(beanName);
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

//    @Override
//    public Constructor<?> determineConstructor(BeanDefinition beanDefinition, Object[] args) {
//        return null;
//    }

    @Override
    public Method determineMethod(BeanDefinition beanDefinition, Object[] args) {
        return null;
    }


    protected Object doGetBean(String beanName) throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Objects.requireNonNull(beanName, "bean name is not null");
        Object instance = beanMap.get(beanName);
        if (instance != null) {
            return instance;
        }
        Set<String> ingBeans = buildBean.get();
        if (ingBeans == null) {
            ingBeans = new HashSet<>();
            buildBean.set(ingBeans);
        }
        /**
         * 循环检测是否有构造函数循环依赖bean
         * */
        if (ingBeans.contains(beanName)) {
            throw new BeanDefinitionException(beanName + " 循环依赖 " + ingBeans);
        }
        ingBeans.add(beanName);
        BeanDefinition beanDefinition = getBeanDefinition(beanName);
        Class<?> beanClass = beanDefinition.getBeanClass();
        if (beanClass != null) {
            if (StringUtils.isEmpty(beanDefinition.getBeanFactoryMethodName())) {
                /**
                 * class 不为空， bean factory method 为空 通过构造器生成实例
                 * */
                instance = createBeanByConstruct(beanDefinition);
            } else {
                instance = createBeanByStaticBeanFactory(beanDefinition);
            }
        } else {
            instance = createBeanByBeanFactory(beanDefinition);
        }
        ingBeans.remove(beanName);
        /**
         * 创建完Bean 后 复制属性依赖
         * */
        try {
            this.setPropertyDIValue(beanDefinition, instance);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        this.doInit(instance, beanDefinition);
        if (beanDefinition.isSingle()) {
            beanMap.put(beanName, instance);
        }
        return instance;
    }



    @Override
    public void close() {
        for (Map.Entry<String, BeanDefinition> entry : this.beanDefinitionMap.entrySet()) {
            String key = entry.getKey();
            BeanDefinition beanDefinition = entry.getValue();
            if (beanDefinition.isSingle() && !StringUtils.isEmpty(beanDefinition.getDestroyMethod())) {
                Object instance = this.beanMap.get(key);
                Method method ;
                try {
                    method = instance.getClass().getMethod(beanDefinition.getDestroyMethod(), null);
                    method.invoke(instance, null);
                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void doInit(Object instance, BeanDefinition beanDefinition) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        if (!StringUtils.isEmpty(beanDefinition.getInitMethod())) {
            Method method = instance.getClass().getMethod(beanDefinition.getInitMethod());
            method.invoke(instance, null);
        }
    }

    private Object createBeanByConstruct(BeanDefinition beanDefinition) throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
        /**
         * 1.根据参数类型进行匹配查找，如果未匹配 则进行第二部
         * 2.获取所有的构造方法遍历，做参数数量过滤，比对类型是否匹配
         * 3. 做一个优化
         *  当判断出构造方法和工厂方法后，对于原型bean 来讲，缓存构造方法和工厂方法
         *
         * */
        Object[] params = this.getConstructParmasValues(beanDefinition);
        if (params == null) {
            return  beanDefinition.getBeanClass().newInstance();
        } else {
            /**
             * 判断获取构造函数
             * 获取对应的构造方法，并构建Bean
             * */
            try {
                return determineConstructor(beanDefinition, params).newInstance(params);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private Object createBeanByStaticBeanFactory(BeanDefinition beanDefinition) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = beanDefinition.getBeanClass().getMethod(beanDefinition.getBeanFactoryMethodName(), null);
        return method.invoke(beanDefinition.getBeanClass(), null);
    }

    private Object createBeanByBeanFactory(BeanDefinition beanDefinition) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {
        Object beanFactory = this.doGetBean(beanDefinition.getBeanFactoryName());
        Method method = beanFactory.getClass().getMethod(beanDefinition.getBeanFactoryMethodName());
        return method.invoke(beanFactory, null);
    }
    /**
     * 构造参中bean 的引用转换成真正的值
     * */
    private Object[] getConstructParmasValues(BeanDefinition beanDefinition) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        return this.getRealValue(beanDefinition.getConstructorParamsValue());
    }

    private Object[] getRealValue(List<?> values) throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        if (CollectionUtils.isEmpty(values)) {
            return null;
        }
        Object obj;
        int i = 0;
        /**
         * 递归实现类型的转换
         * */
        Object[] params = new Object[values.size()];
        for (Object object: values) {
            if (object == null) {
                obj = null;
            } else if (object instanceof BeanReference) {
                obj = getObjectForReference(object);
            } else if (object instanceof  Map) {
                //TODO 处理类型为MAP的类型转换
                obj = getObjectForMap((Map<String, Object>) object);
            } else if (object instanceof Object[] ) {
                //TODO 处理类型为Object数组的类型转换
                obj = getObjectForData((Object[]) object);
            } else if (object instanceof Collection) {
                //TODO 处理类型为Collection的数据类型转换
                obj = getObjectForCollection((Collection) object);
            }  else if (object instanceof Properties) {
                //TODO 处理类型为Properties的类型
                obj = getObjectForPorperties((Properties) object);
            } else {
                obj = object;
            }
            params[i++] = obj;
        }
        return params;
    }
    private Object getObjectForReference(Object object) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        BeanReference beanReference = (BeanReference) object;
        return this.doGetBean(beanReference.getBeanName());
    }

    private Object getObjectForMap(Map<String, Object> params) {

        return null;
    }

    private Object getObjectForData(Object[] data) {

        return null;
    }

    private Object getObjectForCollection(Collection<?> collection) {
        return null;
    }

    private Object getObjectForPorperties(Properties properties) {
        return null;
    }

    private Constructor<?> determineConstructor(BeanDefinition beanDefinition, Object[] args) throws Exception {

        Constructor constructor;

        if (args == null) {
            return beanDefinition.getBeanClass().getConstructor(null);
        }
        constructor = beanDefinition.getConstructor();
        if (constructor != null) {
            return constructor;
        }
        /**
         * 根据参数类型获取精确匹配的构造方法
         * */
        Class[] paramsType = new Class[args.length];
        int j = 0;
        for (Object p: args) {
            paramsType[j++] = p.getClass();
        }
        try {
            constructor = beanDefinition.getBeanClass().getConstructor(paramsType);

        } catch (Exception e) {
            e.printStackTrace();
        }
        if (constructor == null) {
            /**
             * 第一次精确匹配没有命中，需要第二次遍历所有
             * */
            outer : for (Constructor c : beanDefinition.getBeanClass().getConstructors()) {
                Class[] pts = c.getParameterTypes();
                if (pts.length == args.length) {
                    // 参数个数一直
                    for (int i = 0; i< pts.length; i ++) {
                        if (!pts[i].isAssignableFrom(args[i].getClass())) {
                            continue outer;
                        }
                    }
                    constructor = c;
                    break outer;
                }
            }
        }
        if (constructor != null) {
            if (!beanDefinition.isSingle()) {
                beanDefinition.setConstructor(constructor);
            }

        } else {
            throw new BeanDefinitionException("构造方法不合法");
        }
        return constructor;
    }
    private void setPropertyDIValue(BeanDefinition beanDefinition, Object instance) throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, InstantiationException {
       List<PropertyValue> propertyValues = beanDefinition.getPropertyValue();
       if (CollectionUtils.isEmpty(propertyValues)) {
           return;
       }
       for (PropertyValue pv: propertyValues) {
           if (StringUtils.isEmpty(pv.getName())) {
               continue;
           }
           Class<?> clazz = instance.getClass();
           Field field = clazz.getDeclaredField(pv.getName());
           field.setAccessible(true);
           Object rv = pv.getValue();
           Object v = null;
           if (rv == null) {
               v = null;
           } else if (rv instanceof BeanReference) {
               v = this.doGetBean(((BeanReference) rv).getBeanName());
           } else {
               v = null;
           }
           field.set(instance, v);
       }
    }
}
