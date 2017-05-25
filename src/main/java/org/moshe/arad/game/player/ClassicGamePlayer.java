package org.moshe.arad.game.player;

import org.moshe.arad.game.instrument.BackgammonPawn;
import org.moshe.arad.game.move.Move;
import org.moshe.arad.game.turn.Turn;

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
