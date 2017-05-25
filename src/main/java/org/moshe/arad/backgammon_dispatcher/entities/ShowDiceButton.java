package org.moshe.arad.backgammon_dispatcher.entities;

public class ShowDiceButton extends BasicDetails{

	private boolean isToShowDiceButton;
	
	public ShowDiceButton(int messageToken, String color, Boolean isYourTurn, boolean isToShowDiceButton) {
		super(messageToken, color, isYourTurn);
		this.isToShowDiceButton = isToShowDiceButton;
	}

	public boolean isToShowDiceButton() {
		return isToShowDiceButton;
	}

	public void setToShowDiceButton(boolean isToShowDiceButton) {
		this.isToShowDiceButton = isToShowDiceButton;
	}

	@Override
	public String toString() {
		return "ShowDiceButton [isToShowDiceButton=" + isToShowDiceButton + ", getColor()=" + getColor()
				+ ", getIsYourTurn()=" + getIsYourTurn() + ", getMessageToken()=" + getMessageToken() + "]";
	}
	
	
}
