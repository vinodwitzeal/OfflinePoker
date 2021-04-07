package bigcash.poker.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
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
import bigcash.poker.font.FontPool;
import bigcash.poker.font.FontType;
import bigcash.poker.game.PokerRulesDialog;
import bigcash.poker.game.PokerStyle;
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
import bigcash.poker.utils.PokerUtils;
import bigcash.poker.utils.TextureDrawable;
import bigcash.poker.widgets.AutoScrollPane;
import bigcash.poker.widgets.MagicTable;
import bigcash.poker.widgets.PageView;
import bigcash.poker.widgets.WalletTable;

public class PokerContestScreen extends UIScreen {
    public static final String HOLDEM = "NL_HOLDEM", OMAHA = "PL_OMAHA";
    public PageView pageView;
    public Table mainTable,bannersTable;
    public String gameType;
    public TextureAtlas uiAtlas;
    private Label.LabelStyle selectedGameStyle, unselectedGameStyle;
    private TextureRegionDrawable newDrawable, comingSoonDrawable;
    public PokerContestStyle contestStyle;
    public PokerStyle pokerStyle;
    public ProcessDialog processDialog;
    private HoldemContestPage holdemContestPage;


    private NinePatchDrawable lineDrawable;
    private Label walletLabel,winningLabel;

    public PokerContestScreen(PokerGame pokerGame) {
        this(pokerGame, HOLDEM);
    }

    public PokerContestScreen(PokerGame pokerGame, String gameType) {
        super(pokerGame, "PokerContestScreen");
        this.gameType = gameType;
    }


    @Override
    public void init() {
        uiAtlas = AssetsLoader.instance().uiAtlas;
    }


