package com.tanhua.dubbo.api;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tanhua.dubbo.mappers.QuestionMapper;
import com.tanhua.model.domain.Question;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

@DubboService
public class QuestionApiImpl implements QuestionApi {
    @Resource
    private QuestionMapper questionMapper;

    @Override
    public Question getQuestionByUserId(Long userId) {
        LambdaQueryWrapper<Question> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Question::getUserId, userId);
        return this.questionMapper.selectOne(queryWrapper);
    }

    @Override
    public void save(Question question) {
        this.questionMapper.insert(question);
    }

    @Override
    public void update(Question question) {
        this.questionMapper.updateById(question);
    }
}
