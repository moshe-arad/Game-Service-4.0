package org.moshe.arad.backgammon_dispatcher.entities;

public class EmptyMessage extends BasicDetails {

	private boolean isEmpty;
	
	public EmptyMessage(int messageToken) {
		super(messageToken, "", false);
		this.isEmpty = true;
	}

	public boolean isEmpty() {
		return isEmpty;
	}

	public void setEmpty(boolean isEmpty) {
		this.isEmpty = isEmpty;
	}

	@Override
	public String toString() {
		return "EmptyMessage [isEmpty=" + isEmpty + ", getColor()=" + getColor() + ", getIsYourTurn()="
				+ getIsYourTurn() + ", getMessageToken()=" + getMessageToken() + "]";
	}
}
