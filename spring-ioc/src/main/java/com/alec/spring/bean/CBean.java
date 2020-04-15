package com.alec.spring.bean;

/**
 * @Author: alec
 * Description:
 * @date: 21:20 2020-04-15
 */
public class CBean {

    private DBean dBean;

    private String name;

    private Integer age;

    public CBean(String name, Integer age) {
        this.name = name;
        this.age = age;
    }

    public void init () {
        System.out.println("CBean name :" + this.name + " age " + this.age + " f " + this.dBean.getBean());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public DBean getdBean() {
        return dBean;
    }

    public void setdBean(DBean dBean) {
        this.dBean = dBean;
    }
}
