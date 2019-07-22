package cn.com.flaginfo.platform.registered.kafka;


import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.listener.MessageListener;

import java.util.concurrent.*;


/**
 * Created by Liang.Zhang on 2018/11/12.
 **/
public  class KafkaMessageConsumer implements MessageListener<String,String>{
    private static final Logger log = LoggerFactory.getLogger(KafkaMessageConsumer.class);
    private static ExecutorService pool= null;
    static {
        //
        ThreadFactory nameThreadFactory=new ThreadFactoryBuilder().setNameFormat("demo-pool-%d").build();
        pool=new ThreadPoolExecutor(5,100,0L, TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(1024),nameThreadFactory,new ThreadPoolExecutor.AbortPolicy());
    }


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
