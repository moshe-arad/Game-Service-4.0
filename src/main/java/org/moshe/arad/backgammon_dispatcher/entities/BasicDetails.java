package org.moshe.arad.backgammon_dispatcher.entities;

import java.util.UUID;

public class BasicDetails implements DispatchableEntity {

	private UUID uuid;
	private int messageToken;
	private String color;
	private Boolean isYourTurn;
	
	public BasicDetails(int messageToken, String color, Boolean isYourTurn) {
		this.uuid = UUID.randomUUID();
		this.messageToken = messageToken;
		this.color = color;
		this.isYourTurn = isYourTurn;
	}
	
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	public Boolean getIsYourTurn() {
		return isYourTurn;
	}
	public void setIsYourTurn(Boolean isYourTurn) {
		this.isYourTurn = isYourTurn;
	}

	public int getMessageToken() {
		return messageToken;
	}

	public void setMessageToken(int messageToken) {
		this.messageToken = messageToken;
	}
	
	public UUID getUuid() {
		return uuid;
	}

	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

	@Override
	public String toString() {
		return "BasicDetails [uuid=" + uuid + ", messageToken=" + messageToken + ", color=" + color + ", isYourTurn="
				+ isYourTurn + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((color == null) ? 0 : color.hashCode());
		result = prime * result + ((isYourTurn == null) ? 0 : isYourTurn.hashCode());
		result = prime * result + messageToken;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BasicDetails other = (BasicDetails) obj;
		if (color == null) {
			if (other.color != null)
				return false;
		} else if (!color.equals(other.color))
			return false;
		if (isYourTurn == null) {
			if (other.isYourTurn != null)
				return false;
		} else if (!isYourTurn.equals(other.isYourTurn))
			return false;
		if (messageToken != other.messageToken)
			return false;
		return true;
	}	
}
