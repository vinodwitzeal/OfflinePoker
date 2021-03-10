package bigcash.poker.dialogs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.HashMap;

import bigcash.poker.constants.Constant;
import bigcash.poker.font.FontPool;
import bigcash.poker.font.FontType;
import bigcash.poker.models.ReferralLeague;
import bigcash.poker.models.ReferralPlayerDto;
import bigcash.poker.models.UIDialog;
import bigcash.poker.models.UIScreen;
import bigcash.poker.network.ApiHandler;
import bigcash.poker.network.GdxListener;
import bigcash.poker.utils.AssetsLoader;
import bigcash.poker.utils.PokerTimer;
import bigcash.poker.utils.PokerUtils;
import bigcash.poker.utils.TextureDrawable;
import bigcash.poker.widgets.MagicTable;
import bigcash.poker.widgets.MaskLayer;
import bigcash.poker.widgets.MaskedImage;
import bigcash.poker.widgets.PageTable;
import bigcash.poker.widgets.PrizeDrawable;
import bigcash.poker.widgets.StyledLabel;

public class ReferralLeagueDialog extends UIDialog {
    private TextureAtlas uiAtlas;
    private NinePatchDrawable playerBackground;
    private TextureRegion playerRegion;
    private float playerImageSize;
    private float rankSize;
    private Label.LabelStyle rankStyle;
    private Label.LabelStyle nameStyle;
    private Drawable prizeDrawable;
    private StyledLabel.StyledLabelStyle prizeStyle;
    private MaskLayer playerMaskLayer;
    private float prizePad;
    private PageTable pageTable;
    private boolean isPrevious;
    private GdxListener<String> listener;
    private ReferralLeague referralLeague;
    private Button previousButton;
    private Label timerLabel;
    private PokerTimer pokerTimer;
    public ReferralLeagueDialog(UIScreen screen,ReferralLeague referralLeague,GdxListener<String> listener) {
        super(screen);
        this.referralLeague=referralLeague;
        this.listener=listener;
        this.uiAtlas=AssetsLoader.instance().uiAtlas;
        buildDialog();
    }

    @Override
    public void init() {

    }

