package com.easychat.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface GlobalInterceptor {

    boolean checkLogin() default true;//进行登录校验，大部分接口肯定是需要登录才可以使用，少部分只需要自己修改

    boolean checkAdmin() default false;//大部分不是管理员


}
