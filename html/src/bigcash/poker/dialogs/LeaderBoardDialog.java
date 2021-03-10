package bigcash.poker.dialogs;

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

import java.util.HashMap;

import bigcash.poker.font.FontPool;
import bigcash.poker.font.FontType;
import bigcash.poker.models.CasualLeague;
import bigcash.poker.models.CasualPlayerDto;
import bigcash.poker.models.UIDialog;
import bigcash.poker.network.ApiHandler;
import bigcash.poker.network.GdxListener;
import bigcash.poker.screens.PokerContestScreen;
import bigcash.poker.utils.AssetsLoader;
import bigcash.poker.utils.PokerUtils;
import bigcash.poker.utils.TextureDrawable;
import bigcash.poker.widgets.MagicTable;
import bigcash.poker.widgets.MaskLayer;
import bigcash.poker.widgets.MaskedImage;
import bigcash.poker.widgets.PageTable;
import bigcash.poker.widgets.PrizeDrawable;
import bigcash.poker.widgets.StyledLabel;

public class LeaderBoardDialog extends UIDialog {
    public PokerContestScreen contestScreen;
    private TextureAtlas uiAtlas;
    private StyledLabel.StyledLabelStyle topWinnerNameStyle,topWinnerAmountStyle;
    private StyledLabel totalWinningLabel;
    private TextureRegion userRegion;
    private Drawable circleBackground;
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
    private TopWinnerPlayer firstPlayer,secondPlayer,thirdPlayer;
    private PageTable pageTable;
    public LeaderBoardDialog(PokerContestScreen contestScreen) {
        super(contestScreen);
        this.contestScreen=contestScreen;
        this.uiAtlas=contestScreen.uiAtlas;
        buildDialog();
    }

    @Override
    public void init() {

    }

