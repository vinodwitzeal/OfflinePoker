package bigcash.poker.dialogs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

import bigcash.poker.constants.Constant;
import bigcash.poker.font.FontPool;
import bigcash.poker.font.FontType;
import bigcash.poker.models.CashAddOffer;
import bigcash.poker.models.CashBackDto;
import bigcash.poker.models.UIDialog;
import bigcash.poker.models.UIScreen;
import bigcash.poker.network.ApiHandler;
import bigcash.poker.network.GdxListener;
import bigcash.poker.network.Payment;
import bigcash.poker.network.RazorPayMethod;
import bigcash.poker.utils.AssetsLoader;
import bigcash.poker.utils.DrawableBuilder;
import bigcash.poker.utils.GamePreferences;
import bigcash.poker.utils.PokerTimer;
import bigcash.poker.utils.PokerUtils;
import bigcash.poker.utils.TextureDrawable;
import bigcash.poker.utils.TimeoutHandler;
import bigcash.poker.widgets.HtmlTextField;
import bigcash.poker.widgets.StyledLabel;
import bigcash.poker.widgets.WalletTable;

public class CashAddDialog extends UIDialog {
    private TextureAtlas uiAtlas;
    private Label walletLabel;
    private GdxListener<String> responseListener;
    private String errorMessage, cashBackId;
    private String paymentType = "";
    private boolean isReport;
    private CashBackDto cashBackDto;
    private boolean isCashBackSet;
    private Label paytmPayLabel, paytmWalletBalance;
    private ProcessDialog processDialog;
    private String source = "HOME";
    private float entryFee;
    private PokerTimer dailySpinTimer;
    private Table amountTable;
    private TextField amountField;
    private CashButtonStyle cashButtonStyle;
    private ButtonGroup<CashButton> cashButtonButtonGroup;
    private boolean paymentStarted;

    public CashAddDialog(UIScreen screen,GdxListener<String> responseListener) {
        super(screen);
        getCashBackDto();
        this.responseListener = responseListener;
        dismissOnBack(true);
        buildDialog();
    }

    public CashAddDialog(UIScreen screen, String source, float amount) {
        this(screen, source, amount, null);
    }

    public CashAddDialog(UIScreen screen, String source, float amount, GdxListener<String> listener) {
        super(screen);
        getCashBackDto();
        this.source = source;
        this.entryFee = amount;
        if (listener == null) {
            this.responseListener = new GdxListener<String>() {
                @Override
                public void onSuccess(String s) {

                }

                @Override
                public void onFail(String reason) {

                }

                @Override
                public void onError(String error) {

                }
            };
        } else {
            this.responseListener = listener;
        }
        dismissOnBack(true);
        buildDialog();
    }

    @Override
    public void pause() {
        super.pause();
    }

    @Override
    public void resume() {
        if (paymentStarted){
            paymentStarted=false;
            PokerUtils.setTimeOut(3000, new TimeoutHandler() {
                @Override
                public void onTimeOut() {
                    processDialog.hide();
                }
            });
        }
    }

    private void getCashBackDto() {
       cashBackDto=Constant.userProfile.cashBackDto;
    }

    private void resetCashBackDto() {
        Constant.userProfile.setCashBackDto(null);
    }

    @Override
    public void init() {
        uiAtlas = AssetsLoader.instance().uiAtlas;
    }

