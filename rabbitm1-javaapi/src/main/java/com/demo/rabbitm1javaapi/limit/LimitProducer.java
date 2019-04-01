package com.demo.rabbitm1javaapi.limit;

import com.demo.rabbitm1javaapi.util.ResourceUtil;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * @author: admin
 * @create: 2019/3/28
 * @update: 9:16
 * @version: V1.0
 * @detail:
 **/
public class LimitProducer {
    private final static String QUEUE_NAME = "TEST_LIMIT_QUEUE";

    public static void main(String[] args)throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUri(ResourceUtil.getKey("rabbitmq.uri"));

        //建立连接
        Connection conn = factory.newConnection();
        //创建消息通道
        Channel channel = conn.createChannel();

        String msg = " ";
    }
}
