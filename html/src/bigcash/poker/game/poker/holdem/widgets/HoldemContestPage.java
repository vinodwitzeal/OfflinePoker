package bigcash.poker.game.poker.holdem.widgets;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;

import bigcash.poker.dialogs.PrivateContestDialog;
import bigcash.poker.dialogs.ProcessDialog;
import bigcash.poker.game.poker.widgets.PokerContestPage;
import bigcash.poker.game.poker.PokerLoginListener;
import bigcash.poker.game.poker.holdem.HoldemGameScreen;
import bigcash.poker.game.poker.holdem.controllers.HoldemWarpController;
import bigcash.poker.models.PokerContest;
import bigcash.poker.models.QrInfo;
import bigcash.poker.network.GdxListener;
import bigcash.poker.network.PokerApi;
import bigcash.poker.qr.QrScannerDialog;
import bigcash.poker.screens.PokerContestScreen;
import bigcash.poker.utils.GeolocationPosition;
import bigcash.poker.utils.PokerConstants;
import bigcash.poker.widgets.PageView;

public class HoldemContestPage extends PokerContestPage {
    public Image tableLogoImage;
    public Label buttonLabel;
    public HoldemContestPage(PokerContestScreen contestScreen, PageView pageView) {
        super(contestScreen, pageView,PokerContestScreen.HOLDEM);
    }

    @Override
    public void onRefresh() {
        if (pokerGame.qrInfo==null && pokerGame.qrId!=null && !pokerGame.qrId.isEmpty()){
            PokerApi.getQRInfo(pokerGame.qrId, new GdxListener<QrInfo>() {
                @Override
                public void onSuccess(QrInfo qrInfo) {
                    pokerGame.qrInfo=qrInfo;
                    HoldemContestPage.super.onRefresh();
                }

                @Override
                public void onFail(String reason) {
                    HoldemContestPage.super.onRefresh();
                }

                @Override
                public void onError(String error) {
                    HoldemContestPage.super.onRefresh();
                }
            });
        }else {
            super.onRefresh();
        }

    }

    @Override
    public void onContestsFetched(Array<PokerContest> max5Contests, Array<PokerContest> max2Contests) {
        addPrivateContestTable(contentTable,pokerGame.qrInfo);
        if (max5Contests!=null && max5Contests.size>0) {
            for (int i = 0; i < max5Contests.size; i++) {
                contentTable.add(getContestTable(max5Contests.get(i))).width(pokerStyle.width).padBottom(pokerStyle.bottomPad).row();
            }
        }
        if (max2Contests!=null && max2Contests.size>0) {
            contentTable.add().padTop(density * 10f).row();
            for (int i = 0; i < max2Contests.size; i++) {
                contentTable.add(getContestTable(max2Contests.get(i))).width(pokerStyle.width).padBottom(pokerStyle.bottomPad).row();
            }
        }
    }


    public void addPrivateContestTable(Table contentTable,QrInfo qrInfo) {
        WidgetGroup widgetGroup=new WidgetGroup();
        Image backgroundImage=new Image(pokerStyle.offlineBackgroundTexture);
        backgroundImage.setSize(pokerStyle.backgroundWidth,pokerStyle.backgroundHeight);
        backgroundImage.setPosition(pokerStyle.backgroundX,pokerStyle.backgroundY);
        widgetGroup.addActor(backgroundImage);

        tableLogoImage=new Image();
        tableLogoImage.setSize(pokerStyle.offlineIconSize,pokerStyle.offlineIconSize);
        tableLogoImage.setPosition(pokerStyle.offlineIconX,pokerStyle.offlineIconY);
        widgetGroup.addActor(tableLogoImage);
        if (qrInfo!=null) {
            pokerGame.downloadImage(qrInfo.getLogoImageUrl(), tableLogoImage);
        }

        Table scanButton=new Table();
        scanButton.setBackground(pokerStyle.scanButtonBackground);
        scanButton.add(new Image(pokerStyle.qrIconRegion)).width(pokerStyle.qrIconWidth).height(pokerStyle.qrIconHeight).padRight(pokerStyle.qrIconWidth/2f);
        buttonLabel=new Label("",pokerStyle.scanButtonStyle);
        if (qrInfo==null){
            buttonLabel.setText("Scan");

        }else {
            buttonLabel.setText("Play");
        }
        scanButton.add(buttonLabel);
        scanButton.setSize(pokerStyle.scanButtonWidth,pokerStyle.scanButtonHeight);
        scanButton.setPosition(pokerStyle.scanButtonX,pokerStyle.scanButtonY);
        widgetGroup.addActor(scanButton);

        scanButton.setTouchable(Touchable.enabled);
        scanButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                onPrivateButtonClicked(tableLogoImage,buttonLabel);
            }
        });

        Image titleImage=new Image();
        titleImage.setSize(pokerStyle.logoWidth,pokerStyle.logoHeight);
        titleImage.setPosition(pokerStyle.logoX,pokerStyle.logoY);
        widgetGroup.addActor(titleImage);
        pokerGame.downloadImage("https://1101993670.rsc.cdn77.org/img/private_table_logo.png", titleImage);

        contentTable.add(widgetGroup).width(pokerStyle.offlineWidth).height(pokerStyle.offlineHeight).padTop(pokerStyle.topPad).padBottom(pokerStyle.bottomPad).row();
    }

    private void onPrivateButtonClicked(Image logoImage,Label buttonLabel){
        if (pokerGame.qrInfo==null){
            new QrScannerDialog(contestScreen, new GdxListener<String>() {
                @Override
                public void onSuccess(String qrId) {
                    ProcessDialog processDialog=new ProcessDialog(contestScreen);
                    processDialog.show(contestScreen.stage);
                    PokerApi.getQRInfo(qrId, new GdxListener<QrInfo>() {
                        @Override
                        public void onSuccess(QrInfo qrInfo) {
                            pokerGame.qrInfo=qrInfo;
                            pokerGame.qrId=qrId;
                            processDialog.hide();
                            pokerGame.downloadImage(qrInfo.getLogoImageUrl(),logoImage);
                            buttonLabel.setText("Play");
                            new PrivateContestDialog(contestScreen,qrInfo,pokerStyle).show(contestScreen.stage);
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
        }else {
            new PrivateContestDialog(contestScreen,pokerGame.qrInfo,pokerStyle).show(contestScreen.stage);
        }
    }

    @Override
    public void enterGame(PokerContest contest, GeolocationPosition position) {
        HoldemWarpController holdemWarpController = new HoldemWarpController(pokerGame, position, contest);
        new PokerLoginListener(contestScreen, holdemWarpController, null, PokerConstants.PUBLIC_TABLE){
            @Override
            public void setScreen() {
                pokerGame.setScreen(new HoldemGameScreen(pokerGame,holdemWarpController,contest.getContestId(),null,PokerConstants.PUBLIC_TABLE));
            }
        }.connect();
    }
}
