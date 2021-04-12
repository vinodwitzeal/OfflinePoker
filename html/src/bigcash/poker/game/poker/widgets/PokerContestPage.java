package bigcash.poker.game.poker.widgets;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;

import bigcash.poker.constants.Constant;
import bigcash.poker.dialogs.CashAddDialog;
import bigcash.poker.dialogs.MobileVerificationDialog;
import bigcash.poker.dialogs.ProcessDialog;
import bigcash.poker.game.poker.holdem.widgets.PokerContestStyle;
import bigcash.poker.gwt.PokerGame;
import bigcash.poker.models.PokerContest;
import bigcash.poker.network.ApiHandler;
import bigcash.poker.network.GdxListener;
import bigcash.poker.network.LocationHandler;
import bigcash.poker.network.PokerApi;
import bigcash.poker.screens.PokerContestScreen;
import bigcash.poker.utils.GeolocationPosition;
import bigcash.poker.utils.PokerUtils;
import bigcash.poker.widgets.MagicTable;
import bigcash.poker.widgets.PageView;
import bigcash.poker.widgets.StyledLabel;

public abstract class PokerContestPage extends PageView.PageContent {
    public PokerContestScreen contestScreen;
    public PokerStyle pokerStyle;
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
        this.pokerStyle=contestScreen.pokerStyle;
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


        return containerTable;
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
