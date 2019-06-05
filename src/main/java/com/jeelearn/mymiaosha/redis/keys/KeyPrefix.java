package com.jeelearn.mymiaosha.redis.keys;

public interface KeyPrefix {

	public int expireSeconds();
	public String getPrefix();
}
