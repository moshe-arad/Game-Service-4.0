package org.moshe.arad.kafka;

public class KafkaUtils {

	public static final String SERVERS = "localhost:9092,localhost:9093,localhost:9094";
	public static final String CREATE_NEW_USER_COMMAND_GROUP = "CreateNewUserCommandGroup";
	public static final String STRING_DESERIALIZER = "org.apache.kafka.common.serialization.StringDeserializer";
	public static final String STRING_SERIALIZER = "org.apache.kafka.common.serialization.StringSerializer";
	public static final String CREATE_NEW_USER_COMMAND_DESERIALIZER = "org.moshe.arad.kafka.deserializers.CreateNewUserCommandDeserializer";
	public static final String NEW_USER_CREATED_EVENT_SERIALIZER = "org.moshe.arad.kafka.serializers.NewUserCreatedEventSerializer";
	public static final String COMMANDS_TO_USERS_SERVICE_TOPIC = "Commands-To-Users-Service";
	public static final String NEW_USER_CREATED_EVENT_DESERIALIZER = "org.moshe.arad.kafka.deserializers.NewUserCreatedEventDeserializer";
	public static final String NEW_USER_CREATED_EVENT_TOPIC = "New-User-Created-Event";
	public static final String NEW_USER_JOINED_LOBBY_EVENT_TOPIC = "New-User-Joined-Lobby-Event";
	public static final String NEW_USER_JOINED_LOBBY_EVENT_SERIALIZER = "org.moshe.arad.kafka.serializers.NewUserCreatedEventSerializer";
	public static final String NEW_USER_CREATED_EVENT_GROUP = "NewUserCreatedEventGroup3";
	public static final String CHECK_USER_NAME_AVAILABILITY_COMMAND_DESERIALIZER = "org.moshe.arad.kafka.deserializers.CheckUserNameAvailabilityCommandDeserializer";
	public static final String CHECK_USER_EMAIL_AVAILABILITY_COMMAND_TOPIC = "Check-User-Email-Availability-Command";
	public static final String CHECK_USER_NAME_AVAILABILITY_COMMAND_TOPIC = "Check-User-Name-Availability-Command";
	public static final String USER_NAME_AVAILABILITY_CHECKED_EVENT_TOPIC = "User-Name-Availability-Checked-Event";
	public static final String CHECK_USER_NAME_AVAILABILITY_GROUP = "CheckUserNameAvailabilityGroup";
	public static final String USER_NAME_AVAILABILITY_CHECKED_EVENT_SERIALIZER = "org.moshe.arad.kafka.serializers.UserNameAvailabilityCheckedEventSerializer";
	public static final String CHECK_USER_EMAIL_AVAILABILITY_GROUP = "CheckUserEmailAvailabilityGroup";
	public static final String CHECK_USER_EMAIL_AVAILABILITY_COMMAND_DESERIALIZER = "org.moshe.arad.kafka.deserializers.CheckUserEmailAvailabilityCommandDeserializer";
	public static final String USER_EMAIL_AVAILABILITY_CHECKED_EVENT_SERIALIZER = "org.moshe.arad.kafka.serializers.UserEmailAvailabilityCheckedEventSerializer";
	public static final String EMAIL_AVAILABILITY_CHECKED_EVENT_TOPIC = "Email-Availability-Checked-Event";
	public static final String NEW_USER_JOINED_LOBBY_EVENT_GROUP2 = "NewUserJoinedLobbyEventGroup2";
	public static final String LOG_IN_USER_COMMAND_CONFIG_GROUP = "LogInUserCommnadConfigGroup";
	public static final String LOG_IN_USER_ACK_EVENT_TOPIC = "Log-In-User-Ack-Event";
	public static final String LOG_IN_USER_COMMAND_TOPIC = "Log-In-User-Command";
	public static final String EXISTING_USER_JOINED_LOBBY_EVENT_GROUP = "ExistingUserJoinedLobbyEventGroup2";
	public static final String EXISTING_USER_JOINED_LOBBY_EVENT_TOPIC = "Existing-User-Joined-Lobby-Event";
	public static final String LOG_OUT_USER_COMMAND_TOPIC = "Log-Out-User-Command";
	public static final String LOG_OUT_USER_COMMAND_CONFIG_GROUP = "LogOutUserCommandGroup";
	public static final String LOG_OUT_USER_ACK_EVENT_TOPIC = "Log-Out-User-Ack-Event";
	public static final String NEW_USER_CREATED_EVENT_ACK_TOPIC = "New-User-Created-Event-Ack";
	public static final String LOGGED_IN_EVENT_GROUP = "LoggedInEventGroup";
	public static final String LOGGED_IN_EVENT_TOPIC = "Logged-In-Event";
	public static final String LOGGED_IN_EVENT_ACK_TOPIC = "Logged-In-Event-Ack";
	public static final String INIT_GAME_ROOM_COMPLETED_EVENT_GROUP = "InitGameRoomCompletedEventGroup2";
	public static final String INIT_GAME_ROOM_COMPLETED_EVENT_TOPIC = "Init-Game-Room-Completed-Event";
	public static final String GAME_STARTED_EVENT_TOPIC = "Game-Started-Event";
	public static final String ROLL_DICE_COMMAND_GROUP = "RollDiceCommandGroup1";
	public static final String ROLL_DICE_COMMAND_TOPIC = "Roll-Dice-Command";
	public static final String INIT_DICE_COMPLETED_EVENT_TOPIC = "Init-Dice-Completed-Event";
	public static final String ROLL_DICE_GAME_ROOM_FOUND_EVENT_GROUP = "RollDiceGameRoomFoundEventGroup2";
	public static final String ROLL_DICE_GAME_ROOM_FOUND_EVENT_TOPIC = "Roll-Dice-Game-Room-Found-Event";
	public static final String DICE_ROLLED_EVENT_TOPIC = "Dice-Rolled-Event";
	
}