package bigcash.poker.widgets;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

import bigcash.poker.font.Font;
import bigcash.poker.font.FontPool;
import bigcash.poker.font.FontStyle;
import bigcash.poker.font.FontType;


public class StyledLabel extends Label {
    private Font font;
    private FontStyle fontStyle;


    public static StyledLabel getLabel(String text,StyledLabelStyle labelStyle,FontStyle fontStyle){
        StyledLabel label=new StyledLabel(text,labelStyle);
        label.fontStyle=fontStyle;
        return label;
    }

    public StyledLabel(CharSequence text) {
        this(text, new StyledLabelStyle());
    }

    public StyledLabel(CharSequence text, StyledLabelStyle style) {
        super(text, style);
        this.font = style.styledFont;
    }


    public StyledLabel outline(float outline, Color color) {
        if (this.fontStyle == null) {
            this.fontStyle = new FontStyle();
        }
        this.fontStyle.setOutline(outline);
        this.fontStyle.setOutlineColor(color);
        return this;
    }

    public StyledLabel shadow(float x, float y, Color color) {
        if (this.fontStyle == null) {
            this.fontStyle = new FontStyle();
        }
        this.fontStyle.setShadowX(x);
        this.fontStyle.setShadowY(y);
        this.fontStyle.setShadowColor(color);
        return this;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        font.setFontStyle(fontStyle);
        super.draw(batch, parentAlpha);
        font.setFontStyle(null);
    }

    public static class StyledLabelStyle extends LabelStyle {
        public Font styledFont;

        private StyledLabelStyle() {
            this(8);
        }

        public StyledLabelStyle(float size) {
            this(FontType.ROBOTO_REGULAR, size);
        }

        public StyledLabelStyle(FontType fontType, float size) {
            styledFont = FontPool.obtain(fontType, size);
            font = styledFont;
            fontColor = Color.WHITE;
        }
    }
}
