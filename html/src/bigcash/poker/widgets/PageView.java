package bigcash.poker.widgets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import bigcash.poker.font.FontPool;
import bigcash.poker.font.FontType;
import bigcash.poker.models.UIScreen;
import bigcash.poker.utils.AssetsLoader;

public class PageView extends Table {
    public ScrollPane.ScrollPaneStyle scrollPaneStyle;
    private PageContent currentPage;
    private HashMap<String, PageContent> contentHashMap;
    private FooterItem currentFooter;
    private HeaderItem currentHeader;
    private List<FooterItem> footerItems;
    private List<HeaderItem> headerItems;
    private UIScreen screen;
    private TextureAtlas uiAtlas;

    public PageView(UIScreen screen) {
        this.screen = screen;
        this.uiAtlas = AssetsLoader.instance().uiAtlas;
        NinePatchDrawable verticalKnob = new NinePatchDrawable(new NinePatch(uiAtlas.findRegion("v_knob"), 5, 5, 5, 5));
        NinePatchDrawable horizontalKnob = new NinePatchDrawable(new NinePatch(uiAtlas.findRegion("h_knob"), 5, 5, 5, 5));
        scrollPaneStyle = new ScrollPane.ScrollPaneStyle();
        scrollPaneStyle.vScrollKnob = verticalKnob;
        scrollPaneStyle.hScrollKnob = horizontalKnob;
        contentHashMap = new HashMap<String, PageContent>();
        headerItems = new ArrayList<HeaderItem>();
        footerItems = new ArrayList<FooterItem>();
    }

    public void addPageContent(HeaderItem headerItem, PageContent pageContent) {
        headerItems.add(headerItem);
        contentHashMap.put(headerItem.type + "", pageContent);
    }

    public void addPageContent(FooterItem footerItem, PageContent pageContent) {
        footerItems.add(footerItem);
        contentHashMap.put(footerItem.type + "", pageContent);
    }

    public void addPageContent(FooterItem footerItem, HeaderItem headerItem, PageContent pageContent) {
        if (!headerItems.contains(headerItem))
            headerItems.add(headerItem);
        if (!footerItems.contains(footerItem))
            footerItems.add(footerItem);
        contentHashMap.put(headerItem.type + "-" + footerItem.type, pageContent);
    }

    public PageContent getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(PageContent currentPage) {
        if (this.currentPage == null || this.currentPage != currentPage) {
            clear();
            if (this.currentPage != null) {
                this.currentPage.onUnselected();
            }
            this.currentPage = currentPage;
            this.currentPage.setOnRefresh();
        }
    }

    public FooterItem getCurrentFooter() {
        return currentFooter;
    }

    public void setCurrentFooter(final FooterItem currentFooter) {
        if (this.currentFooter != null && this.currentFooter == currentFooter) {
            return;
        }
        this.currentFooter = currentFooter;
        this.currentFooter.onSelected();
        if (headerItems.size() > 0) {

            for (int i = 0; i < headerItems.size(); i++) {
                if (i > 0) {
                    headerItems.get(i).onUnselected();
                }
            }
            HeaderItem headerItem = this.currentHeader;
            this.currentHeader = null;
            if (headerItem == null) {
                setCurrentHeader(headerItems.get(0));
            } else {
                setCurrentHeader(headerItem);
            }

        } else {
            setCurrentPage(contentHashMap.get(currentFooter.type + ""));
        }
        for (FooterItem footerItem : footerItems) {
            if (footerItem != currentFooter) {
                footerItem.onUnselected();
            }
        }
    }

    public HeaderItem getCurrentHeader() {
        return currentHeader;
    }

    public void setCurrentHeader(final HeaderItem currentHeader) {
        if (this.currentHeader != null && this.currentHeader == currentHeader) {
            return;
        }
        this.currentHeader = currentHeader;
        currentHeader.onSelected();
        final String contentValue;
        if (currentFooter != null) {
            contentValue = currentHeader.type + "-" + currentFooter.type;
        } else {
            contentValue = currentHeader.type + "";
        }
        for (HeaderItem headerItem : headerItems) {
            if (headerItem != currentHeader) {
                headerItem.onUnselected();
            }
        }

        if (contentHashMap.containsKey(contentValue)) {
            setCurrentPage(contentHashMap.get(contentValue));
        }
    }

    public void setCurrent(FooterItem footerItem, HeaderItem headerItem) {
        if (footerItem != null) {
            this.currentFooter = footerItem;
            for (FooterItem item : footerItems) {
                if (item != footerItem) {
                    item.onUnselected();
                }
            }
            this.currentFooter.onSelected();
        }

        if (headerItem != null) {
            setCurrentHeader(headerItem);
        }
    }

