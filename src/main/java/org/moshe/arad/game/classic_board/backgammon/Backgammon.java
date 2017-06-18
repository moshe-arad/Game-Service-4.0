package org.moshe.arad.game.classic_board.backgammon;

import org.moshe.arad.backgammon_dispatcher.confirm.ConfirmArriveQueueManager;
import org.moshe.arad.backgammon_dispatcher.entities.BasicDetails;
import org.moshe.arad.backgammon_dispatcher.entities.DiceRolling;
import org.moshe.arad.backgammon_dispatcher.entities.DispatchableEntity;
import org.moshe.arad.backgammon_dispatcher.entities.DispatchableEntityFactory;
import org.moshe.arad.backgammon_dispatcher.entities.InvalidMove;
import org.moshe.arad.backgammon_dispatcher.entities.ShowDiceButton;
import org.moshe.arad.backgammon_dispatcher.entities.ValidMove;
import org.moshe.arad.backgammon_dispatcher.entities.WinnerMessage;
import org.moshe.arad.backgammon_dispatcher.request.BackgammonUserQueue;
import org.moshe.arad.game.classic_board.ClassicBoardGame;
import org.moshe.arad.game.instrument.BackgammonBoard;
import org.moshe.arad.game.instrument.Board;
import org.moshe.arad.game.instrument.Dice;
import org.moshe.arad.game.move.BackgammonBoardLocation;
import org.moshe.arad.game.move.Move;
import org.moshe.arad.game.player.BackgammonPlayer;
import org.moshe.arad.game.player.Player;
import org.moshe.arad.game.turn.TurnOrderable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

public class Backgammon extends ClassicBoardGame implements Runnable{

	private Move move = null;	
	private String gameRoomName;
	private BackgammonUserQueue whiteQueue = null;
	private BackgammonUserQueue blackQueue = null;
	private Logger logger = LoggerFactory.getLogger(Backgammon.class);
	private Object diceLocker = new Object();
	private Object nextMoveLocker = new Object();
	private Object whiteConfirmLocker = new Object();
	private Object blackConfirmLocker = new Object();	
	private ConfirmArriveQueueManager confirmArriveQueueManager;	

	private Dice first; 
	private Dice second;
	
	public Backgammon(Board board, TurnOrderable turnOrderManager) {
		super(board, turnOrderManager);
	}		

	@Override
	public void run() {
		super.startGame();
	}
	
