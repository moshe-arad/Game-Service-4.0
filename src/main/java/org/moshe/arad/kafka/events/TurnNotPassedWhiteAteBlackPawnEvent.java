package org.moshe.arad.kafka.events;

import org.moshe.arad.backgammon.instrument.BackgammonBoard;
import org.moshe.arad.backgammon.instrument.BackgammonDice;
import org.moshe.arad.backgammon.json.BackgammonBoardJson;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class TurnNotPassedWhiteAteBlackPawnEvent extends BackgammonEvent{

	private String userName;
	private String gameRoomName;
	private int from;
	private int to;
	private BackgammonBoardJson backgammonBoardJson;
	private BackgammonDice firstDice;
	private BackgammonDice secondDice;
	private boolean isWhite;
	
	public TurnNotPassedWhiteAteBlackPawnEvent() {
	
	}

	public TurnNotPassedWhiteAteBlackPawnEvent(String userName, String gameRoomName, int from, int to,
			BackgammonBoardJson backgammonBoardJson, BackgammonDice firstDice, BackgammonDice secondDice,
			boolean isWhite) {
		super();
		this.userName = userName;
		this.gameRoomName = gameRoomName;
		this.from = from;
		this.to = to;
		this.backgammonBoardJson = backgammonBoardJson;
		this.firstDice = firstDice;
		this.secondDice = secondDice;
		this.isWhite = isWhite;
	}

	@Override
	public String toString() {
		return "TurnNotPassedWhiteAteBlackPawnEvent [userName=" + userName + ", gameRoomName=" + gameRoomName
				+ ", from=" + from + ", to=" + to + ", backgammonBoardJson=" + backgammonBoardJson + ", firstDice="
				+ firstDice + ", secondDice=" + secondDice + ", isWhite=" + isWhite + "]";
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

	public BackgammonBoardJson getBackgammonBoardJson() {
		return backgammonBoardJson;
	}

	public void setBackgammonBoardJson(BackgammonBoardJson backgammonBoardJson) {
		this.backgammonBoardJson = backgammonBoardJson;
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
