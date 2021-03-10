package bigcash.poker.dialogs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.concurrent.TimeUnit;

import bigcash.poker.constants.Constant;
import bigcash.poker.font.Font;
import bigcash.poker.font.FontPool;
import bigcash.poker.font.FontType;
import bigcash.poker.models.ReferralLeague;
import bigcash.poker.models.UIDialog;
import bigcash.poker.models.UIScreen;
import bigcash.poker.network.ApiHandler;
import bigcash.poker.network.GdxListener;
import bigcash.poker.utils.AssetsLoader;
import bigcash.poker.utils.DrawableBuilder;
import bigcash.poker.utils.GamePreferences;
import bigcash.poker.utils.PokerTimer;
import bigcash.poker.utils.PokerUtils;
import bigcash.poker.utils.TextureDrawable;
import bigcash.poker.widgets.HorizontalProgressbar;
import bigcash.poker.widgets.MaskedImage;
import bigcash.poker.widgets.StyledLabel;
import bigcash.poker.widgets.WalletTable;


public class InviteDialog extends UIDialog {
    private TextureAtlas uiAtlas;
    private Label walletLabel;
    private ProcessDialog processDialog;
    boolean istwitter = false;
    private Label timerLabel;
    private ReferralLeague referralLeague;
    private PokerTimer referralLeagueTimerCounter;
    private GdxListener<String> listener;

    public InviteDialog(UIScreen screen, ReferralLeague referralLeague) {
        super(screen);
        dismissOnBack(true);
        this.referralLeague = referralLeague;
        this.uiAtlas = AssetsLoader.instance().uiAtlas;
        buildDialog();
    }

    @Override
    public void init() {
    }


