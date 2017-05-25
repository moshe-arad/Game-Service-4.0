package org.moshe.arad.game.turn;

import javax.annotation.Resource;

import org.moshe.arad.game.instrument.BackgammonDice;

/**
 * a backgammon turn has dices.
 */
public class BackgammonTurn implements Turn {

	private static BackgammonTurn instance;
	@Resource
	private BackgammonDice firstDice;
	@Resource
	private BackgammonDice secondDice;
	
	
	
	public BackgammonTurn(BackgammonDice firstDice, BackgammonDice secondDice) {
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
