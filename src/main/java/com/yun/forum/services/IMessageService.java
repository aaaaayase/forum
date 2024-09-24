package com.yun.forum.services;

import com.yun.forum.model.Message;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    /**
     * 获取未读站内信数量
     *
     * @param receiveUserId
     * @return
     */
    Integer selectUnreadCount(Long receiveUserId);

    /**
     * 查找站内信列表
     *
     * @param receiveUserId
     * @return
     */
    List<Message> selectByReceiveUserId(Long receiveUserId);


    /**
     * 更新指定站内信状态
     *
     * @param id
     * @param state
     */
    void updateStateById(Long id, Byte state);

    /**
     * 根据id查找站内信
     *
     * @param id
     * @return
     */
    Message selectById(Long id);

    /**
     * 回复站内信
     *
     * @param repliedId 要回复的站内信id
     * @param message
     */
    @Transactional
    void reply(Long repliedId, Message message);
}
