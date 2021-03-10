package bigcash.poker.game;

import com.badlogic.gdx.Gdx;
import com.google.gwt.core.shared.GWT;

import bigcash.poker.appwarp.events.LiveRoomInfoEvent;
import bigcash.poker.appwarp.events.LiveUserInfoEvent;
import bigcash.poker.appwarp.events.MatchedRoomEvent;
import bigcash.poker.appwarp.events.MoveEvent;
import bigcash.poker.dialogs.ProcessDialog;
import bigcash.poker.gwt.PokerGame;
import bigcash.poker.models.UIScreen;
import bigcash.poker.utils.PokerConstants;
import bigcash.poker.utils.PokerUtils;
import bigcash.poker.utils.TimeoutHandler;

public abstract class PokerLoginListener implements PokerWarpListener {
    private UIScreen screen;
    private PokerGame pokerGame;
    private PokerWarpController controller;
    private ProcessDialog dialog;
    private String qrId;
    private int tableType;

    public PokerLoginListener(UIScreen screen, PokerWarpController controller, String qrId, int tableType){
        this.screen=screen;
        this.qrId = qrId;
        this.tableType = tableType;
        this.pokerGame=screen.pokerGame;
        this.controller=controller;
        this.dialog=new ProcessDialog(screen,"Connecting...");
    }

    public PokerLoginListener(UIScreen screen,PokerWarpController controller){
        this(screen,controller,null, PokerConstants.PUBLIC_TABLE);
    }

    public void connect(){
        dialog.show(screen.getStage());
        controller.setListener(this);
        controller.connect();
    }


    @Override
    public void onConnected() {
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                setScreen();
//                PokerUtils.openFullScreen(new TimeoutHandler() {
//                    @Override
//                    public void onTimeOut() {
//
//                    }
//                });
            }
        });
    }

    public abstract void setScreen();

    @Override
    public void onConnectionFailed() {
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                dialog.hide();
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

    }
}
