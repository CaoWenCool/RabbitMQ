package com.demo.rabbitm1javaapi.dlx;

import com.demo.rabbitm1javaapi.util.ResourceUtil;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: admin
 * @create: 2019/3/27
 * @update: 17:06
 * @version: V1.0
 * @detail: 使用延时插件实现的消息的投递-生产者
 * 必须要在服务端安装 rabbitmq-delayed-message-exchange插件，安装步骤见README.MD
 *  先启动消费者
 **/
public class DelayPluginProducer {

    public static void main(String[] args)throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUri(ResourceUtil.getKey("rabbitmq.uri"));

        //建立连接
        Connection conn = factory.newConnection();
        //创建消息通道
        Channel channel = conn.createChannel();

        //延时投递，比如延时1分钟
        Date now = new Date();
        Calendar delayTimeD = Calendar.getInstance();
        delayTimeD.add(Calendar.MINUTE,+1);//1分钟后投递
        Date delayTime = delayTimeD.getTime();

        //准时投递
        Date  exactDelayTime = new Date("2019/01/14,22:22:00");

        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        String msg = "publish at" + sf.format(now)+ "\t deliver at" +sf.format(delayTime);

        //延迟的间隔时间，目标时刻减去当前时刻
        Map<String,Object> headers = new HashMap<String,Object>();
        headers.put("x-delay",delayTime.getTime() - now.getTime());

        AMQP.BasicProperties.Builder props = new AMQP.BasicProperties.Builder()
                .headers(headers);
        channel.basicPublish("DELAY_EXCHANGE","DELAY_KEY",props.build(),msg.getBytes());

        channel.close();
        conn.close();

    }
}
