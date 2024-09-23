package com.yun.forum.dao;

import com.yun.forum.model.Article;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ArticleMapper {
    int insert(Article row);

    int insertSelective(Article row);

    Article selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Article row);

    int updateByPrimaryKeyWithBLOBs(Article row);

    int updateByPrimaryKey(Article row);

    /**
     * 查询所有帖子列表
     *
     * @return
     */
    List<Article> selectAll();

    /**
     * 根据boardId来查询相应的帖子
     *
     * @return
     */
    List<Article> selectAllByBoardId(@Param("boardId") Long boardId);

    /**
     * 根据id来查找与帖子详情
     *
     * @param id
     * @return
     */
    Article selectDetailById(@Param("id") Long id);

    /**
     * 根据用户id来查询帖子列表
     *
     * @param userId
     * @return
     */
    List<Article> selectByUserId(@Param("userId") Long userId);
}