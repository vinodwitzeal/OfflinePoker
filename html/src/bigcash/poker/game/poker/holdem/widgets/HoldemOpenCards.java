package bigcash.poker.game.poker.holdem.widgets;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import bigcash.poker.game.poker.holdem.HoldemWorld;


public class HoldemOpenCards extends Group {
    private HoldemWorld holdemWorld;
    private float centerX,centerY,cardWidth,cardHeight,cardPad;
    private LinkedHashMap<Integer, HoldemCard> cardHashMap;
    public HoldemOpenCards(HoldemWorld holdemWorld){
        this.holdemWorld = holdemWorld;
        this.cardWidth= holdemWorld.cardWidth;
        this.cardHeight= holdemWorld.cardHeight;
        this.cardPad=this.cardWidth*0.1f;
        this.cardHashMap=new LinkedHashMap<Integer, HoldemCard>();
    }


    public void updateCards(int[] cards){
        this.cardHashMap.clear();
        clearChildren();
        if (cards==null || cards.length==0)return;
        for (int i=0;i<cards.length;i++){
            HoldemCard card=new HoldemCard(holdemWorld,cards[i],1.0f);
            card.setSize(cardWidth,cardHeight);
            this.cardHashMap.put(card.id,card);
        }
        int totalCards=this.cardHashMap.size();

        float totalWidth=totalCards*cardWidth+(totalCards-1)*cardPad;
        float startX=0-totalWidth/2f;
        float cardY=0;
        for (int i=0;i<cards.length;i++){
            HoldemCard holdemCard =this.cardHashMap.get(cards[i]);
            holdemCard.setPosition(startX,cardY);
            this.cardHashMap.get(cards[i]).setPosition(startX,cardY);
            startX=startX+cardPad+cardWidth;
            addActor(holdemCard);
        }
    }

    @Override
    public void setColor(Color color) {
        super.setColor(color);
        for (Actor actor:getChildren()){
            actor.setColor(color);
        }
    }

    public void openCards(int[] openCards){
        if (openCards==null || openCards.length==0)return;
        List<HoldemCard> newCards=getNewCards(openCards);
        List<HoldemCard> oldCards=new ArrayList<HoldemCard>();
        oldCards.addAll(cardHashMap.values());

        int totalCards=oldCards.size()+newCards.size();


        float totalWidth=totalCards*cardWidth+(totalCards-1)*cardPad;
        float startX=-totalWidth/2f;
        float cardY=0;


        for (HoldemCard oldCard:oldCards){
            oldCard.addAction(Actions.moveTo(startX,cardY,0.1f));
            startX=startX+cardPad+cardWidth;
        }

        Vector2 startPosition=new Vector2(holdemWorld.dealerX, holdemWorld.dealerY);
        startPosition=stageToLocalCoordinates(startPosition);
        for (HoldemCard newCard:newCards){
            newCard.setSize(cardWidth,cardHeight);
            newCard.setOrigin(cardWidth/2f,0);
            newCard.setScale(-1,1);
            newCard.setPosition(startPosition.x,startPosition.y);
            cardHashMap.put(newCard.id,newCard);
            addActor(newCard);
            newCard.addAction(Actions.sequence(
                    Actions.delay(0.1f),
                    Actions.moveTo(startX,cardY,0.2f),
                    Actions.scaleTo(1,1,0.1f)
            ));
            startX=startX+cardPad+cardWidth;
        }

    }

    private List<HoldemCard> getNewCards(int[] openCards){
        List<HoldemCard> newCards=new ArrayList<HoldemCard>();
        for (int i=0;i<openCards.length;i++){
            if (!cardHashMap.containsKey(openCards[i])){
                HoldemCard holdemCard =new HoldemCard(holdemWorld,openCards[i],-1);
                newCards.add(holdemCard);
            }
        }
        return newCards;
    }

    public HashMap<Integer, HoldemCard> getCards(){
        return this.cardHashMap;
    }

    public void removeCards(){
        clearChildren();
        cardHashMap.clear();
    }

}
