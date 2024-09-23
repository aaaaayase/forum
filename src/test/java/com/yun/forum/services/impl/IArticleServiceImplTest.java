package com.yun.forum.services.impl;

import com.yun.forum.model.Article;
import com.yun.forum.services.IArticleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author yun
 * @date 2024/9/17 14:53
 * @desciption:
 */

@SpringBootTest
class IArticleServiceImplTest {

    @Autowired
    IArticleService articleService;

    @Test
    @Transactional
    void create() {
        Article article = new Article();
        article.setBoardId(1l);
        article.setUserId(2l);
        article.setContent("无情横扫");
        article.setTitle("国士无双");

        articleService.create(article);

    }

    @Test
    void selectAllByBoardId() {

        System.out.println(articleService.selectAllByBoardId(1l));
    }

    @Test
    void selectDetailById() {

        System.out.println(articleService.selectDetailById(2l));
    }

    @Test
    @Transactional
    void modify() {
        articleService.modify(5l, "惊天浪淘沙", "汪汪汪");
    }

    @Test
    @Transactional
    void thumbsUpById() {
        articleService.thumbsUpById(3l);
    }

    @Test
    @Transactional
    void deleteById() {

        articleService.deleteById(4l);
    }

    @Transactional
    @Test
    void addOneReplyCountById() {

        articleService.addOneReplyCountById(4l);
    }

    @Test
    void selectByUserId() {
        articleService.selectByUserId(4l).forEach(System.out::println);
    }
}