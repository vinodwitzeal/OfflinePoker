package bigcash.poker.game.holdem.widgets.buttons;

import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import bigcash.poker.game.holdem.HoldemWorld;
import bigcash.poker.utils.PokerConstants;

public class RaiseButton extends GameButton {
    public static final int RAISE = 3,ALL_IN=4;
    private HoldemWorld holdemWorld;
    private TextureRegionDrawable normalDrawable, uncheckedDrawable, checkedDrawable;
    private Label label;
    private float imageSize, pad, buttonPad;
    private Image image;

    public RaiseButton(final HoldemWorld holdemWorld, int id, Label.LabelStyle labelStyle, TextureRegionDrawable uncheckedDrawable, TextureRegionDrawable checkedDrawable) {
        setName("RaiseButton");
        this.holdemWorld = holdemWorld;
        this.uncheckedDrawable = uncheckedDrawable;
        this.checkedDrawable = checkedDrawable;
        this.normalDrawable = new TextureRegionDrawable(holdemWorld.atlas.findRegion("icon_raise"));
        this.label = new Label("", labelStyle);
        this.imageSize = labelStyle.font.getLineHeight();
        this.buttonPad = imageSize;
        this.pad = imageSize * 0.25f;
        this.image = new Image(normalDrawable);
        setId(id);
        setState(NO_ACTION);

        NinePatchDrawable background = new NinePatchDrawable(new NinePatch(holdemWorld.atlas.findRegion("btn_raise"), 13, 13, 10, 15));
        setBackground(background);
        pad(0, buttonPad, 0, buttonPad);
        setTouchable(Touchable.enabled);

        addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                switch (getState()) {
                    case UNCHECKED:
                        updateButton(CHECKED,getAmount());
                        holdemWorld.screen.onButtonChecked(getId());
                        break;
                    case CHECKED:
                        updateButton(UNCHECKED,getAmount());
                        break;
                    case RAISE:
                        if (!holdemWorld.screen.holdemRaiseTable.isVisible()){
                            holdemWorld.screen.holdemRaiseTable.setVisible(true);
                        }else {
                            holdemWorld.screen.holdemRaiseTable.setVisible(false);
                            if (getAmount()>= holdemWorld.holdemUserPlayer.balanceAmount){
                                holdemWorld.screen.sendMove(PokerConstants.MOVE_ALL_IN, getAmount());
                            }else {
                                holdemWorld.screen.sendMove(PokerConstants.MOVE_RAISE, getAmount());
                            }
                        }
                        break;
                    case ALL_IN:
                        holdemWorld.screen.sendMove(PokerConstants.MOVE_ALL_IN,getAmount());
                        break;

                }
            }
        });
    }

    public void updateButton(int state, float amount) {
        setState(state);
        setAmount(amount);
        switch (state) {
            case UNCHECKED:
                updateButton(uncheckedDrawable, "RAISE");
                break;
            case CHECKED:
                updateButton(checkedDrawable, "RAISE");
                break;
            case RAISE:
                updateButton(normalDrawable, "RAISE");
                break;
            case ALL_IN:
                updateButton(normalDrawable,"ALL IN");
                break;
            default:
                clearChildren();
                break;

        }
    }

    private void updateButton(Drawable iconDrawable, String buttonText) {
        clearChildren();
        if (iconDrawable != null) {
            image.setDrawable(iconDrawable);
            add(image).width(imageSize).height(imageSize).padRight(pad);
        }
        if (getAmount() > 0.0f) {
            label.setText(buttonText + " \u20b9 " + getAmount());
        } else {
            label.setText(buttonText);
        }
        add(label);
    }

    @Override
    public boolean updateButton(boolean userTurn, boolean betPlaced, float balance, float betAmount, float maxBet) {
        if (userTurn){
            if (betPlaced){
                float minRaiseAmount=maxBet-betAmount+ holdemWorld.getBigBlind();
                if (balance>minRaiseAmount && minRaiseAmount>0){
                    holdemWorld.screen.holdemRaiseTable.updateBets(minRaiseAmount,balance);
                    updateButton(RAISE,minRaiseAmount);
                    return true;
                }else {
                    if (balance>0){
                        updateButton(ALL_IN,balance);
                        return true;
                    }
                }
            }else {
                float minRaiseAmount= holdemWorld.getBigBlind();
                if (balance>minRaiseAmount){
                    holdemWorld.screen.holdemRaiseTable.updateBets(minRaiseAmount,balance);
                    updateButton(RAISE,minRaiseAmount);
                    return true;
                }else {
                    if (balance>0){
                        updateButton(ALL_IN,balance);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean processButton(boolean userTurn, boolean betPlaced, float balance, float betAmount, float maxBet) {
        return false;
    }
}
