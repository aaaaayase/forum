package com.yun.forum.services.impl;

import com.yun.forum.common.AppResult;
import com.yun.forum.common.ResultCode;
import com.yun.forum.dao.UserMapper;
import com.yun.forum.exception.ApplicationException;
import com.yun.forum.model.User;
import com.yun.forum.services.IUserService;
import com.yun.forum.utils.MD5Utils;
import com.yun.forum.utils.StringUtils;
import com.yun.forum.utils.UUIDUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author yun
 * @date 2024/9/14 20:38
 * @desciption: 用户业务逻辑实现
 */
@Slf4j
@Service
public class IUserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public void createNormalUser(User user) {
        // 检查参数是否有效
        // 非空判断
        if (user == null || StringUtils.isEmpty(user.getUsername())
                || StringUtils.isEmpty(user.getNickname()) || StringUtils.isEmpty(user.getPassword())
                || StringUtils.isEmpty(user.getSalt())) {
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }

        // 按用户名查找用户对象
        User existUser = userMapper.selectByUserName(user.getUsername());
        // 如果用户已经存在
        if (existUser != null) {
            log.info(ResultCode.FAILED_USER_EXISTS.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_USER_EXISTS));
        }

        // 新增用户 设置默认值
        user.setGender((byte) 2);
        user.setArticleCount(0);
        user.setIsAdmin((byte) 0);
        user.setState((byte) 0);
        user.setDeleteState((byte) 0);
        // 当前日期
        Date date = new Date();
        user.setCreateTime(date);
        user.setUpdateTime(date);

        int row = userMapper.insertSelective(user);
        if (row != 1) {
            log.info(ResultCode.FAILED_CREATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_CREATE));
        }

        // 打印日志
        log.info("新用户成功，username = " + user.getUsername() + "。");

    }

    @Override
    public User selectByUserName(String username) {
        // 非空校验
        if (StringUtils.isEmpty(username)) {
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }

        return userMapper.selectByUserName(username);
    }

    @Override
    public User login(String username, String password) {
        // 非空校验
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }

        // 查询用户信息
        User user = selectByUserName(username);
        // 检验用户是否存在
        if (user == null) {
            log.warn(ResultCode.FAILED_LOGIN.toString() + "username=" + username);
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_LOGIN));
        }
        // 核对密码
        String salt = user.getSalt();
        String secret = MD5Utils.md5Salt(password, salt);
        if (!secret.equals(user.getPassword())) {
            log.warn(ResultCode.FAILED_LOGIN.toString() + "密码错误，username=" + username);
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_LOGIN));
        }

        // 核对成功
        log.info(username + " 登录成功");
        // 返回用户信息
        return user;
    }

    @Override
    public User selectById(Long id) {
        // 非空校验
        if (id == null) {
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }

        return userMapper.selectByPrimaryKey(id);
    }

    @Override
    public void addOneArticleCountById(Long id) {
        // 非空校验
        if (id == null || id <= 0) {
            log.warn(ResultCode.FAILED_USER_ARTICLE_COUNT.toString() + "id不合法");
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_USER_ARTICLE_COUNT));
        }

        // 查询用户
        User user = userMapper.selectByPrimaryKey(id);
        if (user == null) {
            log.warn(ResultCode.ERROR_IS_NULL.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.ERROR_IS_NULL));
        }

        // 新建用户用来更新
        User userUpdate = new User();
        userUpdate.setArticleCount(user.getArticleCount() + 1);
        userUpdate.setId(user.getId());

        // 校验更新是否成功
        int row = userMapper.updateByPrimaryKeySelective(userUpdate);
        if (row != 1) {
            log.warn(ResultCode.FAILED.toString() + "受影响的行数不为1");
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED));
        }
    }

    @Override
    public void subOneArticleCountById(Long id) {
        // 非空校验
        if (id == null || id <= 0) {
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }

        // 校验用户是否存在
        User user = userMapper.selectByPrimaryKey(id);
        if (user == null) {
            log.warn(ResultCode.FAILED_USER_NOT_EXISTS.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_USER_NOT_EXISTS));
        }

        // 构造用于更新的对象
        User userUpdate = new User();
        userUpdate.setId(user.getId());
        userUpdate.setArticleCount(user.getArticleCount() - 1);
        if (userUpdate.getArticleCount() < 0) {
            userUpdate.setArticleCount(0);
        }

        // 调用DAO更新
        int row = userMapper.updateByPrimaryKeySelective(userUpdate);
        if (row != 1) {
            log.warn(ResultCode.FAILED.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED));
        }

    }

    @Override
    public void modifyInfo(User user) {
        // 非空校验
        if (user == null || user.getId() <= 0 || user.getId() == null) {
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }

        // 根据id查找是否有这样的用户
        User userExist = userMapper.selectByPrimaryKey(user.getId());
        // 校验
        if (userExist == null) {
            log.warn(ResultCode.FAILED_USER_NOT_EXISTS.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_USER_NOT_EXISTS));
        }

        // 设置用于更新的新对象
        User userUpdate = new User();
        userUpdate.setId(user.getId());

        // 设置标志位 标记用于更新的对象是否被设置新值 也就是说用户信息是否被更新
        boolean flg = false;

        // 校验 改变的用户名不能为空 并且必须和之前的用户名不一样 以及不能和数据库中的已有的用户名重名
        if (!StringUtils.isEmpty(user.getUsername())
                && !user.getUsername().equals(userExist.getUsername())) {
            User userCheck = userMapper.selectByUserName(user.getUsername());
            if (userCheck != null) {
                log.warn(ResultCode.FAILED_USER_EXISTS.toString());
                throw new ApplicationException(AppResult.failed(ResultCode.FAILED_USER_EXISTS));
            }
            userUpdate.setUsername(user.getUsername());
            flg = true;
        }

        if (!StringUtils.isEmpty(user.getNickname())
                && !user.getNickname().equals(userExist.getNickname())) {
            userUpdate.setNickname(user.getNickname());
            flg = true;
        }

        if (user.getGender() != null && user.getGender() != userExist.getGender()) {
            userUpdate.setGender(user.getGender());
            if (userUpdate.getGender() > 2 || userUpdate.getGender() < 0) {
                userUpdate.setGender((byte) 2);
            }
            flg = true;
        }

        if (!StringUtils.isEmpty(user.getEmail())
                && !user.getEmail().equals(userExist.getEmail())) {
            userUpdate.setEmail(user.getEmail());
            flg = true;
        }

        if (!StringUtils.isEmpty(user.getPhoneNum())
                && !user.getPhoneNum().equals(userExist.getPhoneNum())) {
            userUpdate.setPhoneNum(user.getPhoneNum());
            flg = true;
        }

        if (!StringUtils.isEmpty(user.getRemark())
                && !user.getRemark().equals(userExist.getRemark())) {
            userUpdate.setRemark(user.getRemark());
            flg = true;
        }

        if (flg == false) {
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }

        int row = userMapper.updateByPrimaryKeySelective(userUpdate);
        if (row != 1) {
            log.warn(ResultCode.FAILED.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED));
        }


    }

    @Override
    public void modifyPassword(Long id, String oldPassword, String newPassword) {
        // 非空校验
        if (id == null || id <= 0 || StringUtils.isEmpty(oldPassword) || StringUtils.isEmpty(newPassword)) {
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }

        User user = userMapper.selectByPrimaryKey(id);
        // 校验用户是否存在
        if (user == null || user.getDeleteState() == 1) {
            log.warn(ResultCode.FAILED_USER_NOT_EXISTS.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_USER_NOT_EXISTS));
        }

        // 验证输入的密码是否正确
        String oldSecret = MD5Utils.md5Salt(oldPassword, user.getSalt());
        if (!oldSecret.equalsIgnoreCase(user.getPassword())) {
            log.warn(ResultCode.FAILED.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED));
        }


        // 构造用于更新的用户对象
        User userUpdate = new User();
        userUpdate.setId(user.getId());
        // 获取新的盐值
        String salt = UUIDUtils.UUID_32();
        String newSecret = MD5Utils.md5Salt(newPassword, salt);
        userUpdate.setPassword(newSecret);
        userUpdate.setSalt(salt);
        Date date = new Date();
        userUpdate.setUpdateTime(date);

        // 调用DAO
        int row = userMapper.updateByPrimaryKeySelective(userUpdate);
        // 校验
        if (row != 1) {
            log.warn(ResultCode.FAILED.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED));
        }

    }
}