    @Override
    public void buildDialog() {
        Table contentTable = getContentTable();
        contentTable.top();
        NinePatchDrawable grayBackground = new DrawableBuilder(20, 20).color(Color.valueOf("d8d5df")).createNinePatch();
        contentTable.setBackground(grayBackground);

        Table headerTable = new Table();
        NinePatchDrawable headerBackground = new NinePatchDrawable(new NinePatch(uiAtlas.findRegion("bg_header_bar"), 1, 1, 1, 6));
        headerTable.setBackground(headerBackground);

        Label.LabelStyle headerLabelStyle = new Label.LabelStyle();
        headerLabelStyle.font = FontPool.obtain(FontType.ROBOTO_BOLD, 7);
        headerLabelStyle.fontColor = Color.WHITE;
        float walletTableHeight = headerLabelStyle.font.getLineHeight() * 1.6f;

        Label.LabelStyle titleStyle = new Label.LabelStyle();
        titleStyle.font = FontPool.obtain(FontType.ROBOTO_BOLD, 9);
        titleStyle.fontColor = Color.WHITE;

        TextureRegion backTexture = uiAtlas.findRegion("btn_wback");
        float backHeight = walletTableHeight;
        float backWidth = backHeight * backTexture.getRegionWidth() / backTexture.getRegionHeight();
        TextureRegionDrawable menuDrawable = TextureDrawable.getDrawable(backTexture, backWidth, backHeight);
        final Button backButton = new Button(menuDrawable, menuDrawable, menuDrawable);
        headerTable.add(backButton).width(backWidth).height(backHeight).padLeft(8 * density).padRight(16 * density);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hide();
            }
        });

        headerTable.add(new Label("Invite", titleStyle)).expandX().align(Align.left);

        TextureRegion walletBackTexture = uiAtlas.findRegion("bg_wallet");
        float walletTableWidth = walletTableHeight * walletBackTexture.getRegionWidth() / walletBackTexture.getRegionHeight();
        float walletTablePad = walletTableWidth * 0.1f;
        float walletPad = walletTablePad;

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
        float headerHeight = walletTableHeight * 2;
        contentTable.add(headerTable).width(width).height(headerHeight).row();

        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hide();
            }
        });

        Label.LabelStyle inviteTextStyle = new Label.LabelStyle();
        inviteTextStyle.font = FontPool.obtain(FontType.ROBOTO_REGULAR, 7);
        inviteTextStyle.fontColor = Color.DARK_GRAY;
        NinePatchDrawable whiteBackground = new NinePatchDrawable(new NinePatch(uiAtlas.findRegion("bg_content_full"), 8, 8, 8, 8));
        NinePatchDrawable inviteImageBackground = new NinePatchDrawable(new NinePatch(uiAtlas.findRegion("bg_banner"), 8, 8, 8, 8));

        TextureRegion logoTexture = uiAtlas.findRegion("character");
        float logoWidth = width * 0.45f;
        float logoHeight = logoWidth * logoTexture.getRegionHeight() / logoTexture.getRegionWidth();
        Table scrollTable = new Table();
        scrollTable.top();

        Table inviteImageTable = new Table();
        inviteImageTable.pad(4 * density, 4 * density, 0, 4 * density);
        inviteImageTable.setBackground(inviteImageBackground);


        Font headingFont = FontPool.obtain(FontType.ROBOTO_BOLD, 11.5f);
        Label.LabelStyle headingStyle = new Label.LabelStyle();
        headingStyle.font = headingFont;
        headingStyle.fontColor = Color.valueOf("e51212");


        inviteImageTable.add(new Image(logoTexture)).width(logoWidth).height(logoHeight).padLeft(1.1f * density).padTop(25 * density);
        inviteImageTable.add(new Label("INVITE & EARN", headingStyle)).padLeft(1.3f);
        //   inviteImageTable.add(infoImage).width(infoWidth).height(infoHeight*.8f).pad(0,0,2*density,2).align(Align.bottomRight);

        Table topTable = new Table();
        topTable.add(inviteImageTable).width(width * .98f).row();
        scrollTable.add(topTable).expandX().fillX().padLeft(8 * density).padRight(8 * density).padTop(4 * density).row();

        Table messageTable = new Table();
        messageTable.setBackground(whiteBackground);

        String Rules = GamePreferences.instance().getDisplayInviteText().replace("\n", "$");
        String[] gameRules = Rules.split("\\$");
        for (int i = 0; i < gameRules.length; i++) {
            Label ruleLabel = new Label(gameRules[i], inviteTextStyle);
            ruleLabel.setAlignment(Align.left);
            ruleLabel.setWrap(true);
            messageTable.add(ruleLabel).width(width * 0.9f).padTop(10 * density).padBottom(density).row();
            messageTable.add().expandY().row();
        }

        scrollTable.add(messageTable).expandX().fillX().padLeft(8 * density).padTop(-2f * density).padRight(8 * density).row();

        TextureRegion wButtonTexture = uiAtlas.findRegion("btn_wtshare");
        float wButtonWidth = width * 0.9f;
        float wButtonHeight = wButtonWidth * wButtonTexture.getRegionHeight() / wButtonTexture.getRegionWidth();
        TextureRegionDrawable wButtonDrawable = TextureDrawable.getDrawable(wButtonTexture, wButtonWidth, wButtonHeight);


        ImageButton whatsAppButton = new ImageButton(wButtonDrawable, wButtonDrawable, wButtonDrawable);

        messageTable.add(whatsAppButton).width(wButtonWidth).height(wButtonHeight).padTop(wButtonHeight * 0.25f).row();

        whatsAppButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                PokerUtils.inviteOnWhatsapp("https://www.bigcash.live/pokercash");
            }
        });

        Table buttonTable = new Table();
        float buttonHeight = width * 0.2f;
        float buttonWidth;
        TextureRegion facebookTexture = uiAtlas.findRegion("btn_fb");
        buttonWidth = buttonHeight * facebookTexture.getRegionWidth() / facebookTexture.getRegionHeight();
        TextureRegionDrawable facebookButtonDrawable = TextureDrawable.getDrawable(facebookTexture, buttonWidth, buttonHeight);
        final ImageButton facebookButton = new ImageButton(facebookButtonDrawable, facebookButtonDrawable);
//        facebookButton.getStyle().pressedOffsetY = -2 * density;
        buttonTable.add(facebookButton).width(buttonWidth).height(buttonHeight).expandX();
        TextureRegion text_msgTexture = uiAtlas.findRegion("btn_twitter");

        buttonWidth = buttonHeight * text_msgTexture.getRegionWidth() / text_msgTexture.getRegionHeight();
        TextureRegionDrawable textmsgButtonDrawable = TextureDrawable.getDrawable(text_msgTexture, buttonWidth, buttonHeight);
        final ImageButton text_msg_Button = new ImageButton(textmsgButtonDrawable, textmsgButtonDrawable);
