package bigcash.poker.game;

import com.badlogic.gdx.Gdx;

import bigcash.poker.appwarp.events.LiveRoomInfoEvent;
import bigcash.poker.appwarp.events.LiveUserInfoEvent;
import bigcash.poker.appwarp.events.MatchedRoomEvent;
import bigcash.poker.appwarp.events.MoveEvent;
import bigcash.poker.dialogs.ProcessDialog;
import bigcash.poker.gwt.PokerGame;
import bigcash.poker.screens.PokerContestScreen;

public abstract class PokerBalanceListener implements PokerWarpListener {

    private PokerAbstractScreen screen;
    private PokerGame pokerGame;
    private PokerWarpController controller;
    private ProcessDialog dialog;
    public PokerBalanceListener(PokerAbstractScreen screen, PokerWarpController controller){
        this.screen=screen;
        this.pokerGame=screen.pokerGame;
        this.controller=controller;
        this.dialog=new ProcessDialog(screen,"Connecting...");
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
                dialog.hide();
                controller.setListener(screen);
                onJoinAfterAdCash();
            }
        });
    }

    public abstract void onJoinAfterAdCash();

    @Override
    public void onConnectionFailed() {
        // dialog.showMessage("Joining game..");
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                dialog.hide();
                pokerGame.setScreen(new PokerContestScreen(pokerGame));
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
//        dialog.hide();
//        screen.rummyWorld.sendJoinRequest();
    }

    @Override
    public void onSessionRecovered() {

    }

    @Override
    public void onRoomLeft() {

    }






    @Override
    public void onGameStopped() {

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
    public void onUserJoined(String user) {

    }

    @Override
    public void onUserLeft(String user) {

    }

    @Override
    public void handleClientMessage(String messageData, String sender) {

    }

    @Override
    public void handleServerMessage(String messageData, String sender) {

    }

    @Override
    public void onAgainJoinAfterLowBalance() {

    }

    @Override
    public void onGetLiveRoomInfo(LiveRoomInfoEvent liveRoomInfoEvent) {

    }

    @Override
    public void onGetLiveUserInfo(LiveUserInfoEvent liveUserInfoEvent) {

    }

    @Override
    public void onGameStarted(String turnId, String startTime) {

    }

    @Override
    public void onMoveCompleted(MoveEvent moveEvent) {

    }

    @Override
    public void onGetMatchedRoomsDone(MatchedRoomEvent matchedRoomsEvent) {

    }
}
