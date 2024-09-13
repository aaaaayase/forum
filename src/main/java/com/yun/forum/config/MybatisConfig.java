package com.yun.forum.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author yun
 * @date 2024/9/13 11:11
 * @desciption: 配置Mybatis的扫描路径
 */
// 配置类
@Configuration
// Mybatis配置路径
@MapperScan("com.yun.forum.dao")
public class MybatisConfig {
}