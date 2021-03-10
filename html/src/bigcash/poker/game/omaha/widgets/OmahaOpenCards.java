package bigcash.poker.game.omaha.widgets;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import bigcash.poker.game.omaha.OmahaWorld;

public class OmahaOpenCards extends Group {
    private OmahaWorld omahaWorld;
    private float centerX,centerY,cardWidth,cardHeight,cardPad;
    private LinkedHashMap<Integer, OmahaCard> cardHashMap;
    public OmahaOpenCards(OmahaWorld omahaWorld){
        this.omahaWorld=omahaWorld;
        this.cardWidth=omahaWorld.cardWidth;
        this.cardHeight=omahaWorld.cardHeight;
        this.cardPad=this.cardWidth*0.1f;
        this.cardHashMap=new LinkedHashMap<Integer, OmahaCard>();
    }


    public void updateCards(int[] cards){
        this.cardHashMap.clear();
        clearChildren();
        if (cards==null || cards.length==0)return;
        for (int i=0;i<cards.length;i++){
            OmahaCard card=new OmahaCard(omahaWorld,cards[i],1.0f);
            card.setSize(cardWidth,cardHeight);
            this.cardHashMap.put(card.id,card);
        }
        int totalCards=this.cardHashMap.size();

        float totalWidth=totalCards*cardWidth+(totalCards-1)*cardPad;
        float startX=0-totalWidth/2f;
        float cardY=0;
        for (int i=0;i<cards.length;i++){
            OmahaCard omahaCard =this.cardHashMap.get(cards[i]);
            omahaCard.setPosition(startX,cardY);
            this.cardHashMap.get(cards[i]).setPosition(startX,cardY);
            startX=startX+cardPad+cardWidth;
            addActor(omahaCard);
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
        List<OmahaCard> newCards=getNewCards(openCards);
        List<OmahaCard> oldCards=new ArrayList<OmahaCard>();
        oldCards.addAll(cardHashMap.values());

        int totalCards=oldCards.size()+newCards.size();


        float totalWidth=totalCards*cardWidth+(totalCards-1)*cardPad;
        float startX=-totalWidth/2f;
        float cardY=0;


        for (OmahaCard oldCard:oldCards){
            oldCard.addAction(Actions.moveTo(startX,cardY,0.1f));
            startX=startX+cardPad+cardWidth;
        }

        Vector2 startPosition=new Vector2(omahaWorld.dealerX,omahaWorld.dealerY);
        startPosition=stageToLocalCoordinates(startPosition);
        for (OmahaCard newCard:newCards){
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

    private List<OmahaCard> getNewCards(int[] openCards){
        List<OmahaCard> newCards=new ArrayList<OmahaCard>();
        for (int i=0;i<openCards.length;i++){
            if (!cardHashMap.containsKey(openCards[i])){
                OmahaCard omahaCard =new OmahaCard(omahaWorld,openCards[i],-1);
                newCards.add(omahaCard);
            }
        }
        return newCards;
    }

    public HashMap<Integer, OmahaCard> getCards(){
        return this.cardHashMap;
    }

    public void removeCards(){
        clearChildren();
        cardHashMap.clear();
    }

}
