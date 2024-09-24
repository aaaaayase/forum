package com.yun.forum.services.impl;

import com.yun.forum.common.AppResult;
import com.yun.forum.common.ResultCode;
import com.yun.forum.dao.MessageMapper;
import com.yun.forum.exception.ApplicationException;
import com.yun.forum.model.Message;
import com.yun.forum.model.User;
import com.yun.forum.services.IMessageService;
import com.yun.forum.services.IUserService;
import com.yun.forum.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author yun
 * @date 2024/9/23 20:35
 * @desciption: 站内信业务逻辑实现
 */
@Service
@Slf4j
public class IMessageServiceImpl implements IMessageService {

    @Autowired
    private IUserService userService;

    @Autowired
    private MessageMapper messageMapper;

    @Override
    public void create(Message message) {
        // 参数校验
        if (message == null
                || message.getPostUserId() == null
                || message.getReceiveUserId() == null
                || StringUtils.isEmpty(message.getContent())) {
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }

        // 设置默认值
        message.setDeleteState((byte) 0);
        message.setState((byte) 0);
        Date date = new Date();
        message.setCreateTime(date);
        message.setUpdateTime(date);


        // 查找要发送的对象 校验是否存在
        User user = userService.selectById(message.getReceiveUserId());
        if (user == null || user.getState() == 1 || user.getState() == 1) {
            log.warn(ResultCode.FAILED_USER_NOT_EXISTS.toString());
            throw new ApplicationException(AppResult.failed());
        }

        // 调用DAO 插入站内信
        int row = messageMapper.insertSelective(message);
        if (row != 1) {
            log.warn(ResultCode.FAILED_CREATE.toString());
            throw new ApplicationException(ResultCode.FAILED_CREATE.toString());
        }

    }

    @Override
    public Integer selectUnreadCount(Long receiveUserId) {
        // 非空校验
        if (receiveUserId == null || receiveUserId <= 0) {
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }

        // 调用DAO
        Integer count = messageMapper.selectUnreadCount(receiveUserId);

        if (count == null) {
            log.warn(ResultCode.ERROR_SERVICES.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.ERROR_SERVICES));
        }

        return count;
    }

    @Override
    public List<Message> selectByReceiveUserId(Long receiveUserId) {

        // 非空校验
        if (receiveUserId == null || receiveUserId <= 0) {
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }

        // 调用DAO
        List<Message> messages = messageMapper.selectByReceiveUserId(receiveUserId);

        return messages;

    }

    @Override
    public void updateStateById(Long id, Byte state) {

        // 参数校验
        if (id <= 0 || id == null || state < 0 || state > 2) {
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }

        // 构造用于更新的对象
        Message message = new Message();
        message.setId(id);
        message.setState(state);
        Date date = new Date();
        message.setUpdateTime(date);

        // 调用DAO
        int row = messageMapper.updateByPrimaryKeySelective(message);
        if (row != 1) {
            log.warn(ResultCode.ERROR_SERVICES.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.ERROR_SERVICES));
        }

    }

    @Override
    public Message selectById(Long id) {
        // 参数校验
        if (id == null || id <= 0) {
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }

        // 调用DAO
        Message message = messageMapper.selectByPrimaryKey(id);

        return message;
    }

    @Override
    public void reply(Long repliedId, Message message) {

        // 参数校验
        if (repliedId == null || repliedId <= 0) {
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }

        // 查找指定的站内信是否存在
        Message messageExist = messageMapper.selectByPrimaryKey(repliedId);
        if (messageExist == null || messageExist.getDeleteState() == 1) {
            log.warn(ResultCode.FAILED_MESSAGE_NOT_EXIST.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_MESSAGE_NOT_EXIST));
        }

        // 更新回复的站内信状态
        updateStateById(repliedId, (byte) 2);

        // 将回复的message加入t_message表
        create(message);
    }
}
