package com.answer.consumer;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @descreption
 * @Author answer
 * @Date 2019/5/6 19 58
 */
@Service
public class DelayConsumer {

    @Value("${rocketmq.producer.nameSrvAddr}")
    private String namesrvAddr;

    public void defaultMQPushConsumer() {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("delayGroup");
        consumer.setNamesrvAddr(namesrvAddr);
        try {
            consumer.subscribe("TopicTest1", "push");

            consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
            consumer.registerMessageListener((MessageListenerConcurrently) (list, context)->{
                try {
                    for (MessageExt messageExt : list) {
                        String messageBody = new String(messageExt.getBody());
                        System.out.println( new Date().toString() + "  消费响应:"+ messageExt.getMsgId() +", MsgBody:" + messageBody);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                }
                return  ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            });

            consumer.start();

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
