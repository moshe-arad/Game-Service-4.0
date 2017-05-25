package org.moshe.arad.backgammon_dispatcher.request;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.moshe.arad.backgammon_dispatcher.BasicUser;
import org.moshe.arad.backgammon_dispatcher.entities.DispatchableEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class BackgammonUsersQueuesManager {
	
	private final Logger logger = LoggerFactory.getLogger(BackgammonUsersQueuesManager.class);
	private Map<String, BackgammonUserQueue> usersQueues = new ConcurrentHashMap<>(1000, 0.75F, 1000);
	
	public BackgammonUserQueue createNewQueueForUser(BasicUser basicUser){
		BackgammonUserQueue newQueue = new BackgammonUserQueue();
		if(basicUser == null) return null;
		usersQueues.putIfAbsent(basicUser.getUserName(), newQueue);
		return newQueue;
	}
	
	public DispatchableEntity takeMoveFromQueue(BasicUser basicUser){
		if(basicUser == null) return null;
		if(!usersQueues.containsKey(basicUser.getUserName())) return null;
		return usersQueues.get(basicUser.getUserName()).takeMoveFromQueue();
	}
	
	public void putMoveIntoQueue(BasicUser basicUser, DispatchableEntity entity){
		if(basicUser == null || entity == null){
			logger.error("Move obj or basicUser obj is null.");
			return;
		}
		if(!usersQueues.containsKey(basicUser.getUserName())) {
			logger.error("Queue doen't exists for this user - " + basicUser.getUserName());
			return;
		}
		usersQueues.get(basicUser.getUserName()).putMoveIntoQueue(entity);
		logger.info("Message was put on queue for user: " + basicUser.getUserName());
	}
	
	public BackgammonUserQueue getUserMoveQueue(BasicUser basicUser){
		return usersQueues.get(basicUser.getUserName());
	}
}
