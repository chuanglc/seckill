package com.chuang.seckill.config;


import com.chuang.seckill.entity.User;

/**
 * @author: LC
 * @date 2022/3/9 4:49 下午
 * @ClassName: UserContext
 */
public class UserContext {

    private static ThreadLocal<User> userThreadLocal = new ThreadLocal<>();

    public static void setUser(User user) {
        userThreadLocal.set(user);
    }

    public static User getUser() {
        return userThreadLocal.get();
    }
}
