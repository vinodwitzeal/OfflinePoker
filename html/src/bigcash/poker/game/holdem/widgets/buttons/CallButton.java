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
import bigcash.poker.utils.PokerUtils;

public class CallButton extends GameButton {
    public static final int CALL = 3;
    private HoldemWorld holdemWorld;
    private TextureRegionDrawable uncheckedDrawable, checkedDrawable;
    private Label label;
    private float imageSize, pad, buttonPad;
    private Image image;

    public CallButton(final HoldemWorld holdemWorld, int id, Label.LabelStyle labelStyle, TextureRegionDrawable uncheckedDrawable, TextureRegionDrawable checkedDrawable) {
        setName("CallButton");
        this.holdemWorld = holdemWorld;
        this.uncheckedDrawable = uncheckedDrawable;
        this.checkedDrawable = checkedDrawable;
        this.label = new Label("", labelStyle);
        setId(id);
        setState(NO_ACTION);
        this.imageSize = labelStyle.font.getLineHeight();
        this.buttonPad = imageSize;
        this.pad = imageSize * 0.25f;
        this.image = new Image();

        NinePatchDrawable background = new NinePatchDrawable(new NinePatch(holdemWorld.atlas.findRegion("btn_call"), 13, 13, 10, 15));
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
                    case CALL:
                        holdemWorld.screen.sendMove(PokerConstants.MOVE_CALL,getAmount());
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
                updateButton(uncheckedDrawable, "CALL");
                break;
            case CHECKED:
                updateButton(checkedDrawable, "CALL");
                break;
            case CALL:
                updateButton(null, "CALL");
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
        if (betPlaced) {
            float callAmount = maxBet - betAmount;
            if (callAmount > 0) {
                if (PokerUtils.getValue(balance) > PokerUtils.getValue(callAmount)) {
                    if (userTurn) {
                        updateButton(CALL, callAmount);
                    }else {
                        updateButton(UNCHECKED,callAmount);
                    }
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean processButton(boolean userTurn, boolean betPlaced, float balance, float betAmount, float maxBet) {
        if (betPlaced && userTurn) {
            float callAmount = maxBet - betAmount;
            if (callAmount > 0 && callAmount==getAmount()) {
                if (PokerUtils.getValue(balance) > PokerUtils.getValue(callAmount)) {
                    holdemWorld.screen.sendMove(PokerConstants.MOVE_CALL,callAmount);
                    return true;
                }
            }
        }
        return false;
    }
}
