package bigcash.poker.dialogs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

import java.util.List;

import bigcash.poker.constants.Constant;
import bigcash.poker.font.FontPool;
import bigcash.poker.font.FontType;
import bigcash.poker.game.PokerLoginListener;
import bigcash.poker.game.holdem.HoldemGameScreen;
import bigcash.poker.game.holdem.controllers.HoldemRunningRoomListener;
import bigcash.poker.game.holdem.controllers.HoldemWarpController;
import bigcash.poker.game.holdem.widgets.PokerContestStyle;
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
import bigcash.poker.widgets.ScrollTable;
import bigcash.poker.widgets.StyledLabel;

public class PrivateContestDialog extends UIDialog {
    private TextureAtlas uiAtlas;
    private PokerContestStyle contestStyle;
    private float buttonWidth;
    private QrInfo qrInfo;
    private ProcessDialog processDialog;
    private ScrollTable scrollTable;
    private boolean fetchContest;
    private float contestWidth,contestPad;

    public PrivateContestDialog(UIScreen screen, QrInfo qrInfo, PokerContestStyle contestStyle,boolean fetchContest) {
        super(screen);
        this.qrInfo = qrInfo;
        this.processDialog = new ProcessDialog(screen);
        this.contestStyle = contestStyle;
        this.uiAtlas = AssetsLoader.instance().uiAtlas;
        this.fetchContest=fetchContest;
        dismissOnBack(true);
        buildDialog();
    }

    public PrivateContestDialog(UIScreen screen, QrInfo qrInfo, PokerContestStyle contestStyle) {
        this(screen,qrInfo,contestStyle,false);
    }

    @Override
    public void init() {
    }

    @Override
    public void buildDialog() {
        float dialogWidth = width * 0.9f;
        contestWidth=dialogWidth*0.95f;
        Gdx.app.error("PC Dialog","84");

        contestPad=contestStyle.bigLabelStyle.font.getCapHeight();
        Gdx.app.error("PC Dialog","87");

        float closeSize = width * 0.08f;
        buttonWidth = width * 0.32f;
        Table dataTable = buildDataTable();
        NinePatch greyNinePatch = new NinePatch(uiAtlas.findRegion("bottom_white"), 30, 30, 30, 30);
        NinePatchDrawable greybackground = new NinePatchDrawable(greyNinePatch);
        greybackground.setMinWidth(60);
        greybackground.setMinHeight(60);

        TextureRegion bg_phone = uiAtlas.findRegion("poker_contest_popup");

        NinePatch whiteNinePatch = new NinePatch(uiAtlas.findRegion("poker_contest_popup"), 30, 30, 30, 30);
        NinePatchDrawable whitebackground = new NinePatchDrawable(whiteNinePatch);
        whitebackground.setMinWidth(60);
        whitebackground.setMinHeight(60);



        Table topTable = new Table();
        topTable.setBackground(whitebackground);
        topTable.top();

        Table bottomTable = new Table();
        bottomTable.setBackground(greybackground);
        bottomTable.bottom();

        Table logoTable = new Table();
        float phoneWidth = dialogWidth;
        float phoneHeight = phoneWidth * bg_phone.getRegionHeight() / bg_phone.getRegionWidth();
        Table bgTable = new Table();
        bgTable.top();
        bgTable.add(new Image(bg_phone)).height(phoneHeight * 1.2f).width(phoneWidth).align(Align.top);
        Image blindImage = new Image();
        pokerGame.downloadImage(qrInfo.getLogoImageUrl(), blindImage);
        logoTable.add(blindImage).width(phoneHeight * .4f).height(phoneHeight * .4f).row();
        topTable.add(logoTable).height(phoneHeight).width(phoneWidth).row();

        Gdx.app.error("PC Dialog","1");

        scrollTable =getScrollTable();
        bottomTable.add(scrollTable).padBottom(5 * density).padTop(-3 * density).width(dialogWidth).height(height*0.5f);

        Label.LabelStyle messageStyle = new Label.LabelStyle();
        messageStyle.font = FontPool.obtain(FontType.ROBOTO_BOLD, 9);
        messageStyle.fontColor = Color.BLACK;

        Label.LabelStyle subMessageStyle = new Label.LabelStyle();
        subMessageStyle.font = FontPool.obtain(FontType.ROBOTO_BOLD, 6);
        subMessageStyle.fontColor = Color.GRAY;

        dataTable.add(topTable).width(dialogWidth).row();
        dataTable.add(bottomTable).width(dialogWidth).row();

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

        Gdx.app.error("PC Dialog","2");
//        stack.add(closeTable);
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
        Table frontTable = new Table();
        NinePatchDrawable background = new NinePatchDrawable(new NinePatch(uiAtlas.findRegion("private_table_bg"), 8, 8, 8, 8));
        frontTable.setBackground(background);
        frontTable.pad(contestPad, contestPad*2, contestPad, contestPad);


        Table winnersTable = new Table();
        winnersTable.add(new Label("Bet Value", contestStyle.bigLabelStyle)).row();
        winnersTable.add(new Label(contest.getBetValue() + "", contestStyle.winnersStyle));

        frontTable.add(winnersTable);


        Table winningsTable = new Table();
        winningsTable.add(new Label("Winnings", contestStyle.bigLabelStyle)).row();
        final String numberAsString = PokerUtils.getValue(contest.getTotalWinnings()) + "";
        winningsTable.add(new Label("\u20b9" + numberAsString, contestStyle.winningsStyle));
        frontTable.add(winningsTable).expandX();

        Table buttonTable = new Table();
        buttonTable.setBackground(contestStyle.blueButton);

        buttonTable.add(StyledLabel.getLabel("Entry Fee:", contestStyle.smallWhiteLabelStyle,contestStyle.fontStyle)).padRight(6 * density);

        if (contest.getMinJoiningFee() >= 1000) {
            buttonTable.add(StyledLabel.getLabel("\u20b9" + getAmountString(contest.getMinJoiningFee()) + "/" + getAmountString(contest.getMaxJoiningFee()), contestStyle.pokerWhiteLabelStyleForHighEntry1,contestStyle.fontStyle));
        } else {
            buttonTable.add(StyledLabel.getLabel("\u20b9" + getAmountString(contest.getMinJoiningFee()) + "/" + getAmountString(contest.getMaxJoiningFee()), contestStyle.pokerBigWhiteLabelStyle,contestStyle.fontStyle));
        }

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


        frontTable.add(buttonTable).width(buttonWidth).align(Align.right).uniformY().fillY();

        frontTable.row();

        if (contest.getOfferText() != null && !contest.getOfferText().isEmpty()) {
            Label offer = new Label("Offer: " + contest.getOfferText(), contestStyle.offerStyle);
            frontTable.add(offer)
                    .colspan(3)
                    .expandX()
                    .align(Align.center)
                    .padTop(contestPad * 0.5f)
                    .row();
            offer.addAction(Actions.repeat(RepeatAction.FOREVER, Actions.sequence(
                    Actions.fadeIn(0.1f), Actions.delay(1.0f),
                    Actions.fadeOut(0.1f)

            )));
        }

        return frontTable;
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

