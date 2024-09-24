package com.yun.forum.controller;

import com.yun.forum.common.AppResult;
import com.yun.forum.common.ResultCode;
import com.yun.forum.config.AppConfig;
import com.yun.forum.model.User;
import com.yun.forum.services.IUserService;
import com.yun.forum.utils.MD5Utils;
import com.yun.forum.utils.StringUtils;
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

    /**
     * 登录用户更新个人中心的信息
     *
     * @param request
     * @param username
     * @param nickname
     * @param gender
     * @param email
     * @param phoneNum
     * @param remark
     * @return
     */
    @PostMapping("/modifyInfo")
    public AppResult modifyInfo(HttpServletRequest request
            , @RequestParam(value = "username", required = false) String username
            , @RequestParam(value = "nickname", required = false) String nickname
            , @RequestParam(value = "gender", required = false) Byte gender
            , @RequestParam(value = "email", required = false) String email
            , @RequestParam(value = "phoneNum", required = false) String phoneNum
            , @RequestParam(value = "remark", required = false) String remark) {
        // 非空校验
        if (StringUtils.isEmpty(username)
                && StringUtils.isEmpty(nickname)
                && StringUtils.isEmpty(phoneNum)
                && StringUtils.isEmpty(remark)
                && StringUtils.isEmpty(email)
                && gender == null) {
            return AppResult.failed("请输入要修改的内容");
        }

        // 获取session
        HttpSession session = request.getSession(false);
        // 获取登录用户
        User user = (User) session.getAttribute(AppConfig.USER_SESSION);

        // 设置用于更新的对象
        User userUpdate = new User();
        userUpdate.setId(user.getId());
        userUpdate.setUsername(username);
        userUpdate.setNickname(nickname);
        userUpdate.setGender(gender);
        userUpdate.setEmail(email);
        userUpdate.setPhoneNum(phoneNum);
        userUpdate.setRemark(remark);

        // 调用service
        userService.modifyInfo(userUpdate);

        // 查询最新的用户信息
        user = userService.selectById(user.getId());

        // 把最新的用户信息放入session
        session.setAttribute(AppConfig.USER_SESSION, user);

        return AppResult.success(user);
    }

    @PostMapping("/modifyPwd")
    public AppResult modifyPassword(HttpServletRequest request
            , @RequestParam("oldPassword") String oldPassword
            , @RequestParam("newPassword") String newPassword
            , @RequestParam("passwordRepeat") String passwordRepeat) {
        // 为什么这里没有进行参数的非空校验 因为在service层会进行校验
        // 首先验证
        if (!newPassword.equals(passwordRepeat)) {
            return AppResult.failed(ResultCode.FAILED_TWO_PWD_NOT_SAME);
        }

        // 获取登录的对象
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute(AppConfig.USER_SESSION);

        userService.modifyPassword(user.getId(), oldPassword, newPassword);

        // 销毁session
        if (session != null) {
            session.invalidate();
        }

        // 返回结果
        return AppResult.success();
    }
}
