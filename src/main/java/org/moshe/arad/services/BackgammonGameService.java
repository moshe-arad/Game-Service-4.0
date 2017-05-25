package org.moshe.arad.services;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.moshe.arad.entities.GameRoom;
import org.moshe.arad.game.classic_board.backgammon.Backgammon;
import org.moshe.arad.game.player.BackgammonPlayer;
import org.moshe.arad.game.player.ClassicGamePlayer;
import org.moshe.arad.game.player.Player;
import org.moshe.arad.game.turn.BackgammonTurn;
import org.moshe.arad.game.turn.ClassicGameTurnOrderManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Service
public class BackgammonGameService {

	private Map<String,Backgammon> backgammonGames = new HashMap<>(100000);
	
	private ExecutorService gamesPool = Executors.newFixedThreadPool(8);
	
	@Autowired
	private ApplicationContext context;
	
	public void initGameAndStart(GameRoom gameRoom){
		Backgammon backgammon = context.getBean(Backgammon.class);
		BackgammonTurn turn = context.getBean(BackgammonTurn.class);
		
		Player playerWhite = new BackgammonPlayer(gameRoom.getOpenBy(),
				"", 100, turn, 
				true);
		
		Player playerBlack = new BackgammonPlayer(gameRoom.getSecondPlayer(),
				"", 100, turn, 
				false);
		
		LinkedList<ClassicGamePlayer> backgammonPlayers = new LinkedList<>();
		backgammonPlayers.add((BackgammonPlayer) playerWhite);
		backgammonPlayers.add((BackgammonPlayer) playerBlack);
				
		((ClassicGameTurnOrderManager)backgammon.getTurnOrderManager()).setOrder(backgammonPlayers);
		
		backgammon.setFirstPlayer(playerWhite);
		backgammon.setSecondPlayer(playerBlack);
		
		backgammonGames.put(gameRoom.getName(), backgammon);
		gamesPool.execute(backgammon);
	}
}
