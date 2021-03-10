package bigcash.poker.dialogs;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.TimeZone;

import java.util.Date;

import bigcash.poker.constants.Constant;
import bigcash.poker.font.FontPool;
import bigcash.poker.font.FontType;
import bigcash.poker.models.AccountSummary;
import bigcash.poker.models.UIDialog;
import bigcash.poker.models.UIScreen;
import bigcash.poker.network.ApiHandler;
import bigcash.poker.network.GdxListener;
import bigcash.poker.utils.AssetsLoader;
import bigcash.poker.utils.DrawableBuilder;
import bigcash.poker.utils.GamePreferences;
import bigcash.poker.utils.TextureDrawable;
import bigcash.poker.widgets.PagingScrollPane;
import bigcash.poker.widgets.WalletTable;

/**
 * Created by Vinod on 11-09-2017.
 */

public class TransactionDialog extends UIDialog {
    private Texture backgroundTexture;
    private TextureAtlas atlas;
    private PagingScrollPane pagingScrollPane;
    private Table summaryTable;

    public TransactionDialog(UIScreen screen) {
        super(screen);
        dismissOnBack(true);
        buildDialog();
    }

    @Override
    public void init() {
        backgroundTexture = AssetsLoader.instance().screenBackground;
        atlas = AssetsLoader.instance().uiAtlas;
    }

