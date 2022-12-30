package com.tanhua.admin.task;

import com.tanhua.admin.service.AnalysisService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.text.ParseException;

@Component
public class AnalysisTask {

    @Resource
    private AnalysisService analysisService;

    /**
     * 定时任务，每隔一定的时间就去计算一下统计数据
     * @throws ParseException
     */
    @Scheduled( cron = "0/20 * * * * ? ")
    public void analysis() throws ParseException {
        this.analysisService.getAnalysis();
    }
}
