### 模拟实现简单的Spring IOC 容器
    1. 定义Bean Factory 接口
    2. 定义Bean Definition
    3. 定义Bean Definition 注册器
    4. 实现通用的 bena definition 
        包含 bean 的创建方式，bean 作用域 以及 bean的初始化方法 和 销毁方法
    5. 实现简单的Bean factory工厂
       通过反射机制 创建bean 实例
       根据bean 描述信息 缓存bean实例    
    
### DI 部分实现
    1. 上一部分的 spring ioc 容器 实现的Bean Factory 创建bean 里面只有无参构造函数，及 无参的工厂方法
    2. 在 BeanDefinition 中增加接口
        - 构造参数的bean 类型
### 循环依赖如何实现
    构造对象时不能循环依赖，因为在创建实例时依赖另外一个实例
    ，但是另外一个实例创建时又依赖当前这个实例，造成僵死的局面。
    BeanFactory 一定要检测出是否有循环依赖。
    加入正在构造Bean的记录
    每一个bean在开始构造时加入到记录中，构造完成时移除，
    找出有循环依赖的bean时 抛出异常
    可以以属性的方式来完成循环依赖
    
    ThreadLocal:的作用
    ThreadLocal 线程安全
### 属性依赖
    属性依赖，某个属性依赖了一个值
    如何描述一个属性依赖。
    -- 属性名，属性值， 定义一个类 标识 属性名，属性值
    多个属性依赖
    list
    属性值的情况，和构造参数的值是否意义    