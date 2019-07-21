package cn.com.flaginfo.platform.registered.mq;

import cn.com.flaginfo.platform.registered.commons.diamond.DynamicProperties;
import com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
import com.alibaba.rocketmq.client.exception.MQClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Created by hc on 2017/8/28.
 */
@Component
public class MessageConsumer {

    private static final Logger log = LoggerFactory.getLogger(MessageConsumer.class);
    @Autowired
    private MessageListenerAdapter messageListenerAdapter;
    //@PostConstruct
    public void setConsumer() throws MQClientException{
       /* String MQtype= DynamicProperties.getInstance().getProperty("MQtype");
        if(MQtype.equals("TEST")) {
            DefaultMQPushConsumer consumer=RocketConsumer.getPushConsumer();
            consumer.subscribe(DynamicProperties.getInstance().getProperty("topic_consumer"),"*");
            consumer.registerMessageListener(messageListenerAdapter);
            consumer.start();
            log.info("consumer started");
        }*/

    }
}
