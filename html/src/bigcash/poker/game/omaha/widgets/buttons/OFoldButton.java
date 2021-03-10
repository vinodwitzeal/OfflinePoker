package bigcash.poker.game.omaha.widgets.buttons;

import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import bigcash.poker.game.omaha.OmahaWorld;
import bigcash.poker.utils.PokerConstants;

public class OFoldButton extends OmahaButton {
    public static final int FOLD=3;
    private OmahaWorld omahaWorld;
    private TextureRegionDrawable uncheckedDrawable, checkedDrawable;
    private Label label;
    private float imageSize, pad, buttonPad;
    private Image image;

    public OFoldButton(final OmahaWorld omahaWorld, int id, Label.LabelStyle labelStyle, TextureRegionDrawable uncheckedDrawable, TextureRegionDrawable checkedDrawable) {
        setName("FoldButton");
        this.omahaWorld = omahaWorld;
        this.uncheckedDrawable = uncheckedDrawable;
        this.checkedDrawable = checkedDrawable;
        this.label = new Label("", labelStyle);
       setId(id);
       setState(NO_ACTION);
        this.imageSize = labelStyle.font.getLineHeight();
        this.buttonPad = imageSize;
        this.pad = imageSize * 0.25f;
        this.image = new Image();

        NinePatchDrawable background = new NinePatchDrawable(new NinePatch(omahaWorld.atlas.findRegion("btn_fold"), 13, 13, 10, 15));
        setBackground(background);
        pad(0, buttonPad, 0, buttonPad);
        setTouchable(Touchable.enabled);

        addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                switch (getState()) {
                    case UNCHECKED:
                        updateButton(CHECKED);
                        omahaWorld.screen.onButtonChecked(getId());
                        break;
                    case CHECKED:
                        updateButton(UNCHECKED);
                        break;
                    case FOLD:
                        omahaWorld.screen.sendMove(PokerConstants.MOVE_FOLD,0.0f);
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
                updateButton(uncheckedDrawable, "FOLD");
                break;
            case CHECKED:
                updateButton(checkedDrawable, "FOLD");
                break;
            case FOLD:
                updateButton(null,"FOLD");
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
        if (userTurn){
            updateButton(FOLD);
            return true;
        }else{
            if (betPlaced) {
                float callAmount = maxBet - betAmount;
                if (callAmount > 0) {
                    updateButton(UNCHECKED, 0.0f);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean processButton(boolean userTurn, boolean betPlaced, float balance, float betAmount, float maxBet) {
        if (userTurn){
            omahaWorld.screen.sendMove(PokerConstants.MOVE_FOLD,0.0f);
            return true;
        }
        return false;
    }
}
