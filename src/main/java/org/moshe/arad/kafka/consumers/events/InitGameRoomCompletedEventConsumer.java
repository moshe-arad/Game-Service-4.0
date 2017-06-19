package org.moshe.arad.kafka.consumers.events;

import java.io.IOException;
import java.util.Date;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.moshe.arad.kafka.ConsumerToProducerQueue;
import org.moshe.arad.kafka.events.GameStartedEvent;
import org.moshe.arad.kafka.events.InitGameRoomCompletedEvent;
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
public class InitGameRoomCompletedEventConsumer extends SimpleEventsConsumer {

	@Autowired
	private BackgammonGameService backgammonGameService;
	
	@Autowired
	private ApplicationContext context;
	
	private ConsumerToProducerQueue consumerToProducerQueue;
	
	Logger logger = LoggerFactory.getLogger(InitGameRoomCompletedEventConsumer.class);
	
	public InitGameRoomCompletedEventConsumer() {
	}
	
	@Override
	public void consumerOperations(ConsumerRecord<String, String> record) {
		InitGameRoomCompletedEvent initGameRoomCompletedEvent = convertJsonBlobIntoEvent(record.value());
		
		try{
			logger.info("Will start new game..." + initGameRoomCompletedEvent);
			String gameRoomName = initGameRoomCompletedEvent.getGameRoom().getName();
			String openBy = initGameRoomCompletedEvent.getGameRoom().getOpenBy();
			String second = initGameRoomCompletedEvent.getGameRoom().getSecondPlayer();
			backgammonGameService.createNewGame(gameRoomName, openBy, second);
			GameStartedEvent gameStartedEvent = context.getBean(GameStartedEvent.class);
			gameStartedEvent.setUuid(initGameRoomCompletedEvent.getUuid());
			gameStartedEvent.setArrived(new Date());
			gameStartedEvent.setClazz("GameStartedEvent");
			gameStartedEvent.setGameRoom(initGameRoomCompletedEvent.getGameRoom());
			
			consumerToProducerQueue.getEventsQueue().put(gameStartedEvent);
		}
		catch(Exception e){
			logger.error("Failed to add new game room to view...");
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}
	
	private InitGameRoomCompletedEvent convertJsonBlobIntoEvent(String JsonBlob){
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			return objectMapper.readValue(JsonBlob, InitGameRoomCompletedEvent.class);
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
