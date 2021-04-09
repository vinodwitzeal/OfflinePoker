package bigcash.poker.dialogs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
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
import bigcash.poker.models.PaytmOtpDetail;
import bigcash.poker.models.UIDialog;
import bigcash.poker.models.UIScreen;
import bigcash.poker.network.ApiHandler;
import bigcash.poker.network.GdxListener;
import bigcash.poker.utils.AssetsLoader;
import bigcash.poker.utils.DrawableBuilder;
import bigcash.poker.utils.GamePreferences;
import bigcash.poker.widgets.HtmlTextField;

/**
 * Created by sundram on 5/7/2019.
 */

public class PaytmNumberVerifiedDialog extends UIDialog {

    public static final long LIFE_2 = 123436;
    public static final long LIFE_5 = 123435;
    public static final long BONUS_10 = 123434;
    public static final long ADD_MONEY = 123437;
    private TextureAtlas uiAtlas;
    private GdxListener<String> listener;
    private TextField mobileTextField;
    private boolean isproceed,isNumberChanged=false;
    private Label errorlable;
    private Table errorTable;
    private String mobileNo;
    private float dialogWidth;
    private boolean isValidateOtp = true;
    private PaytmOtpDetail paytmOtpDetail;

    public PaytmNumberVerifiedDialog(UIScreen modelScreen, boolean isproceed, String mobileNo, GdxListener<String> listener) {
        super(modelScreen);
        this.listener = listener;
        this.isproceed = isproceed;
        this.mobileNo = mobileNo == null ? "" : mobileNo;
        dismissOnBack(true);
        buildDialog();
    }

    @Override
    public void init() {
        uiAtlas = AssetsLoader.instance().uiAtlas;
    }

