package com.alec.spring.bean;

/**
 * @Author: alec
 * Description:
 * @date: 10:41 2020-04-10
 */
public class PeopleFactory {

    public static People createPeople () {
        return new People();
    }

    public People createInstance() {
        return new People();
    }
}
