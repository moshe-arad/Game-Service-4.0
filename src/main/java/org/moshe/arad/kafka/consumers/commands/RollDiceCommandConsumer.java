package org.moshe.arad.kafka.consumers.commands;

import java.io.IOException;
import java.util.Date;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.moshe.arad.kafka.ConsumerToProducerQueue;
import org.moshe.arad.kafka.commands.RollDiceCommand;
import org.moshe.arad.kafka.events.InitDiceCompletedEvent;
import org.moshe.arad.services.BackgammonGameService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
@Scope("prototype")
public class RollDiceCommandConsumer extends SimpleCommandsConsumer {	

	@Autowired
	private BackgammonGameService backgammonGameService;
	
	private Logger logger = LoggerFactory.getLogger(RollDiceCommandConsumer.class);
	
	private ConsumerToProducerQueue consumerToProducerQueue;
	
	@Autowired
	private ApplicationContext context;
	
	public RollDiceCommandConsumer() {
	}

	@Override
	public void consumerOperations(ConsumerRecord<String, String> record) {
		RollDiceCommand rollDiceCommand = convertJsonBlobIntoEvent(record.value());
		backgammonGameService.initDice(rollDiceCommand.getGameRoomName());
		
		InitDiceCompletedEvent initDiceCompletedEvent = context.getBean(InitDiceCompletedEvent.class);
		initDiceCompletedEvent.setUuid(rollDiceCommand.getUuid());
		initDiceCompletedEvent.setArrived(new Date());
		initDiceCompletedEvent.setClazz("InitDiceCompletedEvent");
		initDiceCompletedEvent.setGameRoomName(rollDiceCommand.getGameRoomName());
		initDiceCompletedEvent.setUsername(rollDiceCommand.getUsername());
		
		consumerToProducerQueue.getEventsQueue().put(initDiceCompletedEvent);
	}
	
	private RollDiceCommand convertJsonBlobIntoEvent(String JsonBlob){
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			return objectMapper.readValue(JsonBlob, RollDiceCommand.class);
		} catch (IOException e) {
			logger.error("Falied to convert Json blob into Event...");
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void setConsumerToProducerQueue(ConsumerToProducerQueue consumerToProducerQueue) {
		this.consumerToProducerQueue = consumerToProducerQueue;
	}
}




	