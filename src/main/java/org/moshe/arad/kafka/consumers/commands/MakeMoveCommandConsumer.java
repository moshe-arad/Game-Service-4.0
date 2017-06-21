package org.moshe.arad.kafka.consumers.commands;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.moshe.arad.backgammon.instrument.BackgammonBoard;
import org.moshe.arad.kafka.ConsumerToProducerQueue;
import org.moshe.arad.kafka.commands.MakeMoveCommand;
import org.moshe.arad.kafka.commands.RollDiceCommand;
import org.moshe.arad.kafka.events.BackgammonEvent;
import org.moshe.arad.kafka.events.BlackAteWhitePawnEvent;
import org.moshe.arad.kafka.events.BlackPawnCameBackEvent;
import org.moshe.arad.kafka.events.BlackPawnTakenOutEvent;
import org.moshe.arad.kafka.events.InitDiceCompletedEvent;
import org.moshe.arad.kafka.events.LastMoveBlackPawnCameBackEvent;
import org.moshe.arad.kafka.events.LastMoveWhitePawnCameBackEvent;
import org.moshe.arad.kafka.events.TurnNotPassedBlackPawnCameBackEvent;
import org.moshe.arad.kafka.events.TurnNotPassedWhitePawnCameBackEvent;
import org.moshe.arad.kafka.events.UserMadeInvalidMoveEvent;
import org.moshe.arad.kafka.events.UserMadeMoveEvent;
import org.moshe.arad.kafka.events.WhiteAteBlackPawnEvent;
import org.moshe.arad.kafka.events.WhitePawnCameBackEvent;
import org.moshe.arad.kafka.events.WhitePawnTakenOutEvent;
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
		int whiteEaten = backgammonGameService.getWhiteEatenNum(gameRoomName);
		int blackEaten = backgammonGameService.getBlackEatenNum(gameRoomName);
		
		//TODO notify to view about eaten pawns
		if(backgammonGameService.isUserWithTurn(gameRoomName, username)){
			try {
				
				if(backgammonGameService.makeMove(username, gameRoomName, from, to)){
					logger.info("The move was made..");
					
					//if has winner, then handle: winner move made + end game
					
					
					//TODO handle eat move events + take out events
					//TODO handle can not play because of eaten can come back
					if(backgammonGameService.isCanKeepPlay(gameRoomName, username)){
						if(from == BackgammonBoard.EATEN_WHITE){
							logger.info("White player returned a white pawn into the game...");
							WhitePawnCameBackEvent whitePawnCameBackEvent = context.getBean(WhitePawnCameBackEvent.class);
							whitePawnCameBackEvent.setUuid(makeMoveCommand.getUuid());
							whitePawnCameBackEvent.setArrived(new Date());
							whitePawnCameBackEvent.setClazz("WhitePawnCameBackEvent");
							whitePawnCameBackEvent.setUserName(username);
							whitePawnCameBackEvent.setGameRoomName(gameRoomName);
							whitePawnCameBackEvent.setFrom(from);
							whitePawnCameBackEvent.setTo(to);
							whitePawnCameBackEvent.setBoard(backgammonGameService.getBoard(gameRoomName));
							whitePawnCameBackEvent.setFirstDice(backgammonGameService.getFirstDice(gameRoomName));
							whitePawnCameBackEvent.setSecondDice(backgammonGameService.getSecondDice(gameRoomName));
							whitePawnCameBackEvent.setWhite(backgammonGameService.getIsWhite(gameRoomName, username));
							
							consumerToProducer.get(WhitePawnCameBackEvent.class).getEventsQueue().put(whitePawnCameBackEvent);
						}
						else if(from == BackgammonBoard.EATEN_BLACK){
							logger.info("Black player returned a black pawn into the game...");
							BlackPawnCameBackEvent blackPawnCameBackEvent = context.getBean(BlackPawnCameBackEvent.class);
							blackPawnCameBackEvent.setUuid(makeMoveCommand.getUuid());
							blackPawnCameBackEvent.setArrived(new Date());
							blackPawnCameBackEvent.setClazz("BlackPawnCameBackEvent");
							blackPawnCameBackEvent.setUserName(username);
							blackPawnCameBackEvent.setGameRoomName(gameRoomName);
							blackPawnCameBackEvent.setFrom(from);
							blackPawnCameBackEvent.setTo(to);
							blackPawnCameBackEvent.setBoard(backgammonGameService.getBoard(gameRoomName));
							blackPawnCameBackEvent.setFirstDice(backgammonGameService.getFirstDice(gameRoomName));
							blackPawnCameBackEvent.setSecondDice(backgammonGameService.getSecondDice(gameRoomName));
							blackPawnCameBackEvent.setWhite(backgammonGameService.getIsWhite(gameRoomName, username));
							
							consumerToProducer.get(BlackPawnCameBackEvent.class).getEventsQueue().put(blackPawnCameBackEvent);
						}
						else if(to == BackgammonBoard.OUT_WHITE){
							logger.info("White player took out a white pawn from the board game...");
							WhitePawnTakenOutEvent whitePawnTakenOutEvent = context.getBean(WhitePawnTakenOutEvent.class);
							whitePawnTakenOutEvent.setUuid(makeMoveCommand.getUuid());
							whitePawnTakenOutEvent.setArrived(new Date());
							whitePawnTakenOutEvent.setClazz("WhitePawnTakenOutEvent");
							whitePawnTakenOutEvent.setUserName(username);
							whitePawnTakenOutEvent.setGameRoomName(gameRoomName);
							whitePawnTakenOutEvent.setFrom(from);
							whitePawnTakenOutEvent.setTo(to);
							whitePawnTakenOutEvent.setBoard(backgammonGameService.getBoard(gameRoomName));
							whitePawnTakenOutEvent.setFirstDice(backgammonGameService.getFirstDice(gameRoomName));
							whitePawnTakenOutEvent.setSecondDice(backgammonGameService.getSecondDice(gameRoomName));
							whitePawnTakenOutEvent.setWhite(backgammonGameService.getIsWhite(gameRoomName, username));
							
							consumerToProducer.get(WhitePawnTakenOutEvent.class).getEventsQueue().put(whitePawnTakenOutEvent);
						}
						else if(to == BackgammonBoard.OUT_BLACK){
							logger.info("Black player took out a black pawn from the board game...");
							BlackPawnTakenOutEvent blackPawnTakenOutEvent = context.getBean(BlackPawnTakenOutEvent.class);
							blackPawnTakenOutEvent.setUuid(makeMoveCommand.getUuid());
							blackPawnTakenOutEvent.setArrived(new Date());
							blackPawnTakenOutEvent.setClazz("BlackPawnTakenOutEvent");
							blackPawnTakenOutEvent.setUserName(username);
							blackPawnTakenOutEvent.setGameRoomName(gameRoomName);
							blackPawnTakenOutEvent.setFrom(from);
							blackPawnTakenOutEvent.setTo(to);
							blackPawnTakenOutEvent.setBoard(backgammonGameService.getBoard(gameRoomName));
							blackPawnTakenOutEvent.setFirstDice(backgammonGameService.getFirstDice(gameRoomName));
							blackPawnTakenOutEvent.setSecondDice(backgammonGameService.getSecondDice(gameRoomName));
							blackPawnTakenOutEvent.setWhite(backgammonGameService.getIsWhite(gameRoomName, username));
							
							consumerToProducer.get(BlackPawnTakenOutEvent.class).getEventsQueue().put(blackPawnTakenOutEvent);
						}
						else if((whiteEaten + 1) == backgammonGameService.getWhiteEatenNum(gameRoomName)){
							logger.info("Black player ate white pawn...");
							BlackAteWhitePawnEvent blackAteWhitePawnEvent = context.getBean(BlackAteWhitePawnEvent.class);
							blackAteWhitePawnEvent.setUuid(makeMoveCommand.getUuid());
							blackAteWhitePawnEvent.setArrived(new Date());
							blackAteWhitePawnEvent.setClazz("BlackAteWhitePawnEvent");
							blackAteWhitePawnEvent.setUserName(username);
							blackAteWhitePawnEvent.setGameRoomName(gameRoomName);
							blackAteWhitePawnEvent.setFrom(from);
							blackAteWhitePawnEvent.setTo(to);
							blackAteWhitePawnEvent.setBoard(backgammonGameService.getBoard(gameRoomName));
							blackAteWhitePawnEvent.setFirstDice(backgammonGameService.getFirstDice(gameRoomName));
							blackAteWhitePawnEvent.setSecondDice(backgammonGameService.getSecondDice(gameRoomName));
							blackAteWhitePawnEvent.setWhite(backgammonGameService.getIsWhite(gameRoomName, username));
							
							consumerToProducer.get(BlackAteWhitePawnEvent.class).getEventsQueue().put(blackAteWhitePawnEvent);
						}
						else if((blackEaten + 1) == backgammonGameService.getBlackEatenNum(gameRoomName)){
							logger.info("White player ate black pawn...");
							WhiteAteBlackPawnEvent whiteAteBlackPawnEvent = context.getBean(WhiteAteBlackPawnEvent.class);
							whiteAteBlackPawnEvent.setUuid(makeMoveCommand.getUuid());
							whiteAteBlackPawnEvent.setArrived(new Date());
							whiteAteBlackPawnEvent.setClazz("WhiteAteBlackPawnEvent");
							whiteAteBlackPawnEvent.setUserName(username);
							whiteAteBlackPawnEvent.setGameRoomName(gameRoomName);
							whiteAteBlackPawnEvent.setFrom(from);
							whiteAteBlackPawnEvent.setTo(to);
							whiteAteBlackPawnEvent.setBoard(backgammonGameService.getBoard(gameRoomName));
							whiteAteBlackPawnEvent.setFirstDice(backgammonGameService.getFirstDice(gameRoomName));
							whiteAteBlackPawnEvent.setSecondDice(backgammonGameService.getSecondDice(gameRoomName));
							whiteAteBlackPawnEvent.setWhite(backgammonGameService.getIsWhite(gameRoomName, username));
							
							consumerToProducer.get(WhiteAteBlackPawnEvent.class).getEventsQueue().put(whiteAteBlackPawnEvent);
						}
						else{
							logger.info("Player made a move...");
							UserMadeMoveEvent userMadeMoveEvent = context.getBean(UserMadeMoveEvent.class);
							userMadeMoveEvent.setUuid(makeMoveCommand.getUuid());
							userMadeMoveEvent.setArrived(new Date());
							userMadeMoveEvent.setClazz("UserMadeMoveEvent");
							userMadeMoveEvent.setUserName(username);
							userMadeMoveEvent.setGameRoomName(gameRoomName);
							userMadeMoveEvent.setFrom(from);
							userMadeMoveEvent.setTo(to);
							userMadeMoveEvent.setBoard(backgammonGameService.getBoard(gameRoomName));
							userMadeMoveEvent.setFirstDice(backgammonGameService.getFirstDice(gameRoomName));
							userMadeMoveEvent.setSecondDice(backgammonGameService.getSecondDice(gameRoomName));
							userMadeMoveEvent.setWhite(backgammonGameService.getIsWhite(gameRoomName, username));
							
							consumerToProducer.get(UserMadeMoveEvent.class).getEventsQueue().put(userMadeMoveEvent);
						}
						//if from = 24 || -1, then handle: eaten pawn came back event // done
						//if to = 24 || -1, then handle: pawn taken was out event //done
						
						//if eaten grew by one then handle: pawn was eaten //done
						
						//**************************************
						//handle user Made Move //done
						//**************************************
					}
					else{
						if((backgammonGameService.getIsWhite(gameRoomName, username) && backgammonGameService.isWhiteCanPlay(gameRoomName)) ||
								(!backgammonGameService.getIsWhite(gameRoomName, username) && backgammonGameService.isBlackCanPlay(gameRoomName))){
							//turn not passed
							if(from == BackgammonBoard.EATEN_WHITE){
								logger.info("White player returned a white pawn into the game...");
								TurnNotPassedWhitePawnCameBackEvent turnNotPassedWhitePawnCameBackEvent = context.getBean(TurnNotPassedWhitePawnCameBackEvent.class);
								turnNotPassedWhitePawnCameBackEvent.setUuid(makeMoveCommand.getUuid());
								turnNotPassedWhitePawnCameBackEvent.setArrived(new Date());
								turnNotPassedWhitePawnCameBackEvent.setClazz("TurnNotPassedWhitePawnCameBackEvent");
								turnNotPassedWhitePawnCameBackEvent.setUserName(username);
								turnNotPassedWhitePawnCameBackEvent.setGameRoomName(gameRoomName);
								turnNotPassedWhitePawnCameBackEvent.setFrom(from);
								turnNotPassedWhitePawnCameBackEvent.setTo(to);
								turnNotPassedWhitePawnCameBackEvent.setBoard(backgammonGameService.getBoard(gameRoomName));
								turnNotPassedWhitePawnCameBackEvent.setFirstDice(backgammonGameService.getFirstDice(gameRoomName));
								turnNotPassedWhitePawnCameBackEvent.setSecondDice(backgammonGameService.getSecondDice(gameRoomName));
								turnNotPassedWhitePawnCameBackEvent.setWhite(backgammonGameService.getIsWhite(gameRoomName, username));
								
								consumerToProducer.get(TurnNotPassedWhitePawnCameBackEvent.class).getEventsQueue().put(turnNotPassedWhitePawnCameBackEvent);
							}
							else if(from == BackgammonBoard.EATEN_BLACK){
								logger.info("Black player returned a black pawn into the game...");
								TurnNotPassedBlackPawnCameBackEvent turnNotPassedBlackPawnCameBackEvent = context.getBean(TurnNotPassedBlackPawnCameBackEvent.class);
								turnNotPassedBlackPawnCameBackEvent.setUuid(makeMoveCommand.getUuid());
								turnNotPassedBlackPawnCameBackEvent.setArrived(new Date());
								turnNotPassedBlackPawnCameBackEvent.setClazz("TurnNotPassedBlackPawnCameBackEvent");
								turnNotPassedBlackPawnCameBackEvent.setUserName(username);
								turnNotPassedBlackPawnCameBackEvent.setGameRoomName(gameRoomName);
								turnNotPassedBlackPawnCameBackEvent.setFrom(from);
								turnNotPassedBlackPawnCameBackEvent.setTo(to);
								turnNotPassedBlackPawnCameBackEvent.setBoard(backgammonGameService.getBoard(gameRoomName));
								turnNotPassedBlackPawnCameBackEvent.setFirstDice(backgammonGameService.getFirstDice(gameRoomName));
								turnNotPassedBlackPawnCameBackEvent.setSecondDice(backgammonGameService.getSecondDice(gameRoomName));
								turnNotPassedBlackPawnCameBackEvent.setWhite(backgammonGameService.getIsWhite(gameRoomName, username));
								
								consumerToProducer.get(TurnNotPassedBlackPawnCameBackEvent.class).getEventsQueue().put(turnNotPassedBlackPawnCameBackEvent);
							}
						}
						else{
							//turn passed
							if(from == BackgammonBoard.EATEN_WHITE){
								logger.info("White player returned a white pawn into the game...");
								LastMoveWhitePawnCameBackEvent lastMoveWhitePawnCameBackEvent = context.getBean(LastMoveWhitePawnCameBackEvent.class);
								lastMoveWhitePawnCameBackEvent.setUuid(makeMoveCommand.getUuid());
								lastMoveWhitePawnCameBackEvent.setArrived(new Date());
								lastMoveWhitePawnCameBackEvent.setClazz("LastMoveWhitePawnCameBackEvent");
								lastMoveWhitePawnCameBackEvent.setUserName(username);
								lastMoveWhitePawnCameBackEvent.setGameRoomName(gameRoomName);
								lastMoveWhitePawnCameBackEvent.setFrom(from);
								lastMoveWhitePawnCameBackEvent.setTo(to);
								lastMoveWhitePawnCameBackEvent.setBoard(backgammonGameService.getBoard(gameRoomName));
								lastMoveWhitePawnCameBackEvent.setFirstDice(backgammonGameService.getFirstDice(gameRoomName));
								lastMoveWhitePawnCameBackEvent.setSecondDice(backgammonGameService.getSecondDice(gameRoomName));
								lastMoveWhitePawnCameBackEvent.setWhite(backgammonGameService.getIsWhite(gameRoomName, username));
								
								consumerToProducer.get(LastMoveWhitePawnCameBackEvent.class).getEventsQueue().put(lastMoveWhitePawnCameBackEvent);
								
								backgammonGameService.passTurn(gameRoomName);
							}
							else if(from == BackgammonBoard.EATEN_BLACK){
								logger.info("Black player returned a black pawn into the game...");
								LastMoveBlackPawnCameBackEvent lastMoveBlackPawnCameBackEvent = context.getBean(LastMoveBlackPawnCameBackEvent.class);
								lastMoveBlackPawnCameBackEvent.setUuid(makeMoveCommand.getUuid());
								lastMoveBlackPawnCameBackEvent.setArrived(new Date());
								lastMoveBlackPawnCameBackEvent.setClazz("LastMoveBlackPawnCameBackEvent");
								lastMoveBlackPawnCameBackEvent.setUserName(username);
								lastMoveBlackPawnCameBackEvent.setGameRoomName(gameRoomName);
								lastMoveBlackPawnCameBackEvent.setFrom(from);
								lastMoveBlackPawnCameBackEvent.setTo(to);
								lastMoveBlackPawnCameBackEvent.setBoard(backgammonGameService.getBoard(gameRoomName));
								lastMoveBlackPawnCameBackEvent.setFirstDice(backgammonGameService.getFirstDice(gameRoomName));
								lastMoveBlackPawnCameBackEvent.setSecondDice(backgammonGameService.getSecondDice(gameRoomName));
								lastMoveBlackPawnCameBackEvent.setWhite(backgammonGameService.getIsWhite(gameRoomName, username));
								
								consumerToProducer.get(LastMoveBlackPawnCameBackEvent.class).getEventsQueue().put(lastMoveBlackPawnCameBackEvent);
								
								backgammonGameService.passTurn(gameRoomName);
							}
						}
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




	