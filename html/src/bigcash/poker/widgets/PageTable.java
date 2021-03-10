package bigcash.poker.widgets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.concurrent.TimeUnit;

import bigcash.poker.font.FontPool;
import bigcash.poker.font.FontType;
import bigcash.poker.utils.AssetsLoader;

public abstract class PageTable extends Table{
    public Label.LabelStyle errorStyle;
    public float processSize;
    public Table contentTable;
    private Table processTable;
    private Label errorLabel;
    public PullScrollPane scrollPane;
    private long lastRefresh;
    private boolean canScroll,canRefresh;

    public PageTable(float processSize) {
        this(processSize,true,true);
    }

    public PageTable(float processSize,boolean canScroll,boolean canRefresh){
        this.processSize=processSize;
        this.canScroll=canScroll;
        this.canRefresh=canRefresh;
        errorStyle = new Label.LabelStyle();
        errorStyle.font = FontPool.obtain(FontType.ROBOTO_REGULAR, 5);
        errorStyle.fontColor = Color.LIGHT_GRAY;
        buildProcessTable();
        errorLabel = new Label("", errorStyle);
        errorLabel.setWrap(true);
        errorLabel.setAlignment(Align.center);
        contentTable = new Table();
        contentTable.top();
        lastRefresh = TimeUtils.millis();
        TextureAtlas uiAtlas = AssetsLoader.instance().uiAtlas;
        NinePatchDrawable verticalKnob = new NinePatchDrawable(new NinePatch(uiAtlas.findRegion("v_knob"), 5, 5, 5, 5));
        NinePatchDrawable horizontalKnob = new NinePatchDrawable(new NinePatch(uiAtlas.findRegion("h_knob"), 5, 5, 5, 5));
        ScrollPane.ScrollPaneStyle scrollPaneStyle = new ScrollPane.ScrollPaneStyle();
        scrollPaneStyle.vScrollKnob = verticalKnob;
        scrollPaneStyle.hScrollKnob = horizontalKnob;
        if (canScroll){
            scrollPane = new PullScrollPane(contentTable,scrollPaneStyle,processSize*0.2f) {
                @Override
                public boolean onPullRefresh() {
                    boolean refresh = (TimeUtils.millis() - lastRefresh) >= TimeUnit.SECONDS.toMillis(5);
                    if (refresh) {
                        Gdx.app.postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                scrollPane.setVisible(true);
                                setOnRefresh();
                            }
                        });
                    }
                    return refresh;
                }
            };

            scrollPane.setFlickScroll(true);
//                scrollPane.setScrollbarsOnTop(true);
//                scrollPane.setFadeScrollBars(false);
            scrollPane.setScrollingDisabled(true, false);
        }

    }

    private void buildProcessTable() {
        processTable = new Table();
        ProcessView processView = new ProcessView();
        processTable.add(processView).width(processSize).height(processSize).row();
        Label.LabelStyle messageLabelStyle = new Label.LabelStyle();
        messageLabelStyle.font = FontPool.obtain(FontType.ROBOTO_REGULAR, 4);
        messageLabelStyle.fontColor = Color.LIGHT_GRAY;
        Label messageLabel = new Label("Please Wait...", messageLabelStyle);
        processTable.add(messageLabel);
    }


    public void setOnRefresh() {
        if (canRefresh) {
            clear();
            add(processTable).expand().fill();
            contentTable.clear();
        }
        onRefresh();
    }

    public void clearContentTable(){
        contentTable.clear();
        contentTable.top();
    }

    public abstract void onRefresh();

    public void dispose() {
    }

    public void onRefreshCompleted() {
        clear();
        lastRefresh = TimeUtils.millis();
        if (canScroll){
            scrollPane.setScrollPercentY(0);
            add(scrollPane).expand().fill();
        }else {
            add(contentTable).expand().fill();
        }

    }

    public void onRefreshError(String message) {
        errorLabel.setText(message);
        clear();
        add(errorLabel).expandX().fillX();
    }

    public void refreshAgain() {
        setOnRefresh();
    }


    public void onUnselected() {

    }
}