    @Override
    public void buildDialog() {
        float pad = height * 0.04f;
        dialogWidth = width * 0.95f;
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


        TextureRegion paytmIconTexture = uiAtlas.findRegion("icon_paytm");
        float paytmIconHeight = headerStyle.font.getCapHeight() * 1.6f;
        float paytmIconWidth = paytmIconHeight * paytmIconTexture.getRegionWidth() / paytmIconTexture.getRegionHeight();

        Table headerTable = new Table();
        headerTable.add(new Image(paytmIconTexture)).width(paytmIconWidth).height(paytmIconHeight);

        dataTable.add(headerTable).padTop(6 * density).align(Align.left).padLeft(paytmIconWidth * .45f).row();

        Label.LabelStyle messageStyle = new Label.LabelStyle();
        messageStyle.font = FontPool.obtain(FontType.ROBOTO_REGULAR, 6);
        messageStyle.fontColor = Color.DARK_GRAY;
        Label messageLabel = new Label("Link Paytm wallet associated with this number.", messageStyle);
        if (GamePreferences.instance().isShowPaytmLink()) {
            messageLabel.setText(GamePreferences.instance().getPaytmLinkDescription());
        }
        messageLabel.setWrap(true);
        messageLabel.setAlignment(Align.left);
        dataTable.add(messageLabel).width(dialogWidth * 0.8f).padTop(10 * density).align(Align.left).padLeft(paytmIconWidth * .5f).row();

        Label numberlable = new Label(mobileNo, numberStyle);
        numberlable.setAlignment(Align.left);

        // create text field
        Font dropDownFont = FontPool.obtain(FontType.ROBOTO_BOLD, 8);

        TextureRegionDrawable cursorDrawable = new DrawableBuilder(4, (int) dropDownFont.getCapHeight()).color(Color.valueOf("f29e21")).createDrawable();

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

        Font AlertFont = FontPool.obtain(FontType.ROBOTO_REGULAR, 6);
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
            mobileTable.setBackground(dropDownBackground);
            Table prefixTable = new Table();
            prefixTable.setBackground(dropDownBackground);
            prefixTable.add(new Label("+91", labelStyle)).pad(4 * density);
            mobileTextField = new TextField("", textFieldStyle);
            mobileTextField.setText(mobileNo);
            TextField.TextFieldFilter.DigitsOnlyFilter digitsOnlyFilter = new TextField.TextFieldFilter.DigitsOnlyFilter();
            mobileTextField.setTextFieldFilter(digitsOnlyFilter);
            mobileTextField.setMaxLength(10);
            mobileTextField.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if(!isNumberChanged){
                        isNumberChanged=true;
                        mobileTextField.setText("");
                    }
                    updateError("", true);
                }
            });
            HtmlTextField.setTextInput(mobileTextField,"Enter Mobile No.",HtmlTextField.NUMBER);
            mobileTable.add(mobileTextField).expandX().fillX().pad(4 * density);
            Table numberTable = new Table();
            numberTable.left();
            numberTable.padLeft(paytmIconWidth * .5f);
            numberTable.add(prefixTable);
            numberTable.add(mobileTable).expandX().fillX().align(Align.left);
            dataTable.add(numberTable).width(dialogWidth * 0.75f).padTop(12 * density).align(Align.left).row();
        } else {
            dataTable.add(numberlable).align(Align.left).padLeft(paytmIconWidth * .5f).padTop(10 * density).row();
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
            HtmlTextField.setTextInput(mobileTextField,"Enter OTP",HtmlTextField.NUMBER);
            mobileTable.add(mobileTextField).pad(2 * density).expandX().fillX();
            dataTable.add(mobileTable).width(dialogWidth * 0.3f).padTop(12 * density).expandX().fillX().align(Align.center).row();
            Table otptable = new Table();
            otptable.add(new Label("Didn't received the OTP ?", otpStyle));
            final Label resendLable = new Label("RESEND", resendStyle);
            resendLable.setTouchable(Touchable.enabled);
            resendLable.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    callPaytmSendOtpApi(mobileNo, true);

                    //TODO
//                    getProGame().config.showToast("OTP send successfully");
                    resendLable.setTouchable(Touchable.disabled);
                }
            });
            otptable.add(resendLable).padLeft(1 * density);
            dataTable.add(otptable).width(dialogWidth * 0.4f).padTop(7 * density).expandX().fillX().align(Align.center).row();
        }

        errorTable = new Table();
        errorlable = new Label("", alertStyle);
        errorlable.setAlignment(Align.center);
        errorlable.setWrap(true);
        errorTable.add(errorlable).width(dialogWidth * 0.9f);
        dataTable.add(errorTable).width(dialogWidth * 0.9f).fillX().padTop(4 * density).row();


        if (isproceed) {
            Label cancelLabel = new Label("CANCEL", headerStyle);
            Label proceedLabel = new Label("PROCEED", headerStyle);

            Table buttonTable = new Table();
            buttonTable.right().bottom();
            buttonTable.add(cancelLabel);
            buttonTable.add(proceedLabel).padLeft(18 * density).padRight(paytmIconWidth * .1f).row();
            dataTable.add(buttonTable).width(dialogWidth * 0.6f).padTop(messageLabel.getHeight()).align(Align.right).row();

            proceedLabel.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (mobileTextField.getText().trim().length() < 10) {
                        updateError("Enter valid mobile no.", false);
                        return;
                    }
                    callPaytmSendOtpApi(mobileTextField.getText().trim(), false);
                }
            });


            cancelLabel.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    hide();
                }
            });

        } else {
            TextButton proceedButton = getSaveButton();
            proceedButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (mobileTextField.getText().trim().length() < 6) {
                        updateError("Enter valid otp", false);
                    } else {
                        callOtpValidateApi(mobileTextField.getText().trim());
                    }
                }
            });

            dataTable.add(proceedButton).uniformY().fillY().width(dialogWidth * 0.7f).padTop(messageLabel.getHeight() * 1.4f).padBottom(1 * density).row();
        }

        float padtop = height * .1f;
        Table backTable = new Table();
        float closeSize = width * 0.1f;
        float sidePad = width * 0.2f;
        backTable.add(dataTable).width(width * 0.9f).pad(padtop, sidePad, sidePad, sidePad);

        Table closeTable = new Table();
        closeTable.top().right();
        TextureRegion closeTexture = uiAtlas.findRegion("btn_close");
        TextureRegionDrawable closeDrawable = DrawableBuilder.getDrawable(closeTexture, closeSize, closeSize);
        ImageButton closeButton = new ImageButton(closeDrawable, closeDrawable, closeDrawable);
        closeTable.add(closeButton).width(closeSize).height(closeSize).padTop(padtop - closeSize * 0.5f).padLeft(closeSize * .45f);
        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hide();
            }
        });
        Stack stack = new Stack();
        stack.add(backTable);
        if (!isproceed)
            stack.add(closeTable);
        getContentTable().add(stack).width(dialogWidth);
    }

    private TextButton getSaveButton() {
        TextureRegionDrawable ninePatchDrawable = new TextureRegionDrawable(uiAtlas.findRegion("btn_save"));
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = FontPool.obtain(FontType.ROBOTO_BOLD, 7);
        ninePatchDrawable.setMinWidth(20);
        ninePatchDrawable.setMinHeight(buttonStyle.font.getLineHeight() * 2.0f);
        buttonStyle.fontColor = Color.WHITE;
        buttonStyle.downFontColor = Color.LIGHT_GRAY;
        buttonStyle.up = ninePatchDrawable;
        buttonStyle.down = ninePatchDrawable;
        return new TextButton("VERIFY AND PROCEED", buttonStyle);

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

    private void callPaytmSendOtpApi(final String msisdn, final boolean isResend) {
        final ProcessDialog processDialog = new ProcessDialog(screen);
        processDialog.show(getStage());
        ApiHandler.callPaytmSendOtpApi(msisdn, new GdxListener<PaytmOtpDetail>() {
            @Override
            public void onSuccess(PaytmOtpDetail otpDetail) {
                processDialog.hide();
                paytmOtpDetail = otpDetail;
                if (!isResend) {
                    openOtpDialog(msisdn);
                }
            }

            @Override
            public void onFail(String reason) {
                processDialog.hide();

                //TODO
//                getProGame().config.showToast(reason);
            }

            @Override
            public void onError(String errorMessage) {
                processDialog.hide();

                updateError(errorMessage, false);
            }
        });
    }

    @Override
    public Dialog show(Stage stage) {
        return super.show(stage);
    }

    private void openOtpDialog(String msisdn) {
        this.isproceed = false;
        this.mobileNo = msisdn;
        getContentTable().clear();
        buildDialog();
    }

//    @Override
//    public void messageReceived(String sender, String messageText) {
//        String[] arrMessage = messageText.split(" ");
//        if (paytmOtpDetail != null && isValidateOtp) {
//            isValidateOtp = false;
//            String otp = arrMessage[paytmOtpDetail.getOtpLocation()];
//            otp = otp.substring(0, 6);
//            try {
//                int otpInt = Integer.parseInt(otp);
//                mobileTextField.setText(otpInt + "");
//                callOtpValidateApi(mobileTextField.getText().trim());
//            } catch (Exception e) {
//
//            }
//
//        }
//    }

    private void callOtpValidateApi(String otp) {
        final ProcessDialog processDialog = new ProcessDialog(screen);
        processDialog.show(getStage());
        ApiHandler.callPaytmValidateOtpApi(mobileNo, otp, paytmOtpDetail.getState(), false, new GdxListener<String>() {
            @Override
            public void onSuccess(String response) {
                processDialog.hide();
                hide();
                listener.setSuccess("Success");
            }

            @Override
            public void onFail(String reason) {
                processDialog.hide();
            }

            @Override
            public void onError(String errorMessage) {
                processDialog.hide();
                if (errorMessage != null && errorMessage.length() > 100) {
                    errorMessage = "Server error..";
                    updateError(errorMessage, true);
                } else {
                    updateError(errorMessage, false);
                }
            }
        });
    }
}