    @Override
    public void buildDialog() {
        Table contentTable = getContentTable();
        contentTable.top();
        TextureRegionDrawable background=TextureDrawable.getDrawable(AssetsLoader.instance().screenBackground,width,height);
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
                hide();
            }
        });

        userRegion=AssetsLoader.instance().gameAtlas.findRegion("img_user");
        topWinnerNameStyle=new StyledLabel.StyledLabelStyle(FontType.ROBOTO_BOLD,7);
        topWinnerNameStyle.fontColor=Color.WHITE;

        topWinnerAmountStyle=new StyledLabel.StyledLabelStyle(FontType.ROBOTO_BOLD,7);
        topWinnerAmountStyle.fontColor=Color.GOLD;

        circleBackground=new TextureRegionDrawable(AssetsLoader.instance().gameAtlas.findRegion("circle"));

        headerTable.add(new Label("Poker", titleStyle)).expandX();
        headerTable.add().width(backWidth).padRight(8*density);
        float headerHeight=backHeight*2.0f;
        contentTable.add(headerTable).width(width).height(headerHeight).row();

        StyledLabel.StyledLabelStyle winnerLabelStyle=new StyledLabel.StyledLabelStyle(FontType.ROBOTO_BOLD,6);
        winnerLabelStyle.fontColor=Color.WHITE;

        StyledLabel winnerLabel=new StyledLabel("Players have won today",winnerLabelStyle);
        winnerLabel.shadow(0.5f,0.5f,Color.BLACK);
        contentTable.add(winnerLabel).padTop(4*density).row();
        totalWinningLabel=new StyledLabel("\u20b9 0",topWinnerAmountStyle);
        totalWinningLabel.shadow(0.5f,0.5f, Color.BLACK);
        contentTable.add(totalWinningLabel).padBottom(8*density).row();

        Table topWinnerTable=new Table();

        float firstWinnerSize=width*0.25f;
        float nameSize=width*0.3f;
        float otherWinnerSize=width*0.2f;

        firstPlayer=new TopWinnerPlayer(uiAtlas.findRegion("badge_first"),firstWinnerSize,nameSize);
        secondPlayer=new TopWinnerPlayer(uiAtlas.findRegion("badge_second"),otherWinnerSize,nameSize);
        thirdPlayer=new TopWinnerPlayer(uiAtlas.findRegion("badge_third"),otherWinnerSize,nameSize);

        topWinnerTable.add(secondPlayer).expand().align(Align.bottom);
        topWinnerTable.add(firstPlayer).expand().align(Align.bottom);
        topWinnerTable.add(thirdPlayer).expand().align(Align.bottom);
        contentTable.add(topWinnerTable).padBottom(8*density).width(width).row();

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

        NinePatchDrawable playerTableBackground=new NinePatchDrawable(new NinePatch(uiAtlas.findRegion("bg_menu"),8,8,20,1));
        Table magicTable=new MagicTable(playerTableBackground);
        magicTable.top();
        contentTable.add(magicTable).expand().fill();

        pageTable=new PageTable(width*0.16f) {

            @Override
            public void setOnRefresh() {
                firstPlayer.clearData();
                secondPlayer.clearData();
                thirdPlayer.clearData();
                super.setOnRefresh();
            }

            @Override
            public void onRefresh() {
                LeaderBoardDialog.this.onRefresh();
            }
        };
        magicTable.add(pageTable).padTop(20).expand().fill();
    }

    private void onRefresh(){
        ApiHandler.callCasualLeaderBoardApi(new GdxListener<CasualLeague>() {
            @Override
            public void onSuccess(CasualLeague casualLeague) {
                if (casualLeague==null){
                    pageTable.onRefreshError("No data available");
                }else {
                    contestScreen.setWinnings(casualLeague.getTotalWinningAmount());
                    totalWinningLabel.setText("\u20b9 "+ PokerUtils.formatValue(casualLeague.getTotalWinningAmount()));
                    HashMap<Integer,CasualPlayerDto> playerDtoHashMap=casualLeague.getCasualPlayerDtos();
                    CasualPlayerDto firstPlayerData=playerDtoHashMap.get(1);
                    CasualPlayerDto secondPlayerData=playerDtoHashMap.get(2);
                    CasualPlayerDto thirdPlayerData=playerDtoHashMap.get(3);
                    firstPlayer.setData(firstPlayerData);
                    secondPlayer.setData(secondPlayerData);
                    thirdPlayer.setData(thirdPlayerData);
                    Table contentTable=pageTable.contentTable;
                    if (playerDtoHashMap.size()>3){
                        for (int i=4;i<playerDtoHashMap.size();i++){
                            contentTable.add(new WinnerTable(playerDtoHashMap.get(i))).expandX().fillX().padBottom(4*density).row();
                        }
                    }
                    pageTable.onRefreshCompleted();
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


    @Override
    public Dialog show(Stage stage, Action action) {
        pageTable.setOnRefresh();
        return super.show(stage, action);
    }

    private class TopWinnerPlayer extends Table{
        private MaskedImage image;
        private StyledLabel nameLabel,winningLabel;
        public TopWinnerPlayer(TextureRegion badge,float imageSize,float nameSize){

            Stack stack=new Stack();
            Table imageBackTable=new Table();
            Table imageTable=new Table();
            imageTable.setBackground(circleBackground);
            imageBackTable.add(imageTable).width(imageSize).height(imageSize);
            image=new MaskedImage(screen,userRegion,AssetsLoader.instance().circleMaskLayer);
            imageTable.add(image).expand().fill().pad(2);
            float rankWidth=nameSize*0.2f;
            float rankHeight=rankWidth*badge.getRegionHeight()/badge.getRegionWidth();
            Table rankTable=new Table();
            rankTable.top().left();
            rankTable.add(new Image(badge)).width(rankWidth).height(rankHeight);
            stack.add(imageBackTable);
            stack.add(rankTable);

            add(stack).row();

            nameLabel=new StyledLabel("",topWinnerNameStyle);
            nameLabel.shadow(0.5f,0.5f,Color.BLACK);
            nameLabel.setAlignment(Align.center);
            nameLabel.setEllipsis("");
            add(nameLabel).width(nameSize).row();

            winningLabel=new StyledLabel("\u20b9 0",topWinnerAmountStyle);
            winningLabel.setAlignment(Align.center);
            winningLabel.shadow(0.5f,0.5f,Color.BLACK);
            add(winningLabel).width(nameSize);
        }

        public void clearData(){
            image.setDrawable(new TextureRegionDrawable(userRegion));
            nameLabel.setText("--");
            winningLabel.setText("--");
        }

        public void setData(CasualPlayerDto playerDto){
            screen.pokerGame.downloadImage(playerDto.getUserImageUrl(),image);
            nameLabel.setText(playerDto.getUserName());
            winningLabel.setText("\u20b9 "+ PokerUtils.formatValue(playerDto.getWinningAmount()));
        }
    }




    private class WinnerTable extends Stack {
        public WinnerTable(CasualPlayerDto playerDto) {
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
