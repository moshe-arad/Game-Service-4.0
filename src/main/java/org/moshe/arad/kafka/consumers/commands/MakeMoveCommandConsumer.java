package org.moshe.arad.kafka.consumers.commands;

import java.io.IOException;
import java.util.Date;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.moshe.arad.kafka.ConsumerToProducerQueue;
import org.moshe.arad.kafka.commands.MakeMoveCommand;
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
public class MakeMoveCommandConsumer extends SimpleCommandsConsumer {	

	@Autowired
	private BackgammonGameService backgammonGameService;
	
	private Logger logger = LoggerFactory.getLogger(MakeMoveCommandConsumer.class);
	
	private ConsumerToProducerQueue consumerToProducerQueue;
	
	@Autowired
	private ApplicationContext context;
	
	public MakeMoveCommandConsumer() {
	}

	@Override
	public void consumerOperations(ConsumerRecord<String, String> record) {
		MakeMoveCommand makeMoveCommand = convertJsonBlobIntoEvent(record.value());
		
//		backgammonGameService.makeMove(makeMoveCommand.getUsername(), makeMoveCommand.getGameRoomName(), makeMoveCommand.getFrom(), makeMoveCommand.getTo());
	}
	
	private MakeMoveCommand convertJsonBlobIntoEvent(String JsonBlob){
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			return objectMapper.readValue(JsonBlob, MakeMoveCommand.class);
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




	