    @Override
    public void buildDialog() {
        Table contentTable = getContentTable();
        contentTable.top();
        TextureRegionDrawable background= TextureDrawable.getDrawable(AssetsLoader.instance().screenBackground,width,height);
        contentTable.setBackground(background);

        Table headerTable = new Table();
        NinePatchDrawable headerBackground = new NinePatchDrawable(new NinePatch(uiAtlas.findRegion("bg_header_bar"), 1, 1, 1, 6));
        headerTable.setBackground(headerBackground);

        Label.LabelStyle headerLabelStyle = new Label.LabelStyle();
        headerLabelStyle.font = FontPool.obtain(FontType.ROBOTO_BOLD, 7);
        headerLabelStyle.fontColor = Color.WHITE;

        Label.LabelStyle titleStyle = new Label.LabelStyle();
        titleStyle.font = FontPool.obtain(FontType.ROBOTO_BOLD, 9);
        titleStyle.fontColor = Color.WHITE;

        TextureRegion backTexture = uiAtlas.findRegion("btn_wback");
        float backHeight = headerLabelStyle.font.getLineHeight()*1.6f;
        float backWidth = backHeight * backTexture.getRegionWidth() / backTexture.getRegionHeight();
        TextureRegionDrawable menuDrawable = TextureDrawable.getDrawable(backTexture, backWidth, backHeight);
        final Button backButton = new Button(menuDrawable, menuDrawable, menuDrawable);
        headerTable.add(backButton).width(backWidth).height(backHeight).padLeft(8 * density);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (isPrevious){
                    isPrevious=false;
                    pageTable.setOnRefresh();
                }else {
                    hide();
                }
            }
        });


        headerTable.add(new Label("Referral League", titleStyle)).expandX();

        TextureRegion infoButtonTexture=uiAtlas.findRegion("btn_info");
        float infoButtonHeight=titleStyle.font.getLineHeight();
        float infoButtonWidth=infoButtonHeight*infoButtonTexture.getRegionWidth()/infoButtonTexture.getRegionHeight();
        TextureRegionDrawable infoDrawable=TextureDrawable.getDrawable(infoButtonTexture,infoButtonWidth,infoButtonHeight);

        Button infoButton=new Button(infoDrawable);
        infoButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                new ReferralRulesDialog(screen,referralLeague.getPrizeRules()).show(screen.stage);
            }
        });

        headerTable.add(infoButton).width(infoButtonWidth).height(infoButtonHeight).padRight(4*density);

        TextureRegion previousButtonTexture=uiAtlas.findRegion("previous_result");
        float previousButtonHeight=titleStyle.font.getLineHeight();
        float previousButtonWidth=previousButtonHeight*previousButtonTexture.getRegionWidth()/previousButtonTexture.getRegionHeight();

        TextureRegionDrawable previousButtonDrawable=TextureDrawable.getDrawable(previousButtonTexture,previousButtonWidth,previousButtonHeight);
        previousButton=new Button(previousButtonDrawable);

        headerTable.add(previousButton).width(previousButtonWidth).height(previousButtonHeight).padRight(8*density);
        previousButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                isPrevious=true;
                pageTable.setOnRefresh();
                previousButton.setVisible(false);
            }
        });

        float headerHeight=backHeight*2.0f;
        contentTable.add(headerTable).width(width).height(headerHeight).row();

        Image logoImage=new Image();
        screen.pokerGame.downloadImage("https://1101993670.rsc.cdn77.org/img/referral_contest.png",logoImage);
        float logoWidth=width*0.25f;
        float logoHeight=logoWidth*0.68f;
        contentTable.add(logoImage).width(logoWidth).height(logoHeight).padTop(4*density).padBottom(4*density).row();

        StyledLabel.StyledLabelStyle winnerLabelStyle=new StyledLabel.StyledLabelStyle(FontType.ROBOTO_BOLD,6);
        winnerLabelStyle.fontColor=Color.WHITE;


        playerMaskLayer=AssetsLoader.instance().squareMaskLayer;
        playerBackground=new NinePatchDrawable(new NinePatch(uiAtlas.findRegion("private_table_bg"),8,8,8,8));
        playerRegion=uiAtlas.findRegion("contest_profile");
        nameStyle=new Label.LabelStyle();
        nameStyle.font=FontPool.obtain(FontType.ROBOTO_REGULAR,6);
        nameStyle.fontColor= Color.BLACK;
        playerImageSize=nameStyle.font.getLineHeight()*3;
        rankStyle=new Label.LabelStyle();
        rankStyle.font=FontPool.obtain(FontType.ROBOTO_BOLD,3);
        rankStyle.fontColor=Color.WHITE;
        rankStyle.background=new TextureRegionDrawable(uiAtlas.findRegion("bg_rank"));
        rankSize=rankStyle.font.getCapHeight()*3;
        prizeDrawable=new PrizeDrawable(uiAtlas.findRegion("bg_prize"),10,4);
        prizeStyle=new StyledLabel.StyledLabelStyle(FontType.ROBOTO_BOLD,7);
        prizeStyle.fontColor=Color.WHITE;
        prizePad=prizeStyle.font.getCapHeight();

        Label.LabelStyle timerStyle=new Label.LabelStyle();
        timerStyle.font=FontPool.obtain(FontType.ROBOTO_BOLD,8);
        timerStyle.fontColor= Color.WHITE;

        NinePatchDrawable timerBackground=new NinePatchDrawable(new NinePatch(uiAtlas.findRegion("btn_red"),8,8,8,8));
        MagicTable timerTable=new MagicTable(timerBackground);
        float timerPad=timerStyle.font.getLineHeight()*2;
        float timerHeight=timerStyle.font.getLineHeight()*2;

        timerLabel=new Label("-----",timerStyle);
        timerTable.add(timerLabel).padLeft(timerPad).padRight(timerPad).height(timerHeight);


        NinePatchDrawable playerTableBackground=new NinePatchDrawable(new NinePatch(uiAtlas.findRegion("bg_menu"),8,8,20,1));
        Table magicTable=new MagicTable(playerTableBackground);
        magicTable.top();

        pageTable=new PageTable(width*0.16f) {

            @Override
            public void onRefresh() {
                ReferralLeagueDialog.this.onRefresh();
            }
        };
        magicTable.add(pageTable).padTop(timerHeight).expand().fill();

        Stack stack=new Stack();
        Table pageBackTable=new Table();
        pageBackTable.add(magicTable).padTop(timerHeight/2f).expand().fill();
        Table timerBackTable=new Table();
        timerBackTable.top();
        timerBackTable.add(timerTable);
        stack.add(pageBackTable);
        stack.add(timerBackTable);
        contentTable.add(stack).expand().fill();
    }

    private void onRefresh(){
        ApiHandler.callReferralLeaderboardApi(isPrevious, new GdxListener<ReferralLeague>() {
            @Override
            public void onSuccess(ReferralLeague referralLeague) {
                if (!isPrevious){
                    previousButton.setVisible(true);
                }
                if (referralLeague==null){
                    pageTable.onRefreshError("No data available");
                }else {
                    HashMap<Integer, ReferralPlayerDto> playerDtoHashMap=referralLeague.getReferralPlayerDtos();
                    Table contentTable=pageTable.contentTable;
                    if (playerDtoHashMap.size()>3){
                        for (int i=1;i<playerDtoHashMap.size();i++){
                            contentTable.add(new WinnerTable(playerDtoHashMap.get(i))).expandX().fillX().padBottom(4*density).row();
                        }
                    }
                    pageTable.onRefreshCompleted();
                    listener.setSuccess("Success");
                }
            }

            @Override
            public void onFail(String reason) {
                pageTable.onRefreshError(reason);
            }

            @Override
            public void onError(String error) {
                pageTable.onRefreshError(error);
            }
        });
    }


    private void setTimerText(long millisUntilFinished){
        int seconds = (int) (millisUntilFinished / 1000) % 60;
        int minutes = (int) ((millisUntilFinished / (1000 * 60)) % 60);
        int hours = (int) ((millisUntilFinished / (1000 * 60 * 60)) % 24);
        int days = (int) ((millisUntilFinished / (1000 * 60 * 60 * 24)));
        String da = days < 10 ? "0" + days : days + "";
        String hou = hours < 10 ? "0" + hours : hours + "";
        String min = minutes < 10 ? "0" + minutes : minutes + "";
        String sec = seconds < 10 ? "0" + seconds : seconds + "";
        if (days == 0) {
            timerLabel.setText(hou + "h:" + min + "m:" + sec + "s");
        } else {
            timerLabel.setText(da + "d:" + hou + "h:" + min + "m");
        }
    }
    private void startTimer(){
        long leagueTime = Constant.userProfile.getReferralLeagueDetail().getReferralLeaderboardRemainingTime() - TimeUtils.millis() + Constant.userProfile.getReferralLeagueDetail().getLastUpdatedTime();
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                setTimerText(leagueTime);
            }
        });

        pokerTimer=new PokerTimer(leagueTime, 1000, new PokerTimer.PokerTimerUpdater() {
            @Override
            public void onTick(long millis) {
                setTimerText(millis);
            }

            @Override
            public void onFinish() {

            }
        });
        pokerTimer.start();
    }


    @Override
    public Dialog show(Stage stage, Action action) {
        pageTable.setOnRefresh();
        startTimer();
        return super.show(stage, action);
    }





    private class WinnerTable extends Stack {
        public WinnerTable(ReferralPlayerDto playerDto) {
            MagicTable playerTable=new MagicTable(playerBackground,playerDto.isMyDetail()?"00ffff":"ffffff");
            MaskedImage image=new MaskedImage(screen,playerRegion,playerMaskLayer);
            screen.pokerGame.downloadImage(playerDto.getUserImageUrl(),image);
            playerTable.add(image).width(playerImageSize).height(playerImageSize).padRight(prizePad);
            Label nameLabel=new Label(playerDto.getUserName(),nameStyle);
            playerTable.add(nameLabel).expandX().align(Align.left);
            Table prizeTable=new Table();
            prizeTable.padLeft(prizePad).padRight(prizePad);
            prizeTable.setBackground(prizeDrawable);
            StyledLabel prizeLabel=new StyledLabel("\u20b9 "+PokerUtils.formatValue(playerDto.getWinningAmount()),prizeStyle);
            prizeLabel.shadow(0.5f,0.5f,Color.DARK_GRAY);
            prizeTable.add(prizeLabel);
            playerTable.add(prizeTable).expandY().fillY();

            Table rankTable=new Table();
            rankTable.top().left();
            Label rankLabel=new Label(playerDto.getUserRank()+"",rankStyle);
            rankLabel.setAlignment(Align.center);
            rankTable.add(rankLabel).width(rankSize).height(rankSize);

            Table playerBackTable=new Table();
            playerBackTable.add(playerTable).expandX().fillX().padLeft(rankSize/2f).padTop(rankSize/2f);

            add(playerBackTable);
            add(rankTable);
        }
    }
}
