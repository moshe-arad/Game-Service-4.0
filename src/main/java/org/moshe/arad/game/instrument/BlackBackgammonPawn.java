package org.moshe.arad.game.instrument;

import org.moshe.arad.game.move.BackgammonBoardLocation;
import org.moshe.arad.game.move.Move;

public class BlackBackgammonPawn extends BackgammonPawn{

	/**
	 * assumes move is between 24 to -1.
	 * @throws Exception 
	 */
	@Override
	public boolean isAbleToDo(Move move) throws Exception {
		if(move == null) throw new Exception("move is null.");
		return (((BackgammonBoardLocation)move.getFrom()).getIndex() - ((BackgammonBoardLocation)move.getTo()).getIndex() < 0);
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof BlackBackgammonPawn;
	}	
}
