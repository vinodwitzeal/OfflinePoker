package bigcash.poker.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import bigcash.poker.game.holdem.widgets.RadialSprite;
import bigcash.poker.utils.AssetsLoader;
import bigcash.poker.widgets.MaskLayer;

public class PokerPlayerTurnImage extends Image {
    private MaskLayer maskLayer;
    private boolean winner;
    private TextureRegion frameRegion, crownRegion;
    private RadialSprite radialSprite;
    private float percentage, angle;
    private float crownXOffset, crownYOffset, crownWidth, crownHeight, frameSize, frameXOffset, frameYOffset;
    private Vector2 localPosition;
    private PokerAbstractScreen gameScreen;

    public PokerPlayerTurnImage(PokerAbstractScreen gameScreen, TextureRegion region, TextureRegion crown, float imageSize) {
        super(region);
        this.gameScreen = gameScreen;
        this.frameRegion = AssetsLoader.instance().circleRegion;
        this.crownRegion = crown;
        this.localPosition = new Vector2();
        this.maskLayer = AssetsLoader.instance().circleMaskLayer;
        this.frameSize = imageSize * 1.1f;
        this.frameXOffset = (frameSize - imageSize) / 2f;
        this.frameYOffset = (frameSize - imageSize) / 2f;
        this.crownWidth = imageSize * 1.5f;
        this.crownHeight = crownWidth * crown.getRegionHeight() / crown.getRegionWidth();
        this.crownXOffset = (crownWidth - imageSize) / 2f;
        this.crownYOffset = crownHeight * 0.13f;
        this.radialSprite = new RadialSprite(this.frameRegion);
        radialSprite.setColor(Color.CLEAR);
        angle = 0.0f;
        this.percentage = 0.0f;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        validate();
        float x = getX();
        float y = getY();
        Color color = getColor();
        batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
        batch.draw(frameRegion, x - frameXOffset, y - frameYOffset, frameSize / 2f, frameSize / 2f, frameSize, frameSize, getScaleX(), getScaleY(), getRotation());
        localPosition.set(0, 0);
        localPosition = gameScreen.getMaskPosition(this,localPosition);
        maskLayer.setPosition(localPosition.x, localPosition.y);
        maskLayer.setSize(getWidth(), getHeight());
        maskLayer.begin(batch, gameScreen.stage.getWidth(), gameScreen.stage.getHeight());
        super.draw(batch, parentAlpha);
        maskLayer.end(batch);
        radialSprite.draw(batch, x, y, getWidth(), getHeight(), angle);
        if (winner) {
            batch.draw(crownRegion, getX() - crownXOffset, getY() - crownYOffset, crownWidth / 2f, crownHeight / 2f, crownWidth, crownHeight, getScaleX(), getScaleY(), getRotation());
        }
    }

    public void resetImage() {
        radialSprite.setColor(Color.CLEAR);
    }


    public void setTimerColor(Color color) {
        radialSprite.setColor(color);
    }

    public void updateTime(float leftTime, float totalTime) {
        percentage = leftTime / totalTime;
        if (percentage > 1.0f)
            percentage = 1.0f;
        angle = 360 * percentage;
    }

    public void showWinner() {
        winner = true;
    }

    public void hideWinner() {
        winner = false;
    }
}
