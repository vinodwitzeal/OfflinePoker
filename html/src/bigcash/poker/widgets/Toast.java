package bigcash.poker.widgets;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import bigcash.poker.font.FontPool;
import bigcash.poker.font.FontType;
import bigcash.poker.models.UIScreen;
import bigcash.poker.utils.AssetsLoader;

public class Toast extends Label {
    private UIScreen screen;
    private float toastMaxWidth;
    private static GlyphLayout glyphLayout=new GlyphLayout();
    public Toast(String text,UIScreen screen){
        super(text,screen.toastStyle);
        this.screen=screen;
        this.toastMaxWidth=screen.width*0.8f;
    }

    public void show(float duration){
        pack();
        if (getWidth()>toastMaxWidth){
            setWidth(toastMaxWidth);
            setHeight(getStyle().font.getLineHeight()*2+2*getStyle().font.getCapHeight());
        }
        setPosition((screen.width-getWidth())/2f,screen.height*0.1f);
        addAction(Actions.sequence(Actions.fadeIn(0.1f),Actions.delay(duration),Actions.fadeOut(0.2f),Actions.removeActor()));
        screen.stage.addActor(this);
    }


    public static class ToastStyle extends LabelStyle{
        public ToastStyle(){
            font= FontPool.obtain(FontType.ROBOTO_REGULAR,5);
            fontColor= Color.WHITE;
            background=new EqualDrawable(AssetsLoader.instance().uiAtlas.findRegion("bg_toast"),72,font.getCapHeight());
        }
    }

    public static class EqualDrawable implements Drawable{
        private Drawable leftDrawable,rightDrawable,centerDrawable;
        private float aspectRatio,sideWidth,centerWidth,leftX,rightX,centerX;
        private float x,width,height,btHeight;
        public EqualDrawable(TextureRegion region,int side,float btHeight){
            this.btHeight=btHeight;
            int totalWidth=region.getRegionWidth();
            int totalHeight=region.getRegionHeight();
            TextureRegion leftRegion=new TextureRegion(region,1,1,side,totalHeight);
            TextureRegion rightRegion=new TextureRegion(region,totalWidth-side,1,side,totalHeight);
            TextureRegion centerRegion=new TextureRegion(region,side+1,1,totalWidth-2*side,totalHeight);
            leftDrawable=new TextureRegionDrawable(leftRegion);
            rightDrawable=new TextureRegionDrawable(rightRegion);
            centerDrawable=new NinePatchDrawable(new NinePatch(centerRegion,1,1,1,1));
            aspectRatio=(float) side/(float) totalHeight;
        }

        private void validate(float x,float y,float width,float height){
            if (this.width!=width || this.height!=height){
                sideWidth=aspectRatio*height;
            }

            if (this.x!=x){
                leftX=x-sideWidth;
                rightX=x+width;
                centerX=x;
            }
            this.x=x;
            this.width=width;
            this.height=height;
        }
        @Override
        public void draw(Batch batch, float x, float y, float width, float height) {
            validate(x,y,width,height);
            leftDrawable.draw(batch,leftX,y,sideWidth,height);
            rightDrawable.draw(batch,rightX,y,sideWidth,height);
            centerDrawable.draw(batch,centerX,y,width,height);
        }

        @Override
        public float getLeftWidth() {
            return 0;
        }

        @Override
        public void setLeftWidth(float leftWidth) {

        }

        @Override
        public float getRightWidth() {
            return 0;
        }

        @Override
        public void setRightWidth(float rightWidth) {

        }

        @Override
        public float getTopHeight() {
            return btHeight;
        }

        @Override
        public void setTopHeight(float topHeight) {

        }

        @Override
        public float getBottomHeight() {
            return btHeight;
        }

        @Override
        public void setBottomHeight(float bottomHeight) {

        }

        @Override
        public float getMinWidth() {
            return 0;
        }

        @Override
        public void setMinWidth(float minWidth) {

        }

        @Override
        public float getMinHeight() {
            return 0;
        }

        @Override
        public void setMinHeight(float minHeight) {

        }
    }
}
