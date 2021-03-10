package bigcash.poker.appwarp;

import com.google.gwt.core.client.JavaScriptObject;

public class WarpClient extends JavaScriptObject {
    protected WarpClient() {
        super();
    }

    public final native void resetListeners()/*-{
        this.resetResponseListener($wnd.AppWarp.Events.onConnectDone);
        this.resetResponseListener($wnd.AppWarp.Events.onDisconnectDone);
        this.resetResponseListener($wnd.AppWarp.Events.onGetLiveUserInfoDone);
        this.resetResponseListener($wnd.AppWarp.Events.onJoinRoomDone);
        this.resetResponseListener($wnd.AppWarp.Events.onLeaveRoomDone);
        this.resetResponseListener($wnd.AppWarp.Events.onCreateRoomDone);
        this.resetResponseListener($wnd.AppWarp.Events.onSubscribeRoomDone);
        this.resetResponseListener($wnd.AppWarp.Events.onGetLiveRoomInfoDone);
        this.resetResponseListener($wnd.AppWarp.Events.onGetMatchedRoomsDone);

        this.resetNotifyListener($wnd.AppWarp.Events.onUserJoinedRoom);
        this.resetNotifyListener($wnd.AppWarp.Events.onUserLeftRoom);
        this.resetNotifyListener($wnd.AppWarp.Events.onChatReceived);
        this.resetNotifyListener($wnd.AppWarp.Events.onUpdatePeersReceived);
        this.resetNotifyListener($wnd.AppWarp.Events.onMoveCompleted);
        this.resetNotifyListener($wnd.AppWarp.Events.onGameStarted);
        this.resetNotifyListener($wnd.AppWarp.Events.onGameStopped);
    }-*/;

    public final native void setListeners(WarpResponseListener listener)/*-{
        var that=this;
        $wnd.onConnectDone=$entry(function(res){
            console.log("onConnectDone");
            console.log(res);
            listener.@bigcash.poker.appwarp.WarpResponseListener::onConnectDone(I)(res);
        });

        $wnd.onDisconnectDone=$entry(function(res){
            console.log("onDisconnectDone");
            console.log(res);
            listener.@bigcash.poker.appwarp.WarpResponseListener::onDisconnectDone(I)(res);
        });

        $wnd.onGetLiveUserInfoDone=$entry(function(event){
            console.log("onGetLiveUserInfoDone");
            console.log(event);
            listener.@bigcash.poker.appwarp.WarpResponseListener::onGetLiveUserInfoDone(Lbigcash/poker/appwarp/events/LiveUserInfoEvent;)(event);
        });



        $wnd.onJoinRoomDone=$entry(function(event){
            console.log("onJoinRoomDone");
            console.log(event);
            listener.@bigcash.poker.appwarp.WarpResponseListener::onJoinRoomDone(Lbigcash/poker/appwarp/events/RoomEvent;)(event);
        });

        $wnd.onLeaveRoomDone=$entry(function(event){
            console.log("onLeaveRoomDone");
            console.log(event);
            listener.@bigcash.poker.appwarp.WarpResponseListener::onLeaveRoomDone(Lbigcash/poker/appwarp/events/RoomEvent;)(event);
        });

       $wnd.onCreateRoomDone=$entry(function(event){
            console.log("onCreateRoomDone");
            console.log(event);
            listener.@bigcash.poker.appwarp.WarpResponseListener::onCreateRoomDone(Lbigcash/poker/appwarp/events/RoomEvent;)(event);
        });

        $wnd.onSubscribeRoomDone=$entry(function(event){
            console.log("onSubscribeRoomDone");
            console.log(event);
            listener.@bigcash.poker.appwarp.WarpResponseListener::onSubscribeRoomDone(Lbigcash/poker/appwarp/events/RoomEvent;)(event);
        });

         $wnd.onGetLiveRoomInfoDone=$entry(function(event){
            console.log("onGetLiveRoomInfoDone");
            console.log(event);
            listener.@bigcash.poker.appwarp.WarpResponseListener::onGetLiveRoomInfoDone(Lbigcash/poker/appwarp/events/LiveRoomInfoEvent;)(event);
        });

         $wnd.onGetMatchedRoomsDone=$entry(function(event){
            console.log("onGetMatchedRoomsDone");
            console.log(event);
            listener.@bigcash.poker.appwarp.WarpResponseListener::onGetMatchedRoomsDone(Lbigcash/poker/appwarp/events/MatchedRoomEvent;)(event);
        });

       that.setResponseListener($wnd.AppWarp.Events.onConnectDone,$wnd.onConnectDone);
       that.setResponseListener($wnd.AppWarp.Events.onDisconnectDone,$wnd.onDisconnectDone);
       this.setResponseListener($wnd.AppWarp.Events.onGetLiveUserInfoDone,$wnd.onGetLiveUserInfoDone);

       this.setResponseListener($wnd.AppWarp.Events.onJoinRoomDone,$wnd.onJoinRoomDone);
       this.setResponseListener($wnd.AppWarp.Events.onLeaveRoomDone,$wnd.onLeaveRoomDone);
       this.setResponseListener($wnd.AppWarp.Events.onCreateRoomDone,$wnd.onCreateRoomDone);
       this.setResponseListener($wnd.AppWarp.Events.onSubscribeRoomDone,$wnd.onSubscribeRoomDone);
       this.setResponseListener($wnd.AppWarp.Events.onGetLiveRoomInfoDone,$wnd.onGetLiveRoomInfoDone);

       this.setResponseListener($wnd.AppWarp.Events.onGetMatchedRoomsDone,$wnd.onGetMatchedRoomsDone);

        $wnd.onUserJoinedRoom=$entry(function(event,user){
            console.log("onUserJoinedRoom");
            console.log(event);
            listener.@bigcash.poker.appwarp.WarpResponseListener::onUserJoinedRoom(Lbigcash/poker/appwarp/events/RoomEvent;Ljava/lang/String;)(event,user);
        });

        $wnd.onUserLeftRoom=$entry(function(event,user){
            console.log("onUserLeftRoom");
            console.log(event);
            listener.@bigcash.poker.appwarp.WarpResponseListener::onUserLeftRoom(Lbigcash/poker/appwarp/events/RoomEvent;Ljava/lang/String;)(event,user);
        });

        $wnd.onChatReceived=$entry(function(event){
            console.log("onChatReceived");
            console.log(event);
            listener.@bigcash.poker.appwarp.WarpResponseListener::onChatReceived(Lbigcash/poker/appwarp/events/ChatEvent;)(event);
        });

        $wnd.onUpdatePeersReceived=$entry(function(update){
            console.log("onUpdatePeersReceived");
            console.log(update);
            listener.@bigcash.poker.appwarp.WarpResponseListener::onUpdatePeersReceived([B)(update);
        });

        $wnd.onMoveCompleted=$entry(function(event){
            console.log("onMoveCompleted");
            console.log(event);
            listener.@bigcash.poker.appwarp.WarpResponseListener::onMoveCompleted(Lbigcash/poker/appwarp/events/MoveEvent;)(event);
        });

        $wnd.onGameStarted=$entry(function(sender,room,nextTurn){
            console.log("onGameStarted");
            listener.@bigcash.poker.appwarp.WarpResponseListener::onGameStarted(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)(sender,room,nextTurn);
        });

        $wnd.onGameStopped=$entry(function(sender,room){
            console.log("onGameStopped");
            listener.@bigcash.poker.appwarp.WarpResponseListener::onGameStopped(Ljava/lang/String;Ljava/lang/String;)(sender,room);
        });

        this.setNotifyListener($wnd.AppWarp.Events.onUserJoinedRoom,$wnd.onUserJoinedRoom);
        this.setNotifyListener($wnd.AppWarp.Events.onUserLeftRoom,$wnd.onUserLeftRoom);
        this.setNotifyListener($wnd.AppWarp.Events.onChatReceived,$wnd.onChatReceived);
        this.setNotifyListener($wnd.AppWarp.Events.onUpdatePeersReceived,$wnd.onUpdatePeersReceived);
        this.setNotifyListener($wnd.AppWarp.Events.onMoveCompleted,$wnd.onMoveCompleted);
        this.setNotifyListener($wnd.AppWarp.Events.onGameStarted,$wnd.onGameStarted);
        this.setNotifyListener($wnd.AppWarp.Events.onGameStopped,$wnd.onGameStopped);

    }-*/;

