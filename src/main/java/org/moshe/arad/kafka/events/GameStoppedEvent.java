package org.moshe.arad.kafka.events;

import org.moshe.arad.backgammon.instrument.BackgammonDice;
import org.moshe.arad.backgammon.json.BackgammonBoardJson;
import org.moshe.arad.entities.GameRoom;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class GameStoppedEvent extends BackgammonEvent{

	private String userName;
	private GameRoom gameRoom;
	private BackgammonBoardJson backgammonBoardJson;
	private BackgammonDice firstDice;
	private BackgammonDice secondDice;
	private boolean isWhite;
	
	public GameStoppedEvent() {
	
	}

	public GameStoppedEvent(String userName, GameRoom gameRoom, BackgammonBoardJson backgammonBoardJson,
			BackgammonDice firstDice, BackgammonDice secondDice, boolean isWhite) {
		super();
		this.userName = userName;
		this.gameRoom = gameRoom;
		this.backgammonBoardJson = backgammonBoardJson;
		this.firstDice = firstDice;
		this.secondDice = secondDice;
		this.isWhite = isWhite;
	}

	@Override
	public String toString() {
		return "GameStoppedEvent [userName=" + userName + ", gameRoom=" + gameRoom + ", backgammonBoardJson="
				+ backgammonBoardJson + ", firstDice=" + firstDice + ", secondDice=" + secondDice + ", isWhite="
				+ isWhite + "]";
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public GameRoom getGameRoom() {
		return gameRoom;
	}

	public void setGameRoom(GameRoom gameRoom) {
		this.gameRoom = gameRoom;
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
