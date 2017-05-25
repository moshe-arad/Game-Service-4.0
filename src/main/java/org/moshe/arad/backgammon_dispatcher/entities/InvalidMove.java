package org.moshe.arad.backgammon_dispatcher.entities;

public class InvalidMove extends BasicDetails {

	private Boolean isInvalid;
	
	public InvalidMove(int messageToken, String color, Boolean isYourTurn) {
		super(messageToken, color, isYourTurn);
		this.isInvalid = true;
	}

	public Boolean getIsInvalid() {
		return isInvalid;
	}

	public void setIsInvalid(Boolean isInvalid) {
		this.isInvalid = isInvalid;
	}

	@Override
	public String toString() {
		return "InvalidMove [isInvalid=" + isInvalid + ", getColor()=" + getColor() + ", getIsYourTurn()="
				+ getIsYourTurn() + ", getMessageToken()=" + getMessageToken() + "]";
	}
}
