package com.jeelearn.mymiaosha.redis.keys;

public class OrderKey extends BasePrefix {

	public OrderKey(String prefix) {
        super(prefix);
	}

	public static OrderKey getMiaoshaOrderByUidGid = new OrderKey("moug");
}
