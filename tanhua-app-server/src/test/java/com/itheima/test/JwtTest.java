package com.itheima.test;

import io.jsonwebtoken.*;
import org.junit.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtTest {

    @Test
    public void testCreateToken() {
        //生成token
        //1、准备数据
        Map map = new HashMap();
        map.put("id",1);
        map.put("mobile","13800138000");
        //2、使用JWT的工具类生成token
        long now = System.currentTimeMillis();
        String token = Jwts.builder()
                .signWith(SignatureAlgorithm.HS512, "itcast") //指定加密算法
                .setClaims(map) //写入数据
                .setExpiration(new Date(now + 30000)) //失效时间
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
        String token = "eyJhbGciOiJIUzUxMiJ9.eyJtb2JpbGUiOiIxMzgwMDEzODAwMCIsImlkIjoxLCJleHAiOjE2MTgzOTcxOTV9.2lQiovogL5tJa0px4NC-DW7zwHFqZuwhnL0HPAZunieGphqnMPduMZ5TtH_mxDrgfiskyAP63d8wzfwAj-MIVw";
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey("itcast")
                    .parseClaimsJws(token)
                    .getBody();
            Object id = claims.get("id");
            Object mobile = claims.get("mobile");
            System.out.println(id + "--" + mobile);
        }catch (ExpiredJwtException e) {
            System.out.println("token已过期");
        }catch (SignatureException e) {
            System.out.println("token不合法");
        }

    }
}
