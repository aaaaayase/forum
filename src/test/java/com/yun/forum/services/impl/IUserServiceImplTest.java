package com.yun.forum.services.impl;

import com.yun.forum.model.User;
import com.yun.forum.services.IUserService;
import com.yun.forum.utils.MD5Utils;
import com.yun.forum.utils.UUIDUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author yun
 * @date 2024/9/14 21:10
 * @desciption:
 */
@SpringBootTest
class IUserServiceImplTest {

    @Autowired
    IUserService userService;

    @Test
    @Transactional
    void createNormalUser() {
        User user = new User();
        user.setUsername("handsome_pig");
        user.setNickname("帅气骚猪");

        String password = "123456";
        String salt = UUIDUtils.UUID_32();
        String secret = MD5Utils.md5Salt(password, salt);
        user.setPassword(secret);
        user.setSalt(salt);

        userService.createNormalUser(user);
        System.out.println(user.getId());
    }

    @Test
    void selectByUserName() {

        User coldGoose = userService.selectByUserName("cold_goose");
        System.out.println(coldGoose);

    }

    @Test
    @Transactional
    void login() {

        User login = userService.login("cold_goose", "12345");
        System.out.println(login);

    }

    @Test
    @Transactional
    void addOneArticleCountById() {
        userService.addOneArticleCountById(2l);
    }

    @Test
    @Transactional
    void subOneArticleCountById() {

        userService.subOneArticleCountById(4l);
    }
}