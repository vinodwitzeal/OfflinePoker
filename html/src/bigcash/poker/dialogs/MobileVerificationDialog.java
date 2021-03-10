package bigcash.poker.dialogs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

import bigcash.poker.constants.Constant;
import bigcash.poker.font.Font;
import bigcash.poker.font.FontPool;
import bigcash.poker.font.FontType;
import bigcash.poker.models.UIDialog;
import bigcash.poker.models.UIScreen;
import bigcash.poker.network.ApiHandler;
import bigcash.poker.network.GdxListener;
import bigcash.poker.utils.AssetsLoader;
import bigcash.poker.utils.DrawableBuilder;
import bigcash.poker.utils.PokerInterval;

public class MobileVerificationDialog extends UIDialog {
    public boolean verified;
    private TextureAtlas uiAtlas;
    private GdxListener<String> listener;
    private Label verifyWithLabel;
    private ProcessDialog processDialog;
    private String truecallerRequestId;
    private boolean truecallerProcessing;
    private int count;

    public MobileVerificationDialog(UIScreen screen) {
        super(screen);
        dismissOnBack(true);
        verified = false;
        buildDialog();
    }

    public MobileVerificationDialog(UIScreen screen, GdxListener<String> listener) {
        super(screen);
        dismissOnBack(true);
        verified = false;
        this.listener = listener;
        buildDialog();
    }

    @Override
    public void init() {
        uiAtlas= AssetsLoader.instance().uiAtlas;
    }

