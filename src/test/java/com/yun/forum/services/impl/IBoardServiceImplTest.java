package com.yun.forum.services.impl;

import com.yun.forum.dao.BoardMapper;
import com.yun.forum.services.IBoardService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author yun
 * @date 2024/9/16 21:29
 * @desciption:
 */
@SpringBootTest
class IBoardServiceImplTest {

    @Autowired
    BoardMapper mapper;

    @Autowired
    IBoardService boardService;
    @Test
    void selectByNum() {

        mapper.selectByNum(2).forEach(System.out::println);
    }

    @Test
    @Transactional
    void addOneArticleCountById() {
        boardService.addOneArticleCountById(1l);
    }
}