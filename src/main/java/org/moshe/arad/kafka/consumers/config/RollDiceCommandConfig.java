package org.moshe.arad.kafka.consumers.config;

import org.moshe.arad.kafka.KafkaUtils;
import org.springframework.stereotype.Component;

@Component
public class RollDiceCommandConfig extends SimpleConsumerConfig{

	public RollDiceCommandConfig() {
		super();
		super.getProperties().put("group.id", KafkaUtils.ROLL_DICE_COMMAND_GROUP);
	}
}
