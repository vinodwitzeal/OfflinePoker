package bigcash.poker.game.holdem.widgets;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;

import bigcash.poker.game.holdem.HoldemWorld;
import bigcash.poker.utils.PokerConstants;


public class HoldemCard extends Widget {
    public int id, rank, suit;
    private TextureRegion back, front;
    public HoldemWorld holdemWorld;

    public HoldemCard(HoldemWorld holdemWorld, int id, float scaleX) {
        this.holdemWorld = holdemWorld;
        if (id==-1){
            this.id=id;
            this.rank=id;
            this.suit=id;
            this.back= holdemWorld.backCardRegion;
            this.front= holdemWorld.backCardRegion;
        }else {
            this.id = id;
            this.rank = id % 13;
            if (this.rank == 0) {
                this.rank = 13;
            }
            this.suit = getSuit(id);
            this.front = holdemWorld.atlas.findRegion(holdemWorld.suitMap.get(suit), rank);
            this.back = holdemWorld.backCardRegion;
        }
        this.setScaleX(scaleX);
    }

    public HoldemCard(HoldemWorld holdemWorld){
        this(holdemWorld,-1,-1.0f);
    }

    public void updateCard(int id,float scaleX){
        if (id==-1){
            this.id=id;
            this.rank=id;
            this.suit=id;
            this.back= holdemWorld.backCardRegion;
            this.front= holdemWorld.backCardRegion;
        }else {
            this.id = id;
            this.rank = id % 13;
            if (this.rank == 0) {
                this.rank = 13;
            }
            this.suit = getSuit(id);
            this.front = holdemWorld.atlas.findRegion(holdemWorld.suitMap.get(suit), rank);
            this.back = holdemWorld.backCardRegion;
        }
        this.setScaleX(scaleX);
    }

    public void flipToBack(float duration){
        if (duration>0.0f) {
            addAction(Actions.scaleTo(-1.0f, 1.0f, duration));
        }else {
            setScaleX(-1.0f);
        }
    }

    public void flipToFront(float duration){
        if (duration>0.0f) {
            addAction(Actions.scaleTo(1.0f, 1.0f, duration));
        }else {
            setScaleX(1.0f);
        }
    }



    private int getSuit(int id){
        if (id>=1 && id<=13){
            return PokerConstants.SUIT_SPADE;
        }else if (id>=14 && id<=26){
            return PokerConstants.SUIT_HEART;
        }else if (id>=27 && id<=39){
            return PokerConstants.SUIT_CLUB;
        }else if (id>=40 && id<=52){
           return PokerConstants.SUIT_DIAMOND;
        }
        return PokerConstants.INVALID;
    }

    public void fadeCard(){
        setColor(Color.LIGHT_GRAY);
    }



    @Override
    public void draw(Batch batch, float parentAlpha) {
        Color color=batch.getColor();
        batch.setColor(getColor());
        if (this.id==-1){
            batch.draw(back, getX(), getY(), getWidth() / 2f, getHeight() / 2f, getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
        }else {
            if (getScaleX() >= 0) {
                batch.draw(front, getX(), getY(), getWidth() / 2f, getHeight() / 2f, getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
            } else {
                batch.draw(back, getX(), getY(), getWidth() / 2f, getHeight() / 2f, getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
            }
        }
        batch.setColor(color);
    }


    public void flashCard(boolean flash,float delay){
        setOrigin(getWidth()/2f,0);
        Color color=getColor();
        setColor(Color.GRAY);
        if (flash){
            setColor(Color.WHITE);
            addAction(Actions.sequence(
                    Actions.delay(delay),
                    Actions.scaleTo(1.4f,1.4f,0.2f),
                    Actions.delay(1.6f),
                    Actions.scaleTo(1.0f,1.0f,0.2f)
            ));
        }else {
            addAction(Actions.sequence(
                    Actions.delay(2.0f),
                    Actions.run(new Runnable() {
                        @Override
                        public void run() {
                            setColor(Color.WHITE);
                        }
                    })
            ));
        }
    }
}
