package bigcash.poker.gwt;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.badlogic.gdx.backends.gwt.GwtGraphics;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Colors;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.google.gwt.user.client.Window;
import bigcash.poker.constants.Constant;
import bigcash.poker.downloader.TextureDownloader;
import bigcash.poker.font.FontPool;
import bigcash.poker.models.QrInfo;
import bigcash.poker.models.UIScreen;
import bigcash.poker.screens.PokerContestScreen;
import bigcash.poker.utils.GamePreferences;
import bigcash.poker.utils.PokerUtils;
import bigcash.poker.widgets.Emoji;
import bigcash.poker.widgets.Toast;

public class PokerGame extends PokerGameAdapter {
    public boolean autoRefillButtonChecked;
    public boolean gameSoundOn;
    public long backFomPokerTime;
    private TextureDownloader textureDownloader;
    public String qrId;
    public QrInfo qrInfo;
    public boolean qrContestShown;
    public GwtApplicationConfiguration appConfig;

    public void setAppConfig(GwtApplicationConfiguration appConfig){
        this.appConfig=appConfig;
    }

    @Override
    public void create() {
        textureDownloader=new TextureDownloader();
        qrId=Window.Location.getParameter("qr");
        if (qrId==null){
            qrId="";
        }

        PokerUtils.setScreen("LoadingScreen");
        FontPool.init();
        super.create();
        setListener(this);
        setContestScreen();
    }

    @Override
    public void setScreen(UIScreen screen) {
        appConfig.fullscreenOrientation= GwtGraphics.OrientationLockType.LANDSCAPE_PRIMARY;
        super.setScreen(screen);
    }

    private native void setListener(PokerGame listener)/*-{
        $wnd.setContestScreen=function(){
            listener.@bigcash.poker.gwt.PokerGame::setContestScreen()();
        }
    }-*/;

    private void setContestScreen(){
        if (PokerUtils.setAppData()){
            qrContestShown=false;
            setScreen(new PokerContestScreen(this));
        }
    }


    public boolean isAutoRefillButtonChecked() {
        return autoRefillButtonChecked;
    }

    public void setAutoRefillButtonChecked(boolean autoRefillButtonChecked) {
        this.autoRefillButtonChecked = autoRefillButtonChecked;
    }

    public boolean isGameSoundOn() {
        return gameSoundOn;
    }

    public void setGameSoundOn(boolean gameSoundOn) {
        this.gameSoundOn = gameSoundOn;
    }

    public void downloadEmoji(String name, Emoji emoji){
        textureDownloader.downloadEmoji(name,emoji);
    }

    public void downloadImage(String url, Image image){
        textureDownloader.downloadImage(url,image);
    }

    public static void resetAppWhenErrorGet(){
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
               PokerUtils.resetApp();
            }
        });
    }

    @Override
    public void dispose() {
        super.dispose();
        FontPool.dispose();
    }

    public void onBackKeyPressed(){
        Gdx.input.getInputProcessor().keyDown(Input.Keys.BACK);
    }


}
