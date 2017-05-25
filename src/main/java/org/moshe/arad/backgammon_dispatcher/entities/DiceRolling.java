package org.moshe.arad.backgammon_dispatcher.entities;

public class DiceRolling extends BasicDetails {

	private int firstDice;
	private int secondDice;
	private boolean isCanPlay;
	
	public DiceRolling(int messageToken, String color, Boolean isYourTurn, int firstDice, int secondDice,
			boolean isCanPlay) {
		super(messageToken, color, isYourTurn);
		this.firstDice = firstDice;
		this.secondDice = secondDice;
		this.isCanPlay = isCanPlay;
	}

	public int getFirstDice() {
		return firstDice;
	}
	public void setFirstDice(int firstDice) {
		this.firstDice = firstDice;
	}
	public int getSecondDice() {
		return secondDice;
	}
	public void setSecondDice(int secondDice) {
		this.secondDice = secondDice;
	}

	public boolean isCanPlay() {
		return isCanPlay;
	}

	public void setCanPlay(boolean isCanPlay) {
		this.isCanPlay = isCanPlay;
	}

	@Override
	public String toString() {
		return "DiceRolling [firstDice=" + firstDice + ", secondDice=" + secondDice + ", isCanPlay=" + isCanPlay
				+ ", getColor()=" + getColor() + ", getIsYourTurn()=" + getIsYourTurn() + ", getMessageToken()="
				+ getMessageToken() + "]";
	}	
}
