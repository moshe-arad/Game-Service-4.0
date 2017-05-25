package org.moshe.arad.game;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BasicGame implements BasicGameable{

	private final Logger logger = LoggerFactory.getLogger("org.moshe.arad");
	
	@Override
	public void startGame(){
		logger.info("Template pattern begins.");
		initGame();
		play();
		doWinnerActions();
		logger.info("Template pattern ends.");
	}
	
	@Override
	public abstract void initGame();

	@Override
	public abstract void play();
	
	@Override
	public abstract void doWinnerActions();

	@Override
	public abstract boolean isHasWinner();
}