    @Override
    public void buildDialog() {
        TextureRegionDrawable screenBackground = DrawableBuilder.getDrawable(backgroundTexture, width, height);
        Table contentTable = getContentTable();
        contentTable.defaults().space(0);
        contentTable.top();
        contentTable.setBackground(screenBackground);

        Table headerTable = new Table();
        NinePatchDrawable headerBackground = new NinePatchDrawable(new NinePatch(atlas.findRegion("bg_header_bar"), 1, 1, 1, 6));
        headerTable.setBackground(headerBackground);

        Label.LabelStyle headerLabelStyle = new Label.LabelStyle();
        headerLabelStyle.font = FontPool.obtain(FontType.ROBOTO_BOLD, 7);
        headerLabelStyle.fontColor = Color.WHITE;
        float walletTableHeight = headerLabelStyle.font.getLineHeight() * 1.6f;

        Label.LabelStyle titleStyle = new Label.LabelStyle();
        titleStyle.font = FontPool.obtain(FontType.ROBOTO_BOLD, 9);
        titleStyle.fontColor = Color.WHITE;

        TextureRegion backTexture = atlas.findRegion("btn_wback");
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

        headerTable.add(new Label("Transactions", titleStyle)).expandX().align(Align.left);

        TextureRegion walletBackTexture = atlas.findRegion("bg_wallet");
        float walletTableWidth = walletTableHeight * walletBackTexture.getRegionWidth() / walletBackTexture.getRegionHeight();
        float walletTablePad = walletTableWidth * 0.1f;
        float walletPad = walletTablePad;

        TextureRegion addTexture = atlas.findRegion("icon_add");
        float addHeight = headerLabelStyle.font.getLineHeight() * 1.2f;
        float addWidth = addHeight * addTexture.getRegionWidth() / addTexture.getRegionHeight();

        TextureRegion balanceWalletTexture = atlas.findRegion("icon_walletn");
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


        Table summaryHeaderTable = new Table();
        summaryHeaderTable.pad(width * 0.01f);
        TextureRegionDrawable stripDrawable = DrawableBuilder.getDrawable(atlas.findRegion("bg_strip"), 20, 20);
        stripDrawable.setTopHeight(8 * density);
        stripDrawable.setBottomHeight(8 * density);
        summaryHeaderTable.setBackground(stripDrawable);

        Label.LabelStyle summaryHeadingStyle = new Label.LabelStyle();
        summaryHeadingStyle.font = FontPool.obtain(FontType.ROBOTO_BOLD, 7);
        summaryHeadingStyle.fontColor = Color.WHITE;

        Label dateLabel = new Label("Date", summaryHeadingStyle);
        dateLabel.setAlignment(Align.center);
        summaryHeaderTable.add(dateLabel).expandX().align(Align.top);
        Label descriptionLabel = new Label("Description", summaryHeadingStyle);
        descriptionLabel.setWrap(true);
        descriptionLabel.setAlignment(Align.center);
        summaryHeaderTable.add(descriptionLabel).align(Align.center).width(width * 0.55f);
        Label amountLabel = new Label("Amount", summaryHeadingStyle);
        amountLabel.setAlignment(Align.top);
        summaryHeaderTable.add(amountLabel).align(Align.center).expandX();


        contentTable.add(summaryHeaderTable).expandX().fillX().padTop(height * 0.05f).row();

        final Label.LabelStyle detailsStyle = new Label.LabelStyle();
        detailsStyle.font = FontPool.obtain(FontType.ROBOTO_REGULAR, 5);
        detailsStyle.fontColor = Color.WHITE;

        Table summaryBackTable = new Table();
        summaryBackTable.top();
        NinePatchDrawable summaryBackground = new NinePatchDrawable(new NinePatch(atlas.findRegion("bg_menu"), 19, 19, 24, 0));
        summaryBackTable.setBackground(summaryBackground);
        final TextureRegionDrawable lineDrawable = new DrawableBuilder(4, 4).color(Color.WHITE).createDrawable();
        pagingScrollPane=new PagingScrollPane(screen,new Table(),true){
            @Override
            public void onEndReached() {
                if(Constant.arrAccountSummary != null && Constant.arrAccountSummary.size==0){
                    pagingScrollPane.hideProcess();
                    return;
                }
                ApiHandler.callAccountSummaryApi(Constant.minId,new GdxListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        setData(detailsStyle,lineDrawable);
                    }

                    @Override
                    public void onFail(String reason) {
                        pagingScrollPane.hideProcess();
                    }

                    @Override
                    public void onError(String errorMessage) {
                        pagingScrollPane.hideProcess();
                    }
                });
            }
        };
        summaryTable = pagingScrollPane.getDataTable();
        summaryTable.top();
        setData(detailsStyle,lineDrawable);
        summaryBackTable.add(pagingScrollPane).expandX().fillX();
        contentTable.add(summaryBackTable).expand().fill();
    }



    private void setData(Label.LabelStyle detailsStyle, TextureRegionDrawable lineDrawable){
        DateTimeFormat dateTimeFormat=DateTimeFormat.getFormat("dd/MM/yy '\n at 'hh:mm aa");
        boolean utc=GamePreferences.instance().getUseJenkinsTimeEnable();
        TimeZone timeZone=TimeZone.createTimeZone(0);
        if (Constant.arrAccountSummary != null && Constant.arrAccountSummary.size > 0) {
            for (AccountSummary accountSummary : Constant.arrAccountSummary) {
                Date date = new Date(Long.parseLong(accountSummary.getCreatedTime()));
                addData(dateTimeFormat.format(date, timeZone), accountSummary.getOfferName(), accountSummary.getAmount(), detailsStyle, lineDrawable);
            }
        }
        pagingScrollPane.hideProcess();
    }


    private void addData(String date, String details, String amount, Label.LabelStyle detailsStyle, Drawable lineDrawable) {
        Label dateLabel = new Label(date, detailsStyle);
        dateLabel.setAlignment(Align.center);
        summaryTable.add(dateLabel).expandX().align(Align.top);
        Label descriptionLabel = new Label(details, detailsStyle);
        descriptionLabel.setWrap(true);
        descriptionLabel.setAlignment(Align.center);
        summaryTable.add(descriptionLabel).align(Align.center).width(width * 0.55f);
        Label amountLabel = new Label("\u20b9 " + amount, detailsStyle);
        amountLabel.setAlignment(Align.top);
        summaryTable.add(amountLabel).align(Align.center).expandX().row();
        summaryTable.add(new Image(lineDrawable)).width(width * 0.97f).padTop(8 * density).padBottom(8 * density).colspan(3).fillX().row();
    }
}
