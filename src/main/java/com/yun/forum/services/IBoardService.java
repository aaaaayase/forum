package com.yun.forum.services;

import com.yun.forum.model.Board;

import java.util.List;

/**
 * @author yun
 * @date 2024/9/16 21:17
 * @desciption: 版块业务逻辑接口
 */
public interface IBoardService {

    /**
     * 显示前num个版块
     *
     * @param num
     * @return
     */
    List<Board> selectByNum(Integer num);

    /**
     * 版块帖子数量 +1
     *
     * @param id
     * @return
     */
    void addOneArticleCountById(Long id);

    /**
     * 版块帖子数量 -1
     *
     * @param id
     * @return
     */
    void subOneArticleCountById(Long id);

    /**
     * 按照id查找版块
     *
     * @param id
     * @return
     */
    Board selectById(Long id);
}
