package org.moshe.arad.backgammon.turn;

import org.moshe.arad.backgammon.player.Player;

public interface TurnOrderable <T extends Player> {

	public T howHasTurn();
	
	public boolean passTurn();
	
	public T howIsNextInTurn();
}
