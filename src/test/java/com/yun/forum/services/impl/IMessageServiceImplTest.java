package com.yun.forum.services.impl;

import com.yun.forum.model.Message;
import com.yun.forum.services.IMessageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author yun
 * @date 2024/9/23 20:49
 * @desciption:
 */
@SpringBootTest
class IMessageServiceImplTest {

    @Autowired
    private IMessageService messageService;

    @Test
    void create() {
        Message message = new Message();
        message.setPostUserId(4l);
        message.setReceiveUserId(10l);
        message.setContent("站内信测试");

        messageService.create(message);
    }
}