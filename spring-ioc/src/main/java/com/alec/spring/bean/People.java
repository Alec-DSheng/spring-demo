package com.alec.spring.bean;

/**
 * @Author: alec
 * Description:
 * @date: 10:40 2020-04-10
 */
public class People {

    public void  work () {
        System.out.println("this is bean instance and do work" + this);
    }

    public void init () {
        System.out.println(" start working");
    }

    public void destroy() {
        System.out.println("stop work");
    }
}
