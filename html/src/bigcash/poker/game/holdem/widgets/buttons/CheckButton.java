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

public class CheckButton extends GameButton {
    public static final int CHECK = 3;
    private HoldemWorld holdemWorld;
    private TextureRegionDrawable uncheckedDrawable, checkedDrawable;
    private Label label;
    private float imageSize, pad, buttonPad;
    private Image image;

    public CheckButton(final HoldemWorld holdemWorld, int id, Label.LabelStyle labelStyle, TextureRegionDrawable uncheckedDrawable, TextureRegionDrawable checkedDrawable) {
        setName("CheckButton");
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

        NinePatchDrawable background = new NinePatchDrawable(new NinePatch(holdemWorld.atlas.findRegion("btn_check"), 13, 13, 10, 15));
        setBackground(background);
        pad(0, buttonPad, 0, buttonPad);
        setTouchable(Touchable.enabled);

        addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                switch (getState()) {
                    case UNCHECKED:
                        updateButton(CHECKED);
                        holdemWorld.screen.onButtonChecked(getId());
                        break;
                    case CHECKED:
                        updateButton(UNCHECKED);
                        break;
                    case CHECK:
                        holdemWorld.screen.sendMove(PokerConstants.MOVE_CHECK, 0.0f);
                        holdemWorld.playCheckSound();
                        break;
                }
            }
        });
    }

    @Override
    public void updateButton(int state, float amount) {
        updateButton(state);
    }

    public void updateButton(int state) {
        setState(state);
        switch (state) {
            case UNCHECKED:
                updateButton(uncheckedDrawable, "CHECK");
                break;
            case CHECKED:
                updateButton(checkedDrawable, "CHECK");
                break;
            case CHECK:
                updateButton(null, "CHECK");
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
        label.setText(buttonText);
        add(label);
    }

    @Override
    public boolean updateButton(boolean userTurn, boolean betPlaced, float balance, float betAmount, float maxBet) {
        if (betPlaced){
            if (userTurn){
                if (betAmount>=maxBet){
                    updateButton(CHECK);
                    return true;
                }
            }else {
                if (betAmount>=maxBet){
                    updateButton(UNCHECKED);
                    return true;
                }
            }
        }else {
            if (userTurn){
                updateButton(CHECK);
            }else {
                updateButton(UNCHECKED);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean processButton(boolean userTurn, boolean betPlaced, float balance, float betAmount, float maxBet) {
        if (userTurn){
            if (betPlaced){
                if (betAmount>=maxBet){
                    holdemWorld.screen.sendMove(PokerConstants.MOVE_CHECK,0.0f);
                    return true;
                }
            }else {
                holdemWorld.screen.sendMove(PokerConstants.MOVE_CHECK,0.0f);
                return true;
            }
        }
        return false;
    }
}
