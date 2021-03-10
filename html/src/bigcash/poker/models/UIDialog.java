package bigcash.poker.models;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;

import bigcash.poker.gwt.PokerGame;
import bigcash.poker.utils.AssetsLoader;

public abstract class UIDialog extends Dialog {
    public UIScreen screen;
    public PokerGame pokerGame;
    public float width,height,density;
    public UIDialog(UIScreen screen) {
        super("", getWindowStyle());
        width=screen.width;
        height= screen.height;
        density = screen.density;
        this.screen = screen;
        this.pokerGame=screen.pokerGame;
        setResizeBorder(0);
        defaults().space(0);
        defaults().pad(0);
        getContentTable().defaults().space(0);
        getContentTable().defaults().pad(0);
        getButtonTable().defaults().space(0);
        getButtonTable().defaults().pad(0);
        getTitleLabel().clear();
        getButtonTable().clear();
        getContentTable().setBackground(AssetsLoader.instance().bgDialog);
        clearChildren();
        add(getContentTable()).width(width).height(height);
        setKeepWithinStage(false);
        cancel();
        init();
    }


    private static WindowStyle getWindowStyle(){
        return AssetsLoader.instance().windowStyle;
    }

    public abstract void init();

    public abstract void buildDialog();

    @Override
    public Dialog show(Stage stage) {
        stage = screen.stage;
        show(screen.stage, null);
        setPosition(Math.round((screen.width - getWidth()) / 2), Math.round((screen.height - getHeight()) / 2));
        return this;
    }

    public void toast(String message){
        screen.toast(message);
    }

    public void toast(String message,float duration){
        screen.toast(message,duration);
    }

    public void pause(){}
    public void resume(){}

    @Override
    public void hide() {
        hide(null);
    }

    public void dismissOnBack(boolean dismiss) {
        if (dismiss) {
            key(Input.Keys.BACK, "back");
        }

    }
}
