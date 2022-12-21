package com.tanhua.server.interceptor;

import com.tanhua.model.domain.User;

/**
 * 工具类：负责保存登录用户的相关信息
 */
public class UserHolder {
    private static ThreadLocal<User> threadLocal = new ThreadLocal<>();

    /**
     * 将用户保存到threadLocal
     * @param user
     */
    public static void setUser(User user) {
        threadLocal.set(user);
    }

    /**
     * 从threadLocal获取用户
     * @return
     */
    public static User getUser() {
        return threadLocal.get();
    }

    public static Long getUserId() {
        return threadLocal.get().getId();
    }

    public static String getUserPhone() {
        return threadLocal.get().getMobile();
    }

    /**
     * 请求执行完毕后将用户信息用threadLocal中删除
     */
    public static void remove() {
        threadLocal.remove();
    }

}
