package org.moshe.arad.backgammon_dispatcher.request;

import java.util.concurrent.Callable;

import org.moshe.arad.backgammon_dispatcher.entities.DispatchableEntity;
import org.moshe.arad.game.move.Move;

public class BackgammonUserTask implements Callable<DispatchableEntity>{

	private BackgammonUserQueue movesQueue;
	
	public BackgammonUserTask(BackgammonUserQueue movesQueue) {
		this.movesQueue = movesQueue;
	}
	
	@Override
	public DispatchableEntity call() throws Exception {
		return movesQueue.takeMoveFromQueue();		
	}

}
