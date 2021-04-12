package bigcash.poker.game.poker.omaha.widgets;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import java.util.ArrayList;

import bigcash.poker.constants.Constant;
import bigcash.poker.font.FontPool;
import bigcash.poker.font.FontType;
import bigcash.poker.game.poker.omaha.OmahaWorld;

public class OmahaUserPlayer extends OmahaPlayer {
    public Table combinationTable;
    public Label combinationTypeLabel;
    public CombinationCards combinationCards;
    public OmahaUserPlayer(OmahaWorld omahaWorld, PokerPlayerStyle playerStyle) {
        super(omahaWorld, playerStyle,false);
        combinationTable=new Table(){
            @Override
            public void setColor(Color color) {
                super.setColor(color);
                for (Actor actor:getChildren()){
                    actor.setColor(color);
                }
            }
        };
        combinationCards=new CombinationCards();
        Label.LabelStyle combinationTypeStyle = new Label.LabelStyle();
        combinationTypeStyle.font = FontPool.obtain(FontType.ROBOTO_BOLD, 4);
        combinationTypeStyle.fontColor = Color.WHITE;
        combinationTypeLabel=new Label("",combinationTypeStyle);
        combinationTable.setBackground(playerStyle.detailsBackground);
        combinationTable.setVisible(false);
        emptySeat();
    }

    @Override
    public void emptySeat() {
        setX(omahaWorld.screen.width*0.466f);
        combinationTable.setVisible(false);
        combinationTypeLabel.setText("");
        super.emptySeat();
    }

    @Override
    public void addActors() {
        combinationTable.pad(0);
        combinationTable.padRight(playerStyle.imageSize/2f);
        combinationTable.setSize(playerStyle.detailsWidth,playerStyle.detailsHeight);
        combinationTable.setPosition(-combinationTable.getWidth(),detailsTable.getY());
        combinationTable.setVisible(false);
        addActor(combinationTable);
        super.addActors();
    }

    @Override
    public void resetAll() {
        super.resetAll();
        removeCombinations();
    }

    public void updateCombinations(ArrayList<Integer> cards, String name){
        setX(omahaWorld.screen.width/2f);
        combinationTable.clear();
        combinationCards.clear();
        combinationCards.updateCombinations(cards);
        combinationTypeLabel.setText(name);
        combinationTable.setVisible(true);
        combinationTable.add(combinationCards).width(combinationCards.totalWidth).height(combinationCards.cardHeight).row();
        combinationTable.add(combinationTypeLabel);
    }

    @Override
    public void minusBalanceAmount(float amount) {
        super.minusBalanceAmount(amount);
        Constant.userProfile.setPokerBalance(balanceAmount);
    }

    @Override
    public void setBalanceAmount(float balanceAmount) {
        super.setBalanceAmount(balanceAmount);
        Constant.userProfile.setPokerBalance(super.balanceAmount);
    }

    @Override
    public void addBalanceAmount(float amount) {
        super.addBalanceAmount(amount);
        Constant.userProfile.setPokerBalance(balanceAmount);
    }

    public void removeCombinations(){
        combinationCards.clear();
        setX(omahaWorld.screen.width*0.466f);
        combinationTypeLabel.setText("");
        combinationTable.setVisible(false);
    }

    @Override
    public void startTimer(Color timerColor) {
        super.startTimer(timerColor);
        omahaWorld.screen.vibrate(500);
    }

    @Override
    public boolean resumeTimer(long remainingTime, boolean lifeUsed) {
        if (super.resumeTimer(remainingTime,lifeUsed)){
            omahaWorld.screen.vibrate(200);
            return true;
        }
        return false;
    }

    private class CombinationCards extends Group {
        public float cardYOffset;
        public float cardWidth,cardHeight;
        public float cardPad;
        public float totalWidth;
        public CombinationCards(){
            setTransform(false);
            cardWidth=playerStyle.cardWidth*0.5f;
            cardHeight=playerStyle.cardHeight*0.5f;
            cardYOffset=cardHeight*0.2f;
            cardPad=cardWidth*0.55f;
            totalWidth=cardPad*4+cardWidth;
            setSize(totalWidth,cardHeight);
        }



        @Override
        public void setColor(Color color) {
            super.setColor(color);
            for (Actor actor:getChildren()){
                actor.setColor(color);
            }
        }

        public void updateCombinations(ArrayList<Integer> cards){
            clearChildren();
            if (cards==null || cards.size()!=5)return;
            float startX=(getWidth()-totalWidth)/2f;
            for (int i=0;i<5;i++){
                OmahaCard omahaCard =new OmahaCard(omahaWorld,cards.get(i),1);
                omahaCard.setSize(cardWidth,cardHeight);
                omahaCard.setOrigin(cardWidth/2f,0);
                omahaCard.setPosition(startX,0);
                addActor(omahaCard);
                startX=startX+cardPad;
            }
        }
    }
}
