package org.moshe.arad.backgammon;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class Backgammon {

	@Autowired
	private BackgammonDice firstDice;
	
	@Autowired
	private BackgammonDice secondDice;
	
	private Boolean isWhiteTurn;
	private Boolean isBlackTurn;
	
	public Backgammon() {
		isWhiteTurn = true;
		isBlackTurn = false;
	}
	
	public BackgammonDice getFirstDice() {
		return firstDice;
	}
	
	public void setFirstDice(BackgammonDice firstDice) {
		this.firstDice = firstDice;
	}
	
	public BackgammonDice getSecondDice() {
		return secondDice;
	}
	
	public void setSecondDice(BackgammonDice secondDice) {
		this.secondDice = secondDice;
	}

	public Boolean getIsWhiteTurn() {
		return isWhiteTurn;
	}

	public void setIsWhiteTurn(Boolean isWhiteTurn) {
		this.isWhiteTurn = isWhiteTurn;
	}

	public Boolean getIsBlackTurn() {
		return isBlackTurn;
	}

	public void setIsBlackTurn(Boolean isBlackTurn) {
		this.isBlackTurn = isBlackTurn;
	}
}
