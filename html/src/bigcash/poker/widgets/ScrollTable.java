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

public abstract class ScrollTable extends Table {
    public Label.LabelStyle errorStyle;
    public Table contentTable;
    private Table processTable;
    private Label errorLabel;
    private PullScrollPane scrollPane;
    private long lastRefresh;
    private float processSize;
    private boolean canScroll,canRefresh;


    public ScrollTable(ScrollTableStyle style){
        this.canScroll=style.canScroll;
        this.canRefresh=style.canRefresh;
        this.processSize=style.processSize;
        errorStyle = new Label.LabelStyle();
        errorStyle.font = FontPool.obtain(FontType.ROBOTO_REGULAR, 5);
        errorStyle.fontColor = Color.DARK_GRAY;
        buildProcessTable();

        errorLabel = new Label("", errorStyle);
        errorLabel.setWrap(true);
        errorLabel.setAlignment(Align.center);
        contentTable = new Table();
        contentTable.top();
        lastRefresh = TimeUtils.millis();
        if (canScroll){
            scrollPane = new PullScrollPane(contentTable,style.scrollPaneStyle,style.refreshSize) {
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
            scrollPane.setScrollingDisabled(true, false);
        }

    }

    private void buildProcessTable() {
        processTable = new Table();
        ProcessView processView = new ProcessView();
        processTable.add(processView).width(processSize).height(processSize).row();
        Label.LabelStyle messageLabelStyle = new Label.LabelStyle();
        messageLabelStyle.font = FontPool.obtain(FontType.ROBOTO_REGULAR, 4);
        messageLabelStyle.fontColor = Color.DARK_GRAY;
        Label messageLabel = new Label("Please Wait...", messageLabelStyle);
        processTable.add(messageLabel);
    }


    public void setOnRefresh() {
        if (canRefresh) {
            clear();
            add(processTable).expand().fill();
            contentTable.clear();
            onRefresh();
        }

    }

    public abstract void onRefresh();

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
        add(errorLabel).width(getWidth() * 0.8f);
    }

    public void refreshAgain() {
        setOnRefresh();
    }


    public void onUnselected() {

    }


    public static class ScrollTableStyle{
        public float processSize,refreshSize,density;
        public ScrollPane.ScrollPaneStyle scrollPaneStyle;
        public boolean canScroll,canRefresh;

        public ScrollTableStyle(){
            canRefresh=true;
            canScroll=true;
            TextureAtlas uiAtlas=AssetsLoader.instance().uiAtlas;
            NinePatchDrawable verticalKnob = new NinePatchDrawable(new NinePatch(uiAtlas.findRegion("v_knob"), 5, 5, 5, 5));
            NinePatchDrawable horizontalKnob = new NinePatchDrawable(new NinePatch(uiAtlas.findRegion("h_knob"), 5, 5, 5, 5));
            scrollPaneStyle = new ScrollPane.ScrollPaneStyle();
            scrollPaneStyle.vScrollKnob = verticalKnob;
            scrollPaneStyle.hScrollKnob = horizontalKnob;
        }
    }
}