    @Override
    public void buildDialog() {
        processDialog=new ProcessDialog(screen);
        float dialogWidth = width * 0.9f;
        float closeSize = width * 0.1f;
        Table dataTable = new Table();

        Gdx.app.error("Passed","65");
        TextureRegion circleTexture = uiAtlas.findRegion("d_circle");
        TextureRegion shadowTexture = uiAtlas.findRegion("shadow");
        TextureRegion bg_phone = uiAtlas.findRegion("icon_phone");
        TextureRegion logoTexture = uiAtlas.findRegion("icon_true");
        float logoWidth = dialogWidth * 0.1f;
        float logoHeight = logoWidth * logoTexture.getRegionHeight() / logoTexture.getRegionWidth();
        Gdx.app.error("Passed","72");

        NinePatch whiteNinePatch = new NinePatch(uiAtlas.findRegion("bg_dialog"), 30, 30, 30, 30);
        NinePatchDrawable whitebackground = new NinePatchDrawable(whiteNinePatch);
        whitebackground.setMinWidth(60);
        whitebackground.setMinHeight(60);

        Gdx.app.error("Passed","79");

        NinePatch greyNinePatch = new NinePatch(uiAtlas.findRegion("bg_bottom"), 30, 30, 30, 30);
        NinePatchDrawable greybackground = new NinePatchDrawable(greyNinePatch);
        greybackground.setMinWidth(60);
        greybackground.setMinHeight(60);

        Gdx.app.error("Passed","86");

        Table topTable = new Table();
        topTable.setBackground(whitebackground);
        topTable.top();

        Table bottomTable = new Table();
        bottomTable.setBackground(greybackground);
        bottomTable.bottom();


        Table circleTable = new Table();
        circleTable.add(new Image(circleTexture)).width(dialogWidth*.65f).height(dialogWidth*.55f);

        Table shadowTable=new Table();
        shadowTable.bottom();
        shadowTable.add(new Image(shadowTexture)).width(dialogWidth*.85f).padTop(2*density).height(closeSize*.7f);

        Table logoTable=new Table();
        logoTable.bottom();
        float phoneWidth=dialogWidth*.20f;
        float phoneHeight=phoneWidth*bg_phone.getRegionHeight()/bg_phone.getRegionWidth();
        Table phoneTable=new Table();
        phoneTable.add(new Image(bg_phone)).height(phoneHeight).width(phoneWidth);

        Table luckyWheelTable=new Table();
        luckyWheelTable.add(new Image(logoTexture)).height(logoHeight).width(logoWidth).padTop(8 * density).row();


        Stack wheelStack=new Stack();
        wheelStack.add(phoneTable);
        wheelStack.add(luckyWheelTable);
        logoTable.add(wheelStack);



        Stack topStack=new Stack();
        topStack.add(circleTable);
        topStack.add(logoTable);
//        topStack.add(shadowTable);

        topTable.add(topStack).padTop(closeSize*.5f).colspan(2).row();
        topTable.add(shadowTable).width(dialogWidth*.8f).row();

        final Label.LabelStyle messageStyle = new Label.LabelStyle();
        messageStyle.font = FontPool.obtain(FontType.ROBOTO_BOLD, 10);
        messageStyle.fontColor = Color.BLACK;

        Label.LabelStyle subMessageStyle = new Label.LabelStyle();
        subMessageStyle.font = FontPool.obtain(FontType.ROBOTO_BOLD, 6);
        subMessageStyle.fontColor = Color.GRAY;

        final Label messageLabel1 = new Label("Verify Mobile Number", messageStyle);
        messageLabel1.setWrap(true);
        messageLabel1.setAlignment(Align.center);
        topTable.add(messageLabel1).width(dialogWidth * 0.9f).padBottom(10 * density).padTop(10 * density).row();

        final Label messageLabel=new Label("Please verify your mobile number to play and earn.",subMessageStyle);
        messageLabel.setWrap(true);
        messageLabel.setAlignment(Align.center);
        topTable.add(messageLabel).width(dialogWidth*.7f).padBottom(10*density).padTop(10*density).row();

        final Label.LabelStyle errorStyle = new Label.LabelStyle();
        errorStyle.font = FontPool.obtain(FontType.ROBOTO_REGULAR, 7);
        errorStyle.fontColor = Color.SCARLET;

//        final Label messageLabel = new Label("", messageStyle);
//        messageLabel.setWrap(true);
//        messageLabel.setAlignment(Align.center);
//        topTable.add(messageLabel).width(dialogWidth * 0.9f).padTop(logoHeight * .2f).padBottom(20 * density).row();


        Font buttonFont = FontPool.obtain(FontType.ROBOTO_BOLD, 7);
        Label.LabelStyle buttonLabelStyle = new Label.LabelStyle();
        buttonLabelStyle.font = buttonFont;
        buttonLabelStyle.fontColor = Color.WHITE;

        final Table okTable = new Table();
        NinePatchDrawable blueDrawable = new NinePatchDrawable(new NinePatch(uiAtlas.findRegion("button_green"), 12, 12, 12, 16));
        blueDrawable.setMinHeight(buttonFont.getLineHeight() * 1.8f);
        blueDrawable.setMinWidth(28);
        okTable.setBackground(blueDrawable);
        okTable.add(new Label("OK", buttonLabelStyle)).padLeft(25 * density).padRight(25 * density).expandX();
        okTable.setTouchable(Touchable.enabled);
        okTable.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hide();
            }
        });

        final Table buttonTable = new Table();

        TextureRegion truecallerButtonTexture = uiAtlas.findRegion("btn_truecaller");
        float buttonWidth = dialogWidth * 0.85f;
        float buttonHeight = buttonWidth * truecallerButtonTexture.getRegionHeight() / truecallerButtonTexture.getRegionWidth();
