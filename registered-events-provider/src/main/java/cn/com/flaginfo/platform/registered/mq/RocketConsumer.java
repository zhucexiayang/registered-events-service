package cn.com.flaginfo.platform.registered.mq;

import cn.com.flaginfo.platform.registered.commons.diamond.DiamondProperties;
import com.alibaba.rocketmq.client.consumer.DefaultMQPullConsumer;
import com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
import com.alibaba.rocketmq.client.consumer.MQPullConsumerScheduleService;
import com.alibaba.rocketmq.client.consumer.rebalance.AllocateMessageQueueAveragelyByCircle;
import com.alibaba.rocketmq.common.consumer.ConsumeFromWhere;
import com.alibaba.rocketmq.common.protocol.heartbeat.MessageModel;

public class RocketConsumer {

	static final DefaultMQPushConsumer pushConsumer = new DefaultMQPushConsumer(DiamondProperties.getInstance().getProperty("rocket_mq_group"));

	static final DefaultMQPullConsumer pullConsumer = new DefaultMQPullConsumer(DiamondProperties.getInstance().getProperty("rocket_mq_group"));

	static final MQPullConsumerScheduleService scheduleService = new MQPullConsumerScheduleService("ums_schedule");

	static{
		pushConsumer.setNamesrvAddr(DiamondProperties.getInstance().getProperty("rocket_mq_address"));
		pushConsumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);//CONSUME_FROM_LAST_OFFSET
		pushConsumer.setInstanceName(RocketConsumer.class.hashCode()+"_push");
		pushConsumer.setMessageModel(MessageModel.CLUSTERING);
		pushConsumer.setConsumeMessageBatchMaxSize(1);
		pushConsumer.setAllocateMessageQueueStrategy(new AllocateMessageQueueAveragelyByCircle());

		pullConsumer.setNamesrvAddr(DiamondProperties.getInstance().getProperty("rocket_mq_address"));
		pullConsumer.setInstanceName(RocketConsumer.class.hashCode()+"_pull");
		pullConsumer.setMessageModel(MessageModel.CLUSTERING);
		pullConsumer.setPersistConsumerOffsetInterval(1*500);

		scheduleService.getDefaultMQPullConsumer().setNamesrvAddr(DiamondProperties.getInstance().getProperty("rocket_mq_address"));
		scheduleService.getDefaultMQPullConsumer().setInstanceName(RocketConsumer.class.hashCode()+"_schedule_instance");
		//scheduleService.getDefaultMQPullConsumer().setAllocateMessageQueueStrategy(allocateMessageQueueStrategy);
		scheduleService.setMessageModel(MessageModel.CLUSTERING);
		scheduleService.setPullThreadNums(100);
	}

	public static DefaultMQPushConsumer getPushConsumer(){
		return pushConsumer;
	}

	public static DefaultMQPullConsumer getPullConsumer(){
		return pullConsumer;
	}

	public static MQPullConsumerScheduleService getScheduleservice() {
		return scheduleService;
	}
}
