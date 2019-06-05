package com.jeelearn.mymiaosha.redis.keys;

public class UserKey extends BasePrefix{

	public UserKey(String prefix) {
		super(prefix);
	}

	public static UserKey getById = new UserKey("id");
	public static UserKey getByName = new UserKey("name");
	
}
