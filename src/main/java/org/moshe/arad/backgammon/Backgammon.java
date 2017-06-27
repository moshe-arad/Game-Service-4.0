package org.moshe.arad.backgammon;

import java.util.Arrays;
import java.util.LinkedList;

import org.moshe.arad.backgammon.instrument.BackgammonBoard;
import org.moshe.arad.backgammon.instrument.BackgammonDice;
import org.moshe.arad.backgammon.instrument.Board;
import org.moshe.arad.backgammon.player.BackgammonPlayer;
import org.moshe.arad.backgammon.player.ClassicGamePlayer;
import org.moshe.arad.backgammon.turn.BackgammonTurn;
import org.moshe.arad.backgammon.turn.ClassicGameTurnOrderManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class Backgammon {

	private BackgammonBoard board;	
	private ClassicGameTurnOrderManager<BackgammonPlayer> turnManager;
	private BackgammonPlayer firstPlayer;
	private BackgammonPlayer secondPlayer;
	private Boolean isHasWinner;
	
	public Backgammon() {
		isHasWinner = false;
	}
	
	public Backgammon(BackgammonPlayer first, 
			BackgammonPlayer second,
			@Autowired BackgammonTurn turn,
			@Autowired ClassicGameTurnOrderManager<BackgammonPlayer> turnManager,
			@Autowired BackgammonBoard board){
		
		isHasWinner = false;
		
		//turn contains/holds dice
		firstPlayer.setTurn(turn);
		firstPlayer = first;
		secondPlayer = second;
		this.board = board;
		
		turnManager.getOrder().add(firstPlayer);
		turnManager.getOrder().add(firstPlayer);
		
		this.turnManager = turnManager;
		this.board = board;
	}

	public BackgammonBoard getBoard() {
		return board;
	}

	public void setBoard(BackgammonBoard board) {
		this.board = board;
	}

	public ClassicGameTurnOrderManager<BackgammonPlayer> getTurnManager() {
		return turnManager;
	}

	public void setTurnManager(ClassicGameTurnOrderManager<BackgammonPlayer> turnManager) {
		this.turnManager = turnManager;
	}

	public BackgammonPlayer getFirstPlayer() {
		return firstPlayer;
	}

	public void setFirstPlayer(BackgammonPlayer firstPlayer) {
		this.firstPlayer = firstPlayer;
	}

	public BackgammonPlayer getSecondPlayer() {
		return secondPlayer;
	}

	public void setSecondPlayer(BackgammonPlayer secondPlayer) {
		this.secondPlayer = secondPlayer;
	}

	public Boolean getIsHasWinner() {
		return isHasWinner;
	}

	public void setIsHasWinner(Boolean isHasWinner) {
		this.isHasWinner = isHasWinner;
	}
}
