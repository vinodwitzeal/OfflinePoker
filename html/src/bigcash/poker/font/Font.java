package bigcash.poker.font;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFontCache;
import com.badlogic.gdx.graphics.g2d.DistanceFieldFont;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;

public class Font extends DistanceFieldFont {
    private static final String SMOOTHING = "u_smoothing";
    private static final String ENABLE_SHADOW = "enableShadow", SHADOW_OFFSET = "u_shadow", SHADOW_COLOR = "u_shadowColor";
    private static final String ENABLE_OUTLINE = "enableOutline", OUTLINE_WIDTH = "u_outline", OUTLINE_COLOR = "u_outlineColor";
    private final Vector2 shadow = new Vector2();
    private int enableShadow, enableOutline;
    private float outline, smoothing;
    private Color shadowColor, outlineColor;
    private float shadowOffset;

    private ShaderProgram defaultShader, distanceShader;

    protected Font(FontData fontData, float size, ShaderProgram distanceShader) {
        super(fontData.getData(), fontData.getTexture(size), false);
        shadowColor = Color.BLACK;
        outlineColor = Color.GRAY;
        this.smoothing = fontData.getSmoothing(size);
        this.distanceShader = distanceShader;
        this.shadowOffset = 0.002f;
        getData().setScale(fontData.getScale(size));
    }


    public void setFontStyle(FontStyle style) {
        if (style == null) {
            enableOutline = 0;
            this.outline = 0;
            enableShadow = 0;
            shadowColor = Color.CLEAR;
            outlineColor = Color.CLEAR;
        } else {
            if (style.outline > 0) {
                this.outline = style.outline;
                enableOutline = 1;
            }
            if (style.outlineColor != null)
                outlineColor = style.outlineColor;

            if (style.shadowColor != null) {
                enableShadow = 1;
                shadow.set(style.getShadowX() * shadowOffset, style.getShadowY() * shadowOffset);
                shadowColor = style.shadowColor;
            }
        }
    }

    @Override
    public BitmapFontCache newFontCache() {
        return new FontCache(this, false);
    }

    private class FontCache extends BitmapFontCache {
        private FontCache(BitmapFont font, boolean integer) {
            super(font, integer);
        }

        private void setShader(Batch batch) {
            defaultShader = batch.getShader();
            batch.flush();
            batch.setShader(distanceShader);
            distanceShader.setUniformf(SMOOTHING, smoothing);
            distanceShader.setUniformi(ENABLE_OUTLINE, enableOutline);
            distanceShader.setUniformi(ENABLE_SHADOW, enableShadow);
            distanceShader.setUniformf(OUTLINE_WIDTH, outline);
            distanceShader.setUniformf(OUTLINE_COLOR, outlineColor);
            distanceShader.setUniformf(SHADOW_OFFSET, shadow);
            distanceShader.setUniformf(SHADOW_COLOR, shadowColor);
        }

        private void removeShader(Batch batch) {
            batch.flush();
            batch.setShader(defaultShader);
        }

        @Override
        public void draw(Batch spriteBatch) {
            setShader(spriteBatch);
            super.draw(spriteBatch);
            removeShader(spriteBatch);
        }

        @Override
        public void draw(Batch spriteBatch, int start, int end) {
            setShader(spriteBatch);
            super.draw(spriteBatch, start, end);
            removeShader(spriteBatch);
        }

        @Override
        public void draw(Batch spriteBatch, float alphaModulation) {
            setShader(spriteBatch);
            super.draw(spriteBatch, alphaModulation);
            removeShader(spriteBatch);
        }
    }

}
