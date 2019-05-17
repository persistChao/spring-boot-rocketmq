package com.answer.config;

import com.answer.exception.RocketMQErrorEnum;
import com.answer.exception.RocketMQException;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @descreption
 * @Author answer
 * @Date 2019/3/29 12 50
 */
//@Configuration
public class MqProducerConfig {
    private static final Logger logger = LoggerFactory.getLogger(MqProducerConfig.class);

    @Value("${rocketmq.producer.groupName}")
    private String groupName;

    @Value("${rocketmq.producer.nameSrvAddr}")
    private String namesrvAddr;

    @Value("${rocketmq.producer.maxMessageSize}")
    private Integer maxMessageSize;

    @Value("${rocketmq.producer.sendMsgTimeout}")
    private Integer sendMsgTimeout;

    @Value("${rocketmq.producer.retryTimesWhenSendFailed}")
    private Integer retryTimesWhenSendFailed;


    @Bean
    public DefaultMQProducer getRocketMQProducer() throws MQClientException {
        if (StringUtils.isBlank(this.groupName)) {
            throw new RocketMQException(RocketMQErrorEnum.PARAM_NULL , "nameServerAddr is blank" , false);
        }

        if (StringUtils.isEmpty(this.namesrvAddr)) {
            throw new RocketMQException(RocketMQErrorEnum.PARAM_NULL, "nameServerAddr is blank", false);
        }

        DefaultMQProducer producer;
        producer = new DefaultMQProducer(this.groupName);
        producer.setNamesrvAddr(this.namesrvAddr);
        //如果需要同一个jvm中不同的producer往不同的mq集群发送消息，需要设置不同的instanceName
        producer.setInstanceName(System.currentTimeMillis()+"_instance");
        if(this.maxMessageSize!=null){
            producer.setMaxMessageSize(this.maxMessageSize);
        }
        if(this.sendMsgTimeout!=null){
            producer.setSendMsgTimeout(this.sendMsgTimeout);
        }
        //如果发送消息失败，设置重试次数，默认为2次
        if(this.retryTimesWhenSendFailed!=null){
            producer.setRetryTimesWhenSendFailed(this.retryTimesWhenSendFailed);
        }

        try {
            producer.start();
            logger.info(String.format("producer is start! groupName:[] , namesrvAddr:[]") , this.groupName , this.namesrvAddr);
        }catch (RocketMQException e){
            logger.error(String.format("producer is error {}", e.getMessage(), e));
            throw new RocketMQException(e);
        }
        return producer;

    }


}
