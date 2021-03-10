package bigcash.poker.dialogs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

import bigcash.poker.font.Font;
import bigcash.poker.font.FontPool;
import bigcash.poker.font.FontType;
import bigcash.poker.models.UIDialog;
import bigcash.poker.models.UIScreen;
import bigcash.poker.network.ApiHandler;
import bigcash.poker.network.GdxListener;
import bigcash.poker.utils.AssetsLoader;
import bigcash.poker.utils.DrawableBuilder;
import bigcash.poker.utils.TextureDrawable;
import bigcash.poker.widgets.HtmlTextField;

public abstract class OtpDialog extends UIDialog {
    private TextureAtlas uiAtlas;
    private Label msgLable;
    private TextField mobileTextField;
    private boolean isproceed, isNumberChanged = false;
    private Label errorlable;
    private Table errorTable;
    private String mobileNo;

    public OtpDialog(UIScreen screen, String mobileNo, boolean isproceed) {
        super(screen);
        dismissOnBack(true);
        this.isproceed = isproceed;
        this.mobileNo = mobileNo;
        uiAtlas = AssetsLoader.instance().uiAtlas;
        buildDialog();
    }

    @Override
    public void init() {
    }

    @Override
    public void buildDialog() {
        float dialogWidth = width * .95f;
        Table contentTable = getContentTable();
        contentTable.top();
        NinePatchDrawable grayBackground = new DrawableBuilder(20, 20).color(Color.valueOf("d8d5df")).createNinePatch();
        contentTable.setBackground(grayBackground);

        Table headerTable = new Table();
        NinePatchDrawable headerBackground = new NinePatchDrawable(new NinePatch(uiAtlas.findRegion("bg_header_bar"), 1, 1, 1, 6));
        headerTable.setBackground(headerBackground);

        Label.LabelStyle headerLabelStyle = new Label.LabelStyle();
        headerLabelStyle.font = FontPool.obtain(FontType.ROBOTO_BOLD, 7);
        headerLabelStyle.fontColor = Color.WHITE;
        float walletTableHeight = headerLabelStyle.font.getLineHeight() * 1.6f;

        Label.LabelStyle titleStyle = new Label.LabelStyle();
        titleStyle.font = FontPool.obtain(FontType.ROBOTO_BOLD, 9);
        titleStyle.fontColor = Color.WHITE;

        TextureRegion backTexture = uiAtlas.findRegion("btn_wback");
        float backHeight = headerLabelStyle.font.getLineHeight();
        float backWidth = backHeight * backTexture.getRegionWidth() / backTexture.getRegionHeight();
        TextureRegionDrawable menuDrawable = TextureDrawable.getDrawable(backTexture, backWidth, backHeight);
        final Button backButton = new Button(menuDrawable, menuDrawable, menuDrawable);
        headerTable.add(backButton).width(backWidth).height(backHeight).padLeft(8 * density).padRight(16 * density);

        headerTable.add(new Label("Number Verify", titleStyle)).expandX().align(Align.left);

        float headerHeight = walletTableHeight * 2;
        contentTable.add(headerTable).width(width).height(headerHeight).row();
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hide();
            }
        });

        Label.LabelStyle inviteTextStyle = new Label.LabelStyle();
        inviteTextStyle.font = FontPool.obtain(FontType.ROBOTO_REGULAR, 7);
        inviteTextStyle.fontColor = Color.DARK_GRAY;
        NinePatchDrawable whiteBackground = new NinePatchDrawable(new NinePatch(uiAtlas.findRegion("bg_content_full"), 8, 8, 8, 8));

        Label.LabelStyle bigLabelStyle = new Label.LabelStyle();
        bigLabelStyle.font = FontPool.obtain(FontType.ROBOTO_BOLD, 8);
        bigLabelStyle.fontColor = Color.valueOf("535353");

        Label.LabelStyle winningLabelStyle = new Label.LabelStyle();
        winningLabelStyle.font = FontPool.obtain(FontType.ROBOTO_BOLD, 6);
        winningLabelStyle.fontColor = Color.valueOf("f3821d");

        Label.LabelStyle smallLabelStyle = new Label.LabelStyle();
        smallLabelStyle.font = FontPool.obtain(FontType.ROBOTO_BOLD, 4);
        smallLabelStyle.fontColor = Color.valueOf("535353");

        Label.LabelStyle bigWhiteLabelStyle = new Label.LabelStyle();
        bigWhiteLabelStyle.font = FontPool.obtain(FontType.ROBOTO_BOLD, 8);
        bigWhiteLabelStyle.fontColor = Color.WHITE;

        Table detailsTable = new Table();
        detailsTable.top();
        detailsTable.setBackground(whiteBackground);
        msgLable = new Label("", bigLabelStyle);
        msgLable.setAlignment(Align.center);
        msgLable.setWrap(true);
        detailsTable.add(msgLable).width(dialogWidth * .8f).padTop(20 * density).row();

        Label.LabelStyle numberStyle = new Label.LabelStyle();
        numberStyle.font = FontPool.obtain(FontType.ROBOTO_BOLD, 7f);
        numberStyle.fontColor = Color.valueOf("000000");

        Label numberlable = new Label(mobileNo, numberStyle);
        numberlable.setAlignment(Align.left);


        // create text field
        Font dropDownFont = FontPool.obtain(FontType.ROBOTO_BOLD, 9);

        TextureRegionDrawable cursorDrawable = new DrawableBuilder(4, (int) dropDownFont.getCapHeight() * 2).color(Color.valueOf("f29e21")).createDrawable();

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = dropDownFont;
        labelStyle.fontColor = Color.GRAY;


        Font otpFont = FontPool.obtain(FontType.ROBOTO_REGULAR, 4);
        Label.LabelStyle otpStyle = new Label.LabelStyle();
        otpStyle.font = otpFont;
        otpStyle.fontColor = Color.GRAY;


        Font ResendFont = FontPool.obtain(FontType.ROBOTO_BOLD, 5);
        Label.LabelStyle resendStyle = new Label.LabelStyle();
        resendStyle.font = ResendFont;
        resendStyle.fontColor = Color.valueOf("109003");

        Font AlertFont = FontPool.obtain(FontType.ROBOTO_BOLD, 6);
        Label.LabelStyle alertStyle = new Label.LabelStyle();
        alertStyle.font = AlertFont;
        alertStyle.fontColor = Color.valueOf("f12424");

        TextField.TextFieldStyle textFieldStyle = new TextField.TextFieldStyle();
        textFieldStyle.font = dropDownFont;
        textFieldStyle.fontColor = Color.DARK_GRAY;
        textFieldStyle.messageFontColor = Color.valueOf("797575");
        textFieldStyle.cursor = cursorDrawable;

        NinePatchDrawable dropDownBackground = new NinePatchDrawable(new NinePatch(uiAtlas.findRegion("bg_text"), 3, 3, 3, 3));
        dropDownBackground.setMinWidth(15);
        dropDownBackground.setMinHeight(15);

        NinePatchDrawable otpBackground = new NinePatchDrawable(new NinePatch(uiAtlas.findRegion("otp_bg"), 3, 3, 3, 3));
        otpBackground.setMinWidth(15);
        otpBackground.setMinHeight(15);
        NinePatchDrawable prefixBackground = new NinePatchDrawable(new NinePatch(uiAtlas.findRegion("bg_gray"), 3, 3, 3, 3));
        prefixBackground.setMinWidth(15);
        prefixBackground.setMinHeight(15);
        Table mobileTable = new Table();


        if (isproceed) {
            msgLable.setText("Enter your mobile number to verify");
            mobileTable.setBackground(dropDownBackground);
            Table prefixTable = new Table();
            prefixTable.setBackground(dropDownBackground);
            prefixTable.add(new Label("+91", labelStyle)).pad(4 * density);
            prefixTable.pack();
            mobileTextField = new TextField("", textFieldStyle);
            mobileTextField.setText(mobileNo);
            TextField.TextFieldFilter.DigitsOnlyFilter digitsOnlyFilter = new TextField.TextFieldFilter.DigitsOnlyFilter();
            mobileTextField.setTextFieldFilter(digitsOnlyFilter);
            mobileTextField.setMaxLength(10);
            mobileTextField.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (!isNumberChanged) {
                        isNumberChanged = true;
                        mobileTextField.setText("");
                    }
                    updateError("", true);
                }
            });
            HtmlTextField.setTextInput(mobileTextField,"Enter Mobile No.", HtmlTextField.NUMBER);
            mobileTable.add(mobileTextField).expandX().fillX().pad(4 * density);
            Table numberTable = new Table();
            numberTable.padLeft(density * 10);
            numberTable.add(prefixTable);
            numberTable.add(mobileTable).expandX().fillX().align(Align.left);
            detailsTable.add(numberTable).width(dialogWidth * 0.75f).padLeft(prefixTable.getWidth() / 2).padTop(12 * density).align(Align.left).row();
        } else {
            msgLable.setText("OTP send successfully to your " + mobileNo + " number");
//            detailsTable.add(numberlable).align(Align.left).padLeft(density * 50f).padTop(10 * density).row();
            mobileTable.setBackground(otpBackground);
            mobileTextField = new TextField("", textFieldStyle);
            mobileTextField.setMessageText("OTP");
            mobileTextField.setAlignment(Align.center);
            mobileTextField.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    updateError("", true);
                }
            });
            HtmlTextField.setTextInput(mobileTextField,"Enter OTP", HtmlTextField.NUMBER);
            mobileTable.add(mobileTextField).pad(2 * density).expandX().fillX();
            detailsTable.add(mobileTable).width(dialogWidth * 0.4f).padTop(12 * density).expandX().fillX().align(Align.center).row();
            Table otptable = new Table();
            otptable.add(new Label("Didn't received the OTP ?", otpStyle));
            final Label resendLable = new Label("RESEND", resendStyle);
            resendLable.setTouchable(Touchable.enabled);
            resendLable.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    openOtpDialog(mobileNo);
                }
            });
            otptable.add(resendLable).padLeft(1 * density);
            detailsTable.add(otptable).width(dialogWidth * 0.4f).padTop(7 * density).expandX().fillX().align(Align.center).row();
        }
        errorTable = new Table();
        errorlable = new Label("", alertStyle);
        errorlable.setAlignment(Align.center);
        errorlable.setWrap(true);
        errorTable.add(errorlable).width(dialogWidth * 0.9f);
        detailsTable.add(errorTable).width(dialogWidth * 0.9f).fillX().padTop(4 * density).row();


        if (isproceed) {
            TextButton proceed = getSaveButton("PROCEED");

            Table buttonTable = new Table();
            buttonTable.add(proceed).width(dialogWidth * .6f).row();
            detailsTable.add(buttonTable).width(dialogWidth * 0.6f).padTop(msgLable.getHeight()).align(Align.center).row();

            proceed.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {

                    if (mobileTextField.getText().trim().length() < 10) {
                        updateError("Enter valid mobile no.", false);
                    } else {
                        openOtpDialog(mobileTextField.getText());
                    }
                }
            });

        } else {
            TextButton proceedButton = getSaveButton("VERIFY AND PROCEED");
            proceedButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (mobileTextField.getText().trim().length() < 4) {
                        updateError("Enter valid otp", false);
                    } else {
                        final ProcessDialog processDialog = new ProcessDialog(screen);
                        processDialog.show(screen.stage);
                        ApiHandler.callWitzealMsisdnApi(mobileNo, mobileTextField.getText(), "", new GdxListener<String>() {
                            @Override
                            public void onSuccess(String s) {
                                processDialog.hide();
                                if (s.matches("Success")) {
                                    onVerified();
                                    hide();
                                } else {
                                    updateError(s, true);
                                }
                            }

                            @Override
                            public void onFail(String reason) {
                                processDialog.hide();
                            }

                            @Override
                            public void onError(String error) {
                                processDialog.hide();
                            }
                        });
                    }
                }
            });

            detailsTable.add(proceedButton).uniformY().fillY().width(dialogWidth * 0.7f).padTop(msgLable.getHeight() * 1.4f).padBottom(1 * density).row();
        }


