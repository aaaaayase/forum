package com.yun.forum.controller;

import com.yun.forum.common.AppResult;
import com.yun.forum.common.ResultCode;
import com.yun.forum.exception.ApplicationException;
import com.yun.forum.model.Board;
import com.yun.forum.services.impl.IBoardServiceImpl;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yun
 * @date 2024/9/16 21:31
 * @desciption: 版块api接口
 */
@Slf4j
@RestController
@RequestMapping("/board")
public class BoardController {
    // 默认版块数
    @Value("${panda-forum.index.board-num:9}")
    private Integer indexBoardNum;

    @Autowired
    private IBoardServiceImpl boardService;

    /**
     * 显示前num个版块
     *
     * @return
     */
    @GetMapping("/topList")
    public AppResult<List<Board>> topList() {
        log.info("配置的版块个数" + indexBoardNum);
        List<Board> boards = boardService.selectByNum(indexBoardNum);
        if (boards == null) {
            boards = new ArrayList<>();
        }
        return AppResult.success(boards);
    }

    @GetMapping("/getById")
    public AppResult<Board> getById(@RequestParam("id") @NonNull Long id) {
        // 获取board
        Board board = boardService.selectById(id);
        // 校验
        if (board == null || board.getDeleteState() == 1) {
            log.warn(ResultCode.FAILED_BOARD_NOT_EXIST.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_BOARD_NOT_EXIST));
        }

        // 返回结果
        return AppResult.success(board);
    }
}
