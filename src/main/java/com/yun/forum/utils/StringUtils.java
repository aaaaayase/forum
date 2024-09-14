package com.yun.forum.utils;

/**
 * @author yun
 * @date 2024/9/14 14:08
 * @desciption: 字符串工具类
 */
public class StringUtils {

    /**
     * 字符串是否为空
     *
     * @param value 待验证的字符串
     * @return
     */
    public static boolean isEmpty(String value) {
        return value == null || value.length() == 0;
    }
}
