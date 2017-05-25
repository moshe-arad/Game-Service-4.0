package org.moshe.arad.backgammon_dispatcher.entities;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = BasicDetailsAndGameRoomIdJsonDeserializer.class)
public class BasicDetailsAndGameRoomId extends BasicDetails{

	private Long gameRoomId;

	public BasicDetailsAndGameRoomId(int messageToken, String color, Boolean isYourTurn, Long gameRoomId) {
		super(messageToken, color, isYourTurn);
		this.gameRoomId = gameRoomId;
	}
	
	public Long getGameRoomId() {
		return gameRoomId;
	}

	public void setGameRoomId(Long gameRoomId) {
		this.gameRoomId = gameRoomId;
	}

	@Override
	public String toString() {
		return "BasicDetailsAndGameRoomId [gameRoomId=" + gameRoomId + ", getColor()=" + getColor()
				+ ", getIsYourTurn()=" + getIsYourTurn() + ", getMessageToken()=" + getMessageToken() + ", getUuid()="
				+ getUuid() + "]";
	}
}
