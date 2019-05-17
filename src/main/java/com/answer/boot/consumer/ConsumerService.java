package com.answer.boot.consumer;

import com.answer.boot.bean.MessageEvent;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @descreption
 * @Author answer
 * @Date 2019/5/17 15 14
 */
@Component
public class ConsumerService {
    @EventListener(condition = "#event.msgs[0].topic=='user-topic' &&  #event.msgs[0].tags=='white'")
    public void rocketMqMsgListener(MessageEvent event) {
        try {
            List<MessageExt> msgs = event.getMsgs();
            for (MessageExt ms : msgs) {
                System.err.println("消费消息:" + new String(ms.getBody()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
