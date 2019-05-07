package com.answer.consumer;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @descreption
 * @Author answer
 * @Date 2019/5/7 16 20
 */
@Component
public class MyProducer {

    Log log = LogFactory.get();

    public static final Logger logger = LoggerFactory.getLogger(MQConsumerConfiguration.class);
    @Value("${rocketmq.consumer.namesrvAddr}")
    private String namesrvAddr;
    @Value("${rocketmq.consumer.groupName}")
    private String groupName;


    @PostConstruct
    private void init() {
        DefaultMQProducer producer = new DefaultMQProducer();
        producer.setNamesrvAddr(namesrvAddr);
        producer.setProducerGroup(groupName);

    }
}
