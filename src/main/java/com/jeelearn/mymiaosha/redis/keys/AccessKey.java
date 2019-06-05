package com.jeelearn.mymiaosha.redis.keys;

import com.jeelearn.mymiaosha.access.AccessLimit;

/**
 * @Description:
 * @Auther: lyd
 * @Date: 2019/6/5
 * @Version:v1.0
 */
public class AccessKey extends BasePrefix{

    public AccessKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public static AccessKey withExpire(int expireSeconds){
        return new AccessKey(expireSeconds, "access");
    }
}

