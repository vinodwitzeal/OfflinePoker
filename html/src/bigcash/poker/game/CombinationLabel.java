package bigcash.poker.game;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;

public class CombinationLabel extends Group {
    private Label label;
    private Image image;
    public CombinationLabel(PokerAbstractScreen screen, Label.LabelStyle labelStyle, TextureRegion combinationStars, String combinationName){
        setPosition(0,0);
        setSize(screen.width, screen.height);
        setTouchable(Touchable.disabled);
        label=new Label(combinationName, labelStyle);
        label.pack();
        float labelHeight= labelStyle.font.getLineHeight()*2;
        label.setWidth(label.getWidth()+4*labelHeight);
        label.setHeight(labelHeight*1.2f);
        label.setAlignment(Align.top);
        float imageWidth=label.getWidth();
        float imageHeight=imageWidth* combinationStars.getRegionHeight()/ combinationStars.getRegionWidth();
        image=new Image(combinationStars);
        image.setSize(imageWidth,imageHeight);
        image.setOrigin(imageWidth/2f,imageHeight/2f);
        addActor(image);
        addActor(label);

        image.addAction(Actions.repeat(
                RepeatAction.FOREVER,
                Actions.rotateBy(30,1.0f)
        ));
    }

    @Override
    public void setOrigin(float originX, float originY) {
        super.setOrigin(originX, originY);
        label.setPosition(originX-label.getWidth()/2f,originY-label.getHeight()/2f);
        image.setPosition(originX-image.getWidth()/2f,originY-image.getHeight()/2f);
    }


    @Override
    public void draw(Batch batch, float parentAlpha) {
        image.draw(batch,parentAlpha);
        label.draw(batch,parentAlpha);
    }
}