//        text_msg_Button.getStyle().pressedOffsetY = -2 * density;
        buttonTable.add(text_msg_Button).width(buttonWidth).height(buttonHeight).expandX();


        TextureRegion otherTexture = uiAtlas.findRegion("btn_others");
        buttonWidth = buttonHeight * otherTexture.getRegionWidth() / otherTexture.getRegionHeight();
        TextureRegionDrawable othersButtonDrawable = TextureDrawable.getDrawable(otherTexture, buttonWidth, buttonHeight);

        facebookButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                PokerUtils.shareOnFacebook("https://www.bigcash.live/pokercash");
            }
        });
//
        text_msg_Button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                PokerUtils.shareOnTwitter("https://www.bigcash.live/pokercash");
            }
        });

        messageTable.add(buttonTable).width(width * 0.9f).padTop(wButtonHeight * 0.25f).padBottom(10 * density).row();

        Label.LabelStyle bigLabelStyle = new Label.LabelStyle();
        bigLabelStyle.font = FontPool.obtain(FontType.ROBOTO_BOLD, 6);
        bigLabelStyle.fontColor = Color.valueOf("535353");

        Label.LabelStyle winningLabelStyle = new Label.LabelStyle();
        winningLabelStyle.font = FontPool.obtain(FontType.ROBOTO_BOLD, 6);
        winningLabelStyle.fontColor = Color.valueOf("f3821d");

        Label.LabelStyle smallLabelStyle = new Label.LabelStyle();
        smallLabelStyle.font = FontPool.obtain(FontType.ROBOTO_BOLD, 4);
        smallLabelStyle.fontColor = Color.valueOf("535353");

        Label.LabelStyle bigWhiteLabelStyle = new Label.LabelStyle();
        bigWhiteLabelStyle.font = FontPool.obtain(FontType.ROBOTO_BOLD, 8);
        bigWhiteLabelStyle.fontColor = Color.WHITE;

        if (Constant.userProfile.getReferralLeagueDetail().isReferralMonthlyContestStatus()) {
            addInviteBanner(scrollTable, referralLeague);
        }

        Label.LabelStyle termsStyle = new Label.LabelStyle();
        termsStyle.font = FontPool.obtain(FontType.ROBOTO_REGULAR, 6);
        termsStyle.fontColor = Color.BLUE;
        Label tcLabel = new Label("Terms and Conditions Apply*", termsStyle);
        ScrollPane dataScrollPane = new ScrollPane(scrollTable);
        dataScrollPane.setScrollingDisabled(true, false);
        contentTable.add(dataScrollPane).expand().fill().align(Align.top).row();
        contentTable.add(tcLabel).padTop(15 * density);


        tcLabel.setTouchable(Touchable.enabled);
        tcLabel.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (PokerUtils.isNetworkConnected()){
                    ApiHandler.callPrivacyPolicyApi("INVITE", new GdxListener<String>() {
                        @Override
                        public void onSuccess(String s) {
                            processDialog.hide();
                            new PolicyDialog(screen,s).show(screen.stage);
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
            }
        });

    }

    private void showLeaderBoard() {

    }

    private void updateBalance() {
        walletLabel.setText(Constant.userProfile.getPaytmBalance() + "");
        screen.updateBalance();
    }

    @Override
    public void hide() {
        super.hide();
        if (referralLeagueTimerCounter != null)
            referralLeagueTimerCounter.cancel();
    }


    private void addInviteBanner(Table mainTable, ReferralLeague referralLeague) {
        Table inviteTable = new Table();
        inviteTable.top();
        float tableWidth, tableHeight;
        tableWidth = width * 0.98f;
        if (referralLeague == null) {
            TextureRegion bgTexture = uiAtlas.findRegion("bg_invite_small");
            tableHeight = tableWidth * bgTexture.getRegionHeight() / bgTexture.getRegionWidth();
            inviteTable.setBackground(TextureDrawable.getDrawable(bgTexture, tableWidth, tableHeight));
        } else {
            TextureRegion bgTexture = uiAtlas.findRegion("bg_invite");
            tableHeight = tableWidth * bgTexture.getRegionHeight() / bgTexture.getRegionWidth();
            inviteTable.setBackground(TextureDrawable.getDrawable(bgTexture, tableWidth, tableHeight));
        }
        TextureRegion bgTexture = uiAtlas.findRegion("bg_invite");
        tableHeight = tableWidth * bgTexture.getRegionHeight() / bgTexture.getRegionWidth();

        //Top Table
        Table topTable = new Table();

        //Winnings Label
        StyledLabel.StyledLabelStyle winningStyle = new StyledLabel.StyledLabelStyle(FontType.ROBOTO_BOLD, 9);
        winningStyle.fontColor = Color.WHITE;
        String numberAsString = PokerUtils.getValue(Constant.userProfile.getReferralLeagueDetail().getReferralWinningAmount()) + "";
        StyledLabel winningLabel = new StyledLabel("\u20b9 " + numberAsString, winningStyle);
        winningLabel.shadow(1, 1, Color.BLACK);
//        winningLabel.outline(0.2f,Color.BLACK);
        winningLabel.setAlignment(Align.center);
        topTable.add(winningLabel).width(tableWidth * 0.24f).padLeft(tableWidth * 0.08f).padTop(tableHeight * 0.4f);


        TextureRegion bgTime = uiAtlas.findRegion("bt_timer");
        float timerWidth = tableWidth * 0.25f;
        float timerHeight = timerWidth * bgTime.getRegionHeight() / bgTime.getRegionWidth();
        TextureRegionDrawable timerBackground = TextureDrawable.getDrawable(bgTime, timerWidth, timerHeight);

        Table timerTable = new Table();
        timerTable.setBackground(timerBackground);
        Label.LabelStyle timerStyle = new Label.LabelStyle();
        timerStyle.font = FontPool.obtain(FontType.ROBOTO_BOLD, 6);
        timerStyle.fontColor = Color.valueOf("503a79");
        timerLabel = new Label("29 Days", timerStyle);
        timerLabel.setAlignment(Align.center);
        timerTable.add(timerLabel).width(timerWidth * 0.63f).expand().padLeft(timerWidth * 0.08f).padTop(timerHeight * 0.4f).align(Align.topLeft);


        topTable.add(timerTable).width(timerWidth).height(timerHeight).expandX().align(Align.right).padRight(tableWidth * 0.02f).padTop(tableHeight * 0.05f);
        inviteTable.add(topTable).expandX().fillX().row();


        //Middle Table
        Table middleTable = new Table();
        Table progressTable = new Table();
        TextureRegion progressEmpty = uiAtlas.findRegion("progress_empty");
        TextureRegion progressFill = uiAtlas.findRegion("progress_fill");
        HorizontalProgressbar horizontalProgressbar = new HorizontalProgressbar(progressEmpty, progressFill);
        Label.LabelStyle playerStyle = new Label.LabelStyle();
        playerStyle.font = FontPool.obtain(FontType.ROBOTO_BOLD, 4);
        playerStyle.fontColor = Color.WHITE;
        float progressHeight = playerStyle.font.getLineHeight();
        float progressWidth = tableWidth * 0.55f;
        progressTable.add(horizontalProgressbar).width(progressWidth).height(progressHeight).expandX().align(Align.left).row();
        long maxParticipant = 20000;
        if (Constant.userProfile.getReferralLeagueDetail().getReferralTotalParticipant() >= 18000) {
            maxParticipant = Constant.userProfile.getReferralLeagueDetail().getReferralTotalParticipant() + 5000;
        }
        horizontalProgressbar.setPercent((float) Constant.userProfile.getReferralLeagueDetail().getReferralTotalParticipant() / (float) maxParticipant);

        final Label playersLabel = new Label(Constant.userProfile.getReferralLeagueDetail().getReferralTotalParticipant() + " Players", playerStyle);
        progressTable.add(playersLabel).expandX().align(Align.left);
        middleTable.add(progressTable).width(progressWidth).height(progressHeight).padLeft(tableWidth * 0.03f).align(Align.left);

        TextureRegion buttonTexture = uiAtlas.findRegion("btn_leaderboard");
        float buttonWidth = timerWidth;
        float buttonHeight = buttonWidth * buttonTexture.getRegionHeight() / buttonTexture.getRegionWidth();
        TextureRegionDrawable buttonDrawable = TextureDrawable.getDrawable(buttonTexture, buttonWidth, buttonHeight);
        ImageButton leaderboardButton = new ImageButton(buttonDrawable, buttonDrawable);
        middleTable.add(leaderboardButton).width(buttonWidth).height(buttonHeight).expandX().align(Align.right).padRight(tableWidth * 0.02f).padBottom(progressHeight);

        inviteTable.add(middleTable).expandX().fillX().row();

        if (referralLeague != null && referralLeague.getMyDetail() != null && referralLeague.getMyDetail().getUserRank() > 0) {
            TextureRegionDrawable rankBackground = new TextureRegionDrawable(uiAtlas.findRegion("bg_rank"));
            final Label.LabelStyle bigLabelStyle = new Label.LabelStyle();
            bigLabelStyle.font = FontPool.obtain(FontType.ROBOTO_REGULAR, 4);
            bigLabelStyle.fontColor = Color.BLACK;
            final Label.LabelStyle smallLabelStyle = new Label.LabelStyle();
            smallLabelStyle.font = FontPool.obtain(FontType.ROBOTO_REGULAR, 2);
//        smallLabelStyle.fontColor = Color.valueOf("535353");
            smallLabelStyle.fontColor = Color.BLACK;
            Table detailsTable = new Table();
            Stack imageStack = new Stack();
            Table rankTopTable = new Table();
            rankTopTable.top().left();
            Table rankTable = new Table();
            rankTable.setBackground(rankBackground);
            float rankSize = bigLabelStyle.font.getCapHeight() * 3;
            int rank = referralLeague.getMyDetail().getUserRank();
            final Label rankLabel = new Label("", bigLabelStyle);
            rankTable.add(rankLabel);
            if (rank <= 999) {
                rankLabel.setText(rank + "");
                rankLabel.setStyle(bigLabelStyle);
            } else {
                rankLabel.setText(rank + "");
                rankLabel.setStyle(smallLabelStyle);
            }
            rankTopTable.add(rankTable).width(rankSize).height(rankSize);

            Table imageTable = new Table();
            MaskedImage imageMasked = new MaskedImage(screen, AssetsLoader.instance().userRegion, AssetsLoader.instance().squareMaskLayer);

            if (Constant.userProfile.getImageUrl() != null && !Constant.userProfile.getImageUrl().isEmpty()) {
                pokerGame.downloadImage(Constant.userProfile.getImageUrl(), imageMasked);
            }

            float imageSize = buttonHeight * 2;
            imageTable.add(imageMasked).width(imageSize).height(imageSize).padLeft(rankSize / 2f).padTop(rankSize / 5f);

            imageStack.add(imageTable);
            imageStack.add(rankTopTable);

            detailsTable.add(imageStack).padRight(6 * density);


            Stack scoreStack = new Stack();
            Table scoreBackTable = new Table();
            NinePatchDrawable detailsBackground = new NinePatchDrawable(new NinePatch(uiAtlas.findRegion("bg_details"), 28, 28, 28, 28));
            scoreBackTable.setBackground(detailsBackground);
            Table scoreFrontTable = new Table();
            scoreFrontTable.padLeft(12 * density);
            Table scoreTable = new Table();
            scoreTable.add(new Label(Constant.userProfile.getName(), bigLabelStyle)).expandX().align(Align.left).row();
            Label.LabelStyle winnersStyle = new Label.LabelStyle();
            winnersStyle.font = FontPool.obtain(FontType.ROBOTO_BOLD, 7);
            winnersStyle.fontColor = Color.valueOf("325197");
            final Label pointLabel = new Label("Points: " + referralLeague.getMyDetail().getTotalPoint(), winnersStyle);
            scoreTable.add(pointLabel).expandX().align(Align.left).row();

            scoreFrontTable.add(scoreTable).height(imageSize).expandX().align(Align.left);
            Table prizeTable = new Table();
            NinePatchDrawable prizeBackground = new NinePatchDrawable(new NinePatch(uiAtlas.findRegion("bg_prize"), 47, 25, 25, 25));
            prizeTable.setBackground(prizeBackground);
            StyledLabel.StyledLabelStyle bigWhiteLabelStyle = new StyledLabel.StyledLabelStyle(FontType.ROBOTO_BOLD,6.5f);
            bigWhiteLabelStyle.fontColor= Color.WHITE;
            final StyledLabel prizeLabel = new StyledLabel("\u20b9" + referralLeague.getMyDetail().getWinningAmount(), bigWhiteLabelStyle);
            prizeLabel.shadow(1,1, Color.BLACK);
            prizeTable.add(prizeLabel);
            if (referralLeague.getMyDetail().getWinningAmount() > 0) {
                scoreFrontTable.add(prizeTable).height(imageSize).fillY();
            }

            scoreStack.add(scoreBackTable);
            scoreStack.add(scoreFrontTable);

            detailsTable.add(scoreStack).height(imageSize).padTop(rankSize / 5f).expandX().fill();
            inviteTable.add(detailsTable).expandX().fillX().padLeft(tableWidth * 0.03f).padRight(tableWidth * 0.03f).padBottom(tableWidth * 0.02f);
            mainTable.add(inviteTable).width(tableWidth).padTop(tableHeight * 0.09f).row();
            listener = new GdxListener<String>() {
                @Override
                public void onSuccess(String s) {
                    String[] arr = s.split("\\$");
                    if (Constant.userProfile.getReferralLeagueDetail().getReferralTotalParticipant() < 100) {
                        playersLabel.setText(arr[0] + " Players");
                    }
                    pointLabel.setText("Points: " + arr[2]);
                    if (Integer.parseInt(arr[1]) <= 999) {
                        rankLabel.setText(arr[1] + "");
                        rankLabel.setStyle(bigLabelStyle);
                    } else {
                        rankLabel.setText(arr[1] + "");
                        rankLabel.setStyle(smallLabelStyle);
                    }
                    prizeLabel.setText("\u20b9" + arr[3]);
                }

                @Override
                public void onFail(String reason) {

                }

                @Override
                public void onError(String errorMessage) {

                }
            };
        } else {
            bgTexture = uiAtlas.findRegion("bg_invite_small");
            tableHeight = tableWidth * bgTexture.getRegionHeight() / bgTexture.getRegionWidth();
            inviteTable.setBackground(TextureDrawable.getDrawable(bgTexture, tableWidth, tableHeight));
            mainTable.add(inviteTable).width(tableWidth).height(tableHeight).padTop(tableHeight * 0.1f).row();
            listener = new GdxListener<String>() {
                @Override
                public void onSuccess(String s) {
                    if (Constant.userProfile.getReferralLeagueDetail().getReferralTotalParticipant() < 100) {
                        playersLabel.setText(s + " Players");
                    }
                }

                @Override
                public void onFail(String reason) {

                }

                @Override
                public void onError(String errorMessage) {

                }
            };
        }
        inviteTable.setTouchable(Touchable.enabled);
        inviteTable.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                new ReferralLeagueDialog(screen,referralLeague,listener).show(screen.stage);
            }
        });

    }

    private void startTimer() {
        if (Constant.userProfile.getReferralLeagueDetail().isReferralMonthlyContestStatus()) {
            long leagueTime = Constant.userProfile.getReferralLeagueDetail().getReferralLeaderboardRemainingTime() - TimeUtils.millis() + Constant.userProfile.getReferralLeagueDetail().getLastUpdatedTime();
            PokerTimer.PokerTimerUpdater leagueTimeUpdate = new PokerTimer.PokerTimerUpdater() {
                @Override
                public void onTick(final long millisUntilFinished) {
                    Gdx.app.postRunnable(new Runnable() {
                        @Override
                        public void run() {
                            int days = (int) TimeUnit.MILLISECONDS.toDays(millisUntilFinished);
                            if (days > 0) {
                                if (days == 1) {
                                    timerLabel.setText(days + " Day");
                                } else {
                                    timerLabel.setText(days + " Days");
                                }
                            } else {
                                int hours = (int) TimeUnit.MILLISECONDS.toHours(millisUntilFinished);
                                long time = millisUntilFinished - TimeUnit.HOURS.toMillis(hours);
                                int minutes = (int) TimeUnit.MILLISECONDS.toMinutes(time);
                                time = time - TimeUnit.MINUTES.toMillis(minutes);
                                int seconds = (int) TimeUnit.MILLISECONDS.toSeconds(time);
                                String hou = hours < 10 ? "0" + hours : hours + "";
                                String min = minutes < 10 ? "0" + minutes : minutes + "";
                                String sec = seconds < 10 ? "0" + seconds : seconds + "";
                                timerLabel.setText(hou + ":" + min + ":" + sec);
                            }

                        }
                    });
                }

                @Override
                public void onFinish() {

                }
            };
            if (referralLeagueTimerCounter != null)
                referralLeagueTimerCounter.cancel();
            referralLeagueTimerCounter = new PokerTimer(leagueTime, 1000, leagueTimeUpdate);
            referralLeagueTimerCounter.start();
        }
    }

    @Override
    public Dialog show(Stage stage, Action action) {
        startTimer();
        return super.show(stage, action);
    }
}
