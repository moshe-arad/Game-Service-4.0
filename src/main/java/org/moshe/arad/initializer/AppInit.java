package org.moshe.arad.initializer;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.moshe.arad.kafka.ConsumerToProducerQueue;
import org.moshe.arad.kafka.KafkaUtils;
import org.moshe.arad.kafka.consumers.ISimpleConsumer;
import org.moshe.arad.kafka.consumers.commands.MakeMoveCommandConsumer;
import org.moshe.arad.kafka.consumers.commands.RollDiceCommandConsumer;
import org.moshe.arad.kafka.consumers.config.InitGameRoomCompletedEventConfig;
import org.moshe.arad.kafka.consumers.config.MakeMoveCommandConfig;
import org.moshe.arad.kafka.consumers.config.RollDiceCommandConfig;
import org.moshe.arad.kafka.consumers.config.RollDiceGameRoomFoundEventConfig;
import org.moshe.arad.kafka.consumers.config.SimpleConsumerConfig;
import org.moshe.arad.kafka.consumers.events.InitGameRoomCompletedEventConsumer;
import org.moshe.arad.kafka.consumers.events.RollDiceGameRoomFoundEventConsumer;
import org.moshe.arad.kafka.events.BackgammonEvent;
import org.moshe.arad.kafka.events.BlackAteWhitePawnEvent;
import org.moshe.arad.kafka.events.BlackPawnCameBackAndAteWhitePawnEvent;
import org.moshe.arad.kafka.events.BlackPawnCameBackEvent;
import org.moshe.arad.kafka.events.BlackPawnTakenOutEvent;
import org.moshe.arad.kafka.events.DiceRolledCanNotPlayEvent;
import org.moshe.arad.kafka.events.DiceRolledEvent;
import org.moshe.arad.kafka.events.GameStartedEvent;
import org.moshe.arad.kafka.events.InitDiceCompletedEvent;
import org.moshe.arad.kafka.events.LastMoveBlackAteWhitePawnEvent;
import org.moshe.arad.kafka.events.LastMoveBlackPawnCameBackAndAteWhitePawnEvent;
import org.moshe.arad.kafka.events.LastMoveBlackPawnCameBackEvent;
import org.moshe.arad.kafka.events.LastMoveBlackPawnTakenOutEvent;
import org.moshe.arad.kafka.events.LastMoveWhiteAteBlackPawnEvent;
import org.moshe.arad.kafka.events.LastMoveWhitePawnCameBackAndAteBlackPawnEvent;
import org.moshe.arad.kafka.events.LastMoveWhitePawnCameBackEvent;
import org.moshe.arad.kafka.events.LastMoveWhitePawnTakenOutEvent;
import org.moshe.arad.kafka.events.TurnNotPassedBlackAteWhitePawnEvent;
import org.moshe.arad.kafka.events.TurnNotPassedBlackPawnCameBackAndAteWhitePawnEvent;
import org.moshe.arad.kafka.events.TurnNotPassedBlackPawnCameBackEvent;
import org.moshe.arad.kafka.events.TurnNotPassedBlackPawnTakenOutEvent;
import org.moshe.arad.kafka.events.TurnNotPassedUserMadeMoveEvent;
import org.moshe.arad.kafka.events.TurnNotPassedWhiteAteBlackPawnEvent;
import org.moshe.arad.kafka.events.TurnNotPassedWhitePawnCameBackAndAteBlackPawnEvent;
import org.moshe.arad.kafka.events.TurnNotPassedWhitePawnCameBackEvent;
import org.moshe.arad.kafka.events.TurnNotPassedWhitePawnTakenOutEvent;
import org.moshe.arad.kafka.events.UserMadeInvalidMoveEvent;
import org.moshe.arad.kafka.events.UserMadeLastMoveEvent;
import org.moshe.arad.kafka.events.UserMadeMoveEvent;
import org.moshe.arad.kafka.events.WhiteAteBlackPawnEvent;
import org.moshe.arad.kafka.events.WhitePawnCameBackAndAteBlackPawnEvent;
import org.moshe.arad.kafka.events.WhitePawnCameBackEvent;
import org.moshe.arad.kafka.events.WhitePawnTakenOutEvent;
import org.moshe.arad.kafka.producers.ISimpleProducer;
import org.moshe.arad.kafka.producers.events.SimpleEventsProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class AppInit implements ApplicationContextAware, IAppInitializer {	
	
	private InitGameRoomCompletedEventConsumer initGameRoomCompletedEventConsumer;
	
	@Autowired
	private InitGameRoomCompletedEventConfig initGameRoomCompletedEventConfig;
	
	@Autowired
	private SimpleEventsProducer<GameStartedEvent> gameStartedEventEventProducer;
	
	private RollDiceCommandConsumer rollDiceCommandConsumer;
	
	@Autowired
	private RollDiceCommandConfig rollDiceCommandConfig;
	
	@Autowired
	private SimpleEventsProducer<InitDiceCompletedEvent> initDiceCompletedEventProducer;
	
	private RollDiceGameRoomFoundEventConsumer rollDiceGameRoomFoundEventConsumer;
	
	@Autowired
	private RollDiceGameRoomFoundEventConfig rollDiceGameRoomFoundEventConfig;
	
	@Autowired
	private SimpleEventsProducer<DiceRolledEvent> diceRolledEventProducer;
	
	@Autowired
	private SimpleEventsProducer<DiceRolledCanNotPlayEvent> diceRolledCanNotPlayEventProducer;
	
	private MakeMoveCommandConsumer makeMoveCommandConsumer;
	
	@Autowired
	private MakeMoveCommandConfig makeMoveCommandConfig;
	
	@Autowired
	private SimpleEventsProducer<UserMadeInvalidMoveEvent> userMadeInvalidMoveEventProducer;
	
	@Autowired
	private SimpleEventsProducer<WhitePawnCameBackEvent> whitePawnCameBackEventProducer;
	
	@Autowired
	private SimpleEventsProducer<BlackPawnCameBackEvent> blackPawnCameBackEventProducer;
	
	@Autowired
	private SimpleEventsProducer<WhitePawnTakenOutEvent> whitePawnTakenOutEventProducer;
	
	@Autowired
	private SimpleEventsProducer<BlackPawnTakenOutEvent> blackPawnTakenOutEventProducer;
	
	@Autowired
	private SimpleEventsProducer<BlackAteWhitePawnEvent> blackAteWhitePawnEventProducer;
	
	@Autowired
	private SimpleEventsProducer<WhiteAteBlackPawnEvent> whiteAteBlackPawnEventProducer;
	
	@Autowired
	private SimpleEventsProducer<UserMadeMoveEvent> userMadeMoveEventProducer;
	
	@Autowired
	private SimpleEventsProducer<LastMoveWhitePawnCameBackEvent> lastMoveWhitePawnCameBackEventProducer;
	
	@Autowired
	private SimpleEventsProducer<TurnNotPassedWhitePawnCameBackEvent> turnNotPassedWhitePawnCameBackEventProducer;
	
	@Autowired
	private SimpleEventsProducer<LastMoveBlackPawnCameBackEvent> lastMoveBlackPawnCameBackEventProducer;
	
	@Autowired
	private SimpleEventsProducer<TurnNotPassedBlackPawnCameBackEvent> turnNotPassedBlackPawnCameBackEventProducer;
	
	@Autowired
	private SimpleEventsProducer<LastMoveWhitePawnTakenOutEvent> lastMoveWhitePawnTakenOutEventProducer;
	
	@Autowired
	private SimpleEventsProducer<TurnNotPassedWhitePawnTakenOutEvent> turnNotPassedWhitePawnTakenOutEventProducer;
	
	@Autowired
	private SimpleEventsProducer<LastMoveBlackPawnTakenOutEvent> lastMoveBlackPawnTakenOutEventProducer;
	
	@Autowired
	private SimpleEventsProducer<TurnNotPassedBlackPawnTakenOutEvent> turnNotPassedBlackPawnTakenOutEventProducer;
	
	@Autowired
	private SimpleEventsProducer<LastMoveBlackAteWhitePawnEvent> lastMoveBlackAteWhitePawnEventProducer;
	
	@Autowired
	private SimpleEventsProducer<TurnNotPassedBlackAteWhitePawnEvent> turnNotPassedBlackAteWhitePawnEventProducer;
	
	@Autowired
	private SimpleEventsProducer<LastMoveWhiteAteBlackPawnEvent> lastMoveWhiteAteBlackPawnEventProducer;
	
	@Autowired
	private SimpleEventsProducer<TurnNotPassedWhiteAteBlackPawnEvent> turnNotPassedWhiteAteBlackPawnEventProducer;
	
	@Autowired
	private SimpleEventsProducer<UserMadeLastMoveEvent> userMadeLastMoveEventProducer;
	
	@Autowired
	private SimpleEventsProducer<TurnNotPassedUserMadeMoveEvent> turnNotPassedUserMadeMoveEventProducer;
	
	@Autowired
	private SimpleEventsProducer<WhitePawnCameBackAndAteBlackPawnEvent> whitePawnCameBackAndAteBlackPawnEventProducer;
	
	@Autowired
	private SimpleEventsProducer<LastMoveWhitePawnCameBackAndAteBlackPawnEvent> lastMoveWhitePawnCameBackAndAteBlackPawnEventProducer;
	
	@Autowired
	private SimpleEventsProducer<TurnNotPassedWhitePawnCameBackAndAteBlackPawnEvent> turnNotPassedWhitePawnCameBackAndAteBlackPawnEventProducer;
	
	@Autowired
	private SimpleEventsProducer<BlackPawnCameBackAndAteWhitePawnEvent> blackPawnCameBackAndAteWhitePawnEventProducer;
	
	@Autowired
	private SimpleEventsProducer<LastMoveBlackPawnCameBackAndAteWhitePawnEvent> lastMoveBlackPawnCameBackAndAteWhitePawnEventProducer;
	
	@Autowired
	private SimpleEventsProducer<TurnNotPassedBlackPawnCameBackAndAteWhitePawnEvent> turnNotPassedBlackPawnCameBackAndAteWhitePawnEventProducer;
	
	private ConsumerToProducerQueue initGameRoomCompletedEventQueue;
	
	private ConsumerToProducerQueue rollDiceCommandQueue;
	
//	private ConsumerToProducerQueue rollDiceGameRoomFoundEventQueue;
	
	private ConsumerToProducerQueue userMadeInvalidMoveEventQueue;
	
	private ConsumerToProducerQueue whitePawnCameBackEventQueue;
	
	private ConsumerToProducerQueue blackPawnCameBackEventQueue;
	
	private ConsumerToProducerQueue whitePawnTakenOutEventQueue;
	
	private ConsumerToProducerQueue blackPawnTakenOutEventQueue;
	
	private ConsumerToProducerQueue blackAteWhitePawnEventQueue;
	
	private ConsumerToProducerQueue whiteAteBlackPawnEventQueue;
	
	private ConsumerToProducerQueue userMadeMoveEventQueue;
	
	private ConsumerToProducerQueue lastMoveWhitePawnCameBackEventQueue;
	
	private ConsumerToProducerQueue turnNotPassedWhitePawnCameBackEventQueue;
	
	private ConsumerToProducerQueue lastMoveBlackPawnCameBackEventQueue;
	
	private ConsumerToProducerQueue turnNotPassedBlackPawnCameBackEventQueue;
	
	private ConsumerToProducerQueue lastMoveWhitePawnTakenOutEventQueue;
	
	private ConsumerToProducerQueue turnNotPassedWhitePawnTakenOutEventQueue;
	
	private ConsumerToProducerQueue lastMoveBlackPawnTakenOutEventQueue;
	
	private ConsumerToProducerQueue turnNotPassedBlackPawnTakenOutEventQueue;
	
	private ConsumerToProducerQueue lastMoveBlackAteWhitePawnEventQueue;
	
	private ConsumerToProducerQueue turnNotPassedBlackAteWhitePawnEventQueue;
	
	private ConsumerToProducerQueue lastMoveWhiteAteBlackPawnEventQueue;
	
	private ConsumerToProducerQueue turnNotPassedWhiteAteBlackPawnEventQueue;
	
	private ConsumerToProducerQueue userMadeLastMoveEventQueue;
	
	private ConsumerToProducerQueue turnNotPassedUserMadeMoveEventQueue;
	
	private ConsumerToProducerQueue whitePawnCameBackAndAteBlackPawnEventQueue;
	
	private ConsumerToProducerQueue lastMoveWhitePawnCameBackAndAteBlackPawnEventQueue;
	
	private ConsumerToProducerQueue turnNotPassedWhitePawnCameBackAndAteBlackPawnEventQueue;
	
	private ConsumerToProducerQueue blackPawnCameBackAndAteWhitePawnEventQueue;
	
	private ConsumerToProducerQueue lastMoveBlackPawnCameBackAndAteWhitePawnEventQueue;
	
	private ConsumerToProducerQueue turnNotPassedBlackPawnCameBackAndAteWhitePawnEventQueue;
	
	private ConsumerToProducerQueue diceRolledEventQueue;
	
	private ConsumerToProducerQueue diceRolledCanNotPlayQueue;
	
	private ExecutorService executor = Executors.newFixedThreadPool(6);
	
	private Logger logger = LoggerFactory.getLogger(AppInit.class);
	
	@Autowired
	private ApplicationContext context;
	
	public static final int NUM_CONSUMERS = 3;
	
	@Override
	public void initKafkaCommandsConsumers() {
		rollDiceCommandQueue = context.getBean(ConsumerToProducerQueue.class);
		userMadeInvalidMoveEventQueue = context.getBean(ConsumerToProducerQueue.class);
		whitePawnCameBackEventQueue = context.getBean(ConsumerToProducerQueue.class);
		blackPawnCameBackEventQueue = context.getBean(ConsumerToProducerQueue.class);
		whitePawnTakenOutEventQueue = context.getBean(ConsumerToProducerQueue.class);
		blackPawnTakenOutEventQueue = context.getBean(ConsumerToProducerQueue.class);
		blackAteWhitePawnEventQueue = context.getBean(ConsumerToProducerQueue.class);
		whiteAteBlackPawnEventQueue = context.getBean(ConsumerToProducerQueue.class);
		userMadeMoveEventQueue = context.getBean(ConsumerToProducerQueue.class);
		lastMoveWhitePawnCameBackEventQueue = context.getBean(ConsumerToProducerQueue.class);
		turnNotPassedWhitePawnCameBackEventQueue = context.getBean(ConsumerToProducerQueue.class);
		lastMoveBlackPawnCameBackEventQueue = context.getBean(ConsumerToProducerQueue.class);
		turnNotPassedBlackPawnCameBackEventQueue = context.getBean(ConsumerToProducerQueue.class);
		lastMoveWhitePawnTakenOutEventQueue = context.getBean(ConsumerToProducerQueue.class);
		turnNotPassedWhitePawnTakenOutEventQueue = context.getBean(ConsumerToProducerQueue.class);
		lastMoveBlackPawnTakenOutEventQueue = context.getBean(ConsumerToProducerQueue.class);
		turnNotPassedBlackPawnTakenOutEventQueue = context.getBean(ConsumerToProducerQueue.class);
		lastMoveBlackAteWhitePawnEventQueue = context.getBean(ConsumerToProducerQueue.class);
		turnNotPassedBlackAteWhitePawnEventQueue = context.getBean(ConsumerToProducerQueue.class);
		lastMoveWhiteAteBlackPawnEventQueue = context.getBean(ConsumerToProducerQueue.class);
		turnNotPassedWhiteAteBlackPawnEventQueue = context.getBean(ConsumerToProducerQueue.class);
		userMadeLastMoveEventQueue = context.getBean(ConsumerToProducerQueue.class);
		turnNotPassedUserMadeMoveEventQueue = context.getBean(ConsumerToProducerQueue.class);
		whitePawnCameBackAndAteBlackPawnEventQueue = context.getBean(ConsumerToProducerQueue.class);
		lastMoveWhitePawnCameBackAndAteBlackPawnEventQueue = context.getBean(ConsumerToProducerQueue.class);
		turnNotPassedWhitePawnCameBackAndAteBlackPawnEventQueue = context.getBean(ConsumerToProducerQueue.class);
		blackPawnCameBackAndAteWhitePawnEventQueue = context.getBean(ConsumerToProducerQueue.class);
		lastMoveBlackPawnCameBackAndAteWhitePawnEventQueue = context.getBean(ConsumerToProducerQueue.class);
		turnNotPassedBlackPawnCameBackAndAteWhitePawnEventQueue = context.getBean(ConsumerToProducerQueue.class);
		
		for(int i=0; i<NUM_CONSUMERS; i++){
			rollDiceCommandConsumer = context.getBean(RollDiceCommandConsumer.class);
			initSingleConsumer(rollDiceCommandConsumer, KafkaUtils.ROLL_DICE_COMMAND_TOPIC, rollDiceCommandConfig, rollDiceCommandQueue);
			
			makeMoveCommandConsumer = context.getBean(MakeMoveCommandConsumer.class);
			
			HashMap<Class<? extends BackgammonEvent>, ConsumerToProducerQueue> queueMap = new HashMap<>(10000);
			queueMap.put(UserMadeInvalidMoveEvent.class, userMadeInvalidMoveEventQueue);
			queueMap.put(WhitePawnCameBackEvent.class, whitePawnCameBackEventQueue);
			queueMap.put(BlackPawnCameBackEvent.class, blackPawnCameBackEventQueue);
			queueMap.put(WhitePawnTakenOutEvent.class, whitePawnTakenOutEventQueue);
			queueMap.put(BlackPawnTakenOutEvent.class, blackPawnTakenOutEventQueue);
			queueMap.put(BlackAteWhitePawnEvent.class, blackAteWhitePawnEventQueue);
			queueMap.put(WhiteAteBlackPawnEvent.class, whiteAteBlackPawnEventQueue);
			queueMap.put(UserMadeMoveEvent.class, userMadeMoveEventQueue);
			queueMap.put(LastMoveWhitePawnCameBackEvent.class, lastMoveWhitePawnCameBackEventQueue);
			queueMap.put(TurnNotPassedWhitePawnCameBackEvent.class, turnNotPassedWhitePawnCameBackEventQueue);
			queueMap.put(LastMoveBlackPawnCameBackEvent.class, lastMoveBlackPawnCameBackEventQueue);
			queueMap.put(TurnNotPassedBlackPawnCameBackEvent.class, turnNotPassedBlackPawnCameBackEventQueue);
			queueMap.put(LastMoveWhitePawnTakenOutEvent.class, lastMoveWhitePawnTakenOutEventQueue);
			queueMap.put(TurnNotPassedWhitePawnTakenOutEvent.class, turnNotPassedWhitePawnTakenOutEventQueue);
			queueMap.put(LastMoveBlackPawnTakenOutEvent.class, lastMoveBlackPawnTakenOutEventQueue);
			queueMap.put(TurnNotPassedBlackPawnTakenOutEvent.class, turnNotPassedBlackPawnTakenOutEventQueue);
			queueMap.put(LastMoveBlackAteWhitePawnEvent.class, lastMoveBlackAteWhitePawnEventQueue);
			queueMap.put(TurnNotPassedBlackAteWhitePawnEvent.class, turnNotPassedBlackAteWhitePawnEventQueue);
			queueMap.put(LastMoveWhiteAteBlackPawnEvent.class, lastMoveWhiteAteBlackPawnEventQueue);
			queueMap.put(TurnNotPassedWhiteAteBlackPawnEvent.class, turnNotPassedWhiteAteBlackPawnEventQueue);
			queueMap.put(UserMadeLastMoveEvent.class, userMadeLastMoveEventQueue);
			queueMap.put(TurnNotPassedUserMadeMoveEvent.class, turnNotPassedUserMadeMoveEventQueue);
			queueMap.put(WhitePawnCameBackAndAteBlackPawnEvent.class, whitePawnCameBackAndAteBlackPawnEventQueue);
			queueMap.put(LastMoveWhitePawnCameBackAndAteBlackPawnEvent.class, lastMoveWhitePawnCameBackAndAteBlackPawnEventQueue);
			queueMap.put(TurnNotPassedWhitePawnCameBackAndAteBlackPawnEvent.class, turnNotPassedWhitePawnCameBackAndAteBlackPawnEventQueue);
			queueMap.put(BlackPawnCameBackAndAteWhitePawnEvent.class, blackPawnCameBackAndAteWhitePawnEventQueue);
			queueMap.put(LastMoveBlackPawnCameBackAndAteWhitePawnEvent.class, lastMoveBlackPawnCameBackAndAteWhitePawnEventQueue);
			queueMap.put(TurnNotPassedBlackPawnCameBackAndAteWhitePawnEvent.class, turnNotPassedBlackPawnCameBackAndAteWhitePawnEventQueue);
			
			makeMoveCommandConsumer.setConsumerToProducer(queueMap);
			
			initSingleConsumer(makeMoveCommandConsumer, KafkaUtils.MAKE_MOVE_COMMAND_TOPIC, makeMoveCommandConfig, null);
			
			executeProducersAndConsumers(Arrays.asList(rollDiceCommandConsumer,
					makeMoveCommandConsumer));
		}
	}

	@Override
	public void initKafkaEventsConsumers() {
		initGameRoomCompletedEventQueue = context.getBean(ConsumerToProducerQueue.class);
		diceRolledEventQueue = context.getBean(ConsumerToProducerQueue.class);
		diceRolledCanNotPlayQueue = context.getBean(ConsumerToProducerQueue.class);
		
		for(int i=0; i<NUM_CONSUMERS; i++){
			initGameRoomCompletedEventConsumer = context.getBean(InitGameRoomCompletedEventConsumer.class);
			initSingleConsumer(initGameRoomCompletedEventConsumer, KafkaUtils.INIT_GAME_ROOM_COMPLETED_EVENT_TOPIC, initGameRoomCompletedEventConfig, initGameRoomCompletedEventQueue);
			
			rollDiceGameRoomFoundEventConsumer = context.getBean(RollDiceGameRoomFoundEventConsumer.class);
			
			HashMap<Class<? extends BackgammonEvent>, ConsumerToProducerQueue> queueMap = new HashMap<>(10000);
			queueMap.put(DiceRolledEvent.class, diceRolledEventQueue);
			queueMap.put(DiceRolledCanNotPlayEvent.class, diceRolledCanNotPlayQueue);
			rollDiceGameRoomFoundEventConsumer.setConsumerToProducer(queueMap);
			
			initSingleConsumer(rollDiceGameRoomFoundEventConsumer, KafkaUtils.ROLL_DICE_GAME_ROOM_FOUND_EVENT_TOPIC, rollDiceGameRoomFoundEventConfig, null);
			
			executeProducersAndConsumers(Arrays.asList(initGameRoomCompletedEventConsumer,
					rollDiceGameRoomFoundEventConsumer));
		}
	}

	@Override
	public void initKafkaCommandsProducers() {
		
	}

	@Override
	public void initKafkaEventsProducers() {
		
		initSingleProducer(gameStartedEventEventProducer, KafkaUtils.GAME_STARTED_EVENT_TOPIC, initGameRoomCompletedEventQueue);
		
		initSingleProducer(initDiceCompletedEventProducer, KafkaUtils.INIT_DICE_COMPLETED_EVENT_TOPIC, rollDiceCommandQueue);
		
		initSingleProducer(diceRolledEventProducer, KafkaUtils.DICE_ROLLED_EVENT_TOPIC, diceRolledEventQueue);
		
		initSingleProducer(diceRolledCanNotPlayEventProducer, KafkaUtils.DICE_ROLLED_CAN_NOT_PLAY_EVENT_TOPIC, diceRolledCanNotPlayQueue);
		
		initSingleProducer(userMadeInvalidMoveEventProducer, KafkaUtils.USER_MADE_INVALID_MOVE_EVENT_TOPIC, userMadeInvalidMoveEventQueue);
		
		initSingleProducer(whitePawnCameBackEventProducer, KafkaUtils.WHITE_PAWN_CAME_BACK_EVENT_TOPIC, whitePawnCameBackEventQueue);
	
		initSingleProducer(blackPawnCameBackEventProducer, KafkaUtils.BLACK_PAWN_CAME_BACK_EVENT_TOPIC, blackPawnCameBackEventQueue);
		
		initSingleProducer(whitePawnTakenOutEventProducer, KafkaUtils.WHITE_PAWN_TAKEN_OUT_EVENT_TOPIC, whitePawnTakenOutEventQueue);
		
		initSingleProducer(blackPawnTakenOutEventProducer, KafkaUtils.BLACK_PAWN_TAKEN_OUT_EVENT_TOPIC, blackPawnTakenOutEventQueue);
		
		initSingleProducer(blackAteWhitePawnEventProducer, KafkaUtils.BLACK_ATE_WHITE_PAWN_EVENT_TOPIC, blackAteWhitePawnEventQueue);
		
		initSingleProducer(whiteAteBlackPawnEventProducer, KafkaUtils.WHITE_ATE_BLACK_PAWN_EVENT_TOPIC, whiteAteBlackPawnEventQueue);
		
		initSingleProducer(userMadeMoveEventProducer, KafkaUtils.USER_MADE_MOVE_EVENT_TOPIC, userMadeMoveEventQueue);
		
		initSingleProducer(lastMoveWhitePawnCameBackEventProducer, KafkaUtils.LAST_MOVE_WHITE_PAWN_CAME_BACK_EVENT_TOPIC, lastMoveWhitePawnCameBackEventQueue);
	
		initSingleProducer(turnNotPassedWhitePawnCameBackEventProducer, KafkaUtils.TURN_NOT_PASSED_WHITE_PAWN_CAME_BACK_EVENT_TOPIC, turnNotPassedWhitePawnCameBackEventQueue);
		
		initSingleProducer(lastMoveBlackPawnCameBackEventProducer, KafkaUtils.LAST_MOVE_BLACK_PAWN_CAME_BACK_EVENT_TOPIC, lastMoveBlackPawnCameBackEventQueue);
		
		initSingleProducer(turnNotPassedBlackPawnCameBackEventProducer, KafkaUtils.TURN_NOT_PASSED_BLACK_PAWN_CAME_BACK_EVENT_TOPIC, turnNotPassedBlackPawnCameBackEventQueue);
		
		initSingleProducer(lastMoveWhitePawnTakenOutEventProducer, KafkaUtils.LAST_MOVE_WHITE_PAWN_TAKEN_OUT_EVENT_TOPIC, lastMoveWhitePawnTakenOutEventQueue);
		
		initSingleProducer(turnNotPassedWhitePawnTakenOutEventProducer, KafkaUtils.TURN_NOT_PASSED_WHITE_PAWN_TAKEN_OUT_EVENT_TOPIC, turnNotPassedWhitePawnTakenOutEventQueue);
		
		initSingleProducer(lastMoveBlackPawnTakenOutEventProducer, KafkaUtils.LAST_MOVE_BLACK_PAWN_TAKEN_OUT_EVENT_TOPIC, lastMoveBlackPawnTakenOutEventQueue);
		
		initSingleProducer(turnNotPassedBlackPawnTakenOutEventProducer, KafkaUtils.TURN_NOT_PASSED_BLACK_PAWN_TAKEN_OUT_EVENT_TOPIC, turnNotPassedBlackPawnTakenOutEventQueue);
		
		initSingleProducer(lastMoveBlackAteWhitePawnEventProducer, KafkaUtils.LAST_MOVE_BLACK_ATE_WHITE_PAWN_EVENT_TOPIC, lastMoveBlackAteWhitePawnEventQueue);
		
		initSingleProducer(turnNotPassedBlackAteWhitePawnEventProducer, KafkaUtils.TURN_NOT_PASSED_BLACK_ATE_WHITE_PAWN_EVENT_TOPIC, turnNotPassedBlackAteWhitePawnEventQueue);
		
		initSingleProducer(lastMoveWhiteAteBlackPawnEventProducer, KafkaUtils.LAST_MOVE_WHITE_ATE_BLACK_PAWN_EVENT_TOPIC, lastMoveWhiteAteBlackPawnEventQueue);
		
		initSingleProducer(turnNotPassedWhiteAteBlackPawnEventProducer, KafkaUtils.TURN_NOT_PASSED_WHITE_ATE_BLACK_PAWN_EVENT_TOPIC, turnNotPassedWhiteAteBlackPawnEventQueue);

		initSingleProducer(userMadeLastMoveEventProducer, KafkaUtils.USER_MADE_LAST_MOVE_EVENT_TOPIC, userMadeLastMoveEventQueue);
		
		initSingleProducer(turnNotPassedUserMadeMoveEventProducer, KafkaUtils.TURN_NOT_PASSED_USER_MADE_MOVE_EVENT_TOPIC, turnNotPassedUserMadeMoveEventQueue);
		
		initSingleProducer(whitePawnCameBackAndAteBlackPawnEventProducer, KafkaUtils.WHITE_PAWN_CAME_BACK_AND_ATE_BLACK_PAWN_EVENT_TOPIC, whitePawnCameBackAndAteBlackPawnEventQueue);
		
		initSingleProducer(lastMoveWhitePawnCameBackAndAteBlackPawnEventProducer, KafkaUtils.LAST_MOVE_WHITE_PAWN_CAME_BACK_AND_ATE_BLACK_PAWN_EVENT_TOPIC, lastMoveWhitePawnCameBackAndAteBlackPawnEventQueue);
		
		initSingleProducer(turnNotPassedWhitePawnCameBackAndAteBlackPawnEventProducer, KafkaUtils.TURN_NOT_PASSED_WHITE_PAWN_CAME_BACK_AND_ATE_BLACK_PAWN_EVENT_TOPIC, turnNotPassedWhitePawnCameBackAndAteBlackPawnEventQueue);
		
		initSingleProducer(blackPawnCameBackAndAteWhitePawnEventProducer, KafkaUtils.BLACK_PAWN_CAME_BACK_AND_ATE_WHITE_PAWN_EVENT_TOPIC, blackPawnCameBackAndAteWhitePawnEventQueue);
		
		initSingleProducer(lastMoveBlackPawnCameBackAndAteWhitePawnEventProducer, KafkaUtils.LAST_MOVE_BLACK_PAWN_CAME_BACK_AND_ATE_WHITE_PAWN_EVENT_TOPIC, lastMoveBlackPawnCameBackAndAteWhitePawnEventQueue);
		
		initSingleProducer(turnNotPassedBlackPawnCameBackAndAteWhitePawnEventProducer, KafkaUtils.TURN_NOT_PASSED_BLACK_PAWN_CAME_BACK_AND_ATE_WHITE_PAWN_EVENT_TOPIC, turnNotPassedBlackPawnCameBackAndAteWhitePawnEventQueue);
		
		executeProducersAndConsumers(Arrays.asList(gameStartedEventEventProducer,
				initDiceCompletedEventProducer,
				diceRolledEventProducer,
				userMadeInvalidMoveEventProducer,
				whitePawnCameBackEventProducer,
				blackPawnCameBackEventProducer,
				whitePawnTakenOutEventProducer,
				blackPawnTakenOutEventProducer,
				blackAteWhitePawnEventProducer,
				whiteAteBlackPawnEventProducer,
				userMadeMoveEventProducer,
				lastMoveWhitePawnCameBackEventProducer,
				turnNotPassedWhitePawnCameBackEventProducer,
				lastMoveBlackPawnCameBackEventProducer,
				turnNotPassedBlackPawnCameBackEventProducer,
				lastMoveWhitePawnTakenOutEventProducer,
				turnNotPassedWhitePawnTakenOutEventProducer,
				lastMoveBlackPawnTakenOutEventProducer,
				turnNotPassedBlackPawnTakenOutEventProducer,
				lastMoveBlackAteWhitePawnEventProducer,
				turnNotPassedBlackAteWhitePawnEventProducer,
				lastMoveWhiteAteBlackPawnEventProducer,
				turnNotPassedWhiteAteBlackPawnEventProducer,
				userMadeLastMoveEventProducer,
				turnNotPassedUserMadeMoveEventProducer,
				whitePawnCameBackAndAteBlackPawnEventProducer,
				lastMoveWhitePawnCameBackAndAteBlackPawnEventProducer,
				turnNotPassedWhitePawnCameBackAndAteBlackPawnEventProducer,
				blackPawnCameBackAndAteWhitePawnEventProducer,
				lastMoveBlackPawnCameBackAndAteWhitePawnEventProducer,
				turnNotPassedBlackPawnCameBackAndAteWhitePawnEventProducer,
				diceRolledCanNotPlayEventProducer));
	}

	@Override
	public void setApplicationContext(ApplicationContext context) throws BeansException {
		this.context = context;
	}

	@Override
	public void engineShutdown() {
		logger.info("about to do shutdown.");	
//		shutdownSingleProducer(userEmailAvailabilityCheckedEventProducer);
		selfShutdown();
		logger.info("shutdown compeleted.");
	}	
	
	private void initSingleConsumer(ISimpleConsumer consumer, String topic, SimpleConsumerConfig consumerConfig, ConsumerToProducerQueue queue) {
		consumer.setTopic(topic);
		consumer.setSimpleConsumerConfig(consumerConfig);
		consumer.initConsumer();	
		consumer.setConsumerToProducerQueue(queue);
	}
	
	private void initSingleProducer(ISimpleProducer producer, String topic, ConsumerToProducerQueue queue) {
		producer.setTopic(topic);	
		producer.setConsumerToProducerQueue(queue);
	}
	
	private void shutdownSingleConsumer(ISimpleConsumer consumer) {
		consumer.setRunning(false);
		consumer.getScheduledExecutor().shutdown();	
	}
	
	private void shutdownSingleProducer(ISimpleProducer producer) {
		producer.setRunning(false);
		producer.getScheduledExecutor().shutdown();	
	}
	
	private void selfShutdown(){
		this.executor.shutdown();
	}
	
	private void executeProducersAndConsumers(List<Runnable> jobs){
		for(Runnable job:jobs)
			executor.execute(job);
	}
}
