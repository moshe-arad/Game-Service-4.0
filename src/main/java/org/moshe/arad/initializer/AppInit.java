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
import org.moshe.arad.kafka.events.BlackPawnCameBackEvent;
import org.moshe.arad.kafka.events.BlackPawnTakenOutEvent;
import org.moshe.arad.kafka.events.DiceRolledEvent;
import org.moshe.arad.kafka.events.GameStartedEvent;
import org.moshe.arad.kafka.events.InitDiceCompletedEvent;
import org.moshe.arad.kafka.events.UserMadeInvalidMoveEvent;
import org.moshe.arad.kafka.events.UserMadeMoveEvent;
import org.moshe.arad.kafka.events.WhiteAteBlackPawnEvent;
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
	
	private ConsumerToProducerQueue initGameRoomCompletedEventQueue;
	
	private ConsumerToProducerQueue rollDiceCommandQueue;
	
	private ConsumerToProducerQueue rollDiceGameRoomFoundEventQueue;
	
	private ConsumerToProducerQueue userMadeInvalidMoveEventQueue;
	
	private ConsumerToProducerQueue whitePawnCameBackEventQueue;
	
	private ConsumerToProducerQueue blackPawnCameBackEventQueue;
	
	private ConsumerToProducerQueue whitePawnTakenOutEventQueue;
	
	private ConsumerToProducerQueue blackPawnTakenOutEventQueue;
	
	private ConsumerToProducerQueue blackAteWhitePawnEventQueue;
	
	private ConsumerToProducerQueue whiteAteBlackPawnEventQueue;
	
	private ConsumerToProducerQueue userMadeMoveEventQueue;
	
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
			makeMoveCommandConsumer.setConsumerToProducer(queueMap);
			
			initSingleConsumer(makeMoveCommandConsumer, KafkaUtils.MAKE_MOVE_COMMAND_TOPIC, makeMoveCommandConfig, null);
			
			executeProducersAndConsumers(Arrays.asList(rollDiceCommandConsumer,
					makeMoveCommandConsumer));
		}
	}

	@Override
	public void initKafkaEventsConsumers() {
		initGameRoomCompletedEventQueue = context.getBean(ConsumerToProducerQueue.class);
		rollDiceGameRoomFoundEventQueue = context.getBean(ConsumerToProducerQueue.class);
		
		for(int i=0; i<NUM_CONSUMERS; i++){
			initGameRoomCompletedEventConsumer = context.getBean(InitGameRoomCompletedEventConsumer.class);
			initSingleConsumer(initGameRoomCompletedEventConsumer, KafkaUtils.INIT_GAME_ROOM_COMPLETED_EVENT_TOPIC, initGameRoomCompletedEventConfig, initGameRoomCompletedEventQueue);
			
			rollDiceGameRoomFoundEventConsumer = context.getBean(RollDiceGameRoomFoundEventConsumer.class);
			initSingleConsumer(rollDiceGameRoomFoundEventConsumer, KafkaUtils.ROLL_DICE_GAME_ROOM_FOUND_EVENT_TOPIC, rollDiceGameRoomFoundEventConfig, rollDiceGameRoomFoundEventQueue);
			
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
		
		initSingleProducer(diceRolledEventProducer, KafkaUtils.DICE_ROLLED_EVENT_TOPIC, rollDiceGameRoomFoundEventQueue);
		
		initSingleProducer(userMadeInvalidMoveEventProducer, KafkaUtils.USER_MADE_INVALID_MOVE_EVENT_TOPIC, userMadeInvalidMoveEventQueue);
		
		initSingleProducer(whitePawnCameBackEventProducer, KafkaUtils.WHITE_PAWN_CAME_BACK_EVENT_TOPIC, whitePawnCameBackEventQueue);
	
		initSingleProducer(blackPawnCameBackEventProducer, KafkaUtils.BLACK_PAWN_CAME_BACK_EVENT_TOPIC, blackPawnCameBackEventQueue);
		
		initSingleProducer(whitePawnTakenOutEventProducer, KafkaUtils.WHITE_PAWN_TAKEN_OUT_EVENT_TOPIC, whitePawnTakenOutEventQueue);
		
		initSingleProducer(blackPawnTakenOutEventProducer, KafkaUtils.BLACK_PAWN_TAKEN_OUT_EVENT_TOPIC, blackPawnTakenOutEventQueue);
		
		initSingleProducer(blackAteWhitePawnEventProducer, KafkaUtils.BLACK_ATE_WHITE_PAWN_EVENT_TOPIC, blackAteWhitePawnEventQueue);
		
		initSingleProducer(whiteAteBlackPawnEventProducer, KafkaUtils.WHITE_ATE_BLACK_PAWN_EVENT_TOPIC, whiteAteBlackPawnEventQueue);
		
		initSingleProducer(userMadeMoveEventProducer, KafkaUtils.USER_MADE_MOVE_EVENT_TOPIC, userMadeMoveEventQueue);
		
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
				userMadeMoveEventProducer));
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