    @Override
    public void buildDialog() {
        Table contentTable = getContentTable();
        contentTable.top();
        NinePatchDrawable grayBackground = new DrawableBuilder(20, 20).color(Color.valueOf("ebebeb")).createNinePatch();
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

        headerTable.add(new Label("Add Cash", titleStyle)).expandX().align(Align.left);

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
        walletLabel = new Label(Constant.userProfile.getPaytmBalance() + "", headerLabelStyle);
        paytmWalletTable.add(walletLabel).padLeft(walletPad).padRight(walletPad).expandX().align(Align.right);
        paytmWalletTable.add(new Image(addTexture)).width(addWidth).height(addHeight);
        headerTable.add(paytmWalletTable).height(walletTableHeight).padRight(8 * density);
        float headerHeight = walletTableHeight * 2;
        contentTable.add(headerTable).width(width).height(headerHeight).row();

        Label.LabelStyle headingStyle = new Label.LabelStyle();
        headingStyle.font = FontPool.obtain(FontType.ROBOTO_BOLD, 10);
        headingStyle.fontColor = Color.BLACK;
        NinePatchDrawable whiteBackground = new NinePatchDrawable(new NinePatch(uiAtlas.findRegion("bg_content_full"), 8, 8, 8, 8));


        Table scrollTable = new Table();
        scrollTable.top();


        if (cashBackDto != null && cashBackDto.getDescription() != null) {
            Table addTable = new Table();
            addTable.setBackground(whiteBackground);
            Table offerTable = new Table();
            Label.LabelStyle descriptionStyle = new Label.LabelStyle();
            descriptionStyle.font = FontPool.obtain(FontType.ROBOTO_BOLD, 5);
            descriptionStyle.fontColor = Color.SCARLET;
            Label descriptionLabel = new Label(cashBackDto.getDescription(), descriptionStyle);
            descriptionLabel.addAction(Actions.repeat(RepeatAction.FOREVER, Actions.sequence(
                    Actions.fadeIn(0.1f), Actions.delay(1.0f),
                    Actions.fadeOut(0.1f)

            )));
            descriptionLabel.setWrap(true);
            descriptionLabel.setAlignment(Align.center);
            offerTable.add(descriptionLabel).width(width * 0.75f);
            addTable.add(offerTable);
            scrollTable.add(addTable).expandX().fillX().padLeft(4 * density).padRight(4 * density).padTop(2 * density).row();
        }

        Table fronTable = new Table();
        fronTable.pad(4 * density);

        fronTable.setBackground(whiteBackground);

        cashButtonStyle = new CashButtonStyle();
        NinePatchDrawable cashUp = new NinePatchDrawable(new NinePatch(uiAtlas.findRegion("bgt_grey"), 17, 17, 17, 17));
        NinePatchDrawable cashCheck = new NinePatchDrawable(new NinePatch(uiAtlas.findRegion("bgt_green"), 17, 17, 17, 17));
        cashUp.setMinWidth(20);
        cashUp.setMinHeight(20);
        cashCheck.setMinWidth(20);
        cashCheck.setMinHeight(20);

        cashButtonStyle.up = cashUp;
        cashButtonStyle.down = cashUp;
        cashButtonStyle.checked = cashCheck;

        cashButtonStyle.lifeTexture = uiAtlas.findRegion("token");

        cashButtonStyle.checkTexture = uiAtlas.findRegion("icon_check");

        cashButtonStyle.addStyle = new Label.LabelStyle();
        cashButtonStyle.addStyle.font = FontPool.obtain(FontType.ROBOTO_REGULAR, 6);
        cashButtonStyle.addStyle.fontColor = Color.valueOf("393939");

        cashButtonStyle.amountStyle = new Label.LabelStyle();
        cashButtonStyle.amountStyle.font = FontPool.obtain(FontType.ROBOTO_BOLD, 8);
        cashButtonStyle.amountStyle.fontColor = Color.BLACK;

        cashButtonStyle.offerStyle = new Label.LabelStyle();
        cashButtonStyle.offerStyle.font = FontPool.obtain(FontType.ROBOTO_REGULAR, 4);
        cashButtonStyle.offerStyle.fontColor = Color.valueOf("393939");

        cashButtonStyle.lifeStyle = new Label.LabelStyle();
        cashButtonStyle.lifeStyle.font = FontPool.obtain(FontType.ROBOTO_REGULAR, 5);
        cashButtonStyle.lifeStyle.fontColor = Color.valueOf("393939");

        cashButtonStyle.rupeeStyle = new Label.LabelStyle();
        cashButtonStyle.rupeeStyle.font = FontPool.obtain(FontType.ROBOTO_REGULAR, 5);
        cashButtonStyle.rupeeStyle.fontColor = Color.SCARLET;


        Table messageTable = new Table();
        final Label.LabelStyle minMessageStyle = new Label.LabelStyle();
        minMessageStyle.font = FontPool.obtain(FontType.ROBOTO_REGULAR, 7);
        minMessageStyle.fontColor = Color.DARK_GRAY;

        Label.LabelStyle minPriceStyle = new Label.LabelStyle();
        minPriceStyle.font = FontPool.obtain(FontType.ROBOTO_REGULAR, 7);
        minPriceStyle.fontColor = Color.valueOf("202ca4");
        messageTable.add(new Label("Minimum add amount ", minMessageStyle));
        messageTable.add(new Label("\u20b9 " + Constant.minAddCashThreshold, minPriceStyle));

        Table textTable = new Table();
        NinePatchDrawable textBackground = new NinePatchDrawable(new NinePatch(uiAtlas.findRegion("bg_atext"), 14, 14, 14, 14));
        textTable.setBackground(textBackground);
        Label.LabelStyle symbolStyle = new Label.LabelStyle();
        symbolStyle.font = FontPool.obtain(FontType.ROBOTO_BOLD, 10);
        symbolStyle.fontColor = Color.DARK_GRAY;
        TextField.TextFieldStyle textFieldStyle = new TextField.TextFieldStyle();
        textFieldStyle.font = symbolStyle.font;
        textFieldStyle.cursor = new DrawableBuilder(4, (int) symbolStyle.font.getCapHeight()).color(Color.valueOf("202ca4")).createDrawable();
        textFieldStyle.fontColor = Color.BLACK;
        textFieldStyle.messageFontColor = Color.valueOf("494748");

        textTable.add(new Label("\u20b9", symbolStyle)).padLeft(2 * density).padRight(2 * density);
        amountField = new TextField("", textFieldStyle);
        amountField.setAlignment(Align.left);
        amountField.setTextFieldFilter(new TextField.TextFieldFilter() {
            @Override
            public boolean acceptChar(TextField textField, char c) {
                return Character.isDigit(c);
            }
        });

        amountField.setTextFieldListener(new TextField.TextFieldListener() {
            @Override
            public void keyTyped(TextField textField, char c) {
                if (Constant.userProfile.isPaytmAccountLinked()) {
                    paytmWalletBalance.setText(Constant.userProfile.getPaytmWalletText(getAmount(textField)));
                }
            }
        });
        HtmlTextField.setTextInput(amountField,"Enter Amount", HtmlTextField.NUMBER);
        textTable.add(amountField).expandX().fillX();
        amountTable = new Table();
        cashButtonButtonGroup = new ButtonGroup<CashButton>();
        cashButtonButtonGroup.setMinCheckCount(0);
        cashButtonButtonGroup.setMaxCheckCount(1);
        ScrollPane scrollPane = new ScrollPane(amountTable);
        scrollPane.setScrollingDisabled(false, true);
        fronTable.add(scrollPane).expandX().fillX().padTop(4 * density).row();

        Array<CashAddOffer> arrCashAddOffer = new Array<CashAddOffer>();

        boolean setChecked = true;

        if (Constant.userProfile.getCashBackDto() == null || Constant.userProfile.getCashBackDto().size() <= 1) {
            if (Constant.userProfile.isPaytmAccountLinked() && Constant.userProfile.getPaytmAccountBalance() >= 100.0f) {
                arrCashAddOffer.add(new CashAddOffer(100, 0));
                arrCashAddOffer.add(new CashAddOffer(200, 0));
                arrCashAddOffer.add(new CashAddOffer(500, 0));
                arrCashAddOffer.add(new CashAddOffer(1000, 0));
                arrCashAddOffer.add(new CashAddOffer(5000, 0));
            } else {
                arrCashAddOffer.add(new CashAddOffer(20, 0));
                arrCashAddOffer.add(new CashAddOffer(50, 0));
                arrCashAddOffer.add(new CashAddOffer(100, 0));
                arrCashAddOffer.add(new CashAddOffer(200, 0));
                arrCashAddOffer.add(new CashAddOffer(500, 0));
                arrCashAddOffer.add(new CashAddOffer(1000, 0));
                arrCashAddOffer.add(new CashAddOffer(5000, 0));
            }
            boolean isSelectedOffer = false;

            for (int i = 0; i < arrCashAddOffer.size; i++) {
                CashButton cashButton = new CashButton(cashButtonStyle, arrCashAddOffer.get(i).getAddCash(), amountField);
                cashButtonButtonGroup.add(cashButton);
                if (setChecked) {
                    if (i == 0 && entryFee <= 20 && !isSelectedOffer) {
                        cashButton.setChecked(true);
                    }
                    if (entryFee == 0 && Constant.userProfile.isPaytmAccountLinked() && Constant.userProfile.getPaytmAccountBalance() >= 20.0f
                            && arrCashAddOffer.get(i).getAddCash() <= Constant.userProfile.getPaytmAccountBalance() && arrCashAddOffer.get(i).getAddCash() <= 100) {
                        cashButton.setChecked(true);
                    }
                    if (!isSelectedOffer && entryFee > 20 && Constant.userProfile.isPaytmAccountLinked()
                            && Constant.userProfile.getPaytmAccountBalance() > entryFee
                            && entryFee <= arrCashAddOffer.get(i).getAddCash() && arrCashAddOffer.get(i).getAddCash() <= Constant.userProfile.getPaytmAccountBalance()) {
                        isSelectedOffer = true;
                        cashButton.setChecked(true);
                    }
                }
                amountTable.add(cashButton).uniform().fill().padRight(12 * density);
            }
            if (entryFee > 20 && !isSelectedOffer) {
                amountField.setText((int) entryFee + "");
            }
        } else {
            boolean isSelectedOffer = false;

            List<CashBackDto> arrCashBackDtos = Constant.userProfile.getCashBackDto();
            for (int i = 0; i < arrCashBackDtos.size(); i++) {
                CashButton cashButton = new CashButton(cashButtonStyle, arrCashBackDtos.get(i).getMinimumAmount(), amountField, arrCashBackDtos.get(i));
                cashButtonButtonGroup.add(cashButton);
                if (setChecked) {
                    if (!isSelectedOffer && entryFee <= arrCashBackDtos.get(i).getMinimumAmount()) {
                        cashButton.setChecked(true);
                        isSelectedOffer = true;
                    }
                }
                amountTable.add(cashButton).uniform().fill().padRight(12 * density);
            }
            if (entryFee > 20 && !isSelectedOffer) {
                amountField.setText((int) entryFee + "");
            }

        }


        amountField.addListener(new InputListener() {
                                    @Override
                                    public boolean keyTyped(InputEvent event, char character) {
                                        cashButtonButtonGroup.uncheckAll();
                                        return super.keyTyped(event, character);

                                    }
                                }
        );

        amountField.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                cashButtonButtonGroup.uncheckAll();
                minMessageStyle.fontColor = Color.DARK_GRAY;
                paytmWalletBalance.setText(Constant.userProfile.getPaytmWalletText(getAmount(amountField)));
            }
        });

        fronTable.add(messageTable).expandX().align(Align.left).padTop(15 * density).row();
        fronTable.add(textTable).expandX().fillX().row();

        Label.LabelStyle optionStyle = new Label.LabelStyle();
        optionStyle.font = FontPool.obtain(FontType.ROBOTO_REGULAR, 8);
        optionStyle.fontColor = Color.DARK_GRAY;

        fronTable.add(new Label("Pick a Payment Option", optionStyle)).expandX().align(Align.left).padTop(20 * density).padBottom(4 * density).row();

        NinePatchDrawable payBackground = new NinePatchDrawable(new NinePatch(uiAtlas.findRegion("bg_winner"), 9, 9, 9, 9));
        payBackground.setMinHeight(20);
        payBackground.setMinWidth(20);
        StyledLabel.StyledLabelStyle payStyle = new StyledLabel.StyledLabelStyle(FontType.ROBOTO_BOLD, 8);
        payStyle.fontColor = Color.WHITE;

        Table buttonTable = new Table();
        StyledLabel.StyledLabelStyle merchantStyle = new StyledLabel.StyledLabelStyle(FontType.ROBOTO_BOLD, 10);
        merchantStyle.fontColor = Color.WHITE;

        float iconWidth = merchantStyle.font.getLineHeight();
        float iconHeight;
        Table paytmTable = new Table();
        paytmTable.pad(0, 4 * density, 0, 8 * density);
        NinePatchDrawable paytmBackground = new NinePatchDrawable(new NinePatch(uiAtlas.findRegion("bg_sky"), 9, 9, 9, 9));
        paytmBackground.setMinWidth(20);
        payBackground.setMinHeight(20);
        paytmTable.setBackground(paytmBackground);

        TextureRegion paytmTexture = uiAtlas.findRegion("icon_paytm_square");
        iconHeight = iconWidth * paytmTexture.getRegionHeight() / paytmTexture.getRegionWidth();
        paytmTable.add(new Image(paytmTexture)).width(iconWidth).height(iconHeight);
        Table paytmLabelTable = new Table();
        paytmLabelTable.add(getStyledLabel("Paytm", merchantStyle)).align(Align.left);
        StyledLabel.StyledLabelStyle paytmDetailsStyle = new StyledLabel.StyledLabelStyle(FontType.ROBOTO_BOLD, 6);
        paytmDetailsStyle.fontColor = Color.WHITE;
        paytmWalletBalance = getStyledLabel("", paytmDetailsStyle);
        paytmLabelTable.add(paytmWalletBalance).align(Align.left).padLeft(5 * density);
        paytmTable.add(paytmLabelTable).padLeft(5 * density);
        if (Constant.userProfile.isPaytmAccountLinked()) {
            paytmWalletBalance.setText(Constant.userProfile.getPaytmWalletText(getAmount(amountField)));
            paytmPayLabel = getStyledLabel("ADD", payStyle);
        } else {
            // paytmTable.add(getStyledLabel("Paytm", merchantStyle)).padLeft(8 * density);
            paytmPayLabel = getStyledLabel("LINK", payStyle);
        }
        paytmTable.add(paytmPayLabel).expandX().align(Align.right);


        StyledLabel.StyledLabelStyle detailsStyle = new StyledLabel.StyledLabelStyle(FontType.ROBOTO_REGULAR, 4);
        detailsStyle.fontColor = Color.BLACK;


        Table upiTable = new Table();
        upiTable.pad(0, 4 * density, 0, 8 * density);
        NinePatchDrawable upiBackground = new NinePatchDrawable(new NinePatch(uiAtlas.findRegion("bg_cyan"), 9, 9, 9, 9));
        upiBackground.setMinWidth(20);
        upiBackground.setMinHeight(20);
        upiTable.setBackground(upiBackground);
        TextureRegion upiTexture = uiAtlas.findRegion("icon_upi");
        iconHeight = iconWidth * upiTexture.getRegionHeight() / upiTexture.getRegionWidth();
        upiTable.add(new Image(upiTexture)).width(iconWidth).height(iconHeight);
        upiTable.add(getStyledLabel("UPI", merchantStyle)).padLeft(8 * density);
        upiTable.add(getStyledLabel("ADD", payStyle)).expandX().align(Align.right);

        buttonTable.add(paytmTable).height(height * .075f).uniformY().fillY().expandX().fillX().padBottom(4 * density).row();
