package org.moshe.arad.backgammon_dispatcher.entities;

import org.moshe.arad.game.move.Move;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = MoveAndGameRoomIdJsonDeserializer.class)
public class MoveAndGameRoomId {

	private Move move;
	private Long gameRoomId;
	
	public MoveAndGameRoomId() {
	}
	
	public MoveAndGameRoomId(Move move, Long gameRoomId) {
		this.move = move;
		this.gameRoomId = gameRoomId;
	}
	
	public Move getMove() {
		return move;
	}
	public void setMove(Move move) {
		this.move = move;
	}
	public Long getGameRoomId() {
		return gameRoomId;
	}
	public void setGameRoomId(Long gameRoomId) {
		this.gameRoomId = gameRoomId;
	}

	@Override
	public String toString() {
		return "MoveAndGameRoomId [move=" + move + ", gameRoomId=" + gameRoomId + "]";
	}
}
