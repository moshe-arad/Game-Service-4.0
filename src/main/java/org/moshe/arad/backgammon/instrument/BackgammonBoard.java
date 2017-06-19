package org.moshe.arad.backgammon.instrument;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import org.moshe.arad.backgammon.move.BackgammonBoardLocation;
import org.moshe.arad.backgammon.move.BoardLocation;
import org.moshe.arad.backgammon.move.Move;
import org.moshe.arad.backgammon.player.BackgammonPlayer;
import org.moshe.arad.backgammon.player.Player;
import org.moshe.arad.backgammon.turn.BackgammonTurn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class BackgammonBoard implements Board {

	public static final int LENGTH = 24;
	public static final int MAX_COLUMN = 15;
	public static final int EATEN_WHITE = 24;
	public static final int EATEN_BLACK = -1;
	public static final int OUT_WHITE = -1;
	public static final int OUT_BLACK = 24;
	private List<Deque<BackgammonPawn>> board = new ArrayList<Deque<BackgammonPawn>>(LENGTH);
	private LinkedList<BackgammonPawn> eatenBlacks = new LinkedList<BackgammonPawn>();
	private LinkedList<BackgammonPawn> eatenWhites = new LinkedList<BackgammonPawn>();
	private final Logger logger = LoggerFactory.getLogger("org.moshe.arad");
	
	public BackgammonBoard() {
		for (int i = 0; i < LENGTH; i++)
			board.add(new ArrayDeque<BackgammonPawn>(MAX_COLUMN));
	}

	public BackgammonBoard(BackgammonBoard b) throws Exception {
		if (b == null)
			throw new Exception("Board b is null.");

		for (int i = 0; i < LENGTH; i++)
			board.add(new ArrayDeque<BackgammonPawn>(MAX_COLUMN));

		for (int i = 0; i < LENGTH; i++) {
			BackgammonBoardLocation location = new BackgammonBoardLocation(i);
			BackgammonPawn pawn = b.peekAtColumn(location);
			for (int j = 0; j < b.getSizeOfColumn(location); j++)
				board.get(i).push(pawn);
		}
	}

	@Override
	public void initBoard() {
		clearBoard();

		for (int i = 0; i < 2; i++)
			board.get(0).push(new BlackBackgammonPawn());

		for (int i = 0; i < 5; i++)
			board.get(5).push(new WhiteBackgammonPawn());

		for (int i = 0; i < 3; i++)
			board.get(7).push(new WhiteBackgammonPawn());

		for (int i = 0; i < 5; i++)
			board.get(11).push(new BlackBackgammonPawn());

		for (int i = 0; i < 5; i++)
			board.get(12).push(new WhiteBackgammonPawn());

		for (int i = 0; i < 3; i++)
			board.get(16).push(new BlackBackgammonPawn());

		for (int i = 0; i < 5; i++)
			board.get(18).push(new BlackBackgammonPawn());

		for (int i = 0; i < 2; i++)
			board.get(23).push(new WhiteBackgammonPawn());
	}
	
	@Override
	public void clearBoard() {
		for (Deque<BackgammonPawn> column : board) {
			column.clear();
		}
	}

	@Override
	public void display(){
		System.out.println(this);
	}

	@Override
	public boolean isHasMoreMoves(Player player) throws Exception {
		if(player == null) throw new Exception("Player is null."); 
		BackgammonPlayer backgammonPlayer = (BackgammonPlayer)player;
		BackgammonTurn turn = backgammonPlayer.getTurn();
		Dice first =  turn.getFirstDice();
		Dice second = turn.getSecondDice();		
		
		if(first.getValue() == BackgammonDice.NONE && second.getValue() == BackgammonDice.NONE) return false;
		else if(backgammonPlayer.isWhite()) return checkWhiteHasMoreMoves(first, second, backgammonPlayer);
		else if(!backgammonPlayer.isWhite()) return checkBlackHasMoreMoves(first, second, backgammonPlayer);
		else throw new RuntimeException();
	}
	
	@Override
	public boolean isValidMove(Player player, Move move) throws Exception {
		int fromIndex, toIndex, step;
		BackgammonPlayer backgammonPlayer = (BackgammonPlayer)player;
		BackgammonTurn turn = backgammonPlayer.getTurn();
		
		if(player == null || move == null) throw new Exception("player or move null.");
		else{
			fromIndex = ((BackgammonBoardLocation)move.getFrom()).getIndex();
			toIndex = ((BackgammonBoardLocation)move.getTo()).getIndex();
			BackgammonBoardLocation toLocation = ((BackgammonBoardLocation)move.getTo());
			step = fromIndex - toIndex < 0 ? (fromIndex - toIndex)*(-1) : fromIndex - toIndex;
			BackgammonPawn pawnFrom = popPawnFromEatenQueueOrPopFromColumn(fromIndex);
			Dice firstDice = turn.getFirstDice();
			Dice secondDice = turn.getSecondDice();
	
			try {
				if(pawnFrom == null) return false;
				if(!pawnFrom.isAbleToDo(move)) return false;
				if(!isDicesHaveCorrectValue(firstDice, secondDice, fromIndex, toIndex, step)) return false;
				if(!player.isCanPlayWith(pawnFrom)) return false;
				if(!eatenPawnsValidation(fromIndex, pawnFrom)) return false;
				if(!isPawnCanBeSetIn(pawnFrom, toLocation)) return false;
				if(!isEatenPawnCanComeBack(fromIndex, pawnFrom, toLocation)) return false; 
				if(!isCanTakePawnOutside(toIndex)) return false;
			} catch (Exception e) {
				logger.error(e.getMessage());
				e.printStackTrace();
			}
		}
		return true;
	}
	
	@Override
	public void executeMove(Player player, Move move) throws Exception {
		int fromIndex, toIndex;
		
		if(player == null || move == null) throw new Exception("player or move are null.");
		else{
			BackgammonPlayer backgammonPlayer = (BackgammonPlayer)player;
			fromIndex = ((BackgammonBoardLocation)move.getFrom()).getIndex();
			toIndex = ((BackgammonBoardLocation)move.getTo()).getIndex();
			if(backgammonPlayer.isWhite()) executeWhiteBackgammonMove(fromIndex, toIndex);
			else executeBlackBackgammonMove(fromIndex, toIndex);
		}
	}
	
	@Override
	public boolean setPawn(Pawn pawn, BoardLocation location) {
		if ((pawn == null) || (location == null)) return false;
		else {
			int locationIndex = ((BackgammonBoardLocation) location).getIndex();
			BackgammonPawn backgammonPawn = (BackgammonPawn) pawn;

			if (locationIndex < 0 || locationIndex > LENGTH - 1) {
				return false;
			} else {
				if (!isCanSetOnDestination(locationIndex, backgammonPawn)) {
					logger.warn("Can't place different kind of pawns on the same column.");
					return false;
				} else if (board.get(locationIndex).size() == MAX_COLUMN) {
					logger.warn("This column is full.");
					return false;
				} else {
					board.get(locationIndex).push(backgammonPawn);
					return true;
				}
			}
		}
	}

	@Override
	public boolean isWinner(Player player) throws Exception {
		if(player == null) throw new Exception("player is null.");
		BackgammonPlayer backgammonPlayer = (BackgammonPlayer)player;
		return !isHasColor(backgammonPlayer.isWhite());
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BackgammonBoard other = (BackgammonBoard) obj;
		if (board == null) {
			if (other.board != null)
				return false;
		} else if (!this.checkBoardEquality(other))
			return false;
		return true;
	}

	@Override
	public String toString() {
		BackgammonBoard boardCopy = null;
		
		try {
			boardCopy = new BackgammonBoard(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		StringBuilder sb = new StringBuilder();
		sb.append("       ** The Board **").append("\n");

		sb.append("  ##############################").append("\n");
		sb.append("  # 1 1 0 0 0 0    0 0 0 0 0 0 #").append("\n");
		sb.append("  # 1 0 9 8 7 6    5 4 3 2 1 0 #").append("\n");
		sb.append("  #-------------  -------------#").append("\n");
		printUpperBoard(boardCopy, sb);

		sb.append("  #                            #\n");

		printBottomBoard(boardCopy, sb);
		sb.append("  #-------------  -------------#").append("\n");
		sb.append("  # 1 1 1 1 1 1    1 1 2 2 2 2 #").append("\n");
		sb.append("  # 2 3 4 5 6 7    8 9 0 1 2 3 #").append("\n");
		sb.append("  ##############################").append("\n");

		printHowManyPawnsOutside();

		return sb.toString();
	}
	
	/**
	 * 
	 * @param index
	 * @return return null if empty.
	 */
	public BackgammonPawn peekAtColumn(BackgammonBoardLocation location) {
		if(location == null) throw new NullPointerException("location is null.");
		else{
			int locationIndex = location.getIndex();
			
			if (locationIndex < 0 || locationIndex > LENGTH - 1) {
				throw new IndexOutOfBoundsException("Index value out of bounds.");
			}
			return board.get(locationIndex).peek();
		}
	}

	public int getSizeOfColumn(BackgammonBoardLocation location) {
		if(location == null) return -1;
		else{
			int locationIndex = location.getIndex();
			
			if (locationIndex < 0 || locationIndex > (LENGTH - 1)) {
				return -1;
			}
			return board.get(locationIndex).size();
		}		
	}

	public boolean isEmptyColumn(BackgammonBoardLocation location) {
		if(location == null) return false;
		else{
			int locationIndex = location.getIndex();
			
			if (locationIndex < 0 || locationIndex > (LENGTH - 1)) {
				return false;
			}
			return board.get(locationIndex).isEmpty();
		}
	}

	public BackgammonPawn popAtColumn(BackgammonBoardLocation location) {
		if(location == null) throw new NullPointerException("location is null.");
		else{
			int locationIndex = location.getIndex();
			
			if (locationIndex < 0 || locationIndex > (LENGTH - 1)) {
				throw new IndexOutOfBoundsException("Index value out of bounds.");
			}

			try {
				return board.get(locationIndex).pop();
			} catch (NoSuchElementException ex) {
				return null;
			}
		}
	}

	public void clearPawnsOutsideGame() {
		eatenBlacks.clear();
		eatenWhites.clear();
	}
	
	public void addBlackPawnToEatenQueue(BlackBackgammonPawn pawn){
		eatenBlacks.add(pawn);
	}
	
	public void addWhitePawnToEatenQueue(WhiteBackgammonPawn pawn){
		eatenWhites.add(pawn);
	}
	
	public int getBlackEatenSize(){
		return eatenBlacks.size();
	}
	
	public int getWhiteEatenSize(){
		return eatenWhites.size();
	}
	
	private boolean isCanSetOnDestination(int locationIndex, BackgammonPawn backgammonPawn) {
		return (board.get(locationIndex).size() > 0) && (board.get(locationIndex).peek() != null) && (!board.get(locationIndex).peek().equals(backgammonPawn)) ? false : true;
	}
	
	private BackgammonPawn popPawnFromEatenQueueOrPopFromColumn(int fromIndex){
		try{
			if(fromIndex == EATEN_WHITE) return eatenWhites.peek();
			else if(fromIndex == EATEN_BLACK) return eatenBlacks.peek();
			else return board.get(fromIndex).peek();
		}catch(NoSuchElementException ex){
			return null;
		}
	}
	
	private void printHowManyPawnsOutside() {
		StringBuilder sb = new StringBuilder();
		
		if (eatenBlacks.size() > 0)
			sb.append("  There are " + eatenBlacks.size() + " black pawns outside the game.").append("\n");
		if (eatenWhites.size() > 0)
			sb.append("  There are " + eatenWhites.size() + " white pawns outside the game.").append("\n");
		logger.info(sb.toString());
	}

	private void printUpperBoard(BackgammonBoard boardCopy, StringBuilder sb) {
		BackgammonBoardLocation backgammonLocation;
		
		for (int i = 0; i < 7; i++) {
			sb.append("  #");
			for (int j = 11; j > -1; j--) {
				backgammonLocation = new BackgammonBoardLocation(j);
				if (i == 0) {					
					int pawnCount = boardCopy.getSizeOfColumn(backgammonLocation);
					String pawnCountStr = Integer.toHexString(pawnCount).toUpperCase();
					sb.append(" ").append(pawnCountStr);
					if (j == 6)
						sb.append("   ");
					if (j == 0)
						sb.append(" ");
				} else if (i == 1) {
					sb.append("-------------  -------------");
					break;
				} else {
					if (boardCopy.isEmptyColumn(backgammonLocation)) {
						sb.append("|*");
					} else {
						BackgammonPawn p = boardCopy.popAtColumn(backgammonLocation);
						if (p instanceof BlackBackgammonPawn) {
							sb.append("|B");
						} else {
							sb.append("|W");
						}
					}

					if (j == 6)
						sb.append("|  ");
					if (j == 0)
						sb.append("|");
				}
			}
			sb.append("#\n");
		}
	}

	private void printBottomBoard(BackgammonBoard boardCopy, StringBuilder sb) {
		BackgammonBoardLocation backgammonLocation;
		StringBuilder sbPawns = new StringBuilder();
		for (int i = 0; i < 7; i++) {
			sb.append("  #");

			if (i == 6) {
				sb.append(sbPawns.toString()).append(" #\n");
				continue;
			} else if (i == 5) {
				sb.append("-------------  -------------#").append("\n");
				continue;
			}

			for (int j = 12; j < 24; j++) {
				backgammonLocation = new BackgammonBoardLocation(j);
				if (i == 0) {
					int pawnCount = boardCopy.getSizeOfColumn(backgammonLocation);
					String pawnCountStr = Integer.toHexString(pawnCount).toUpperCase();
					sbPawns.append(" ").append(pawnCountStr);
					if (j == 17)
						sbPawns.append("   ");
				}

				if (i + 1 + boardCopy.getSizeOfColumn(backgammonLocation) <= 5) {
					sb.append("|*");
				} else {
					BackgammonPawn p = boardCopy.popAtColumn(backgammonLocation);
					if (p instanceof BlackBackgammonPawn) {
						sb.append("|B");
					} else {
						sb.append("|W");
					}
				}

				if (j == 17)
					sb.append("|  ");
				if (j == 23)
					sb.append("|");
			}
			sb.append("#\n");
		}
	}

	private boolean checkBoardEquality(BackgammonBoard other) {
		BackgammonBoardLocation backgammonLOcation;
		
		for (int i = 0; i < board.size(); i++) {
			backgammonLOcation = new BackgammonBoardLocation(i);
			if (board.get(i).size() != other.getSizeOfColumn(backgammonLOcation))
				return false;
			else if (board.get(i).size() > 0) {
				BackgammonPawn pawn = board.get(i).peek();
				BackgammonPawn otherPawn = other.peekAtColumn(backgammonLOcation);
				if(!pawn.equals(otherPawn)) return false;
			}
		}
		return true;
	}

	private void executeWhiteBackgammonMove(int fromIndex, int toIndex){
		BackgammonPawn pawnFrom;
		
		if(fromIndex == EATEN_WHITE) pawnFrom = eatenWhites.pop();
		else pawnFrom = board.get(fromIndex).pop();
		if(toIndex != OUT_WHITE){
			if(board.get(toIndex).peek() != null && !board.get(toIndex).peek().equals(pawnFrom)){
				eatenBlacks.add(board.get(toIndex).pop());
			}
			board.get(toIndex).push(pawnFrom);
		}
	}
	
	private void executeBlackBackgammonMove(int fromIndex, int toIndex){
		BackgammonPawn pawnFrom;
		
		if(fromIndex == EATEN_BLACK) pawnFrom = eatenBlacks.pop();
		else pawnFrom = board.get(fromIndex).pop();
		if(toIndex != OUT_BLACK){
			if(board.get(toIndex).peek() != null && !board.get(toIndex).peek().equals(pawnFrom)){
				eatenWhites.add(board.get(toIndex).pop());
			}
			board.get(toIndex).push(pawnFrom);
		}
	}

	private boolean isDicesHaveCorrectValue(Dice first, Dice second, int fromIndex, int toIndex, int step){
		return isDicesHaveCorrectValueNotForOuting(first, second, toIndex, step) ||
				isDicesHaveCorrectValueForOutingBlack(first, second, fromIndex, toIndex, step) ||
				isDicesHaveCorrectValueForOutingWhite(first, second, fromIndex, toIndex, step);
	}
	
	private boolean isDicesHaveCorrectValueNotForOuting(Dice first, Dice second, int toIndex, int step){
		if(toIndex != OUT_BLACK && toIndex != OUT_WHITE){
			if(first.getValue() != step && second.getValue() != step) return false;
			else return true;
		}
		else return false;
	}
	
	private boolean isDicesHaveCorrectValueForOutingBlack(Dice first, Dice second, int fromIndex, int toIndex, int step){
		if(toIndex == OUT_BLACK && isBlackCanTakePawnOutside()){
			if(first.getValue() == step || second.getValue() == step) return true;
			else if(first.getValue() > step || second.getValue() > step){
				if(!isBlackPawnsBetweenFromIndexAndHighDice(first, second, fromIndex)) return true;
			}
		}
		return false;
	}

	private boolean isBlackPawnsBetweenFromIndexAndHighDice(Dice first, Dice second, int fromIndex) {
		int highValue = first.getValue() >= second.getValue() ? first.getValue() : second.getValue();
		BackgammonPawn fromPawn = board.get(fromIndex).peek();
		for(int i=(OUT_BLACK-highValue); i<fromIndex; i++){
			BackgammonPawn current = board.get(i).peek();
			if(current != null && current.equals(fromPawn)) return true;
		}
		return false;
	}
	
	private boolean isWhitePawnsBetweenFromIndexAndHighDice(Dice first, Dice second, int fromIndex) {
		int highValue = first.getValue() >= second.getValue() ? first.getValue() : second.getValue();
		BackgammonPawn fromPawn = board.get(fromIndex).peek();
		for(int i=(OUT_WHITE + highValue); i>fromIndex; i--){
			BackgammonPawn current = board.get(i).peek();
			if(current != null && current.equals(fromPawn)) return true;
		}
		return false;
	}
	
	private boolean isDicesHaveCorrectValueForOutingWhite(Dice first, Dice second, int fromIndex, int toIndex, int step){
		if(toIndex == OUT_WHITE && isWhiteCanTakePawnOutside()){
			if(first.getValue() == step || second.getValue() == step) return true;
			else if(first.getValue() > step || second.getValue() > step){
				if(!isWhitePawnsBetweenFromIndexAndHighDice(first, second, fromIndex)) return true;
			}
		}
		return false;
	}
	
	private boolean eatenPawnsValidation(int fromIndex, BackgammonPawn pawnFrom){
		if(fromIndex != EATEN_WHITE && eatenWhites.size() > 0 && pawnFrom.equals(eatenWhites.peek())) return false;
		if(fromIndex != EATEN_BLACK && eatenBlacks.size() > 0 && pawnFrom.equals(eatenBlacks.peek())) return false;
		return true;
	}
	
	private boolean isPawnCanBeSetIn(BackgammonPawn pawn, BackgammonBoardLocation to){
		if(to.getIndex() == OUT_BLACK || to.getIndex() == OUT_WHITE) return true;
		BackgammonPawn other = board.get(to.getIndex()).peek();
		int sizeAtTo = board.get(to.getIndex()).size();
		
		if(other == null) return true;
		if(!other.equals(pawn) && sizeAtTo > 1) return false;
		else return true;
	}
	
	private boolean isEatenPawnCanComeBack(int fromIndex, BackgammonPawn pawnFrom, BackgammonBoardLocation toLocation){
		if(fromIndex == EATEN_WHITE && !isEatenWhiteCanComeBack(pawnFrom, toLocation)) return false;
		if(fromIndex == EATEN_BLACK && !isEatenBlackCanComeBack(pawnFrom, toLocation)) return false;
		return true;
	}
	
	private boolean isEatenWhiteCanComeBack(BackgammonPawn pawn, BackgammonBoardLocation to){
		if(eatenWhites.size() == 0) return false;
		return isPawnCanBeSetIn(pawn, to);
	}
	
	private boolean isEatenBlackCanComeBack(BackgammonPawn pawn, BackgammonBoardLocation to){
		if(eatenBlacks.size() == 0) return false;
		return isPawnCanBeSetIn(pawn, to);
	}
	
	private boolean isCanTakePawnOutside(int toIndex){
		if(toIndex == OUT_WHITE && !isWhiteCanTakePawnOutside()) return false;
		if(toIndex == OUT_BLACK && !isBlackCanTakePawnOutside()) return false;
		return true;
	}
	
	private boolean isWhiteCanTakePawnOutside(){
		for(int i=6; i<24; i++){
			if(board.get(i).peek() instanceof WhiteBackgammonPawn) return false;
		}
		return true;
	}
	
	private boolean isBlackCanTakePawnOutside(){
		for(int i=0; i<18; i++){
			if(board.get(i).peek() instanceof BlackBackgammonPawn) return false;
		}
		return true;
	}

	private boolean isHasColor(boolean isWhite){
		for(int i=0; i<LENGTH; i++){
			if(isWhite && (board.get(i).peek() instanceof WhiteBackgammonPawn)) return true;
			if(!isWhite && (board.get(i).peek() instanceof BlackBackgammonPawn)) return true;
		}
		return false;
	}
	
	private boolean checkBlackHasMoreMoves(Dice first, Dice second, BackgammonPlayer player) {
		if(first.getValue() != BackgammonDice.NONE && second.getValue() != BackgammonDice.NONE){
			if(!isBlackHasMoreMoves(first) && !isBlackHasMoreMoves(second)) return false;
			else return true;
		}
		
		if(first.getValue() != BackgammonDice.NONE){
			if(!isBlackHasMoreMoves(first)) return false;
			else return true;
		}
		
		if(second.getValue() != BackgammonDice.NONE){
			if(!isBlackHasMoreMoves(second)) return false;
			else return true;
		}
		
		return false;
	}

	private boolean checkWhiteHasMoreMoves(Dice first, Dice second, BackgammonPlayer player) {
		if(first.getValue() != BackgammonDice.NONE && second.getValue() != BackgammonDice.NONE){
			if(!isWhiteHasMoreMoves(first) && !isWhiteHasMoreMoves(second)) return false;
			else return true;
		}
		
		if(first.getValue() != BackgammonDice.NONE){
			if(!isWhiteHasMoreMoves(first)) return false;
			else return true;
		}
		
		if(second.getValue() != BackgammonDice.NONE){
			if(!isWhiteHasMoreMoves(second)) return false;
			else return true;
		}
		return false;
	}

	private boolean isWhiteHasMoreMoves(Dice dice){
		BackgammonBoardLocation toLocation = new BackgammonBoardLocation(EATEN_WHITE - dice.getValue());
		
		if(eatenWhites.size() > 0 && !isPawnCanBeSetIn(eatenWhites.peek(), toLocation)) return false;
		return true;
	}
	
	private boolean isBlackHasMoreMoves(Dice dice){
		BackgammonBoardLocation toLocation = new BackgammonBoardLocation(EATEN_BLACK + dice.getValue());
		
		if(eatenBlacks.size() > 0 && !isPawnCanBeSetIn(eatenBlacks.peek(), toLocation)) return false;
		return true;
	}
}
