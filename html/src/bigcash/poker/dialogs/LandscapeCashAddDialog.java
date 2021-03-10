package bigcash.poker.dialogs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.backends.gwt.GwtGraphics;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
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
import bigcash.poker.utils.TextureDrawable;
import bigcash.poker.widgets.HtmlTextField;
import bigcash.poker.widgets.StyledLabel;
import bigcash.poker.widgets.WalletTable;


public class LandscapeCashAddDialog extends UIDialog {
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
    private float balance;
    private String source;
    private float entryFee;
    private Table leftTable,rightTable;

    public LandscapeCashAddDialog(UIScreen modelScreen, String source, float entryFee,GdxListener<String> responseListener) {
        super(modelScreen);
        this.responseListener = responseListener;
        this.balance = Constant.userProfile.getPaytmBalance();
        this.source = source;
        getCashBackDto();
        this.entryFee = entryFee;
        this.density=this.width/360.0f;
        dismissOnBack(true);
//        float a=width;
//        width=height;
//        height=a;
        buildDialog();
    }

    private void getCashBackDto(){
       cashBackDto=Constant.userProfile.cashBackDto;
    }

    private void resetCashBackDto(){
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
        NinePatchDrawable grayBg = new DrawableBuilder(20, 20).color(Color.valueOf("f0f0f0")).createNinePatch();
        NinePatchDrawable DarkgrayBg = new DrawableBuilder(20, 20).color(Color.valueOf("707070")).createNinePatch();
        Table scrollTable = new Table();
        scrollTable.top();
        leftTable=new Table();
        leftTable.setBackground(grayBg);
        rightTable=new Table();

        CashButtonStyle cashButtonStyle = new CashButtonStyle();
        NinePatchDrawable cashUp = new NinePatchDrawable(new NinePatch(uiAtlas.findRegion("bgt_grey"), 17, 17, 17, 17));
        NinePatchDrawable cashCheck = new NinePatchDrawable(new NinePatch(uiAtlas.findRegion("bgt_green"), 17, 17, 17, 17));
        cashUp.setMinWidth(40);
        cashUp.setMinHeight(40);
        cashCheck.setMinWidth(40);
        cashCheck.setMinHeight(40);

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
        cashButtonStyle.offerStyle.font = FontPool.obtain(FontType.ROBOTO_REGULAR, 4.5f);
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
        minPriceStyle.font = FontPool.obtain(FontType.ROBOTO_BOLD, 7);
        minPriceStyle.fontColor = Color.valueOf("202ca4");
        messageTable.add(new Label("Minimum add amount ", minMessageStyle));
        messageTable.add(new Label("\u20b9 " + Constant.minAddCashThreshold, minPriceStyle));

        Table textTable = new Table();
        NinePatchDrawable textBackground = new NinePatchDrawable(new NinePatch(uiAtlas.findRegion("bg_atext"), 14, 14, 14, 14));
        textTable.setBackground(textBackground);
        textTable.pad(4 * density);
        Label.LabelStyle symbolStyle = new Label.LabelStyle();
        symbolStyle.font = FontPool.obtain(FontType.ROBOTO_BOLD, 10);
        symbolStyle.fontColor = Color.DARK_GRAY;
        TextField.TextFieldStyle textFieldStyle = new TextField.TextFieldStyle();
        textFieldStyle.font = symbolStyle.font;
        textFieldStyle.fontColor = Color.BLACK;
        textFieldStyle.messageFontColor = Color.valueOf("494748");
        textTable.add(new Label("\u20b9", symbolStyle)).padLeft(4 * density).padRight(4 * density);
        final TextField amountField = new TextField("20", textFieldStyle){

        };
        if(entryFee>20){
            amountField.setText((int)entryFee+"");
        }
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
        amountField.clearListeners();
        HtmlTextField.setTextInput(amountField,"Enter Amount",HtmlTextField.NUMBER);
        textTable.add(amountField).expandX().fillX();
        Table amountTable = new Table();
        ScrollPane scrollPane = new ScrollPane(amountTable);
        scrollPane.setScrollingDisabled(true, false);
        Label.LabelStyle blackStyle=new Label.LabelStyle();
        blackStyle.font= FontPool.obtain(FontType.ROBOTO_BOLD,6);
        blackStyle.fontColor=Color.BLACK;
        leftTable=new Table();
        leftTable.add(new Label("Choose Amount to Add Money :",blackStyle)).align(Align.left).padLeft(width*.04f).padTop(5*density).row();
        leftTable.add(scrollPane).width(width*.5f).expandX().fillX();
        final ButtonGroup<CashButton> cashButtonButtonGroup = new ButtonGroup<CashButton>();
        cashButtonButtonGroup.setMinCheckCount(0);
        cashButtonButtonGroup.setMaxCheckCount(1);
        Array<CashAddOffer> arrCashAddOffer = new Array<CashAddOffer>();
        if(Constant.userProfile.getCashBackDto() == null || Constant.userProfile.getCashBackDto().size() <=1) {
            if (Constant.userProfile.isPaytmAccountLinked() && Constant.userProfile.getPaytmAccountBalance() > 100.0f) {
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
                CashButton cashButton = new CashButton(cashButtonStyle, arrCashAddOffer.get(i).getAddCash(), arrCashAddOffer.get(i).getLives(), amountField);
                cashButtonButtonGroup.add(cashButton);
                if (i == 0 && entryFee <= 20) {
                    cashButton.setChecked(true);
                }
                if(entryFee == 0 && Constant.userProfile.isPaytmAccountLinked() && Constant.userProfile.getPaytmAccountBalance() >= 20.0f
                        && Constant.userProfile.getPaytmAccountBalance() < 100.0f  && arrCashAddOffer.get(i).getAddCash() <= Constant.userProfile.getPaytmAccountBalance()){
                    cashButton.setChecked(true);
                }
                if (!isSelectedOffer && entryFee > 20 && Constant.userProfile.isPaytmAccountLinked()
                        && Constant.userProfile.getPaytmAccountBalance() > entryFee
                        && entryFee <= arrCashAddOffer.get(i).getAddCash() && arrCashAddOffer.get(i).getAddCash() <= Constant.userProfile.getPaytmAccountBalance()) {
                    isSelectedOffer = true;
                    cashButton.setChecked(true);
                }
                amountTable.add(cashButton).width(width * .4f).pad(5 * density, 10 * density, 0, 10 * density).uniform().fill().row();
            }
            if(entryFee>20 && !isSelectedOffer){
                amountField.setText((int)entryFee+"");
            }
        }else{
            boolean isSelectedOffer = false;
            List<CashBackDto> arrCashBackDtos = Constant.userProfile.getCashBackDto();
            for (int i = 0; i < arrCashBackDtos.size(); i++) {
                CashButton cashButton = new CashButton(cashButtonStyle, arrCashBackDtos.get(i).getMinimumAmount(), arrCashBackDtos.get(i).getMaximumCashBack(), amountField,arrCashBackDtos.get(i));
                cashButtonButtonGroup.add(cashButton);
                if (!isSelectedOffer && entryFee <=  arrCashBackDtos.get(i).getMinimumAmount()) {
                    cashButton.setChecked(true);
                    isSelectedOffer = true;
                }
                amountTable.add(cashButton).width(width * .4f).pad(5 * density, 10 * density, 0, 10 * density).uniform().fill().row();
            }

            if(entryFee>20 && !isSelectedOffer){
                amountField.setText((int)entryFee+"");
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
                minMessageStyle.fontColor = Color.DARK_GRAY;
                paytmWalletBalance.setText(Constant.userProfile.getPaytmWalletText(getAmount(amountField)));
            }
        });

