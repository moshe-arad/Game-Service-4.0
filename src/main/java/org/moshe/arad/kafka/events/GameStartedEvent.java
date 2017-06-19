package org.moshe.arad.kafka.events;

import java.util.Date;
import java.util.UUID;

import org.moshe.arad.entities.GameRoom;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class GameStartedEvent extends BackgammonEvent{

	private GameRoom gameRoom;
	
	public GameStartedEvent() {
	
	}

	public GameStartedEvent(GameRoom gameRoom) {
		super();
		this.gameRoom = gameRoom;
	}

	public GameStartedEvent(UUID uuid, int serviceId, int eventId, Date arrived, String clazz,
			GameRoom gameRoom) {
		super(uuid, serviceId, eventId, arrived, clazz);
		this.gameRoom = gameRoom;
	}

	@Override
	public String toString() {
		return "GameStartedEvent [gameRoom=" + gameRoom + "]";
	}

	public GameRoom getGameRoom() {
		return gameRoom;
	}

	public void setGameRoom(GameRoom gameRoom) {
		this.gameRoom = gameRoom;
	}
}
