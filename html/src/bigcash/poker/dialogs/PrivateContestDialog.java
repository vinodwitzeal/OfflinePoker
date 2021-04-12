package bigcash.poker.dialogs;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import java.util.List;

import bigcash.poker.constants.Constant;
import bigcash.poker.game.poker.PokerLoginListener;
import bigcash.poker.game.poker.widgets.PokerStyle;
import bigcash.poker.game.poker.holdem.HoldemGameScreen;
import bigcash.poker.game.poker.holdem.controllers.HoldemRunningRoomListener;
import bigcash.poker.game.poker.holdem.controllers.HoldemWarpController;
import bigcash.poker.models.PokerContest;
import bigcash.poker.models.QRContests;
import bigcash.poker.models.QrInfo;
import bigcash.poker.models.UIDialog;
import bigcash.poker.models.UIScreen;
import bigcash.poker.network.ApiHandler;
import bigcash.poker.network.GdxListener;
import bigcash.poker.network.LocationHandler;
import bigcash.poker.network.PokerApi;
import bigcash.poker.utils.AssetsLoader;
import bigcash.poker.utils.DrawableBuilder;
import bigcash.poker.utils.GeolocationPosition;
import bigcash.poker.utils.PokerConstants;
import bigcash.poker.utils.PokerUtils;
import bigcash.poker.utils.TextureDrawable;
import bigcash.poker.widgets.MagicTable;
import bigcash.poker.widgets.ScrollTable;
import bigcash.poker.widgets.StyledLabel;

public class PrivateContestDialog extends UIDialog {
    private TextureAtlas uiAtlas;
    private PokerStyle pokerStyle;
    private float buttonWidth;
    private QrInfo qrInfo;
    private ProcessDialog processDialog;
    private ScrollTable scrollTable;
    private boolean fetchContest;
    private float contestWidth,contestPad;

    public PrivateContestDialog(UIScreen screen, QrInfo qrInfo, PokerStyle pokerStyle,boolean fetchContest) {
        super(screen);
        this.qrInfo = qrInfo;
        this.processDialog = new ProcessDialog(screen);
        this.pokerStyle =pokerStyle;
        this.uiAtlas = AssetsLoader.instance().uiAtlas;
        this.fetchContest=fetchContest;
        dismissOnBack(true);
        buildDialog();
    }

    public PrivateContestDialog(UIScreen screen, QrInfo qrInfo, PokerStyle pokerStyle) {
        this(screen,qrInfo,pokerStyle,false);
    }

    @Override
    public void init() {
    }

