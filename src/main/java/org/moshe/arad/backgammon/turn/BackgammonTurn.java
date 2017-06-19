package org.moshe.arad.backgammon.turn;

import org.moshe.arad.backgammon.instrument.BackgammonDice;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * a backgammon turn has dices.
 */

public class BackgammonTurn implements Turn {

	private static BackgammonTurn instance;
	
	private BackgammonDice firstDice;
	
	private BackgammonDice secondDice;
	
	
	
	private BackgammonTurn(BackgammonDice firstDice, BackgammonDice secondDice) {
		this.firstDice = firstDice;
		this.secondDice = secondDice;
	}

	public static BackgammonTurn getInstance(BackgammonDice first,
			BackgammonDice second){
		
		if(instance == null){
			synchronized (BackgammonTurn.class) {
				if(instance == null){
					instance = new BackgammonTurn(first, second);
				}
			}
		}
		return instance;
	}

	public BackgammonDice getFirstDice() {
		return firstDice;
	}

	public BackgammonDice getSecondDice() {
		return secondDice;
	}
}
