package org.moshe.arad.backgammon.turn;

import java.util.LinkedList;

import javax.annotation.Resource;

import org.moshe.arad.backgammon.player.ClassicGamePlayer;


public class ClassicGameTurnOrderManager<T extends ClassicGamePlayer> implements TurnOrderable<T> {

	private LinkedList<T> order;
	
	@Override
	public T howHasTurn() {
		return (order.peek().getTurn() != null) ? order.peek() : null;  
	}

	@Override
	public boolean passTurn() {
		if(order.peek().getTurn() != null){
			T played = order.pop();
			order.peek().setTurn(played.getTurn());
			played.setTurn(null);
			order.addLast(played);
			return true;
		}
		else return false;
	}

	@Override
	public T howIsNextInTurn() {
		return (order.peek().getTurn() != null) ? order.peekLast() : null;
	}

	public LinkedList<T> getOrder() {
		return order;
	}

	public void setOrder(LinkedList<T> order) {
		this.order = order;
	}
}
