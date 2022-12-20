package com.itheima.test;

import io.jsonwebtoken.*;
import org.junit.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtTest {

    @Test
    public void testCreateToken() {
        // 1.准备数据
        Map map = new HashMap();
        map.put("id",1);
        map.put("mobile","13800138000");
        // 2.使用JWT工具生成JWT字符串
        String token = Jwts.builder()
                .signWith(SignatureAlgorithm.HS512, "itcast") // 指定加密算法和秘钥
                .setClaims(map) // 设置数据
                .setExpiration(new Date(System.currentTimeMillis() + 1000000)) // 设置有效期5秒
                .compact();
        System.out.println(token);
    }

    //解析token

    /**
     * SignatureException : token不合法
     * ExpiredJwtException：token已过期
     */
    @Test
    public void testParseToken() {
        try {
            String token = "eyJhbGciOiJIUzUxMiJ9.eyJtb2JpbGUiOiIxMzgwMDEzODAwMCIsImlkIjoxLCJleHAiOjE2NzE1MzE0Njd9.eGljWldVwurcmwkWzc-Jfm6XIsokCVx_TwMazLqiFk0rdabY9ALnbpUEavrqF_maN3FiWl9oOtjZKuJF1rfhUw";
            Claims claims = Jwts.parser()
                    .setSigningKey("itcast") // 设置秘钥
                    .parseClaimsJws(token) // 设置token
                    .getBody();
            Object id = claims.get("id");
            Object code = claims.get("mobile");

            System.out.println(id + "---" + code);
        } catch (SignatureException e){
            System.out.println("token不合法");
        } catch (ExpiredJwtException e) {
            System.out.println("token已过期");
        }


    }
}
