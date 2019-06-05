package com.jeelearn.mymiaosha.controller;

import com.jeelearn.mymiaosha.domain.MiaoshaUser;
import com.jeelearn.mymiaosha.domain.OrderInfo;
import com.jeelearn.mymiaosha.redis.RedisService;
import com.jeelearn.mymiaosha.result.CodeMsg;
import com.jeelearn.mymiaosha.result.Result;
import com.jeelearn.mymiaosha.service.GoodsService;
import com.jeelearn.mymiaosha.service.MiaoshaUserService;
import com.jeelearn.mymiaosha.service.OrderService;
import com.jeelearn.mymiaosha.vo.GoodsVo;
import com.jeelearn.mymiaosha.vo.OrderDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/order")
public class OrderController {

	@Autowired
	MiaoshaUserService userService;
	
	@Autowired
	RedisService redisService;
	
	@Autowired
	OrderService orderService;
	
	@Autowired
	GoodsService goodsService;
	
    @RequestMapping("/detail")
    @ResponseBody
    public Result<OrderDetailVo> info(Model model, MiaoshaUser user,
									  @RequestParam("orderId") long orderId) {
    	if(user == null) {
    		return Result.error(CodeMsg.SESSION_ERROR);
    	}
    	OrderInfo order = orderService.getOrderById(orderId);
    	if(order == null) {
    		return Result.error(CodeMsg.ORDER_NOT_EXIST);
    	}
    	long goodsId = order.getGoodsId();
    	GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
    	OrderDetailVo vo = new OrderDetailVo();
    	vo.setOrder(order);
    	vo.setGoods(goods);
    	return Result.success(vo);
    }
    
}