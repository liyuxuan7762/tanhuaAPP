package com.tanhua.model.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Analysis{

    private Long id;
    /**
     * 日期
     */
    private Date recordDate;
    /**
     * 新注册用户数
     */
    private Integer numRegistered = 0;
    /**
     * 活跃用户数
     */
    private Integer numActive = 0;
    /**
     * 登陆次数
     */
    private Integer numLogin = 0;
    /**
     * 次日留存用户数
     */
    private Integer numRetention1d = 0;

    private Date created;

    private Date updated;
}