
package org.moshe.arad.game;

import org.moshe.arad.game.player.Player;

public interface BasicGameable {

	public void initGame();
	
	public void play();
	
	public void playGameTurn(Player player);
	
	public void doWinnerActions();
	
	public boolean isHasWinner();

	public void startGame();
}
