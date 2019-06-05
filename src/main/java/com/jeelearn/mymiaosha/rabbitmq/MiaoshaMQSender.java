package com.jeelearn.mymiaosha.rabbitmq;

import com.jeelearn.mymiaosha.redis.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Description:
 * @Auther: lyd
 * @Date: 2019/5/30
 * @Version:v1.0
 */
@Service
public class MiaoshaMQSender {

    private static Logger log = LoggerFactory.getLogger(MiaoshaMQSender.class);

    @Autowired
    AmqpTemplate amqpTemplate;

    public void sendMiaoshaMessage(MiaoshaMessage mm) {
        String msg = RedisService.bean2String(mm);
        log.info("send message:"+msg);
        amqpTemplate.convertAndSend(MiaoshaMQConfig.MIAOSHA_QUEUE, msg);
    }
}

