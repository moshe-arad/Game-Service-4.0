package org.moshe.arad.initializer;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.moshe.arad.kafka.ConsumerToProducerQueue;
import org.moshe.arad.kafka.KafkaUtils;
import org.moshe.arad.kafka.consumers.ISimpleConsumer;
import org.moshe.arad.kafka.consumers.commands.RollDiceCommandConsumer;
import org.moshe.arad.kafka.consumers.config.InitGameRoomCompletedEventConfig;
import org.moshe.arad.kafka.consumers.config.RollDiceCommandConfig;
import org.moshe.arad.kafka.consumers.config.RollDiceGameRoomFoundEventConfig;
import org.moshe.arad.kafka.consumers.config.SimpleConsumerConfig;
import org.moshe.arad.kafka.consumers.events.InitGameRoomCompletedEventConsumer;
import org.moshe.arad.kafka.consumers.events.RollDiceGameRoomFoundEventConsumer;
import org.moshe.arad.kafka.events.DiceRolledEvent;
import org.moshe.arad.kafka.events.GameStartedEvent;
import org.moshe.arad.kafka.events.InitDiceCompletedEvent;
import org.moshe.arad.kafka.events.InitGameRoomCompletedEvent;
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
	
	private ConsumerToProducerQueue initGameRoomCompletedEventQueue;
	
	private ConsumerToProducerQueue rollDiceCommandQueue;
	
	private ConsumerToProducerQueue rollDiceGameRoomFoundEventQueue;
	
	private ExecutorService executor = Executors.newFixedThreadPool(6);
	
	private Logger logger = LoggerFactory.getLogger(AppInit.class);
	
	@Autowired
	private ApplicationContext context;
	
	public static final int NUM_CONSUMERS = 3;
	
	@Override
	public void initKafkaCommandsConsumers() {
		rollDiceCommandQueue = context.getBean(ConsumerToProducerQueue.class);
		
		for(int i=0; i<NUM_CONSUMERS; i++){
			rollDiceCommandConsumer = context.getBean(RollDiceCommandConsumer.class);
			initSingleConsumer(rollDiceCommandConsumer, KafkaUtils.ROLL_DICE_COMMAND_TOPIC, rollDiceCommandConfig, rollDiceCommandQueue);
			
			executeProducersAndConsumers(Arrays.asList(rollDiceCommandConsumer));
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
		
		executeProducersAndConsumers(Arrays.asList(gameStartedEventEventProducer,
				initDiceCompletedEventProducer,
				diceRolledEventProducer));
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