//        buttonTable.add(razorTable).uniformY().fillY().expandX().fillX().padBottom(8*density).row();
        buttonTable.add(upiTable).height(height * .075f).uniformY().fillY().expandX().fillX().padBottom(2 * density).row();

        Table bottomTable = new Table();

        float textureheight, texturewidth;
        textureheight = height * .075f;

        Table debit = new Table();
        TextureRegion debitTexture = uiAtlas.findRegion("ic_debit");
        Label cardLabel = new Label("Debit/Credit", detailsStyle);
        texturewidth = textureheight * debitTexture.getRegionWidth() / debitTexture.getRegionHeight();
        debit.add(new Image(debitTexture)).width(texturewidth).height(textureheight).row();
        debit.add(cardLabel);
        bottomTable.add(debit).width(texturewidth).height(textureheight).padTop(5 * density).expandX();

        Table google = new Table();
        TextureRegion creditTexture = uiAtlas.findRegion("ic_googlepay");
        Label googleLabel = new Label("Google Pay", detailsStyle);
        texturewidth = textureheight * creditTexture.getRegionWidth() / creditTexture.getRegionHeight();
        google.add(new Image(creditTexture)).width(texturewidth).height(textureheight).row();
        google.add(googleLabel);
        bottomTable.add(google).width(texturewidth).height(textureheight).padTop(5 * density).expandX();

        Table phonePay = new Table();
        TextureRegion phonePayTexture = uiAtlas.findRegion("ic_phonepay");
        texturewidth = textureheight * phonePayTexture.getRegionWidth() / phonePayTexture.getRegionHeight();
        phonePay.add(new Image(phonePayTexture)).width(texturewidth).height(textureheight).row();
        phonePay.add(new Label("PhonePe", detailsStyle));
        bottomTable.add(phonePay).width(texturewidth).height(textureheight).padTop(5 * density).expandX().row();

        Table netbanking = new Table();
        TextureRegion netTexture = uiAtlas.findRegion("ic_net");
        texturewidth = textureheight * netTexture.getRegionWidth() / netTexture.getRegionHeight();
        netbanking.add(new Image(netTexture)).width(texturewidth).height(textureheight).row();
        netbanking.add(new Label("Net Banking", detailsStyle));
        bottomTable.add(netbanking).width(texturewidth).height(textureheight).padTop(20 * density).expandX();

        Table wallet = new Table();
        TextureRegion walletTexture = uiAtlas.findRegion("ic_wallet");
        texturewidth = textureheight * walletTexture.getRegionWidth() / walletTexture.getRegionHeight();
        wallet.add(new Image(walletTexture)).width(texturewidth).height(textureheight).row();
        wallet.add(new Label("Wallet", detailsStyle));
        bottomTable.add(wallet).width(texturewidth).height(textureheight).padTop(20 * density).expandX();

        Table others = new Table();
        TextureRegion otherTexture = uiAtlas.findRegion("ic_others");
        texturewidth = textureheight * otherTexture.getRegionWidth() / otherTexture.getRegionHeight();
        others.add(new Image(otherTexture)).width(texturewidth).height(textureheight).row();
        others.add(new Label("Others", detailsStyle));
        bottomTable.add(others).width(texturewidth).height(textureheight).padTop(20 * density).expandX();


        fronTable.add(buttonTable).expandX().fillX().row();
        fronTable.add(bottomTable).expandX().fillX().padTop(10 * density).padLeft(5 * density).padRight(5 * density).padBottom(10 * density).row();


        scrollTable.add(fronTable).expandX().fillX().pad(8 * density).expandX().row();

        Label.LabelStyle termsStyle = new Label.LabelStyle();
        termsStyle.font = FontPool.obtain(FontType.ROBOTO_REGULAR, 6);
        termsStyle.fontColor = Color.BLUE;
        Label tcLabel = new Label("Terms and Conditions Apply*", termsStyle);
        ScrollPane dataScrollPane = new ScrollPane(scrollTable);
        dataScrollPane.setScrollingDisabled(true, false);
        contentTable.add(dataScrollPane).expand().fill().align(Align.top).row();
        contentTable.add(tcLabel).padTop(20 * density);

        paytmTable.setTouchable(Touchable.enabled);
