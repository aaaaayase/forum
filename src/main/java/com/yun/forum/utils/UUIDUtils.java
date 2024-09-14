package com.yun.forum.utils;

import java.util.UUID;

/**
 * @author yun
 * @date 2024/9/14 14:04
 * @desciption: 随机字符串生成
 */
public class UUIDUtils {

    /**
     * 生成一个标准的UUID
     *
     * @return
     */
    public static String UUID_36() {
        return UUID.randomUUID().toString();
    }

    /**
     * 生成一个32位的UUID
     *
     * @return 原UUID36位去掉4位-得到32位
     */
    public static String UUID_32() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
