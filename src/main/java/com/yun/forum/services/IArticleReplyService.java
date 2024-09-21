package com.yun.forum.services;

import com.yun.forum.model.ArticleReply;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author yun
 * @date 2024/9/21 8:38
 * @desciption: 帖子回复业务逻辑接口
 */
public interface IArticleReplyService {

    /**
     * 创建帖子回复
     *
     * @param articleReply
     */
    @Transactional
    void create(ArticleReply articleReply);

    /**
     * 根据帖子id来查找底下的回复
     *
     * @param articleId
     * @return
     */
    List<ArticleReply> selectByArticleId(Long articleId);
}
