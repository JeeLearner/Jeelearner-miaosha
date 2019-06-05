package com.jeelearn.mymiaosha.rabbitmq;

import com.jeelearn.mymiaosha.domain.MiaoshaOrder;
import com.jeelearn.mymiaosha.domain.MiaoshaUser;
import com.jeelearn.mymiaosha.redis.RedisService;
import com.jeelearn.mymiaosha.service.GoodsService;
import com.jeelearn.mymiaosha.service.MiaoshaService;
import com.jeelearn.mymiaosha.service.OrderService;
import com.jeelearn.mymiaosha.vo.GoodsVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Description:
 * @Auther: lyd
 * @Date: 2019/5/30
 * @Version:v1.0
 */
@Service
public class MiaoshaMQReceiver {

    private static Logger log = LoggerFactory.getLogger(MiaoshaMQReceiver.class);

    @Autowired
    RedisService redisService;

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    MiaoshaService miaoshaService;

    @RabbitListener(queues = MiaoshaMQConfig.MIAOSHA_QUEUE)
    public void receive(String message){
        log.info("receive message: " + message);
        MiaoshaMessage mm = RedisService.string2Bean(message, MiaoshaMessage.class);
        MiaoshaUser user = mm.getUser();
        long goodsId = mm.getGoodsId();

        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        int stock = goods.getStockCount();
        if(stock <= 0) {
            return;
        }
        //判断是否已经秒杀到了
        MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(), goodsId);
        if(order != null) {
            return;
        }
        //减库存 下订单 写入秒杀订单
        miaoshaService.miaosha(user, goods);
    }


}

