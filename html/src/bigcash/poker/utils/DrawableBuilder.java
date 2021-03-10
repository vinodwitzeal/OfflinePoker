package bigcash.poker.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class DrawableBuilder {
    private int width, height, radius, outline;
    private Color color, outlineColor;

    public DrawableBuilder() {
        this(4, 4, Color.DARK_GRAY);
    }

    public DrawableBuilder(int width, int height) {
        this(width, height, Color.DARK_GRAY);
    }

    public DrawableBuilder(int width, int height, Color color) {
        this.width = width;
        this.height = height;
        this.color = color;
    }

    public static float getMinHeight(TextureRegion textureRegion, float minWidth) {
        return minWidth * textureRegion.getRegionHeight() / textureRegion.getRegionWidth();
    }

    public static float getMinWidth(TextureRegion textureRegion, float minHeight) {
        return minHeight * textureRegion.getRegionWidth() / textureRegion.getRegionHeight();
    }

    public static float getMinHeight(Texture texture, float minWidth) {
        return minWidth * texture.getHeight() / texture.getWidth();
    }

    public static float getMinWidth(Texture texture, float minHeight) {
        return minHeight * texture.getWidth() / texture.getHeight();
    }

    public static TextureRegionDrawable getDrawable(TextureRegion textureRegion, float minWidth, float minHeight) {
        TextureRegionDrawable textureRegionDrawable = new TextureRegionDrawable(textureRegion);

        if (minWidth <= 0 && minHeight <= 0) {
            return textureRegionDrawable;
        } else if (minWidth <= 0) {
            minWidth = getMinWidth(textureRegion, minHeight);
        } else if (minHeight <= 0) {
            minHeight = getMinHeight(textureRegion, minWidth);
        }
        textureRegionDrawable.setMinWidth(minWidth);
        textureRegionDrawable.setMinHeight(minHeight);
        return textureRegionDrawable;
    }

    public static TextureRegionDrawable getDrawable(Texture texture, float minWidth, float minHeight) {
        return getDrawable(new TextureRegion(texture), minWidth, minHeight);
    }

    public DrawableBuilder color(Color color) {
        this.color = color;
        return this;
    }

    public DrawableBuilder outline(int outline) {
        return outline(outline, Color.BLACK);
    }

    public DrawableBuilder outline(int outline, Color color) {
        this.outline = outline;
        this.outlineColor = color;
        return this;
    }

    public DrawableBuilder radius(int radius) {
        this.radius = radius;
        return this;
    }

    public TextureRegionDrawable createDrawable() {
        return createDrawable(false);
    }

    public TextureRegionDrawable createDrawable(boolean scale) {
        if (scale) {
            float density = 1;
            this.width = (int) (density * width);
            this.height = (int) (density * height);
            this.outline = (int) (density * outline);
            this.radius = (int) (density * radius);
        }
        Pixmap pixmap = pixmap(width, height, color);
        Texture texture = new Texture(pixmap);
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        pixmap.dispose();
        return new TextureRegionDrawable(new TextureRegion(texture));
    }

    public NinePatchDrawable createNinePatch() {
        return createNinePatch(false);
    }

    public NinePatchDrawable createNinePatch(boolean scale) {
        if (scale) {
            float density =1;
            this.width = (int) (density * width);
            this.height = (int) (density * height);
            this.outline = (int) (density * outline);
            this.radius = (int) (density * radius);
        }
        NinePatch ninePatch;
        if (outline > 0) {
            if (radius > 0) {
                int outerWidth = width + outline * 2;
                int outerHeight = height + outline * 2;
                int outerRadius = radius + outline;
                Pixmap outerPixmap = roundedPixmap(outerWidth, outerHeight, outerRadius, outlineColor);
                Pixmap innerPixmap = roundedPixmap(width, height, radius, color);
                outerPixmap.drawPixmap(innerPixmap, outline - 1, outline - 1);
                Texture texture = new Texture(outerPixmap);
                texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
                outerPixmap.dispose();
                innerPixmap.dispose();
                ninePatch = new NinePatch(texture, outerRadius, outerRadius, outerRadius, outerRadius);
            } else {
                int outerWidth = width + outline * 2;
                int outerHeight = height + outline * 2;
                Pixmap outerPixmap = pixmap(outerWidth, outerHeight, outlineColor);
                Pixmap innerPixmap = pixmap(width, height, color);
                outerPixmap.drawPixmap(innerPixmap, outline - 1, outline - 1);
                Texture texture = new Texture(outerPixmap);
                texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
                outerPixmap.dispose();
                innerPixmap.dispose();
                ninePatch = new NinePatch(texture, outline, outline, outline, outline);
            }
        } else {
            if (radius > 0) {
                Pixmap pixmap = roundedPixmap(width, height, radius, color);
                Texture texture = new Texture(pixmap);
                texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
                pixmap.dispose();
                ninePatch = new NinePatch(texture, radius + 1, radius + 1, radius + 1, radius + 1);
            } else {
                Pixmap pixmap = pixmap(width, height, color);
                Texture texture = new Texture(pixmap);
                texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
                pixmap.dispose();
                ninePatch = new NinePatch(texture);
            }
        }

        return new NinePatchDrawable(ninePatch);
    }

    private Pixmap roundedPixmap(int width, int height, int radius, Color color) {
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        pixmap.setFilter(Pixmap.Filter.BiLinear);
        pixmap.setColor(color);

//        Rectangles
        pixmap.fillRectangle(radius - 1, 0, pixmap.getWidth() - 2 * radius, pixmap.getHeight());
        pixmap.fillRectangle(0, radius - 1, pixmap.getWidth(), pixmap.getHeight() - 2 * radius);

        //Top-Left Circle
        pixmap.fillCircle(radius - 1, radius - 1, radius);

        //Top-Right
        pixmap.fillCircle(pixmap.getWidth() - radius - 1, radius - 1, radius);

        //Bottom-Left
        pixmap.fillCircle(radius - 1, pixmap.getHeight() - radius - 1, radius);

        //Bottom-Right
        pixmap.fillCircle(pixmap.getWidth() - radius - 1, pixmap.getHeight() - radius - 1, radius);


        return pixmap;
    }

    private Pixmap pixmap(int width, int height, Color color) {
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        pixmap.setFilter(Pixmap.Filter.BiLinear);
        pixmap.setColor(color);
        pixmap.fill();
        return pixmap;
    }
}
