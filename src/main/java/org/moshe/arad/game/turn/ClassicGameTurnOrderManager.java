package org.moshe.arad.game.turn;

import java.util.LinkedList;

import javax.annotation.Resource;

import org.moshe.arad.game.player.ClassicGamePlayer;
import org.moshe.arad.game.player.Player;

public class ClassicGameTurnOrderManager implements TurnOrderable {

	private LinkedList<ClassicGamePlayer> order;
	
	@Override
	public ClassicGamePlayer howHasTurn() {
		return (order.peek().getTurn() != null) ? order.peek() : null;  
	}

	@Override
	public boolean passTurn() {
		if(order.peek().getTurn() != null){
			ClassicGamePlayer played = order.pop();
			order.peek().setTurn(played.getTurn());
			played.setTurn(null);
			order.addLast(played);
			return true;
		}
		else return false;
	}

	@Override
	public Player howIsNextInTurn() {
		return (order.peek().getTurn() != null) ? order.peekLast() : null;
	}

	public LinkedList<ClassicGamePlayer> getOrder() {
		return order;
	}

	public void setOrder(LinkedList<ClassicGamePlayer> order) {
		this.order = order;
	}
}
