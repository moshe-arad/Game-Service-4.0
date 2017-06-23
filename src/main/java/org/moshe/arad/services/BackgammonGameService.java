package org.moshe.arad.services;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.moshe.arad.backgammon.Backgammon;
import org.moshe.arad.backgammon.instrument.BackgammonBoard;
import org.moshe.arad.backgammon.instrument.BackgammonDice;
import org.moshe.arad.backgammon.move.BackgammonBoardLocation;
import org.moshe.arad.backgammon.move.Move;
import org.moshe.arad.backgammon.player.BackgammonPlayer;
import org.moshe.arad.backgammon.player.Player;
import org.moshe.arad.backgammon.turn.BackgammonTurn;
import org.moshe.arad.backgammon.turn.ClassicGameTurnOrderManager;
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
		BackgammonTurn turn = context.getBean(BackgammonTurn.class);
		ClassicGameTurnOrderManager<BackgammonPlayer> turnManager = context.getBean(ClassicGameTurnOrderManager.class);
		BackgammonBoard board = context.getBean(BackgammonBoard.class);
		BackgammonPlayer firstPlayer = context.getBean(BackgammonPlayer.class);
		BackgammonPlayer secondPlayer = context.getBean(BackgammonPlayer.class);
		
		firstPlayer.setFirstName(openBy);
		firstPlayer.setWhite(true);
		secondPlayer.setFirstName(second);
		firstPlayer.setTurn(turn);
		
		LinkedList<BackgammonPlayer> order = new LinkedList<>();
		order.add(firstPlayer);
		order.add(secondPlayer);
		
		turnManager.setOrder(order);
		
		backgammon.setFirstPlayer(firstPlayer);
		backgammon.setSecondPlayer(secondPlayer);
		
		backgammon.setTurnManager(turnManager);
		
		board.initBoard();
		backgammon.setBoard(board);
		
		backgammonGames.put(gameRoomName, backgammon);
	}
	
	public boolean initDice(String gameRoomName, String username){
		Backgammon backgammon = backgammonGames.get(gameRoomName);
		BackgammonPlayer player = backgammon.getTurnManager().howHasTurn();
		
		if(username.equals(player.getFirstName())){
			player.getTurn().getFirstDice().initDice();
			player.getTurn().getSecondDice().initDice();
			return true;
		}
		else return false;
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

	public boolean isUserWithTurn(String gameRoomName, String username){
		Backgammon backgammon = backgammonGames.get(gameRoomName);
		BackgammonPlayer withTurn = backgammon.getTurnManager().howHasTurn();
		
		if(withTurn.getFirstName().equals(username)) return true;
		else {
			logger.error("Player does not own turn, can not make move");
			return false;
		}
	}
	
	public boolean makeMove(String username, String gameRoomName, int from, int to) throws Exception {
		Backgammon backgammon = backgammonGames.get(gameRoomName);
		BackgammonPlayer withTurn = backgammon.getTurnManager().howHasTurn();
		BackgammonBoard board = backgammon.getBoard();
		
		logger.info("The board before making move:");
		logger.info(board.toString());
		
		int eatenWhite = board.getWhiteEatenSize();
		int eatenBlack = board.getBlackEatenSize();
		logger.info("Before applying move, white has eaten = " + eatenWhite);
		logger.info("Before applying move, black has eaten = " + eatenBlack);			
		
		Move move = initMove(from, to);
		
		if(board.isValidMove(withTurn, move)){
			logger.info("The move passed validation.");
			backgammon.getBoard().executeMove(withTurn, move);
			withTurn.makePlayed(move);
			
			logger.info("A move was made...");
			logger.info("************************************");
			logger.info("The board after making move:");
			logger.info(board.toString());
			
			return true;
		}
		return false;
	}

	public boolean isCanKeepPlay(String gameRoomName, String username) throws Exception{
		Backgammon backgammon = backgammonGames.get(gameRoomName);
		
		if(isUserWithTurn(gameRoomName, username)){
			BackgammonPlayer withTurn = backgammon.getTurnManager().howHasTurn();
			BackgammonBoard board = backgammon.getBoard();
			
			return isCanKeepPlay(withTurn, board);
		}
		else return false;
	}
	
	public BackgammonBoard getBoard(String gameRoomName){
		return backgammonGames.get(gameRoomName).getBoard();
	}
	
	public boolean getIsWhite(String gameRoomName, String username){
		Backgammon backgammon = backgammonGames.get(gameRoomName);
		BackgammonPlayer player = backgammon.getTurnManager().howHasTurn();
		if(player.getFirstName().equals(username)){
			return player.isWhite();
		}
		else{
			player = backgammon.getTurnManager().howIsNextInTurn();
			return player.isWhite();
		}
	}
	
	public int getWhiteEatenNum(String gameRoomName){
		Backgammon backgammon = backgammonGames.get(gameRoomName);		
		return backgammon.getBoard().getWhiteEatenSize();
	}
	
	public int getBlackEatenNum(String gameRoomName){
		Backgammon backgammon = backgammonGames.get(gameRoomName);		
		return backgammon.getBoard().getBlackEatenSize();
	}
	
	public boolean isHasWinner(String gameRoomName) throws Exception{
		Backgammon backgammon = backgammonGames.get(gameRoomName);
		BackgammonBoard board = backgammon.getBoard();
		
		BackgammonPlayer first = backgammon.getTurnManager().howHasTurn();
		BackgammonPlayer second = backgammon.getTurnManager().howIsNextInTurn();
		return board.isWinner(first) || board.isWinner(second);
	}
	
	public boolean isWhiteCanPlay(String gameRoomName) throws Exception{
		Backgammon backgammon = backgammonGames.get(gameRoomName);
		BackgammonBoard board = backgammon.getBoard();
		
		return board.isWhiteCanPlay();
	}
	
	public boolean isBlackCanPlay(String gameRoomName) throws Exception{
		Backgammon backgammon = backgammonGames.get(gameRoomName);
		BackgammonBoard board = backgammon.getBoard();
		
		return board.isBlackCanPlay();
	}
	
	public void passTurn(String gameRoomName){
		Backgammon backgammon = backgammonGames.get(gameRoomName);
		backgammon.getTurnManager().passTurn();
	}
	
	private Move initMove(int from, int to) {
		Move move = context.getBean(Move.class);
		BackgammonBoardLocation fromLocation = context.getBean(BackgammonBoardLocation.class);
		BackgammonBoardLocation toLocation = context.getBean(BackgammonBoardLocation.class);
		
		fromLocation.setIndex(from);
		toLocation.setIndex(to);
		
		move.setFrom(fromLocation);
		move.setTo(toLocation);
		return move;
	}
	
	private boolean isCanKeepPlay(BackgammonPlayer player, BackgammonBoard board) throws Exception{
		return !board.isWinner(player) && board.isHasMoreMoves(player);
	}
}
