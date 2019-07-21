package cn.com.flaginfo.platform.registered.mq;


import cn.com.flaginfo.platform.registered.commons.diamond.DiamondProperties;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.common.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by sen.wang on 2017/7/4.
 */


public class RocketmqProducer {
    /*private static final Logger log = LoggerFactory.getLogger(RocketmqProducer.class);
    private DefaultMQProducer producer;
    private static RocketmqProducer instance = new RocketmqProducer();
    public  static RocketmqProducer getInstance(){
        return instance;
    }
    private RocketmqProducer(){
        String MQtype= DiamondProperties.getInstance().getProperty("MQtype");
        if(MQtype.equals("TEST")) {
            initConnect();
            log.info("SenderToMQ start succ");
        }
    }

    private void initConnect() {
        try {
            producer = new DefaultMQProducer("send_task_record");
            producer.setNamesrvAddr(DiamondProperties.getInstance().getProperty("rocket_mq_address"));
//		    producer.setInstanceName(DiamondProperties.getInstance().getProperty("producer_Instance"));
            producer.setHeartbeatBrokerInterval(60000);
            producer.setMaxMessageSize(2*1024*1024);
            producer.start();
            log.info("initConnect succ");
        } catch (MQClientException e) {
            e.printStackTrace();
        }
    }

    public boolean send(String json) {
        String tag = DiamondProperties.getInstance().getProperty("tag");
        String topic = DiamondProperties.getInstance().getProperty("topic");
        return send(json,topic,tag);
    }
    public boolean sendWithkey(String json, String key) {
        String tag = DiamondProperties.getInstance().getProperty("tag");
        String topic =DiamondProperties.getInstance().getProperty("topic");
        return sendWithkey(json,topic,tag,key);
    }

    public boolean send(String json, String tag) {
        String topic = DiamondProperties.getInstance().getProperty("topic");
        return send(json,topic,tag);
    }

    private boolean sendWithkey(String str, String topic, String tag, String key) {
        try {
            long begin = System.currentTimeMillis();
            Message msg = new Message(topic,tag,key,str.getBytes());
            SendResult sr = producer.send(msg);
            String result =sr.getSendStatus().toString();
            log.info(str+",result:"+result+"("+(System.currentTimeMillis()-begin)+"ms)");
            if("SEND_OK".equals(result)){
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.warn("Exception:" + e);
            log.warn("json not send:" + str);
        }
        return false;
    }
    private boolean send(String str, String topic, String tag) {
        try {
            long begin = System.currentTimeMillis();
            Message msg = new Message(topic,tag,str.getBytes());
            SendResult sr = producer.send(msg);
            String result =sr.getSendStatus().toString();
            log.info(str+",result:"+result+"("+(System.currentTimeMillis()-begin)+"ms)");
            if("SEND_OK".equals(result)){
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.warn("Exception:" + e);
            log.warn("json not send:" + str);
        }
        return false;
    }

    public void close(){
        producer.shutdown();
    }*/
}

