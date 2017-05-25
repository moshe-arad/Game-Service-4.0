package org.moshe.arad.backgammon_dispatcher.entities;

import java.io.IOException;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

public class BasicDetailsAndGameRoomIdJsonDeserializer extends JsonDeserializer<BasicDetails> {

	@Override
	public BasicDetails deserialize(JsonParser p, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {
		
		BasicDetailsAndGameRoomId result = new BasicDetailsAndGameRoomId(-1, "", false, -1L);
		
		JsonNode node = p.readValueAsTree();
		
		result.setGameRoomId(node.get("gameRoomId").asLong());
		result.setColor(node.get("confirm").get("color").asText());
		result.setIsYourTurn(node.get("confirm").get("isYourTurn").asBoolean());
		result.setMessageToken(node.get("confirm").get("token").asInt());
		
		result.setUuid(UUID.fromString(node.get("confirm").get("uuid").asText()));
		
		return result;
	}

}
