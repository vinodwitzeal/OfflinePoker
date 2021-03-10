package bigcash.poker.widgets;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

public class MagicTable extends Table {
    private Drawable backgroundDrawable;
    private Color backgroundColor;
    public MagicTable(Drawable backgroundDrawable,String backgroundColorValue){
        this.backgroundDrawable=backgroundDrawable;
        this.backgroundColor=Color.valueOf(backgroundColorValue);
    }
    public MagicTable(Drawable backgroundDrawable){
        this(backgroundDrawable,"ffffff");
    }

    public void changeColor(Color color){
        backgroundColor=color;
    }

    @Override
    protected void drawBackground(Batch batch, float parentAlpha, float x, float y) {
        if (backgroundDrawable!=null){
            Color color=batch.getColor();
            batch.setColor(backgroundColor.r,backgroundColor.g,backgroundColor.b,backgroundColor.a*parentAlpha);
            backgroundDrawable.draw(batch,x,y,getWidth(),getHeight());
            batch.setColor(color);
        }
    }
}