    @Override
    public void build() {
        processDialog = new ProcessDialog(this, "Please Wait..");
        //Start

        NinePatchDrawable backgroundDrawable = new NinePatchDrawable(new NinePatch(uiAtlas.findRegion("img_dot"), 1, 1, 1, 1));
        mainTable = new MagicTable(backgroundDrawable, "233352");
        mainTable.top();


        Table headerTable = new Table();
        NinePatchDrawable headerBackground = new NinePatchDrawable(new NinePatch(uiAtlas.findRegion("bg_header_bar"), 1, 1, 1, 6));
        headerTable.setBackground(headerBackground);

        Label.LabelStyle headerLabelStyle = new Label.LabelStyle();
        headerLabelStyle.font = FontPool.obtain(FontType.ROBOTO_BOLD, 7);
        headerLabelStyle.fontColor = Color.WHITE;
        float walletTableHeight = headerLabelStyle.font.getLineHeight() * 1.6f;
        float headerHeight = walletTableHeight * 2;

        Label.LabelStyle titleStyle = new Label.LabelStyle();
        titleStyle.font = FontPool.obtain(FontType.ROBOTO_BOLD, 9);
        titleStyle.fontColor = Color.WHITE;

        TextureRegion menuTexture = uiAtlas.findRegion("icon_menu");
        float menuHeight = walletTableHeight;
        float menuWIdth = menuHeight * menuTexture.getRegionWidth() / menuTexture.getRegionHeight();
        TextureRegionDrawable menuDrawable = TextureDrawable.getDrawable(menuTexture, menuWIdth, menuHeight);
        final Button menuButton = new Button(menuDrawable, menuDrawable, menuDrawable);
        headerTable.add(menuButton).width(menuWIdth).height(menuHeight).padLeft(8 * density).padRight(16 * density);

        headerTable.add(new Label("Poker", titleStyle)).expandX().align(Align.left);

        TextureRegion walletBackTexture = uiAtlas.findRegion("bg_wallet");
        float walletTableWidth = walletTableHeight * walletBackTexture.getRegionWidth() / walletBackTexture.getRegionHeight();
        float walletTablePad = walletTableWidth * 0.1f;
        float walletPad = walletTablePad;

        TextureRegion qrTexture = uiAtlas.findRegion("ic_qr");
        float qrHeight = headerLabelStyle.font.getLineHeight();
        float qrWidth = qrHeight * qrTexture.getRegionWidth() / qrTexture.getRegionHeight();

        Table qrTable = new WalletTable(walletBackTexture);
        qrTable.pad(0, 2 * walletTablePad, 0, 2 * walletTablePad);
        qrTable.add(new Image(qrTexture)).width(qrWidth).height(qrHeight).expandX();
        headerTable.add(qrTable).padRight(8 * density).height(walletTableHeight);

        TextureRegion leaderboardTexture = uiAtlas.findRegion("ic_leaderboard");
        float leaderboardHeight = walletTableHeight;
        float leaderboardWidth = leaderboardHeight * leaderboardTexture.getRegionWidth() / leaderboardTexture.getRegionHeight();
        TextureRegionDrawable leaderboardDrawable = TextureDrawable.getDrawable(leaderboardTexture, leaderboardWidth, leaderboardHeight);
        Button leaderboardButton = new Button(leaderboardDrawable, leaderboardDrawable, leaderboardDrawable);
        headerTable.add(leaderboardButton).width(leaderboardWidth).height(leaderboardHeight).padRight(walletPad);

        TextureRegion infoTexture = uiAtlas.findRegion("ic_info");
        float infoHeight = walletTableHeight;
        float infoWidth = infoHeight * infoTexture.getRegionWidth() / infoTexture.getRegionHeight();
        TextureRegionDrawable infoDrawable = TextureDrawable.getDrawable(infoTexture, infoWidth, infoHeight);
        Button infoButton = new Button(infoDrawable, infoDrawable, infoDrawable);
        headerTable.add(infoButton).width(infoWidth).height(infoHeight).padRight(walletPad);

        TextureRegion addTexture = uiAtlas.findRegion("icon_add");
        float addHeight = headerLabelStyle.font.getLineHeight() * 1.2f;
        float addWidth = addHeight * addTexture.getRegionWidth() / addTexture.getRegionHeight();


        TextureRegion balanceWalletTexture = uiAtlas.findRegion("icon_walletn");
        float balanceWalletHeight = headerLabelStyle.font.getLineHeight();
        float balanceWalletWidth = balanceWalletHeight * balanceWalletTexture.getRegionWidth() / balanceWalletTexture.getRegionHeight();

        WalletTable paytmWalletTable = new WalletTable(walletBackTexture);
        paytmWalletTable.pad(0, walletTablePad * 2, 0, walletTablePad);
        paytmWalletTable.add(new Image(balanceWalletTexture)).width(balanceWalletWidth).height(balanceWalletHeight).expandX().align(Align.left);
        walletLabel = new Label(Constant.userProfile.getPaytmBalance() + "", headerLabelStyle);
        paytmWalletTable.add(walletLabel).padLeft(walletPad).padRight(walletPad).expandX().align(Align.right);
        paytmWalletTable.add(new Image(addTexture)).width(addWidth).height(addHeight);

        headerTable.add(paytmWalletTable).height(walletTableHeight).padRight(8 * density);

        leaderboardButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showLeaderboard();
            }
        });

        infoButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showInfo();
            }
        });

        paytmWalletTable.setTouchable(Touchable.enabled);
        paytmWalletTable.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                new CashAddDialog(PokerContestScreen.this, "PokerContestScreen", 0).show(getStage());
            }
        });

        mainTable.add(headerTable).width(width).height(headerHeight).row();

        //Banners
        bannersTable = new Table();
        updateBanners();
        float bannerHeight = height * 0.1f;

        ScrollPane scrollPane = new AutoScrollPane(bannersTable);
        scrollPane.setScrollingDisabled(false, true);

        mainTable.add(scrollPane).width(width).height(bannerHeight).padTop(8 * density).padBottom(8 * density).row();

       unselectedGameStyle = new Label.LabelStyle();
        unselectedGameStyle.font = FontPool.obtain(FontType.ROBOTO_REGULAR, 6);
        unselectedGameStyle.fontColor = Color.GRAY;

        selectedGameStyle = new Label.LabelStyle();
        selectedGameStyle.font = FontPool.obtain(FontType.ROBOTO_BOLD, 6);
        selectedGameStyle.fontColor = Color.WHITE;

        lineDrawable = new NinePatchDrawable(new NinePatch(uiAtlas.findRegion("img_dot"), 1, 1, 1, 1));
        TextureRegion newTexture = uiAtlas.findRegion("bg_new");
        float newHeight = unselectedGameStyle.font.getCapHeight();
        float newWidth = newHeight * newTexture.getRegionWidth() / newTexture.getRegionHeight();
        newDrawable = TextureDrawable.getDrawable(newTexture, newWidth, newHeight);
        comingSoonDrawable = newDrawable;

        final Color pageViewColor = Color.valueOf("234f74");
        pageView = new PageView(this) {
            @Override
            protected void drawBackground(Batch batch, float parentAlpha, float x, float y) {
                Color color = batch.getColor();
                batch.setColor(pageViewColor.r, pageViewColor.g, pageViewColor.b, pageViewColor.a * parentAlpha);
                lineDrawable.draw(batch, x, y, getWidth(), getHeight());
                batch.setColor(color);
            }
        };

        PokerHeaderItem holdem = new PokerHeaderItem(pageView, HOLDEM, "NL Holdem");
        PokerHeaderItem omaha = new PokerHeaderItem(pageView, OMAHA, "PL Omaha");

        Table gameHeaderTable = new Table();
        gameHeaderTable.add(holdem).uniformX().expandX().fillX();
        gameHeaderTable.add(omaha).uniformX().expandX().fillX();
        mainTable.add(gameHeaderTable).width(width).padTop(8 * density).row();

        PageView.FooterItem live = new PageView.FooterItem(pageView, "LIVE") {
            @Override
            public void onSelected() {

            }

            @Override
            public void onUnselected() {

            }
        };


        contestStyle = new PokerContestStyle( uiAtlas,this);
        pokerStyle=new PokerStyle(this,uiAtlas);
        holdemContestPage = new HoldemContestPage(this, pageView);
        OmahaContestPage omahaContestPage = new OmahaContestPage(this, pageView);
        pageView.addPageContent(live, holdem, holdemContestPage);
        pageView.addPageContent(live, omaha, omahaContestPage);
        pageView.build("LIVE", gameType);
        mainTable.add(pageView).width(width).expandY().fillY();


        addLayer(mainTable);

        menuButton.addListener(new ClickListener() {
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
                                new PrivateContestDialog(PokerContestScreen.this,qrInfo, pokerStyle).show(stage);
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
    }

    public void updateBanners() {
        bannersTable.clear();
        final float bannerHeight = height * 0.1f;
        final float bannerWidth = bannerHeight * 3.5f;
        final float bannerPad = width * 0.01f;

        TextureRegion rummyRake = uiAtlas.findRegion("poker_rake");
        Image bannerRake = new Image(rummyRake);
        bannersTable.add(bannerRake).width(bannerWidth).height(bannerHeight).padLeft(bannerPad);

        TextureRegion rummyWinning = uiAtlas.findRegion("poker_winning");
        TextureRegionDrawable bannerWinningDrawable = TextureDrawable.getDrawable(rummyWinning, bannerWidth, bannerHeight);
        Table rummyWinningTable = new Table();
        float bannerLeftPad = bannerWidth * 0.04f;
        float bannerTopPad = bannerHeight * 0.4f;
        rummyWinningTable.padTop(bannerTopPad).padLeft(bannerLeftPad);
        rummyWinningTable.top().left();
        rummyWinningTable.setBackground(bannerWinningDrawable);
        Label.LabelStyle winningStyle = new Label.LabelStyle();
        winningStyle.font = FontPool.obtain(FontType.ROBOTO_BOLD, 14);
        winningStyle.fontColor = Color.valueOf("ffffff");
        winningLabel = new Label("\u20b9 25", winningStyle);
        setWinnings(Constant.userProfile.getCasualWinnings());
        rummyWinningTable.add(winningLabel);
        rummyWinningTable.setTouchable(Touchable.enabled);
        rummyWinningTable.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showLeaderboard();
            }
        });
        bannersTable.add(rummyWinningTable).width(bannerWidth).height(bannerHeight).padLeft(bannerPad);
    }

    public void setWinnings(float amount) {

        try {
            String numberAsString = PokerUtils.formatValue(amount);
            winningLabel.setText("\u20b9 " + numberAsString);
        } catch (Exception e) {
            ApiHandler.callEventLogApi("PokerScreen", "WINNIGS_ISSUE", amount+ "");
        }

    }

    public void showLeaderboard(){
        new LeaderBoardDialog(this).show(stage);
    }

    public void showInfo(){
        new PokerRulesDialog(this).show(stage);
    }


    public void updateBalance() {
        walletLabel.setText(Constant.userProfile.getPaytmBalance() + "");
    }

    @Override
    public void onBackKeyPressed() {

    }


    @Override
    public void dispose() {
        super.dispose();
    }


    @Override
    public void show() {
        build();
        super.show();
        if (!pokerGame.qrId.isEmpty() && !pokerGame.qrContestShown){
            ProcessDialog processDialog=new ProcessDialog(PokerContestScreen.this);
            processDialog.show(stage);
            PokerApi.getQRInfo(pokerGame.qrId, new GdxListener<QrInfo>() {
                @Override
                public void onSuccess(QrInfo qrInfo) {
                    processDialog.hide();
                    pokerGame.qrInfo=qrInfo;
                    new PrivateContestDialog(PokerContestScreen.this,qrInfo, pokerStyle).show(stage);
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
            pokerGame.qrContestShown=true;
        }
    }


    @Override
    public void resume() {
        super.resume();
    }

    private class PokerHeaderItem extends PageView.HeaderItem {
        private Label nameLabel;
        private Table table;
        private Image selectedImage;
        private float imageHeight;

        public PokerHeaderItem(PageView pageView, String type, String name) {
            super(pageView, type);
            nameLabel = new Label(name, unselectedGameStyle);
            add(nameLabel).row();
            table = new Table();
            add(table).expandX().fillX().height(unselectedGameStyle.font.getLineHeight()).row();
            selectedImage = new Image(lineDrawable);
            selectedImage.setColor(Color.RED);
            imageHeight = unselectedGameStyle.font.getCapHeight() / 4f;
            add(selectedImage).expandX().fillX().height(imageHeight);
        }

        public void setNew() {
            table.clear();
            table.add(new Image(newDrawable)).width(newDrawable.getMinWidth()).height(newDrawable.getMinHeight());
        }

        public void setComingSoon() {
            table.clear();
            table.add(new Image(comingSoonDrawable)).width(comingSoonDrawable.getMinWidth()).height(comingSoonDrawable.getMinHeight());
        }

        @Override
        public void onSelected() {
            nameLabel.setStyle(selectedGameStyle);
            selectedImage.setVisible(true);
        }

        @Override
        public void onUnselected() {
            nameLabel.setStyle(unselectedGameStyle);
            selectedImage.setVisible(false);
        }
    }

}
