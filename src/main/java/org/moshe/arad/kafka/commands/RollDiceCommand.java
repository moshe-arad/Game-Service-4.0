package org.moshe.arad.kafka.commands;

import org.springframework.stereotype.Component;

@Component
public class RollDiceCommand extends Command{

	private String username;
	private String gameRoomName;
	
	public RollDiceCommand() {
	
	}

	public RollDiceCommand(String username, String gameRoomName) {
		super();
		this.username = username;
		this.gameRoomName = gameRoomName;
	}

	@Override
	public String toString() {
		return "RollDiceCommand [username=" + username + ", gameRoomName=" + gameRoomName + "]";
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getGameRoomName() {
		return gameRoomName;
	}

	public void setGameRoomName(String gameRoomName) {
		this.gameRoomName = gameRoomName;
	}
}
