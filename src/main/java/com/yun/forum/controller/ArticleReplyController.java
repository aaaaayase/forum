package com.yun.forum.controller;

import com.yun.forum.common.AppResult;
import com.yun.forum.common.ResultCode;
import com.yun.forum.config.AppConfig;
import com.yun.forum.model.Article;
import com.yun.forum.model.ArticleReply;
import com.yun.forum.model.User;
import com.yun.forum.services.IArticleReplyService;
import com.yun.forum.services.IArticleService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author yun
 * @date 2024/9/21 9:53
 * @desciption: 帖子下回复api
 */
@Slf4j
@RequestMapping("/reply")
@RestController
public class ArticleReplyController {

    @Autowired
    private IArticleReplyService articleReplyService;

    @Autowired
    private IArticleService articleService;

    @PostMapping("/create")
    public AppResult create(HttpServletRequest request
            , @RequestParam("content") @NonNull String content
            , @RequestParam("articleId") @NonNull Long articleId) {
        // 获取登录对象
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute(AppConfig.USER_SESSION);
        // 判断用户是否被禁言
        if (user.getState() == 1) {
            return AppResult.failed(ResultCode.FAILED_USER_BANNED);
        }

        // 查询articleId对应的帖子 校验帖子状态
        Article article = articleService.selectById(articleId);
        if (article == null || article.getDeleteState() == 1) {
            return AppResult.failed(ResultCode.FAILED_ARTICLE_NOT_EXIST);
        }
        if (article.getState() == 1) {
            return AppResult.failed(ResultCode.FAILED_ARTICLE_BANNED);
        }

        // 建立对象准备插入
        ArticleReply articleReply = new ArticleReply();
        articleReply.setContent(content);
        articleReply.setPostUserId(user.getId());
        articleReply.setArticleId(articleId);

        // 写入回复
        articleReplyService.create(articleReply);
        // 返回
        return AppResult.success();
    }

    @GetMapping("/getReplies")
    public AppResult<List<ArticleReply>> getRepliesByArticleId(@RequestParam("articleId") @NonNull Long articleId) {
        // 获取帖子并且进行校验
        Article article = articleService.selectById(articleId);
        if (article == null || article.getDeleteState() == 1) {
            return AppResult.failed(ResultCode.FAILED_ARTICLE_NOT_EXIST);
        }
        if (article.getState() == 1) {
            return AppResult.failed(ResultCode.FAILED_ARTICLE_BANNED);
        }

        // 获得结果并返回
        List<ArticleReply> articleReplies = articleReplyService.selectByArticleId(articleId);
        return AppResult.success(articleReplies);
    }
}
