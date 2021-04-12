package bigcash.poker.game.poker.dialogs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

import java.util.concurrent.TimeUnit;

import bigcash.poker.font.Font;
import bigcash.poker.font.FontPool;
import bigcash.poker.font.FontType;
import bigcash.poker.game.poker.PokerAbstractScreen;
import bigcash.poker.models.UIDialog;
import bigcash.poker.utils.DrawableBuilder;
import bigcash.poker.utils.PokerTimer;
import bigcash.poker.utils.PokerUtils;
import bigcash.poker.utils.TextureDrawable;
import bigcash.poker.widgets.StyledLabel;

public abstract class PokerAddBalanceDialog extends UIDialog {
    private TextureAtlas uiAtlas, pokerAtlas;
    public Label blindLabel, bigCashWalletLabel, currentSelectedAmountLabel, minLabel, maxlabel, addLable;
    private Slider slider;
    private PokerTimer timeCounter;
    private Label timerLable;
    private float maxAddAmount, minAddAmount;
    private float betValue;
    private Texture bgSelectedAmount;

    public PokerAddBalanceDialog(PokerAbstractScreen gameScreen, float maxAddAmount, float minAddAmount, float betValue){
        super(gameScreen);
        this.maxAddAmount = maxAddAmount;
        this.minAddAmount = minAddAmount;
        this.betValue = betValue;
        pokerAtlas = gameScreen.atlas;
        uiAtlas = gameScreen.uiAtlas;
        bgSelectedAmount = gameScreen.bgSelectedAmount;
        this.density=this.width/360.0f;
        dismissOnBack(true);
        buildDialog();
    }

    @Override
    public void init() {
    }

