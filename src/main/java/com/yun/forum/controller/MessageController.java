package com.yun.forum.controller;

import com.yun.forum.common.AppResult;
import com.yun.forum.common.ResultCode;
import com.yun.forum.config.AppConfig;
import com.yun.forum.model.Message;
import com.yun.forum.model.User;
import com.yun.forum.services.IMessageService;
import com.yun.forum.services.IUserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yun
 * @date 2024/9/23 21:19
 * @desciption: 站内信api接口
 */
@Slf4j
@RestController
@RequestMapping("/message")
public class MessageController {

    @Autowired
    IUserService userService;

    @Autowired
    IMessageService messageService;

    @PostMapping("/send")
    public AppResult create(HttpServletRequest request
            , @RequestParam("receiveUserId") @NonNull Long receiveUserId
            , @RequestParam("content") @NonNull String content) {
        // 获取登录用户
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute(AppConfig.USER_SESSION);

        // 判断登录用户状态是否正常
        if (user.getState() == 1) {
            return AppResult.failed(ResultCode.FAILED_USER_BANNED);
        }

        // 站内信不能自己发给自己
        if (receiveUserId == user.getId()) {
            return AppResult.failed("站内信不能自己发给自己");
        }

        // 获取接受用户并判断是否存在
        User userReceive = userService.selectById(receiveUserId);
        if (userReceive == null || userReceive.getDeleteState() == 1) {
            return AppResult.failed("接收者状态异常");
        }

        Message message = new Message();
        message.setPostUserId(user.getId());
        message.setReceiveUserId(receiveUserId);
        message.setContent(content);

        // 调用service插入记录
        messageService.create(message);

        // 返回结果
        return AppResult.success("发送成功");
    }

}
