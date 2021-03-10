package bigcash.poker.game;

import bigcash.poker.appwarp.events.LiveRoomInfoEvent;
import bigcash.poker.appwarp.events.LiveUserInfoEvent;
import bigcash.poker.appwarp.events.MatchedRoomEvent;
import bigcash.poker.appwarp.events.MoveEvent;

public interface PokerWarpListener {
    void onConnected();

    void onConnectionFailed();

    void onConnectionRecovered();

    void onRecoverConnection();

    void onRecoverConnectionFailed();

    void onDisconnected();

    void onRoomJoined();

    void onSessionRecovered();

    void onUserJoined(String user);

    void onUserLeft(String user);

    void onGetLiveRoomInfo(LiveRoomInfoEvent liveRoomInfoEvent);

    void onGetLiveUserInfo(LiveUserInfoEvent liveUserInfoEvent);

    void onGameStarted(String turnId, String startTime);

    void onMoveCompleted(MoveEvent moveEvent);

    void onGameStopped();

    void handleClientMessage(String messageData, String sender);

    void handleServerMessage(String messageData, String sender);

    void onSwitchRoom();

    void resetOnSwitch();

    void onSwitchIfNoRoomAvailable();

    void setSwitchRoomListener();

    void onSwitchRoomJoined();

    void onAgainJoinAfterLowBalance();

    void onRoomLeft();

    void onGetMatchedRoomsDone(MatchedRoomEvent matchedRoomsEvent);
}
