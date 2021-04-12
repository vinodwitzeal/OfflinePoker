package bigcash.poker.game.poker.widgets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

import bigcash.poker.font.Font;
import bigcash.poker.font.FontPool;
import bigcash.poker.font.FontType;
import bigcash.poker.game.poker.PokerAbstractScreen;
import bigcash.poker.utils.AssetsLoader;
import bigcash.poker.utils.PokerUtils;
import bigcash.poker.utils.TextureDrawable;
import bigcash.poker.widgets.MagicTable;

public abstract class PokerRaiseTable extends Table {
    private TextureAtlas gameAtlas;
    public final float stepSize;
    public RaiseLabel raiseLabel;
    public PokerSlider slider;
    private Label.LabelStyle buttonLabelStyle;
    private NinePatchDrawable buttonBackground;
    private float buttonHeight, buttonPad, tablePad;
    public RaiseTableButton maxButton, potButton, minButton;
    private Table plusButton, minusButton;
    private boolean disablePeriodButton;
    public Actor userTable, raiseButton;
    private NinePatchDrawable background, keypadBackground;
    private Color backgroundColor;
    private Image arrowImage;
    private Table amountTable;
    private NumberKeyboard numberKeyboard;
    private ErrorTable errorTable;
    private PokerAbstractScreen screen;
    private Vector2 backgroundPosition;

    public PokerRaiseTable(PokerAbstractScreen screen, final float minBetAmount, final float maxBetAmount, float stepSize) {
        this(screen, false, minBetAmount, maxBetAmount, stepSize);
    }

