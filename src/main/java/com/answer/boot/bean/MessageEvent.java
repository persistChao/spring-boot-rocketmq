package com.answer.boot.bean;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.context.ApplicationEvent;

import java.util.List;

/**
 * @descreption
 * @Author answer
 * @Date 2019/5/17 14 35
 */

public class MessageEvent extends ApplicationEvent{

    private DefaultMQPushConsumer consumer;
    private List<MessageExt> msgs;

    public MessageEvent(List<MessageExt> msgs , DefaultMQPushConsumer consumer) throws Exception {
        super(msgs);
        this.consumer = consumer;
        this.setMsgs(msgs);
    }

    public DefaultMQPushConsumer getConsumer() {
        return consumer;
    }

    public void setConsumer(DefaultMQPushConsumer consumer) {
        this.consumer = consumer;
    }

    public List<MessageExt> getMsgs() {
        return msgs;
    }

    public void setMsgs(List<MessageExt> msgs) {
        this.msgs = msgs;
    }
}
