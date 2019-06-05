package com.jeelearn.mymiaosha.service;

import com.jeelearn.mymiaosha.domain.MiaoshaOrder;
import com.jeelearn.mymiaosha.redis.RedisService;
import com.jeelearn.mymiaosha.redis.keys.MiaoshaKey;
import com.jeelearn.mymiaosha.utils.MD5Util;
import com.jeelearn.mymiaosha.utils.UUIDUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jeelearn.mymiaosha.domain.MiaoshaUser;
import com.jeelearn.mymiaosha.domain.OrderInfo;
import com.jeelearn.mymiaosha.vo.GoodsVo;

import java.util.List;

@SuppressWarnings("restriction")
@Service
public class MiaoshaService {

	@Autowired
	GoodsService goodsService;
	
	@Autowired
	OrderService orderService;

	@Autowired
	RedisService redisService;
	
	/**
	 * 1.减库存 
	 * 2.下订单 写入秒杀订单
	 * @param user
	 * @param goods
	 * @return
	 */
	public OrderInfo miaosha(MiaoshaUser user, GoodsVo goods) {
		boolean success = goodsService.reduceStock(goods);
		if (success){
			//order_info maiosha_order
			return orderService.createOrder(user, goods);
		} else {
			//减库存失败，说明卖完了
			setGoodsOver(goods.getId());
			return null;
		}
	}

	public long getMiaoshaResult(Long userId, long goodsId) {
		MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(userId, goodsId);
		if(order != null) {//秒杀成功
			return order.getOrderId();
		}else {
			boolean isOver = getGoodsOver(goodsId);
			if(isOver) {
				return -1; //秒杀失败（卖完了）
			}else {
				return 0; //处理中
			}
		}
	}

	private boolean getGoodsOver(long goodsId) {
		//只有在减库存失败（也就是卖完了）的时候才会设置这个属性，同时为true；其他情况这个值是不存在的
		return redisService.exists(MiaoshaKey.isGoodsOver, ""+goodsId);
	}

	private void setGoodsOver(Long goodsId) {
		redisService.set(MiaoshaKey.isGoodsOver, ""+goodsId, true);
	}

	public void reset(List<GoodsVo> goodsList) {
		goodsService.resetStock(goodsList);
		orderService.deleteOrders();
	}

    public String createMiaoshaPath(MiaoshaUser user, long goodsId) {
		if(user == null || goodsId <=0) {
			return null;
		}
		String str = MD5Util.md5(UUIDUtil.uuid() + "123456");
		redisService.set(MiaoshaKey.getMiaoshaPath, ""+user.getId()+"_"+goodsId, str);
		return str;
	}

	public boolean checkPath(MiaoshaUser user, long goodsId, String path) {
		if(user == null || path == null) {
			return false;
		}
		String pathOld = redisService.get(MiaoshaKey.getMiaoshaPath, ""+user.getId() + "_"+ goodsId, String.class);
		return path.equals(pathOld);
	}
}
