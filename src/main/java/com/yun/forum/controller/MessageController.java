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
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    /**
     * 获取未读站内信的数量
     *
     * @param request
     * @return
     */
    @GetMapping("/getUnreadCount")
    public AppResult getUnreadCount(HttpServletRequest request) {
        // 获取登录用户
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute(AppConfig.USER_SESSION);

        // 调用service
        Integer count = messageService.selectUnreadCount(user.getId());

        // 返回结果
        return AppResult.success(count);
    }

    @GetMapping("/getAll")
    public AppResult<List<Message>> getAll(HttpServletRequest request) {
        // 1. 获取当前登录的用户
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute(AppConfig.USER_SESSION);
        // 2. 调用Service
        List<Message> messages = messageService.selectByReceiveUserId(user.getId());
        // 3. 返回结果
        System.out.println(AppResult.success(messages));
        System.out.println(messages);
        return AppResult.success(messages);
    }

    @PostMapping("/markRead")
    public AppResult markRead(HttpServletRequest request, @RequestParam("id") @NonNull Long id) {

        // 根据站内信id 查找是否有该站内信
        Message message = messageService.selectById(id);
        if (message == null || message.getDeleteState() == 1) {
            return AppResult.failed(ResultCode.FAILED_MESSAGE_NOT_EXIST);
        }

        // 获取登录对象
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute(AppConfig.USER_SESSION);

        // 校验当前的登录用户和站内信的接收者是否是一个
        if (user.getId() != message.getReceiveUserId()) {
            return AppResult.failed(ResultCode.FAILED_FORBIDDEN);
        }

        // 调用service 设置站内信的状态为已读
        messageService.updateStateById(id, (byte) 1);

        return AppResult.success();
    }

    @PostMapping("/reply")
    public AppResult reply(HttpServletRequest request
            , @RequestParam("repliedId") @NonNull Long repliedId
            , @RequestParam("content") @NonNull String content) {
        // 获取登录对象
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute(AppConfig.USER_SESSION);
        // 校验当前登录用户对象的状态
        if (user.getState() == 1) {
            return AppResult.failed(ResultCode.FAILED_USER_BANNED);
        }

        // 获取指定id的要回复的站内信对象
        Message messageExist = messageService.selectById(repliedId);
        if (messageExist == null || messageExist.getDeleteState() == 1) {
            return AppResult.failed(ResultCode.FAILED_MESSAGE_NOT_EXIST);
        }

        // 需要校验不能自己给自己回站内信
        if (user.getId() == messageExist.getPostUserId()) {
            return AppResult.failed("不能给自己的站内信回复");
        }

        // 构造用于加入t_message表的message对象
        Message message = new Message();
        message.setPostUserId(user.getId());
        message.setContent(content);
        message.setReceiveUserId(messageExist.getPostUserId());

        // 调用service
        messageService.reply(repliedId, message);

        return AppResult.success();
    }
}
