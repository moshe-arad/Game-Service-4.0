package org.moshe.arad.backgammon_dispatcher.entities;

public class WinnerMessage extends BasicDetails{

	private boolean isWinner;

	public WinnerMessage(int messageToken, String color, Boolean isYourTurn, boolean isWinner) {
		super(messageToken, color, isYourTurn);
		this.isWinner = isWinner;
	}

	public boolean isWinner() {
		return isWinner;
	}

	public void setWinner(boolean isWinner) {
		this.isWinner = isWinner;
	}

	@Override
	public String toString() {
		return "WinnerMessage [isWinner=" + isWinner + ", getColor()=" + getColor() + ", getIsYourTurn()="
				+ getIsYourTurn() + ", getMessageToken()=" + getMessageToken() + ", getUuid()=" + getUuid() + "]";
	}
}
