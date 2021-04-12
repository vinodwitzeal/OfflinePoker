package bigcash.poker.game.poker.omaha;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

import java.util.ArrayList;
import java.util.List;

import bigcash.poker.font.FontPool;
import bigcash.poker.font.FontType;
import bigcash.poker.game.poker.omaha.messages.OmahaResultMessage;
import bigcash.poker.game.poker.omaha.widgets.OmahaCard;
import bigcash.poker.models.UIDialog;
import bigcash.poker.utils.PokerUtils;
import bigcash.poker.widgets.MaskLayer;
import bigcash.poker.widgets.StyledLabel;

public class OmahaResultDialog extends UIDialog {
    private OmahaGameScreen gameScreen;
    private OmahaWorld omahaWorld;
    private TextureAtlas gameAtlas;
    private OmahaResultMessage resultMessage;
    private float dialogWidth,dataWidth;
    private PlayerDetailStyle playerDetailStyle;
    private float nameWidth,combinationWidth,resultWidth,separatorWidth,separatorHeight;

    public OmahaResultDialog(OmahaGameScreen gameScreen, OmahaResultMessage resultMessage) {
        super(gameScreen);
        this.gameAtlas=gameScreen.atlas;
        this.gameScreen=gameScreen;
        this.omahaWorld=gameScreen.omahaWorld;
        this.resultMessage=resultMessage;
        dismissOnBack(true);
        buildDialog();
    }

    @Override
    public void init() {


    }

