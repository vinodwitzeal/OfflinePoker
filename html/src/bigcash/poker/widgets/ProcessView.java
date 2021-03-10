package bigcash.poker.widgets;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;

import bigcash.poker.utils.AssetsLoader;

public class ProcessView extends Widget {
    private Animation<TextureRegion> animation;
    private float current = 0;
    private TextureRegion region;

    public ProcessView() {
        animation = new Animation<TextureRegion>(0.05f, AssetsLoader.instance().processAtlas.findRegions("icon"));
        animation.setPlayMode(Animation.PlayMode.LOOP);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        current += delta;
        region = animation.getKeyFrame(current);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        validate();
        if (region != null) {
            Color color=getColor();
            batch.setColor(color.r,color.g,color.b,color.a*parentAlpha);
            batch.draw(region, getX(), getY(), getOriginX(), getOriginY(), getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
        }
    }
}
