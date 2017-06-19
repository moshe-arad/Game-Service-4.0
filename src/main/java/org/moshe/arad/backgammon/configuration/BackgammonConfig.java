package org.moshe.arad.backgammon.configuration;

import org.moshe.arad.backgammon.instrument.BackgammonBoard;
import org.moshe.arad.backgammon.instrument.BackgammonDice;
import org.moshe.arad.backgammon.player.BackgammonPlayer;
import org.moshe.arad.backgammon.turn.BackgammonTurn;
import org.moshe.arad.backgammon.turn.ClassicGameTurnOrderManager;
import org.moshe.arad.game.player.ClassicGamePlayer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class BackgammonConfig {

	@Bean
	@Scope("prototype")
	public BackgammonBoard getBoard(){
		BackgammonBoard board = new BackgammonBoard();
		return board;
	}
	
	@Bean
	@Scope("prototype")
	public ClassicGameTurnOrderManager<BackgammonPlayer> getBackgammonTurnManager(){
		return new ClassicGameTurnOrderManager<BackgammonPlayer>();
	}
	
	@Bean
	@Scope("prototype")
	public BackgammonTurn getBackgammonTurn(){
		return BackgammonTurn.getInstance(getFirstDice(), getSecondDice());
	}
	
	@Bean
	@Scope("prototype")
	public BackgammonDice getFirstDice(){
		return new BackgammonDice();
	}
	
	@Bean
	@Scope("prototype")
	public BackgammonDice getSecondDice(){
		return new BackgammonDice();
	}
}
