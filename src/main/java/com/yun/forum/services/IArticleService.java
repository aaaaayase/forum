package com.yun.forum.services;

import com.yun.forum.model.Article;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author yun
 * @date 2024/9/17 9:49
 * @desciption: 帖子业务逻辑接口
 */
public interface IArticleService {

    /**
     * 发布帖子
     *
     * @param article
     * @return
     */
    @Transactional
    void create(Article article);

    /**
     * 查询所有帖子
     *
     * @return
     */
    List<Article> selectAll();

    /**
     * 根据boardId查询所有帖子
     *
     * @return
     */
    List<Article> selectAllByBoardId(Long boardId);

    /**
     * 根据id来查询帖子详情
     *
     * @param id
     * @return
     */
    Article selectDetailById(Long id);

    /**
     * 编辑帖子 修改
     *
     * @param id
     * @param title
     * @param content
     */
    void modify(Long id, String title, String content);

    /**
     * 获取对应id的帖子
     *
     * @param id
     * @return
     */
    Article selectById(Long id);

    /**
     * 点赞
     *
     * @param id
     */
    void thumbsUpById(Long id);

    /**
     * 通过id来删除帖子
     *
     * @param id
     */
    @Transactional
    void deleteById(Long id);

    @Transactional
    void addOneReplyCountById(Long id);
}
