package com.answer.web;

import com.answer.consumer.DelayConsumer;
import com.answer.producer.DelayMQProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @descreption
 * @Author answer
 * @Date 2019/5/7 10 50
 */
@RestController
@RequestMapping("/delay")
public class DelayController {

    @Autowired
    DelayMQProducer producer;

    @Autowired
    DelayConsumer consumer;

    @RequestMapping("send")
    public Object sendMsg() {
        producer.delayMqProducer();
        return "ok";
    }

    @RequestMapping("/consume")
    public String consume() {
        consumer.defaultMQPushConsumer();
        return "ok";
    }

}
