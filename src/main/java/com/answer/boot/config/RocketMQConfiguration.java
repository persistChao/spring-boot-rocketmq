package com.answer.boot.config;

import com.answer.boot.bean.MessageEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.*;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @descreption
 * @Author answer
 * @Date 2019/5/17 11 18
 */
@Configuration
//启动自动配置文件属性的获取,通过指定的类
@EnableConfigurationProperties
@Slf4j
public class RocketMQConfiguration {

    private final Logger logger = LoggerFactory.getLogger(RocketMQConfiguration.class);
    @Autowired
    private RocketMQProperties rocketMQProperties;

    /**
     * 事件监听
     */
    @Autowired
    private ApplicationEventPublisher publisher = null;

    private static boolean isFirstSub = true;

    private static long startTime = System.currentTimeMillis();

    /**
     * 容器初始化的时候打印参数
     */
    @PostConstruct
    public void init() {
        System.err.println(rocketMQProperties.getNamesrvAddr());
        System.err.println(rocketMQProperties.getProducerGroupName());
        System.err.println(rocketMQProperties.getConsumerBatchMaxSize());
        System.err.println(rocketMQProperties.getConsumerGroupName());
        System.err.println(rocketMQProperties.getConsumerInstanceName());
        System.err.println(rocketMQProperties.getProducerInstanceName());
        System.err.println(rocketMQProperties.getProducerTranInstanceName());
        System.err.println(rocketMQProperties.getTransactionProducerGroupName());
        System.err.println(rocketMQProperties.isConsumerBroadcasting());
        System.err.println(rocketMQProperties.isEnableHistoryConsumer());
        System.err.println(rocketMQProperties.isEnableOrderConsumer());
        System.out.println(rocketMQProperties.getSubscribe().get(0));
        logger.info("[RocketMq初始化成功...]");
    }

    /**
     * 创建普通消息发送者实例
     *
     * @return
     * @throws MQClientException
     */
    @Bean(name = "defaultMQProducer")
    public DefaultMQProducer defaultMQProducer() throws MQClientException {
        DefaultMQProducer producer = new DefaultMQProducer(rocketMQProperties.getProducerGroupName());
        producer.setNamesrvAddr(rocketMQProperties.getNamesrvAddr());
        producer.setInstanceName(rocketMQProperties.getProducerTranInstanceName());
        producer.setRetryTimesWhenSendAsyncFailed(10);
        producer.start();
        return producer;
    }

    /**
     * 创建支持消息事务发送的实例
     *
     * @return
     * @throws MQClientException
     */

    @Bean(name = "transactionMQProducer")
    public TransactionMQProducer transactionMQProducer() throws MQClientException {
        TransactionMQProducer producer = new TransactionMQProducer(rocketMQProperties.getTransactionProducerGroupName());
        producer.setNamesrvAddr(rocketMQProperties.getNamesrvAddr());
        producer.setInstanceName(rocketMQProperties.getProducerTranInstanceName());
        producer.setRetryTimesWhenSendAsyncFailed(10);
        producer.setCheckRequestHoldMax(1);
        producer.start();
        return producer;
    }


    /**
     * 创建消息消费的实例
     *
     * @return
     * @throws MQClientException
     */

    @Bean
    public DefaultMQPushConsumer defaultMQPushConsumer() throws MQClientException {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(rocketMQProperties.getConsumerGroupName());
        consumer.setNamesrvAddr(rocketMQProperties.getNamesrvAddr());
        consumer.setInstanceName(rocketMQProperties.getConsumerInstanceName());
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);

        //判断是否是广播模式
        if (rocketMQProperties.isConsumerBroadcasting()) {
            consumer.setMessageModel(MessageModel.BROADCASTING);
        }

        //设置批量消费
        consumer.setConsumeMessageBatchMaxSize(rocketMQProperties.getConsumerBatchMaxSize() == 0 ?
                1 : rocketMQProperties.getConsumerBatchMaxSize());

        //获取topic和tag
        List<String> subscribeList = rocketMQProperties.getSubscribe();
        for (String subscribe : subscribeList) {
            consumer.subscribe(subscribe.split(":")[0], subscribe.split(":")[1]);
        }
        // 顺序消费
        if (rocketMQProperties.isEnableOrderConsumer()) {
            consumer.registerMessageListener(new MessageListenerOrderly() {
                @Override
                public ConsumeOrderlyStatus consumeMessage(List<MessageExt> msgs, ConsumeOrderlyContext context) {
                    try {
                        context.setAutoCommit(true);
                        //过滤消息
                        msgs = filterMessage(msgs);
                    } catch (Exception e) {
                        logger.error("consume meesage exception ", e);
                        return ConsumeOrderlyStatus.SUSPEND_CURRENT_QUEUE_A_MOMENT;
                    }
                    return ConsumeOrderlyStatus.SUCCESS;
                }
            });
        } else {
            //并发消费
            consumer.registerMessageListener(new MessageListenerConcurrently() {
                @Override
                public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                    try {
                        msgs = filterMessage(msgs);
                        if (msgs.size() == 0) {
                            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                        }
                        publisher.publishEvent(new MessageEvent(msgs, consumer));
                    } catch (Exception e) {
                        logger.error("consumer concurrent message exception ", e);
                        return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                    }
                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                }
            });
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                    try {
                        consumer.start();
                    } catch (MQClientException e) {
                        e.printStackTrace();
                    }
                    logger.info("rocketMq consumer server is starting......");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        return consumer;
    }

    private List<MessageExt> filterMessage(List<MessageExt> msgs) {
        if (isFirstSub && !rocketMQProperties.isEnableHistoryConsumer()) {
            msgs = msgs.stream().filter(item -> startTime - item.getBornTimestamp() < 0).collect(Collectors.toList());
        }
        if (isFirstSub && msgs.size() > 0) {
            isFirstSub = false;
        }
        return msgs;
    }
}
