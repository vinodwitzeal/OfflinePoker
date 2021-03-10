package bigcash.poker.dialogs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

import bigcash.poker.constants.Constant;
import bigcash.poker.font.Font;
import bigcash.poker.font.FontPool;
import bigcash.poker.font.FontType;
import bigcash.poker.models.UIScreen;
import bigcash.poker.models.UserProfile;
import bigcash.poker.network.ApiHandler;
import bigcash.poker.network.GdxListener;
import bigcash.poker.utils.GamePreferences;
import bigcash.poker.utils.TextureDrawable;
import bigcash.poker.widgets.HtmlTextField;
import bigcash.poker.widgets.WalletTable;


public class RedeemDialog extends IDialog {
    Array<String> arrRedeemOption = new Array<String>();
    private GamePreferences preference;
    private UserProfile userProfile;
    private ProcessDialog processDialog;
    private Label errorLabel;
    private TextField mobileTextField, confirmTextField;
    private SelectBox<String> amountSelectBox;
    private GdxListener<Float> listener;
    private KYCInfoTable kycInfoTable;
    private Table topTable, errorTable;
    private float errorWidth;
    private boolean isNumberChanged = false, isconfirmedChanged = false;

    public RedeemDialog(UIScreen screen, GdxListener<Float> listener) {
        super(screen);
        this.listener = listener;
        dismissOnBack(true);
        preference = GamePreferences.instance();
        userProfile = Constant.userProfile;
        buildDialog();
    }

    public RedeemDialog(UIScreen screen) {
        super(screen);

        dismissOnBack(true);
        preference = GamePreferences.instance();
        userProfile = Constant.userProfile;
        buildDialog();
    }


    @Override
    public void init() {

    }

