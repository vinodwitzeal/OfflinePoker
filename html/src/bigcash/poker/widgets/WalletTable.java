package bigcash.poker.widgets;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class WalletTable extends Table {
    private TextureRegion centerRegion,leftRegion,rightRegion;
    private float leftWidth,rightWidth,centerWidth;
    private float leftX,rightX,centerX;
    public WalletTable(TextureRegion backgroundRegion){
        leftRegion=new TextureRegion(backgroundRegion,0,0,53,backgroundRegion.getRegionHeight());
        rightRegion=new TextureRegion(backgroundRegion,backgroundRegion.getRegionWidth()-53,0,53,backgroundRegion.getRegionHeight());
        centerRegion=new TextureRegion(backgroundRegion,54,0,backgroundRegion.getRegionWidth()-106,backgroundRegion.getRegionHeight());
    }

    @Override
    public void layout() {
        super.layout();

        float height=getHeight();
        leftWidth=height*leftRegion.getRegionWidth()/leftRegion.getRegionHeight();
        rightWidth=height*rightRegion.getRegionWidth()/rightRegion.getRegionHeight();

        leftX=0;
        rightX=getWidth()-rightWidth;
        centerWidth=getWidth()-leftWidth-rightWidth;
        centerX=leftX+leftWidth;
    }

    @Override
    protected void drawBackground(Batch batch, float parentAlpha, float x, float y) {
        Color color = getColor();
        batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
        batch.draw(leftRegion,x+leftX,y,leftWidth,getHeight());
        batch.draw(rightRegion,x+rightX,y,rightWidth,getHeight());
        batch.draw(centerRegion,x+centerX,y,centerWidth,getHeight());
    }
}
