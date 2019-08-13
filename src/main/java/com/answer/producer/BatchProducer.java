package com.answer.producer;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @Author answer
 * @Date 2019/8/12 21 26
 */

public class BatchProducer {


    public static class ListSplitter implements Iterator<List<Message>> {

        private final int SIZE_LIMIT = 1000 * 1000;

        private final List<Message> messages;

        private int currIndex;

        public ListSplitter(List<Message> messages) {
            this.messages = messages;
        }

        @Override
        public boolean hasNext() {
            return currIndex < messages.size();
        }

        @Override
        public List<Message> next() {
            int nextIdex = currIndex;
            int totalSize = 0;
            for (; nextIdex < messages.size(); nextIdex++) {
                Message msg = messages.get(nextIdex);
                int tmpSize = msg.getTopic().length() + msg.getBody().length;
                Map<String, String> properties = msg.getProperties();
                for (Map.Entry<String, String> entry : properties.entrySet()) {
                    tmpSize += entry.getKey().length() + entry.getValue().length();
                }
                tmpSize = tmpSize + 20;
                if (tmpSize > SIZE_LIMIT) {
                    if (nextIdex - currIndex == 0) {
                        nextIdex++;
                    }
                    break;
                }
                if (tmpSize + totalSize > SIZE_LIMIT) {
                    break;
                } else {
                    totalSize += tmpSize;
                }
            }
            List<Message> subList = messages.subList(currIndex, nextIdex);
            currIndex = nextIdex;
            return subList;
        }
    }


    public static void main(String[] args) throws MQClientException {
        List<Message> messages = new ArrayList<>();
        for (int i = 0; i <50;i++) {
            Message message = new Message("TestTopic" ,"TagA || TagB || TagC","batchMsgKey" + i,("this is batch message producer demo body is batchMessage" +i).getBytes());
            messages.add(message);
        }
        DefaultMQProducer producer = new DefaultMQProducer("batchProducerGroup");
        producer.setNamesrvAddr("localhost:9876");
        producer.start();
        ListSplitter splitter = new ListSplitter(messages);
        while (splitter.hasNext()) {
            try {
                List<Message> listItem = splitter.next();
                producer.send(listItem);
            } catch (Exception e) {
                e.printStackTrace();
                //handle the error
            }
        }
    }
}
