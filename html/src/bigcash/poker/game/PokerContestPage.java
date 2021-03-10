package bigcash.poker.game;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

import bigcash.poker.constants.Constant;
import bigcash.poker.dialogs.CashAddDialog;
import bigcash.poker.dialogs.MobileVerificationDialog;
import bigcash.poker.dialogs.ProcessDialog;
import bigcash.poker.game.holdem.widgets.PokerContestStyle;
import bigcash.poker.gwt.PokerGame;
import bigcash.poker.models.PokerContest;
import bigcash.poker.network.ApiHandler;
import bigcash.poker.network.GdxListener;
import bigcash.poker.network.LocationHandler;
import bigcash.poker.network.PokerApi;
import bigcash.poker.screens.PokerContestScreen;
import bigcash.poker.utils.GeolocationPosition;
import bigcash.poker.utils.PokerUtils;
import bigcash.poker.widgets.PageView;
import bigcash.poker.widgets.StyledLabel;

public abstract class PokerContestPage extends PageView.PageContent {
    public PokerContestScreen contestScreen;
    public PokerContestStyle contestStyle;
    public PokerGame pokerGame;
    public float buttonWidth;
    public ProcessDialog processDialog;
    public float amount;
    private String gameType;

    public PokerContestPage(PokerContestScreen contestScreen, PageView pageView, String gameType) {
        super(pageView);
        this.gameType = gameType;
        this.processDialog = new ProcessDialog(contestScreen);
        this.contestScreen = contestScreen;
        this.contestStyle = contestScreen.contestStyle;
        this.pokerGame = contestScreen.pokerGame;
        this.buttonWidth = contestScreen.width * 0.32f;
    }

    @Override
    public void onRefresh() {
        contestScreen.gameType = gameType;
        PokerUtils.getLocation(new LocationHandler() {
            @Override
            public void onSuccess(GeolocationPosition position) {
                callPokerContestApi(position);
            }

            @Override
            public void onFailed() {
                callPokerContestApi(null);
            }
        });
    }

