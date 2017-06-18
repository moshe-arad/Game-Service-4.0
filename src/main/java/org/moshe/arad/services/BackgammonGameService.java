package org.moshe.arad.services;

import java.util.HashMap;
import java.util.Map;

import org.moshe.arad.backgammon.Backgammon;
import org.moshe.arad.backgammon.BackgammonDice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Service
public class BackgammonGameService {

	private Map<String,Backgammon> backgammonGames = new HashMap<>(100000);
	
	@Autowired
	private ApplicationContext context;
	
	public void createNewGame(String gameRoomName){
		backgammonGames.put(gameRoomName, context.getBean(Backgammon.class));
	}
	
	public void initDice(String gameRoomName){
		backgammonGames.get(gameRoomName).getFirstDice().initDice();
		backgammonGames.get(gameRoomName).getSecondDice().initDice();
	}
	
	public void rollDice(String gameRoomName){
		backgammonGames.get(gameRoomName).getFirstDice().rollDice();
		backgammonGames.get(gameRoomName).getSecondDice().rollDice();
	}
	
	public BackgammonDice getFirstDice(String gameRoomName){
		return backgammonGames.get(gameRoomName).getFirstDice();
	}
	
	public BackgammonDice getSecondDice(String gameRoomName){
		return backgammonGames.get(gameRoomName).getSecondDice();
	}
	
	public Boolean isWhiteTurn(String gameRoomName){
		return backgammonGames.get(gameRoomName).getIsWhiteTurn();
	}
	
	public Boolean isBlackTurn(String gameRoomName){
		return backgammonGames.get(gameRoomName).getIsBlackTurn();
	}
}
