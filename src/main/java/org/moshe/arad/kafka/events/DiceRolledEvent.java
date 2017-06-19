package org.moshe.arad.kafka.events;

import java.util.Date;
import java.util.UUID;

import org.moshe.arad.backgammon.instrument.BackgammonDice;
import org.moshe.arad.entities.GameRoom;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class DiceRolledEvent extends BackgammonEvent{

	private String username;
	private GameRoom gameRoom;
	private BackgammonDice firstDice;
	private BackgammonDice secondDice;
	
	public DiceRolledEvent() {
	
	}

	public DiceRolledEvent(String username, GameRoom gameRoom, BackgammonDice firstDice, BackgammonDice secondDice) {
		super();
		this.username = username;
		this.gameRoom = gameRoom;
		this.firstDice = firstDice;
		this.secondDice = secondDice;
	}

	@Override
	public String toString() {
		return "DiceRolledEvent [username=" + username + ", gameRoom=" + gameRoom + ", firstDice=" + firstDice
				+ ", secondDice=" + secondDice + "]";
	}

	public GameRoom getGameRoom() {
		return gameRoom;
	}

	public void setGameRoom(GameRoom gameRoom) {
		this.gameRoom = gameRoom;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
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
}
