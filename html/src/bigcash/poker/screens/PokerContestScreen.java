package bigcash.poker.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

import bigcash.poker.constants.Constant;
import bigcash.poker.dialogs.CashAddDialog;
import bigcash.poker.dialogs.LeaderBoardDialog;
import bigcash.poker.dialogs.PrivateContestDialog;
import bigcash.poker.dialogs.ProcessDialog;
import bigcash.poker.dialogs.ProfileDialog;
import bigcash.poker.font.Font;
import bigcash.poker.font.FontPool;
import bigcash.poker.font.FontType;
import bigcash.poker.game.PokerRulesDialog;
import bigcash.poker.game.holdem.widgets.HoldemContestPage;
import bigcash.poker.game.holdem.widgets.PokerContestStyle;
import bigcash.poker.game.omaha.widgets.OmahaContestPage;
import bigcash.poker.gwt.PokerGame;
import bigcash.poker.models.QrInfo;
import bigcash.poker.models.UIScreen;
import bigcash.poker.network.ApiHandler;
import bigcash.poker.network.GdxListener;
import bigcash.poker.network.PokerApi;
import bigcash.poker.qr.QrScannerDialog;
import bigcash.poker.utils.AssetsLoader;
import bigcash.poker.utils.DrawableBuilder;
import bigcash.poker.utils.PokerUtils;
import bigcash.poker.utils.TextureDrawable;
import bigcash.poker.widgets.PageView;
import bigcash.poker.widgets.WalletTable;

public class PokerContestScreen extends UIScreen {
    public static final String HOLDEM = "NL_HOLDEM", OMAHA = "PL_OMAHA";
    public PageView pageView;
    public TextureAtlas uiAtlas;
    private Texture backgroundTexture;

    private Label walletLabel;
    private Table winningTable;
    private Label winningLabel;
    public PokerContestStyle contestStyle;
    public ProcessDialog processDialog;
    public String gameType;
    private HoldemContestPage holdemContestPage;

    public PokerContestScreen(PokerGame pokerGame) {
        this(pokerGame, HOLDEM);
    }

    public PokerContestScreen(PokerGame pokerGame, String gameType) {
        super(pokerGame, "PokerContestScreen");
        this.gameType = gameType;
    }


    @Override
    public void init() {
        backgroundTexture = AssetsLoader.instance().screenBackground;
        uiAtlas = AssetsLoader.instance().uiAtlas;
    }


