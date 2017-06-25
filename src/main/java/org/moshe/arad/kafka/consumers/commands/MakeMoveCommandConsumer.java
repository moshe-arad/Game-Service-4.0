package org.moshe.arad.kafka.consumers.commands;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.moshe.arad.backgammon.instrument.BackgammonBoard;
import org.moshe.arad.backgammon.json.BackgammonBoardJson;
import org.moshe.arad.kafka.ConsumerToProducerQueue;
import org.moshe.arad.kafka.commands.MakeMoveCommand;
import org.moshe.arad.kafka.events.BackgammonEvent;
import org.moshe.arad.kafka.events.BlackAteWhitePawnEvent;
import org.moshe.arad.kafka.events.BlackPawnCameBackEvent;
import org.moshe.arad.kafka.events.BlackPawnTakenOutEvent;
import org.moshe.arad.kafka.events.LastMoveBlackAteWhitePawnEvent;
import org.moshe.arad.kafka.events.LastMoveBlackPawnCameBackEvent;
import org.moshe.arad.kafka.events.LastMoveBlackPawnTakenOutEvent;
import org.moshe.arad.kafka.events.LastMoveWhiteAteBlackPawnEvent;
import org.moshe.arad.kafka.events.LastMoveWhitePawnCameBackEvent;
import org.moshe.arad.kafka.events.LastMoveWhitePawnTakenOutEvent;
import org.moshe.arad.kafka.events.TurnNotPassedBlackAteWhitePawnEvent;
import org.moshe.arad.kafka.events.TurnNotPassedBlackPawnCameBackEvent;
import org.moshe.arad.kafka.events.TurnNotPassedBlackPawnTakenOutEvent;
import org.moshe.arad.kafka.events.TurnNotPassedUserMadeMoveEvent;
import org.moshe.arad.kafka.events.TurnNotPassedWhiteAteBlackPawnEvent;
import org.moshe.arad.kafka.events.TurnNotPassedWhitePawnCameBackEvent;
import org.moshe.arad.kafka.events.TurnNotPassedWhitePawnTakenOutEvent;
import org.moshe.arad.kafka.events.UserMadeInvalidMoveEvent;
import org.moshe.arad.kafka.events.UserMadeLastMoveEvent;
import org.moshe.arad.kafka.events.UserMadeMoveEvent;
import org.moshe.arad.kafka.events.WhiteAteBlackPawnEvent;
import org.moshe.arad.kafka.events.WhitePawnCameBackAndAteBlackPawnEvent;
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
		
		if(backgammonGameService.isUserWithTurn(gameRoomName, username)){
			try {
				
				if(backgammonGameService.makeMove(username, gameRoomName, from, to)){
					logger.info("The move was made..");
					
					//TODO if has winner, then handle: winner move made + end game
					
					if(backgammonGameService.isCanKeepPlay(gameRoomName, username)){
						if((from == BackgammonBoard.EATEN_WHITE) && ((blackEaten + 1) == backgammonGameService.getBlackEatenNum(gameRoomName))){
							logger.info("White player returned a white pawn into the game...");
							WhitePawnCameBackAndAteBlackPawnEvent whitePawnCameBackAndAteBlackPawnEvent = context.getBean(WhitePawnCameBackAndAteBlackPawnEvent.class);
							whitePawnCameBackAndAteBlackPawnEvent.setUuid(makeMoveCommand.getUuid());
							whitePawnCameBackAndAteBlackPawnEvent.setArrived(new Date());
							whitePawnCameBackAndAteBlackPawnEvent.setClazz("WhitePawnCameBackAndAteBlackPawnEvent");
							whitePawnCameBackAndAteBlackPawnEvent.setUserName(username);
							whitePawnCameBackAndAteBlackPawnEvent.setGameRoomName(gameRoomName);
							whitePawnCameBackAndAteBlackPawnEvent.setFrom(from);
							whitePawnCameBackAndAteBlackPawnEvent.setTo(to);
							BackgammonBoardJson backgammonBoardJson = context.getBean(BackgammonBoardJson.class);
							backgammonBoardJson.initBoard(backgammonGameService.getBoard(gameRoomName));
							whitePawnCameBackAndAteBlackPawnEvent.setBackgammonBoardJson(backgammonBoardJson);
							whitePawnCameBackAndAteBlackPawnEvent.setFirstDice(backgammonGameService.getFirstDice(gameRoomName));
							whitePawnCameBackAndAteBlackPawnEvent.setSecondDice(backgammonGameService.getSecondDice(gameRoomName));
							whitePawnCameBackAndAteBlackPawnEvent.setWhite(backgammonGameService.getIsWhite(gameRoomName, username));
							
							consumerToProducer.get(WhitePawnCameBackAndAteBlackPawnEvent.class).getEventsQueue().put(whitePawnCameBackAndAteBlackPawnEvent);
						}
						else if(from == BackgammonBoard.EATEN_WHITE){
							logger.info("White player returned a white pawn into the game...");
							WhitePawnCameBackEvent whitePawnCameBackEvent = context.getBean(WhitePawnCameBackEvent.class);
							whitePawnCameBackEvent.setUuid(makeMoveCommand.getUuid());
							whitePawnCameBackEvent.setArrived(new Date());
							whitePawnCameBackEvent.setClazz("WhitePawnCameBackEvent");
							whitePawnCameBackEvent.setUserName(username);
							whitePawnCameBackEvent.setGameRoomName(gameRoomName);
							whitePawnCameBackEvent.setFrom(from);
							whitePawnCameBackEvent.setTo(to);
							BackgammonBoardJson backgammonBoardJson = context.getBean(BackgammonBoardJson.class);
							backgammonBoardJson.initBoard(backgammonGameService.getBoard(gameRoomName));
							whitePawnCameBackEvent.setBackgammonBoardJson(backgammonBoardJson);
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
							BackgammonBoardJson backgammonBoardJson = context.getBean(BackgammonBoardJson.class);
							backgammonBoardJson.initBoard(backgammonGameService.getBoard(gameRoomName));
							blackPawnCameBackEvent.setBackgammonBoardJson(backgammonBoardJson);
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
							BackgammonBoardJson backgammonBoardJson = context.getBean(BackgammonBoardJson.class);
							backgammonBoardJson.initBoard(backgammonGameService.getBoard(gameRoomName));
							whitePawnTakenOutEvent.setBackgammonBoardJson(backgammonBoardJson);
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
							BackgammonBoardJson backgammonBoardJson = context.getBean(BackgammonBoardJson.class);
							backgammonBoardJson.initBoard(backgammonGameService.getBoard(gameRoomName));
							blackPawnTakenOutEvent.setBackgammonBoardJson(backgammonBoardJson);
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
							BackgammonBoardJson backgammonBoardJson = context.getBean(BackgammonBoardJson.class);
							backgammonBoardJson.initBoard(backgammonGameService.getBoard(gameRoomName));
							blackAteWhitePawnEvent.setBackgammonBoardJson(backgammonBoardJson);
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
							BackgammonBoardJson backgammonBoardJson = context.getBean(BackgammonBoardJson.class);
							backgammonBoardJson.initBoard(backgammonGameService.getBoard(gameRoomName));
							whiteAteBlackPawnEvent.setBackgammonBoardJson(backgammonBoardJson);
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
							BackgammonBoardJson backgammonBoardJson = context.getBean(BackgammonBoardJson.class);
							backgammonBoardJson.initBoard(backgammonGameService.getBoard(gameRoomName));
							userMadeMoveEvent.setBackgammonBoardJson(backgammonBoardJson);
							userMadeMoveEvent.setFirstDice(backgammonGameService.getFirstDice(gameRoomName));
							userMadeMoveEvent.setSecondDice(backgammonGameService.getSecondDice(gameRoomName));
							userMadeMoveEvent.setWhite(backgammonGameService.getIsWhite(gameRoomName, username));
							
							consumerToProducer.get(UserMadeMoveEvent.class).getEventsQueue().put(userMadeMoveEvent);
						}
					}
					else{
						if((backgammonGameService.getIsWhite(gameRoomName, username) && !backgammonGameService.isBlackCanPlay(gameRoomName)) ||
								(!backgammonGameService.getIsWhite(gameRoomName, username) && !backgammonGameService.isWhiteCanPlay(gameRoomName))){
				
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
								BackgammonBoardJson backgammonBoardJson = context.getBean(BackgammonBoardJson.class);
								backgammonBoardJson.initBoard(backgammonGameService.getBoard(gameRoomName));
								turnNotPassedWhitePawnCameBackEvent.setBackgammonBoardJson(backgammonBoardJson);
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
								BackgammonBoardJson backgammonBoardJson = context.getBean(BackgammonBoardJson.class);
								backgammonBoardJson.initBoard(backgammonGameService.getBoard(gameRoomName));
								turnNotPassedBlackPawnCameBackEvent.setBackgammonBoardJson(backgammonBoardJson);
								turnNotPassedBlackPawnCameBackEvent.setFirstDice(backgammonGameService.getFirstDice(gameRoomName));
								turnNotPassedBlackPawnCameBackEvent.setSecondDice(backgammonGameService.getSecondDice(gameRoomName));
								turnNotPassedBlackPawnCameBackEvent.setWhite(backgammonGameService.getIsWhite(gameRoomName, username));
								
								consumerToProducer.get(TurnNotPassedBlackPawnCameBackEvent.class).getEventsQueue().put(turnNotPassedBlackPawnCameBackEvent);
							}
							else if(to == BackgammonBoard.OUT_WHITE){
								logger.info("White player took out a white pawn from the board game...");
								TurnNotPassedWhitePawnTakenOutEvent turnNotPassedWhitePawnTakenOutEvent = context.getBean(TurnNotPassedWhitePawnTakenOutEvent.class);
								turnNotPassedWhitePawnTakenOutEvent.setUuid(makeMoveCommand.getUuid());
								turnNotPassedWhitePawnTakenOutEvent.setArrived(new Date());
								turnNotPassedWhitePawnTakenOutEvent.setClazz("TurnNotPassedWhitePawnTakenOutEvent");
								turnNotPassedWhitePawnTakenOutEvent.setUserName(username);
								turnNotPassedWhitePawnTakenOutEvent.setGameRoomName(gameRoomName);
								turnNotPassedWhitePawnTakenOutEvent.setFrom(from);
								turnNotPassedWhitePawnTakenOutEvent.setTo(to);
								BackgammonBoardJson backgammonBoardJson = context.getBean(BackgammonBoardJson.class);
								backgammonBoardJson.initBoard(backgammonGameService.getBoard(gameRoomName));
								turnNotPassedWhitePawnTakenOutEvent.setBackgammonBoardJson(backgammonBoardJson);
								turnNotPassedWhitePawnTakenOutEvent.setFirstDice(backgammonGameService.getFirstDice(gameRoomName));
								turnNotPassedWhitePawnTakenOutEvent.setSecondDice(backgammonGameService.getSecondDice(gameRoomName));
								turnNotPassedWhitePawnTakenOutEvent.setWhite(backgammonGameService.getIsWhite(gameRoomName, username));
								
								consumerToProducer.get(TurnNotPassedWhitePawnTakenOutEvent.class).getEventsQueue().put(turnNotPassedWhitePawnTakenOutEvent);
							}
							else if(to == BackgammonBoard.OUT_BLACK){
								logger.info("Black player took out a black pawn from the board game...");
								TurnNotPassedBlackPawnTakenOutEvent turnNotPassedBlackPawnTakenOutEvent = context.getBean(TurnNotPassedBlackPawnTakenOutEvent.class);
								turnNotPassedBlackPawnTakenOutEvent.setUuid(makeMoveCommand.getUuid());
								turnNotPassedBlackPawnTakenOutEvent.setArrived(new Date());
								turnNotPassedBlackPawnTakenOutEvent.setClazz("TurnNotPassedBlackPawnTakenOutEvent");
								turnNotPassedBlackPawnTakenOutEvent.setUserName(username);
								turnNotPassedBlackPawnTakenOutEvent.setGameRoomName(gameRoomName);
								turnNotPassedBlackPawnTakenOutEvent.setFrom(from);
								turnNotPassedBlackPawnTakenOutEvent.setTo(to);
								BackgammonBoardJson backgammonBoardJson = context.getBean(BackgammonBoardJson.class);
								backgammonBoardJson.initBoard(backgammonGameService.getBoard(gameRoomName));
								turnNotPassedBlackPawnTakenOutEvent.setBackgammonBoardJson(backgammonBoardJson);
								turnNotPassedBlackPawnTakenOutEvent.setFirstDice(backgammonGameService.getFirstDice(gameRoomName));
								turnNotPassedBlackPawnTakenOutEvent.setSecondDice(backgammonGameService.getSecondDice(gameRoomName));
								turnNotPassedBlackPawnTakenOutEvent.setWhite(backgammonGameService.getIsWhite(gameRoomName, username));
								
								consumerToProducer.get(TurnNotPassedBlackPawnTakenOutEvent.class).getEventsQueue().put(turnNotPassedBlackPawnTakenOutEvent);
							}
							else if((whiteEaten + 1) == backgammonGameService.getWhiteEatenNum(gameRoomName)){
								logger.info("Black player ate white pawn...");
								TurnNotPassedBlackAteWhitePawnEvent turnNotPassedBlackAteWhitePawnEvent = context.getBean(TurnNotPassedBlackAteWhitePawnEvent.class);
								turnNotPassedBlackAteWhitePawnEvent.setUuid(makeMoveCommand.getUuid());
								turnNotPassedBlackAteWhitePawnEvent.setArrived(new Date());
								turnNotPassedBlackAteWhitePawnEvent.setClazz("TurnNotPassedBlackAteWhitePawnEvent");
								turnNotPassedBlackAteWhitePawnEvent.setUserName(username);
								turnNotPassedBlackAteWhitePawnEvent.setGameRoomName(gameRoomName);
								turnNotPassedBlackAteWhitePawnEvent.setFrom(from);
								turnNotPassedBlackAteWhitePawnEvent.setTo(to);
								BackgammonBoardJson backgammonBoardJson = context.getBean(BackgammonBoardJson.class);
								backgammonBoardJson.initBoard(backgammonGameService.getBoard(gameRoomName));
								turnNotPassedBlackAteWhitePawnEvent.setBackgammonBoardJson(backgammonBoardJson);
								turnNotPassedBlackAteWhitePawnEvent.setFirstDice(backgammonGameService.getFirstDice(gameRoomName));
								turnNotPassedBlackAteWhitePawnEvent.setSecondDice(backgammonGameService.getSecondDice(gameRoomName));
								turnNotPassedBlackAteWhitePawnEvent.setWhite(backgammonGameService.getIsWhite(gameRoomName, username));
								
								consumerToProducer.get(TurnNotPassedBlackAteWhitePawnEvent.class).getEventsQueue().put(turnNotPassedBlackAteWhitePawnEvent);								
							}
							else if((blackEaten + 1) == backgammonGameService.getBlackEatenNum(gameRoomName)){
								logger.info("White player ate black pawn...");
								TurnNotPassedWhiteAteBlackPawnEvent turnNotPassedWhiteAteBlackPawnEvent = context.getBean(TurnNotPassedWhiteAteBlackPawnEvent.class);
								turnNotPassedWhiteAteBlackPawnEvent.setUuid(makeMoveCommand.getUuid());
								turnNotPassedWhiteAteBlackPawnEvent.setArrived(new Date());
								turnNotPassedWhiteAteBlackPawnEvent.setClazz("TurnNotPassedWhiteAteBlackPawnEvent");
								turnNotPassedWhiteAteBlackPawnEvent.setUserName(username);
								turnNotPassedWhiteAteBlackPawnEvent.setGameRoomName(gameRoomName);
								turnNotPassedWhiteAteBlackPawnEvent.setFrom(from);
								turnNotPassedWhiteAteBlackPawnEvent.setTo(to);
								BackgammonBoardJson backgammonBoardJson = context.getBean(BackgammonBoardJson.class);
								backgammonBoardJson.initBoard(backgammonGameService.getBoard(gameRoomName));
								turnNotPassedWhiteAteBlackPawnEvent.setBackgammonBoardJson(backgammonBoardJson);
								turnNotPassedWhiteAteBlackPawnEvent.setFirstDice(backgammonGameService.getFirstDice(gameRoomName));
								turnNotPassedWhiteAteBlackPawnEvent.setSecondDice(backgammonGameService.getSecondDice(gameRoomName));
								turnNotPassedWhiteAteBlackPawnEvent.setWhite(backgammonGameService.getIsWhite(gameRoomName, username));
								
								consumerToProducer.get(TurnNotPassedWhiteAteBlackPawnEvent.class).getEventsQueue().put(turnNotPassedWhiteAteBlackPawnEvent);								
							}
							else{
								logger.info("Player made a move...");
								TurnNotPassedUserMadeMoveEvent turnNotPassedUserMadeMoveEvent = context.getBean(TurnNotPassedUserMadeMoveEvent.class);
								turnNotPassedUserMadeMoveEvent.setUuid(makeMoveCommand.getUuid());
								turnNotPassedUserMadeMoveEvent.setArrived(new Date());
								turnNotPassedUserMadeMoveEvent.setClazz("TurnNotPassedUserMadeMoveEvent");
								turnNotPassedUserMadeMoveEvent.setUserName(username);
								turnNotPassedUserMadeMoveEvent.setGameRoomName(gameRoomName);
								turnNotPassedUserMadeMoveEvent.setFrom(from);
								turnNotPassedUserMadeMoveEvent.setTo(to);
								BackgammonBoardJson backgammonBoardJson = context.getBean(BackgammonBoardJson.class);
								backgammonBoardJson.initBoard(backgammonGameService.getBoard(gameRoomName));
								turnNotPassedUserMadeMoveEvent.setBackgammonBoardJson(backgammonBoardJson);
								turnNotPassedUserMadeMoveEvent.setFirstDice(backgammonGameService.getFirstDice(gameRoomName));
								turnNotPassedUserMadeMoveEvent.setSecondDice(backgammonGameService.getSecondDice(gameRoomName));
								turnNotPassedUserMadeMoveEvent.setWhite(backgammonGameService.getIsWhite(gameRoomName, username));
								
								consumerToProducer.get(TurnNotPassedUserMadeMoveEvent.class).getEventsQueue().put(turnNotPassedUserMadeMoveEvent);
							}
						}
						else{
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
								BackgammonBoardJson backgammonBoardJson = context.getBean(BackgammonBoardJson.class);
								backgammonBoardJson.initBoard(backgammonGameService.getBoard(gameRoomName));
								lastMoveWhitePawnCameBackEvent.setBackgammonBoardJson(backgammonBoardJson);
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
								BackgammonBoardJson backgammonBoardJson = context.getBean(BackgammonBoardJson.class);
								backgammonBoardJson.initBoard(backgammonGameService.getBoard(gameRoomName));
								lastMoveBlackPawnCameBackEvent.setBackgammonBoardJson(backgammonBoardJson);
								lastMoveBlackPawnCameBackEvent.setFirstDice(backgammonGameService.getFirstDice(gameRoomName));
								lastMoveBlackPawnCameBackEvent.setSecondDice(backgammonGameService.getSecondDice(gameRoomName));
								lastMoveBlackPawnCameBackEvent.setWhite(backgammonGameService.getIsWhite(gameRoomName, username));
								
								consumerToProducer.get(LastMoveBlackPawnCameBackEvent.class).getEventsQueue().put(lastMoveBlackPawnCameBackEvent);
								
								backgammonGameService.passTurn(gameRoomName);
							}
							else if(to == BackgammonBoard.OUT_WHITE){
								logger.info("White player took out a white pawn from the board game...");
								LastMoveWhitePawnTakenOutEvent lastMoveWhitePawnTakenOutEvent = context.getBean(LastMoveWhitePawnTakenOutEvent.class);
								lastMoveWhitePawnTakenOutEvent.setUuid(makeMoveCommand.getUuid());
								lastMoveWhitePawnTakenOutEvent.setArrived(new Date());
								lastMoveWhitePawnTakenOutEvent.setClazz("LastMoveWhitePawnTakenOutEvent");
								lastMoveWhitePawnTakenOutEvent.setUserName(username);
								lastMoveWhitePawnTakenOutEvent.setGameRoomName(gameRoomName);
								lastMoveWhitePawnTakenOutEvent.setFrom(from);
								lastMoveWhitePawnTakenOutEvent.setTo(to);
								BackgammonBoardJson backgammonBoardJson = context.getBean(BackgammonBoardJson.class);
								backgammonBoardJson.initBoard(backgammonGameService.getBoard(gameRoomName));
								lastMoveWhitePawnTakenOutEvent.setBackgammonBoardJson(backgammonBoardJson);
								lastMoveWhitePawnTakenOutEvent.setFirstDice(backgammonGameService.getFirstDice(gameRoomName));
								lastMoveWhitePawnTakenOutEvent.setSecondDice(backgammonGameService.getSecondDice(gameRoomName));
								lastMoveWhitePawnTakenOutEvent.setWhite(backgammonGameService.getIsWhite(gameRoomName, username));
								
								consumerToProducer.get(LastMoveWhitePawnTakenOutEvent.class).getEventsQueue().put(lastMoveWhitePawnTakenOutEvent);
								
								backgammonGameService.passTurn(gameRoomName);
							}
							else if(to == BackgammonBoard.OUT_BLACK){
								logger.info("Black player took out a black pawn from the board game...");
								LastMoveBlackPawnTakenOutEvent lastMoveBlackPawnTakenOutEvent = context.getBean(LastMoveBlackPawnTakenOutEvent.class);
								lastMoveBlackPawnTakenOutEvent.setUuid(makeMoveCommand.getUuid());
								lastMoveBlackPawnTakenOutEvent.setArrived(new Date());
								lastMoveBlackPawnTakenOutEvent.setClazz("LastMoveBlackPawnTakenOutEvent");
								lastMoveBlackPawnTakenOutEvent.setUserName(username);
								lastMoveBlackPawnTakenOutEvent.setGameRoomName(gameRoomName);
								lastMoveBlackPawnTakenOutEvent.setFrom(from);
								lastMoveBlackPawnTakenOutEvent.setTo(to);
								BackgammonBoardJson backgammonBoardJson = context.getBean(BackgammonBoardJson.class);
								backgammonBoardJson.initBoard(backgammonGameService.getBoard(gameRoomName));
								lastMoveBlackPawnTakenOutEvent.setBackgammonBoardJson(backgammonBoardJson);
								lastMoveBlackPawnTakenOutEvent.setFirstDice(backgammonGameService.getFirstDice(gameRoomName));
								lastMoveBlackPawnTakenOutEvent.setSecondDice(backgammonGameService.getSecondDice(gameRoomName));
								lastMoveBlackPawnTakenOutEvent.setWhite(backgammonGameService.getIsWhite(gameRoomName, username));
								
								consumerToProducer.get(LastMoveBlackPawnTakenOutEvent.class).getEventsQueue().put(lastMoveBlackPawnTakenOutEvent);
								
								backgammonGameService.passTurn(gameRoomName);
							}
							else if((whiteEaten + 1) == backgammonGameService.getWhiteEatenNum(gameRoomName)){
								logger.info("Black player ate white pawn...");
								LastMoveBlackAteWhitePawnEvent lastMoveBlackAteWhitePawnEvent = context.getBean(LastMoveBlackAteWhitePawnEvent.class);
								lastMoveBlackAteWhitePawnEvent.setUuid(makeMoveCommand.getUuid());
								lastMoveBlackAteWhitePawnEvent.setArrived(new Date());
								lastMoveBlackAteWhitePawnEvent.setClazz("LastMoveBlackAteWhitePawnEvent");
								lastMoveBlackAteWhitePawnEvent.setUserName(username);
								lastMoveBlackAteWhitePawnEvent.setGameRoomName(gameRoomName);
								lastMoveBlackAteWhitePawnEvent.setFrom(from);
								lastMoveBlackAteWhitePawnEvent.setTo(to);
								BackgammonBoardJson backgammonBoardJson = context.getBean(BackgammonBoardJson.class);
								backgammonBoardJson.initBoard(backgammonGameService.getBoard(gameRoomName));
								lastMoveBlackAteWhitePawnEvent.setBackgammonBoardJson(backgammonBoardJson);
								lastMoveBlackAteWhitePawnEvent.setFirstDice(backgammonGameService.getFirstDice(gameRoomName));
								lastMoveBlackAteWhitePawnEvent.setSecondDice(backgammonGameService.getSecondDice(gameRoomName));
								lastMoveBlackAteWhitePawnEvent.setWhite(backgammonGameService.getIsWhite(gameRoomName, username));
								
								consumerToProducer.get(LastMoveBlackAteWhitePawnEvent.class).getEventsQueue().put(lastMoveBlackAteWhitePawnEvent);
								
								backgammonGameService.passTurn(gameRoomName);
							}
							else if((blackEaten + 1) == backgammonGameService.getBlackEatenNum(gameRoomName)){
								logger.info("White player ate black pawn...");
								LastMoveWhiteAteBlackPawnEvent lastMoveWhiteAteBlackPawnEvent = context.getBean(LastMoveWhiteAteBlackPawnEvent.class);
								lastMoveWhiteAteBlackPawnEvent.setUuid(makeMoveCommand.getUuid());
								lastMoveWhiteAteBlackPawnEvent.setArrived(new Date());
								lastMoveWhiteAteBlackPawnEvent.setClazz("LastMoveWhiteAteBlackPawnEvent");
								lastMoveWhiteAteBlackPawnEvent.setUserName(username);
								lastMoveWhiteAteBlackPawnEvent.setGameRoomName(gameRoomName);
								lastMoveWhiteAteBlackPawnEvent.setFrom(from);
								lastMoveWhiteAteBlackPawnEvent.setTo(to);
								BackgammonBoardJson backgammonBoardJson = context.getBean(BackgammonBoardJson.class);
								backgammonBoardJson.initBoard(backgammonGameService.getBoard(gameRoomName));
								lastMoveWhiteAteBlackPawnEvent.setBackgammonBoardJson(backgammonBoardJson);
								lastMoveWhiteAteBlackPawnEvent.setFirstDice(backgammonGameService.getFirstDice(gameRoomName));
								lastMoveWhiteAteBlackPawnEvent.setSecondDice(backgammonGameService.getSecondDice(gameRoomName));
								lastMoveWhiteAteBlackPawnEvent.setWhite(backgammonGameService.getIsWhite(gameRoomName, username));
								
								consumerToProducer.get(LastMoveWhiteAteBlackPawnEvent.class).getEventsQueue().put(lastMoveWhiteAteBlackPawnEvent);
								
								backgammonGameService.passTurn(gameRoomName);
							}
							else{
								logger.info("Player made a move...");
								UserMadeLastMoveEvent userMadeLastMoveEvent = context.getBean(UserMadeLastMoveEvent.class);
								userMadeLastMoveEvent.setUuid(makeMoveCommand.getUuid());
								userMadeLastMoveEvent.setArrived(new Date());
								userMadeLastMoveEvent.setClazz("UserMadeLastMoveEvent");
								userMadeLastMoveEvent.setUserName(username);
								userMadeLastMoveEvent.setGameRoomName(gameRoomName);
								userMadeLastMoveEvent.setFrom(from);
								userMadeLastMoveEvent.setTo(to);
								BackgammonBoardJson backgammonBoardJson = context.getBean(BackgammonBoardJson.class);
								backgammonBoardJson.initBoard(backgammonGameService.getBoard(gameRoomName));
								userMadeLastMoveEvent.setBackgammonBoardJson(backgammonBoardJson);
								userMadeLastMoveEvent.setFirstDice(backgammonGameService.getFirstDice(gameRoomName));
								userMadeLastMoveEvent.setSecondDice(backgammonGameService.getSecondDice(gameRoomName));
								userMadeLastMoveEvent.setWhite(backgammonGameService.getIsWhite(gameRoomName, username));
								
								consumerToProducer.get(UserMadeLastMoveEvent.class).getEventsQueue().put(userMadeLastMoveEvent);
								
								backgammonGameService.passTurn(gameRoomName);
							}
						}
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
					BackgammonBoardJson backgammonBoardJson = context.getBean(BackgammonBoardJson.class);
					backgammonBoardJson.initBoard(backgammonGameService.getBoard(gameRoomName));
					userMadeInvalidMoveEvent.setBackgammonBoardJson(backgammonBoardJson);
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




	