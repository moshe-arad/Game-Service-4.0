package org.moshe.arad.game.classic_board;

import org.moshe.arad.game.BasicGame;
import org.moshe.arad.game.instrument.Board;
import org.moshe.arad.game.player.BackgammonPlayer;
import org.moshe.arad.game.player.Player;
import org.moshe.arad.game.turn.TurnOrderable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class ClassicBoardGame extends BasicGame {

	private final Logger logger = LoggerFactory.getLogger("org.moshe.arad");
	@Autowired
	protected Board board;
	@Autowired
	private TurnOrderable turnOrderManager;
	
	private boolean isPlaying = true;
	private Player firstPlayer;
	private Player secondPlayer;
	
	private Player winner;
	
	public ClassicBoardGame(Board board, TurnOrderable turnOrderManager) {
		this.board = board;
		this.turnOrderManager = turnOrderManager;
	}
	
	@Override
	public void initGame() {
		logger.info("Initializing board...");
		board.initBoard();
		logger.info("Board initializing completed...");
	}	

	@Override
	public void play(){
		logger.info("Game is about to begin...");
		try{
			while(isPlaying){				
				Player playerWithTurn = turnOrderManager.howHasTurn();
				logger.info("Turn passed to " + playerWithTurn);
				playGameTurn(playerWithTurn);
				logger.info("Turn was played.");
				if(!isHasWinner()) turnOrderManager.passTurn();				
				else {
					winner = playerWithTurn;
					isPlaying = false;
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("Game end with a winner...");
	}

	@Override
	public abstract void doWinnerActions();

	@Override
	public boolean isHasWinner() {
		try{
			BackgammonPlayer first = (BackgammonPlayer)turnOrderManager.howHasTurn();
			BackgammonPlayer second = (BackgammonPlayer)turnOrderManager.howIsNextInTurn();
			return board.isWinner(first) || board.isWinner(second);
		}
		catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return false;
	}
	
	public abstract void playGameTurn(Player player);
	
	public Board getBoard() {
		return board;
	}
	public Player getFirstPlayer() {
		return firstPlayer;
	}
	public Player getSecondPlayer() {
		return secondPlayer;
	}

	public void setFirstPlayer(Player firstPlayer) {
		this.firstPlayer = firstPlayer;
	}

	public void setSecondPlayer(Player secondPlayer) {
		this.secondPlayer = secondPlayer;
	}

	public TurnOrderable getTurnOrderManager() {
		return turnOrderManager;
	}

	public Player getWinner() {
		return winner;
	}	
}
