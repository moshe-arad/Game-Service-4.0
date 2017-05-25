package org.moshe.arad.game.player;

import org.moshe.arad.game.instrument.BackgammonDice;
import org.moshe.arad.game.instrument.BackgammonPawn;
import org.moshe.arad.game.instrument.Dice;
import org.moshe.arad.game.move.BackgammonBoardLocation;
import org.moshe.arad.game.move.Move;
import org.moshe.arad.game.turn.BackgammonTurn;
import org.moshe.arad.game.turn.Turn;

public class BackgammonPlayer extends ClassicGamePlayer {

	private String firstName;
	private String lastName;
	@SuppressWarnings("unused")
	private int age;
	private BackgammonTurn turn;
	private boolean isWhite;

	
	public BackgammonPlayer(String firstName, String lastName, int age, BackgammonTurn turn,
			boolean isWhite) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.age = age;
		this.turn = turn;
		this.isWhite = isWhite;
	}

	@Override
	public void makePlayed(Move move) throws Exception {
		if(move == null) throw new Exception("move is null.");
		Dice first = turn.getFirstDice();
		Dice second = turn.getSecondDice();
		int fromIndex = ((BackgammonBoardLocation)move.getFrom()).getIndex();
		int toIndex = ((BackgammonBoardLocation)move.getTo()).getIndex();
		int step = fromIndex - toIndex < 0 ? (fromIndex - toIndex)*(-1) : fromIndex - toIndex;
		
		if(first.getValue() == step) first.initDice();
		else if(second.getValue() == step) second.initDice();
		else if(first.getValue() > second.getValue()) first.initDice();
		else second.initDice();
	}

	@Override
	public void rollDices() { 
		turn.getFirstDice().rollDice();
		turn.getSecondDice().rollDice();
		doubleDices();
	}

	@Override
	public boolean isCanPlayWith(BackgammonPawn pawn) throws Exception {
		if(pawn == null) throw new Exception("pawn is null.");
		return ((isWhite && BackgammonPawn.isWhite(pawn)) ||
				(!isWhite && !BackgammonPawn.isWhite(pawn)));
	}

	@Override
	public BackgammonTurn getTurn() {
		return turn;
	}

	@Override
	public void setTurn(Turn turn) {
		this.turn = (BackgammonTurn)turn;
	}
	
	public boolean isWhite() {
		return isWhite;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}
	
	private void doubleDices() {
		if(turn.getFirstDice().getValue() == turn.getSecondDice().getValue()){
			turn.getFirstDice().setTimes(BackgammonDice.DOUBLE);
			turn.getSecondDice().setTimes(BackgammonDice.DOUBLE);
		}
	}
}
