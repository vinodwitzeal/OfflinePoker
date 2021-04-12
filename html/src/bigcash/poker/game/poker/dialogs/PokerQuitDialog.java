package bigcash.poker.game.poker.dialogs;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

import bigcash.poker.font.FontPool;
import bigcash.poker.font.FontType;
import bigcash.poker.game.poker.PokerAbstractScreen;
import bigcash.poker.models.UIDialog;
import bigcash.poker.utils.DrawableBuilder;
import bigcash.poker.utils.PokerUtils;
import bigcash.poker.widgets.StyledLabel;

public class PokerQuitDialog extends UIDialog {

    private TextureAtlas atlas;
    private float creditBalance;

    public PokerQuitDialog(PokerAbstractScreen screen, TextureAtlas atlas, float creditBalance) {
        super(screen);
        this.creditBalance=creditBalance;
        this.atlas=atlas;
        this.density=this.width/360.0f;
        dismissOnBack(true);
        buildDialog();
    }

    @Override
    public void init() {
    }

    @Override
    public void buildDialog() {
        float dialogWidth = width * 0.8f;
        TextureRegion logoTexture = atlas.findRegion("txt_exit");
        float logoWidth = dialogWidth * 0.25f;
        float logoHeight = logoWidth * logoTexture.getRegionHeight() / logoTexture.getRegionWidth();

        Table dataTable = buildDataTable();
        dataTable.add(new Image(logoTexture)).width(logoWidth).padBottom(5*density).padTop(5*density).height(logoHeight).row();
        StyledLabel.StyledLabelStyle winningLabelStyle = new StyledLabel.StyledLabelStyle(FontType.ROBOTO_BOLD, 13);
        winningLabelStyle.fontColor = Color.valueOf("ff8604");

        Label.LabelStyle headingStyle = new Label.LabelStyle();
        headingStyle.font = FontPool.obtain(FontType.ROBOTO_BOLD, 7);
        headingStyle.fontColor = Color.WHITE;

        Label.LabelStyle msgStyle = new Label.LabelStyle();
        msgStyle.font = FontPool.obtain(FontType.ROBOTO_BOLD, 5);
        msgStyle.fontColor = Color.valueOf("#ff9c00");
        NinePatchDrawable detailsbg = new NinePatchDrawable(new NinePatch(atlas.findRegion("details_bg"), 7, 7, 7, 7));
        Table detailsTable=new Table();
        detailsTable.setBackground(detailsbg);
        Label titleLable=new Label("YOU WILL LOOSE",headingStyle);
        titleLable.setWrap(true);
        titleLable.setAlignment(Align.center);
        if(creditBalance>0) {
            detailsTable.add(titleLable).padTop(4 * density).width(dialogWidth * 0.55f).row();
        }
        Label winningLable=new Label("\u20b9 "+ PokerUtils.getValue(creditBalance),winningLabelStyle);
        winningLable.setWrap(true);
        winningLable.setAlignment(Align.center);
        if(creditBalance>0.0f) {
            detailsTable.add(winningLable).padTop(4 * density).row();
        }
        Label alertLable=new Label("ARE YOU SURE YOU WANT TO EXIT ?",headingStyle);
        alertLable.setWrap(true);
        alertLable.setAlignment(Align.center);
        detailsTable.add(alertLable).padTop(8*density).row();

        Table bottomTable=new Table();
        float okHeight=height*.14f;
        float okWidth;
        TextureRegion exitTexture = atlas.findRegion("btn_exit");
        okWidth=okHeight*exitTexture.getRegionWidth()/exitTexture.getRegionHeight();;
        TextureRegionDrawable exitDrawable = DrawableBuilder.getDrawable(exitTexture, okWidth, okHeight);
        ImageButton exitButton = new ImageButton(exitDrawable, exitDrawable, exitDrawable);
        bottomTable.add(exitButton).width(okWidth*.7f).height(okHeight).align(Align.left);
        exitButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {

                onLeave();
            }
        });

        TextureRegion addTexture = atlas.findRegion("btn_resume");
        okWidth=okHeight*addTexture.getRegionWidth()/addTexture.getRegionHeight();;
        TextureRegionDrawable addDrawable = DrawableBuilder.getDrawable(addTexture, okWidth, okHeight);
        ImageButton addButton = new ImageButton(addDrawable, addDrawable, addDrawable);
        bottomTable.add(addButton).width(okWidth*.7f).height(okHeight).align(Align.right).expandX();
        addButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hide();
            }
        });

        detailsTable.add(bottomTable).width(dialogWidth*.35f).padTop(height*.08f).padBottom(4*density).row();

        dataTable.add(detailsTable).width(dialogWidth*.57f).padBottom(8*density);

        float topPad = 0;
        Table backTable = new Table();
        float closeSize = height * 0.12f;
        float sidePad = closeSize * 0.4f;
        backTable.add(dataTable).width(dialogWidth*.67f).pad(topPad, sidePad, 0, sidePad);


        Table closeTable = new Table();
        closeTable.top().right();
        TextureRegion closeTexture = atlas.findRegion("btn_cross");
        TextureRegionDrawable closeDrawable = DrawableBuilder.getDrawable(closeTexture, closeSize, closeSize);
        ImageButton closeButton = new ImageButton(closeDrawable, closeDrawable, closeDrawable);
        closeTable.add(closeButton).width(closeSize).height(closeSize).padTop(topPad - closeSize * 0.4f);
        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hide();
            }
        });
        Stack stack = new Stack();
        stack.add(backTable);
        stack.add(closeTable);
        Table contentTable=getContentTable();
        contentTable.add(stack).expand();
    }

    public void onLeave(){}

    public Table buildDataTable() {
        Table dataTable = new Table();
        NinePatch bgNinePatch = new NinePatch(atlas.findRegion("bg_dialog"), 10, 10, 10, 10);
        NinePatchDrawable background = new NinePatchDrawable(bgNinePatch);
        background.setMinWidth(60);
        background.setMinHeight(60);
        dataTable.setBackground(background);
        return dataTable;
    }
}