package com.example.meimeng.util;

import java.util.Random;

/**
 * 作者：凌涛 on 2018/7/6 14:36
 * 邮箱：771548229@qq..com
 */
public class RandomUtil {

    private static final String[] chars = new String[]{"a"};
    public static final String SOURCES = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";


    public static String generateString(Random random, int length) {
        char[] text = new char[length];
        for (int i = 0; i < length; i++) {
            text[i] = SOURCES.charAt(random.nextInt(SOURCES.length()));
        }
        return new String(text);
    }


}
