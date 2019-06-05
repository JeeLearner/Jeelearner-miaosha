package com.jeelearn.mymiaosha.rabbitmq;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description:
 * @Auther: lyd
 * @Date: 2019/5/30
 * @Version:v1.0
 */
@Configuration
public class MiaoshaMQConfig {

    public static final String MIAOSHA_QUEUE = "miaosha.queue";

    @Bean
    public Queue queue() {
        return new Queue(MIAOSHA_QUEUE, true);
    }
}