//        TextureRegionDrawable trueCallerDrawable = DrawableBuilder.getDrawable(truecallerButtonTexture, buttonWidth, buttonHeight);
//
//        ImageButton trueCallerButton = new ImageButton(trueCallerDrawable, trueCallerDrawable, trueCallerDrawable);
//        buttonTable.add(trueCallerButton).width(buttonWidth).height(buttonHeight).row();
//
//        trueCallerButton.addListener(new ClickListener() {
//            @Override
//            public void clicked(InputEvent event, float x, float y) {
//                truecallerProcessing=false;
//                processDialog.show(screen.stage);
//                truecallerRequestId=Constant.userProfile.getUserId()+"-"+ TimeUtils.millis();
//                TrueCaller.invokeTrueCaller(truecallerRequestId, new TrueCaller.TrueCallerHandler() {
//                    @Override
//                    public void onProcessing() {
//                        count=3;
//                        truecallerProcessing=true;
//                    }
//
//                    @Override
//                    public void onFailed() {
//                        Gdx.app.postRunnable(new Runnable() {
//                            @Override
//                            public void run() {
//                                processDialog.hide();
//                                toast("Truecaller App Not Present");
//                            }
//                        });
//                    }
//                });
//                TrueCaller.initializeVerification(new GdxListener<String>() {
//                    @Override
//                    public void onSuccess(String s) {
//
//                    }
//
//                    @Override
//                    public void onFail(String reason) {
//                        processDialog.hide();
//                        toast(reason);
//                    }
//
//                    @Override
//                    public void onError(String errorMessage) {
//                        processDialog.hide();
//                        messageLabel.setText(errorMessage);
//                        messageLabel.setStyle(errorStyle);
//                    }
//                });
//            }
//        });
//
//        Gdx.app.error("Passed",219+"");

        Table verifyTable = new Table();
        NinePatchDrawable greenDrawable = new NinePatchDrawable(new NinePatch(uiAtlas.findRegion("button_green"), 12, 12, 12, 16));
        greenDrawable.setMinHeight(buttonFont.getLineHeight() * 1.8f);
        greenDrawable.setMinWidth(40);
        verifyTable.setBackground(greenDrawable);
        verifyTable.add(new Label("Verify with OTP", buttonLabelStyle)).padLeft(10 * density).padRight(10 * density).expandX();

        buttonTable.add(verifyTable).width(buttonWidth).height(buttonHeight).padTop(8*density).expandX();

        verifyTable.setTouchable(Touchable.enabled);
        verifyTable.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String msisdn=Constant.userProfile.getMsisdn();
                if (msisdn==null){
                    msisdn="";
                }
                new OtpDialog(screen,msisdn,true){
                    @Override
                    public void onVerified() {
                        verified = true;
                        if (listener == null) {
                            messageLabel.setText(Constant.userProfile.getMsisdn() + " verified successfully!");
                            buttonTable.clear();
                            buttonTable.add(okTable).expandX();
                        } else {
                            hide();
                            listener.setSuccess("Success");
                        }
                    }
                }.show(screen.stage);
            }
        });

        bottomTable.add(buttonTable).width(dialogWidth * 0.9f);


        dataTable.add(topTable).width(dialogWidth).row();
        dataTable.add(bottomTable).width(dialogWidth);


        Table backTable = new Table();
        float sidePad = closeSize * 0.4f;
        backTable.add(dataTable).width(width * 0.9f).pad(sidePad, sidePad, sidePad, sidePad);

        Gdx.app.error("Passed","272");

        Table closeTable = new Table();
        closeTable.top().right();
        TextureRegion closeTexture = uiAtlas.findRegion("icon_close");
        TextureRegionDrawable closeDrawable = DrawableBuilder.getDrawable(closeTexture, closeSize, closeSize);
        ImageButton closeButton = new ImageButton(closeDrawable, closeDrawable, closeDrawable);
        closeTable.add(closeButton).width(closeSize).height(closeSize).padTop(sidePad - closeSize * 0.4f);
        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hide();
            }
        });

        Gdx.app.error("Passed","287");

        Stack stack = new Stack();
        stack.add(backTable);
        stack.add(closeTable);
        getContentTable().add(stack);
    }

    private void onTrueCallerSuccess(){
        processDialog.hide();
        verified = true;
        if (listener == null) {
//            messageLabel.setText(Constant.userProfile.getMsisdn() + " verified successfully!");
//            buttonTable.clear();
//            buttonTable.add(okTable).expandX();
            // messageLabel.setStyle(messageStyle);
            verifyWithLabel.setVisible(false);
        } else {
            hide();
            listener.setSuccess("Success");
        }
    }


    @Override
    public void resume() {
        super.resume();
        if (truecallerProcessing){
            truecallerProcessing=false;
            PokerInterval pokerInterval=new PokerInterval(3000) {
                @Override
                public void onInterval() {
                    ApiHandler.callTrueCallerApi(truecallerRequestId, new GdxListener<String>() {
                        @Override
                        public void onSuccess(String s) {
                            if (s.matches("417")){
                                count--;
                                if (count<=0){
                                    cancel();
                                }
                            }else {
                                cancel();
                                onTrueCallerSuccess();
                            }
                        }

                        @Override
                        public void onFail(String reason) {
                            count--;
                            if (count<=0){
                                cancel();
                            }
                        }

                        @Override
                        public void onError(String error) {
                            count--;
                            if (count<=0){
                                cancel();
                            }
                        }
                    });
                }
            };
            pokerInterval.start();
        }
    }
}