    public void build(String footer, String header) {
        if (headerItems.size() > 0) {
            for (final HeaderItem headerItem : headerItems) {
                headerItem.setTouchable(Touchable.enabled);
                headerItem.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        headerItem.onClicked();
                    }
                });
            }
        }
        if (footerItems.size() > 0) {
            for (final FooterItem footerItem : footerItems) {
                footerItem.setTouchable(Touchable.enabled);
                footerItem.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        footerItem.onClicked();
                    }
                });
            }
        }

        HeaderItem selectedHeader = null;
        for (HeaderItem headerItem : headerItems) {
            if (headerItem.type.matches(header)) {
                selectedHeader = headerItem;
                break;
            }
        }

        FooterItem selectedFooter = null;
        for (FooterItem footerItem : footerItems) {
            if (footerItem.type.matches(footer)) {
                selectedFooter = footerItem;
                break;
            }
        }

        if (selectedHeader != null && selectedFooter != null) {
            for (FooterItem footerItem : footerItems) {
                if (!footerItem.type.matches(footer)) {
                    footerItem.onUnselected();
                }
            }
            this.currentFooter = selectedFooter;
            this.currentFooter.onSelected();
            setCurrentHeader(selectedHeader);
        }
    }

    public void setCurrentPage(String footer, String header) {
        FooterItem selectedFooter = null;
        for (FooterItem footerItem : footerItems) {
            if (footerItem.type.matches(footer)) {
                selectedFooter = footerItem;
            }
        }

        HeaderItem selectedHeader = null;
        for (HeaderItem headerItem : headerItems) {
            if (headerItem.type.matches(header)) {
                selectedHeader = headerItem;
            }
        }

        if (selectedFooter != null) {
            setCurrentFooter(selectedFooter);
        }

        if (selectedHeader != null) {
            setCurrentHeader(selectedHeader);
        }
    }


    public abstract static class HeaderItem extends Table {
        public String type;
        private PageView pageView;

        public HeaderItem(PageView pageView, String type) {
            this.pageView = pageView;
            this.type = type;
        }

        public void onClicked() {
            pageView.setCurrentHeader(this);
        }

        public abstract void onSelected();

        public abstract void onUnselected();
    }

    public abstract static class FooterItem extends Table {
        public static final String LIVE = "Live", CONTEST = "MyContest", RESULT = "Result", NONE = "none";
        public String type;
        private PageView pageView;

        public FooterItem(PageView pageView, String type) {
            this.pageView = pageView;
            this.type = type;
        }

        public void onClicked() {
            pageView.setCurrentFooter(this);
        }

        public abstract void onSelected();

        public abstract void onUnselected();
    }

    public abstract static class PageContent extends Table implements Disposable {
        public Label.LabelStyle errorStyle;
        public PageView pageView;
        public float width, height, density;
        public Table contentTable;
        private Table processTable;
        private Label errorLabel;
        public PullScrollPane scrollPane;
        private long lastRefresh;
        private boolean canScroll,canRefresh;

        public PageContent(PageView pageView) {
            this(pageView,true,true);
        }

        public PageContent(PageView pageView,boolean canScroll,boolean canRefresh){
            this.pageView = pageView;
            this.canScroll=canScroll;
            this.canRefresh=canRefresh;
            this.width = pageView.screen.width;
            this.height = pageView.screen.height;
            this.density = pageView.screen.density;
            errorStyle = new Label.LabelStyle();
            errorStyle.font = FontPool.obtain(FontType.ROBOTO_REGULAR, 5);
            errorStyle.fontColor = Color.LIGHT_GRAY;
            buildProcessTable();

            errorLabel = new Label("", errorStyle);
            errorLabel.setWrap(true);
            errorLabel.setAlignment(Align.center);
            contentTable = new Table();
            contentTable.padTop(8 * density);
            contentTable.top();
            lastRefresh = TimeUtils.millis();
            if (canScroll){
                scrollPane = new PullScrollPane(contentTable, pageView.scrollPaneStyle,width*0.1f) {
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
            float processSize = width * 0.16f;
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
                pageView.clear();
                pageView.add(this).expand().fill();
                contentTable.clear();
            }else {
                pageView.clear();
                pageView.add(this).expand().fill();
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
                add(scrollPane).expand().fill().padBottom(height * 0.008f);
            }else {
                add(contentTable).expand().fill().padBottom(height * 0.008f);
            }

        }

        public void onRefreshError(String message) {
            errorLabel.setText(message);
            clear();
            add(errorLabel).width(width * 0.8f);
        }

        public void refreshAgain() {
            setOnRefresh();
        }


        public void onUnselected() {

        }
    }
}
