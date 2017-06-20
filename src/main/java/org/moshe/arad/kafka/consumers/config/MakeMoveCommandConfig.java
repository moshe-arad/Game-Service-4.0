package org.moshe.arad.kafka.consumers.config;

import org.moshe.arad.kafka.KafkaUtils;
import org.springframework.stereotype.Component;

@Component
public class MakeMoveCommandConfig extends SimpleConsumerConfig{

	public MakeMoveCommandConfig() {
		super();
		super.getProperties().put("group.id", KafkaUtils.MAKE_MOVE_COMMAND_GROUP);
	}
}
