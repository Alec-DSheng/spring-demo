package com.alec.spring.bean;

/**
 * @Author: alec
 * Description:
 * @date: 22:40 2020-04-14
 */
public class Car {

    private String name;

    private People people;

    public Car(People people) {
        this.people = people;
    }

    public void running () {
        people.work();
        System.out.println("有一个人 " + people + "running");
    }
}
