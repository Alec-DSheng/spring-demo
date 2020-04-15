package com.alec.spring.di;

/**
 * @Author: alec
 * Description:
 * @date: 20:58 2020-04-15
 */
public class PropertyValue {

    private String name;

    private Object value;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
