package com.yun.forum.controller;

import com.yun.forum.common.AppResult;
import com.yun.forum.common.ResultCode;
import com.yun.forum.config.AppConfig;
import com.yun.forum.exception.ApplicationException;
import com.yun.forum.model.Article;
import com.yun.forum.model.Board;
import com.yun.forum.model.User;
import com.yun.forum.services.IArticleService;
import com.yun.forum.services.IBoardService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yun
 * @date 2024/9/17 16:01
 * @desciption: 帖子api接口
 */
@Slf4j
@RestController
@RequestMapping("/article")
public class ArticleController {

    @Autowired
    IBoardService boardService;
    @Autowired
    IArticleService articleService;

    @PostMapping("/create")
    public AppResult create(HttpServletRequest request,
                            @RequestParam("boardId") @NonNull Long boardId,
                            @RequestParam("title") @NonNull String title,
                            @RequestParam("content") @NonNull String content) {
        // 获取登录的用户对象
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute(AppConfig.USER_SESSION);
        // 判断此时的用户状态
        if (user.getState() == 1) {
            log.warn(ResultCode.FAILED_USER_BANNED.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_USER_BANNED));
        }
        // 获取
        Board board = boardService.selectById(boardId);
        // 判断
        if (board == null || board.getState() == 1 || board.getDeleteState() == 1) {
            log.warn(ResultCode.FAILED_BOARD_BANNED.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_BOARD_BANNED));
        }

        // 封装帖子对象
        Article article = new Article();
        article.setTitle(title);
        article.setContent(content);
        article.setUserId(user.getId());
        article.setBoardId(boardId);

        articleService.create(article);

        return AppResult.success();
    }

    @GetMapping("/getAllByBoardId")
    public AppResult<List<Article>> getAllByBoardId(@RequestParam(value = "boardId", required = false) Long boardId) {
        List<Article> articles = null;
        // 版块id为null未传入 显示首页帖子
        if (boardId == null) {
            articles = articleService.selectAll();

        } else {
            // 版块id传入则显示指定版块的帖子
            articles = articleService.selectAllByBoardId(boardId);
        }
        // 查询出问题则返回空的列表
        if (articles == null) {
            articles = new ArrayList<>();
        }
        return AppResult.success(articles);
    }

    @GetMapping("/details")
    public AppResult<Article> getDetails(HttpServletRequest request, @RequestParam("id") @NonNull Long id) {
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute(AppConfig.USER_SESSION);
        Article article = articleService.selectDetailById(id);
        if (article == null) {
            log.warn(ResultCode.FAILED_ARTICLE_NOT_EXIST.toString());
            return AppResult.failed(ResultCode.FAILED_ARTICLE_NOT_EXIST);
        }

        if (user.getId() == article.getUserId()) {
            article.setOwn(true);
        }

        return AppResult.success(article);
    }

    @PostMapping("/modify")
    public AppResult modify(HttpServletRequest request
            , @RequestParam("id") @NonNull Long id
            , @RequestParam("title") @NonNull String title
            , @RequestParam("content") @NonNull String content) {
        // 获取登录用户
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute(AppConfig.USER_SESSION);

        // 校验用户状态
        if (user.getDeleteState() == 1 || user.getState() == 1) {
            return AppResult.failed(ResultCode.FAILED_USER_BANNED);
        }

        // 获取帖子对应的用户id
        Article article = articleService.selectById(id);
        // 校验帖子
        if (article == null) {
            return AppResult.failed(ResultCode.FAILED_ARTICLE_NOT_EXIST);
        }

        // 校验当前登录的用户是否为帖子作者
        if (article.getUserId() != user.getId()) {
            return AppResult.failed(ResultCode.FAILED_FORBIDDEN);
        }

        // 判断帖子当前的状态
        if (article.getState() == 1 || article.getDeleteState() == 1) {
            return AppResult.failed(ResultCode.FAILED_ARTICLE_BANNED);
        }

        // 修改帖子信息
        articleService.modify(id, title, content);

        log.info("id为" + id + "的帖子title更新为：" + title + ", content修改为:" + content);

        // 返回正确的结果
        return AppResult.success();
    }

    @GetMapping("/thumbsUp")
    public AppResult thumbsUp(HttpServletRequest request, @RequestParam("id") @NonNull Long id) {
        // 校验用户的状态
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute(AppConfig.USER_SESSION);
        if (user.getState() == 1) {
            return AppResult.failed(ResultCode.FAILED_USER_BANNED);
        }
        // 调用service
        articleService.thumbsUpById(id);
        // 返回结果
        return AppResult.success();
    }

    @PostMapping("/delete")
    public AppResult deleteById(HttpServletRequest request, @RequestParam("id") @NonNull Long id) {
        // 校验当前用户状态
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute(AppConfig.USER_SESSION);
        if (user.getState() == 1) {
            return AppResult.failed(ResultCode.FAILED_USER_BANNED);
        }

        // 获取帖子详情
        Article article = articleService.selectById(id);
        if (article == null || article.getState() == 1 || article.getDeleteState() == 1) {
            return AppResult.failed(ResultCode.FAILED_ARTICLE_NOT_EXIST);
        }

        // 检验当前用户是否是帖子作者
        if (user.getId() != article.getUserId()) {
            return AppResult.failed(ResultCode.FAILED_FORBIDDEN);
        }

        // 更新即删除帖子
        articleService.deleteById(id);

        return AppResult.success();
    }
}
