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