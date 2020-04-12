package com.hzc.rpc.core;

/**
 * @author: hzc
 * @Date: 2020/03/19  09:27
 * @Description:
 */
public class Pair<T1, T2> {
    T1 Object1;
    T2 Object2;

    public void put(T1 object1, T2 object2) {
        this.Object1 = object1;
        this.Object2 = object2;
    }

    public T1 getObject1() {
        return Object1;
    }

    public T2 getObject2() {
        return Object2;
    }
}
