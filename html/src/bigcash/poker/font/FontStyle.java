package bigcash.poker.font;

import com.badlogic.gdx.graphics.Color;

public class FontStyle {
    public float outline;
    public Color outlineColor;

    public float shadowX, shadowY;
    public Color shadowColor;

    public float getOutline() {
        return outline;
    }

    public void setOutline(float outline) {
        if (outline > 1.0f)
            outline = 1.0f;
        outline = 1.0f - outline;
        this.outline = outline * 0.5f;
        if (this.outline < 0.1f) {
            this.outline = 0.1f;
        }
    }

    public Color getOutlineColor() {
        return outlineColor;
    }

    public void setOutlineColor(Color outlineColor) {
        this.outlineColor = outlineColor;
    }

    public float getShadowX() {
        return shadowX;
    }

    public void setShadowX(float shadowX) {
        if (shadowX < 0.01f)
            shadowX = 0.01f;
        if (shadowX > 0.99f)
            shadowX = 0.99f;
        this.shadowX = shadowX;
    }

    public float getShadowY() {
        return shadowY;
    }

    public void setShadowY(float shadowY) {
        if (shadowY < 0.01f)
            shadowY = 0.01f;
        if (shadowY > 0.99f)
            shadowY = 0.99f;
        this.shadowY = shadowY;
    }

    public Color getShadowColor() {
        return shadowColor;
    }

    public void setShadowColor(Color shadowColor) {
        this.shadowColor = shadowColor;
    }
}
