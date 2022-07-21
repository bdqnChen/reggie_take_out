package com.bdqn.reggie.common;

/**
 * 基于threadLocal封装的一个工具类，用于保存和获取用户ID
 */
public class BaseContext {
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<Long>();

    public static Long getCurrentId() {
        return threadLocal.get();
    }

    public static void setCurrentId(Long id) {
         threadLocal.set(id);
    }
}
