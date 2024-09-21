package com.yun.forum.services.impl;

import com.yun.forum.model.ArticleReply;
import com.yun.forum.services.IArticleReplyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author yun
 * @date 2024/9/21 9:29
 * @desciption:
 */
@SpringBootTest
class IArticleReplyServiceImplTest {

    @Autowired
    IArticleReplyService articleReplyService;

    @Transactional
    @Test
    void create() {
        ArticleReply articleReply = new ArticleReply();
        articleReply.setArticleId(2L);
        articleReply.setPostUserId(4L);
        articleReply.setContent("单元测试");
        articleReplyService.create(articleReply);

    }
}