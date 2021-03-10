package bigcash.poker.appwarp;

import bigcash.poker.appwarp.events.ChatEvent;
import bigcash.poker.appwarp.events.LiveRoomInfoEvent;
import bigcash.poker.appwarp.events.LiveUserInfoEvent;
import bigcash.poker.appwarp.events.MatchedRoomEvent;
import bigcash.poker.appwarp.events.MoveEvent;
import bigcash.poker.appwarp.events.RoomEvent;

public interface WarpResponseListener {
    void onConnectDone(int result);
    void onDisconnectDone(int result);

    void onGetLiveUserInfoDone(LiveUserInfoEvent liveUserInfoEvent);

    void onJoinRoomDone(RoomEvent roomEvent);
    void onLeaveRoomDone(RoomEvent roomEvent);
    void onCreateRoomDone(RoomEvent roomEvent);
    void onSubscribeRoomDone(RoomEvent roomEvent);
    void onGetLiveRoomInfoDone(LiveRoomInfoEvent liveRoomInfoEvent);

    void onGetMatchedRoomsDone(MatchedRoomEvent matchedRoomEvent);

//    void onUnSubscribeRoomDone(RoomEvent roomEvent);


    //Notification Listeners
    void onUserJoinedRoom(RoomEvent roomEvent, String username);
    void onUserLeftRoom(RoomEvent roomEvent, String username);

    void onChatReceived(ChatEvent chatEvent);
    void onUpdatePeersReceived(byte[] data);

    void onMoveCompleted(MoveEvent moveEvent);

    void onGameStarted(String sender, String room, String nextTurn);

    void onGameStopped(String sender, String room);



    void onZoneRPCDone(int result, String s);

    void onRoomRPCDone(int result, String s);


//    void onSetCustomRoomDataDone(LiveRoomInfoEvent liveRoomInfoEvent);
//
//    void onUpdatePropertyDone(LiveRoomInfoEvent liveRoomInfoEvent);
//
//    void onDeleteRoomDone(RoomEvent roomEvent);
//
//    void onGetAllRoomsDone(AllRoomEvent allRoomEvent);
//
//
//
//    void onGetOnlineUsersDone(RoomEvent roomEvent);
//
//
//
//    void onSetCustomUserDataDone(LiveUserInfoEvent liveUserInfoEvent);


//
//    void onJoinLobbyDone(LobbyEvent lobbyEvent);
//    void onLeaveLobbyDone(LobbyEvent lobbyEvent);
//
//    void onSubscribeLobbyDone(LobbyEvent lobbyEvent);
//    void onUnSubscribeLobbyDone(LobbyEvent lobbyEvent);
//
//    void onGetLiveLobbyInfoDone(LiveRoomInfoEvent liveRoomInfoEvent);
//
//    void onSendUpdateDone(int result);
//
//    void onSendChatDone(int result);
//
//    void onSendPrivateChatDone(int result);
//
//    void onRoomCreated(RoomEvent roomEvent);
//    void onRoomDestroyed(RoomEvent roomEvent);
//
//
//
//    void onUserLeftLobby(LobbyEvent lobbyEvent,String username);
//    void onUserJoinedLobby(LobbyEvent lobbyEvent,String username);
//
//
//
//
//
//    void onUserChangeRoomProperty(String username,Object properties,Object lockTable);
//
//
//
//    void onSendMoveDone(int result);
//
//    void onStarGameDone(int result);
//
//    void onStopGameDone(int result);
//
//    void onGetMoveHistoryDone(int result,MoveEvent[] moveEvents);
//
//

}
