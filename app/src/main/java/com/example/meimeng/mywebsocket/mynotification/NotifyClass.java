package com.example.meimeng.mywebsocket.mynotification;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 作者：凌涛 on 2018/6/29 10:56
 * 邮箱：771548229@qq..com
 */
//标记注解
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NotifyClass {
    Class<?> value();
}
