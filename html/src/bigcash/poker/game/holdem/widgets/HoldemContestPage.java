package bigcash.poker.game.holdem.widgets;

import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

import bigcash.poker.dialogs.PrivateContestDialog;
import bigcash.poker.dialogs.ProcessDialog;
import bigcash.poker.game.PokerContestPage;
import bigcash.poker.game.PokerLoginListener;
import bigcash.poker.game.holdem.HoldemGameScreen;
import bigcash.poker.game.holdem.controllers.HoldemWarpController;
import bigcash.poker.models.PokerContest;
import bigcash.poker.models.QrInfo;
import bigcash.poker.network.GdxListener;
import bigcash.poker.network.PokerApi;
import bigcash.poker.qr.QrScannerDialog;
import bigcash.poker.screens.PokerContestScreen;
import bigcash.poker.utils.GeolocationPosition;
import bigcash.poker.utils.PokerConstants;
import bigcash.poker.utils.TextureDrawable;
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
        contentTable.add(getPrivateContestTable(pokerGame.qrInfo)).expandX().fillX().padBottom(density*4).row();
        if (max5Contests!=null && max5Contests.size>0) {
            for (int i = 0; i < max5Contests.size; i++) {
                contentTable.add(getContestTable(max5Contests.get(i))).row();
            }
        }
        if (max2Contests!=null && max2Contests.size>0) {
            contentTable.add().padTop(density * 10f).row();
            for (int i = 0; i < max2Contests.size; i++) {
                contentTable.add(getContestTable(max2Contests.get(i))).row();
            }
        }
    }


    public Table getPrivateContestTable(QrInfo qrInfo) {
        final Table stackTable = new Table();
        Table frontTable = new Table();
        TextureRegion qrBackground=contestScreen.uiAtlas.findRegion("private_contest_bg");
        float tableHeight=contestStyle.tableWidth*qrBackground.getRegionHeight()/qrBackground.getRegionWidth();
        TextureRegionDrawable background= TextureDrawable.getDrawable(qrBackground,contestStyle.tableWidth,tableHeight);

        float downButtonHeight = contestStyle.bigLabelStyle.font.getCapHeight();
        float downButtonWidth = downButtonHeight * contestStyle.downDrawable.getMinWidth() / contestStyle.downDrawable.getMinHeight();
        frontTable.setBackground(background);
        frontTable.pad(0, downButtonHeight*4.8f,0, downButtonHeight*2);

        tableLogoImage = new Image();
        float logoImageSize=tableHeight*0.6f;
        if (qrInfo!=null) {
            String setTableLogoUrl = qrInfo.getLogoImageUrl();
            pokerGame.downloadImage(setTableLogoUrl, tableLogoImage);
        }
        frontTable.add(tableLogoImage).width(logoImageSize).height(logoImageSize).padTop(7*density).padRight(4*density).expandX();

        Table buttonTable = new Table();
        NinePatchDrawable redButton = new NinePatchDrawable(new NinePatch(contestScreen.uiAtlas.findRegion("scan_btn_bg"), 8, 8, 8, 8));
        buttonTable.setBackground(redButton);
        float sidePad=4*density;
        float bottomPad=6*density;
        buttonTable.pad(sidePad,sidePad,bottomPad,sidePad);

        TextureRegion qrTexture =contestScreen.uiAtlas.findRegion("ic_qr_code");
        float qrHeight=contestStyle.qrLabelStyle.font.getLineHeight();
        float qrWidth=qrHeight*qrTexture.getRegionWidth()/qrTexture.getRegionHeight();
        buttonTable.add(new Image(qrTexture)).height(qrHeight).width(qrWidth);
        buttonLabel=new Label("",contestStyle.qrLabelStyle);
        if (qrInfo==null){
            buttonLabel.setText("Scan");

        }else {
            buttonLabel.setText("Play");
        }

        buttonTable.add(buttonLabel).padLeft(6 * density);

        buttonTable.setTouchable(Touchable.enabled);
        buttonTable.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                onPrivateButtonClicked(tableLogoImage,buttonLabel);
            }
        });


        frontTable.add(buttonTable).width(buttonWidth * 0.7f).height(height *.05f).align(Align.right);


        float titleHeight = downButtonHeight * 4;
        float titleWidth = titleHeight * 1.32f;

        Table titleTable = new Table();
        titleTable.top().left();

        Image titleImage = new Image();
//        if (contest.getImageUrl() != null) {
        String setimageUrl = "https://1101993670.rsc.cdn77.org/img/private_table_logo.png";
        pokerGame.downloadImage(setimageUrl, titleImage);
//
        titleTable.add(titleImage).width(titleWidth).height(titleHeight).padTop(titleHeight * 0.25f);

        Table backTable = new Table();
        backTable.add(frontTable).width(contestStyle.tableWidth).height(tableHeight).pad(titleHeight*0.5f, downButtonHeight, 0, 0);

        Stack stack = new Stack();
        stack.add(backTable);
        stack.add(titleTable);

        stackTable.add(stack).expand().fill().pad(4 * density);
        return stackTable;
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
                            new PrivateContestDialog(contestScreen,qrInfo,contestStyle).show(contestScreen.stage);
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
            new PrivateContestDialog(contestScreen,pokerGame.qrInfo,contestStyle).show(contestScreen.stage);
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
