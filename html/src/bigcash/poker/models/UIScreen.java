package bigcash.poker.models;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.utils.viewport.FitViewport;

import java.util.ArrayList;
import java.util.List;

import bigcash.poker.dialogs.IDialog;
import bigcash.poker.gwt.PokerGame;
import bigcash.poker.utils.PokerUtils;
import bigcash.poker.widgets.Toast;

public abstract class UIScreen implements Screen, InputProcessor {
    public static final int PORTRAIT = 1, LANDSCAPE = 2;
    public int orientation;
    public boolean shown;
    private String screenName;
    public Toast.ToastStyle toastStyle;
    public PokerGame pokerGame;
    public float width, height, density;
    public Stage stage;
    public FitViewport viewport;
    public Stack stack;
    private List<UIDialog> dialogs;
    private final List<Runnable> normalScreenRunnables=new ArrayList<Runnable>();
    private final List<Runnable> fullScreenRunnables=new ArrayList<Runnable>();

    public UIScreen(PokerGame pokerGame, boolean buildStage, String screenName) {
        this.orientation = PORTRAIT;
        this.pokerGame = pokerGame;
        this.screenName = screenName;
        this.toastStyle = pokerGame.toastStyle;
        PokerUtils.setScreen(screenName);
        dialogs = new ArrayList<UIDialog>();
        init();
    }

    public UIScreen(PokerGame pokerGame, String screenName) {
        this(pokerGame, true, screenName);
    }

    public void toast(String message, float duration) {
        new Toast(message, this).show(duration);
    }

    public void toast(String message) {
        toast(message, 2.0f);
    }

    public void initStage() {
        if (!PokerUtils.isMobile() && orientation == PORTRAIT) {
            int screenWidth = Gdx.graphics.getWidth();
            int screenHeight = Gdx.graphics.getHeight();
            if (screenWidth>screenHeight){
                this.height=screenHeight;
                this.width=this.height*9.0f/16.0f;
            }else {
                this.height=screenHeight;
                this.width=screenWidth;
            }
            viewport=new FitViewport(width,height);
            stage = new Stage(viewport);
            stack = new Stack();
            stack.setFillParent(true);
            stage.addActor(stack);
            getStage().getViewport().update(screenWidth, screenHeight, true);
        } else {
            this.width = Gdx.graphics.getWidth();
            this.height = Gdx.graphics.getHeight();
            viewport=new FitViewport(width,height);
            stage = new Stage(viewport);
            stack = new Stack();
            stack.setFillParent(true);
            stage.addActor(stack);
        }

        this.density=Math.min(width,height)/360.0f;

    }

    public void setInputProcessor() {
        Gdx.input.setInputProcessor(stage);
    }

    public void clearInputProcessor() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void resize(int screenWidth, int screenHeight) {
        if (viewport!=null){
            viewport.update(screenWidth,screenHeight);
        }
    }

    public Vector2 getMaskPosition(Actor actor, Vector2 position) {
        return actor.localToStageCoordinates(position);
    }

    public Vector2 localToStageCoordinates(Actor actor, Vector2 position) {
        return actor.localToStageCoordinates(position);
    }


    public void addLayer(Actor actor) {
        stack.addActor(actor);
    }

    public Stage getStage() {
        return this.stage;
    }

    public void updateBalance() {
    }


    public abstract void init();

    public abstract void build();

    public abstract void onBackKeyPressed();


    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glClearColor(0, 0, 0, 1);
        stage.act(delta);
        viewport.apply();
        stage.draw();

        if (fullScreenRunnables.size()>0){
            if (Gdx.graphics.isFullscreen()){
                synchronized (fullScreenRunnables) {
                    for (Runnable runnable : fullScreenRunnables) {
                        runnable.run();
                    }
                    fullScreenRunnables.clear();
                }
            }
        }
        if (normalScreenRunnables.size()>0){
            if (!Gdx.graphics.isFullscreen()){
                synchronized (normalScreenRunnables) {
                    for (Runnable runnable : normalScreenRunnables) {
                        runnable.run();
                    }
                    normalScreenRunnables.clear();
                }
            }
        }
    }


    public void addDialog(UIDialog dialog) {
        dialogs.add(dialog);
    }

    public void removeDialog(UIDialog dialog) {
        dialogs.remove(dialog);
    }

    @Override
    public void pause() {
        for (UIDialog dialog : dialogs) {
            dialog.pause();
        }
    }

    @Override
    public void resume() {
        for (UIDialog dialog : dialogs) {
            dialog.resume();
        }
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {

    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.BACK) {
            onBackKeyPressed();
            return true;
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }

    public void normalScreenRunnable(Runnable runnable){
        synchronized (normalScreenRunnables){
            normalScreenRunnables.add(runnable);
        }
    }

    public void fullScreenRunnable(Runnable runnable){
        synchronized (fullScreenRunnables){
            fullScreenRunnables.add(runnable);
        }
    }
}