        Table fronTable = new Table();

        fronTable.setBackground(whiteBackground);

        fronTable.add(messageTable).align(Align.left).padLeft(width*.01f).row();
        fronTable.add(textTable).width(width*.45f).height(height*.12f).row();

        Label.LabelStyle optionStyle = new Label.LabelStyle();
        optionStyle.font = FontPool.obtain(FontType.ROBOTO_REGULAR, 8);
        optionStyle.fontColor = Color.DARK_GRAY;

        fronTable.add(new Label("Pick a Payment Option", optionStyle)).align(Align.left).padLeft(width*.01f).padTop(10 * density).padBottom(4 * density).row();

        NinePatchDrawable payBackground = new NinePatchDrawable(new NinePatch(uiAtlas.findRegion("bg_winner"), 18, 18, 18, 18));
        payBackground.setMinHeight(40);
        payBackground.setMinWidth(40);
        StyledLabel.StyledLabelStyle payStyle = new StyledLabel.StyledLabelStyle(FontType.ROBOTO_BOLD, 8);
        payStyle.fontColor = Color.WHITE;

        Table buttonTable = new Table();
        StyledLabel.StyledLabelStyle merchantStyle = new StyledLabel.StyledLabelStyle(FontType.ROBOTO_BOLD, 10);
        merchantStyle.fontColor = Color.WHITE;

