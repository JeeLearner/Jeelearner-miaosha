package com.jeelearn.mymiaosha.access;

import com.jeelearn.mymiaosha.domain.MiaoshaUser;

/**
 * @Description:
 * @Auther: lyd
 * @Date: 2019/6/5
 * @Version:v1.0
 */
public class UserContext {

    /**
     * ThreadLocal当前线程共享资源（一个用户一个线程）
     */
    private static ThreadLocal<MiaoshaUser> userHolder = new ThreadLocal<MiaoshaUser>();

    public static MiaoshaUser getUser(){
        return userHolder.get();
    }

    public static void setUser(MiaoshaUser user){
        userHolder.set(user);
    }
}

