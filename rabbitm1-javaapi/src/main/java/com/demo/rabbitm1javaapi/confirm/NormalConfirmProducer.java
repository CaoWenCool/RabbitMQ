package com.demo.rabbitm1javaapi.confirm;

import com.demo.rabbitm1javaapi.util.ResourceUtil;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;


/**
 * @author: admin
 * @create: 2019/3/27
 * @update: 16:33
 * @version: V1.0
 * @detail: 普通确认模式，
 **/
public class NormalConfirmProducer {

    private final static String QUEUE_NAME = "ORIGIN_QUEUE";

    public static void main(String[] args) throws Exception{
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUri(ResourceUtil.getKey("rabbitmq.uri"));

        //建立连接
        Connection connection = factory.newConnection();
        //创建消息通道
        Channel channel = connection.createChannel();

        String msg = "Hello World,Rabbit MQ,Normal Confirm";
        //声明队列（默认交换机 AMQP default,Direct）
        //String queue,boolean durable,boolean exclusive,boolean autoDelete,Map<String,Object> arguments
        channel.queueDeclare(QUEUE_NAME,false,false,false,null);

        //开启发送方式确认模式
        channel.confirmSelect();

        channel.basicPublish("",QUEUE_NAME,null,msg.getBytes());
        //普通Confirm,发送一条，确认一条
        if(channel.waitForConfirms()){
            System.out.println("消息发送成功");
        }
        channel.close();
        connection.close();
    }
}
