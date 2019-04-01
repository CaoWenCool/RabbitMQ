package com.demo.rabbitm1javaapi.ack;

import com.demo.rabbitm1javaapi.util.ResourceUtil;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * @author: admin
 * @create: 2019/3/27
 * @update: 10:48
 * @version: V1.0
 * @detail:
 **/
public class AckProducer {

    private final static String QUEUE_NAME= "TEST_ACK_QUEUE";

    public static void main(String[] args) throws Exception{
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUri(ResourceUtil.getKey("rabbitmq.uri"));

        //建立连接
        Connection connection = factory.newConnection();
        //创建消息通道
        Channel channel = connection.createChannel();


        String  msg = "test ack message";

        //声明队列（默认交换机AMQP default Direct）
        //String name boolean durable,boolean exclusive ,boolean autoDelete,Map<String,Object> arguments
        channel.queueDeclare(QUEUE_NAME,false,false,false,null);

        //发送消息
        //String exchange String routingKey BasicProperties byte[] body
        for(int i=0;i<5;i++){
            channel.basicPublish("",QUEUE_NAME,null,(msg+i).getBytes());
        }

        channel.close();
        connection.close();
    }
}