        float iconWidth = merchantStyle.font.getLineHeight();
        float iconHeight;
        Table paytmTable = new Table();
        paytmTable.pad(0, 8 * density, 0, 8 * density);
        NinePatchDrawable paytmBackground = new NinePatchDrawable(new NinePatch(uiAtlas.findRegion("bg_sky"), 18, 18, 18, 18));
        paytmBackground.setMinWidth(36);
        payBackground.setMinHeight(36);
        paytmTable.setBackground(paytmBackground);

        TextureRegion paytmTexture = uiAtlas.findRegion("icon_paytm_square");
        iconHeight = iconWidth * paytmTexture.getRegionHeight() / paytmTexture.getRegionWidth();
        paytmTable.add(new Image(paytmTexture)).width(iconWidth).height(iconHeight);
        Table paytmLabelTable = new Table();
        paytmLabelTable.add(getStyledLabel("Paytm", merchantStyle)).align(Align.left);
        StyledLabel.StyledLabelStyle paytmDetailsStyle = new StyledLabel.StyledLabelStyle(FontType.ROBOTO_BOLD, 6);
        paytmDetailsStyle.fontColor = Color.WHITE;
        paytmWalletBalance = getStyledLabel("", paytmDetailsStyle);
        paytmLabelTable.add(paytmWalletBalance).align(Align.left).padLeft(10 * density);
        paytmTable.add(paytmLabelTable).padLeft(8 * density);
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
        upiTable.pad(0, 8 * density, 0, 8 * density);
        NinePatchDrawable upiBackground = new NinePatchDrawable(new NinePatch(uiAtlas.findRegion("bg_cyan"), 18, 18, 18, 18));
        upiBackground.setMinWidth(36);
        upiBackground.setMinHeight(36);
        upiTable.setBackground(upiBackground);
        TextureRegion upiTexture = uiAtlas.findRegion("icon_upi");
        iconHeight = iconWidth * upiTexture.getRegionHeight() / upiTexture.getRegionWidth();
        upiTable.add(new Image(upiTexture)).width(iconWidth).height(iconHeight);
        upiTable.add(getStyledLabel("UPI", merchantStyle)).padLeft(8 * density);
        upiTable.add(getStyledLabel("ADD", payStyle)).expandX().align(Align.right);

        buttonTable.add(paytmTable).height(height * .11f).uniformY().fillY().expandX().fillX().padBottom(4 * density).row();
        buttonTable.add(upiTable).height(height * .11f).uniformY().fillY().expandX().fillX().padBottom(4 * density).row();

        Table bottomTable = new Table();

        float textureheight, texturewidth;
        textureheight = height * .085f;

        Table debit = new Table();
        TextureRegion debitTexture = uiAtlas.findRegion("ic_debit");
        texturewidth = textureheight * debitTexture.getRegionWidth() / debitTexture.getRegionHeight();
        debit.add(new Image(debitTexture)).width(texturewidth).height(textureheight).row();
        debit.add(new Label("Debit/Credit", detailsStyle));
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

        fronTable.add(buttonTable).width(width*.45f).row();
        fronTable.add(bottomTable).width(width*.45f).padTop(10 * density).padLeft(5 * density).padRight(5 * density).padBottom(10 * density).row();

        Table containerTable=new Table();
        containerTable.add(leftTable).width(width*.5f);
        containerTable.add(fronTable).align(Align.top).width(width*.5f).fill();
        scrollTable.add(containerTable).padBottom(3*density).row();

        Label.LabelStyle termsStyle = new Label.LabelStyle();
        termsStyle.font = FontPool.obtain(FontType.ROBOTO_REGULAR, 6);
        termsStyle.fontColor = Color.WHITE;
        Label tcLabel = new Label("Terms and Conditions Apply*", termsStyle);
        tcLabel.setAlignment(Align.center);

        ScrollPane dataScrollPane = new ScrollPane(scrollTable);
        dataScrollPane.setScrollingDisabled(true, false);

        contentTable.add(dataScrollPane).width(width).expand().fill().align(Align.top).row();
        Table termTable=new Table();
        termTable.setBackground(DarkgrayBg);
        termTable.add(tcLabel).width(width);
        contentTable.add(termTable);

