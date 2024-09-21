package com.yun.forum.services.impl;

import com.yun.forum.common.AppResult;
import com.yun.forum.common.ResultCode;
import com.yun.forum.dao.ArticleReplyMapper;
import com.yun.forum.exception.ApplicationException;
import com.yun.forum.model.ArticleReply;
import com.yun.forum.services.IArticleReplyService;
import com.yun.forum.services.IArticleService;
import com.yun.forum.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author yun
 * @date 2024/9/21 8:39
 * @desciption: 帖子回复业务逻辑实现类
 */
@Slf4j
@Service
public class IArticleReplyServiceImpl implements IArticleReplyService {

    @Autowired
    ArticleReplyMapper articleReplyMapper;

    @Autowired
    IArticleService articleService;

    @Override
    public void create(ArticleReply articleReply) {
        // 参数校验
        if (articleReply == null || StringUtils.isEmpty(articleReply.getContent())
                || articleReply.getPostUserId() == null
                || articleReply.getArticleId() == null) {
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }

        // 设置默认值
        Date date = new Date();
        articleReply.setUpdateTime(date);
        articleReply.setCreateTime(date);
        articleReply.setReplyId(null);
        articleReply.setReplyUserId(null);
        articleReply.setState((byte) 0);
        articleReply.setDeleteState((byte) 0);
        articleReply.setLikeCount(0);

        // 调用DAO来进行更新
        int row = articleReplyMapper.insert(articleReply);
        if (row != 1) {
            log.warn(ResultCode.ERROR_SERVICES.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.ERROR_SERVICES));
        }

        // 更新对应帖子的回复数
        articleService.addOneReplyCountById(articleReply.getArticleId());

        // 打印成功信息
        log.info("创建回复成功" + "userId:" + articleReply.getPostUserId() + " articleId:" + articleReply.getArticleId());
    }

    @Override
    public List<ArticleReply> selectByArticleId(Long articleId) {
        // 非空校验
        if (articleId == null || articleId <= 0) {
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }


        List<ArticleReply> articleReplies = articleReplyMapper.selectByArticleId(articleId);

        return articleReplies;
    }
}
