package bigcash.poker.gwt;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;

import bigcash.poker.font.FontPool;
import bigcash.poker.font.FontType;
import bigcash.poker.models.UIScreen;
import bigcash.poker.utils.PokerUtils;
import bigcash.poker.utils.TimeoutHandler;
import bigcash.poker.widgets.ProcessView;

public class PokerGameAdapter implements ApplicationListener {
    private UIScreen screen;
    private FitViewport viewport;
    private Stage stage;
    private boolean renderStage;
    private Label messageLabel;
    private int previousWidth, previousHeight;



    @Override
    public void create() {
        viewport=new FitViewport(Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
        stage = new Stage(viewport);
        Stack stack = new Stack();
        stack.setFillParent(true);
        stage.addActor(stack);
        float processSize = Math.min(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()) * 0.1f;
        ProcessView processView = new ProcessView();
        Label.LabelStyle messageStyle = new Label.LabelStyle();
        messageStyle.font = FontPool.obtain(FontType.ROBOTO_REGULAR, 7);
        messageStyle.fontColor = Color.WHITE;
        Pixmap pixmap=new Pixmap(6,6, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.valueOf("000000dd"));
        pixmap.fill();
        Texture pixmapTexture=new Texture(pixmap);
        pixmapTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        pixmap.dispose();
        NinePatchDrawable background = new NinePatchDrawable(new NinePatch(pixmapTexture, 1, 1, 1, 1));
        Table mainTable = new Table();
        mainTable.setBackground(background);
        mainTable.add(processView).width(processSize).height(processSize).row();
        messageLabel = new Label("Please Wait...", messageStyle);
        mainTable.add(messageLabel);
        stack.addActor(mainTable);
    }

    public void setScreen(UIScreen screen) {
        this.messageLabel.setText("Please Wait...");
        if (this.screen != null) {
            this.screen.hide();
        }
        this.screen = screen;
        if (this.screen != null) {
            if (checkOrientation()) {
                this.screen.initStage();
                this.screen.show();
                this.screen.setInputProcessor();
                this.screen.shown = true;
            } else {
                showAlert();
            }
        }
    }

    public UIScreen getScreen() {
        return this.screen;
    }


    private boolean checkOrientation() {
        return checkOrientation(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    private boolean checkOrientation(int width, int height) {
        previousWidth = width;
        previousHeight = height;
        if (this.screen.orientation == UIScreen.PORTRAIT) {
            if (PokerUtils.isMobile()){
                return width<height;
            }
            return true;
        } else {
            return width > height;
        }
    }

    private void showAlert() {
        screen.clearInputProcessor();
        String orientationMessage;
        renderStage = true;
        if (screen.orientation == UIScreen.LANDSCAPE) {
            if (!screen.shown) {
                Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
            }
            orientationMessage = "Please change browser orientation to landscape";
        } else {
            if (!screen.shown) {
                if (Gdx.graphics.isFullscreen()) {
                    Graphics.DisplayMode displayMode = Gdx.graphics.getDisplayMode();
                    Gdx.graphics.setWindowedMode(displayMode.width, displayMode.height);
                }
            }
            orientationMessage = "Please change browser orientation to portrait";
        }
        if (screen.shown){
            Gdx.app.postRunnable(new Runnable() {
                @Override
                public void run() {
                    messageLabel.setText(orientationMessage);
                }
            });
        }else {
            PokerUtils.setTimeOut(3000, new TimeoutHandler() {
                @Override
                public void onTimeOut() {
                    Gdx.app.postRunnable(new Runnable() {
                        @Override
                        public void run() {
                            messageLabel.setText(orientationMessage);
                        }
                    });
                }
            });
        }
    }

    private void hideAlert() {
        renderStage = false;
        screen.setInputProcessor();
    }


    @Override
    public void resize(int width, int height) {
        Gdx.app.error("Resize","Width:"+width+",Height:"+height);
        previousWidth = width;
        previousHeight = height;
        viewport.update(width,height);
        if (screen != null) {
            screen.resize(width, height);
            if (checkOrientation(width, height)) {
                if (!screen.shown) {
                    screen.initStage();
                    screen.show();
                    screen.shown = true;
                }
                hideAlert();
            } else {
                showAlert();
            }
        }
    }

    @Override
    public void render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glClearColor(0, 0, 0, 0);
        if (screen != null && screen.shown) {
            screen.render(Gdx.graphics.getDeltaTime());
        }
        if (renderStage) {
            stage.act(Gdx.graphics.getDeltaTime());
            viewport.apply();
            stage.draw();
        }
        int width = Gdx.graphics.getWidth();
        int height = Gdx.graphics.getHeight();
        if (previousWidth != width || previousHeight != height) {
            resize(width, height);
        }
    }

    @Override
    public void pause() {
        if (screen != null) {
            screen.pause();
        }
    }

    @Override
    public void resume() {
        if (screen != null) {
            screen.resume();
        }
    }

    @Override
    public void dispose() {
        if (screen != null) {
            screen.dispose();
        }
    }

}
