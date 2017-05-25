package org.moshe.arad.game.move;

import java.util.Date;

public class Move {

	private BoardLocation from;
	private BoardLocation to;
	private Date createDate;
	public final static int WHITE_PLAYER = -2;
	public final static int BLACK_PLAYER = -3;
	public final static int WHITE_PLAYER_TURN = -4;
	public final static int BLACK_PLAYER_TURN = -5;
	public final static int WHITE_ROLLED_DICES = -6;
	public final static int BLACK_ROLLED_DICES = -7;
	public final static int INVALID_MOVE = -8;
	public final static int VALID_MOVE = -9;
	
	public Move() {
		this.createDate = new Date();
	}
	
	public Move(BoardLocation from, BoardLocation to) {
		this.from = from;
		this.to = to;
		this.createDate = new Date();
	}

	public BoardLocation getFrom() {
		return from;
	}

	public BoardLocation getTo() {
		return to;
	}

	public void setFrom(BoardLocation from) {
		this.from = from;
	}

	public void setTo(BoardLocation to) {
		this.to = to;
	}

	public Date getCreateDate() {
		return createDate;
	}

	@Override
	public String toString() {
		return "Move [from=" + from + ", to=" + to + ", createDate=" + createDate + "]";
	}
}
