package org.moshe.arad.kafka.consumers.events;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

import javax.management.RuntimeErrorException;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.moshe.arad.kafka.ConsumerToProducerQueue;
import org.moshe.arad.kafka.events.BackgammonEvent;
import org.moshe.arad.kafka.events.DiceRolledCanNotPlayEvent;
import org.moshe.arad.kafka.events.DiceRolledEvent;
import org.moshe.arad.kafka.events.GameStartedEvent;
import org.moshe.arad.kafka.events.InitGameRoomCompletedEvent;
import org.moshe.arad.kafka.events.RollDiceGameRoomFoundEvent;
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
public class RollDiceGameRoomFoundEventConsumer extends SimpleEventsConsumer {

	@Autowired
	private BackgammonGameService backgammonGameService;
	
	@Autowired
	private ApplicationContext context;
	
	private Map<Class<? extends BackgammonEvent>,ConsumerToProducerQueue> consumerToProducer;
	
	Logger logger = LoggerFactory.getLogger(RollDiceGameRoomFoundEventConsumer.class);
	
	public RollDiceGameRoomFoundEventConsumer() {
	}
	
	@Override
	public void consumerOperations(ConsumerRecord<String, String> record) {
		RollDiceGameRoomFoundEvent rollDiceGameRoomFoundEvent = convertJsonBlobIntoEvent(record.value());
		String gameRoomName = rollDiceGameRoomFoundEvent.getGameRoom().getName();
		String whiteUserName = rollDiceGameRoomFoundEvent.getGameRoom().getOpenBy();
		String blackUserName = rollDiceGameRoomFoundEvent.getGameRoom().getSecondPlayer();
		
		if((backgammonGameService.isWhiteTurn(gameRoomName) && whiteUserName.equals(rollDiceGameRoomFoundEvent.getUsername())) || 
				(backgammonGameService.isBlackTurn(gameRoomName) && blackUserName.equals(rollDiceGameRoomFoundEvent.getUsername()))){
			
			backgammonGameService.rollDice(gameRoomName);
			try {
				if(backgammonGameService.isCanKeepPlay(gameRoomName, rollDiceGameRoomFoundEvent.getUsername())){
					DiceRolledEvent diceRolledEvent = context.getBean(DiceRolledEvent.class);
					diceRolledEvent.setUuid(rollDiceGameRoomFoundEvent.getUuid());
					diceRolledEvent.setArrived(new Date());
					diceRolledEvent.setClazz("DiceRolledEvent");
					diceRolledEvent.setUsername(rollDiceGameRoomFoundEvent.getUsername());
					diceRolledEvent.setGameRoom(rollDiceGameRoomFoundEvent.getGameRoom());
					diceRolledEvent.setFirstDice(backgammonGameService.getFirstDice(gameRoomName));
					diceRolledEvent.setSecondDice(backgammonGameService.getSecondDice(gameRoomName));
					
					consumerToProducer.get(DiceRolledEvent.class).getEventsQueue().put(diceRolledEvent);
				}
				else{
					backgammonGameService.passTurn(gameRoomName);
					
					DiceRolledCanNotPlayEvent diceRolledCanNotPlayEvent = context.getBean(DiceRolledCanNotPlayEvent.class);
					diceRolledCanNotPlayEvent.setUuid(rollDiceGameRoomFoundEvent.getUuid());
					diceRolledCanNotPlayEvent.setArrived(new Date());
					diceRolledCanNotPlayEvent.setClazz("DiceRolledCanNotPlayEvent");
					diceRolledCanNotPlayEvent.setUsername(rollDiceGameRoomFoundEvent.getUsername());
					diceRolledCanNotPlayEvent.setGameRoom(rollDiceGameRoomFoundEvent.getGameRoom());
					diceRolledCanNotPlayEvent.setFirstDice(backgammonGameService.getFirstDice(gameRoomName));
					diceRolledCanNotPlayEvent.setSecondDice(backgammonGameService.getSecondDice(gameRoomName));
					
					consumerToProducer.get(DiceRolledCanNotPlayEvent.class).getEventsQueue().put(diceRolledCanNotPlayEvent);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		else throw new RuntimeException("User don't own play turn, thus can not roll dice...");
		
	}
	
	private RollDiceGameRoomFoundEvent convertJsonBlobIntoEvent(String JsonBlob){
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			return objectMapper.readValue(JsonBlob, RollDiceGameRoomFoundEvent.class);
		} catch (IOException e) {
			logger.error("Falied to convert Json blob into Event...");
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public void setConsumerToProducerQueue(ConsumerToProducerQueue consumerToProducerQueue) {
		
	}

	public Map<Class<? extends BackgammonEvent>, ConsumerToProducerQueue> getConsumerToProducer() {
		return consumerToProducer;
	}

	public void setConsumerToProducer(Map<Class<? extends BackgammonEvent>, ConsumerToProducerQueue> consumerToProducer) {
		this.consumerToProducer = consumerToProducer;
	}
}
