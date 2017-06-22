package org.moshe.arad.kafka.events;

import org.moshe.arad.backgammon.instrument.BackgammonBoard;
import org.moshe.arad.backgammon.instrument.BackgammonDice;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class TurnNotPassedUserMadeMoveEvent extends BackgammonEvent{

	private String userName;
	private String gameRoomName;
	private int from;
	private int to;
	private BackgammonBoard board;
	private BackgammonDice firstDice;
	private BackgammonDice secondDice;
	private boolean isWhite;
	
	public TurnNotPassedUserMadeMoveEvent() {
	
	}

	public TurnNotPassedUserMadeMoveEvent(String userName, String gameRoomName, int from, int to, BackgammonBoard board,
			BackgammonDice firstDice, BackgammonDice secondDice, boolean isWhite) {
		super();
		this.userName = userName;
		this.gameRoomName = gameRoomName;
		this.from = from;
		this.to = to;
		this.board = board;
		this.firstDice = firstDice;
		this.secondDice = secondDice;
		this.isWhite = isWhite;
	}

	@Override
	public String toString() {
		return "TurnNotPassedUserMadeMoveEvent [userName=" + userName + ", gameRoomName=" + gameRoomName + ", from="
				+ from + ", to=" + to + ", board=" + board + ", firstDice=" + firstDice + ", secondDice=" + secondDice
				+ ", isWhite=" + isWhite + "]";
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getGameRoomName() {
		return gameRoomName;
	}

	public void setGameRoomName(String gameRoomName) {
		this.gameRoomName = gameRoomName;
	}

	public int getFrom() {
		return from;
	}

	public void setFrom(int from) {
		this.from = from;
	}

	public int getTo() {
		return to;
	}

	public void setTo(int to) {
		this.to = to;
	}

	public BackgammonBoard getBoard() {
		return board;
	}

	public void setBoard(BackgammonBoard board) {
		this.board = board;
	}

	public BackgammonDice getFirstDice() {
		return firstDice;
	}

	public void setFirstDice(BackgammonDice firstDice) {
		this.firstDice = firstDice;
	}

	public BackgammonDice getSecondDice() {
		return secondDice;
	}

	public void setSecondDice(BackgammonDice secondDice) {
		this.secondDice = secondDice;
	}

	public boolean isWhite() {
		return isWhite;
	}

	public void setWhite(boolean isWhite) {
		this.isWhite = isWhite;
	}
}
