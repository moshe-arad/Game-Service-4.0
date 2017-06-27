package org.moshe.arad.kafka.consumers.events;

import java.io.IOException;
import java.util.Date;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.moshe.arad.backgammon.json.BackgammonBoardJson;
import org.moshe.arad.kafka.ConsumerToProducerQueue;
import org.moshe.arad.kafka.events.GameStartedEvent;
import org.moshe.arad.kafka.events.GameStoppedEvent;
import org.moshe.arad.kafka.events.InitGameRoomCompletedEvent;
import org.moshe.arad.kafka.events.LoggedOutOpenByLeftFirstEvent;
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
public class LoggedOutOpenByLeftFirstEventConsumer extends SimpleEventsConsumer {

	@Autowired
	private BackgammonGameService backgammonGameService;
	
	@Autowired
	private ApplicationContext context;
	
	private ConsumerToProducerQueue consumerToProducerQueue;
	
	Logger logger = LoggerFactory.getLogger(LoggedOutOpenByLeftFirstEventConsumer.class);
	
	public LoggedOutOpenByLeftFirstEventConsumer() {
	}
	
	@Override
	public void consumerOperations(ConsumerRecord<String, String> record) {
		LoggedOutOpenByLeftFirstEvent loggedOutOpenByLeftFirstEvent = convertJsonBlobIntoEvent(record.value());
		
		try{
			logger.info("Will stop game..." + loggedOutOpenByLeftFirstEvent);
			String gameRoomName = loggedOutOpenByLeftFirstEvent.getGameRoom().getName();
			backgammonGameService.setGameStopped(gameRoomName);
			GameStoppedEvent gameStoppedEvent = context.getBean(GameStoppedEvent.class);
			gameStoppedEvent.setUuid(loggedOutOpenByLeftFirstEvent.getUuid());
			gameStoppedEvent.setArrived(new Date());
			gameStoppedEvent.setClazz("GameStoppedEvent");
			gameStoppedEvent.setUserName(loggedOutOpenByLeftFirstEvent.getOpenBy());
			gameStoppedEvent.setGameRoom(loggedOutOpenByLeftFirstEvent.getGameRoom());
			BackgammonBoardJson backgammonBoardJson = context.getBean(BackgammonBoardJson.class);
			backgammonBoardJson.initBoard(backgammonGameService.getBoard(gameRoomName));
			gameStoppedEvent.setBackgammonBoardJson(backgammonBoardJson);
			gameStoppedEvent.setFirstDice(backgammonGameService.getFirstDice(gameRoomName));
			gameStoppedEvent.setSecondDice(backgammonGameService.getSecondDice(gameRoomName));
			
			consumerToProducerQueue.getEventsQueue().put(gameStoppedEvent);
		}
		catch(Exception e){
			logger.error("Failed to add new game room to view...");
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}
	
	private LoggedOutOpenByLeftFirstEvent convertJsonBlobIntoEvent(String JsonBlob){
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			return objectMapper.readValue(JsonBlob, LoggedOutOpenByLeftFirstEvent.class);
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
