package com.yun.forum.services;

import com.yun.forum.model.Message;

/**
 * @author yun
 * @date 2024/9/23 20:34
 * @desciption: 站内信业务逻辑接口
 */
public interface IMessageService {

    /**
     * 发送站内信即创建站内信
     *
     * @param message
     */
    void create(Message message);
}
