package bigcash.poker.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

import bigcash.poker.widgets.LayerMaskCShader;
import bigcash.poker.widgets.MaskLayer;

public class AssetsLoader {
    public Window.WindowStyle windowStyle;
    public NinePatchDrawable bgDialog;
    public TextureAtlas splashAtlas,processAtlas,uiAtlas,gameAtlas;
    public Sound betSound,foldSound,tickSound,checkSound;
    public Texture bgSelectedAmount,screenBackground;
    public MaskLayer circleMaskLayer,squareMaskLayer;
    public TextureRegion circleRegion,userRegion;
    private static AssetsLoader instance;

    public static AssetsLoader instance(){
        if (instance==null){
            instance=new AssetsLoader();
            instance.loadRemaining();
        }

        return instance;
    }

    private AssetsLoader(){
        bgDialog = new DrawableBuilder(10, 10).color(Color.valueOf("000000de")).createNinePatch();
        windowStyle = new Window.WindowStyle();
        windowStyle.titleFont = new BitmapFont();
        windowStyle.titleFontColor = Color.WHITE;
//        splashAtlas=new TextureAtlas(Gdx.files.internal("splash.atlas"));
        processAtlas=new TextureAtlas(Gdx.files.internal("process.atlas"));
    }

    public void loadRemaining(){
        uiAtlas=new TextureAtlas("game/ui.atlas");
        gameAtlas=new TextureAtlas("game/poker.atlas");
        bgSelectedAmount = new Texture(Gdx.files.internal("game/bg_selectedAmount.png"));
        bgSelectedAmount.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        betSound = Gdx.audio.newSound(Gdx.files.internal("game/sounds/sound_bet.wav"));
        foldSound = Gdx.audio.newSound(Gdx.files.internal("game/sounds/sound_fold.wav"));
        tickSound = Gdx.audio.newSound(Gdx.files.internal("game/sounds/tick.ogg"));
        checkSound=Gdx.audio.newSound(Gdx.files.internal("game/sounds/checkSound.wav"));
        screenBackground=new Texture("game/bg_screen.png");
        screenBackground.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        circleRegion=gameAtlas.findRegion("circle");
        userRegion=gameAtlas.findRegion("img_user");
        circleMaskLayer=new MaskLayer(new LayerMaskCShader(),circleRegion);
        squareMaskLayer=new MaskLayer(new LayerMaskCShader(),uiAtlas.findRegion("bg_sky"));
    }
}
