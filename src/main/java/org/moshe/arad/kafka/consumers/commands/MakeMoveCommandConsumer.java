package org.moshe.arad.kafka.consumers.commands;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.moshe.arad.kafka.ConsumerToProducerQueue;
import org.moshe.arad.kafka.commands.MakeMoveCommand;
import org.moshe.arad.kafka.commands.RollDiceCommand;
import org.moshe.arad.kafka.events.BackgammonEvent;
import org.moshe.arad.kafka.events.InitDiceCompletedEvent;
import org.moshe.arad.kafka.events.UserMadeInvalidMoveEvent;
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
	
	@Autowired
	private ApplicationContext context;
	
	private Map<Class<? extends BackgammonEvent>,ConsumerToProducerQueue> consumerToProducer;
	
	public MakeMoveCommandConsumer() {
	}

	@Override
	public void consumerOperations(ConsumerRecord<String, String> record) {
		MakeMoveCommand makeMoveCommand = convertJsonBlobIntoEvent(record.value());
		String gameRoomName = makeMoveCommand.getGameRoomName();
		String username = makeMoveCommand.getUsername();
		int from = makeMoveCommand.getFrom();
		int to = makeMoveCommand.getTo();
		
		//TODO notify to view about eaten pawns
		if(backgammonGameService.isUserWithTurn(gameRoomName, username)){
			try {
				
				if(backgammonGameService.makeMove(username, gameRoomName, from, to)){
					logger.info("The move was made..");
					
					//TODO handle eat move events + take out events
					//TODO handle can not play because of eaten can come back
					if(backgammonGameService.isCanKeepPlay(gameRoomName, username)){
						//if from = 24 || -1, then handle: eaten pawn came back event
						//if to = 24 || -1, then handle: pawn taken was out event
						
						//if eaten grew by one then handle: pawn was eaten
						
						//**************************************
						//handle user Made Move
						//**************************************
					}
					else{
						//if has winner, then handle: winner move made + end game
						
						//else:
						
						//if from = 24 || -1, then handle: eaten pawn came back event
						//if to = 24 || -1, then handle: pawn taken was out event
						
						//if eaten grew by one then handle: pawn was eaten
						
						//**************************************
						//handle user Made Last Move event + 
						// pass turn event ##only if## next player can make move. if can not save: move not made event  
						//**************************************
					}
				}
				else{
					logger.info("User made an invalid move...");
					UserMadeInvalidMoveEvent userMadeInvalidMoveEvent = context.getBean(UserMadeInvalidMoveEvent.class);
					userMadeInvalidMoveEvent.setUuid(makeMoveCommand.getUuid());
					userMadeInvalidMoveEvent.setArrived(new Date());
					userMadeInvalidMoveEvent.setClazz("UserMadeInvalidMoveEvent");
					userMadeInvalidMoveEvent.setUserName(username);
					userMadeInvalidMoveEvent.setGameRoomName(gameRoomName);
					userMadeInvalidMoveEvent.setFrom(from);
					userMadeInvalidMoveEvent.setTo(to);
					userMadeInvalidMoveEvent.setBoard(backgammonGameService.getBoard(gameRoomName));
					userMadeInvalidMoveEvent.setFirstDice(backgammonGameService.getFirstDice(gameRoomName));
					userMadeInvalidMoveEvent.setSecondDice(backgammonGameService.getSecondDice(gameRoomName));
					userMadeInvalidMoveEvent.setWhite(backgammonGameService.getIsWhite(gameRoomName, username));
					
					consumerToProducer.get(UserMadeInvalidMoveEvent.class).getEventsQueue().put(userMadeInvalidMoveEvent);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else throw new RuntimeException("The turn not belongs to user... ");
		
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
		
	}

	public Map<Class<? extends BackgammonEvent>, ConsumerToProducerQueue> getConsumerToProducer() {
		return consumerToProducer;
	}

	public void setConsumerToProducer(Map<Class<? extends BackgammonEvent>, ConsumerToProducerQueue> consumerToProducer) {
		this.consumerToProducer = consumerToProducer;
	}
}




	