//        NinePatchDrawable detailsBackground = new NinePatchDrawable(new NinePatch(commonAtlas.findRegion("blue_box"), 28, 28, 28, 28));
//        TextureRegionDrawable rankBackground = new TextureRegionDrawable(commonAtlas.findRegion("bg_rank"));


        contentTable.add(detailsTable).expand().fill().align(Align.top).row();
    }

    private TextButton getSaveButton(String msg) {
        TextureRegionDrawable ninePatchDrawable = new TextureRegionDrawable(uiAtlas.findRegion("btn_save"));
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = FontPool.obtain(FontType.ROBOTO_BOLD, 7);
        ninePatchDrawable.setMinWidth(20);
        ninePatchDrawable.setMinHeight(buttonStyle.font.getLineHeight() * 2.0f);
        buttonStyle.fontColor = Color.WHITE;
        buttonStyle.downFontColor = Color.LIGHT_GRAY;
        buttonStyle.up = ninePatchDrawable;
        buttonStyle.down = ninePatchDrawable;
        return new TextButton(msg, buttonStyle);

    }

    public void updateError(final String msg, final boolean isBlank) {
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                errorTable.clear();
                if (!isBlank) {
                    errorlable.setText("Error: " + msg);
                } else {
                    errorlable.setText(msg);
                }
                errorTable.add(errorlable).width(width * .8f).fillX().expandX().row();
            }
        });

    }

    private void openOtpDialog(final String msisdn) {
        final ProcessDialog processDialog = new ProcessDialog(screen);
        processDialog.show(screen.stage);
        ApiHandler.callOtpApi(msisdn, new GdxListener<String>() {
            @Override
            public void onSuccess(String s) {
                processDialog.hide();
                if (s.matches("Success")) {
                    isproceed = false;
                    mobileNo = msisdn;
                    getContentTable().clear();
                    buildDialog();
                } else if (!s.isEmpty()) {
                    updateError(s, false);
                }
            }

            @Override
            public void onFail(String reason) {
                processDialog.hide();
                toast("Please check internet connection");
            }

            @Override
            public void onError(String error) {
                processDialog.hide();
                toast("Please check internet connection");
            }
        });

    }

    public abstract void onVerified();


}
