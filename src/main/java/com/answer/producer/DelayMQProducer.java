package com.answer.producer;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

/**
 * @descreption
 * @Author answer
 * @Date 2019/5/6 19 51
 */
@Service
public class DelayMQProducer {

    Log log = LogFactory.get();

    @Value("${rocketmq.producer.nameSrvAddr}")
    private String namesrvAddr;

    public void delayMqProducer() {
        DefaultMQProducer producer = new DefaultMQProducer();
        producer.setProducerGroup("delayGroup");
        producer.setInstanceName("delayInstance");
        producer.setNamesrvAddr(namesrvAddr);

        try {
            producer.start();
            Message message = new Message("DelayTopic", "push", "发送延迟消息---".getBytes());
            message.setDelayTimeLevel(4);
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();

            SendResult result = producer.send(message);
            System.out.println("send result =" + result);
        } catch (MQClientException e) {
            e.printStackTrace();
        } catch (RemotingException e) {
            e.printStackTrace();
        } catch (MQBrokerException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            producer.shutdown();
        }
    }
}
