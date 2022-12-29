package com.tanhua.admin.interceptor;


import com.tanhua.model.domain.Admin;

/**
 * 通过ThreadLocal的形式，存储用户的数据
 */
public class AdminHolder {

    private static ThreadLocal<Admin> admins = new ThreadLocal<>();

    //向当前线程存储数据
    public static void set(Admin admin) {
        admins.set(admin);
    }

    //从当前线程获取数据
    public static Admin getAdmin() {
        return admins.get();
    }

    public static void remove() {
        admins.remove();
    }

    //获取当前用户的id
    public static Long getUserId() {
        return admins.get().getId();
    }
}