        paytmTable.setTouchable(Touchable.enabled);
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
                isReport=true;
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
                        addWithPaytm(amount,processDialog, listener);
                        paymentType = "PAYTM";
                    } else {
                        if (errorMessage != null && !errorMessage.matches("")) {
                            toast(errorMessage);
                        } else {
                            minMessageStyle.fontColor = Color.SCARLET;
                        }
                    }
                } else {
                    new LandscapePaytmNumberVerifiedDialog(screen, true, Constant.userProfile.getMsisdn(), new GdxListener<String>() {
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
                ProcessDialog processDialog=new ProcessDialog(screen,"Please Wait...");
                processDialog.show(screen.stage);
                ApiHandler.callPrivacyPolicyApi("ADDCASH", new GdxListener<String>() {
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
    }

    private float isValidAmountEntered(String text) {
        if (text != null && !text.isEmpty()) {
            float addAmount = 0;
            try {
                addAmount = Float.parseFloat(text);
                if (addAmount > 5000) {
                    errorMessage = "Maximum add cash limit is \u20b9 5000.";
                }
                if (addAmount >= Constant.minAddCashThreshold && addAmount <= 5000) {
                    setCashBackDtoNotFirstTimeUser(addAmount);
                    return addAmount;
                }
            } catch (Exception e) {
            }
        }
        return -1.0f;
    }

    private void setCashBackDtoNotFirstTimeUser(float addAmount){
        if(Constant.userProfile.getCashBackDto()!= null){
            for (CashBackDto cashBackDto : Constant.userProfile.getCashBackDto()){
                if(addAmount>=cashBackDto.getMinimumAmount()){
                    this.cashBackDto = cashBackDto;
                }
            }
        }
    }

    private StyledLabel getStyledLabel(String label, StyledLabel.StyledLabelStyle styledLabelStyle) {
        StyledLabel styledLabel = new StyledLabel(label, styledLabelStyle);
        styledLabel.outline(0.1f, Color.DARK_GRAY);
        styledLabel.shadow(0.1f, 0.1f, Color.BLACK);
        return styledLabel;
    }


    private void addWithPaytm(final float amount,ProcessDialog processDialog, final GdxListener<String> listener) {
        final String orderId = "CASH" + GamePreferences.instance().getUserId()+"-"+ TimeUtils.millis();
        final GdxListener<String> paytmListener = new GdxListener<String>() {
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
                        ApiHandler.callChecksumApi(orderId,amount,cashBackId,processDialog,source,listener);
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
            ApiHandler.callAddCashApi("PAYTM",amount+ "", orderId, orderId,
                    source,cashBackId, listener);
        } else {
            ApiHandler.callChecksumApi(orderId,amount,cashBackId,processDialog,source,paytmListener);
        }
    }


    private void updateBalance() {
        walletLabel.setText(Constant.userProfile.getPaytmBalance() + "");
    }

    @Override
    public void hide() {
        pokerGame.appConfig.fullscreenOrientation= GwtGraphics.OrientationLockType.LANDSCAPE_PRIMARY;
        Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
        super.hide();
        if (!isReport) {
            ApiHandler.callEventLogApi("ADD_CASH", "PAYMENT_CANCEL", "CANCEL");
        }
    }


    private float getAmount(TextField amountField) {
        if (amountField != null && amountField.getText() != null && !amountField.getText().isEmpty()) {
            return Float.parseFloat(amountField.getText());
        } else {
            return 20;
        }
    }



    private class CashButton extends Button {
        private Image checkImage;
        private Label cashBackText;
        private int amount;
        private int lives;
        private TextField amountField;

        public CashButton(CashButtonStyle buttonStyle, final int amount, int lives, final TextField amountField) {
            super(buttonStyle);
            this.amount = amount;
            this.lives = lives;
            this.amountField = amountField;
            Table addTable = new Table();
            final float checkSize = buttonStyle.addStyle.font.getLineHeight();
            checkImage = new Image(buttonStyle.checkTexture);
//            addTable.add(checkImage).width(checkSize).height(checkSize);
            addTable.add(new Label("Add", buttonStyle.addStyle)).expandX().row();
            add(addTable).align(Align.left).expandX();
            if (lives > 0 || cashBackDto != null && amount >= cashBackDto.getMinimumAmount()) {
                addTable.add(new Label("\u20b9 " + amount, buttonStyle.amountStyle)).padTop(2 * density);
                Table centerTable=new Table();
                cashBackText = new Label("Get rewards of  ", buttonStyle.offerStyle);
                centerTable.add(cashBackText).expandX();
                add(centerTable).padLeft(width*.01f).padRight(width*.01f);
                Table lifeTable = new Table();
                float lifeHeight = buttonStyle.lifeStyle.font.getLineHeight()*1.2f;
                float lifeWidth = lifeHeight * buttonStyle.lifeTexture.getRegionWidth() / buttonStyle.lifeTexture.getRegionHeight();
                if (cashBackDto != null && amount >= cashBackDto.getMinimumAmount()) {
                    cashBackText.setText("cashback");
                    int cashback = amount * cashBackDto.getOfferInPercentage() / 100;
                    if (cashback > cashBackDto.getMaximumCashBack()) {
                        cashback = cashBackDto.getMaximumCashBack();
                        if (!isCashBackSet) {
                            setChecked(true);
                            isCashBackSet = true;
                        }
                    }
                    lifeTable.add(new Label("\u20b9", buttonStyle.rupeeStyle));
                    String cashbackString = "";
                    if (lives > 0) {
                        cashbackString = cashback + " + ";
                    } else {
                        cashbackString = cashback + "";
                    }
                    lifeTable.add(new Label(cashbackString, buttonStyle.lifeStyle)).padLeft(lifeWidth * 0.2f);
                }
                if (lives > 0) {
                    if (!isCashBackSet && entryFee ==0
                            && Constant.userProfile.getPaytmAccountBalance()< 20f
                            && Constant.userProfile.getPaytmAccountBalance()>=100) {
                        setChecked(true);
                        isCashBackSet = true;
                    }
                    lifeTable.add(new Label(lives + "", buttonStyle.lifeStyle));
                    lifeTable.add(new Image(buttonStyle.lifeTexture)).width(lifeWidth).height(lifeHeight).padLeft(lifeWidth * 0.2f);
                }
                centerTable.add(lifeTable);
                add(checkImage).width(checkSize).height(checkSize).align(Align.right);
            } else {
                addTable.add(new Label("\u20b9 " + amount, buttonStyle.amountStyle)).expandY();
                add(checkImage).width(checkSize).height(checkSize).align(Align.right);
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

        public CashButton(CashButtonStyle buttonStyle, final int amount, int lives, final TextField amountField,CashBackDto cashBackDto1) {
            super(buttonStyle);
            this.amount = amount;
            this.lives = lives;
            this.amountField = amountField;
            Table addTable = new Table();
            final float checkSize = buttonStyle.addStyle.font.getLineHeight();
            checkImage = new Image(buttonStyle.checkTexture);
//            addTable.add(checkImage).width(checkSize).height(checkSize);
            addTable.add(new Label("Add", buttonStyle.addStyle)).expandX().row();
            add(addTable).align(Align.left).expandX();
            if (lives > 0 || cashBackDto1 != null && amount >= cashBackDto1.getMinimumAmount()) {
                addTable.add(new Label("\u20b9 " + amount, buttonStyle.amountStyle)).padTop(2 * density);
                Table centerTable=new Table();
                cashBackText = new Label("Get rewards of  ", buttonStyle.offerStyle);
                centerTable.add(cashBackText).expandX();
                add(centerTable).padLeft(width*.01f).padRight(width*.01f);
                Table lifeTable = new Table();
                float lifeHeight = buttonStyle.lifeStyle.font.getLineHeight()*1.2f;
                float lifeWidth = lifeHeight * buttonStyle.lifeTexture.getRegionWidth() / buttonStyle.lifeTexture.getRegionHeight();
                if (cashBackDto1 != null && amount >= cashBackDto1.getMinimumAmount()) {
                    cashBackText.setText("cashback");
                    int cashback = amount * cashBackDto1.getOfferInPercentage() / 100;
                    if (cashback > cashBackDto1.getMaximumCashBack()) {
                        cashback = cashBackDto1.getMaximumCashBack();
                        if (!isCashBackSet && entryFee == 0) {
                            setChecked(true);
                            isCashBackSet = true;
                        }
                    }
                    lifeTable.add(new Label(" \u20b9", buttonStyle.rupeeStyle));
                    String cashbackString = "";
//                    if (lives > 0) {
//                        cashbackString = cashback + " + ";
//                    } else {
                    cashbackString = cashback + "";
//                    }
                    lifeTable.add(new Label(cashbackString, buttonStyle.lifeStyle)).padLeft(lifeWidth * 0.2f);
                }
                if (lives > 0 && entryFee==0) {
                    if (!isCashBackSet) {
                        setChecked(true);
                        isCashBackSet = true;
                    }
                    lifeTable.add(new Label(lives + "", buttonStyle.lifeStyle));
                    lifeTable.add(new Image(buttonStyle.lifeTexture)).width(lifeWidth).height(lifeHeight).padLeft(lifeWidth * 0.2f);
                }
                centerTable.add(lifeTable);
                add(checkImage).width(checkSize).height(checkSize).align(Align.right);
            } else {
                addTable.add(new Label("\u20b9 " + amount, buttonStyle.amountStyle)).expandY();
                add(checkImage).width(checkSize).height(checkSize).align(Align.right);
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

    @Override
    protected void result(Object object) {
        if (object.equals("back")) {
            hide();
            responseListener.setFail("");
        }
    }


}
