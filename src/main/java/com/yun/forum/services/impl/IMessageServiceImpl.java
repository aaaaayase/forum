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


}
