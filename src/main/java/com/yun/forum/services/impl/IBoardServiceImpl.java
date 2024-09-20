package com.yun.forum.services.impl;

import com.yun.forum.common.AppResult;
import com.yun.forum.common.ResultCode;
import com.yun.forum.dao.BoardMapper;
import com.yun.forum.exception.ApplicationException;
import com.yun.forum.model.Board;
import com.yun.forum.services.IBoardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author yun
 * @date 2024/9/16 21:23
 * @desciption: 版块业务逻辑实现
 */
@Slf4j
@Service
public class IBoardServiceImpl implements IBoardService {

    @Autowired
    private BoardMapper boardMapper;

    @Override
    public List<Board> selectByNum(Integer num) {
        // 参数校验
        if (num <= 0 || num == null) {
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }

        List<Board> boards = boardMapper.selectByNum(num);
        // 返回结果
        return boards;
    }

    @Override
    public void addOneArticleCountById(Long id) {
        // 非空校验
        if (id == null || id <= 0) {
            log.warn(ResultCode.FAILED_BOARD_ARTICLE_COUNT.toString() + "id不合法");
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_BOARD_ARTICLE_COUNT));
        }

        // 查询版块
        Board board = boardMapper.selectByPrimaryKey(id);
        if (board == null) {
            log.warn(ResultCode.ERROR_IS_NULL.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.ERROR_IS_NULL));
        }

        // 新建用户用来更新
        Board boardUpdate = new Board();
        boardUpdate.setArticleCount(board.getArticleCount() + 1);
        boardUpdate.setId(board.getId());

        // 校验更新是否成功
        int row = boardMapper.updateByPrimaryKeySelective(boardUpdate);
        if (row != 1) {
            log.warn(ResultCode.FAILED.toString() + "受影响的行数不为1");
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED));
        }
    }

    @Override
    public Board selectById(Long id) {
        // 非空校验
        if (id == null) {
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }

        return boardMapper.selectByPrimaryKey(id);
    }

    @Override
    public void subOneArticleCountById(Long id) {
        // 非空校验
        if (id == null || id <= 0) {
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }

        // 校验用户是否存在
        Board board = boardMapper.selectByPrimaryKey(id);
        if (board == null) {
            log.warn(ResultCode.FAILED_USER_NOT_EXISTS.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_USER_NOT_EXISTS));
        }

        // 构造用于更新的对象
        Board boardUpdate = new Board();
        boardUpdate.setId(board.getId());
        boardUpdate.setArticleCount(board.getArticleCount() - 1);
        if (boardUpdate.getArticleCount() < 0) {
            boardUpdate.setArticleCount(0);
        }

        // 调用DAO更新
        int row = boardMapper.updateByPrimaryKeySelective(boardUpdate);
        if (row != 1) {
            log.warn(ResultCode.FAILED.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED));
        }

    }

}
