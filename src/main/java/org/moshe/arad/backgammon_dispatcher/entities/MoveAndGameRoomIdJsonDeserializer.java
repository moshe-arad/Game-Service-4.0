package org.moshe.arad.backgammon_dispatcher.entities;

import java.io.IOException;

import org.moshe.arad.game.move.BackgammonBoardLocation;
import org.moshe.arad.game.move.Move;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

public class MoveAndGameRoomIdJsonDeserializer extends JsonDeserializer<MoveAndGameRoomId> {

	@Override
	public MoveAndGameRoomId deserialize(JsonParser p, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {
		
		Move move = new Move();
		Long gameRoomId;
		
		JsonNode node = p.readValueAsTree();
		
		gameRoomId = node.get("gameRoomId").asLong();
		int from = node.get("move").get("move").get("from").get("index").asInt();
		int to = node.get("move").get("move").get("to").get("index").asInt();
		move.setFrom(new BackgammonBoardLocation(from));
		move.setTo(new BackgammonBoardLocation(to));
		
		MoveAndGameRoomId result = new MoveAndGameRoomId(move, gameRoomId);
		return result;
	}

}