    @Override
    public void buildDialog() {
        float dialogWidth = width * 0.8f;
        Table dataTable = new Table();
        float maxBet = maxAddAmount;
        float minBet = minAddAmount;
        float step = maxBet * 2 / 100.0f;
        final Label.LabelStyle minMaxStyle = new Label.LabelStyle();
        minMaxStyle.font = FontPool.obtain(FontType.ROBOTO_REGULAR, 5);
        minMaxStyle.fontColor = Color.GRAY;

        minLabel = new Label(minBet + "", minMaxStyle);
        maxlabel = new Label(maxBet + "", minMaxStyle);
        StyledLabel.StyledLabelStyle headingLabelStyle = new StyledLabel.StyledLabelStyle(FontType.ROBOTO_BOLD, 9);
        headingLabelStyle.fontColor = Color.WHITE;
        StyledLabel headingLabel = new StyledLabel("Add cash to Table", headingLabelStyle);
        headingLabel.setWrap(true);
        headingLabel.setAlignment(Align.center);
//        headingLabel.outline(0.2f, Color.WHITE);
        headingLabel.shadow(1f, 1f, Color.valueOf("8e2424"));

        NinePatch whiteNinePatch = new NinePatch(uiAtlas.findRegion("bg_details_white"), 30, 30, 30, 30);
        NinePatchDrawable whitebackground = new NinePatchDrawable(whiteNinePatch);
        whitebackground.setMinWidth(60);
        whitebackground.setMinHeight(60);

        NinePatch redNinePatch = new NinePatch(uiAtlas.findRegion("bg_red"), 30, 30, 30, 30);
        NinePatchDrawable redbackground = new NinePatchDrawable(redNinePatch);
        redbackground.setMinWidth(60);
        redbackground.setMinHeight(60);

        Table topTable = new Table();
        topTable.setBackground(redbackground);
        topTable.top();
        topTable.add(headingLabel).width(dialogWidth * 0.5f).align(Align.left).expandX();

        Table timerTable = new Table();
        Label.LabelStyle smallTimerStyle = new Label.LabelStyle();
        smallTimerStyle.font = FontPool.obtain(FontType.ROBOTO_REGULAR, 6);
        smallTimerStyle.fontColor = Color.WHITE;

        Label.LabelStyle bigTimerStyle = new Label.LabelStyle();
        bigTimerStyle.font = FontPool.obtain(FontType.ROBOTO_BOLD, 6);
        bigTimerStyle.fontColor = Color.WHITE;

        timerLable = new Label("", bigTimerStyle);
        timerTable.add(new Label("Time Left:", smallTimerStyle)).row();
        timerTable.add(timerLable).row();

        topTable.add(timerTable).width(dialogWidth * .2f).align(Align.right);

        final Label.LabelStyle messageStyle = new Label.LabelStyle();
        messageStyle.font = FontPool.obtain(FontType.ROBOTO_REGULAR, 7);
        messageStyle.fontColor = Color.GRAY;

        final Label.LabelStyle greenStyle = new Label.LabelStyle();
        greenStyle.font = FontPool.obtain(FontType.ROBOTO_REGULAR, 7);
        greenStyle.fontColor = Color.valueOf("206513");

        final Label.LabelStyle greenBoldStyle = new Label.LabelStyle();
        greenBoldStyle.font = FontPool.obtain(FontType.ROBOTO_BOLD, 7);
        greenBoldStyle.fontColor = Color.valueOf("206513");

        Table bottomTable = new Table();
        bottomTable.setBackground(whitebackground);
        bottomTable.bottom();

        Font buttonFont = FontPool.obtain(FontType.ROBOTO_BOLD, 7);
        Label.LabelStyle buttonLabelStyle = new Label.LabelStyle();
        buttonLabelStyle.font = buttonFont;
        buttonLabelStyle.fontColor = Color.WHITE;

        Table balanceTable = new Table();

        Label blindText = new Label("Table Blind", messageStyle);
        Label bigcashWalletText = new Label("BigCash Wallet", messageStyle);
        blindLabel = new Label(PokerUtils.getValue(betValue)+"", messageStyle);
        bigCashWalletLabel = new Label("0", messageStyle);

        balanceTable.add(blindText).align(Align.left).expandX();
        balanceTable.add(blindLabel).align(Align.right).row();
//        balanceTable.add(bigcashWalletText).align(Align.left).padTop(6*density).expandX();
//        balanceTable.add(bigCashWalletLabel).align(Align.right);

        bottomTable.add(balanceTable).padTop(6*density).width(dialogWidth * .9f).row();

        StyledLabel.StyledLabelStyle raiseLabelStyle = new StyledLabel.StyledLabelStyle(FontType.ROBOTO_BOLD, 5);
        raiseLabelStyle.fontColor = Color.WHITE;

        float knobSize = raiseLabelStyle.font.getLineHeight() * 2f;

        float crossSize = raiseLabelStyle.font.getLineHeight() * 2.0f;


        NinePatch bgNinePatch = new NinePatch(bgSelectedAmount, 30, 30, 30, 30);
        NinePatchDrawable background = new NinePatchDrawable(bgNinePatch);
        background.setMinWidth(60);
        background.setMinHeight(60);

        Table selectedBalanceTable = new Table();
        selectedBalanceTable.setBackground(background);
        currentSelectedAmountLabel = new Label("", greenBoldStyle);
        selectedBalanceTable.add(new Label("Set Cash Amount", greenStyle)).align(Align.left).expandX();
        selectedBalanceTable.add(currentSelectedAmountLabel).align(Align.right);
        bottomTable.add(selectedBalanceTable).padTop(10*density).width(dialogWidth * .8f).row();

        final Slider.SliderStyle sliderStyle = new Slider.SliderStyle();
        sliderStyle.background = new NinePatchDrawable(new NinePatch(pokerAtlas.findRegion("slider_horizontal"), 1, 1, 1, 1));
        sliderStyle.knob = TextureDrawable.getDrawable(pokerAtlas.findRegion("knob_horizontal"), knobSize, knobSize);
        slider = new Slider(minBet, maxBet, step, false, sliderStyle);
        slider.setValue(maxBet);
        Table sliderTable = new Table();
        sliderTable.add(slider).expandX().fillX();
        slider.setVisualInterpolation(Interpolation.linear);
        addLable = new Label("ADD \u20b9 " + PokerUtils.getValue(slider.getValue()), buttonLabelStyle);
        currentSelectedAmountLabel.setText("\u20b9 " + slider.getValue());

        slider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                addLable.setText("ADD \u20b9 " +PokerUtils.getValue(slider.getValue()));
                currentSelectedAmountLabel.setText("\u20b9 " + PokerUtils.getValue(slider.getValue()));
            }
        });

        Table minTable = new Table();
        minTable.add(new Label("\u20b9" + minBet, minMaxStyle)).row();
        minTable.add(new Label("Minimum", minMaxStyle));

        Table maxTable = new Table();
        maxTable.add(new Label("\u20b9" + maxBet, minMaxStyle)).row();
        maxTable.add(new Label("Maximum", minMaxStyle));

        Table slide = new Table();
        slide.add(minTable).width(dialogWidth * .1f);
        slide.add(slider).width(dialogWidth * .65f).height(knobSize / 3f).pad(knobSize / 2f);
        slide.add(maxTable).width(dialogWidth * .1f);
        bottomTable.add(slide).padTop(6*density).width(dialogWidth * .95f).row();


        final Table addTable = new Table();
        NinePatchDrawable blueDrawable = new NinePatchDrawable(new NinePatch(uiAtlas.findRegion("btn_verifie"), 12, 12, 12, 16));
        blueDrawable.setMinHeight(buttonFont.getLineHeight() * 1.8f);
        blueDrawable.setMinWidth(28);
        addTable.setBackground(blueDrawable);
        addTable.add(addLable).padLeft(25 * density).padRight(25 * density).expandX();
        addTable.setTouchable(Touchable.enabled);
        addTable.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                addAmountClick(PokerUtils.getValue(slider.getValue()));
            }
        });

        bottomTable.add(addTable).padTop(5*density).width(dialogWidth * .8f).expandX().row();

        dataTable.add(topTable).width(dialogWidth).row();
        dataTable.add(bottomTable).width(dialogWidth);


        float topPad = 5 * density;

        Table backTable = new Table();
        float closeSize = width * 0.08f;
        float sidePad = closeSize * 0.4f;
        backTable.add(dataTable).width(dialogWidth).pad(topPad, sidePad, sidePad, sidePad);


        Table closeTable = new Table();
        closeTable.top().right();
        TextureRegion closeTexture = uiAtlas.findRegion("btn_cross_white");
        TextureRegionDrawable closeDrawable = DrawableBuilder.getDrawable(closeTexture, closeSize, closeSize);
        ImageButton closeButton = new ImageButton(closeDrawable, closeDrawable, closeDrawable);
        closeTable.add(closeButton).width(closeSize).height(closeSize).padTop(topPad - closeSize * 0.4f);
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

    private void cancelTimer() {
        if (timeCounter != null) {
            timeCounter.cancel();
        }
        timeCounter = null;
    }

    @Override
    public Dialog show(Stage stage) {
        timeCounter =new PokerTimer(TimeUnit.SECONDS.toMillis(15), 1000, new PokerTimer.PokerTimerUpdater() {
            @Override
            public void onTick(final long millisUntilFinished) {
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        String timeText = PokerUtils.getTimer(millisUntilFinished, ":", false);
                        timerLable.setText(timeText);
                    }
                });

            }

            @Override
            public void onFinish() {
                //   hide();
            }
        });
        timeCounter.start();
        return super.show(stage);
    }


    @Override
    public void hide() {
        cancelTimer();
        super.hide();

    }

    public abstract void addAmountClick(float amount);
}

