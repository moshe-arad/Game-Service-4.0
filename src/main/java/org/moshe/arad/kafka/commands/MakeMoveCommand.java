package org.moshe.arad.kafka.commands;

import org.springframework.stereotype.Component;

@Component
public class MakeMoveCommand extends Command{

	private String username;
	private String gameRoomName;
	private int from;
	private int to;
	
	public MakeMoveCommand() {
	
	}

	public MakeMoveCommand(String username, String gameRoomName, int from, int to) {
		super();
		this.username = username;
		this.gameRoomName = gameRoomName;
		this.from = from;
		this.to = to;
	}

	@Override
	public String toString() {
		return "MakeMoveCommand [username=" + username + ", gameRoomName=" + gameRoomName + ", from=" + from + ", to="
				+ to + "]";
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
}