    @Override
    public void buildDialog() {
        Table contentTable = getContentTable();
        contentTable.top();

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
        float backHeight = walletTableHeight;
        float backWidth = backHeight * backTexture.getRegionWidth() / backTexture.getRegionHeight();
        TextureRegionDrawable menuDrawable = TextureDrawable.getDrawable(backTexture, backWidth, backHeight);
        final Button backButton = new Button(menuDrawable, menuDrawable, menuDrawable);
        headerTable.add(backButton).width(backWidth).height(backHeight).padLeft(8 * density).padRight(16 * density);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hide();
            }
        });

        headerTable.add(new Label("Redeem", titleStyle)).expandX().align(Align.left);

        TextureRegion walletBackTexture = uiAtlas.findRegion("bg_wallet");
        float walletTableWidth = walletTableHeight * walletBackTexture.getRegionWidth() / walletBackTexture.getRegionHeight();
        float walletTablePad = walletTableWidth * 0.1f;
        float walletPad = walletTablePad;

        TextureRegion addTexture = uiAtlas.findRegion("icon_add");
        float addHeight = headerLabelStyle.font.getLineHeight() * 1.2f;
        float addWidth = addHeight * addTexture.getRegionWidth() / addTexture.getRegionHeight();

        TextureRegion balanceWalletTexture = uiAtlas.findRegion("icon_walletn");
        float balanceWalletHeight = headerLabelStyle.font.getLineHeight();
        float balanceWalletWidth = balanceWalletHeight * balanceWalletTexture.getRegionWidth() / balanceWalletTexture.getRegionHeight();

        WalletTable paytmWalletTable = new WalletTable(walletBackTexture);

        paytmWalletTable.pad(0, walletTablePad * 2, 0, walletTablePad);
        paytmWalletTable.add(new Image(balanceWalletTexture)).width(balanceWalletWidth).height(balanceWalletHeight).expandX().align(Align.left);
        Label walletLabel = new Label(Constant.userProfile.getPaytmBalance() + "", headerLabelStyle);
        paytmWalletTable.add(walletLabel).padLeft(walletPad).padRight(walletPad).expandX().align(Align.right);
        paytmWalletTable.add(new Image(addTexture)).width(addWidth).height(addHeight);
        headerTable.add(paytmWalletTable).height(walletTableHeight).padRight(8 * density);
        float headerHeight = walletTableHeight * 2;
        contentTable.add(headerTable).width(width).height(headerHeight).row();

        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hide();
            }
        });


        final NinePatchDrawable whiteDrawable = new NinePatchDrawable(new NinePatch(uiAtlas.findRegion("img_dot"), 1, 1, 1, 1));

        kycInfoTable = new KYCInfoTable(whiteDrawable);
        contentTable.add(kycInfoTable).width(width).row();

        final Color balanceBackgroundColor = Color.valueOf("f0f0f0");
        Table balanceBackTable = new Table() {
            @Override
            protected void drawBackground(Batch batch, float parentAlpha, float x, float y) {
                batch.setColor(balanceBackgroundColor.r, balanceBackgroundColor.g, balanceBackgroundColor.b, balanceBackgroundColor.a * parentAlpha);
                whiteDrawable.draw(batch, x, y, getWidth(), getHeight());
            }
        };

        Table balanceTable = new Table();
        TextureRegion balanceBackTexture = uiAtlas.findRegion("bg_redeem");
        float balanceBackWidth = width * 0.95f;
        float balanceBackHeight = balanceBackWidth * balanceBackTexture.getRegionHeight() / balanceBackTexture.getRegionWidth();
        TextureRegionDrawable balanceBackground = TextureDrawable.getDrawable(balanceBackTexture, balanceBackWidth, balanceBackHeight);
        balanceTable.setBackground(balanceBackground);


        Label.LabelStyle amountLabelStyle = new Label.LabelStyle();
        amountLabelStyle.font = FontPool.obtain(FontType.ROBOTO_BOLD, 4);
        amountLabelStyle.fontColor = Color.BLACK;

        Label.LabelStyle amountStyle = new Label.LabelStyle();
        amountStyle.font = FontPool.obtain(FontType.ROBOTO_BOLD, 6);
        amountStyle.fontColor = Color.valueOf("35941b");

        Table unutilizedTable = new Table();
        unutilizedTable.add(new Label("Unutilized : ", amountLabelStyle)).expandX().align(Align.right);
        unutilizedTable.add(new Label("\u20b9 " + Constant.userProfile.getDeposited(), amountStyle));


        Table winningsTable = new Table();

        winningsTable.add(new Label("Winnings : ", amountLabelStyle)).expandX().align(Align.right);
        winningsTable.add(new Label("\u20b9 " + Constant.userProfile.getWithdrawable(), amountStyle));

        TextureRegion infoTexture = uiAtlas.findRegion("icon_info");
        float infoHeight = amountLabelStyle.font.getLineHeight() * 1.5f;
        float infoWidth = infoHeight * infoTexture.getRegionWidth() / infoTexture.getRegionHeight();
        TextureRegionDrawable infoDrawable = TextureDrawable.getDrawable(infoTexture, infoWidth, infoHeight);
        Button infoButton = new Button(infoDrawable, infoDrawable, infoDrawable);
        infoButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                new RedeemInfoDialog(screen).show(screen.stage);
            }
        });

        balanceTable.add().width(infoWidth).height(infoHeight).pad(infoWidth / 2f);

        float centerPad = balanceBackWidth * 0.15f;

        balanceTable.add(unutilizedTable).uniformX().expandX().align(Align.right);
        balanceTable.add().width(centerPad);
        balanceTable.add(winningsTable).uniformX().expandX().align(Align.left);
        balanceTable.add(infoButton).width(infoWidth).height(infoHeight).pad(infoWidth / 2f).align(Align.bottom);


        balanceBackTable.add(balanceTable).width(balanceBackWidth).height(balanceBackHeight).padTop(8 * density).padBottom(8 * density);

        contentTable.add(balanceBackTable).width(width).row();


        Table redeemTable = new Table();
        redeemTable.top();
        redeemTable.setBackground(whiteDrawable);
        redeemTable.padTop(16 * density);

        Label.LabelStyle optionStyle = new Label.LabelStyle();
        optionStyle.font = FontPool.obtain(FontType.ROBOTO_BOLD, 6);
        optionStyle.fontColor = Color.GRAY;

        NinePatchDrawable optionBackground = new NinePatchDrawable(new NinePatch(uiAtlas.findRegion("bg_textfield"), 7, 7, 7, 7));
        NinePatchDrawable textFieldBackground = new NinePatchDrawable(new NinePatch(uiAtlas.findRegion("bg_gtextfield"), 7, 7, 7, 7));

        Font textFieldFont = FontPool.obtain(FontType.ROBOTO_BOLD, 7);
        TextureRegion dropTexture = uiAtlas.findRegion("icon_drop");
        float dropHeight = textFieldFont.getCapHeight() / 2f;
        float dropWidth = dropHeight * dropTexture.getRegionWidth() / dropTexture.getRegionHeight();


        float redeemColumnWidth = width * 0.8f;
        Label optionLabel = new Label("Choose an option to redeem:", optionStyle);
        optionLabel.setAlignment(Align.left);
        redeemTable.add(optionLabel).width(redeemColumnWidth).row();

        Stack optionStack = new Stack();
        Table optionBackTable = new Table();
        optionBackTable.setBackground(optionBackground);
        float fieldPad = 8 * density;
        TextureRegion optionTexture = uiAtlas.findRegion("icon_option");
        float optionHeight = textFieldFont.getLineHeight();
        float optionWidth = optionHeight * optionTexture.getRegionWidth() / optionTexture.getRegionHeight();
        optionBackTable.add(new Image(optionTexture)).width(optionWidth).height(optionHeight).pad(fieldPad);
        Image lineImage = new Image(whiteDrawable);
        lineImage.setColor(Color.GRAY);
        optionBackTable.add(lineImage).width(2 * density).height(optionHeight).pad(fieldPad);

        List.ListStyle dropListStyle = new List.ListStyle();
        dropListStyle.font = textFieldFont;
        dropListStyle.fontColorSelected = Color.WHITE;
        dropListStyle.fontColorUnselected = Color.GRAY;
        dropListStyle.background = whiteDrawable;
        dropListStyle.selection = blueDrawable;

        SelectBox.SelectBoxStyle selectBoxStyle = new SelectBox.SelectBoxStyle();
        selectBoxStyle.font = textFieldFont;
        selectBoxStyle.fontColor = Color.GRAY;
        selectBoxStyle.listStyle = dropListStyle;
        selectBoxStyle.scrollStyle = new ScrollPane.ScrollPaneStyle();

        SelectBox<String> selectBox = new SelectBox<String>(selectBoxStyle);
        selectBox.setItems("Paytm");

        optionBackTable.add(selectBox).expandX().fillX();

        Table optionFrontTable = new Table();
        optionFrontTable.right();
        optionFrontTable.add(new Image(dropTexture)).width(dropWidth).height(dropHeight).padRight(dropWidth);

        optionStack.add(optionBackTable);
        optionStack.add(optionFrontTable);

        redeemTable.add(optionStack).width(redeemColumnWidth).row();


        if (userProfile.getWithdrawable() >= userProfile.getRedeemThreshold()) {
            String[] redeemOptions = preference.getRedeemValues().split("\\,");
            for (int i = 0; i < redeemOptions.length; i++) {
                if (userProfile.getWithdrawable() >= Integer.parseInt(redeemOptions[i])) {
                    arrRedeemOption.add(redeemOptions[i]);
                }
            }
        }
        if (arrRedeemOption.size == 0) {
            arrRedeemOption.add(0 + "");
        }


        Stack amountStack = new Stack();
        Table amountBackTable = new Table();
        amountBackTable.setBackground(textFieldBackground);
        TextureRegion amountTexture = uiAtlas.findRegion("icon_amount");
        float amountHeight = textFieldFont.getLineHeight();
        float amountWidth = amountHeight * amountTexture.getRegionWidth() / amountTexture.getRegionHeight();
        amountBackTable.add(new Image(amountTexture)).width(amountWidth).height(amountHeight).pad(fieldPad);
        Image lineImage2 = new Image(whiteDrawable);
        lineImage2.setColor(Color.GRAY);
        amountBackTable.add(lineImage2).width(2 * density).height(amountHeight).pad(fieldPad);


        amountSelectBox = new SelectBox<String>(selectBoxStyle);
        amountSelectBox.setItems(arrRedeemOption);
        amountBackTable.add(amountSelectBox).expandX().fillX();

        Table amountFrontTable = new Table();
        amountFrontTable.right();
        amountFrontTable.add(new Image(dropTexture)).width(dropWidth).height(dropHeight).padRight(dropWidth);

        amountStack.add(amountBackTable);
        amountStack.add(amountFrontTable);


        Label.LabelStyle messageLabelStyle = new Label.LabelStyle();
        messageLabelStyle.font = FontPool.obtain(FontType.ROBOTO_BOLD, 5);
        messageLabelStyle.fontColor = Color.DARK_GRAY;

        final Label.LabelStyle priceStyle = new Label.LabelStyle();
        priceStyle.font = FontPool.obtain(FontType.ROBOTO_BOLD, 5);
        priceStyle.fontColor = Color.RED;

        final Table alertTable = new Table();
        alertTable.left();
        alertTable.add(new Label("*Minimum winnings ", messageLabelStyle));
        alertTable.add(new Label("\u20b9" + Constant.userProfile.getRedeemThreshold(), priceStyle));
        alertTable.add(new Label(" required to redeem.", messageLabelStyle));
        redeemTable.add(alertTable).width(redeemColumnWidth).padTop(infoHeight).padLeft(fieldPad).align(Align.left).row();

        redeemTable.add(amountStack).width(redeemColumnWidth).pad(fieldPad).row();

        Table numberTable = new Table();
        numberTable.setBackground(textFieldBackground);
        TextureRegion mobileTexture = uiAtlas.findRegion("icon_mobile");
        float mobileHeight = textFieldFont.getLineHeight();
        float mobileWidth = mobileHeight * mobileTexture.getRegionWidth() / mobileTexture.getRegionHeight();
        numberTable.add(new Image(mobileTexture)).width(mobileWidth).height(mobileHeight).pad(fieldPad);
        Image lineImage3 = new Image(whiteDrawable);
        lineImage3.setColor(Color.GRAY);
        numberTable.add(lineImage3).width(2 * density).height(amountHeight).pad(fieldPad);

        NinePatchDrawable cursorDrawable = new NinePatchDrawable(new NinePatch(uiAtlas.findRegion("img_cursor"), 1, 1, 1, 1));
        cursorDrawable.setMinSize(2 * density, textFieldFont.getLineHeight());
        TextField.TextFieldStyle textFieldStyle = new TextField.TextFieldStyle();
        textFieldStyle.font = textFieldFont;
        textFieldStyle.fontColor = Color.GRAY;
        textFieldStyle.messageFontColor = Color.LIGHT_GRAY;
        textFieldStyle.cursor = cursorDrawable;

        final String verifiedNo = (userProfile.getMsisdn() != null && !userProfile.getMsisdn().isEmpty()) ? userProfile.getMsisdn() : "";

        mobileTextField = new TextField(verifiedNo, textFieldStyle);
        mobileTextField.setMessageText("Enter your mobile number.");
        HtmlTextField.setTextInput(mobileTextField,"Enter your mobile number",HtmlTextField.NUMBER);
        numberTable.add(mobileTextField).expandX().fillX().align(Align.left);
        mobileTextField.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!isNumberChanged) {
                    isNumberChanged = true;
                    mobileTextField.setText("");
                }
            }
        });
        redeemTable.add(numberTable).width(redeemColumnWidth).pad(fieldPad).row();
        Table confirmTable = new Table();
        confirmTable.setBackground(textFieldBackground);
        confirmTable.add(new Image(mobileTexture)).width(mobileWidth).height(mobileHeight).pad(fieldPad);
        Image lineImage4 = new Image(whiteDrawable);
        lineImage4.setColor(Color.GRAY);
        confirmTable.add(lineImage4).width(2 * density).height(amountHeight).pad(fieldPad);
        confirmTextField = new TextField(verifiedNo, textFieldStyle);
        confirmTextField.setMessageText("Confirm your mobile number.");
        HtmlTextField.setTextInput(confirmTextField,"Confirm your mobile number",HtmlTextField.NUMBER);
        confirmTextField.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!isconfirmedChanged) {
                    isconfirmedChanged = true;
                    confirmTextField.setText("");
                }
            }
        });
        confirmTable.add(confirmTextField).expandX().fillX().align(Align.left);
        redeemTable.add(confirmTable).width(redeemColumnWidth).pad(fieldPad).row();


        final Label.LabelStyle mobileMessageStyle = new Label.LabelStyle();
        mobileMessageStyle.font = FontPool.obtain(FontType.ROBOTO_BOLD, 5);
        mobileMessageStyle.fontColor = Color.RED;

        errorWidth = redeemColumnWidth;
        errorTable = new Table();
        errorLabel = new Label("", mobileMessageStyle);
        errorLabel.setWrap(true);
        errorLabel.setAlignment(Align.center);

        redeemTable.add(errorTable).width(errorWidth).padBottom(fieldPad * 2).row();

        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = FontPool.obtain(FontType.ROBOTO_BOLD, 8);
        buttonStyle.fontColor = Color.WHITE;
        buttonStyle.up = blueDrawable;
        buttonStyle.down = blueDrawable;
        buttonStyle.checked = blueDrawable;

        float buttonWHeight = buttonStyle.font.getCapHeight() * 3;

        TextButton redeemButton = new TextButton("Redeem", buttonStyle);

        redeemTable.add(redeemButton).width(redeemColumnWidth).height(buttonWHeight).row();

        redeemButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                if (mobileTextField.getText().trim().length() == 10) {
                    if (mobileTextField.getText().trim().matches(confirmTextField.getText().trim())) {
                        if (!amountSelectBox.getSelected().matches("0")) {
                            redeemRequest();
                        } else {
                            Gdx.app.postRunnable(new Runnable() {
                                @Override
                                public void run() {
                                    alertTable.clear();
                                    alertTable.add(new Label("*Minimum winnings", mobileMessageStyle));
                                    alertTable.add(new Label("\u20b9" + Constant.userProfile.getRedeemThreshold(), priceStyle));
                                    alertTable.add(new Label(" required to redeem.", mobileMessageStyle));
                                }
                            });
                        }
                    } else {
                        mobileTextField.setText("");
                        confirmTextField.setText("");
                        setError("Mobile Number doesn't match. Re-enter mobile number.");
                    }

                } else {
                    setError("Enter valid mobile number..");
                }
            }
        });

        Label.LabelStyle inviteStyle = new Label.LabelStyle();
        inviteStyle.font = FontPool.obtain(FontType.ROBOTO_BOLD, 4);
        inviteStyle.fontColor = Color.GRAY;

        Label inviteLabel = new Label("*Earn \u20b9 15 for every friend joined from your invite link.", inviteStyle);
        inviteLabel.setAlignment(Align.center);
        inviteLabel.setWrap(true);
        redeemTable.add(inviteLabel).width(redeemColumnWidth).pad(fieldPad).row();


        Label.LabelStyle termsStyle = new Label.LabelStyle();
        termsStyle.font = FontPool.obtain(FontType.ROBOTO_BOLD, 5);
        termsStyle.fontColor = Color.valueOf("0084ff");

        Label termsLabel = new Label("Terms & Conditions Apply", termsStyle);
        termsLabel.setAlignment(Align.center);
        termsLabel.setWrap(true);
        redeemTable.add(termsLabel).pad(fieldPad).width(redeemColumnWidth);
        termsLabel.setTouchable(Touchable.enabled);
        termsLabel.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ProcessDialog processDialog=new ProcessDialog(screen,"Please Wait...");
                processDialog.show(screen.stage);
                ApiHandler.callPrivacyPolicyApi("REDEEM", new GdxListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        processDialog.hide();
                        new PolicyDialog(screen,s).show(screen.stage);
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
        });


        Stack redeemStack = new Stack();
        topTable = new Table();
        Image image = new Image(whiteDrawable);
        image.setColor(Color.valueOf("ffffff88"));
        topTable.add(image).expand().fill();
        redeemStack.add(redeemTable);
        redeemStack.add(topTable);
        topTable.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (Constant.userProfile.getStatus() != null && Constant.userProfile.getStatus().matches("REQUIRED")) {
                    setEnabled(false);
                    openNotifyDialogForKyc("Kyc Required", Constant.userProfile.getRedeemErrorMessage(), "Proceed");
                } else if (Constant.userProfile.getStatus() != null && Constant.userProfile.getStatus().matches("FAILED")) {
                    setEnabled(false);
                    openNotifyDialogForKyc("Kyc Failed", Constant.userProfile.getRedeemErrorMessage(), "Proceed");
                } else if (Constant.userProfile.getStatus() != null && Constant.userProfile.getStatus().matches("PENDING")) {
                    setEnabled(false);
                    openNotifyDialog("Kyc Pending", Constant.userProfile.getRedeemErrorMessage(), "OK");
                } else {
                    setEnabled(true);
                }
            }
        });
        contentTable.add(redeemStack).width(width).expandY().fill().row();
        checkStatus();
    }

    private void checkStatus() {
        if (Constant.userProfile.getStatus() != null && Constant.userProfile.getStatus().matches("REQUIRED")) {
            setEnabled(false);
        } else if (Constant.userProfile.getStatus() != null && Constant.userProfile.getStatus().matches("FAILED")) {
            setEnabled(false);
        } else if (Constant.userProfile.getStatus() != null && Constant.userProfile.getStatus().matches("PENDING")) {
            setEnabled(false);
        } else {
            setEnabled(true);
        }
    }

    public void setEnabled(boolean enabled) {
        topTable.setTouchable(enabled ? Touchable.disabled : Touchable.enabled);
        topTable.setVisible(!enabled);
    }


    public void redeemRequest() {
        processDialog = new ProcessDialog(screen, "Please wait..");
        processDialog.show(getStage());
        ApiHandler.callPaytmApi(mobileTextField.getText(), amountSelectBox.getSelected(), "PAYTM", new GdxListener<String>() {
            @Override
            public void onSuccess(String s) {
                processDialog.hide();
                if (s.matches("Success")) {
                    hide();
                    screen.updateBalance();
                    if (listener != null) {
                        listener.setSuccess(Constant.userProfile.getPaytmBalance());
                    }
                    new MessageDialog(screen, "Redeem Successful", Constant.RedeemMessage, "OK").show(getStage());
                    Constant.RedeemMessage = "";
                } else {
                    RedeemDialog.this.setError(s);
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


    private void setError(final String messageText) {
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                errorTable.clear();
                errorLabel.setText(messageText);
                errorTable.add(errorLabel).width(errorWidth);
            }
        });
    }


    public class KYCInfoTable extends Table {
        public Color backgroundColor;
        public NinePatchDrawable background;
        public Label infoLabel;

        public KYCInfoTable(NinePatchDrawable background) {
            this.background = background;
            Font infoFont = FontPool.obtain(FontType.ROBOTO_BOLD, 5);
            infoFont.getData().markupEnabled = true;
            Label.LabelStyle infoLabelStyle = new Label.LabelStyle();
            infoLabelStyle.font = infoFont;
            infoLabelStyle.fontColor = Color.WHITE;

            infoLabel = new Label("", infoLabelStyle);
            infoLabel.setAlignment(Align.center);
            infoLabel.setWrap(true);
            add(infoLabel).width(width * 0.95f).padTop(2 * density).padBottom(2 * density);
            this.backgroundColor = Color.WHITE;
            if (Constant.userProfile.getStatus() != null && Constant.userProfile.getStatus().matches("REQUIRED")) {
                setUploadKYC();
            } else if (Constant.userProfile.getStatus() != null && Constant.userProfile.getStatus().matches("FAILED")) {
                setKYCDeclined();
            } else if (Constant.userProfile.getStatus() != null && Constant.userProfile.getStatus().matches("PENDING")) {
                setKYCPending();
            }

            infoLabel.setTouchable(Touchable.enabled);
            infoLabel.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    showStatusDialog();
                }
            });

        }


        public void setUploadKYC() {
            this.backgroundColor = Color.valueOf("f8c79f");
            infoLabel.setText("[BLACK]NOTE : [DARK_GRAY]You need to complete your KYC to redeem. Click on link to complete : [BLUE]KYC[WHITE]");

        }

        public void setKYCDeclined() {
            this.backgroundColor = Color.valueOf("f89f9f");
            infoLabel.setText("[BLACK]NOTE : [DARK_GRAY]Your KYC request has been declined. Click on link to : [BLUE]KNOW MORE[WHITE]");
        }

        public void setKYCPending() {
            this.backgroundColor = Color.valueOf("f89f9f");
            infoLabel.setText("[BLACK]NOTE : [DARK_GRAY]Your KYC request is pending. Click on link to : [BLUE]KNOW MORE[WHITE]");
        }


        @Override
        protected void drawBackground(Batch batch, float parentAlpha, float x, float y) {
            batch.setColor(backgroundColor.r, backgroundColor.g, backgroundColor.b, backgroundColor.a * parentAlpha);
            background.draw(batch, x, y, getWidth(), getHeight());
        }
    }

    private void openNotifyDialog(String title, String message, String buttonName) {
        new NotifyDialog(screen, title, message, buttonName, new GdxListener<String>() {
            @Override
            public void onSuccess(String s) {

            }

            @Override
            public void onFail(String reason) {

            }

            @Override
            public void onError(String errorMessage) {

            }
        }).show(getStage());
    }

    private void openNotifyDialogForKyc(String title, String message, String buttonName) {
        new NotifyDialog(screen, title, message, buttonName, new GdxListener<String>() {
            @Override
            public void onSuccess(String s) {
                openKycDialog();
            }

            @Override
            public void onFail(String reason) {

            }

            @Override
            public void onError(String errorMessage) {

            }
        }).show(getStage());
    }

    private void openKycDialog() {
        new KYCDialog(screen, new GdxListener<String>() {
            @Override
            public void onSuccess(String s) {
                openNotifyDialog("Successfully!", s, "OK");
                kycInfoTable.setKYCPending();
                Constant.userProfile.setStatus("PENDING");
                Constant.userProfile.setRedeemErrorMessage("Your KYC verification is in process, please wait for some time.");
            }

            @Override
            public void onFail(String reason) {

            }

            @Override
            public void onError(String errorMessage) {

            }
        }).show(screen.stage);
    }

    @Override
    public Dialog show(Stage stage) {
        super.show(stage);
        showStatusDialog();
        return this;
    }

    private void showStatusDialog() {
        if (Constant.userProfile.getStatus() != null && Constant.userProfile.getStatus().matches("REQUIRED")) {
            openNotifyDialogForKyc("Kyc Required", Constant.userProfile.getRedeemErrorMessage(), "Proceed");
        } else if (Constant.userProfile.getStatus() != null && Constant.userProfile.getStatus().matches("FAILED")) {
            openNotifyDialogForKyc("Kyc Failed", Constant.userProfile.getRedeemErrorMessage(), "Proceed");
        } else if (Constant.userProfile.getStatus() != null && Constant.userProfile.getStatus().matches("PENDING")) {
            openNotifyDialog("Kyc Pending", Constant.userProfile.getRedeemErrorMessage(), "OK");
        }
    }
}
