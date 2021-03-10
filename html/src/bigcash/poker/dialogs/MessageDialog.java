package bigcash.poker.dialogs;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

import bigcash.poker.font.FontPool;
import bigcash.poker.font.FontType;
import bigcash.poker.models.UIScreen;
import bigcash.poker.network.GdxListener;
import bigcash.poker.utils.DrawableBuilder;

public class MessageDialog extends IDialog {
    private String title,message,buttonName;
    private GdxListener<String> listener;
    public MessageDialog(UIScreen screen, String title, String message, String buttonName, GdxListener<String> listener) {
        super(screen);
        this.title=title;
        this.message=message;
        this.buttonName=buttonName;
        this.listener = listener;
        buildDialog();
    }

    public MessageDialog(UIScreen screen, String title, String message, String buttonName) {
        this(screen, title, message, buttonName, new GdxListener<String>() {
            @Override
            public void onSuccess(String s) {

            }

            @Override
            public void onFail(String reason) {

            }

            @Override
            public void onError(String error) {

            }
        });
    }

    @Override
    public void init() {
    }

    @Override
    public void buildDialog() {
        float dialogWidth=width*0.9f;

        Label.LabelStyle titleStyle=new Label.LabelStyle();
        titleStyle.font= FontPool.obtain(FontType.ROBOTO_BOLD,10);
        titleStyle.fontColor= Color.BLACK;

        TextButton.TextButtonStyle buttonStyle=new TextButton.TextButtonStyle();
        buttonStyle.font=FontPool.obtain(FontType.ROBOTO_BOLD,8);
        buttonStyle.fontColor=Color.WHITE;
        buttonStyle.up=greenDrawable;
        buttonStyle.down=greenDrawable;
        buttonStyle.checked=greenDrawable;

        TextButton button=new TextButton(buttonName,buttonStyle);
        float buttonWidth=dialogWidth*0.7f;
        float buttonHeight=buttonStyle.font.getCapHeight()*3;
        float buttonPad=buttonStyle.font.getCapHeight();

        dataTable.add(new Label(title,titleStyle)).padTop(buttonHeight).row();

        Label.LabelStyle messageStyle=new Label.LabelStyle();
        messageStyle.font=FontPool.obtain(FontType.ROBOTO_REGULAR,7);
        messageStyle.fontColor=Color.DARK_GRAY;

        Label messageLabel=new Label(message,messageStyle);
        messageLabel.setAlignment(Align.center);
        messageLabel.setWrap(true);

        button.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                onButtonClicked();
                hide();
                listener.setSuccess("Success");
            }
        });

        dataTable.add(messageLabel).width(dialogWidth*0.9f).padTop(buttonHeight*0.25f).row();
        buttonTable.add(button).width(buttonWidth).height(buttonHeight).padTop(buttonPad*1.5f).padBottom(buttonPad);

        dataTable.add(buttonTable).width(dialogWidth).padTop(buttonHeight);





        Table backTable = new Table();
        float closeSize = width * 0.1f;
        float sidePad = closeSize * 0.4f;
        backTable.add(dataTable).width(dialogWidth).pad(sidePad, sidePad, sidePad, sidePad);


        Table closeTable = new Table();
        closeTable.top().right();
        TextureRegion closeTexture = uiAtlas.findRegion("icon_close");
        TextureRegionDrawable closeDrawable = DrawableBuilder.getDrawable(closeTexture, closeSize, closeSize);
        ImageButton closeButton = new ImageButton(closeDrawable, closeDrawable, closeDrawable);
        closeTable.add(closeButton).width(closeSize).height(closeSize);
        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hide();
            }
        });
        Stack stack = new Stack();
        stack.add(backTable);
        stack.add(closeTable);
        getContentTable().add(stack);
    }

    public void onButtonClicked(){

    }
}
