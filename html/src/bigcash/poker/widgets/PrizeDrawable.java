package bigcash.poker.widgets;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable;

public class PrizeDrawable extends BaseDrawable {
    private TextureRegion leftRegion;
    private NinePatch patch;
    private float aspectRatio;
    public PrizeDrawable(TextureRegion region,int left,int right){
        int totalWidth=region.getRegionWidth();
        int totalHeight=region.getRegionHeight();
        aspectRatio=(float)left/(float)totalHeight;
        leftRegion=new TextureRegion(region,0,0,left,totalHeight);
        TextureRegion centerRegion=new TextureRegion(region,left,0,totalWidth-left,totalHeight);
        patch=new NinePatch(centerRegion,1,right,right,right);
    }


    @Override
    public void draw(Batch batch, float x, float y, float width, float height) {
        float leftWidth=aspectRatio*height;
        float centerX=x+leftWidth;
        float centerWidth=width-leftWidth;
        batch.draw(leftRegion,x,y,leftWidth,height);
        patch.draw(batch,centerX,y,centerWidth,height);
    }
}
