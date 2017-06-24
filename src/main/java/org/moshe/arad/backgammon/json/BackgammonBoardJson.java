package org.moshe.arad.backgammon.json;

import java.util.ArrayList;
import java.util.List;

import org.moshe.arad.backgammon.instrument.BackgammonBoard;
import org.moshe.arad.backgammon.instrument.BlackBackgammonPawn;
import org.moshe.arad.backgammon.instrument.WhiteBackgammonPawn;
import org.moshe.arad.backgammon.move.BackgammonBoardLocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class BackgammonBoardJson {

	private List<BoardItemJson> backgammonItems;
	private int eatenWhiteCount;
	private int eatenBlackCount;
	private int whiteCount;
	private int blackCount;
	
	@Autowired
	private ApplicationContext context;
	
	public BackgammonBoardJson() {
	
	}

	public BackgammonBoardJson(List<BoardItemJson> backgammonItems, int eatenWhiteCount, int eatenBlackCount,
			int whiteCount, int blackCount) {
		super();
		this.backgammonItems = backgammonItems;
		this.eatenWhiteCount = eatenWhiteCount;
		this.eatenBlackCount = eatenBlackCount;
		this.whiteCount = whiteCount;
		this.blackCount = blackCount;
	}

	public void initBoard(BackgammonBoard board){
		backgammonItems = new ArrayList<BoardItemJson>(BackgammonBoard.LENGTH);
		
		whiteCount = 0;
		blackCount = 0;
		
		for(int i=0; i<board.LENGTH; i++){
			BackgammonBoardLocation location = context.getBean(BackgammonBoardLocation.class);
			location.setIndex(i);
			if(board.isEmptyColumn(location)){
				BoardItemJson boardItemJson = context.getBean(BoardItemJson.class);
				boardItemJson.setSign("E");
				boardItemJson.setCount(0);
				boardItemJson.setColumnIndex(i);
				backgammonItems.add(i, boardItemJson);
			}
			else if(board.peekAtColumn(location) instanceof WhiteBackgammonPawn){
				BoardItemJson boardItemJson = context.getBean(BoardItemJson.class);
				boardItemJson.setSign("W");
				whiteCount += board.getSizeOfColumn(location);
				boardItemJson.setCount(board.getSizeOfColumn(location));
				boardItemJson.setColumnIndex(i);
				backgammonItems.add(i, boardItemJson);
			}
			else if(board.peekAtColumn(location) instanceof BlackBackgammonPawn){
				BoardItemJson boardItemJson = context.getBean(BoardItemJson.class);
				boardItemJson.setSign("B");
				blackCount += board.getSizeOfColumn(location);
				boardItemJson.setCount(board.getSizeOfColumn(location));
				boardItemJson.setColumnIndex(i);
				backgammonItems.add(i, boardItemJson);
			}
		}
		
		eatenWhiteCount = board.getEatenWhites().size();
		eatenBlackCount = board.getEatenBlacks().size();	

	}
	
	@Override
	public String toString() {
		return "BackgammonBoardJson [backgammonItems=" + backgammonItems + ", eatenWhiteCount=" + eatenWhiteCount
				+ ", eatenBlackCount=" + eatenBlackCount + ", whiteCount=" + whiteCount + ", blackCount=" + blackCount
				+ "]";
	}

	public List<BoardItemJson> getBackgammonItems() {
		return backgammonItems;
	}

	public void setBackgammonItems(List<BoardItemJson> backgammonItems) {
		this.backgammonItems = backgammonItems;
	}

	public int getEatenWhiteCount() {
		return eatenWhiteCount;
	}

	public void setEatenWhiteCount(int eatenWhiteCount) {
		this.eatenWhiteCount = eatenWhiteCount;
	}

	public int getEatenBlackCount() {
		return eatenBlackCount;
	}

	public void setEatenBlackCount(int eatenBlackCount) {
		this.eatenBlackCount = eatenBlackCount;
	}

	public int getWhiteCount() {
		return whiteCount;
	}

	public void setWhiteCount(int whiteCount) {
		this.whiteCount = whiteCount;
	}

	public int getBlackCount() {
		return blackCount;
	}

	public void setBlackCount(int blackCount) {
		this.blackCount = blackCount;
	}
}
