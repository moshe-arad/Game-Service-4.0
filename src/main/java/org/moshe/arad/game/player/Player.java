package org.moshe.arad.game.player;

import org.moshe.arad.game.instrument.BackgammonPawn;
import org.moshe.arad.game.move.Move;
import org.moshe.arad.game.turn.Turn;

/**
 * 
 * @author moshe-arad
 *
 */
public interface Player {
	
	public void makePlayed(Move move) throws Exception;
	
	public Turn getTurn();

	public void setTurn(Turn turn);
	
	/**
	 * specific for backgammon
	 */
	public void rollDices(); 
	/**
	 * specific for backgammon
	 * @throws Exception 
	 */
	public boolean isCanPlayWith(BackgammonPawn pawn) throws Exception;
}
