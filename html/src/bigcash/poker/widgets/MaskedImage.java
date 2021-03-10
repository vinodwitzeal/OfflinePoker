package bigcash.poker.widgets;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import bigcash.poker.models.UIScreen;

public class MaskedImage extends Image {
    private UIScreen screen;
    private MaskLayer maskLayer;
    private Vector2 localPosition;
    public MaskedImage(UIScreen screen,TextureRegion region, MaskLayer maskLayer) {
        super(region);
        this.screen=screen;
        this.maskLayer=maskLayer;
        localPosition=new Vector2();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        validate();
        float x=getX();
        float y=getY();
        localPosition.set(0,0);
        localPosition=screen.getMaskPosition(this,localPosition);
        maskLayer.setPosition(localPosition.x,localPosition.y);
        maskLayer.setSize(getWidth(),getHeight());
        maskLayer.begin(batch,getStage().getWidth(),getStage().getHeight());
        super.draw(batch, parentAlpha);
        maskLayer.end(batch);
    }
}
