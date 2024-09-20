package com.yun.forum;

import com.yun.forum.dao.UserMapper;
import com.yun.forum.model.User;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@SpringBootTest
class ForumApplicationTests {
    @Resource
    DataSource dataSource;
    @Autowired
    UserMapper userMapper;
    @Test
    void testConnection() throws SQLException {
        System.out.println("datasourc="+dataSource.getClass());
        Connection connection=dataSource.getConnection();
        System.out.println("connection="+connection);
        connection.close();
    }

    @Test
    void contextLoads() {
    }

    @Test
    @Transactional
    void testMybatis() {
        User user=new User();
        user.setId(1l);
        user.setNickname("冷酷王者");
        userMapper.updateByPrimaryKeySelective(user);
    }

}
