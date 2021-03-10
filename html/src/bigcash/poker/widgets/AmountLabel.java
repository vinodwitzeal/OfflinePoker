package bigcash.poker.widgets;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import bigcash.poker.utils.PokerUtils;

public class AmountLabel extends Label {
    public float amount;
    private TextureRegion leftDrawable,rightDrawable;
    public float sideHeight,sideWidth,leftX,leftY,rightX,rightY;
    public AmountLabel(float amount, PokerLabelStyle style) {
        super("\u20b9 "+ PokerUtils.getValue(amount), style);
        this.amount=PokerUtils.getValue(amount);
        this.leftDrawable=style.leftDrawable;
        this.rightDrawable= style.rightDrawable;
    }

    public float getAmount(){
        return this.amount;
    }


    public void updateAmount(float amount){
        this.amount=PokerUtils.getValue(amount);
        setText("\u20b9 "+this.amount);
    }

    public Vector2 getStageCoordinates(){
        return localToStageCoordinates(new Vector2());
    }

    public void resetLabel(){
        updateAmount(0.00f);
    }

    @Override
    public void layout() {
        super.layout();
        sideHeight=getHeight();
        sideWidth=sideHeight*leftDrawable.getRegionWidth()/leftDrawable.getRegionHeight();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        validate();
        leftX=getX()-sideWidth+1.0f;
        leftY=getY();
        rightX=getX()+getWidth()-1.0f;
        rightY=getY();
        batch.draw(leftDrawable,leftX,leftY,sideWidth,sideHeight);
        batch.draw(rightDrawable,rightX,rightY,sideWidth,sideHeight);
        super.draw(batch, parentAlpha);
    }

    public static class PokerLabelStyle extends LabelStyle{
        public TextureRegion leftDrawable,rightDrawable;
        public PokerLabelStyle(TextureRegion region,int left,int right) {
            super();
            int center=region.getRegionWidth()-(left+right);
            int centerX=region.getRegionWidth()/2;
            this.background=new TextureRegionDrawable(new TextureRegion(region,centerX-center/2,0,center,region.getRegionHeight()));
            this.leftDrawable=new TextureRegion(region,0,0,left,region.getRegionHeight());
            this.rightDrawable=new TextureRegion(region,region.getRegionWidth()-right,0,right,region.getRegionHeight());
        }
    }
}
