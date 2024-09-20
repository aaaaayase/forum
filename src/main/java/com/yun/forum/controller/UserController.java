package com.yun.forum.controller;

import com.yun.forum.common.AppResult;
import com.yun.forum.common.ResultCode;
import com.yun.forum.config.AppConfig;
import com.yun.forum.model.User;
import com.yun.forum.services.IUserService;
import com.yun.forum.utils.MD5Utils;
import com.yun.forum.utils.UUIDUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author yun
 * @date 2024/9/14 22:22
 * @desciption: 用户api接口
 */
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private IUserService userService;

    /**
     * 用户注册
     *
     * @param username
     * @param nickname
     * @param password
     * @param passwordRepeat
     * @return
     */
    @PostMapping("/register")
    public AppResult registry(@Param("username") @NonNull String username,
                              @Param("nickname") @NonNull String nickname,
                              @Param("password") @NonNull String password,
                              @Param("passwordRepeat") @NonNull String passwordRepeat) {
        if (!passwordRepeat.equals(password)) {
            log.warn(ResultCode.FAILED_TWO_PWD_NOT_SAME.toString());
            return AppResult.failed(ResultCode.FAILED_TWO_PWD_NOT_SAME);
        }

        // 数据准备
        User user = new User();
        user.setNickname(nickname);
        user.setUsername(username);
        // 密码加密
        String salt = UUIDUtils.UUID_32();
        user.setSalt(salt);
        String secret = MD5Utils.md5Salt(password, salt);
        user.setPassword(secret);

        userService.createNormalUser(user);
        return AppResult.success();
    }

    /**
     * 用户登录
     *
     * @param request
     * @param username
     * @param password
     * @return
     */
    @PostMapping("/login")
    public AppResult login(HttpServletRequest request,
                           @RequestParam("username") @NonNull String username,
                           @RequestParam("password") @NonNull String password) {
        User user = userService.login(username, password);
        // 这里user无需非空校验 因为service那里已经做过了
        // 登录成功 设置session
        HttpSession session = request.getSession(true);
        session.setAttribute(AppConfig.USER_SESSION, user);

        // 返回结果
        return AppResult.success();
    }

    /**
     * 获取用户信息
     *
     * @param request
     * @param id
     * @return
     */
    @GetMapping("/info")
    public AppResult<User> getUserInfo(HttpServletRequest request, @RequestParam(value = "id", required = false) Long id) {
        User user = null;
        // 判断id是否为空
        if (id == null) {
            // 如果id为null 那么从session中获取用户信息
            HttpSession session = request.getSession(false);
            // 判断是否已经登录
            if (session == null || session.getAttribute(AppConfig.USER_SESSION) == null) {
                // 用户没有登录 那么直接返回错误
                return AppResult.failed(ResultCode.FAILED_FORBIDDEN);
            }
            user = (User) session.getAttribute(AppConfig.USER_SESSION);
        } else {
            // id不为空 直接找用户信息
            user = userService.selectById(id);
        }
        if (user == null) {
            return AppResult.failed(ResultCode.FAILED_USER_NOT_EXISTS);
        }
        return AppResult.success(user);
    }

    /**
     * 退出登录
     *
     * @param request
     * @return
     */
    @GetMapping("/logout")
    public static AppResult logout(HttpServletRequest request) {
        // 获取session
        HttpSession session = request.getSession(false);
        if (session != null) {
            log.info("退出成功");
            // 销毁session
            session.invalidate();
        }

        // 无论是否有session都返回成功
        return AppResult.success("退出成功");
    }
}