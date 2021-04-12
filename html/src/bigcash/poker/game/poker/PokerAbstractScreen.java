package bigcash.poker.game.poker;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import bigcash.poker.appwarp.events.LiveRoomInfoEvent;
import bigcash.poker.appwarp.events.LiveUserInfoEvent;
import bigcash.poker.appwarp.events.MatchedRoomEvent;
import bigcash.poker.appwarp.events.MoveEvent;
import bigcash.poker.gwt.PokerGame;
import bigcash.poker.models.UIScreen;
import bigcash.poker.utils.AssetsLoader;
import bigcash.poker.utils.GamePreferences;

public abstract class PokerAbstractScreen extends UIScreen implements PokerWarpListener {

    private PokerWarpController warpController;
    public TextureAtlas atlas,uiAtlas;
    public Texture bgSelectedAmount;
    public GamePreferences preferences;

    public PokerAbstractScreen(PokerGame pokerGame, PokerWarpController warpController, String screenName) {
        super(pokerGame, screenName);
        this.orientation=LANDSCAPE;
        this.warpController = warpController;
        atlas = AssetsLoader.instance().gameAtlas;
        uiAtlas=AssetsLoader.instance().uiAtlas;
        bgSelectedAmount =AssetsLoader.instance().bgSelectedAmount;
        preferences=GamePreferences.instance();
        this.warpController.setListener(this);
    }


    @Override
    public void onConnected() {

    }

    @Override
    public void onConnectionFailed() {

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
    public void onSessionRecovered() {

    }

    @Override
    public void onUserJoined(String user) {

    }

    @Override
    public void onUserLeft(String user) {

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
