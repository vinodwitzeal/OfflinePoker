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

public class ORaiseButton extends OmahaButton {
    public static final int RAISE = 3,ALL_IN=4;
    private OmahaWorld omahaWorld;
    private TextureRegionDrawable normalDrawable, uncheckedDrawable, checkedDrawable;
    private Label label;
    private float imageSize, pad, buttonPad;
    private Image image;

    public ORaiseButton(final OmahaWorld omahaWorld, int id, Label.LabelStyle labelStyle, TextureRegionDrawable uncheckedDrawable, TextureRegionDrawable checkedDrawable) {
        setName("RaiseButton");
        this.omahaWorld = omahaWorld;
        this.uncheckedDrawable = uncheckedDrawable;
        this.checkedDrawable = checkedDrawable;
        this.normalDrawable = new TextureRegionDrawable(omahaWorld.atlas.findRegion("icon_raise"));
        this.label = new Label("", labelStyle);
        this.imageSize = labelStyle.font.getLineHeight();
        this.buttonPad = imageSize;
        this.pad = imageSize * 0.25f;
        this.image = new Image(normalDrawable);
        setId(id);
        setState(NO_ACTION);

        NinePatchDrawable background = new NinePatchDrawable(new NinePatch(omahaWorld.atlas.findRegion("btn_raise"), 13, 13, 10, 15));
        setBackground(background);
        pad(0, buttonPad, 0, buttonPad);
        setTouchable(Touchable.enabled);

        addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                switch (getState()) {
                    case UNCHECKED:
                        updateButton(CHECKED,getAmount());
                        omahaWorld.screen.onButtonChecked(getId());
                        break;
                    case CHECKED:
                        updateButton(UNCHECKED,getAmount());
                        break;
                    case RAISE:
                        if (!omahaWorld.screen.omahaRaiseTable.isVisible()){
                            omahaWorld.screen.omahaRaiseTable.setVisible(true);
                        }else {
                            omahaWorld.screen.omahaRaiseTable.setVisible(false);
                            if (getAmount()>=omahaWorld.omahaUserPlayer.balanceAmount){
                                omahaWorld.screen.sendMove(PokerConstants.MOVE_ALL_IN, getAmount());
                            }else {
                                omahaWorld.screen.sendMove(PokerConstants.MOVE_RAISE, getAmount());
                            }
                        }
                        break;
                    case ALL_IN:
                        omahaWorld.screen.sendMove(PokerConstants.MOVE_ALL_IN,getAmount());
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
                float minRaiseAmount=omahaWorld.getMinRaiseBet();
                float maxRaiseAmount=omahaWorld.getMaxRaiseBet();
                if (balance>minRaiseAmount && minRaiseAmount>0){
                    omahaWorld.screen.omahaRaiseTable.updateBets(minRaiseAmount,maxRaiseAmount,balance);
                    updateButton(RAISE,minRaiseAmount);
                    return true;
                }else {
                    if (balance>0){
                        updateButton(ALL_IN,balance);
                        return true;
                    }
                }
            }else {
                float minRaiseAmount=omahaWorld.getMinRaiseBet();
                float maxRaiseAmount=omahaWorld.getMaxRaiseBet();
                if (balance>minRaiseAmount){
                    omahaWorld.screen.omahaRaiseTable.updateBets(minRaiseAmount,maxRaiseAmount,balance);
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
