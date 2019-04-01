package com.demo.rabbitm1javaapi.ack;

import com.demo.rabbitm1javaapi.util.ResourceUtil;
import com.rabbitmq.client.*;
import com.sun.corba.se.impl.orbutil.CorbaResourceUtil;

import java.io.IOException;

/**
 * @author: admin
 * @create: 2019/3/27
 * @update: 10:23
 * @version: V1.0
 * @detail: 消息消费者，用于测试消费者手工应答和重回队列
 **/
public class AckConsumer {

    private  final static String QUEUE_NAME = "TEST_ACK_QUEUE";

    public static void main(String[] args)throws  Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUri(ResourceUtil.getKey("rabbitmq.uri"));

        //建立连接
        Connection conn =  factory.newConnection();
        //创建消息通道
        final Channel channel = conn.createChannel();

        //声明队列
        //String queue,boolean durable,boolean exclusive,boolean autoDelete,Map<String Object> arguments
        channel.queueDeclare(QUEUE_NAME, false,false,false,null);
        System.out.println("Waiting for message");

        //创建消费者，并接收消息
        Consumer consumer = new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String msg = new String(body,"UTF-8");
                System.out.println("Received message:'"+msg+"'");

                if(msg.contains("拒收")){
                    //拒绝消息
                    //requeue：是否重新入队列，trues:是，false:直接丢弃，相当于告诉队列可以直接删除掉
                    //如果只有一个消费者，requeue为true 的时候会造成消息重复消费
                    channel.basicReject(envelope.getDeliveryTag(),false);
                }else if(msg.contains("异常")){
                    //批量拒绝
                    //requeue:是否重新入队列
                    //如果只有一个消费者，requeue为true的时候会造成消息重复消费
                    channel.basicNack(envelope.getDeliveryTag(),true,false);
                }else{
                    //手工应答
                    //如果不应大，队列中的消息会一直存在，重新连接的时候会重复消费
                    channel.basicAck(envelope.getDeliveryTag(),true);
                }
            }
        };

        //开始获取消息，注意这里开启了手工应答
        //String queue boolean autoAck  Consumer callback
        channel.basicConsume(QUEUE_NAME,false,consumer);
    }
}
