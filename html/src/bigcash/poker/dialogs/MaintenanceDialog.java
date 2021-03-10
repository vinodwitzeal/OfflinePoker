package bigcash.poker.dialogs;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import bigcash.poker.font.FontPool;
import bigcash.poker.font.FontType;
import bigcash.poker.models.UIScreen;

public class MaintenanceDialog extends IDialog{
    public MaintenanceDialog(UIScreen screen) {
        super(screen);
        buildDialog();
    }

    @Override
    public void buildDialog() {
        float dialogWidth=width*0.9f;
        Table contentTable=getContentTable();

        Label.LabelStyle headingStyle=new Label.LabelStyle();
        headingStyle.font= FontPool.obtain(FontType.ROBOTO_BOLD,12);
        headingStyle.fontColor= Color.SCARLET;
        float headingPad=headingStyle.font.getCapHeight();
        dataTable.add(new Label("SERVER MAINTENANCE",headingStyle)).pad(headingPad).row();

        Label.LabelStyle messageStyle=new Label.LabelStyle();
        messageStyle.font=FontPool.obtain(FontType.ROBOTO_REGULAR,7);
        messageStyle.fontColor=Color.valueOf("595959");

        Label messageLabel=new Label("Server is under maintenance. Please wait for some time and check later.",messageStyle);
        messageLabel.setWrap(true);
        messageLabel.setAlignment(Align.center);

        dataTable.add(messageLabel).width(dialogWidth*0.8f).padBottom(headingPad).row();

        TextButton.TextButtonStyle closeButtonStyle=new TextButton.TextButtonStyle();
        closeButtonStyle.font=FontPool.obtain(FontType.ROBOTO_BOLD,6);
        closeButtonStyle.fontColor=Color.WHITE;
        closeButtonStyle.up=redDrawable;

        float buttonHeight=closeButtonStyle.font.getLineHeight()*3f;
        float buttonWidth=dialogWidth*0.5f;

        TextButton closeButton=new TextButton("Close",closeButtonStyle);
        closeButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hide();
            }
        });
        buttonTable.add(closeButton).width(buttonWidth).height(buttonHeight).padTop(headingPad).padBottom(headingPad/2f);

        dataTable.add(buttonTable).width(dialogWidth);
        contentTable.add(dataTable).width(dialogWidth);
    }
}