//        razorTable.setTouchable(Touchable.enabled);
        upiTable.setTouchable(Touchable.enabled);
        debit.setTouchable(Touchable.enabled);
        google.setTouchable(Touchable.enabled);
        phonePay.setTouchable(Touchable.enabled);
        netbanking.setTouchable(Touchable.enabled);
        wallet.setTouchable(Touchable.enabled);
        others.setTouchable(Touchable.enabled);
        processDialog = new ProcessDialog(screen, "Please wait..");
        final GdxListener<String> listener = new GdxListener<String>() {
            @Override
            public void setProcessing() {
                paymentStarted=false;
            }

            @Override
            public void onSuccess(String s) {
                responseListener.onSuccess(s);
                screen.updateBalance();
                processDialog.hide();
                resetCashBackDto();
                hide();
                toast("Cash Added Successfully");
            }

            @Override
            public void onFail(String reason) {
                processDialog.hide();
                isReport = true;
                ApiHandler.callEventLogApi("ADD_CASH", paymentType, reason);
            }

            @Override
            public void onError(String errorMessage) {
                processDialog.hide();
                toast(errorMessage);
                isReport = true;
                ApiHandler.callEventLogApi("ADD_CASH", paymentType, errorMessage);
            }
        };


        paytmTable.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (Constant.userProfile.isPaytmAccountLinked()) {
                    float amount = isValidAmountEntered(amountField.getText());
                    if (amount > 0) {
                        processDialog.show(getStage());
                        if (cashBackDto != null && amount >= cashBackDto.getMinimumAmount()) {
                            cashBackId = cashBackDto.getCashBackId();
                        } else {
                            cashBackId = null;
                        }
                        paymentStarted=true;
                        addWithPaytm(amount, processDialog, listener);
                        paymentType = "PAYTM";
                    } else {
                        if (errorMessage != null && !errorMessage.matches("")) {
                            toast(errorMessage);
                        } else {
                            minMessageStyle.fontColor = Color.SCARLET;
                        }
                    }
                } else {
                    new PaytmNumberVerifiedDialog(screen, true, Constant.userProfile.getMsisdn(), new GdxListener<String>() {
                        @Override
                        public void onSuccess(String s) {
                            paytmPayLabel.setText("ADD");
                            paytmWalletBalance.setText(Constant.userProfile.getPaytmWalletText(getAmount(amountField)));
                            if (Constant.userProfile.getAlertMessage() != null) {
//                                new AlertDialog(getModelScreen(), Constant.userProfile.getAlertMessage()).show(getStage());
                                Constant.userProfile.setAlertMessage(null);
                            }
                            updateBalance();
                            screen.updateBalance();
                        }

                        @Override
                        public void onFail(String reason) {

                        }

                        @Override
                        public void onError(String errorMessage) {

                        }
                    }).show(getStage());
                }
            }
        });


        debit.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                float amount = isValidAmountEntered(amountField.getText());
                if (amount > 0) {
                    processDialog.show(getStage());
                    if (cashBackDto != null && amount >= cashBackDto.getMinimumAmount()) {
                        cashBackId = cashBackDto.getCashBackId();
                    } else {
                        cashBackId = null;
                    }
                    Payment.payWithRazorOptions(amount, cashBackId, RazorPayMethod.getCard(), source, listener);
                    paymentType = "RAZORPAY";
                } else {
                    if (errorMessage != null && !errorMessage.matches("")) {
                        toast(errorMessage);
                    } else {
                        minMessageStyle.fontColor = Color.SCARLET;
                    }
                }

            }
        });

        google.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                float amount = isValidAmountEntered(amountField.getText());
                if (amount > 0) {
                    processDialog.show(getStage());
                    if (cashBackDto != null && amount >= cashBackDto.getMinimumAmount()) {
                        cashBackId = cashBackDto.getCashBackId();
                    } else {
                        cashBackId = null;
                    }
                    Payment.payWithRazorOptions(amount, cashBackId, RazorPayMethod.getUPI(), source, listener);
                    paymentType = "RAZORPAY";
                } else {
                    if (errorMessage != null && !errorMessage.matches("")) {
                        toast(errorMessage);
                    } else {
                        minMessageStyle.fontColor = Color.SCARLET;
                    }
                }

            }
        });

        phonePay.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                float amount = isValidAmountEntered(amountField.getText());
                if (amount > 0) {
                    processDialog.show(getStage());
                    if (cashBackDto != null && amount >= cashBackDto.getMinimumAmount()) {
                        cashBackId = cashBackDto.getCashBackId();
                    } else {
                        cashBackId = null;
                    }
                    Payment.payWithRazorOptions(amount, cashBackId, RazorPayMethod.getUPI(), source, listener);
                    paymentType = "RAZORPAY";
                } else {
                    if (errorMessage != null && !errorMessage.matches("")) {
                        toast(errorMessage);
                    } else {
                        minMessageStyle.fontColor = Color.SCARLET;
                    }
                }

            }
        });

        netbanking.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                float amount = isValidAmountEntered(amountField.getText());
                if (amount > 0) {
                    processDialog.show(getStage());
                    if (cashBackDto != null && amount >= cashBackDto.getMinimumAmount()) {
                        cashBackId = cashBackDto.getCashBackId();
                    } else {
                        cashBackId = null;
                    }
                    Payment.payWithRazorOptions(amount, cashBackId, RazorPayMethod.getNetBanking(), source, listener);
                    paymentType = "RAZORPAY";
                } else {
                    if (errorMessage != null && !errorMessage.matches("")) {
                        toast(errorMessage);
                    } else {
                        minMessageStyle.fontColor = Color.SCARLET;
                    }
                }

            }
        });

        others.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                float amount = isValidAmountEntered(amountField.getText());
                if (amount > 0) {
                    processDialog.show(getStage());
                    if (cashBackDto != null && amount >= cashBackDto.getMinimumAmount()) {
                        cashBackId = cashBackDto.getCashBackId();
                    } else {
                        cashBackId = null;
                    }
                    Payment.payWithRazor(amount, cashBackId, source, listener);
                    paymentType = "RAZORPAY";
                } else {
                    if (errorMessage != null && !errorMessage.matches("")) {
                        toast(errorMessage);
                    } else {
                        minMessageStyle.fontColor = Color.SCARLET;
                    }
                }
            }
        });

        wallet.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                float amount = isValidAmountEntered(amountField.getText());
                if (amount > 0) {
                    processDialog.show(getStage());
                    if (cashBackDto != null && amount >= cashBackDto.getMinimumAmount()) {
                        cashBackId = cashBackDto.getCashBackId();
                    } else {
                        cashBackId = null;
                    }
                    Payment.payWithRazorOptions(amount, cashBackId, RazorPayMethod.getWallet(), source, listener);
                    paymentType = "RAZORPAY";
                } else {
                    if (errorMessage != null && !errorMessage.matches("")) {
                        toast(errorMessage);
                    } else {
                        minMessageStyle.fontColor = Color.SCARLET;
                    }
                }

            }
        });


        upiTable.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                float amount = isValidAmountEntered(amountField.getText());
                if (amount > 0) {
                    processDialog.show(getStage());
                    if (cashBackDto != null && amount >= cashBackDto.getMinimumAmount()) {
                        cashBackId = cashBackDto.getCashBackId();
                    } else {
                        cashBackId = null;
                    }
                    Payment.payWithRazorOptions(amount, cashBackId, RazorPayMethod.getUPI(), source, listener);
                    paymentType = "RAZORPAY";
                } else {
                    if (errorMessage != null && !errorMessage.matches("")) {
                        toast(errorMessage);
                    } else {
                        minMessageStyle.fontColor = Color.SCARLET;
                    }
                }

            }
        });


        tcLabel.setTouchable(Touchable.enabled);
        tcLabel.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ProcessDialog processDialog = new ProcessDialog(screen, "Please Wait...");
                processDialog.show(screen.stage);
                ApiHandler.callPrivacyPolicyApi("ADDCASH", new GdxListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        processDialog.hide();
                        new PolicyDialog(screen, s).show(screen.stage);
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
    }

    private float isValidAmountEntered(String text) {
        if (text != null && !text.isEmpty()) {
            float addAmount = 0;
            try {
                addAmount = Float.parseFloat(text);
                if (addAmount > 20000) {
                    errorMessage = "Maximum add cash limit is \u20b9 20000.";
                }
                if (addAmount >= Constant.minAddCashThreshold && addAmount <= 20000) {
                    setCashBackDtoNotFirstTimeUser(addAmount);
                    return addAmount;
                }
            } catch (Exception e) {
            }
        }
        return -1.0f;
    }

    private void setCashBackDtoNotFirstTimeUser(float addAmount) {
        float cashBack = 0;
        if (Constant.userProfile.getCashBackDto() != null) {
            for (CashBackDto cashBackDto : Constant.userProfile.getCashBackDto()) {
                if (addAmount >= cashBackDto.getMinimumAmount()) {
                    this.cashBackDto = cashBackDto;
                    cashBack = addAmount * cashBackDto.getOfferInPercentage() / 100;
                    if (cashBack > cashBackDto.getMaximumCashBack()) {
                        cashBack = cashBackDto.getMaximumCashBack();
                    }
                }
            }
        }
    }

    private Table getPayTable(String text, Label.LabelStyle labelStyle, NinePatchDrawable background) {
        Table table = new Table();
        table.pad(4 * density, 16 * density, 4 * density, 16 * density);
        table.setBackground(background);
        table.add(new Label("Add", labelStyle));
        return table;
    }

    private StyledLabel getStyledLabel(String label, StyledLabel.StyledLabelStyle styledLabelStyle) {
        StyledLabel styledLabel = new StyledLabel(label, styledLabelStyle);
        styledLabel.outline(0.1f, Color.DARK_GRAY);
        styledLabel.shadow(0.1f, 0.1f, Color.BLACK);
        return styledLabel;
    }


    private void addWithPaytm(final float amount, ProcessDialog processDialog, final GdxListener<String> listener) {
        final String orderId = "CASH" + GamePreferences.instance().getUserId() + "-" + TimeUtils.millis();
        final GdxListener<String> paytmListener = new GdxListener<String>() {
            @Override
            public void setProcessing() {
                paymentStarted=false;
            }

            @Override
            public void onSuccess(String s) {
                listener.setSuccess(s);
            }

            @Override
            public void onFail(String reason) {
                listener.setFail(reason);
            }

            @Override
            public void onError(String errorMessage) {
                if (errorMessage.matches("402")) {
                    if (Constant.userProfile.isPaytmAccountLinked()) {
                        paytmPayLabel.setText("ADD");
                        paytmWalletBalance.setText(Constant.userProfile.getPaytmWalletText(amount));
                        ApiHandler.callChecksumApi(orderId, amount, cashBackId, processDialog, source, listener);
                    } else {
                        paytmPayLabel.setText("LINK");
                        paytmWalletBalance.setText("");
                        listener.setError("Link again.");
                    }

                } else {
                    listener.setError(errorMessage);
                }
            }
        };
        if (Constant.userProfile.getPaytmAccountBalance() >= amount || (Constant.userProfile.isPaytmPostPaidActive() && Constant.userProfile.getPaytmPostPaidBalance() > 0.0f)) {
            ApiHandler.callAddCashApi("PAYTM", amount + "", orderId, orderId,
                    source, cashBackId, listener);
        } else {
            ApiHandler.callChecksumApi(orderId, amount, cashBackId, processDialog, source, paytmListener);
        }
    }


    private void updateBalance() {
        walletLabel.setText(Constant.userProfile.getPaytmBalance() + "");
    }

    @Override
    public void hide() {
        super.hide();
        if (!isReport) {
            ApiHandler.callEventLogApi("ADD_CASH", "PAYMENT_CANCEL", "CANCEL");
        }
        if (dailySpinTimer != null) {
            dailySpinTimer.cancel();
            dailySpinTimer = null;
        }
    }


    //    Date getDate(long millis) {
