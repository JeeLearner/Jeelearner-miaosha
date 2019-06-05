package com.jeelearn.mymiaosha.controller;

import com.jeelearn.mymiaosha.rabbitmq.basedemo.MQSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jeelearn.mymiaosha.redis.RedisService;
import com.jeelearn.mymiaosha.redis.keys.UserKey;
import com.jeelearn.mymiaosha.domain.User;
import com.jeelearn.mymiaosha.result.CodeMsg;
import com.jeelearn.mymiaosha.result.Result;
import com.jeelearn.mymiaosha.service.UserService;

@Controller
@RequestMapping("/demo")
public class DemoController {


	/*@Autowired
	MQSender sender;

	@RequestMapping("/mq")
    @ResponseBody
    public Result<String> mq() {
		sender.send("hello,imooc");
        return Result.success("Hello，world");
    }

	@RequestMapping("/mq/topic")
    @ResponseBody
    public Result<String> topic() {
		sender.sendTopic("hello,imooc");
        return Result.success("Hello，world");
    }

	@RequestMapping("/mq/fanout")
    @ResponseBody
    public Result<String> fanout() {
		sender.sendFanout("hello,imooc");
        return Result.success("Hello，world");
    }

	@RequestMapping("/mq/header")
    @ResponseBody
    public Result<String> header() {
		sender.sendHeader("hello,imooc");
        return Result.success("Hello，world");
    }*/




	@Autowired
	private UserService userService;
	@Autowired
	private RedisService redisService;
	
	@GetMapping("/")
	@ResponseBody
    String home() {
        return "Hello World!";
    }
	
	@GetMapping("/hello")
	@ResponseBody
	public Result<String> hello(){
		return Result.success("hello");
	}

	@GetMapping("/helloError")
	@ResponseBody
    public Result<String> helloError() {
 		return Result.error(CodeMsg.SERVER_ERROR);
    }
 	
	@GetMapping("/thymeleaf")
    public String  thymeleaf(Model model) {
 		model.addAttribute("name", "thymeleaf");
 		return "hello";
    }
	
	@GetMapping("/db/get")
	@ResponseBody
	public Result<User> getUserById(){
		User user = userService.getUserById(1);
		return Result.success(user);
	}
	
    @RequestMapping("/db/tx")
    @ResponseBody
    public Result<Boolean> dbTx() {
    	userService.tx();
        return Result.success(true);
    }
	
    @RequestMapping("/redis/get")
    @ResponseBody
    public Result<User> redisGet() {
    	User  user  = redisService.get(UserKey.getById, ""+1, User.class);
        return Result.success(user);
    }
    
    @RequestMapping("/redis/set")
    @ResponseBody
    public Result<Boolean> redisSet() {
    	User user  = new User();
    	user.setId(1);
    	user.setName("1111");
    	redisService.set(UserKey.getById, ""+1, user);//UserKey:id1
        return Result.success(true);
    }
    
	@GetMapping("/db/create_user")
	@ResponseBody
	public Result<User> createMiaoshaUser(){
		
		User user = userService.getUserById(1);
		return Result.success(user);
	}

}
