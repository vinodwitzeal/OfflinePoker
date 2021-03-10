package bigcash.poker.utils;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

/**
 * Created by Vinod on 29-08-2017.
 */

public class TextureDrawable {
    public static TextureRegionDrawable getDrawable(Texture texture, float minWidth, float minHeight) {
        return getDrawable(new TextureRegion(texture), minWidth, minHeight);
    }

    public static TextureRegionDrawable getDrawable(TextureRegion textureRegion, float minWidth, float minHeight) {
        TextureRegionDrawable textureRegionDrawable = new TextureRegionDrawable(textureRegion);
        textureRegionDrawable.setMinWidth(minWidth);
        textureRegionDrawable.setMinHeight(minHeight);
        return textureRegionDrawable;
    }
}
