package cn.com.flaginfo.platform.registered.mq;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import com.alibaba.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 消息适配器,能处理任何消息
 * @author Rain
 *
 */
@Service
public class MessageListenerAdapter implements MessageListenerConcurrently {

    private static final Logger log =
            LoggerFactory.getLogger(MessageListenerAdapter.class);
    private final String mqSet="mq_set";

	@Override
	public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> exts,
			ConsumeConcurrentlyContext ccc) {
		MessageExt msg = exts.get(0);
		String messageBody=new String(msg.getBody());
		log.info("msg is [{}]",messageBody);
		long start = System.currentTimeMillis();
		if(exts.size()>1){
			throw new RuntimeException("获取消息数>1,请检查配置");
		}
		JSONObject joMessage = JSON.parseObject(messageBody);
		String messageId = joMessage.getString("messageId");

		return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;

	}


}
