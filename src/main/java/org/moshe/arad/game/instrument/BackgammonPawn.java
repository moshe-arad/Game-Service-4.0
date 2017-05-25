package org.moshe.arad.game.instrument;

import org.moshe.arad.game.move.Move;

public abstract class BackgammonPawn implements Pawn {

	@Override
	public abstract boolean isAbleToDo(Move move) throws Exception;

	public static boolean isWhite(BackgammonPawn pawn) throws Exception{
		if(pawn == null) throw new Exception("pawn is null.");
		return pawn instanceof WhiteBackgammonPawn;
	}
}