    public Table getContestTable(final PokerContest contest) {
        final Table stackTable = new Table();
        Table frontTable = new Table();
        float downButtonHeight = contestStyle.bigLabelStyle.font.getCapHeight();
        float downButtonWidth = downButtonHeight * contestStyle.downDrawable.getMinWidth() / contestStyle.downDrawable.getMinHeight();
        frontTable.setBackground(contestStyle.background);
        frontTable.pad(0, downButtonHeight * 4.8f, downButtonHeight * 0.5f, downButtonHeight);


        if (contest.getOnlineUsers() > 0) {
            Table onlineTable = new Table();
            onlineTable.add(new Image(contestStyle.onlineRegion)).height(downButtonHeight).width(downButtonHeight * 1.2f).padRight(downButtonHeight * 0.3f);
            Label onlineUser = new Label(contest.getOnlineUsers() + " online", contestStyle.onlineStyle);
            onlineTable.add(onlineUser);
            frontTable.add(onlineTable)
                    .colspan(3)
                    .expandX()
                    .align(Align.right)
                    .padBottom(downButtonHeight * 0.5f)
                    .padTop(downButtonHeight * 0.5f)
                    .row();
            onlineUser.addAction(Actions.repeat(RepeatAction.FOREVER, Actions.sequence(
                    Actions.fadeIn(0.1f), Actions.delay(1.0f),
                    Actions.fadeOut(0.1f)

            )));
        } else {
            Table onlineTable = new Table();
            onlineTable.add().height(downButtonHeight).width(downButtonHeight * 1.2f).padRight(downButtonHeight * 0.3f);
            Label onlineUser = new Label("", contestStyle.onlineStyle);
            onlineTable.add(onlineUser);
            frontTable.add(onlineTable)
                    .colspan(3)
                    .expandX()
                    .align(Align.right)
                    .padBottom(downButtonHeight * 0.5f)
                    .padTop(downButtonHeight * 0.5f)
                    .row();
        }

        Table winnersTable = new Table();
        winnersTable.add(new Label("Bet Value", contestStyle.bigLabelStyle)).row();
        winnersTable.add(new Label(contest.getBetValue() + "", contestStyle.winnersStyle));

        frontTable.add(winnersTable);


        Table winningsTable = new Table();
        winningsTable.add(new Label("Winnings", contestStyle.bigLabelStyle)).row();
        final String numberAsString = contest.getTotalWinnings() + "";
        winningsTable.add(new Label("\u20b9" + numberAsString, contestStyle.winningsStyle));
        frontTable.add(winningsTable).align(Align.center).expandX();

        Table buttonTable = new Table();
        buttonTable.setBackground(contestStyle.blueButton);

        buttonTable.add(StyledLabel.getLabel("Entry Fee:", contestStyle.smallWhiteLabelStyle,contestStyle.fontStyle)).padRight(6 * density);
        if (contest.getMinJoiningFee() >= 1000) {
            buttonTable.add(StyledLabel.getLabel("\u20b9" + getAmountString(contest.getMinJoiningFee()) + "/" + getAmountString(contest.getMaxJoiningFee()), contestStyle.pokerWhiteLabelStyleForHighEntry1,contestStyle.fontStyle));
        } else {
            buttonTable.add(StyledLabel.getLabel("\u20b9" + getAmountString(contest.getMinJoiningFee()) + "/" + getAmountString(contest.getMaxJoiningFee()), contestStyle.pokerBigWhiteLabelStyle,contestStyle.fontStyle));
        }

//        }
        buttonTable.setTouchable(Touchable.enabled);
        buttonTable.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if ((Constant.userProfile.getMsisdn() == null || Constant.userProfile.getMsisdn().isEmpty())) {
                    new MobileVerificationDialog(contestScreen, new GdxListener<String>() {
                        @Override
                        public void onSuccess(String s) {
                            contestScreen.toast("Number Verified");
                            onEntryButtonClick(contest);
                        }

                        @Override
                        public void onFail(String reason) {

                        }

                        @Override
                        public void onError(String errorMessage) {

                        }
                    }).show(contestScreen.stage);
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
                    .padTop(downButtonHeight * 0.5f)
                    .row();
            offer.addAction(Actions.repeat(RepeatAction.FOREVER, Actions.sequence(
                    Actions.fadeIn(0.1f), Actions.delay(1.0f),
                    Actions.fadeOut(0.1f)

            )));
        }

        float titleHeight = downButtonHeight * 4;
        float titleWidth = titleHeight * 1.32f;

        Table titleTable = new Table();
        titleTable.top().left();

        Image titleImage = new Image();
        if (contest.getImageUrl() != null) {
            pokerGame.downloadImage(contest.getImageUrl(), titleImage);
        }
        titleTable.add(titleImage).width(titleWidth).height(titleHeight).padTop(titleHeight * 0.25f);

        Table backTable = new Table();
        backTable.add(frontTable).width(contestStyle.tableWidth).pad(titleHeight * 0.5f, downButtonHeight, 0, 0);

        Table topTable = new Table();
        topTable.top().right();
        if (contest.getMaxUsersPerTable() == 2) {
            topTable.add(new Label(contest.getMaxUsersPerTable() + " Players", contestStyle.twoPlayerStyle));
        } else {
            topTable.add(new Label(contest.getMaxUsersPerTable() + " Players", contestStyle.fivePlayerStyle));
        }
        Stack stack = new Stack();
        stack.add(topTable);
        stack.add(backTable);
        stack.add(titleTable);

        stackTable.add(stack).expand().fill().pad(4 * density);
        return stackTable;
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

    public void onEntryButtonClick(PokerContest contest) {
        if (Constant.userProfile.getPaytmBalance() >= contest.getMinJoiningFee()) {
            processDialog.show(contestScreen.stage);
            if (PokerApi.isUseLocation() && contest.getMaxUsersPerTable() == 5) {
                useLocation(contest);
            } else {
                entryInPoker(contest, null);
            }
        } else {
            new CashAddDialog(contestScreen, "PokerContestScreen", contest.getMinJoiningFee()).show(contestScreen.stage);
        }
    }

    public void useLocation(PokerContest contest) {
        if (PokerApi.isFetchFromGpsLocation()) {
            PokerUtils.getLocation(new LocationHandler() {
                @Override
                public void onSuccess(GeolocationPosition position) {
                    processDialog.show(contestScreen.stage);
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

    public void callUserLocationApi(PokerContest contest, GeolocationPosition position) {
        PokerApi.callUserLocationApi(position, new GdxListener<String>() {
            @Override
            public void onSuccess(String s) {
                entryInPoker(contest, position);
            }

            @Override
            public void onFail(String reason) {
                processDialog.hide();
                contestScreen.toast(reason);
            }

            @Override
            public void onError(String errorMessage) {
                processDialog.hide();
                contestScreen.toast("Server Busy");
                ApiHandler.callEventLogApi("UserLocationApiError", errorMessage, errorMessage);
            }
        });
    }

    public void entryInPoker(final PokerContest contest, GeolocationPosition position) {
        amount = contest.getMaxJoiningFee();
        if (Constant.userProfile.getPaytmBalance() >= contest.getMinJoiningFee()
                && Constant.userProfile.getPaytmBalance() < contest.getMaxJoiningFee()) {
            amount = Constant.userProfile.getPaytmBalance();
        }
        if (amount <= Constant.userProfile.getPaytmBalance()) {
            callRefillServerApi(contest, position, amount);
        } else {
            processDialog.hide();
            new CashAddDialog(contestScreen, "PokerContestScreen", amount, new GdxListener<String>() {
                @Override
                public void onSuccess(String s) {
                    processDialog.show(contestScreen.stage);
                    callRefillServerApi(contest, position, amount);
                }

                @Override
                public void onFail(String reason) {

                }

                @Override
                public void onError(String errorMessage) {

                }
            }).show(contestScreen.stage);
        }
    }

    private void callRefillServerApi(final PokerContest contest, GeolocationPosition position, final float amount) {
        PokerApi.callRefillApi(contest.getContestId() + "", amount + "", new GdxListener<String>() {
            @Override
            public void onSuccess(String s) {
                enterGame(contest,position);
            }

            @Override
            public void onFail(String reason) {
                processDialog.hide();
                contestScreen.toast(reason);
            }

            @Override
            public void onError(String errorMessage) {
                processDialog.hide();
            }
        });
    }


    private void callPokerContestApi(GeolocationPosition position) {
        PokerApi.callPokerContestApi(position, gameType, new GdxListener<String>() {
            @Override
            public void onSuccess(String s) {
                contestScreen.updateBalance();
                clearContentTable();
                if ((PokerApi.pokerContestMax5 == null || PokerApi.pokerContestMax5.size == 0) && (PokerApi.pokerContestMax2 == null || PokerApi.pokerContestMax2.size == 0)) {
                    onRefreshError("No Live Contests");
                } else {
                    onContestsFetched(PokerApi.pokerContestMax5, PokerApi.pokerContestMax2);
                    onRefreshCompleted();
                }
            }

            @Override
            public void onFail(String reason) {
                onRefreshError(reason);
            }

            @Override
            public void onError(String errorMessage) {
                onRefreshError(errorMessage);
            }
        });
    }

    public abstract void onContestsFetched(Array<PokerContest> max5Contests, Array<PokerContest> max2Contests);

    public abstract void enterGame(PokerContest contest, GeolocationPosition position);


}
