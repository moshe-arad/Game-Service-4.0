package org.moshe.arad.backgammon.player;

import org.moshe.arad.backgammon.instrument.BackgammonPawn;
import org.moshe.arad.backgammon.move.Move;
import org.moshe.arad.backgammon.turn.Turn;

public abstract class ClassicGamePlayer implements Player{

	public abstract void makePlayed(Move move) throws Exception;
	
	public abstract Turn getTurn();

	public abstract void setTurn(Turn turn);
	
	/**
	 * specific for backgammon
	 */
	public abstract void rollDices(); 
	/**
	 * specific for backgammon
	 * @throws Exception 
	 */
	public abstract boolean isCanPlayWith(BackgammonPawn pawn) throws Exception;
}