    public final native boolean isConnected()/*-{
        return this.isConnected;
    }-*/;

    public final native void connect(String username,String authData)/*-{
        this.connect(username,authData);
    }-*/;

    public final native void disconnect()/*-{
        this.disconnect();
    }-*/;

    public final native void recoverConnectionWithSessionId(int sessionId,String username)/*-{
        this.recoverConnectionWithSessionId(sessionId,username);
    }-*/;

    public final native void joinRoom(String roomId)/*-{
        this.joinRoom(roomId);
    }-*/;

    public final native void joinRoomWithProperties(String properties)/*-{
        this.joinRoomWithProperties(properties);
    }-*/;

    public final native void leaveRoom(String roomId)/*-{
        this.leaveRoom(roomId);
    }-*/;

    public final native void subscribeRoom(String roomId)/*-{
        this.subscribeRoom(roomId);
    }-*/;

    public final native void createTurnRoom(String name,String owner,int maxUsers,String properties,int turnTime)/*-{
        this.createTurnRoom(name,owner,maxUsers,properties,turnTime);
    }-*/;

    public final native void sendChat(String message)/*-{
        this.sendChat(message);
    }-*/;

    public final native void sendMove(String moveData)/*-{
        this.sendMove(moveData);
    }-*/;

    public final native void getRoomWithProperties(String properties)/*-{
         this.getRoomsWithProperties(properties);
    }-*/;

    public final native void sendUpdate(byte[] data)/*-{
        this.sendUpdatePeers(data);
    }-*/;

    public final native void getLiveUserInfo(String username)/*-{
        this.getLiveUserInfo(username);
    }-*/;

    public final native void getLiveRoomInfo(String roomId)/*-{
        this.getLiveRoomInfo(roomId);
    }-*/;
}
