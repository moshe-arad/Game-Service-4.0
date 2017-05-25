package org.moshe.arad.backgammon_dispatcher.request;

import java.util.Comparator;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.moshe.arad.backgammon_dispatcher.entities.DispatchableEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BackgammonUserQueue {
	
	private final Logger logger = LoggerFactory.getLogger(BackgammonUserQueue.class);
	private Lock registerRequestLocker = new ReentrantLock(); //used in service
	private PriorityBlockingQueue<DispatchableEntity> userQueue; 
	
	public BackgammonUserQueue() {
		Comparator<DispatchableEntity> movesByCreateDate = (item1, item2) -> { return item2.createDate.compareTo(item1.createDate);};
		userQueue = new PriorityBlockingQueue<>(1000, movesByCreateDate);
	}
	
	public DispatchableEntity takeMoveFromQueue(){
		DispatchableEntity moveFromQueue = null;
		try {
			moveFromQueue = userQueue.take();
		} catch (InterruptedException e) {
			logger.info("This task may have been canceled.");
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return moveFromQueue;
	}
	
	public void putMoveIntoQueue(DispatchableEntity entity){
		if(entity == null) return;
		userQueue.put(entity);
	}
	
	public boolean isHasMovesOnQueue(){
		return !userQueue.isEmpty();
	}
	
	public boolean isGotMessages()
	{
		return userQueue.size() > 0;
	}

	public Lock getRegisterRequestLocker() {
		return registerRequestLocker;
	}
}