//        SimpleDateFormat myFormat = new SimpleDateFormat("dd-MM-yyyy");
//        Date date = new Date(millis);
//        return date;
//    }
//
//    Date getDate() {
//        try {
//            String sDate1 = "21-05-2019";
//            return new SimpleDateFormat("dd-MM-yyyy").parse(sDate1);
//        } catch (Exception e) {
//
//        }
//        return null;
//    }

    private float getAmount(TextField amountField) {
        if (amountField != null && amountField.getText() != null && !amountField.getText().isEmpty()) {
            return Float.parseFloat(amountField.getText());
        } else {
            return 20;
        }
    }

    private class CashButton extends Button {
        public Image checkImage;
        private Label cashBackText;
        private int amount;
        private TextField amountField;


        public CashButton(CashButtonStyle buttonStyle, final int amount, final TextField amountField) {
            super(buttonStyle);
            this.amount = amount;
            this.amountField = amountField;
            Table addTable = new Table();
            final float checkSize = buttonStyle.addStyle.font.getCapHeight();
            checkImage = new Image(buttonStyle.checkTexture);
            addTable.add(checkImage).width(checkSize).height(checkSize);
            addTable.add(new Label("Add", buttonStyle.addStyle)).expandX();
            addTable.add().width(checkSize).height(checkSize);
            add(addTable).expandX().fillX().row();
            if (cashBackDto != null && amount >= cashBackDto.getMaximumCashBack()) {
                add(new Label("\u20b9 " + amount, buttonStyle.amountStyle)).padTop(4 * density).row();
                int cashback = amount * cashBackDto.getOfferInPercentage() / 100;
                if (cashback >= cashBackDto.getMaximumCashBack()) {
                    cashback = cashBackDto.getMaximumCashBack();
                }
                if (!isCashBackSet && entryFee == 0) {
                    setChecked(true);
                    isCashBackSet = true;
                }
                Table offerTable=new Table();
                cashBackText=new Label("Cashback ",buttonStyle.offerStyle);
                add(cashBackText).padTop(4 * density).row();
                offerTable.add(new Label("\u20b9 ",buttonStyle.rupeeStyle));
                offerTable.add(new Label(cashback+"",buttonStyle.lifeStyle));
                add(offerTable).padTop(4 * density).row();
            } else {
                float pad=2*density+buttonStyle.offerStyle.font.getLineHeight()/2f;
                add(new Label("\u20b9 " + amount, buttonStyle.amountStyle)).padTop(pad).padBottom(pad).expandY().row();
            }
            checkImage.setVisible(isChecked());
            addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    checkImage.setVisible(isChecked());
                    amountField.setText(amount + "");
                    if (isChecked()) {
                        if (cashBackText != null) {
                            cashBackText.addAction(Actions.repeat(RepeatAction.FOREVER, Actions.sequence(
                                    Actions.fadeIn(0.1f), Actions.delay(1.0f),
                                    Actions.fadeOut(0.1f)

                            )));
                        }
                        if (paytmWalletBalance != null)
                            paytmWalletBalance.setText(Constant.userProfile.getPaytmWalletText(amount));
                    } else {
                        if (cashBackText != null) {
                            cashBackText.clearActions();
                            cashBackText.setColor(Color.WHITE);
                        }
                    }
                }
            });
        }

        public CashButton(CashButtonStyle buttonStyle, final int amount, final TextField amountField, CashBackDto cashBackDto1) {
            super(buttonStyle);
            this.amount = amount;
            this.amountField = amountField;
            Table addTable = new Table();
            final float checkSize = buttonStyle.addStyle.font.getCapHeight();
            checkImage = new Image(buttonStyle.checkTexture);
            addTable.add(checkImage).width(checkSize).height(checkSize);
            addTable.add(new Label("Add", buttonStyle.addStyle)).expandX();
            addTable.add().width(checkSize).height(checkSize);
            add(addTable).expandX().fillX().row();
            if (cashBackDto1 != null && amount >= cashBackDto1.getMinimumAmount()) {
                add(new Label("\u20b9 " + amount, buttonStyle.amountStyle)).padTop(4 * density).row();
                int cashback = amount * cashBackDto1.getOfferInPercentage() / 100;
                if (cashback >= cashBackDto1.getMaximumCashBack()) {
                    cashback = cashBackDto1.getMaximumCashBack();
                }

                if (!isCashBackSet && entryFee == 0) {
                    setChecked(true);
                    isCashBackSet = true;
                }
                Table offerTable=new Table();
                cashBackText=new Label("Cashback ",buttonStyle.offerStyle);
                add(cashBackText).padTop(4 * density).row();
                offerTable.add(new Label("\u20b9 ",buttonStyle.rupeeStyle));
                offerTable.add(new Label(cashback+"",buttonStyle.lifeStyle));
                add(offerTable).padTop(4 * density).row();
            } else {
                float pad=2*density+buttonStyle.offerStyle.font.getLineHeight()/2f;
                add(new Label("\u20b9 " + amount, buttonStyle.amountStyle)).padTop(pad).padBottom(pad).expandY().row();
            }
            checkImage.setVisible(isChecked());
            addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    checkImage.setVisible(isChecked());
                    amountField.setText(amount + "");
                    if (isChecked()) {
                        if (cashBackText != null) {
                            cashBackText.addAction(Actions.repeat(RepeatAction.FOREVER, Actions.sequence(
                                    Actions.fadeIn(0.1f), Actions.delay(1.0f),
                                    Actions.fadeOut(0.1f)

                            )));
                        }
                        if (paytmWalletBalance != null)
                            paytmWalletBalance.setText(Constant.userProfile.getPaytmWalletText(amount));
                    } else {
                        if (cashBackText != null) {
                            cashBackText.clearActions();
                            cashBackText.setColor(Color.WHITE);
                        }
                    }
                }
            });
        }


        @Override
        public void setChecked(boolean isChecked) {
            super.setChecked(isChecked);
            checkImage.setVisible(isChecked());
            if (isChecked) {
                amountField.setText(amount + "");
                if (cashBackText != null) {
                    cashBackText.addAction(Actions.repeat(RepeatAction.FOREVER, Actions.sequence(
                            Actions.fadeIn(0.1f), Actions.delay(1.0f),
                            Actions.fadeOut(0.1f)

                    )));
                }
                if (paytmWalletBalance != null)
                    paytmWalletBalance.setText(Constant.userProfile.getPaytmWalletText(amount));
            } else {
                if (cashBackText != null) {
                    cashBackText.clearActions();
                    cashBackText.setColor(Color.WHITE);
                }
            }
        }
    }

    private class CashButtonStyle extends Button.ButtonStyle {
        public TextureRegion checkTexture, lifeTexture;
        public Label.LabelStyle addStyle, amountStyle, offerStyle, lifeStyle, cashbackStyle, rupeeStyle;
    }

}
