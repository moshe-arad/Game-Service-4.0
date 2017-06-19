package org.moshe.arad.services;

import java.util.HashMap;
import java.util.Map;

import org.moshe.arad.backgammon.Backgammon;
import org.moshe.arad.backgammon.instrument.BackgammonDice;
import org.moshe.arad.backgammon.move.BackgammonBoardLocation;
import org.moshe.arad.backgammon.move.Move;
import org.moshe.arad.backgammon.player.BackgammonPlayer;
import org.moshe.arad.backgammon.player.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;


@Service
public class BackgammonGameService {

	private Map<String,Backgammon> backgammonGames = new HashMap<>(100000);
	
	@Autowired
	private ApplicationContext context;
	
	private Logger logger = LoggerFactory.getLogger(BackgammonGameService.class);
	
	public void createNewGame(String gameRoomName, String openBy, String second){
		Backgammon backgammon = context.getBean(Backgammon.class);
		backgammon.getFirstPlayer().setFirstName(openBy);
		backgammon.getSecondPlayer().setFirstName(second);
		backgammonGames.put(gameRoomName, backgammon);
	}
	
	public void initDice(String gameRoomName){
		BackgammonPlayer player = backgammonGames.get(gameRoomName).getTurnManager().howHasTurn();		
		player.getTurn().getFirstDice().initDice();
		player.getTurn().getSecondDice().initDice();
	}
	
	public void rollDice(String gameRoomName){
		BackgammonPlayer player = backgammonGames.get(gameRoomName).getTurnManager().howHasTurn();
		
		player.getTurn().getFirstDice().rollDice();
		player.getTurn().getSecondDice().rollDice();
	}
	
	public BackgammonDice getFirstDice(String gameRoomName){
		BackgammonPlayer player = backgammonGames.get(gameRoomName).getTurnManager().howHasTurn();
		return player.getTurn().getFirstDice();
	}
	
	public BackgammonDice getSecondDice(String gameRoomName){
		BackgammonPlayer player = backgammonGames.get(gameRoomName).getTurnManager().howHasTurn();
		return player.getTurn().getSecondDice();
	}
	
	public Boolean isWhiteTurn(String gameRoomName){
		return backgammonGames.get(gameRoomName).getTurnManager().howHasTurn().isWhite();
	}
	
	public Boolean isBlackTurn(String gameRoomName){
		return !backgammonGames.get(gameRoomName).getTurnManager().howHasTurn().isWhite();
	}

	public void makeMove(String username, String gameRoomName, int from, int to) throws Exception {
//		Backgammon backgammon = backgammonGames.get(gameRoomName);
//		BackgammonPlayer withTurn = backgammon.getTurnManager().howHasTurn();
//		
//		if(!withTurn.getFirstName().equals(username)){
//			logger.error("Player does not own turn, can not make move");
//			return false;
//		}
//		else if(!backgammon.getBoard().isWinner(withTurn) && backgammon.getBoard().isHasMoreMoves(withTurn)){
//			Move move = context.getBean(Move.class);
//			BackgammonBoardLocation fromLocation = context.getBean(BackgammonBoardLocation.class);
//			BackgammonBoardLocation toLocation = context.getBean(BackgammonBoardLocation.class);
//			
//			fromLocation.setIndex(from);
//			toLocation.setIndex(to);
//			
//			move.setFrom(fromLocation);
//			move.setTo(toLocation);
//			
//			if(backgammon.getBoard().isValidMove(withTurn, move)){
//				backgammon.getBoard().executeMove(withTurn, move);
//				withTurn.makePlayed(move);
//			}
//			return false;
//		}
//		else return false;
	}
}
