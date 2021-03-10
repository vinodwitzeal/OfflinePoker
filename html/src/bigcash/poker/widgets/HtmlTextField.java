package bigcash.poker.widgets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.backends.gwt.GwtGraphics;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.TextBoxBase;
import com.google.gwt.user.client.ui.VerticalPanel;

import bigcash.poker.gwt.PokerGame;
import bigcash.poker.utils.PokerUtils;

public class HtmlTextField implements TextField.OnscreenKeyboard {
    private static ResizeHandler handler;
    private static TextFieldDialog shownDialog;

    private static void attachHandler(TextFieldDialog dialog) {
        shownDialog = dialog;
        if (handler == null) {
            handler = new ResizeHandler() {
                @Override
                public void onResize(ResizeEvent event) {
                    shownDialog.center();
                }
            };
            Window.addResizeHandler(handler);
        }
    }

    public static void setTextInput(TextField textField,String message, int type) {
        if (PokerUtils.isMobile()) {
            textField.setOnscreenKeyboard(new HtmlTextField(textField,message, type));
        }
    }

    public static final int TEXT = 0;
    public static final int PASSWORD = 2;
    public static final int NUMBER = 3;
    public static final int EMAIL = 4;
    public static final int AREA=5;


    public TextFieldDialog textFieldDialog;
    public TextField textField;

    private HtmlTextField(TextField textField,String message, int type) {
        this.textField = textField;
        textField.getStyle().cursor = null;
        textFieldDialog = new TextFieldDialog(message,type);
        this.textField.clearListeners();
        this.textField.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                textField.getOnscreenKeyboard().show(true);
            }
        });
    }

    @Override
    public void show(boolean visible) {
        if (visible) {
            if (Gdx.graphics.isFullscreen()){
                PokerGame pokerGame=(PokerGame)Gdx.app.getApplicationListener();
                Graphics.DisplayMode displayMode = Gdx.graphics.getDisplayMode();
                pokerGame.appConfig.fullscreenOrientation=null;
                Gdx.graphics.setWindowedMode(displayMode.width, displayMode.height);
                pokerGame.getScreen().normalScreenRunnable(new Runnable() {
                    @Override
                    public void run() {
                        attachHandler(textFieldDialog);
                        textFieldDialog.textBox.setText(textField.getText());
                        textFieldDialog.center();
                        textFieldDialog.textBox.setFocus(true);
                    }
                });
            }else {
                attachHandler(textFieldDialog);
                textFieldDialog.textBox.setText(textField.getText());
                textFieldDialog.center();
                textFieldDialog.textBox.setFocus(true);
            }

        }
    }

    public class TextFieldDialog extends PopupPanel {
        private TextBoxBase textBox;

        public TextFieldDialog(String message,int textType) {
            super(false);
            setModal(true);
            setGlassEnabled(true);
            setStyleName("input-dialog");
            VerticalPanel verticalPanel = new VerticalPanel();
            verticalPanel.setWidth("100%");

            Label titleLabel = new Label();
            titleLabel.setStyleName("input-title");
            titleLabel.setText(message);
            textBox = new TextBox();
            textBox.setStyleName("input-text");
            textBox.setWidth("100%");
            switch (textType) {
                case NUMBER:
                    textBox.getElement().setAttribute("type", "number");
                    break;
                case EMAIL:
                    textBox.getElement().setAttribute("type", "email");
                    break;
                case PASSWORD:
                    textBox=new PasswordTextBox();
                    textBox.setStyleName("input-text");
                    break;
                case AREA:
                    textBox=new TextArea();
                    textBox.setStyleName("input-area");
                    break;
                default:
                    textBox.getElement().setAttribute("type", "text");
            }

            HorizontalPanel horizontalPanel = new HorizontalPanel();
            horizontalPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
//            horizontalPanel.setWidth("100%");
            Button cancelButton = new Button();
            cancelButton.setText("Cancel");
            cancelButton.setStyleName("btn-cancel");

            horizontalPanel.add(cancelButton);

            Button submitButton = new Button();
            submitButton.setStyleName("btn-submit");
            submitButton.setText("Submit");
            horizontalPanel.add(submitButton);


            verticalPanel.add(titleLabel);
            verticalPanel.add(textBox);
            VerticalPanel buttonPanel=new VerticalPanel();
            buttonPanel.setWidth("100%");
            buttonPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
            buttonPanel.add(horizontalPanel);
            verticalPanel.add(buttonPanel);

            submitButton.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    textField.setText(textBox.getText());
                    hide();
                }
            });

            cancelButton.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    hide();
                }
            });
            setWidget(verticalPanel);
            int width=(int)(Gdx.graphics.getWidth()/ GwtGraphics.getNativeScreenDensity())-40;
            setWidth(width+"px");
        }

        @Override
        public void hide() {
            super.hide();
            shownDialog = null;
        }

    }
}
