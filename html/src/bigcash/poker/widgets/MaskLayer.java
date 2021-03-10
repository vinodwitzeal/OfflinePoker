package bigcash.poker.widgets;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class MaskLayer {
    private LayerMaskCShader layerMaskCShader;
    private TextureRegion region;
    private Texture texture;
    private float u1,v1,u2,v2;
    private Vector2 maskMin,maskMax, glMin, glMax;
    private float x,y,width,height;
    public MaskLayer(LayerMaskCShader layerMaskCShader, TextureRegion region){
        this.layerMaskCShader=layerMaskCShader;
        this.region=region;
        this.texture=region.getTexture();
        this.u1=region.getU();
        this.v1=region.getV();
        this.u2=region.getU2();
        this.v2=region.getV2();
        maskMin=new Vector2(u1,v2);
        maskMax=new Vector2(u2,v1);
        glMin =new Vector2();
        glMax =new Vector2();
    }

    public void setPosition(float x,float y){
        this.x=x;
        this.y=y;
    }

    public void setSize(float width,float height){
        this.width=width;
        this.height=height;
    }


    public void begin(Batch batch, float viewportWidth, float viewportHeight){
       glMin.set(getGlPostion(x,viewportWidth),getGlPostion(y,viewportHeight));
       glMax.set(getGlPostion(x+width,viewportWidth),getGlPostion(y+height,viewportHeight));
       layerMaskCShader.begin(batch,texture,maskMin,maskMax,glMin,glMax);
    }


    public float getGlPostion(float screen,float viewportSize){
        screen=screen-viewportSize/2f;
        return 2*screen/viewportSize;
    }


    public void end(Batch batch){
        layerMaskCShader.end(batch);
    }


}
