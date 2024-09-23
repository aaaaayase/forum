package com.yun.forum.services.impl;

import com.yun.forum.common.AppResult;
import com.yun.forum.common.ResultCode;
import com.yun.forum.dao.ArticleMapper;
import com.yun.forum.exception.ApplicationException;
import com.yun.forum.model.Article;
import com.yun.forum.model.Board;
import com.yun.forum.model.User;
import com.yun.forum.services.IArticleService;
import com.yun.forum.services.IBoardService;
import com.yun.forum.services.IUserService;
import com.yun.forum.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author yun
 * @date 2024/9/17 9:49
 * @desciption: 帖子业务逻辑实现类
 */

@Slf4j
@Service
public class IArticleServiceImpl implements IArticleService {

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private IUserService userService;

    @Autowired
    private IBoardService boardService;

    @Override
    public void create(Article article) {
        // 非空校验
        if (article == null || StringUtils.isEmpty(article.getContent())
                || StringUtils.isEmpty(article.getTitle())
                || article.getBoardId() == null
                || article.getUserId() == null) {
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }

        // 设置默认值
        article.setState((byte) 0);
        article.setDeleteState((byte) 0);
        Date date = new Date();
        article.setCreateTime(date);
        article.setLikeCount(0);
        article.setReplyCount(0);
        article.setUpdateTime(date);
        article.setVisitCount(0);

        // 插入要发布的帖子
        int row = articleMapper.insertSelective(article);
        // 校验插入是否成功
        if (row <= 0) {
            log.warn(ResultCode.FAILED_CREATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_CREATE));
        }

