package org.moshe.arad.backgammon_dispatcher.entities;

public class ValidMove extends BasicDetails {

	private int from;
	private int to;
	private int columnSizeOnFrom;
	private int columnSizeOnTo;
	private boolean isHasMoreMoves;
	private boolean isEaten;	

	public ValidMove(int messageToken, String color, Boolean isYourTurn, int from, int to, int columnSizeOnFrom,
			int columnSizeOnTo, boolean isHasMoreMoves, boolean isEaten) {
		super(messageToken, color, isYourTurn);
		this.from = from;
		this.to = to;
		this.columnSizeOnFrom = columnSizeOnFrom;
		this.columnSizeOnTo = columnSizeOnTo;
		this.isHasMoreMoves = isHasMoreMoves;
		this.isEaten = isEaten;
	}

	public int getFrom() {
		return from;
	}
	public void setFrom(int from) {
		this.from = from;
	}
	public int getTo() {
		return to;
	}
	public void setTo(int to) {
		this.to = to;
	}
	public int getColumnSizeOnFrom() {
		return columnSizeOnFrom;
	}
	public void setColumnSizeOnFrom(int columnSizeOnFrom) {
		this.columnSizeOnFrom = columnSizeOnFrom;
	}
	public int getColumnSizeOnTo() {
		return columnSizeOnTo;
	}
	public void setColumnSizeOnTo(int columnSizeOnTo) {
		this.columnSizeOnTo = columnSizeOnTo;
	}

	public boolean isHasMoreMoves() {
		return isHasMoreMoves;
	}

	public void setHasMoreMoves(boolean isHasMoreMoves) {
		this.isHasMoreMoves = isHasMoreMoves;
	}

	public boolean isEaten() {
		return isEaten;
	}

	public void setEaten(boolean isEaten) {
		this.isEaten = isEaten;
	}

	@Override
	public String toString() {
		return "ValidMove [from=" + from + ", to=" + to + ", columnSizeOnFrom=" + columnSizeOnFrom + ", columnSizeOnTo="
				+ columnSizeOnTo + ", isHasMoreMoves=" + isHasMoreMoves + ", isEaten=" + isEaten + ", getColor()="
				+ getColor() + ", getIsYourTurn()=" + getIsYourTurn() + ", getMessageToken()=" + getMessageToken()
				+ "]";
	}
}
