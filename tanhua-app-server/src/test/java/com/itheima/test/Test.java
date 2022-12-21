package com.itheima.test;

import org.apache.commons.codec.digest.DigestUtils;

public class Test {
    @org.junit.Test
    public void test() {
        String s = DigestUtils.md5Hex("123456");
        System.out.println(s);
    }
}
