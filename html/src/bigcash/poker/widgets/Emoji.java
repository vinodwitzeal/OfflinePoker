package bigcash.poker.widgets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;

public class Emoji extends Widget {
    private String name;
    private Animation<TextureRegion> regionAnimation;
    private TextureRegion currentRegion;
    private float current = 0.0f;
    private float duration;
    private float rotationOffset;
    private float frameRate = 0.1f;
    private boolean downloaded;
    public boolean play = false;
    private EmojiErrorListener emojiErrorListener;

    public Emoji(String name) {
        this.name = name;
    }


    public Emoji(String name, Texture texture) {
        this.name = name;
        this.downloaded = true;
        this.play = false;
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        int textureHeight = texture.getHeight();
        int textureWidth = texture.getWidth();
        int count = textureWidth / textureHeight;
        Array<TextureRegion> regions = new Array<TextureRegion>();
        for (int i = 0; i < count; i++) {
            TextureRegion region = new TextureRegion(texture, i * textureHeight, 0, textureHeight, textureHeight);
            regions.add(region);
        }
        regionAnimation = new Animation<TextureRegion>(frameRate, regions, Animation.PlayMode.LOOP_REVERSED);
        duration = frameRate * count;
        current=0.0f;
    }

    public Emoji(String name, TextureRegion textureRegion) {
        this.downloaded = true;
        this.name = name;
        this.play = false;
        if (textureRegion instanceof TextureAtlas.AtlasRegion) {
            if (((TextureAtlas.AtlasRegion) textureRegion).rotate) {
                rotationOffset = 90;
            }
        }
        int textureHeight = textureRegion.getRegionHeight();
        int textureWidth = textureRegion.getRegionWidth();
        int count = textureWidth / textureHeight;
        Array<TextureRegion> regions = new Array<TextureRegion>();
        for (int i = 0; i < count; i++) {
            TextureRegion region = new TextureRegion(textureRegion, i * textureHeight, 0, textureHeight, textureHeight);
            regions.add(region);
        }
        regionAnimation = new Animation<TextureRegion>(frameRate, regions, Animation.PlayMode.LOOP_REVERSED);
        duration = frameRate * count;
        current=0.0f;
    }

    public Emoji(Emoji emoji) {
        this.regionAnimation = emoji.regionAnimation;
        this.current = 0.0f;
        this.duration = emoji.duration;
        this.play = false;
    }

    public String getEmojiName() {
        return name;
    }

    public boolean isDownloaded() {
        return downloaded;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (regionAnimation != null) {
            if (play) {
                current -= delta;
                if (current <= 0) {
                    current += duration;
                }
            }
            currentRegion = regionAnimation.getKeyFrame(current);
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        if (currentRegion != null) {
            Color color = getColor();
            batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
            batch.draw(currentRegion, getX(), getY(), getWidth() / 2f, getHeight() / 2f, getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation() - 90, false);
        }
    }


    public void setEmoji(TextureRegion textureRegion) {
        int textureHeight = textureRegion.getRegionHeight();
        int textureWidth = textureRegion.getRegionWidth();
        int count = textureWidth / textureHeight;
        Array<TextureRegion> regions = new Array<TextureRegion>();
        for (int i = 0; i < count; i++) {
            TextureRegion region = new TextureRegion(textureRegion, i * textureHeight, 0, textureHeight, textureHeight);
            regions.add(region);
        }
        regionAnimation = new Animation<TextureRegion>(frameRate, regions, Animation.PlayMode.LOOP_REVERSED);
        duration = frameRate * count;
        current=0.0f;
    }

    public void setEmoji(Texture texture) {
        if (downloaded)return;
        this.downloaded = true;
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        setEmoji(new TextureRegion(texture));
    }

    public EmojiErrorListener getEmojiErrorListener() {
        return emojiErrorListener;
    }

    public void setEmojiErrorListener(EmojiErrorListener emojiErrorListener) {
        this.emojiErrorListener = emojiErrorListener;
    }

    public void setError(String source) {
        if (emojiErrorListener != null && !downloaded) {
            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    Gdx.app.postRunnable(new Runnable() {
                        @Override
                        public void run() {
                            emojiErrorListener.onDownloadFailed(Emoji.this);
                        }
                    });
                }
            }, 1.0f);
        }
    }


    public interface EmojiErrorListener {
        void onDownloadFailed(Emoji emoji);
    }
}
