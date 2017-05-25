package org.moshe.arad.game.turn;

import org.moshe.arad.game.player.Player;

public interface TurnOrderable {

	public Player howHasTurn();
	
	public boolean passTurn();
	
	public Player howIsNextInTurn();
}
