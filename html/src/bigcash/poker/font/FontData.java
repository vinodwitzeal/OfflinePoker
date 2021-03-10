package bigcash.poker.font;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class FontData {
    private TextureRegion bigTexture;
    private TextureRegion smallTexture;
    private Texture sTexture, bTexture;
    private FileHandle handle;

    protected FontData(FontType type) {
        bTexture = new Texture("fonts/" + type.value() + "/" + type.value() + ".png");
        bTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        bigTexture = new TextureRegion(bTexture);
        sTexture = new Texture("fonts/" + type.value() + "/" + type.value() + ".png");
        sTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Linear);
        smallTexture = new TextureRegion(sTexture);
        handle = Gdx.files.internal("fonts/" + type.value() + "/" + type.value() + ".fnt");
    }

    protected TextureRegion getTexture(float size) {
        if (size < 32) {
            return smallTexture;
        } else {
            return bigTexture;
        }
    }

    protected float getSmoothing(float size) {
        float scale = size / 16.0f;
        return 0.25f / (2 * scale);
    }

    protected float getScale(float size) {
        return size / 16.0f;
    }

    protected FileHandle getData() {
        return handle;
    }

    public void dispose() {
        sTexture.dispose();
        bTexture.dispose();
    }
}
