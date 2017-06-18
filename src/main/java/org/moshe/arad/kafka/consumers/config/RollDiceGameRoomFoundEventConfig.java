package org.moshe.arad.kafka.consumers.config;

import org.moshe.arad.kafka.KafkaUtils;
import org.springframework.stereotype.Component;

@Component
public class RollDiceGameRoomFoundEventConfig extends SimpleConsumerConfig{

	public RollDiceGameRoomFoundEventConfig() {
		super();
		super.getProperties().put("group.id", KafkaUtils.ROLL_DICE_GAME_ROOM_FOUND_EVENT_GROUP);
	}
}
