package com.jeelearn.mymiaosha.controller;

import com.jeelearn.mymiaosha.access.AccessLimit;
import com.jeelearn.mymiaosha.rabbitmq.MiaoshaMQSender;
import com.jeelearn.mymiaosha.rabbitmq.MiaoshaMessage;
import com.jeelearn.mymiaosha.rabbitmq.basedemo.MQSender;
import com.jeelearn.mymiaosha.redis.RedisService;
import com.jeelearn.mymiaosha.redis.keys.GoodsKey;
import com.jeelearn.mymiaosha.redis.keys.MiaoshaKey;
import com.jeelearn.mymiaosha.redis.keys.OrderKey;
import com.jeelearn.mymiaosha.result.Result;
import com.jeelearn.mymiaosha.service.VerifyCodeService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.jeelearn.mymiaosha.domain.MiaoshaOrder;
import com.jeelearn.mymiaosha.domain.MiaoshaUser;
import com.jeelearn.mymiaosha.domain.OrderInfo;
import com.jeelearn.mymiaosha.result.CodeMsg;
import com.jeelearn.mymiaosha.service.GoodsService;
import com.jeelearn.mymiaosha.service.MiaoshaService;
import com.jeelearn.mymiaosha.service.OrderService;
import com.jeelearn.mymiaosha.vo.GoodsVo;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;


@Controller
@RequestMapping("/miaosha")
public class MiaoshaController implements InitializingBean{
	
	@Autowired
	GoodsService goodsService;
	
	@Autowired
	OrderService orderService;
	@Autowired
	MiaoshaService miaoshaService;
	@Autowired
	VerifyCodeService verifyCodeService;

	@Autowired
	RedisService redisService;
	@Autowired
	MiaoshaMQSender sender;

	private HashMap<Long, Boolean> localOverMap =  new HashMap<Long, Boolean>();

	/**
	 * 系统初始化
	 * */
	@Override
	public void afterPropertiesSet() throws Exception {
		List<GoodsVo> goodsList = goodsService.listGoodsVo();
		if(goodsList == null) {
			return;
		}
		for(GoodsVo goods : goodsList) {
			redisService.set(GoodsKey.getMiaoshaGoodsStock, ""+goods.getId(), goods.getStockCount());
			localOverMap.put(goods.getId(), false);
		}
	}

	/**
	 * orderId：成功
	 * -1：秒杀失败
	 * 0： 排队中
	 * */
	@GetMapping(value="/result")
	@ResponseBody
	public Result<Long> miaoshaResult(Model model,MiaoshaUser user, @RequestParam("goodsId")long goodsId) {
		model.addAttribute("user", user);
		if(user == null) {
			return Result.error(CodeMsg.SESSION_ERROR);
		}
		long result  = miaoshaService.getMiaoshaResult(user.getId(), goodsId);
		return Result.success(result);
	}

	/**
	 * 获取秒杀地址的动态Variable
	 * @param request
	 * @param user
	 * @param goodsId
	 * @param verifyCode
	 * @return
	 */
	@AccessLimit(seconds=5, maxCount=5, needLogin=true)
	@GetMapping(value="/path")
	@ResponseBody
	public Result<String> getMiaoshaPath(HttpServletRequest request, MiaoshaUser user,
										 @RequestParam("goodsId")long goodsId,
										 @RequestParam(value="verifyCode", defaultValue="0")int verifyCode) {
		if(user == null) {
			return Result.error(CodeMsg.SESSION_ERROR);
		}
		boolean check = verifyCodeService.checkVerifyCode(user, goodsId, verifyCode);
		if(!check) {
			return Result.error(CodeMsg.REQUEST_ILLEGAL);
		}
		String path  =miaoshaService.createMiaoshaPath(user, goodsId);
		return Result.success(path);
	}

	/**
	 * QPS:1306
	 * 5000 * 10
	 * QPS: 2114
	 *
	/**
	 *  GET POST有什么区别？  GET幂等
	 * */
	@PostMapping("/{path}/do_miaosha")
	@ResponseBody
	public Result<Integer> miaosha(Model model, MiaoshaUser user,
								   @RequestParam("goodsId")long goodsId,
								   @PathVariable("path") String path) {
		if(user == null) {
			return Result.error(CodeMsg.SESSION_ERROR);
		}
		//验证path
		boolean check = miaoshaService.checkPath(user, goodsId, path);
		if(!check){
			return Result.error(CodeMsg.REQUEST_ILLEGAL);
		}
		//内存标记，减少redis访问
		boolean over = localOverMap.get(goodsId);
		if(over) {
			return Result.error(CodeMsg.MIAO_SHA_OVER);
		}
		//预减库存
		long stock = redisService.decr(GoodsKey.getMiaoshaGoodsStock, ""+goodsId);//10
		if(stock < 0) {
			localOverMap.put(goodsId, true);
			return Result.error(CodeMsg.MIAO_SHA_OVER);
		}
		//判断是否已经秒杀到了
		MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(), goodsId);
		if(order != null) {
			return Result.error(CodeMsg.REPEATE_MIAOSHA);
		}
		//入队
		MiaoshaMessage mm = new MiaoshaMessage();
		mm.setUser(user);
		mm.setGoodsId(goodsId);
		sender.sendMiaoshaMessage(mm);
		return Result.success(0);//排队中
		/*
		//1.判断库存
		GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
		access count = goods.getGoodsStock();
		if(count <= 0){
			return Result.error(CodeMsg.MIAO_SHA_OVER);
		}
		//2.是否重复秒杀
		MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(), goodsId);
		if(order != null) {
			return Result.error(CodeMsg.REPEATE_MIAOSHA);
		}
		//3.减库存/下订单/写入秒杀订单
		OrderInfo orderInfo = miaoshaService.miaosha(user, goods);
		return Result.success(orderInfo);
		*/
	}

	/**
	 * 测试前重置系统数据（仅为测试使用）
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/reset", method=RequestMethod.GET)
	@ResponseBody
	public Result<Boolean> reset(Model model) {
		List<GoodsVo> goodsList = goodsService.listGoodsVo();
		for(GoodsVo goods : goodsList) {
			goods.setStockCount(10);
			redisService.set(GoodsKey.getMiaoshaGoodsStock, ""+goods.getId(), 10);
			localOverMap.put(goods.getId(), false);
		}
		redisService.delete(OrderKey.getMiaoshaOrderByUidGid);
		redisService.delete(MiaoshaKey.isGoodsOver);
		miaoshaService.reset(goodsList);
		return Result.success(true);
	}







	@RequestMapping("/do_miaosha2")
    public String miaosha2(Model model,MiaoshaUser user, @RequestParam("goodsId")long goodsId) {
    	model.addAttribute("user", user);
    	if(user == null) {
    		return "login";
    	}
    	
    	//1.判断库存
    	GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
    	int count = goods.getGoodsStock();
    	if(count <= 0){
    		model.addAttribute("errmsg", CodeMsg.MIAO_SHA_OVER.getMsg());
    		return "miaosha_fail";
    	}
    	//2.是否重复秒杀
    	MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(), goodsId);
    	if(order != null) {
    		model.addAttribute("errmsg", CodeMsg.REPEATE_MIAOSHA.getMsg());
    		return "miaosha_fail";
    	}
    	//3.减库存/下订单/写入秒杀订单
    	OrderInfo orderInfo = miaoshaService.miaosha(user, goods);
    	model.addAttribute("orderInfo", orderInfo);
    	model.addAttribute("goods", goods);
        return "order_detail";
	}

}