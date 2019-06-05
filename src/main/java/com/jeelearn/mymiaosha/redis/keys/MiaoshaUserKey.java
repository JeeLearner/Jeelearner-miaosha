package com.jeelearn.mymiaosha.redis.keys;

public class MiaoshaUserKey extends BasePrefix{

	private static final int DEFAULT_EXPIRE = 3600 * 24 * 2;
	
	private MiaoshaUserKey(int expireSeconds, String prefix) {
		super(expireSeconds, prefix);
	}
	
	public static MiaoshaUserKey token = new MiaoshaUserKey(DEFAULT_EXPIRE, "tk");
	public static MiaoshaUserKey getById = new MiaoshaUserKey(0, "id");
}
