package com.yun.forum.utils;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * @author yun
 * @date 2024/9/14 13:57
 * @desciption: MD5加密
 */
public class MD5Utils {

    /**
     * MD5加密字符串
     *
     * @param str
     * @return
     */
    public static String md5(String str) {
        return DigestUtils.md5Hex(str);
    }

    /**
     * 对用户密码进行加密
     *
     * @param str  明文
     * @param salt 盐值
     * @return
     */
    public static String md5Salt(String str, String salt) {
        return DigestUtils.md5Hex(DigestUtils.md5Hex(str) + salt);
    }
}
