package com.demo.rabbitm1javaapi.confirm;

import com.demo.rabbitm1javaapi.util.ResourceUtil;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmListener;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * @author: admin
 * @create: 2019/3/27
 * @update: 11:07
 * @version: V1.0
 * @detail:
 **/
public class AsyncConfirmProducer {

    private final static String QUEUE_NAME = "ORIGIN_QUEUE";

    public static void main(String[] args)throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUri(ResourceUtil.getKey("rabbitmq.uri"));

        //建立连接
        Connection connection =  factory.newConnection();
        //创建消息通道
        Channel channel = connection.createChannel();

        String msg = "Hello World ,Rabbit MQ ,Async Confirm";
        //声明队列，（默认交换机AMQP default Direct）
        //String queue,boolean durable,boolean exclusive,boolean autoDelete,Map<String,Object> arguments
        channel.queueDeclare(QUEUE_NAME,false,false,false,null);

        //用来维护为确认得消息deliveryTag
        final SortedSet<Long> confirmSet = Collections.synchronizedNavigableSet(new TreeSet<Long>());

        //这里不会打印所有响应得ACK；ACK可能有多个，有可能一次确认多条，也有可能一次确认一条
        //异步监听确认和未确认得消息
        //如果要重复运行，先停掉之前得生产者，清空队列
        channel.addConfirmListener(new ConfirmListener() {
            @Override
            public void handleNack(long deliveryTag, boolean multiple) throws IOException {
                System.out.println("Broker未确认得消息，标识"+deliveryTag);
                if(multiple){
                    //headSet表示后面参数之前得所有元素，全部删除
                    confirmSet.headSet(deliveryTag+1L).clear();

                }else{
                    confirmSet.remove(deliveryTag);
                }
                //这里添加重发得方法
            }

            public void handleAck(long deliveryTag,boolean multiple)throws IOException{
                //如果true表示批量执行了deliveryTag这个值以前（小于deliveryTag的）的所有消息，如果为false的话表示单条确认
                System.out.println(String.format("Broker 已经确认消息，标识：%d,多个消息：%b",deliveryTag,multiple));
                System.out.println("multiple:"+multiple);
                if(multiple){
                    System.out.println("deliveryTag"+deliveryTag);
                    //headSet标识后面参数之前的所有元素，全部删除
                    confirmSet.headSet(deliveryTag+1L).clear();
                }else{
                    //只移除一个元素
                    confirmSet.remove(deliveryTag);
                }
                System.out.println("未确认的消息"+confirmSet);
            }
        });

        //开启发送方确认模式
        channel.confirmSelect();
        for(int i=0;i<10;i++){
            long nextSeqNo = channel.getNextPublishSeqNo();
            //发送消息，
            //String exchange,String routingKey,BasicProperties props,byte[] body
            channel.basicPublish("",QUEUE_NAME,null,(msg+"-"+i).getBytes());
            confirmSet.add(nextSeqNo);
        }
        System.out.println("所有消息"+confirmSet);

        //这里注释掉的原因是如果先关闭了，可能收不到后面的ACK了
        //channel.close();
        //connection.close();
    }
}
