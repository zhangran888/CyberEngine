package com.datacyber.config;


public interface BaseEnums<K, V> {

    /**
     * 获取枚举值的key
     */
    K getCode();

    /**
     * 获取枚举值的value
     */
    V getValue();

}