    @Override
    public void build() {
        Gdx.app.error("PC","1");
        processDialog = new ProcessDialog(this, "Please Wait..");
        Table mainTable = new Table();
        mainTable.top();

        TextureRegionDrawable background = DrawableBuilder.getDrawable(backgroundTexture, width, height);
        mainTable.setBackground(background);

        Table headerTable = new Table();
        NinePatchDrawable headerBackground = new NinePatchDrawable(new NinePatch(uiAtlas.findRegion("bg_header_bar"), 1, 1, 1, 6));
        headerTable.setBackground(headerBackground);

        Gdx.app.error("PC","1.1");

        Label.LabelStyle headerLabelStyle = new Label.LabelStyle();
        headerLabelStyle.font = FontPool.obtain(FontType.ROBOTO_BOLD, 7);
        headerLabelStyle.fontColor = Color.WHITE;
        float walletTableHeight = headerLabelStyle.font.getLineHeight() * 1.6f;

        Label.LabelStyle titleStyle = new Label.LabelStyle();
        titleStyle.font = FontPool.obtain(FontType.ROBOTO_BOLD, 9);
        titleStyle.fontColor = Color.WHITE;

        TextureRegion menuTexture = uiAtlas.findRegion("icon_menu");
        float menuHeight = walletTableHeight;
        float menuWIdth = menuHeight * menuTexture.getRegionWidth() / menuTexture.getRegionHeight();
        TextureRegionDrawable menuDrawable = TextureDrawable.getDrawable(menuTexture, menuWIdth, menuHeight);
        final Button backButton = new Button(menuDrawable, menuDrawable, menuDrawable);
        headerTable.add(backButton).width(menuWIdth).height(menuHeight).padLeft(8 * density).padRight(16 * density);
        Gdx.app.error("PC","1.2");
        headerTable.add(new Label("Poker", titleStyle)).expandX().align(Align.left);


        TextureRegion walletBackTexture = uiAtlas.findRegion("bg_wallet");
        float walletTableWidth = walletTableHeight * walletBackTexture.getRegionWidth() / walletBackTexture.getRegionHeight();
        float walletTablePad = walletTableWidth * 0.1f;
        float walletPad = walletTablePad;

        TextureRegion qrTexture = uiAtlas.findRegion("ic_qr");
        float qrHeight = headerLabelStyle.font.getLineHeight();
        float qrWidth = qrHeight * qrTexture.getRegionWidth() / qrTexture.getRegionHeight();

        TextureRegion addTexture = uiAtlas.findRegion("icon_add");
        float addHeight = headerLabelStyle.font.getLineHeight() * 1.2f;
        float addWidth = addHeight * addTexture.getRegionWidth() / addTexture.getRegionHeight();

        WalletTable qrTable = new WalletTable(walletBackTexture);
        qrTable.pad(0, 2 * walletTablePad, 0, 2 * walletTablePad);
        qrTable.add(new Image(qrTexture)).width(qrWidth).height(qrHeight).expandX().align(Align.left);
        headerTable.add(qrTable).padRight(8 * density).height(walletTableHeight);

        TextureRegion balanceWalletTexture = uiAtlas.findRegion("icon_walletn");
        float balanceWalletHeight = headerLabelStyle.font.getLineHeight();
        float balanceWalletWidth = balanceWalletHeight * balanceWalletTexture.getRegionWidth() / balanceWalletTexture.getRegionHeight();

        WalletTable paytmWalletTable = new WalletTable(walletBackTexture);


        Gdx.app.error("PC","1.3");
        paytmWalletTable.pad(0, walletTablePad * 2, 0, walletTablePad);
        paytmWalletTable.add(new Image(balanceWalletTexture)).width(balanceWalletWidth).height(balanceWalletHeight).expandX().align(Align.left);
        if (Constant.userProfile==null){
            Gdx.app.error("PC","User Profile Not Found");
        }
        walletLabel = new Label(Constant.userProfile.getPaytmBalance() + "", headerLabelStyle);
        Gdx.app.error("PC","1.4");
        paytmWalletTable.add(walletLabel).padLeft(walletPad).padRight(walletPad).expandX().align(Align.right);
        paytmWalletTable.add(new Image(addTexture)).width(addWidth).height(addHeight);
        headerTable.add(paytmWalletTable).height(walletTableHeight).padRight(8 * density);
        float headerHeight = walletTableHeight * 2;
        mainTable.add(headerTable).width(width).height(headerHeight).row();


        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                new ProfileDialog(PokerContestScreen.this).show(stage);
            }
        });


        paytmWalletTable.setTouchable(Touchable.enabled);
        paytmWalletTable.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                new CashAddDialog(PokerContestScreen.this, "PokerContestScreen", 0).show(getStage());
            }
        });

        buildPage(mainTable);


        qrTable.setTouchable(Touchable.enabled);
        qrTable.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                new QrScannerDialog(PokerContestScreen.this, new GdxListener<String>() {
                    @Override
                    public void onSuccess(String qrId) {
                        ProcessDialog processDialog=new ProcessDialog(PokerContestScreen.this);
                        processDialog.show(stage);
                        PokerApi.getQRInfo(qrId, new GdxListener<QrInfo>() {
                            @Override
                            public void onSuccess(QrInfo qrInfo) {
                                processDialog.hide();
                                pokerGame.qrInfo=qrInfo;
                                pokerGame.qrId=qrId;
                                if (holdemContestPage.buttonLabel!=null){
                                    holdemContestPage.buttonLabel.setText("Play");
                                }

                                if (holdemContestPage.tableLogoImage!=null){
                                    pokerGame.downloadImage(qrInfo.getLogoImageUrl(),holdemContestPage.tableLogoImage);
                                }
                                new PrivateContestDialog(PokerContestScreen.this,qrInfo,contestStyle).show(stage);
                            }

                            @Override
                            public void onFail(String reason) {
                                processDialog.hide();
                            }

                            @Override
                            public void onError(String error) {
                                processDialog.hide();
                            }
                        });
                    }

                    @Override
                    public void onFail(String reason) {

                    }

                    @Override
                    public void onError(String error) {

                    }
                }).show();
            }
        });

        addLayer(mainTable);

        Gdx.app.error("PC","2");
    }


    public void updateBalance() {
        walletLabel.setText(Constant.userProfile.getPaytmBalance() + "");
    }


    private void buildPage(Table mainTable) {
        pageView = new PageView(this);
        contestStyle = new PokerContestStyle(AssetsLoader.instance().uiAtlas, this);
        TextureRegion pageHeaderTexture = uiAtlas.findRegion("bg_page_header");
        NinePatchDrawable pageHeaderBackground = new NinePatchDrawable(new NinePatch(pageHeaderTexture, 23, 16, 16, 8));
        Table pageHeaderTable = new Table();
        pageHeaderTable.setBackground(pageHeaderBackground);

        TextureRegion bgTimerTexture = uiAtlas.findRegion("bg_wtimer");
        Label.LabelStyle timerStyle = new Label.LabelStyle();
        timerStyle.font = FontPool.obtain(FontType.ROBOTO_REGULAR, 12);
        timerStyle.fontColor = Color.SCARLET;
        float timerWidth = timerStyle.font.getCapHeight();
        float timerHeight = timerStyle.font.getLineHeight();
        TextureRegionDrawable timerBackground = DrawableBuilder.getDrawable(bgTimerTexture, timerWidth, timerHeight);
        timerStyle.background = timerBackground;
        timerBackground.setMinWidth(timerWidth);
        timerBackground.setMinHeight(timerHeight);

        TextureRegion leaderboardIcon = uiAtlas.findRegion("icon_leaderboard");
        float leaderboardHeight = timerHeight * 1.2f;
        float leaderbooardWidth = leaderboardHeight * leaderboardIcon.getRegionHeight() / leaderboardIcon.getRegionWidth();
        TextureRegionDrawable leaderboardDrawable = DrawableBuilder.getDrawable(leaderboardIcon, leaderbooardWidth, leaderboardHeight);
        final ImageButton leaderboardButton = new ImageButton(leaderboardDrawable, leaderboardDrawable, leaderboardDrawable);
        pageHeaderTable.add(leaderboardButton).width(leaderbooardWidth).height(leaderboardHeight);
        leaderboardButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                new LeaderBoardDialog(PokerContestScreen.this).show(stage);
            }
        });

        Label.LabelStyle timerLabelStyle = new Label.LabelStyle();
        timerLabelStyle.font = FontPool.obtain(FontType.ROBOTO_REGULAR, 4);
        timerLabelStyle.fontColor = Color.WHITE;

        Stack centerStack = new Stack();
        winningTable = new Table();
        NinePatchDrawable winningBackground = new NinePatchDrawable(new NinePatch(uiAtlas.findRegion("bg_items"), 8, 8, 8, 8));
        winningTable.setBackground(winningBackground);

        Table timerBackTable = new Table();
        centerStack.add(timerBackTable);
        Label.LabelStyle winningStyle = new Label.LabelStyle();
        winningStyle.font = FontPool.obtain(FontType.ROBOTO_BOLD, 10);
        winningStyle.fontColor = Color.CYAN;
        winningLabel = new Label("\u20b9 0", winningStyle);
        Label.LabelStyle winningTextStyle = new Label.LabelStyle();
        winningTextStyle.font = FontPool.obtain(FontType.ROBOTO_REGULAR, 5);
        winningTextStyle.fontColor = Color.WHITE;
        winningTable.add(new Label("Players have won today", winningTextStyle)).row();
        winningTable.add(winningLabel);
        winningTable.setTouchable(Touchable.enabled);
        winningTable.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                new LeaderBoardDialog(PokerContestScreen.this).show(stage);
            }
        });

        setWinnings(Constant.userProfile.getCasualWinnings());
        centerStack.add(winningTable);

        pageHeaderTable.add(centerStack).align(Align.center).expandX();

        TextureRegion infoTexture = uiAtlas.findRegion("btn_info");
        float buttonHeight = timerHeight * 1.2f;
        float buttonWidth = buttonHeight * infoTexture.getRegionWidth() / infoTexture.getRegionHeight();
        TextureRegionDrawable infoDrawable = DrawableBuilder.getDrawable(infoTexture, buttonWidth, buttonHeight);
        final ImageButton infoButton = new ImageButton(infoDrawable, infoDrawable, infoDrawable);
        infoButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                new PokerRulesDialog(PokerContestScreen.this).show(stage);
            }
        });


        pageHeaderTable.add(infoButton).width(buttonWidth).height(buttonHeight).uniformX();
        addBanner(mainTable);

        mainTable.add(pageHeaderTable).width(width).row();

        Font gameTypeFont = FontPool.obtain(FontType.ROBOTO_REGULAR, 6);
        TextureRegion leftUnselectedTexture = uiAtlas.findRegion("btn_unselected");
        TextureRegion rightUnselectedTexture = new TextureRegion(leftUnselectedTexture);
        rightUnselectedTexture.flip(true, false);
        TextureRegion rightSelectedTexture = uiAtlas.findRegion("btn_selected");
        TextureRegion leftSelectedTexture = new TextureRegion(rightSelectedTexture);
        leftSelectedTexture.flip(true, false);


        float gameButtonHeight = gameTypeFont.getLineHeight() * 2;
        float gameButtonWidth = gameButtonHeight * leftSelectedTexture.getRegionWidth() / leftSelectedTexture.getRegionHeight();
        TextureRegionDrawable leftUnselectedDrawable = TextureDrawable.getDrawable(leftUnselectedTexture, gameButtonWidth, gameButtonHeight);
        TextureRegionDrawable leftSelectedDrawable = TextureDrawable.getDrawable(leftSelectedTexture, gameButtonWidth, gameButtonHeight);
        TextureRegionDrawable rightUnselectedDrawable = TextureDrawable.getDrawable(rightUnselectedTexture, gameButtonWidth, gameButtonHeight);
        TextureRegionDrawable rightSelectedDrawable = TextureDrawable.getDrawable(rightSelectedTexture, gameButtonWidth, gameButtonHeight);

        TextButton.TextButtonStyle rightGameButtonStyle = new TextButton.TextButtonStyle();
        rightGameButtonStyle.font = gameTypeFont;
        rightGameButtonStyle.fontColor = Color.WHITE;
        rightGameButtonStyle.downFontColor = Color.LIGHT_GRAY;
        rightGameButtonStyle.up = rightUnselectedDrawable;
        rightGameButtonStyle.down = rightUnselectedDrawable;
        rightGameButtonStyle.disabled = rightUnselectedDrawable;
        rightGameButtonStyle.checked = rightSelectedDrawable;


        TextButton.TextButtonStyle leftGameButtonStyle = new TextButton.TextButtonStyle();
        leftGameButtonStyle.font = gameTypeFont;
        leftGameButtonStyle.fontColor = Color.WHITE;
        leftGameButtonStyle.downFontColor = Color.LIGHT_GRAY;
        leftGameButtonStyle.up = leftUnselectedDrawable;
        leftGameButtonStyle.down = leftUnselectedDrawable;
        leftGameButtonStyle.disabled = leftUnselectedDrawable;
        leftGameButtonStyle.checked = leftSelectedDrawable;


        Table gameTypeTable = new Table();

        Label.LabelStyle headerLabelStyle = new Label.LabelStyle();
        headerLabelStyle.font = gameTypeFont;
        headerLabelStyle.fontColor = Color.WHITE;

        PokerHeaderItem holdemPokerButton = new PokerHeaderItem(pageView, HOLDEM, new Label("NL Holdem ", headerLabelStyle), leftUnselectedDrawable, leftSelectedDrawable);
        PokerHeaderItem omahaPokerButton = new PokerHeaderItem(pageView, OMAHA, new Label("PL Omaha", headerLabelStyle), rightUnselectedDrawable, rightSelectedDrawable);

        gameTypeTable.add(holdemPokerButton).width(gameButtonWidth).height(gameButtonHeight);
        gameTypeTable.add(omahaPokerButton).width(gameButtonWidth).height(gameButtonHeight);

        mainTable.add(gameTypeTable).padTop(4 * density).row();


        PageView.FooterItem live = new PageView.FooterItem(pageView, "LIVE") {
            @Override
            public void onSelected() {

            }

            @Override
            public void onUnselected() {

            }
        };


        holdemContestPage = new HoldemContestPage(this,pageView);
        pageView.addPageContent(live, holdemPokerButton, holdemContestPage);

        OmahaContestPage omahaContestPage = new OmahaContestPage(this,pageView);
        pageView.addPageContent(live, omahaPokerButton, omahaContestPage);

        mainTable.add(pageView).expand().fill().padBottom(height * 0.008f);

        pageView.build("LIVE", gameType);
    }

    private void addBanner(Table mainTable) {
        float backgroundHeight = width * contestStyle.pokerBg.getRegionHeight() / contestStyle.pokerBg.getRegionWidth();
        mainTable.add(new Image(contestStyle.pokerBg)).width(width).height(backgroundHeight).row();
    }

    @Override
    public void onBackKeyPressed() {

    }


    @Override
    public void dispose() {
        super.dispose();
    }

    public void setWinnings(float winnings) {
        winningTable.setVisible(true);
        try {
            winningLabel.setText("\u20b9 "+ PokerUtils.formatValue(winnings));
        } catch (Exception e) {
           ApiHandler.callEventLogApi("PokerContestScreen", "WINNIGS_ISSUE", Constant.userProfile.getCasualWinnings()+"");
        }

    }

    @Override
    public void show() {
        build();
        super.show();
        if (!pokerGame.qrId.isEmpty()){
            ProcessDialog processDialog=new ProcessDialog(PokerContestScreen.this);
            processDialog.show(stage);
            PokerApi.getQRInfo(pokerGame.qrId, new GdxListener<QrInfo>() {
                @Override
                public void onSuccess(QrInfo qrInfo) {
                    processDialog.hide();
                    pokerGame.qrInfo=qrInfo;
                    new PrivateContestDialog(PokerContestScreen.this,qrInfo,contestStyle).show(stage);
                }

                @Override
                public void onFail(String reason) {
                    processDialog.hide();
                }

                @Override
                public void onError(String error) {
                    processDialog.hide();
                }
            });
        }
        pokerGame.qrId="";
    }


    @Override
    public void resume() {
        super.resume();
    }

    private class PokerHeaderItem extends PageView.HeaderItem {
        private TextureRegionDrawable selectedBackground, unSelectedBackground;

        public PokerHeaderItem(PageView pageView, String type, Label label, TextureRegionDrawable unselectedBackground, TextureRegionDrawable selectedBackground) {
            super(pageView, type);
            add(label);
            this.selectedBackground = selectedBackground;
            this.unSelectedBackground = unselectedBackground;
            this.setBackground(unselectedBackground);
        }

        @Override
        public void onSelected() {
            setBackground(selectedBackground);
        }

        @Override
        public void onUnselected() {
            setBackground(unSelectedBackground);
        }
    }
}
