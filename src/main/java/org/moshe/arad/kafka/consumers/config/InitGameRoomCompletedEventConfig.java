package org.moshe.arad.kafka.consumers.config;

import org.moshe.arad.kafka.KafkaUtils;
import org.springframework.stereotype.Component;

@Component
public class InitGameRoomCompletedEventConfig extends SimpleConsumerConfig{

	public InitGameRoomCompletedEventConfig() {
		super();
		super.getProperties().put("group.id", KafkaUtils.INIT_GAME_ROOM_COMPLETED_EVENT_GROUP);
	}
}
