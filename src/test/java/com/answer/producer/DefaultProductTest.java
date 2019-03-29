package com.answer.producer;

import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @descreption
 * @Author answer
 * @Date 2019/3/29 18 15
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class DefaultProductTest {

    private static final Logger logger = LoggerFactory.getLogger(DefaultProductTest.class);

    @Autowired
    private DefaultMQProducer producer;

    @Test
    public void send() throws InterruptedException, RemotingException, MQClientException, MQBrokerException {
        String msg = "demo producer message";
        logger.info("发送消息");
        Message message = new Message("TestTopic1", "", msg.getBytes());
        SendResult sendResult = producer.send(message);
        logger.info("消息发送响应信息："+sendResult.toString());
    }
}
