package com.tanhua.admin.service;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tanhua.admin.mapper.AnalysisMapper;
import com.tanhua.common.utils.LogOperationCodeConstants;
import com.tanhua.model.domain.Analysis;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class AnalysisService {
    @Resource
    private AnalysisMapper analysisMapper;

    public void getAnalysis() throws ParseException {
        // 1. 调用Mapper计算相关数据
        // 获取当前时间
        String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String yesterday = DateUtil.yesterday().toString("yyyy-MM-dd");
        // 登录人数
        Integer loginCount = this.analysisMapper.queryByTypeAndLogTime(LogOperationCodeConstants.LOGIN, today);
        // 注册用户
        Integer signupCount = this.analysisMapper.queryByTypeAndLogTime(LogOperationCodeConstants.SIGN_UP, today);
        // 活跃
        Integer activeCount = this.analysisMapper.queryByLogTime(today);
        Integer retainedCount = this.analysisMapper.queryNumRetention1d(today, yesterday);
        // 2. 根据日期查询，看看是更新还是新增数据
        QueryWrapper<Analysis> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("record_date", new SimpleDateFormat("yyyy-MM-dd").parse(today));
        Analysis analysis = this.analysisMapper.selectOne(queryWrapper);
        // 3. 封装数据
        // 4. 写入数据库
        if (analysis == null) {
            analysis = new Analysis();
            analysis.setRecordDate(new SimpleDateFormat("yyyy-MM-dd").parse(today));
            analysis.setCreated(new Date());
            analysis.setUpdated(new Date());
            analysis.setNumLogin(loginCount);
            analysis.setNumActive(activeCount);
            analysis.setNumRegistered(signupCount);
            analysis.setNumRetention1d(retainedCount);
            this.analysisMapper.insert(analysis);
        } else {
            analysis.setUpdated(new Date());
            analysis.setNumLogin(loginCount);
            analysis.setNumActive(activeCount);
            analysis.setNumRegistered(signupCount);
            analysis.setNumRetention1d(retainedCount);
            this.analysisMapper.updateById(analysis);
        }

    }
}
