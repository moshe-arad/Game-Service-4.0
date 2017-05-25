package org.moshe.arad.backgammon_dispatcher.entities;

public class DispatchableEntityFactory {
	
	public static DispatchableEntity getInstance(Class dispatchableEntityClazz,
			int messageToken,
			String color,
			Boolean isYourTurn){
		
		if(dispatchableEntityClazz.equals(BasicDetails.class)){
			return new BasicDetails(messageToken, color, isYourTurn);
		}
		else if(dispatchableEntityClazz.equals(EmptyMessage.class)) return new EmptyMessage(5);
		else if(dispatchableEntityClazz.equals(InvalidMove.class)) return new InvalidMove(messageToken, color, isYourTurn);
		return null;
	}
	
	public static DispatchableEntity getInstance(Class dispatchableEntityClazz,
			int messageToken,
			String color,
			Boolean isYourTurn,
			Long gameRoomId){
		
		if(dispatchableEntityClazz.equals(BasicDetails.class)){
			return new BasicDetails(messageToken, color, isYourTurn);
		}
		else if(dispatchableEntityClazz.equals(BasicDetailsAndGameRoomId.class)){
			return new BasicDetailsAndGameRoomId(messageToken, color, isYourTurn, gameRoomId);
		}
		else if(dispatchableEntityClazz.equals(EmptyMessage.class)) return new EmptyMessage(5);
		else if(dispatchableEntityClazz.equals(InvalidMove.class)) return new InvalidMove(messageToken, color, isYourTurn);
		return null;
	}
	
	public static DispatchableEntity getInstance(Class dispatchableEntityClazz,
			int messageToken,
			String color,
			Boolean isYourTurn,
			int firstDice,
			int secondDice,
			boolean isCanPlay){
		
		if(dispatchableEntityClazz.equals(BasicDetails.class)){
			return new BasicDetails(messageToken, color, isYourTurn);
		}
		else if(dispatchableEntityClazz.equals(DiceRolling.class)){
			return new DiceRolling(messageToken, color, isYourTurn, firstDice, secondDice, isCanPlay);
		}
		else if(dispatchableEntityClazz.equals(EmptyMessage.class)) return new EmptyMessage(5);
		else if(dispatchableEntityClazz.equals(InvalidMove.class)) return new InvalidMove(messageToken, color, isYourTurn);
		return null;
	}
	
	public static DispatchableEntity getInstance(Class dispatchableEntityClazz,
			int messageToken,
			String color,
			Boolean isYourTurn,
			boolean isShowDiceBtnIsWinner){
		
		if(dispatchableEntityClazz.equals(BasicDetails.class)){
			return new BasicDetails(messageToken, color, isYourTurn);
		}
		else if(dispatchableEntityClazz.equals(EmptyMessage.class)) return new EmptyMessage(5);
		else if(dispatchableEntityClazz.equals(InvalidMove.class)) return new InvalidMove(messageToken, color, isYourTurn);
		else if(dispatchableEntityClazz.equals(ShowDiceButton.class)) return new ShowDiceButton(messageToken, color, isYourTurn, isShowDiceBtnIsWinner);
		else if(dispatchableEntityClazz.equals(WinnerMessage.class)) return new WinnerMessage(messageToken, color, isYourTurn, isShowDiceBtnIsWinner);
		return null;
	}
	
	public static DispatchableEntity getInstance(Class dispatchableEntityClazz,
			int messageToken,
			String color,
			Boolean isYourTurn,
			int from,
			int to,
			int columnSizeOnFrom,
			int columnSizeOnTo,
			boolean isHasMoreMoves,
			boolean isEaten){
		
		if(dispatchableEntityClazz.equals(BasicDetails.class)){
			return new BasicDetails(messageToken, color, isYourTurn);
		}
		else if(dispatchableEntityClazz.equals(EmptyMessage.class)) return new EmptyMessage(5);
		else if(dispatchableEntityClazz.equals(InvalidMove.class)) return new InvalidMove(messageToken, color, isYourTurn);
		else if(dispatchableEntityClazz.equals(ValidMove.class)) return new ValidMove(messageToken, color, isYourTurn, from, to, columnSizeOnFrom, columnSizeOnTo, isHasMoreMoves, isEaten);
		return null;
	}
}