    public PokerRaiseTable(PokerAbstractScreen screen, boolean disablePeriodButton, final float minBetAmount, final float maxBetAmount, float stepSize) {
        this.screen = screen;
        this.disablePeriodButton = disablePeriodButton;
        this.stepSize = stepSize;
        this.backgroundPosition = new Vector2();
        gameAtlas = AssetsLoader.instance().gameAtlas;
        TextureRegion buttonTexture = gameAtlas.findRegion("raise_btn");
        this.background = new NinePatchDrawable(new NinePatch(gameAtlas.findRegion("img_dot"), 1, 1, 1, 1));
        this.keypadBackground = new ColorDrawable(new NinePatch(gameAtlas.findRegion("bg_keypad"), 24, 24, 28, 28), Color.BLUE);
        this.backgroundColor = Color.valueOf("00000099");

        Font buttonFont = FontPool.obtain(FontType.ROBOTO_BOLD, 6);
        buttonHeight = buttonFont.getCapHeight() * 3;
        buttonLabelStyle = new Label.LabelStyle();
        buttonLabelStyle.font = buttonFont;
        buttonLabelStyle.fontColor = Color.WHITE;
        buttonBackground = new NinePatchDrawable(new NinePatch(buttonTexture, 15, 15, 15, 20));
        buttonPad = buttonFont.getLineHeight();
        tablePad = buttonHeight / 3f;

        Table dataTable = new MagicTable(new NinePatchDrawable(new NinePatch(gameAtlas.findRegion("bg_raise"), 35, 35, 35, 35)));
        dataTable.pad(tablePad);
        TextureRegion amountBackground = gameAtlas.findRegion("bg_dialog_rounded");

        amountTable = new MagicTable(new NinePatchDrawable(new NinePatch(amountBackground, 20, 20, 20, 20)));
        RaiseLabelStyle labelStyle = new RaiseLabelStyle();
        labelStyle.font = FontPool.obtain(FontType.ROBOTO_BOLD, 7);
        labelStyle.fontColor = Color.BLACK;
        labelStyle.selection = new ColorDrawable(new NinePatch(gameAtlas.findRegion("img_dot"), 1, 1, 1, 1), Color.BLUE);
        labelStyle.selectedColor = Color.WHITE;
        addSymbol(amountTable, labelStyle.font.getCapHeight());
        float amountTableHeight = labelStyle.font.getCapHeight() * 2;
        raiseLabel = new RaiseLabel("", labelStyle) {
            @Override
            public void onTextChanged(String text) {
                onLabelTextChanged(text);
            }
        };
        numberKeyboard = new NumberKeyboard();

        TextureRegion arrowTexture = gameAtlas.findRegion("img_arrow");
        float arrowHeight = labelStyle.font.getCapHeight();
        float arrowWidth = arrowHeight * arrowTexture.getRegionWidth() / arrowTexture.getRegionHeight();
        arrowImage = new Image(arrowTexture);
        arrowImage.setSize(arrowWidth, arrowHeight);
        amountTable.add(raiseLabel);
        dataTable.add(amountTable).expandX().fillX().height(amountTableHeight).padBottom(tablePad).row();


        TextureRegion closeTexture = gameAtlas.findRegion("btn_cross");
        float closeWidth = buttonHeight;
        float closeHeight = closeWidth * closeTexture.getRegionHeight() / closeTexture.getRegionWidth();

        Table buttonTable = new Table();
        maxButton = new RaiseTableButton(maxBetAmount, "MAX");

        potButton = new RaiseTableButton(minBetAmount * 2, "POT");

        minButton = new RaiseTableButton(minBetAmount, "MIN");

        plusButton = new MagicTable(buttonBackground);
        plusButton.setTouchable(Touchable.enabled);
        plusButton.add(new Label("+", buttonLabelStyle));

        minusButton = new MagicTable(buttonBackground);
        minusButton.setTouchable(Touchable.enabled);
        minusButton.add(new Label("-", buttonLabelStyle));


        TextureRegion knobTexture = gameAtlas.findRegion("btn_slider");
        final float knobHeight = buttonHeight * 0.75f;
        final float knobWidth = knobHeight * knobTexture.getRegionWidth() / knobTexture.getRegionHeight();
        TextureRegionDrawable knobDrawable = TextureDrawable.getDrawable(knobTexture, knobWidth, knobHeight);
        final PokerSlider.SliderStyle sliderStyle = new PokerSlider.SliderStyle();
        sliderStyle.background = new VerticalEqualDrawable(gameAtlas.findRegion("raise_bg_slider"), 18);
        sliderStyle.background.setMinWidth(buttonHeight / 4f);
        sliderStyle.knob = knobDrawable;;

        slider = new PokerSlider(minBetAmount, maxBetAmount, stepSize, true, sliderStyle);

        slider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                raiseLabel.selected = true;
                onSliderValueChanged(slider.getValue());
            }
        });

        maxButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                raiseLabel.selected = true;
                onMaxButtonClicked();
            }
        });

        potButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                raiseLabel.selected = true;
                onPotButtonClicked();
            }
        });

        minButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                raiseLabel.selected = true;
                onMinButtonClicked();
            }
        });

        plusButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                raiseLabel.selected = true;
                onPlusButtonClicked();
            }
        });

        minusButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                raiseLabel.selected = true;
                onMinusButtonClicked();
            }
        });

        Table containerTable = new Table();
        buttonTable.add(maxButton).uniformX().fillX().height(buttonHeight).row();
        buttonTable.add(potButton).uniformX().fillX().height(buttonHeight).row();
        buttonTable.add(minButton).uniformX().fillX().height(buttonHeight).row();
        buttonTable.add(plusButton).uniformX().fillX().height(buttonHeight).row();
        buttonTable.add(minusButton).uniformX().fillX().height(buttonHeight).row();
        containerTable.add(buttonTable).padRight(tablePad);
        containerTable.add(slider).expandY().fillY();
        dataTable.add(containerTable);

        TextureRegionDrawable closeDrawable = TextureDrawable.getDrawable(closeTexture, closeWidth, closeHeight);
        Button closeButton = new Button(closeDrawable);
        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                onHide();
                setVisible(false);
            }
        });

        Table closeTable = new Table();
        closeTable.top().right();
        closeTable.add(closeButton).width(closeWidth).height(closeHeight);
        Table backTable = new Table();
        backTable.add(dataTable).padTop(closeHeight / 2f).padRight(closeWidth / 2f);

        Stack stack = new Stack();
        stack.add(backTable);
        stack.add(closeTable);

        add(stack);
        errorTable = new ErrorTable(new NinePatchDrawable(new NinePatch(gameAtlas.findRegion("bg_dialog_rounded"), 8, 8, 8, 8)), "000000bb");
    }

    public void selectAll(boolean selected) {
        raiseLabel.setSelected(selected);
    }


    public abstract boolean isValid();


    public void setUserTable(Actor userTable) {
        this.userTable = userTable;
    }

    public void setRaiseButton(Actor raiseButton) {
        this.raiseButton = raiseButton;
    }


    public void drawStageBackground(Batch batch) {
        if (isVisible()) {
            batch.setColor(backgroundColor);
            background.draw(batch, backgroundPosition.x, backgroundPosition.y, screen.width, screen.height);
        }
    }

    protected void drawChildrenActor(Batch batch, float parentAlpha) {
        if (isVisible()) {
            if (userTable != null) {
                userTable.draw(batch, parentAlpha);
            }

            if (raiseButton != null) {
                raiseButton.draw(batch, parentAlpha);
            }
        }
    }


    @Override
    public void draw(Batch batch, float parentAlpha) {
        drawStageBackground(batch);
        drawChildrenActor(batch, parentAlpha);
        super.draw(batch, parentAlpha);
    }

    public abstract void addSymbol(Table amountTable, float height);

    public abstract void onLabelTextChanged(String displayText);

    public abstract void onMaxButtonClicked();

    public abstract void onPotButtonClicked();

    public abstract void onMinButtonClicked();

    public abstract void onPlusButtonClicked();

    public abstract void onMinusButtonClicked();

    public abstract void onSliderValueChanged(float value);

    public abstract void onShow();

    public abstract void onHide();

    public void showError(final String error) {
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                errorTable.clearActions();
                errorTable.errorLabel.setText(error);
                float totalWidth = numberKeyboard.numberKeyPad.getWidth() + arrowImage.getWidth() + getWidth();
                float totalHeight = errorTable.errorLabel.getStyle().font.getLineHeight() * 3f;
                errorTable.setSize(totalWidth, totalHeight);
                float x = numberKeyboard.numberKeyPad.getX();
                float y = numberKeyboard.numberKeyPad.getY() + numberKeyboard.numberKeyPad.getHeight();
                if ((y + totalHeight) > screen.height) {
                    y = screen.height - totalHeight;
                }
                errorTable.setPosition(x, y);
                errorTable.addAction(Actions.sequence(
                        Actions.fadeIn(0),
                        Actions.delay(5.0f),
                        Actions.fadeOut(0.5f),
                        Actions.removeActor()
                ));
                screen.stage.addActor(errorTable);
            }
        });

    }

    public void clearError() {
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                errorTable.errorLabel.setText("");
                errorTable.clearActions();
                errorTable.remove();
            }
        });

    }

    public float getFloatValue(String str) {
        try {
            return Float.parseFloat(str);
        } catch (Exception e) {
            return 0;
        }
    }

    public int getIntValue(String str) {
        try {
            return Integer.parseInt(str);
        } catch (Exception e) {
            return 0;
        }
    }


    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (visible) {
            clearError();
            onShow();
            numberKeyboard.show(true);
            raiseLabel.selected = true;
        } else {
            clearError();
            numberKeyboard.show(false);
        }
    }


    private class VerticalEqualDrawable implements Drawable {
        private Drawable topDrawable, bottomDrawable, centerDrawable;
        private float aspectRatio, sideHeight, centerHeight, topY, bottomY, centerY;
        private float y, width, height;
        private float minWidth, minHeight;

        public VerticalEqualDrawable(TextureRegion region, int side) {
            int totalWidth = region.getRegionWidth();
            int totalHeight = region.getRegionHeight();
            TextureRegion topRegion = new TextureRegion(region, 0, 0, totalWidth, side);
            TextureRegion bottomRegion = new TextureRegion(region, 0, totalHeight - side, totalWidth, side);
            TextureRegion centerRegion = new TextureRegion(region, 0, side + 1, totalWidth, totalHeight - 2 * side);
            topDrawable = new TextureRegionDrawable(topRegion);
            bottomDrawable = new TextureRegionDrawable(bottomRegion);
            centerDrawable = new NinePatchDrawable(new NinePatch(centerRegion, 1, 1, 1, 1));
            aspectRatio = (float) side / (float) totalWidth;
        }

        private void validate(float x, float y, float width, float height) {
            if (this.width != width || this.height != height) {
                sideHeight = aspectRatio * width;
                centerHeight = height - 2 * sideHeight;
                if (centerHeight < 0) {
                    centerHeight = 0;
                }
            }

            if (this.y != y) {
                bottomY = y;
                centerY = bottomY + sideHeight;
                topY = centerY + centerHeight;
            }
            this.y = y;
            this.width = width;
            this.height = height;
        }

        @Override
        public void draw(Batch batch, float x, float y, float width, float height) {
            validate(x, y, width, height);
            topDrawable.draw(batch, x, topY, width, sideHeight);
            bottomDrawable.draw(batch, x, bottomY, width, sideHeight);
            centerDrawable.draw(batch, x, centerY, width, centerHeight);
        }


        @Override
        public float getLeftWidth() {
            return 0;
        }

        @Override
        public void setLeftWidth(float leftWidth) {

        }

        @Override
        public float getRightWidth() {
            return 0;
        }

        @Override
        public void setRightWidth(float rightWidth) {

        }

        @Override
        public float getTopHeight() {
            return 0;
        }

        @Override
        public void setTopHeight(float topHeight) {

        }

        @Override
        public float getBottomHeight() {
            return 0;
        }

        @Override
        public void setBottomHeight(float bottomHeight) {

        }

        @Override
        public float getMinWidth() {
            return minWidth;
        }

        @Override
        public void setMinWidth(float minWidth) {
            this.minWidth = minWidth;
        }

        @Override
        public float getMinHeight() {
            return minHeight;
        }

        @Override
        public void setMinHeight(float minHeight) {
            this.minHeight = minHeight;
        }
    }

    public class ColorDrawable extends NinePatchDrawable {
        private Color color;

        public ColorDrawable(NinePatch patch, Color color) {
            super(patch);
            this.color = color;
        }

        @Override
        public void draw(Batch batch, float x, float y, float width, float height) {
            Color color = batch.getColor();
            batch.setColor(this.color);
            super.draw(batch, x, y, width, height);
            batch.setColor(color);
        }

        @Override
        public void draw(Batch batch, float x, float y, float originX, float originY, float width, float height, float scaleX, float scaleY, float rotation) {
            Color color = batch.getColor();
            batch.setColor(this.color);
            super.draw(batch, x, y, originX, originY, width, height, scaleX, scaleY, rotation);
            batch.setColor(color);
        }
    }


    private class NumberKeyboard implements TextField.OnscreenKeyboard {
        private NumberKeyPad numberKeyPad;

        public NumberKeyboard() {
            numberKeyPad = new NumberKeyPad();
        }

        @Override
        public void show(boolean visible) {
            if (visible) {
                show();
            } else {
                hide();
            }
        }

        private void show() {
            numberKeyPad.pack();
            Stage stage = getStage();
            if (stage != null) {
                Vector2 amountPosition = new Vector2();
                amountPosition = screen.localToStageCoordinates(amountTable, amountPosition);
                Vector2 raisePosition = new Vector2();
                screen.localToStageCoordinates(PokerRaiseTable.this,raisePosition);
                arrowImage.setPosition(amountPosition.x - arrowImage.getWidth(), amountPosition.y + (amountTable.getHeight() - arrowImage.getHeight()) / 2f);
                numberKeyPad.setPosition(arrowImage.getX() - numberKeyPad.getWidth(), raisePosition.y + getHeight() - (numberKeyPad.getHeight() - numberKeyPad.buttonHeight * 1.5f));
                stage.addActor(arrowImage);
                stage.addActor(numberKeyPad);
            }
        }

        private void hide() {
            numberKeyPad.remove();
            arrowImage.remove();
        }
    }

    private class ErrorTable extends MagicTable {
        private Label errorLabel;

        public ErrorTable(Drawable background, String color) {
            super(background, color);
            Label.LabelStyle errorStyle = new Label.LabelStyle();
            errorStyle.font = FontPool.obtain(FontType.ROBOTO_REGULAR, 5);
            errorStyle.fontColor = Color.WHITE;
            pad(errorStyle.font.getCapHeight());

            Label.LabelStyle retryStyle = new Label.LabelStyle();
            retryStyle.font = FontPool.obtain(FontType.ROBOTO_BOLD, 8);
            retryStyle.fontColor = Color.FIREBRICK;


            errorLabel = new Label("", errorStyle);
            errorLabel.setAlignment(Align.left);
            errorLabel.setWrap(true);

            add(errorLabel).expandX().fillX();
        }
    }

    private class NumberKeyPad extends MagicTable {

        private Label.LabelStyle numberStyle;
        private TextureRegionDrawable numberBackground;
        private float buttonWidth, buttonHeight, topPad, bottomPad;

        public NumberKeyPad() {
            super(keypadBackground);
            TextureRegion buttonBackground = gameAtlas.findRegion("bg_number");
            numberStyle = new Label.LabelStyle();
            numberStyle.font = FontPool.obtain(FontType.ROBOTO_BOLD, 8);
            numberStyle.fontColor = Color.BLACK;
            pad(numberStyle.font.getCapHeight() / 2f);

            buttonHeight = numberStyle.font.getCapHeight() * 3;
            buttonWidth = buttonHeight * buttonBackground.getRegionWidth() / buttonBackground.getRegionHeight();
            numberBackground = TextureDrawable.getDrawable(buttonBackground, buttonWidth, buttonHeight);

            topPad = buttonHeight * 0.15f;
            bottomPad = buttonHeight * 0.18f;

            row();
            add(getNumberButton("1", '1')).width(buttonWidth).height(buttonHeight);
            add(getNumberButton("2", '2')).width(buttonWidth).height(buttonHeight);
            add(getNumberButton("3", '3')).width(buttonWidth).height(buttonHeight);
            row();
            add(getNumberButton("4", '4')).width(buttonWidth).height(buttonHeight);
            add(getNumberButton("5", '5')).width(buttonWidth).height(buttonHeight);
            add(getNumberButton("6", '6')).width(buttonWidth).height(buttonHeight);
            row();
            add(getNumberButton("7", '7')).width(buttonWidth).height(buttonHeight);
            add(getNumberButton("8", '8')).width(buttonWidth).height(buttonHeight);
            add(getNumberButton("9", '9')).width(buttonWidth).height(buttonHeight);
            row();
            add(getNumberButton(".", '.')).width(buttonWidth).height(buttonHeight);
            add(getNumberButton("0", '0')).width(buttonWidth).height(buttonHeight);
            add(getDeleteButton()).width(buttonWidth).height(buttonHeight);
            row();
        }


        private Table getNumberButton(final String number, final char character) {
            Table table = new Table();
            table.padTop(topPad).padBottom(bottomPad);
            table.setBackground(numberBackground);
            table.add(new Label("" + number, numberStyle));
            if (!disablePeriodButton) {
                table.setTouchable(Touchable.enabled);
                table.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        String displayText = raiseLabel.displayText;
                        if ((displayText.matches("") || displayText.matches("0")) && character == '0')
                            return;
                        if (displayText.contains(".") && character == '.') return;
                        if (raiseLabel.selected) {
                            raiseLabel.selected = false;
                            raiseLabel.setText("");
                        }
                        raiseLabel.append(character);
                    }
                });
            } else {
                if (character != '.') {
                    table.setTouchable(Touchable.enabled);
                    table.addListener(new ClickListener() {
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            String displayText = raiseLabel.displayText;
                            if ((displayText.matches("") || displayText.matches("0")) && character == '0')
                                return;
                            if (raiseLabel.selected) {
                                raiseLabel.selected = false;
                                raiseLabel.setText("");
                            }
                            raiseLabel.append(character);
                        }
                    });
                }
            }

            return table;
        }

        private Table getDeleteButton() {
            Table table = new Table();
            table.padTop(topPad).padBottom(bottomPad);
            table.setBackground(numberBackground);
            TextureRegion deleteTexture = gameAtlas.findRegion("btn_delete");
            float deleteHeight = numberStyle.font.getCapHeight();
            float deleteWidth = deleteHeight * deleteTexture.getRegionWidth() / deleteTexture.getRegionHeight();
            table.add(new Image(deleteTexture)).width(deleteWidth).height(deleteHeight);
            table.setTouchable(Touchable.enabled);
            table.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (raiseLabel.selected) {
                        raiseLabel.selected = false;
                        raiseLabel.setText("");
                    }
                    raiseLabel.delete();
                }
            });
            return table;
        }


    }


    public class RaiseTableButton extends Table {
        private Label label;
        private float amount;

        public RaiseTableButton(float amount, String text) {
            setBackground(buttonBackground);
            this.amount = PokerUtils.getValue(amount);
            label = new Label(text, buttonLabelStyle);
            add(label).padLeft(buttonPad).padRight(buttonPad);
            setTouchable(Touchable.enabled);
        }

        public void setAmount(float amount) {
            this.amount = PokerUtils.getValue(amount);
        }

        public float getAmount() {
            return this.amount;
        }
    }


    public class RaiseLabel extends Label {
        private String displayText;
        public boolean selected;
        public RaiseLabelStyle style;

        public RaiseLabel(String text, RaiseLabelStyle style) {
            super(text, style);
            this.displayText = text;
            this.style = style;
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            validate();
            Color fontColor = style.fontColor;
            if (selected) {
                style.fontColor = style.selectedColor;
                float selectedX = getX();
                float selectedY = getY();
                float selectedWidth = getGlyphLayout().width;
                float selectedHeight = getHeight();
                style.selection.draw(batch, selectedX, selectedY, selectedWidth, selectedHeight);
            }
            super.draw(batch, parentAlpha);
            style.fontColor = fontColor;
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
        }

        public void append(char c) {
            if (displayText == null || displayText.isEmpty()) {
                displayText = c + "";
            } else {
                if (!displayText.contains(".")) {
                    displayText = displayText + c;
                } else {
                    String[] s = displayText.split("\\.");
                    if (s.length > 1) {
                        if (s[1].length() < 2) {
                            displayText = displayText + c;
                        }
                    } else {
                        displayText = displayText + c;
                    }
                }
            }
            setText(displayText);
            onTextChanged(displayText);
        }

        public void delete() {
            if (displayText == null || displayText.length() == 0) return;
            if (displayText.length() == 1) {
                displayText = "";
            } else {
                displayText = displayText.substring(0, displayText.length() - 1);
            }
            setText(displayText);
            onTextChanged(displayText);
        }

        public void setText(String text) {
            this.displayText = text;
            super.setText(this.displayText);
        }

        public void onTextChanged(String text) {

        }
    }


    public class RaiseLabelStyle extends Label.LabelStyle {
        public Drawable selection;
        public Color selectedColor;
    }
}
