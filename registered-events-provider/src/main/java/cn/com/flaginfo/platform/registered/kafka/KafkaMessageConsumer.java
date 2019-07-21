package cn.com.flaginfo.platform.registered.kafka;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.listener.MessageListener;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Created by Liang.Zhang on 2018/11/12.
 **/
public  class KafkaMessageConsumer implements MessageListener<String,String>{
    private static final Logger log = LoggerFactory.getLogger(KafkaMessageConsumer.class);
    private static ExecutorService pool= Executors.newFixedThreadPool(5);

    @Override
    public  void onMessage(ConsumerRecord<String, String> data) {
        try {
            log.info("consumer start");
            String strTopic = data.topic();
            log.info("consumer topic is {}",strTopic);
            // 过滤数据
            log.info("message is [{}]",data.value());
           pool.execute(new EventsDealThread(data.value()));
        }
        catch (Exception e){
            e.printStackTrace();
            log.error("consumer error,data is [{}]",data);
        }
    }
}
