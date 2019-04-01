package com.demo.rabbitm1javaapi.dlx;

import com.demo.rabbitm1javaapi.util.ResourceUtil;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * @author: admin
 * @create: 2019/3/28
 * @update: 8:51
 * @version: V1.0
 * @detail: 消息生产者，通过TTL测试死信队列
 **/
public class DlxProducer {

    public static void main(String[] args)throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUri(ResourceUtil.getKey("rabbitmq.uri"));

        //建立连接
        Connection connection = factory.newConnection();
        //创建消息通道
        Channel channel =connection.createChannel();

        String msg = "Hello world,Rabbit MQ,DLX MSG";

        //设置属性，消息10秒钟过期
        AMQP.BasicProperties properties = new AMQP.BasicProperties.Builder()
                .deliveryMode(2)//持久化消息
                .contentEncoding("UTF-8")
                .expiration("10000")//TTL
                .build();

        //发送消息
        for (int i = 0;i<10;i++){
            channel.basicPublish("","TEST_DLX_QUEUE",properties,msg.getBytes());
        }


        channel.close();
        connection.close();
    }

}
