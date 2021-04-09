

package bigcash.poker.game.holdem.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.google.gwt.core.shared.GWT;

import bigcash.poker.appwarp.codes.WarpResponseResultCode;
import bigcash.poker.appwarp.events.LiveRoomInfoEvent;
import bigcash.poker.appwarp.events.LiveUserInfoEvent;
import bigcash.poker.appwarp.events.MatchedRoomEvent;
import bigcash.poker.appwarp.events.MoveEvent;
import bigcash.poker.appwarp.events.RoomData;
import bigcash.poker.game.PokerWarpListener;
import bigcash.poker.gwt.PokerGame;
import bigcash.poker.models.UIScreen;
import bigcash.poker.utils.PokerConstants;

public abstract class HoldemRunningRoomListener implements PokerWarpListener {
    private UIScreen screen;
    private PokerGame pokerGame;
    private HoldemWarpController controller;
    private String qrId;
    private int contestId;
    private RoomData[] arrRoomData;
    private int indexOfCheckedRoom;

    public HoldemRunningRoomListener(UIScreen screen, HoldemWarpController controller, String qrId) {
        this.screen = screen;
        this.pokerGame = screen.pokerGame;
        this.controller = controller;
        this.qrId = qrId;

    }

    public void connect() {
        controller.setListener(this);
        controller.connect();
    }


    @Override
    public void onConnected() {
        JsonValue jsonValue=new JsonValue(JsonValue.ValueType.object);
        jsonValue.addChild("game",new JsonValue(PokerConstants.QR_HOLDEM));
        jsonValue.addChild("qrId",new JsonValue(qrId));
        controller.getRoomWithProperties(jsonValue);
    }

    @Override
    public void onConnectionFailed() {
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {

                GWT.log("Connection Failed");
            }
        });
    }

    @Override
    public void onConnectionRecovered() {

    }

    @Override
    public void onRecoverConnection() {

    }

    @Override
    public void onRecoverConnectionFailed() {

    }

    @Override
    public void onDisconnected() {
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                onGetContestId(contestId);
            }
        });
    }

    @Override
    public void onRoomJoined() {

    }

    @Override
    public void onUserJoined(String user) {

    }

    @Override
    public void onUserLeft(String user) {

    }

    @Override
    public void onSessionRecovered() {

    }


    @Override
    public void onGetLiveUserInfo(LiveUserInfoEvent liveUserInfoEvent) {

    }

    @Override
    public void onGetLiveRoomInfo(LiveRoomInfoEvent liveRoomInfoEvent) {
        if (liveRoomInfoEvent.getResult() == WarpResponseResultCode.SUCCESS) {
            if (liveRoomInfoEvent.getProperties() != null && liveRoomInfoEvent.getJoinedUsers()!= null
                    && liveRoomInfoEvent.getJoinedUsers().length>0 && liveRoomInfoEvent.getJoinedUsers().length<5) {
                String[] users = liveRoomInfoEvent.getJoinedUsers();
                if(users.length==1){
                    if(users[0].matches(controller.myID)){
                        indexOfCheckedRoom++;
                        if(arrRoomData!= null && arrRoomData.length>indexOfCheckedRoom ){
                            controller.client.getLiveRoomInfo(arrRoomData[indexOfCheckedRoom].getId());
                        }else {
                            controller.client.disconnect();
                        }
                        return;
                    }
                }

                JsonValue jsonValue = new JsonReader().parse(liveRoomInfoEvent.getProperties());
                contestId = jsonValue.getInt("variant");
                controller.client.disconnect();
            } else {
                indexOfCheckedRoom++;
                if(arrRoomData!= null && arrRoomData.length>indexOfCheckedRoom ){
                    controller.client.getLiveRoomInfo(arrRoomData[indexOfCheckedRoom].getId());
                }else {
                    controller.client.disconnect();
                }
            }
        } else {
            controller.client.disconnect();
        }

    }

    @Override
    public void onGameStarted(String turnId, String startTime) {

    }

    @Override
    public void onMoveCompleted(MoveEvent moveEvent) {

    }


    @Override
    public void onGameStopped() {

    }


    @Override
    public void handleClientMessage(String messageData, String sender) {

    }

    @Override
    public void handleServerMessage(String messageData, String sender) {

    }

    @Override
    public void onSwitchRoom() {

    }

    @Override
    public void resetOnSwitch() {

    }

    @Override
    public void onSwitchIfNoRoomAvailable() {

    }

    @Override
    public void setSwitchRoomListener() {

    }

    @Override
    public void onSwitchRoomJoined() {

    }

    @Override
    public void onAgainJoinAfterLowBalance() {

    }

    @Override
    public void onRoomLeft() {

    }


    @Override
    public void onGetMatchedRoomsDone(MatchedRoomEvent matchedRoomsEvent) {
        if (matchedRoomsEvent.getResult() == WarpResponseResultCode.SUCCESS) {
            if (matchedRoomsEvent.getRoomsData() != null && matchedRoomsEvent.getRoomsData().length > indexOfCheckedRoom) {
                arrRoomData = matchedRoomsEvent.getRoomsData();
                controller.client.getLiveRoomInfo(arrRoomData[indexOfCheckedRoom].getId());
            }else{
                controller.client.disconnect();
            }
        } else {
            controller.client.disconnect();
        }
    }

    public abstract void onGetContestId(int contestId);

}

