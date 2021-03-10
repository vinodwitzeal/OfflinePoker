package bigcash.poker.dialogs;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Align;

import bigcash.poker.font.FontPool;
import bigcash.poker.font.FontType;
import bigcash.poker.models.UIDialog;
import bigcash.poker.models.UIScreen;
import bigcash.poker.utils.AssetsLoader;

public class RedeemInfoDialog extends UIDialog {
    private TextureAtlas uiAtlas;
    public RedeemInfoDialog(UIScreen screen) {
        super(screen);
        this.uiAtlas= AssetsLoader.instance().uiAtlas;
        buildDialog();
    }

    @Override
    public void init() {

    }

    @Override
    public void buildDialog() {
        float pad = height * 0.04f;
        float dialogWidth = width * 0.95f;
        float dialogHeight = height * 0.75f;
        Table dataTable = new Table();
        dataTable.padTop(16 * density);
        dataTable.padBottom(16 * density);
        dataTable.setBackground(new NinePatchDrawable(new NinePatch(uiAtlas.findRegion("bg_content_full"), 8, 8, 8, 8)));
        Label.LabelStyle headerStyle = new Label.LabelStyle();
        headerStyle.font = FontPool.obtain(FontType.ROBOTO_BOLD, 6.75f);
        headerStyle.fontColor = Color.valueOf("109003");

        Label.LabelStyle numberStyle = new Label.LabelStyle();
        numberStyle.font = FontPool.obtain(FontType.ROBOTO_BOLD, 7f);
        numberStyle.fontColor = Color.valueOf("000000");

        TextureRegion winningTexture = uiAtlas.findRegion("icon_winning_cash");
        TextureRegion unUtilizedTexture = uiAtlas.findRegion("icon_unutilized");
        TextureRegion closeTexture = uiAtlas.findRegion("btn_grey_close");
        float winningHeight = headerStyle.font.getCapHeight() * 1.7f;
        float winningWidth = winningHeight * winningTexture.getRegionWidth() / winningTexture.getRegionHeight();

        Table headerTable = new Table();
        headerTable.add(new Image(winningTexture)).width(winningWidth).height(winningHeight);

        dataTable.add(headerTable).padTop(2 * density).padBottom(10*density).row();

        Label.LabelStyle messageStyle = new Label.LabelStyle();
        messageStyle.font = FontPool.obtain(FontType.ROBOTO_REGULAR, 6.5f);
        messageStyle.fontColor = Color.DARK_GRAY;
        String msg="WINNING CASH is the Cash that you have won in BIG CASH by playing Cash Contest. \n \n" +
                "You can use WINNING CASH with Un-Utilized cash to pay for Cash Contest. \n\n" +
                "You can withdraw your Winning Cash.\n" +
                "";
        Label messageLabel = new Label(msg, messageStyle);
        messageLabel.setWrap(true);
        messageLabel.setAlignment(Align.center);
        dataTable.add(messageLabel).width(dialogWidth * 0.8f).padTop(7 * density).row();



        Table unUtilizedTable = new Table();
        float unutilizedHeight = headerStyle.font.getCapHeight() * 1.7f;
        float unutilizedWidth = unutilizedHeight * unUtilizedTexture.getRegionWidth() / unUtilizedTexture.getRegionHeight();
        unUtilizedTable.add(new Image(unUtilizedTexture)).width(unutilizedWidth).height(unutilizedHeight);
        dataTable.add(unUtilizedTable).padTop(10 * density).padBottom(10*density).row();

        String msg1="UN-UTILIZED CASH is the Cash that you have added to your BIG CASH wallet and winning of Token Contest & Bonus.\n\n " +
                "You can use UN-UTILIZED CASH with Winning Cash to pay for Cash Contest. \n\n" +
                "You cannot withdraw your Un-Utilized Cash.\n";
        Label unUtilizedmessageLabel = new Label(msg1, messageStyle);
        unUtilizedmessageLabel.setWrap(true);
        unUtilizedmessageLabel.setAlignment(Align.center);
        dataTable.add(unUtilizedmessageLabel).width(dialogWidth * 0.8f).padTop(7 * density).row();
        float closeWidth=dialogWidth*0.6f;
        float closeHeight=closeWidth*closeTexture.getRegionHeight()/closeTexture.getRegionWidth();
        Image closeButton=new Image(closeTexture);
        closeButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hide();
            }
        });

        dataTable.add(closeButton).width(closeWidth).height(closeHeight).padTop(10*density).padBottom(1 * density).row();


        float padtop = height * .1f;
        Table backTable = new Table();
        float closeSize = width * 0.1f;
        float sidePad = width * 0.2f;
        backTable.add(dataTable).width(width * 0.9f).pad(padtop, sidePad, sidePad, sidePad);

        Stack stack = new Stack();
        stack.add(backTable);
//        if (!isproceed)
//            stack.add(closeTable);
        getContentTable().add(stack).width(dialogWidth);
    }
}
