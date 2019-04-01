package com.demo.rabbitm1javaapi.confirm;

import com.demo.rabbitm1javaapi.util.ResourceUtil;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.sun.org.apache.bcel.internal.generic.FADD;

/**
 * @author: admin
 * @create: 2019/3/27
 * @update: 16:20
 * @version: V1.0
 * @detail: 消息生产者，测试confirm模式
 **/
public class BatchConfirmProducer {

    private final static String QUEUE_NAME = "ORIGIN_QUEUE";

    public static void main(String[] args)throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUri(ResourceUtil.getKey("rabbitmq.uri"));

        //建立连接
        Connection connection =factory.newConnection();
        //创建消息通道
        Channel channel = connection.createChannel();


        String msg = "Hello world,Rabbit MQ,Batch confirm";
        //声明队列（默认交换机AMQP default Direct）
        //String queue,boolean durable,boolean exclusive,boolean autoDelete,Map<String,Object> arguments
        channel.queueDeclare(QUEUE_NAME, false,false,false,null);

        try{
            channel.confirmSelect();
            for(int i = 0;i<5;i++){
                //发送消息
                //String exchange,String routingKey,BasicProperties props,byte[] body
                channel.basicPublish("",QUEUE_NAME,null,(msg+ "-"+i ).getBytes());
            }
            //批量确认结果，ACK如果是Multiple = True,代表ACK里面的Delivery-Tag之前的消息被确认了
            //比如5条消息可能只收到1个ACK，也可能收到2个
            //直到所有消息都发布，只要有一个未被Broker确认就会IOException
            channel.waitForConfirmsOrDie();
            System.out.println("消息发送完毕，批量确认成功");
        }catch (Exception e){
            //发生异常，可能需要对所有消息进行重发
            e.printStackTrace();
        }

        channel.close();
        connection.close();
    }
}
