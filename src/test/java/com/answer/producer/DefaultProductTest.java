package com.answer.producer;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

/**
 * @descreption
 * @Author answer
 * @Date 2019/3/29 18 15
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class DefaultProductTest {

    private static final Logger logger = LoggerFactory.getLogger(DefaultProductTest.class);



    @Test
    public void send() throws InterruptedException, RemotingException, MQClientException, MQBrokerException {
        DefaultMQProducer producer = new DefaultMQProducer("testProducerGroup");
        producer.setNamesrvAddr("localhost:9876");
        producer.start();
        logger.info("发送消息");
        for (int i = 0; i < 20; i++) {
            String msg = "demo producer message " + i;
            Message message = new Message("TestTopic1", "", msg.getBytes());
            SendResult sendResult = producer.send(message);
            logger.info("消息发送响应信息："+sendResult.toString());
        }
    }

    @Test
    public void consume() throws MQClientException {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("testConsumerGroup");
        consumer.setNamesrvAddr("localhost:9876");
        consumer.subscribe("TestTopic1" ,"*");
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
//        consumer.registerMessageListener((MessageListenerConcurrently)(msgs , context)->{
//            for (MessageExt msg : msgs) {
//                String message = new String(msg.getBody());
//                System.out.println("消费消息 " + message);
//            }
//            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
//        });
        consumer.start();
    }
}
