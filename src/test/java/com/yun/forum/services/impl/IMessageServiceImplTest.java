package com.yun.forum.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yun.forum.model.Message;
import com.yun.forum.services.IMessageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author yun
 * @date 2024/9/23 20:49
 * @desciption:
 */
@SpringBootTest
class IMessageServiceImplTest {

    @Autowired
    private IMessageService messageService;

    @Autowired
    private ObjectMapper objectMapper;

    @Transactional
    @Test
    void create() {
        Message message = new Message();
        message.setPostUserId(4l);
        message.setReceiveUserId(10l);
        message.setContent("站内信测试");

        messageService.create(message);
    }

    @Test
    void selectUnreadCount() {
        Integer count = messageService.selectUnreadCount(2l);
        System.out.println(count);
        count = messageService.selectUnreadCount(5l);
        System.out.println(count);
    }

    @Test
    void selectByReceiveUserId() throws JsonProcessingException {
        List<Message> l1 = messageService.selectByReceiveUserId(5l);
        System.out.println(objectMapper.writeValueAsString(l1));
        List<Message> l2 = messageService.selectByReceiveUserId(6l);
        System.out.println(objectMapper.writeValueAsString(l2));
    }

    @Test
    @Transactional
    void updateStateById() {


        messageService.updateStateById(2L, (byte) 1);


    }
}