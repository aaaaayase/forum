package com.yun.forum.services;

import com.yun.forum.model.User;

/**
 * @author yun
 * @date 2024/9/14 20:37
 * @desciption: 用户业务逻辑接口
 */
public interface IUserService {
    /**
     * 创建一个普通用户
     *
     * @param user
     */
    void createNormalUser(User user);

    /**
     * 按用户名查找用户
     *
     * @param username
     * @return
     */
    User selectByUserName(String username);

    /**
     * 登录
     *
     * @param username
     * @param password
     * @return
     */
    User login(String username, String password);

    /**
     * 根据id来查询相应的对象
     *
     * @param id
     * @return
     */
    User selectById(Long id);

    /**
     * 用户帖子数量 +1
     *
     * @param id
     * @return
     */
    void addOneArticleCountById(Long id);

    /**
     * 用户帖子数量 -1
     *
     * @param id
     * @return
     */
    void subOneArticleCountById(Long id);
}
