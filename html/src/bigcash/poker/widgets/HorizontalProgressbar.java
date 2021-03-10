package bigcash.poker.widgets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;

public class HorizontalProgressbar extends Image {
    private final Rectangle bounds = new Rectangle();
    private final Rectangle scissors = new Rectangle();
    private final float totalTime = 2.0f;
    private NinePatchDrawable fillTexture;
    private float percent;
    private float current;
    private float time;

    public HorizontalProgressbar(TextureRegion backTexture, TextureRegion fillTexture) {
        NinePatchDrawable emptyDrawable = new NinePatchDrawable(new NinePatch(backTexture, 12, 12, 2, 2));
        setDrawable(emptyDrawable);
        this.fillTexture = new NinePatchDrawable(new NinePatch(fillTexture, 12, 12, 2, 2));
        this.percent = 0.0f;
        this.current = 0;
    }


    @Override
    public void draw(Batch batch, float parentAlpha) {
        current += Gdx.graphics.getDeltaTime();
        if (current > time)
            current = time;
        float currentPercent = percent * Interpolation.linear.apply(current / time);
        bounds.set(getX(), getY(), getWidth() * currentPercent, getHeight());
        batch.flush();
        getStage().calculateScissors(bounds, scissors);
        if (ScissorStack.pushScissors(scissors)) {
            fillTexture.draw(batch, getX(), getY(), getWidth(), getHeight());
            batch.flush();
            ScissorStack.popScissors();
        }
        super.draw(batch, parentAlpha);

    }

    public void setPercent(float percent) {
        this.percent = percent;
        this.current = 0;
        this.time = totalTime * percent;
    }
}

