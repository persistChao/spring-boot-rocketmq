package com.answer.boot.controller;

import com.alibaba.fastjson.JSONObject;
import com.answer.boot.bean.User;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @descreption
 * @Author answer
 * @Date 2019/5/17 15 00
 */
@RestController
@RequestMapping("/send")
public class ProducerController {

    @Autowired
    private DefaultMQProducer defaultMQProducer;


    @Autowired
    private TransactionMQProducer transactionMQProducer;

    @GetMapping("msg")
    public void sndMsg() {
        for (int i = 0; i < 10 ; i++) {
            User user = new User();
            user.setLoginName("sc" + i);
            user.setId(i);
            String json = JSONObject.toJSONString(user);
            Message msg = new Message("user-topic", "white", json.getBytes());
            //延迟消息
            msg.setDelayTimeLevel(4);
            try {
                SendResult result = defaultMQProducer.send(msg);
                System.out.println("消息Id:" + result.getMsgId() + " ,  发送状态:" + result.getSendStatus());
            } catch (MQClientException e) {
                e.printStackTrace();
            } catch (RemotingException e) {
                e.printStackTrace();
            } catch (MQBrokerException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 发送事务消息
     * @return
     */
    @GetMapping("/sendTransactionMess")
    public void sendTransactionMess() {

    }
}