    @Override
    public void buildDialog() {
        Table contentTable = getContentTable();
        Table dataTable = buildDataTable();
        dialogWidth = width * 0.9f;

        dataWidth=dialogWidth*0.95f;
        nameWidth=dataWidth*0.27f;
        combinationWidth=dataWidth*0.41f;
        resultWidth=dataWidth*0.27f;
        TextureRegion titleTexture = gameAtlas.findRegion("img_score");
        float titleWidth=dataWidth*0.35f;
        float titleHeight = titleWidth * titleTexture.getRegionHeight() / titleTexture.getRegionWidth();
        dataTable.add(new Image(titleTexture)).width(titleWidth).height(titleHeight).padBottom(titleHeight*0.1f).row();
        Table headerTable = new Table();
        NinePatchDrawable headerBackground = new NinePatchDrawable(new NinePatch(gameAtlas.findRegion("bg_header"), 17, 17, 17, 17));
        headerTable.setBackground(headerBackground);


        Label.LabelStyle headerStyle = new Label.LabelStyle();
        headerStyle.font = FontPool.obtain(FontType.ROBOTO_BOLD, 6);
        headerStyle.fontColor = Color.WHITE;
        separatorWidth=dataWidth*0.003f;
        separatorHeight=headerStyle.font.getLineHeight();
        NinePatchDrawable lineDrawable = new NinePatchDrawable(new NinePatch(gameAtlas.findRegion("seperate_line"), 1, 1, 1, 1));
        Label hNameLabel = new Label("PLAYER NAME", headerStyle);
        hNameLabel.setAlignment(Align.center);
        Label hCardsLabel = new Label("BEST COMBINATION", headerStyle);
        hCardsLabel.setAlignment(Align.center);
        Label hResultLabel = new Label("RESULT", headerStyle);
        hResultLabel.setAlignment(Align.center);

        headerTable.add(hNameLabel).width(nameWidth);
        headerTable.add(new Image(lineDrawable)).width(separatorWidth).height(separatorHeight);

        headerTable.add(hCardsLabel).width(combinationWidth);
        headerTable.add(new Image(lineDrawable)).width(separatorWidth).height(separatorHeight);

        headerTable.add(hResultLabel).width(resultWidth);
        dataTable.add(headerTable).height(2*separatorHeight).width(dataWidth).uniformX().fillX().row();

        Label.LabelStyle poolStyle=new Label.LabelStyle();
        poolStyle.font=FontPool.obtain(FontType.ROBOTO_BOLD,6);
        poolStyle.fontColor= Color.WHITE;
        poolStyle.background=new NinePatchDrawable(new NinePatch(gameAtlas.findRegion("bg_winning"),1,1,1,1));
        playerDetailStyle=new PlayerDetailStyle();

        Table winningTable=new Table();

        for (OmahaResultMessage.PokerWinning winning:resultMessage.winnings){
            int totalPlayers=winning.players.size();
            Label poolWinning=new Label("PRICE POOL : \u20b9 "+ PokerUtils.getValue(winning.amount),poolStyle);
            poolWinning.setAlignment(Align.center);
            winningTable.add(poolWinning).width(dataWidth).row();
            for (OmahaResultMessage.PokerPoolPlayer pokerPoolPlayer:winning.players){
                winningTable.add(new PlayerTable(pokerPoolPlayer,winning.allFolded)).width(dataWidth).uniformY().fillY().padBottom(2).row();
            }
        }

        ScrollPane scrollPane=new ScrollPane(winningTable);
        scrollPane.setScrollingDisabled(true,false);
        dataTable.add(scrollPane).width(dataWidth).row();

        float topPad = 0;
        Table backTable = new Table();
        float closeSize = height * 0.1f;
        float sidePad = closeSize * 0.4f;
        backTable.add(dataTable).pad(titleHeight * 0.3f, sidePad, 0, sidePad);


        Table closeTable = new Table();
        closeTable.top().right();
        TextureRegion closeTexture = gameAtlas.findRegion("btn_cross");
        TextureRegionDrawable closeDrawable = new TextureRegionDrawable(closeTexture);
        closeDrawable.setMinSize(closeSize, closeSize);
        ImageButton closeButton = new ImageButton(closeDrawable, closeDrawable, closeDrawable);
        closeTable.add(closeButton).width(closeSize).height(closeSize);
        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hide();
            }
        });
        Stack stack = new Stack();
        stack.add(backTable);
        stack.add(closeTable);
        contentTable.add(stack);

    }

    public Table buildDataTable() {
        Table dataTable = new Table();
        NinePatch bgNinePatch = new NinePatch(gameAtlas.findRegion("bg_dialog"), 30, 30, 30, 30);
        NinePatchDrawable background = new NinePatchDrawable(bgNinePatch);
        background.setMinWidth(60);
        background.setMinHeight(60);
        dataTable.setBackground(background);
        return dataTable;
    }

    private class PlayerTable extends Table {
        public PlayerTable(OmahaResultMessage.PokerPoolPlayer pokerPoolPlayer, boolean allFolded){
            Table imageBackTable=new Table();
            imageBackTable.setBackground(playerDetailStyle.frameBackground);
            PokerPlayerImage playerImage=new PokerPlayerImage(playerDetailStyle.userRegion,playerDetailStyle.crownRegion,omahaWorld.maskLayer);
            gameScreen.pokerGame.downloadImage(pokerPoolPlayer.pokerResultPlayer.imageUrl,playerImage);
            float nameLabelWidth=nameWidth-playerDetailStyle.imageSize-playerImage.widthOffset;
            Label nameLabel=new Label(pokerPoolPlayer.pokerResultPlayer.name,playerDetailStyle.nameStyle);
            nameLabel.setAlignment(Align.left);
            nameLabel.setEllipsis(true);
            nameLabel.setEllipsis("");
            imageBackTable.add(playerImage).width(playerDetailStyle.imageSize).height(playerDetailStyle.imageSize).pad(playerDetailStyle.framePad);

            Table playerImageTable=new Table();
            playerImageTable.add(imageBackTable);
            add(playerImageTable).padTop(playerImage.heightOffset).padRight(playerImage.widthOffset);
            add(nameLabel).width(nameLabelWidth).padTop(playerImage.heightOffset).padRight(separatorWidth);
            Table combinationTable=new Table();
            PlayerCards playerCards=new PlayerCards(pokerPoolPlayer.pokerResultPlayer.cards);
            combinationTable.add(playerCards).width(playerCards.width).height(playerCards.height).row();


            CombinationLabel combinationLabel=new CombinationLabel("",playerDetailStyle.strengthStyle,playerDetailStyle.combinationBackground);
            combinationLabel.setAlignment(Align.center);

            if (pokerPoolPlayer.winState==1){
                if (allFolded){
                    combinationLabel.setText("All Players Folded");
                }else{
                    combinationLabel.setText(omahaWorld.cardRankingMap.get(pokerPoolPlayer.pokerResultPlayer.groupType));
                }
                combinationLabel.setBackgroundColor(Color.valueOf("055601"));
            }else if (pokerPoolPlayer.winState==0){
                if (allFolded){
                    combinationLabel.setText("All Players Folded");
                }else{
                    combinationLabel.setText(omahaWorld.cardRankingMap.get(pokerPoolPlayer.pokerResultPlayer.groupType));
                }
                combinationLabel.setBackgroundColor(Color.valueOf("41010d"));
            }else {
                combinationLabel.setText("FOLD");
                combinationLabel.setBackgroundColor(Color.valueOf("452e2f"));
            }
            combinationTable.add(combinationLabel).width(playerCards.width);

            add(combinationTable).width(combinationWidth).padRight(separatorWidth).padTop(playerDetailStyle.framePad).padBottom(playerDetailStyle.framePad);


            Table resultTable=new Table();
            if (pokerPoolPlayer.winState==1){
                playerImage.winner=true;
                setBackground(playerDetailStyle.winnerDrawable);
                resultTable.add(new Image(playerDetailStyle.wonTexture)).width(playerDetailStyle.winWidth).height(playerDetailStyle.imageHeight).row();
                StyledLabel amountLabel=new StyledLabel("\u20b9 "+PokerUtils.getValue(Math.abs(pokerPoolPlayer.winning)),playerDetailStyle.amountStyle);
                amountLabel.shadow(-0.5f,0.5f, Color.BLACK);
                resultTable.add(amountLabel);
            }else if (pokerPoolPlayer.winState==0){
                setBackground(playerDetailStyle.loseDrawable);
                resultTable.add(new Image(playerDetailStyle.loseTexture)).width(playerDetailStyle.loseWidth).height(playerDetailStyle.imageHeight).row();
                StyledLabel amountLabel=new StyledLabel("-\u20b9 "+PokerUtils.getValue(Math.abs(pokerPoolPlayer.winning)),playerDetailStyle.amountStyle);
                amountLabel.shadow(-0.5f,0.5f, Color.BLACK);
                resultTable.add(amountLabel);
            }else {
                setBackground(playerDetailStyle.foldDrawable);
                resultTable.add(new Image(playerDetailStyle.foldTexture)).width(playerDetailStyle.foldWidth).height(playerDetailStyle.imageHeight).row();
                StyledLabel amountLabel=new StyledLabel("-\u20b9 "+PokerUtils.getValue(Math.abs(pokerPoolPlayer.winning)),playerDetailStyle.amountStyle);
                amountLabel.shadow(-0.5f,0.5f, Color.BLACK);
                resultTable.add(amountLabel);
            }
            add(resultTable).width(resultWidth);
        }
    }


    private class PokerPlayerImage extends Image {
        private MaskLayer maskLayer;
        private boolean winner;
        private TextureRegion crown;
        private float widthOffset,heightOffset,crownWidth,crownHeight;
        private Vector2 localPosition;
        public PokerPlayerImage(TextureRegion region, TextureRegion crown, MaskLayer maskLayer){
            super(region);
            this.crown=crown;
            this.localPosition=new Vector2();
            this.maskLayer=maskLayer;
            this.crownWidth=playerDetailStyle.imageSize*1.5f;
            this.crownHeight=crownWidth*crown.getRegionHeight()/crown.getRegionWidth();
            this.widthOffset =(crownWidth-playerDetailStyle.imageSize)/2f;
            this.heightOffset =crownHeight*0.13f;
        }



        @Override
        public void draw(Batch batch, float parentAlpha) {
            validate();
            localPosition.set(0,0);
            localPosition=gameScreen.getMaskPosition(this,localPosition);
            maskLayer.setPosition(localPosition.x,localPosition.y);
            maskLayer.setSize(getWidth(),getHeight());
            maskLayer.begin(batch,getStage().getWidth(),getStage().getHeight());
            super.draw(batch, parentAlpha);
            maskLayer.end(batch);
            if (winner){
                batch.draw(crown, getX() - widthOffset, getY() - heightOffset,crownWidth,crownHeight);
            }
        }
    }


    private class PlayerCards extends Widget {
        private List<OmahaCard> omahaCards;
        public float width,height,cardPad,cardBottomPad;
        public PlayerCards(int[] cards){
            omahaCards =new ArrayList<OmahaCard>();
            if (cards==null || cards.length==0){
                OmahaCard omahaCard =new OmahaCard(omahaWorld,-1,-1);
                omahaCard.setSize(playerDetailStyle.cardWidth,playerDetailStyle.cardHeight);
                omahaCards.add(omahaCard);
                omahaCard =new OmahaCard(omahaWorld,-1,-1);
                omahaCard.setSize(playerDetailStyle.cardWidth,playerDetailStyle.cardHeight);
                omahaCards.add(omahaCard);
                omahaCard =new OmahaCard(omahaWorld,-1,-1);
                omahaCard.setSize(playerDetailStyle.cardWidth,playerDetailStyle.cardHeight);
                omahaCards.add(omahaCard);
                omahaCard =new OmahaCard(omahaWorld,-1,-1);
                omahaCard.setSize(playerDetailStyle.cardWidth,playerDetailStyle.cardHeight);
                omahaCards.add(omahaCard);
                omahaCard =new OmahaCard(omahaWorld,-1,-1);
                omahaCard.setSize(playerDetailStyle.cardWidth,playerDetailStyle.cardHeight);
                omahaCards.add(omahaCard);
            }else {
                for (int i=0;i<cards.length;i++) {
                    OmahaCard omahaCard =new OmahaCard(omahaWorld, cards[i], 1);
                    omahaCard.setSize(playerDetailStyle.cardWidth,playerDetailStyle.cardHeight);
                    omahaCards.add(omahaCard);
                }
            }

            cardPad=playerDetailStyle.cardWidth*0.8f;
            width=playerDetailStyle.cardWidth+4*cardPad;
            height=playerDetailStyle.cardHeight*0.9f;
            cardBottomPad=playerDetailStyle.cardHeight*0.1f;
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            validate();
            Color color=getColor();
            batch.setColor(color.r,color.g,color.b,color.a*parentAlpha);
            float totalWidth=cardPad*(omahaCards.size()-1)+playerDetailStyle.cardWidth;
            float startX=getX()+(getWidth()-totalWidth)/2f;
            float cardY=getY()-cardBottomPad;
            for (int i = 0; i< omahaCards.size(); i++){
                OmahaCard omahaCard = omahaCards.get(i);
                omahaCard.setPosition(startX,cardY);
                startX=startX+cardPad;
            }
            for (OmahaCard omahaCard : omahaCards){
                omahaCard.draw(batch,parentAlpha);
            }
        }
    }

    private class CombinationLabel extends Label {
        private Color backgroundColor;
        private NinePatchDrawable background;
        public CombinationLabel(CharSequence text, LabelStyle style, NinePatchDrawable background) {
            super(text, style);
            this.background=background;
        }

        public void setBackgroundColor(Color backgroundColor){
            this.backgroundColor=backgroundColor;
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            validate();
            batch.setColor(backgroundColor.r,backgroundColor.g,backgroundColor.b,backgroundColor.a*parentAlpha);
            this.background.draw(batch,getX(),getY(),getWidth(),getHeight());
            super.draw(batch, parentAlpha);
        }
    }

    private class PlayerDetailStyle{
        public StyledLabel.StyledLabelStyle nameStyle,strengthStyle,amountStyle;
        public TextureRegion wonTexture,loseTexture,foldTexture,userRegion,crownRegion;
        public float imageHeight,winWidth,loseWidth,foldWidth;
        public NinePatchDrawable winnerDrawable,foldDrawable,loseDrawable,combinationBackground;
        public TextureRegionDrawable frameBackground;
        public float framePad;
        public float cardWidth,cardHeight,height,imageSize;

        public PlayerDetailStyle(){
            nameStyle=new StyledLabel.StyledLabelStyle(FontType.ROBOTO_BOLD,6);
            nameStyle.fontColor= Color.WHITE;
            strengthStyle=new StyledLabel.StyledLabelStyle(FontType.ROBOTO_BOLD,5f);
            strengthStyle.fontColor= Color.WHITE;
            amountStyle=new StyledLabel.StyledLabelStyle(FontType.ROBOTO_BOLD,6);
            amountStyle.fontColor= Color.WHITE;

            cardWidth=dataWidth*0.04f;
            cardHeight=cardWidth*omahaWorld.backCardRegion.getRegionHeight()/omahaWorld.backCardRegion.getRegionWidth();


            wonTexture=gameAtlas.findRegion("img_won");
            loseTexture=gameAtlas.findRegion("img_lost");
            foldTexture=gameAtlas.findRegion("img_fold");
            userRegion=gameAtlas.findRegion("img_user");
            crownRegion=gameAtlas.findRegion("img_crown");
            imageHeight=cardHeight*0.5f;
            winWidth=imageHeight*wonTexture.getRegionWidth()/wonTexture.getRegionHeight();
            loseWidth=imageHeight*loseTexture.getRegionWidth()/loseTexture.getRegionHeight();
            foldWidth=imageHeight*foldTexture.getRegionWidth()/foldTexture.getRegionHeight();

            winnerDrawable=new NinePatchDrawable(new NinePatch(gameAtlas.findRegion("bg_won"),2,2,2,2));
            winnerDrawable.setMinSize(4,4);
            loseDrawable=new NinePatchDrawable(new NinePatch(gameAtlas.findRegion("bg_lost"),2,2,2,2));
            loseDrawable.setMinSize(4,4);
            foldDrawable=new NinePatchDrawable(new NinePatch(gameAtlas.findRegion("bg_fold"),2,2,2,2));
            foldDrawable.setMinSize(4,4);
            combinationBackground=new NinePatchDrawable(new NinePatch(gameAtlas.findRegion("bg_scombination"),6,6,6,6));
            combinationBackground.setMinSize(12,12);
            height=cardHeight*1.5f;
            imageSize=cardHeight*0.8f;

            frameBackground=new TextureRegionDrawable(gameAtlas.findRegion("frame_user"));
            framePad=imageSize*0.1f;
            frameBackground.setMinSize(imageSize+2*framePad,imageSize+2*framePad);
        }
    }
}