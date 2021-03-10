package bigcash.poker.downloader;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Disposable;

import bigcash.poker.widgets.Emoji;

public class TextureDownloader implements Disposable {
    public TextureDownloader(){
    }

    public void downloadImage(String url, final Image urlImage){
        Pixmap.downloadFromUrl(url, new Pixmap.DownloadPixmapResponseListener() {
            @Override
            public void downloadComplete(Pixmap pixmap) {
                Texture texture=new Texture(pixmap);
                texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
                pixmap.dispose();
                urlImage.setDrawable(new TextureRegionDrawable(texture));
            }

            @Override
            public void downloadFailed(Throwable throwable) {

            }
        });
    }

    public void downloadEmoji(String name,final Emoji emoji){
        if (name==null || name.isEmpty())return;
        String url="https://1101993670.rsc.cdn77.org/img/emoji/" + name + ".png";
        Pixmap.downloadFromUrl(url, new Pixmap.DownloadPixmapResponseListener() {
            @Override
            public void downloadComplete(Pixmap pixmap) {
                Texture texture=new Texture(pixmap);
                texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
                pixmap.dispose();
                emoji.setEmoji(texture);
            }

            @Override
            public void downloadFailed(Throwable throwable) {

            }
        });
    }

    @Override
    public void dispose() {
    }
}
