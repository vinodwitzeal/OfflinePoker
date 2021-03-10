package bigcash.poker.dialogs;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import bigcash.poker.constants.Constant;
import bigcash.poker.font.Font;
import bigcash.poker.font.FontPool;
import bigcash.poker.font.FontType;
import bigcash.poker.models.UIScreen;
import bigcash.poker.network.ApiHandler;
import bigcash.poker.network.GdxListener;
import bigcash.poker.widgets.HtmlTextField;

public class FeedbackDialog extends IDialog{
    public FeedbackDialog(UIScreen screen) {
        super(screen);
        buildDialog();
    }

    @Override
    public void buildDialog() {
        float dialogWidth=width*0.85f;
        Table contentTable=getContentTable();

        Label.LabelStyle headingStyle=new Label.LabelStyle();
        headingStyle.font= FontPool.obtain(FontType.ROBOTO_BOLD,12);
        headingStyle.fontColor= Color.SCARLET;
        float headingPad=headingStyle.font.getCapHeight();
        dataTable.add(new Label("Feedback",headingStyle)).pad(headingPad).row();
        Font textFieldFont = FontPool.obtain(FontType.ROBOTO_BOLD, 10);

        NinePatchDrawable textFieldbackground = new NinePatchDrawable(new NinePatch(uiAtlas.findRegion("bg_sand"), 14, 14, 11, 15));
        textFieldbackground.setMinWidth(15);
        textFieldbackground.setMinHeight(15);


        TextField.TextFieldStyle textFieldStyle = new TextField.TextFieldStyle();
        textFieldStyle.font = textFieldFont;
        textFieldStyle.fontColor = Color.valueOf("f29e21");
        textFieldStyle.messageFontColor = Color.LIGHT_GRAY;
        textFieldStyle.background = textFieldbackground;


        final TextField mobileTextField = new TextField("", textFieldStyle);
        mobileTextField.setMessageText("Enter Mobile Number");
        if (Constant.userProfile.getMsisdn() != null) {
            mobileTextField.setText(Constant.userProfile.getMsisdn());
        }

        HtmlTextField.setTextInput(mobileTextField,"Enter Mobile Number",HtmlTextField.NUMBER);
        dataTable.add(mobileTextField).width(dialogWidth * 0.8f).padBottom(10 * density).expandX().fillX().row();

        final TextArea feedbackArea = new TextArea("", textFieldStyle);
        feedbackArea.setMessageText("Enter Message");
        dataTable.add(feedbackArea).width(dialogWidth * 0.8f).height(dialogWidth * 0.8f).padBottom(10 * density).expandX().fillX().row();

        NinePatchDrawable ninePatchDrawable = new NinePatchDrawable(new NinePatch(uiAtlas.findRegion("button_blue"), 9, 9, 12, 16));
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = FontPool.obtain(FontType.ROBOTO_BOLD, 7);
        buttonStyle.fontColor = Color.WHITE;
        buttonStyle.downFontColor = Color.LIGHT_GRAY;
        buttonStyle.up = ninePatchDrawable;
        buttonStyle.down = ninePatchDrawable;

        HtmlTextField.setTextInput(feedbackArea,"Enter Message",HtmlTextField.AREA);

        float buttonHeight=buttonStyle.font.getLineHeight()*2f;
        float buttonWidth=dialogWidth*0.5f;

        TextButton submitButton = new TextButton("Submit", buttonStyle);
        submitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!mobileTextField.getText().matches("") && mobileTextField.getText().length() == 10) {
                    if (!feedbackArea.getText().matches("")) {
                        final ProcessDialog processDialog = new ProcessDialog(screen, "Please wait..");
                        processDialog.show(getStage());
                        ApiHandler.callFeedbackApi(feedbackArea.getText(), mobileTextField.getText(), new GdxListener<String>() {
                            @Override
                            public void onSuccess(String s) {
                                processDialog.hide();
                                hide();
                            }

                            @Override
                            public void onFail(String reason) {
                                processDialog.hide();
                                toast(reason);
                            }

                            @Override
                            public void onError(String errorMessage) {
                                processDialog.hide();
                                toast(errorMessage);
                            }
                        });
                    } else {
                        toast("Enter feedback..");
                    }
                } else {
                    toast("Enter valid no...");
                }
            }
        });

        dataTable.add(submitButton).width(buttonWidth).height(buttonHeight).padBottom(4 * density);

        TextureRegion closeTexture=uiAtlas.findRegion("btn_close");
        float closeSize=buttonHeight;
        Table closeTable=new Table();
        closeTable.top().right();
        Button closeButton=new Button(new TextureRegionDrawable(closeTexture));
        closeButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hide();
            }
        });
        closeTable.add(closeButton).width(closeSize).height(closeSize);

        Table backTable=new Table();
        backTable.add(dataTable).width(dialogWidth).padLeft(closeSize/2f).padRight(closeSize/2f).padTop(closeSize/2f);
        Stack stack=new Stack();
        stack.add(backTable);
        stack.add(closeTable);
        contentTable.add(stack);

    }
}