        // 获取帖子对应的用户id 并且查找相应用户
        User user = userService.selectById(article.getUserId());
        // 校验
        if (user == null) {
            log.warn(ResultCode.FAILED_CREATE.toString() + "发帖失败，user id=" + article.getUserId());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_CREATE));
        }
        userService.addOneArticleCountById(user.getId());


        // 获取帖子相应的版块id 并且查找相应的版块
        Board board = boardService.selectById(article.getBoardId());
        // 校验
        if (board == null) {
            log.warn(ResultCode.FAILED_CREATE.toString() + "发帖失败，board id=" + article.getBoardId());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_CREATE));
        }
        boardService.addOneArticleCountById(board.getId());

        // 打印日志
        log.info(ResultCode.SUCCESS.toString() +
                ", user id = " + article.getUserId() +
                ", board id = " + article.getBoardId() +
                ", article id = " + article.getId() + "发帖成功");
    }

    @Override
    public List<Article> selectAll() {
        return articleMapper.selectAll();
    }

    @Override
    public List<Article> selectAllByBoardId(Long boardId) {
        // 非空校验
        if (boardId == null || boardId <= 0) {
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        // 对版块进行校验
        Board board = boardService.selectById(boardId);
        if (board == null) {
            log.warn(ResultCode.FAILED_BOARD_NOT_EXIST.toString() + ", board id = " + boardId);
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_BOARD_NOT_EXIST));
        }

        // 调用DAO查询
        return articleMapper.selectAllByBoardId(boardId);
    }

    @Override
    public Article selectDetailById(Long id) {
        // 非空校验
        if (id == null || id <= 0) {
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        // 查找获取帖子信息
        Article article = articleMapper.selectDetailById(id);
        if (article == null) {
            log.warn(ResultCode.FAILED_ARTICLE_NOT_EXIST.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_ARTICLE_NOT_EXIST));
        }
        // 构造帖子对象用于更新帖子的访问量属性
        Article articleUpdate = new Article();
        articleUpdate.setId(article.getId());
        articleUpdate.setVisitCount(article.getVisitCount() + 1);
        int row = articleMapper.updateByPrimaryKeySelective(articleUpdate);
        // 校验
        if (row != 1) {
            log.warn(ResultCode.ERROR_SERVICES.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.ERROR_SERVICES));
        }

        // 返回正经对象 当然访问此数也要加一
        article.setVisitCount(article.getVisitCount() + 1);
        return article;
    }

    @Override
    public Article selectById(Long id) {
        // 非空校验
        if (id == null || id <= 0) {
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        // 获取对应的帖子
        Article article = articleMapper.selectByPrimaryKey(id);
        // 返回结果
        return article;
    }

    @Override
    public void modify(Long id, String title, String content) {
        // 非空校验
        if (id == null || id <= 0 || StringUtils.isEmpty(title) || StringUtils.isEmpty(content)) {
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }

        // 创建新对象用于修改帖子对象
        Article articleUpdate = new Article();
        articleUpdate.setId(id);
        articleUpdate.setTitle(title);
        articleUpdate.setContent(content);
        articleUpdate.setUpdateTime(new Date());

        // 调用DAO层
        int row = articleMapper.updateByPrimaryKeySelective(articleUpdate);
        // 校验
        if (row != 1) {
            log.warn(ResultCode.ERROR_SERVICES.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.ERROR_SERVICES));
        }

    }

    @Override
    public void thumbsUpById(Long id) {
        // 非空校验
        if (id == null || id <= 0) {
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }

        // 获取对象
        Article article = articleMapper.selectByPrimaryKey(id);

        // 校验article 校验帖子是否存在
        if (article == null) {
            log.warn(ResultCode.FAILED_ARTICLE_NOT_EXIST.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_ARTICLE_NOT_EXIST));
        }

        // 校验帖子状态是否异常
        if (article.getDeleteState() == 1 || article.getState() == 1) {
            log.warn(ResultCode.FAILED_ARTICLE_BANNED.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_ARTICLE_BANNED));
        }

        // 设置用于更新的对象
        Article articleUpdate = new Article();
        articleUpdate.setId(article.getId());
        articleUpdate.setLikeCount(article.getLikeCount() + 1);
        articleUpdate.setUpdateTime(new Date());

        // 更新帖子点赞数
        int row = articleMapper.updateByPrimaryKeySelective(articleUpdate);

        // 校验更新是否成功
        if (row != 1) {
            log.warn(ResultCode.ERROR_SERVICES.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.ERROR_SERVICES));
        }

    }

    @Override
    public void deleteById(Long id) {
        // 非空校验
        if (id == null || id <= 0) {
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }

        // 根据id来查找对象
        Article article = articleMapper.selectByPrimaryKey(id);
        if (article == null) {
            log.warn(ResultCode.FAILED_ARTICLE_NOT_EXIST.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_ARTICLE_NOT_EXIST));
        }

        // 构造更新对象
        Article articleUpdate = new Article();
        articleUpdate.setId(article.getId());
        articleUpdate.setDeleteState((byte) 1);

        // 调用DAO进行更新
        int row = articleMapper.updateByPrimaryKeySelective(articleUpdate);

        // 校验更新效果
        if (row != 1) {
            log.warn(ResultCode.ERROR_SERVICES.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.ERROR_SERVICES));
        }

        // 更新版块帖子数以及用户帖子数
        boardService.subOneArticleCountById(article.getBoardId());
        userService.subOneArticleCountById(article.getUserId());

        log.info("删除成功");
    }

    @Override
    public void addOneReplyCountById(Long id) {
        // 非空校验
        if (id == null || id <= 0) {
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }

        // 调用DAO 获取对象
        Article article = articleMapper.selectByPrimaryKey(id);
        // 校验对象
        if (article == null || article.getDeleteState() == 1) {
            log.warn(ResultCode.FAILED_ARTICLE_NOT_EXIST.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_ARTICLE_NOT_EXIST));
        }
        // 校验对象状态
        if (article.getState() == 1) {
            log.warn(ResultCode.FAILED_ARTICLE_BANNED.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_ARTICLE_BANNED));
        }

        // 设置更新对象
        Article articleUpdate = new Article();
        articleUpdate.setReplyCount(article.getReplyCount() + 1);
        articleUpdate.setId(article.getId());
        articleUpdate.setUpdateTime(new Date());

        // 调用DAO来更新帖子回复数
        int row = articleMapper.updateByPrimaryKeySelective(articleUpdate);
        // 校验更新是否成功
        if (row != 1) {
            log.warn(ResultCode.ERROR_SERVICES.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.ERROR_SERVICES));
        }
    }

    @Override
    public List<Article> selectByUserId(Long userId) {
        // 非空校验
        if (userId == null || userId <= 0) {
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }

        // 查找相应用户来确定用户状态
        User user = userService.selectById(userId);
        if (user == null || user.getDeleteState() == 1) {
            log.warn(ResultCode.FAILED_USER_NOT_EXISTS.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_USER_NOT_EXISTS));
        }
        if (user.getState() == 1) {
            log.warn(ResultCode.FAILED_USER_BANNED.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_USER_BANNED));
        }

        // 调用DAO 查找相应的帖子或帖子列表
        List<Article> articles = articleMapper.selectByUserId(userId);

        return articles;
    }
}