	@Override
	public void playGameTurn(Player player) {
		
		/*** notify about turn ***/
		BackgammonPlayer backgammonPlayer = (BackgammonPlayer)player;
		logger.info("This is turn of " + backgammonPlayer);
		
		BasicDetails basicDetailsWhite = null;
		BasicDetails basicDetailsBlack = null;
		
		logger.info("Server sends message with token=1 to white player with " + basicDetailsWhite);
		logger.info("Server sends message with token=1 to black player with " + basicDetailsBlack);

		basicDetailsWhite = setBasicDetailsMessageWhitePlayer(basicDetailsWhite, backgammonPlayer.isWhite());
		basicDetailsBlack = setBasicDetailsMessageBlackPlayer(basicDetailsBlack, backgammonPlayer.isWhite());

		logger.info("Put message to be confirmed on confirmation queue. " + basicDetailsWhite);
		logger.info("Put message to be confirmed on confirmation queue. " + basicDetailsBlack);
		putMessagesOnConfirmationQueue(basicDetailsWhite,basicDetailsBlack);
		
		putMessageOnWhiteQueue(basicDetailsWhite);		
		putMessageOnBlackQueue(basicDetailsBlack);	
	
		/*** show dice button ***/		
		ShowDiceButton showDiceButton = null;
		showDiceButton = sendShowDiceButton(backgammonPlayer, showDiceButton);
	
		waitForRollDicesClick();

		rollDices(player, backgammonPlayer);
		
		/*** return dices result ***/
		
		DiceRolling diceRollingWhite = null;
		DiceRolling diceRollingBlack = null;
		
		diceRollingWhite = getDiceRollingMessageWhitePlayer(backgammonPlayer);
		diceRollingBlack = getDiceRollingMessageBlackPlayer(backgammonPlayer);
		
		logger.info("Server sends message with token=2 to white player with " + diceRollingWhite);
		logger.info("Server sends message with token=2 to black player with " + diceRollingBlack);			
		
		putMessagesOnConfirmationQueue(diceRollingWhite, diceRollingBlack);
		
		logger.info("Put message to be confirmed on confirmation queue. " + diceRollingWhite);
		logger.info("Put message to be confirmed on confirmation queue. " + diceRollingBlack);
		
		putMessageOnWhiteQueue(diceRollingWhite);
		
		putMessageOnBlackQueue(diceRollingBlack);

		/***** play move *******/

		logger.info("The board before making move:");
		logger.info(board.toString());
		
		try {
			while(isCanKeepPlay(player)){
				logger.info("Player can make more moves, and can keep play.");
				waitForMoveFromClient();
				
				int eatenWhite = ((BackgammonBoard)board).getWhiteEatenSize();
				int eatenBlack = ((BackgammonBoard)board).getBlackEatenSize();
				logger.info("Before applying move, white has eaten = " + eatenWhite);
				logger.info("Before applying move, black has eaten = " + eatenBlack);			
				
				if(board.isValidMove(player, move)){
					logger.info("The move passed validation.");
					board.executeMove(player, move);
					player.makePlayed(move);					
					
					logger.info("A move was made...");
					logger.info("************************************");
					logger.info("The board after making move:");
					logger.info(board.toString());				
					
					boolean isEatenWhite =  (eatenWhite + 1) == ((BackgammonBoard)board).getWhiteEatenSize() ? true : false;
					boolean isEatenBlack =  (eatenBlack + 1) == ((BackgammonBoard)board).getBlackEatenSize() ? true : false;
					logger.info("After move, white has eaten = " + eatenWhite);
					logger.info("After move, black has eaten = " + eatenBlack);
					
					ValidMove validMoveWhite = null;
					ValidMove validMoveBlack = null;				
					
					logger.info("Server sends message with token=4 to white player with " + validMoveWhite);
					logger.info("Server sends message with token=4 to black player with " + validMoveBlack);									
					
					validMoveWhite = getValidMoveMessageWhitePlayer(backgammonPlayer, isEatenWhite, isEatenBlack);
					validMoveBlack = getValidMoveMessageBlackPlayer(backgammonPlayer, isEatenWhite, isEatenBlack);
							
					logger.info("Put message to be confirmed on confirmation queue. " + validMoveWhite);
					logger.info("Put message to be confirmed on confirmation queue. " + validMoveBlack);
					putMessagesOnConfirmationQueue(validMoveWhite, validMoveBlack);
					
					putMessageOnWhiteQueue(validMoveWhite);
					putMessageOnBlackQueue(validMoveBlack);
				}
				else{
					logger.info("The move did not passed validation.");
					InvalidMove invalidMoveWhite = (InvalidMove) DispatchableEntityFactory.getInstance(InvalidMove.class, 3, "white", true);
					InvalidMove invalidMoveBlack = (InvalidMove) DispatchableEntityFactory.getInstance(InvalidMove.class, 3, "black", true);									
					sendInvalidMessage(backgammonPlayer, invalidMoveWhite, invalidMoveBlack);									
				}				
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}
	
	@Override
	public void doWinnerActions(){
		WinnerMessage winnerMessageWhite = null;
		WinnerMessage winnerMessageBlack = null;
		
		if(((BackgammonPlayer)getWinner()).isWhite()){
			winnerMessageWhite = (WinnerMessage) DispatchableEntityFactory.getInstance(WinnerMessage.class, 10, "white", true, true);
			winnerMessageBlack = (WinnerMessage) DispatchableEntityFactory.getInstance(WinnerMessage.class, 10, "black", false, false);
			
			whiteQueue.putMoveIntoQueue(winnerMessageWhite);
			blackQueue.putMoveIntoQueue(winnerMessageBlack);
			
			logger.info("notifying players that white player is the winner.");
		}
		else{
			winnerMessageWhite = (WinnerMessage) DispatchableEntityFactory.getInstance(WinnerMessage.class, 10, "white", false, false);
			winnerMessageBlack = (WinnerMessage) DispatchableEntityFactory.getInstance(WinnerMessage.class, 10, "black", true, true);
			
			whiteQueue.putMoveIntoQueue(winnerMessageWhite);
			blackQueue.putMoveIntoQueue(winnerMessageBlack);
			
			logger.info("notifying players that black player is the winner.");
		}
	}
	
	private void sendInvalidMessage(BackgammonPlayer backgammonPlayer, InvalidMove invalidMoveWhite,
			InvalidMove invalidMoveBlack) {
		if(backgammonPlayer.isWhite()) {
			logger.info("Put message to be confirmed on confirmation queue. " + invalidMoveWhite);
			putMessagesOnConfirmationQueue(invalidMoveWhite);
			putMessageOnWhiteQueue(invalidMoveWhite);
			
		}
		else if(!backgammonPlayer.isWhite()){						
			logger.info("Put message to be confirmed on confirmation queue. " + invalidMoveBlack);
			putMessagesOnConfirmationQueue(invalidMoveBlack);
			putMessageOnBlackQueue(invalidMoveBlack);					
		}
	}
	
	private ValidMove getValidMoveMessageWhitePlayer(BackgammonPlayer player, boolean isEatenWhite, boolean isEatenBlack) {
		try{
			if(player.isWhite()){
				return (ValidMove) DispatchableEntityFactory.getInstance(ValidMove.class, 
						4, 
						"white", 
						true, 
						((BackgammonBoardLocation)move.getFrom()).getIndex(), 
						((BackgammonBoardLocation)move.getTo()).getIndex(), 
						((BackgammonBoard)(super.board)).getSizeOfColumn((BackgammonBoardLocation)move.getFrom()), 
						((BackgammonBoard)(super.board)).getSizeOfColumn((BackgammonBoardLocation)move.getTo()),
								isCanKeepPlay(player),
								isEatenBlack);
			}
			else{
				return (ValidMove) DispatchableEntityFactory.getInstance(ValidMove.class, 
						4, 
						"white", 
						false, 
						((BackgammonBoardLocation)move.getFrom()).getIndex(), 
						((BackgammonBoardLocation)move.getTo()).getIndex(), 
						((BackgammonBoard)(super.board)).getSizeOfColumn((BackgammonBoardLocation)move.getFrom()), 
						((BackgammonBoard)(super.board)).getSizeOfColumn((BackgammonBoardLocation)move.getTo()),
								isCanKeepPlay(player),
								isEatenWhite);
			}
		}catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
	
	private ValidMove getValidMoveMessageBlackPlayer(BackgammonPlayer player, boolean isEatenWhite, boolean isEatenBlack) {
		try{
			if(player.isWhite()){
				return (ValidMove) DispatchableEntityFactory.getInstance(ValidMove.class, 
						4, 
						"black", 
						false, 
						((BackgammonBoardLocation)move.getFrom()).getIndex(), 
						((BackgammonBoardLocation)move.getTo()).getIndex(), 
						((BackgammonBoard)(super.board)).getSizeOfColumn((BackgammonBoardLocation)move.getFrom()), 
						((BackgammonBoard)(super.board)).getSizeOfColumn((BackgammonBoardLocation)move.getTo()),
								isCanKeepPlay(player),
								isEatenBlack);
			}
			else{
				return (ValidMove) DispatchableEntityFactory.getInstance(ValidMove.class, 
						4, 
						"black", 
						true, 
						((BackgammonBoardLocation)move.getFrom()).getIndex(), 
						((BackgammonBoardLocation)move.getTo()).getIndex(), 
						((BackgammonBoard)(super.board)).getSizeOfColumn((BackgammonBoardLocation)move.getFrom()), 
						((BackgammonBoard)(super.board)).getSizeOfColumn((BackgammonBoardLocation)move.getTo()),
								isCanKeepPlay(player),
								isEatenWhite);
			}
		}catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	private void waitForMoveFromClient() throws InterruptedException {
		synchronized (nextMoveLocker) {	
			logger.info("Game Reached to next move locker.");
			nextMoveLocker.wait();
			logger.info("Next move locker released, game continues.");
			logger.info("Game continues with move = " + move);
		}
	}
	
	private DiceRolling getDiceRollingMessageWhitePlayer(BackgammonPlayer backgammonPlayer) {
		try{
			if(backgammonPlayer.isWhite()){
				return (DiceRolling) DispatchableEntityFactory.getInstance(DiceRolling.class, 
						2, 
						"white", 
						true, 
						backgammonPlayer.getTurn().getFirstDice().getValue(), 
						backgammonPlayer.getTurn().getSecondDice().getValue(), 
						board.isHasMoreMoves(backgammonPlayer));
			}
			else
			{
				return (DiceRolling) DispatchableEntityFactory.getInstance(DiceRolling.class, 
						2, 
						"white", 
						false, 
						backgammonPlayer.getTurn().getFirstDice().getValue(), 
						backgammonPlayer.getTurn().getSecondDice().getValue(), 
						board.isHasMoreMoves(backgammonPlayer));	
			}
		}
		catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
	
	private DiceRolling getDiceRollingMessageBlackPlayer(BackgammonPlayer backgammonPlayer) {
		try{
			if(backgammonPlayer.isWhite()){
				return (DiceRolling) DispatchableEntityFactory.getInstance(DiceRolling.class, 
						2, 
						"black", 
						false, 
						backgammonPlayer.getTurn().getFirstDice().getValue(), 
						backgammonPlayer.getTurn().getSecondDice().getValue(), 
						board.isHasMoreMoves(backgammonPlayer));
			}
			else
			{
				return (DiceRolling) DispatchableEntityFactory.getInstance(DiceRolling.class, 
						2, 
						"black", 
						true, 
						backgammonPlayer.getTurn().getFirstDice().getValue(), 
						backgammonPlayer.getTurn().getSecondDice().getValue(), 
						board.isHasMoreMoves(backgammonPlayer));	
			}
		}
		catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	private void rollDices(Player player, BackgammonPlayer backgammonPlayer) {
		Dice first = backgammonPlayer.getTurn().getFirstDice();
		Dice second = backgammonPlayer.getTurn().getSecondDice();
		String name = backgammonPlayer.getFirstName() + " " + backgammonPlayer.getLastName() + ": ";
		
		logger.info(name + "it's your turn. roll the dices.");
		
		player.rollDices();				
		logger.info(name + "you rolled - " + first.getValue() + ": " + second.getValue());
	}

	private void waitForRollDicesClick() {
		synchronized (diceLocker) {
			logger.info("Game Reached to dice locker.");
			try {
				diceLocker.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			logger.info("Dice locker released, game continues.");
		}
	}

	private ShowDiceButton sendShowDiceButton(BackgammonPlayer backgammonPlayer, ShowDiceButton showDiceButton) {
		if(backgammonPlayer.isWhite()){					
			logger.info("Send to white player request to display roll dices button. " + showDiceButton);
			showDiceButton = (ShowDiceButton) DispatchableEntityFactory.getInstance(ShowDiceButton.class, 6, "white", true, true);
			
			logger.info("Put message to be confirmed on confirmation queue. " + showDiceButton);
			putMessagesOnConfirmationQueue(showDiceButton);
			
			putMessageOnWhiteQueue(showDiceButton);
		}
		else{					
			logger.info("Send to black request to display roll dices button. " + showDiceButton);
			showDiceButton = (ShowDiceButton) DispatchableEntityFactory.getInstance(ShowDiceButton.class, 6, "white", true, true);
			
			
			logger.info("Put message to be confirmed on confirmation queue. " + showDiceButton);
			putMessagesOnConfirmationQueue(showDiceButton);
			
			putMessageOnBlackQueue(showDiceButton);	
		}
		
		return showDiceButton;
	}
	
	
	
//	private void setBasicDetailsMessages(BasicDetails basicDetailsWhite, BasicDetails basicDetailsBlack, boolean isWhite){
//		if(isWhite){
//			basicDetailsWhite = (BasicDetails) DispatchableEntityFactory.getInstance(BasicDetails.class, 1, "white", true);
//			basicDetailsBlack = (BasicDetails) DispatchableEntityFactory.getInstance(BasicDetails.class, 1, "black", false);
//		}
//		else {
//			basicDetailsWhite = (BasicDetails) DispatchableEntityFactory.getInstance(BasicDetails.class, 1, "white", false);
//			basicDetailsBlack = (BasicDetails) DispatchableEntityFactory.getInstance(BasicDetails.class, 1, "black", true);
//		}
//	}
	
	private BasicDetails setBasicDetailsMessageWhitePlayer(BasicDetails basicDetailsWhite, boolean isWhite){
		if(isWhite){
			return (BasicDetails) DispatchableEntityFactory.getInstance(BasicDetails.class, 1, "white", true);
		}
		else {
			return (BasicDetails) DispatchableEntityFactory.getInstance(BasicDetails.class, 1, "white", false);
		
		}
	}
	
	private BasicDetails setBasicDetailsMessageBlackPlayer(BasicDetails basicDetailsBlack, boolean isWhite){
		if(isWhite){
			return (BasicDetails) DispatchableEntityFactory.getInstance(BasicDetails.class, 1, "black", false);
		}
		else {
			return (BasicDetails) DispatchableEntityFactory.getInstance(BasicDetails.class, 1, "black", true);
		
		}
	}
	
	private void putMessagesOnConfirmationQueue(DispatchableEntity...entities){
		for(DispatchableEntity entity:entities){
			confirmArriveQueueManager.putMessageToBeConfirmed(entity);
		}
	}
	
	private void putMessageOnBlackQueue(DispatchableEntity basicDetailsBlack) {
		synchronized(blackConfirmLocker){
			logger.info("Arrived to black confirm message locker");
			try {
				blackQueue.putMoveIntoQueue(basicDetailsBlack);
				blackConfirmLocker.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}			
		}
		logger.info("black confirm message locker released.");
	}

	private void putMessageOnWhiteQueue(DispatchableEntity basicDetailsWhite) {
		synchronized(whiteConfirmLocker){
			logger.info("Arrived to white confirm message locker");
			try {
				whiteQueue.putMoveIntoQueue(basicDetailsWhite);
				whiteConfirmLocker.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}			
		}
		logger.info("white confirm message locker released.");
	}

	private boolean isCanKeepPlay(Player player) throws Exception {
		return !board.isWinner(player) && board.isHasMoreMoves(player);
	}
	
	public Move getMove() {
		return move;
	}

	public void setMove(Move move) {
		this.move = move;
	}
	
	public BackgammonUserQueue getWhiteQueue() {
		return whiteQueue;
	}

	public void setWhiteQueue(BackgammonUserQueue whiteQueue) {
		this.whiteQueue = whiteQueue;
	}

	public BackgammonUserQueue getBlackQueue() {
		return blackQueue;
	}

	public void setBlackQueue(BackgammonUserQueue blackQueue) {
		this.blackQueue = blackQueue;
	}	
	
	public Object getDiceLocker() {
		return diceLocker;
	}

	public Object getNextMoveLocker() {
		return nextMoveLocker;
	}
	
	public String getGameRoomName() {
		return gameRoomName;
	}

	public void setGameRoomName(String gameRoomName) {
		this.gameRoomName = gameRoomName;
	}
	
	public Object getWhiteConfirmLocker() {
		return whiteConfirmLocker;
	}

	public Object getBlackConfirmLocker() {
		return blackConfirmLocker;
	}
	
	public ConfirmArriveQueueManager getConfirmArriveQueueManager() {
		return confirmArriveQueueManager;
	}

	public void setConfirmArriveQueueManager(ConfirmArriveQueueManager confirmArriveQueueManager) {
		this.confirmArriveQueueManager = confirmArriveQueueManager;
	}
}
