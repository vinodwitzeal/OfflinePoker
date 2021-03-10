package bigcash.poker.utils;

public class PokerConstants {
    public static String POKER_SERVER = "bg-game-server";

    public static int TURN_TIME = 5*4;
    public static int BUFFER_TIME = 3;

    //Game State
    public static final int GAME_REST = 0;
    public static final int GAME_RUNNING = 1;
    public static final int GAME_RECOVERING = 2;
    public static final int GAME_FINISHED = 3;

    //Player State
    public static final int PLAYER_PLAYING = 1;
    public static final int PLAYER_FOLDED = 2;
    public static final int PLAYER_ALL_IN = 3;
    public static final int PLAYER_WATCHING = 4;

    public static int INVALID = -1;

    public static final int SUIT_SPADE = 1;
    public static final int SUIT_HEART = 2;
    public static final int SUIT_CLUB = 3;
    public static final int SUIT_DIAMOND = 4;

    //Local 1
    public static final String POKER_API = "6b6587ca-ab6f-4198-8";
    public static final String POKER_ADDRESS = "localhost";

    //Local 2
//    public static final String POKER_API = "073b0fac-3be6-4287-b";
//    public static final String POKER_ADDRESS = "192.168.1.38";

    //Server
//    public static final String POKER_API = "2a938f5c-bd9b-429d-8";
//    public static final String POKER_ADDRESS = "52.76.226.238";

    //Message Types
    public static final int MESSAGE_RECOVER_REQUEST = 1002;
    public static final int MESSAGE_RECOVER_RESPONSE = 2002;
    public static final int MESSAGE_CARDS = 2001;
    public static final int MESSAGE_LOW_BALANCE = 2003;
    public static final int MESSAGE_DEALER = 2005;
    public static final int MESSAGE_RESULT = 2007;
    public static final int MESSAGE_UPDATE_BALANCE = 2008;
    public static final int MESSAGE_TIMER = 2009;
    public static final int MESSAGE_JOIN_RESPONSE = 2010;
    public static final int MESSAGE_NEW_USER_JOIN = 2011;
    public static final int MESSAGE_LEAVE = 2012;
    public static final int MESSAGE_EMOJI=1099;
    //Move Types
    public static final String MOVE_CALL = "CALL";
    public static final String MOVE_RAISE = "RAISE";
    public static final String MOVE_CHECK = "CHECK";
    public static final String MOVE_FOLD = "FOLD";
    public static final String MOVE_ALL_IN = "ALL_IN";
    public static final String MOVE_SHOW = "SHOW";

    //Round Types
    public static final int ROUND_BLIND = 112;
    public static final int ROUND_PRE_FLOP = 113;
    public static final int ROUND_FLOP = 114;
    public static final int ROUND_TURN = 115;
    public static final int ROUND_RIVER = 116;


    public static final String KEY_ROOM_STATUS = "roomGameStatus";
    public static final String FINISH_ROOM_STATUS = "FINISH";
    public static final String RUNNING_ROOM_STATUS = "RUNNING";
    public static final String FRESH_ROOM_STATUS = "FRESH";

    public static final int GET_ROOM_WITH_FRESH_PROPERTY = 0;
    public static final int GET_ROOM_WITH_FINISH_PROPERTY = 1;
    public static final int GET_ROOM_WITH_RUNNING_PROPERTY = 2;
    public static final int GET_ROOM_WITH_ALL_PROPERTY = 3;
    public static final int GET_ROOM_WITH_RUNNING_PROPERTY_DONE = 4;
    public static final String OMAHA = "omaha";
    public static final String HOLDEM = "holdem";
    public static final String QR_HOLDEM = "qr_holdem";

    public static final String STATUS_FAIL="FAIL";
    public static final String STATUS_LOW_BALANCE="LOW_BALANCE";
    public static final String STATUS_MAINTENANCE="MAINTENANCE";

    public static final int PRIVATE_TABLE=1;
    public static final int PUBLIC_TABLE=0;
}
