package bigcash.poker.dialogs;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

import bigcash.poker.models.UIDialog;
import bigcash.poker.models.UIScreen;
import bigcash.poker.utils.AssetsLoader;


public class IDialog extends UIDialog {
    public TextureAtlas uiAtlas;
    public DataTable dataTable;
    public Table buttonTable;
    public NinePatchDrawable blueDrawable,greenDrawable,redDrawable;
    public IDialog(UIScreen screen) {
        super(screen);
        uiAtlas= AssetsLoader.instance().uiAtlas;
        NinePatchDrawable  background=new NinePatchDrawable(new NinePatch(uiAtlas.findRegion("bg_dialog_rounded"),20,20,20,20));
        dataTable=new DataTable(background);
        buttonTable=new Table();
        NinePatchDrawable buttonBackground=new NinePatchDrawable(new NinePatch(uiAtlas.findRegion("bg_bottom"),20,20,0,20));
        buttonTable.setBackground(buttonBackground);
        blueDrawable=new NinePatchDrawable(new NinePatch(uiAtlas.findRegion("btn_blue"),9,9,9,9));
        greenDrawable=new NinePatchDrawable(new NinePatch(uiAtlas.findRegion("btn_green"),9,9,9,9));
        redDrawable=new NinePatchDrawable(new NinePatch(uiAtlas.findRegion("btn_red"),9,9,9,9));
    }

    @Override
    public void init() {

    }

    @Override
    public void buildDialog() {

    }


    public class DataTable extends Table{
        private NinePatchDrawable background;
        public DataTable(NinePatchDrawable background){
            this.background=background;
        }

        @Override
        protected void drawBackground(Batch batch, float parentAlpha, float x, float y) {
            if (background!=null){
                Color color=getColor();
                batch.setColor(color.r,color.g,color.b,color.a*parentAlpha);
                background.draw(batch,x,y,getWidth(),getHeight());
            }
        }
    }
}