    @Override
    public void buildDialog() {
        float dialogWidth = width * 0.9f;
        contestWidth=dialogWidth*0.95f;
        contestPad=pokerStyle.bottomPad;
        float closeSize = width * 0.08f;
        buttonWidth = width * 0.32f;

        NinePatch greyNinePatch = new NinePatch(uiAtlas.findRegion("bottom_white"), 30, 30, 30, 30);
        NinePatchDrawable greybackground = new NinePatchDrawable(greyNinePatch);
        greybackground.setMinWidth(60);
        greybackground.setMinHeight(60);
        Table dataTable=new Table();

        Table topTable = new Table();
        TextureRegion logoBackgroundRegion=uiAtlas.findRegion("poker_contest_popup");
        float topTableWidth=dialogWidth;
        float topTableHeight=topTableWidth*logoBackgroundRegion.getRegionHeight()/logoBackgroundRegion.getRegionWidth();
        TextureRegionDrawable logoBackground= TextureDrawable.getDrawable(logoBackgroundRegion,topTableWidth,topTableHeight);
        topTable.setBackground(logoBackground);
        Image logoImage=new Image();
        pokerGame.downloadImage(qrInfo.getLogoImageUrl(),logoImage);
        topTable.add(logoImage).width(topTableWidth*0.4f).height(topTableHeight*0.4f);
        dataTable.add(topTable).width(topTableWidth).height(topTableHeight).row();
        Table bottomTable=new MagicTable(greybackground,"233352");
        scrollTable =getScrollTable();
        bottomTable.add(scrollTable).padBottom(5 * density).padTop(3 * density).width(dialogWidth).height(height*0.5f);
        dataTable.add(bottomTable).width(dialogWidth);

        Table closeTable = new Table();
        closeTable.top().right();
        TextureRegion closeTexture = uiAtlas.findRegion("btn_close");
        TextureRegionDrawable closeDrawable = DrawableBuilder.getDrawable(closeTexture, closeSize, closeSize);
        ImageButton closeButton = new ImageButton(closeDrawable, closeDrawable, closeDrawable);
        closeTable.add(closeButton).width(closeSize).height(closeSize).padRight(5 * density);
        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hide();
            }
        });
        Stack stack = new Stack();
        Table backTable = new Table();
        float sidePad = closeSize/2f;
        backTable.add(dataTable).width(dialogWidth).pad(sidePad, sidePad, 0, sidePad);
        stack.add(backTable);
        stack.add(closeTable);
        getContentTable().add(stack);
    }


    private ScrollTable getScrollTable(){
        ScrollTable.ScrollTableStyle scrollTableStyle=new ScrollTable.ScrollTableStyle();
        scrollTableStyle.processSize=width*0.1f;
        scrollTableStyle.refreshSize=width*0.05f;
        scrollTable=new ScrollTable(scrollTableStyle) {
            @Override
            public void onRefresh() {
                if (fetchContest) {
                    HoldemWarpController controller = new HoldemWarpController(pokerGame, null, null, qrInfo.getQrId(), PokerConstants.PRIVATE_TABLE);
                    HoldemRunningRoomListener holdemRunningRoomListener = new HoldemRunningRoomListener(screen, controller, qrInfo.getQrId()) {
                        @Override
                        public void onGetContestId(int contestId) {
                            getLocation(contestId);
                        }
                    };
                    holdemRunningRoomListener.connect();
                }else {
                    getLocation(0);
                }
            }
        };
        return scrollTable;
    }

    private void getLocation(int contestId){
        PokerUtils.getLocation(new LocationHandler() {
            @Override
            public void onSuccess(GeolocationPosition position) {
                getContests(position,contestId);
            }

            @Override
            public void onFailed(){
                getContests(null,contestId);
            }
        });
    }

    private void getContests(GeolocationPosition position,int contestId){
        PokerApi.callPrivateContestApi(position,contestId, new GdxListener<QRContests>() {
            @Override
            public void onSuccess(QRContests contests) {
                scrollTable.contentTable.clear();
                if (fetchContest){
                    if (contests.getMatchedContests().size()==0){
                        for (int key:contests.getMatchedContests().keySet()){
                            List<PokerContest> contestList=contests.getMatchedContests().get(key);
                            for (PokerContest contest:contestList){
                                scrollTable.contentTable.add(getContestTable(contest)).width(contestWidth).padTop(contestPad).row();
                            }
                        }
                    }else {
                        for (int key:contests.getAllContests().keySet()){
                            List<PokerContest> contestList=contests.getAllContests().get(key);
                            for (PokerContest contest:contestList){
                                scrollTable.contentTable.add(getContestTable(contest)).width(contestWidth).padTop(contestPad).row();
                            }
                        }
                    }
                }else {
                    for (int key:contests.getAllContests().keySet()){
                        List<PokerContest> contestList=contests.getAllContests().get(key);
                        for (PokerContest contest:contestList){
                            scrollTable.contentTable.add(getContestTable(contest)).width(contestWidth).padTop(contestPad).row();
                        }
                    }
                }
                fetchContest=false;
                scrollTable.onRefreshCompleted();
            }

            @Override
            public void onFail(String reason) {
                scrollTable.onRefreshError(reason);
            }

            @Override
            public void onError(String error) {
                scrollTable.onRefreshError(error);
            }
        });
    }


    public Table getContestTable(final PokerContest contest) {
        Table containerTable = new Table();
        Table prizeTable = new MagicTable(pokerStyle.prizeBackground);
        prizeTable.add(new Label("\u20b9" + contest.getTotalWinnings(), pokerStyle.prizeStyle)).row();
        prizeTable.add(new Label("PRIZE", pokerStyle.prizeLabelStyle));

        containerTable.add(prizeTable).width(pokerStyle.prizeWidth).uniformY().fillY().padRight(2);

        Table contestTable = new MagicTable(pokerStyle.contestBackground);
        contestTable.pad(pokerStyle.cardGap, 0, pokerStyle.cardGap, pokerStyle.cardGap);
        Table formatTable = new Table();
        formatTable.add(new Label("Bet Value", pokerStyle.labelStyle)).row();
        formatTable.add(new Label(PokerUtils.getValue(contest.getBetValue())+"", pokerStyle.valueStyle));
        contestTable.add(formatTable).expandX();

        Table playerTable = new Table();
        playerTable.add(new Label("Max Players", pokerStyle.labelStyle)).row();
        Table playerCountTable = new Table();
        playerCountTable.add(new Image(pokerStyle.playerIconRegion)).width(pokerStyle.playerIconWidth).height(pokerStyle.playerIconHeight);
        playerCountTable.add(new Label(" " + contest.getMaxUsersPerTable(), pokerStyle.valueStyle));
        playerTable.add(playerCountTable);
        contestTable.add(playerTable).expandX();


        Table buttonTable = new Table();
        buttonTable.setBackground(pokerStyle.buttonBackground);
        StyledLabel entryLabel = new StyledLabel("Entry Fee:", pokerStyle.entryLabelStyle);
        entryLabel.outline(0.25f, Color.valueOf("00000022"));
        buttonTable.add(entryLabel);
        StyledLabel entryFeeLabel;
        if (contest.getMinJoiningFee() >= 1000) {
            entryFeeLabel = new StyledLabel("\u20b9" + getAmountString(contest.getMinJoiningFee()) + "/" + getAmountString(contest.getMaxJoiningFee()), pokerStyle.entryStyle);
        } else {
            entryFeeLabel = new StyledLabel("\u20b9" + getAmountString(contest.getMinJoiningFee()) + "/" + getAmountString(contest.getMaxJoiningFee()), pokerStyle.entryStyle);
        }

        entryFeeLabel.outline(0.25f, Color.valueOf("00000022"));
        buttonTable.add(entryFeeLabel);
        contestTable.add(buttonTable).width(pokerStyle.buttonWidth).height(pokerStyle.buttonHeight);
        containerTable.add(contestTable).uniformY().expandX().fill();
        buttonTable.setTouchable(Touchable.enabled);
        buttonTable.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if ((Constant.userProfile.getMsisdn() == null || Constant.userProfile.getMsisdn().isEmpty())) {
                    new MobileVerificationDialog(screen, new GdxListener<String>() {
                        @Override
                        public void onSuccess(String s) {
                            toast("Number Verified");
                            onEntryButtonClick(contest);
                        }

                        @Override
                        public void onFail(String reason) {

                        }

                        @Override
                        public void onError(String errorMessage) {

                        }
                    }).show(screen.stage);
                    return;
                }
                onEntryButtonClick(contest);
            }
        });
        return containerTable;
    }

    private void onEntryButtonClick(PokerContest contest) {
        if (Constant.userProfile.getPaytmBalance() >= contest.getMinJoiningFee()) {
            processDialog.show(screen.stage);
            if (PokerApi.isUseLocation() && contest.getMaxUsersPerTable() == 5) {
                useLocation(contest);
            } else {
                entryInPoker(contest, null);
            }
        } else {
            processDialog.hide();
            new CashAddDialog(screen, "PrivateContestScreen", contest.getMinJoiningFee()).show(screen.stage);
        }
    }


    private void useLocation(final PokerContest contest) {
        if (PokerApi.isFetchFromGpsLocation()) {
            PokerUtils.getLocation(new LocationHandler() {
                @Override
                public void onSuccess(GeolocationPosition position) {
                    callUserLocationApi(contest, position);
                }

                @Override
                public void onFailed() {
                    callUserLocationApi(contest, null);
                }
            });
        } else {
            callUserLocationApi(contest, null);
        }
    }

    private void callUserLocationApi(final PokerContest contest,final GeolocationPosition position) {
        PokerApi.callUserLocationApi(position, new GdxListener<String>() {
            @Override
            public void onSuccess(String s) {
                entryInPoker(contest, position);
            }

            @Override
            public void onFail(String reason) {
                processDialog.hide();
                toast(reason);
            }

            @Override
            public void onError(String errorMessage) {
                processDialog.hide();
                toast("Server Busy");
                ApiHandler.callEventLogApi("UserLocationApiError", errorMessage, errorMessage);
            }
        });
    }


    private void entryInPoker(final PokerContest contest,final GeolocationPosition position) {
        float amount = contest.getMaxJoiningFee();
        if (Constant.userProfile.getPaytmBalance() >= contest.getMinJoiningFee()
                && Constant.userProfile.getPaytmBalance() < contest.getMaxJoiningFee()) {
            amount = Constant.userProfile.getPaytmBalance();
        }
        if (amount <= Constant.userProfile.getPaytmBalance()) {
            callRefillServerApi(contest, position, amount);
        } else {
            processDialog.hide();
            final float addAmount=amount;
            new CashAddDialog(screen, "PrivateContestScreen", amount, new GdxListener<String>() {
                @Override
                public void onSuccess(String s) {
                    processDialog.show(screen.stage);
                    callRefillServerApi(contest, position, addAmount);
                }

                @Override
                public void onFail(String reason) {

                }

                @Override
                public void onError(String errorMessage) {

                }
            }).show(screen.stage);
        }
    }

    private void callRefillServerApi(final PokerContest contest,final GeolocationPosition position, final float amount) {
        PokerApi.callRefillApi(contest.getContestId() + "", amount + "", new GdxListener<String>() {
            @Override
            public void onSuccess(String s) {
                HoldemWarpController holdemWarpController = new HoldemWarpController(pokerGame, position, contest, qrInfo.getQrId(), PokerConstants.PRIVATE_TABLE);
                new PokerLoginListener(screen, holdemWarpController){
                    @Override
                    public void setScreen() {
                        pokerGame.setScreen(new HoldemGameScreen(pokerGame,holdemWarpController,contest.getContestId(),qrInfo.getQrId(), PokerConstants.PRIVATE_TABLE));
                    }
                }.connect();
            }

            @Override
            public void onFail(String reason) {
                processDialog.hide();

                //TODO
//                getProGame().config.showToast(reason);
            }

            @Override
            public void onError(String errorMessage) {
                processDialog.hide();
//
            }
        });
    }


    private String getAmountString(float value) {
        if (value < 1000) {
            return (int) value + "";
        } else {
            float value1 = PokerUtils.getValue(value / 1000);
            if (value1 == (int) value1) {
                return (int) value1 + "K";
            } else {
                return value1 + "K";
            }
        }
    }


    public Table buildDataTable() {
        Table dataTable = new Table();
//        NinePatch bgNinePatch = new NinePatch(commonAtlas.findRegion("bg_dialog"), 30, 30, 30, 30);
//        NinePatchDrawable background = new NinePatchDrawable(bgNinePatch);
//        background.setMinWidth(60);
//        background.setMinHeight(60);
//        dataTable.setBackground(background);
        return dataTable;
    }

    @Override
    public Dialog show(Stage stage) {
        super.show(stage);
        scrollTable.setOnRefresh();
        return this;
    }
}

