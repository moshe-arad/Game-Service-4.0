package org.moshe.arad.game.instrument;

import java.util.Random;

public class BackgammonDice implements Dice {

	public static final int NONE = 0;
	public static final int MAX = 6;
	public static final int DOUBLE = 2;
	public static final int NON_DOUBLE = 1;
	private int value = NONE;
	private Random random = new Random();
	private int times = NONE;
	
	@Override
	public void rollDice(){
		value = random.nextInt(MAX)+1;
		setTimes(NON_DOUBLE);
	}

	@Override
	public int getValue() {
		return value;
	}

	@Override
	public void initDice() {
		setTimes(getTimes() - 1);
		if(getTimes() == NONE) value = NONE;
	}

	public void setTimes(int times) {
		if(times != NONE && times != NON_DOUBLE && times != DOUBLE) return;
		else this.times = times;
	}

	public int getTimes() {
		return times;
	}
}
