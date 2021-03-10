package bigcash.poker.game.omaha.widgets;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import bigcash.poker.font.FontPool;
import bigcash.poker.font.FontType;
import bigcash.poker.game.PokerRaiseTable;
import bigcash.poker.game.holdem.widgets.buttons.RaiseButton;
import bigcash.poker.game.omaha.OmahaWorld;
import bigcash.poker.utils.PokerUtils;


public class OmahaRaiseTable extends PokerRaiseTable {
    private OmahaWorld omahaWorld;
    public float minBet, maxBet, betAmount;

    public OmahaRaiseTable(OmahaWorld omahaWorld, float minBetAmount, float maxBetAmount) {
        super(omahaWorld.screen,minBetAmount, maxBetAmount, 0.1f);
        this.omahaWorld = omahaWorld;
    }


    public void updateBets(float minBet, float maxBet, float balance) {
        if (maxBet > balance) {
            maxBet = balance;
        }
        this.minBet = PokerUtils.getValue(minBet);
        this.maxBet = PokerUtils.getValue(maxBet);
        setBetAmount(this.minBet);
        this.slider.setRange(this.minBet, this.maxBet);
        this.slider.setValue(this.minBet, true);
        maxButton.setAmount(this.maxBet);
        potButton.setAmount(PokerUtils.getValue(Math.min(this.minBet * 2, this.maxBet)));
        minButton.setAmount(this.minBet);
    }

    private void setTextFieldValue(float value) {
        raiseLabel.setText(PokerUtils.getValue(value) + "");
    }

    @Override
    public void addSymbol(Table amountTable, float height) {
        Label.LabelStyle symbolStyle = new Label.LabelStyle();
        symbolStyle.font = FontPool.obtain(FontType.ROBOTO_BOLD, 7);
        symbolStyle.fontColor = Color.BLACK;
        amountTable.add(new Label("\u20b9 ", symbolStyle));
    }

    @Override
    public void onLabelTextChanged(String displayText) {
        float currentValue = getFloatValue(displayText);
        slider.setValue(currentValue, false);
        if (currentValue>maxBet){
            showError("Amount can't be greater than \u20b9 " + maxBet);
            setBetAmount(maxBet);
            setTextFieldValue(maxBet);
            return;
        }
        setBetAmount(currentValue);
    }

    @Override
    public void onMaxButtonClicked() {
        slider.setValue(maxButton.getAmount(),true);
    }

    @Override
    public void onPotButtonClicked() {
        slider.setValue(potButton.getAmount(),true);
    }

    @Override
    public void onMinButtonClicked() {
        slider.setValue(minButton.getAmount(),true);
    }

    @Override
    public void onPlusButtonClicked() {
        if(!slider.setValue(slider.getValue()+stepSize,true)){
            showError("Amount can't be greater than "+maxBet);
        }
    }

    @Override
    public void onMinusButtonClicked() {
        if(!slider.setValue(slider.getValue()-stepSize,true)){
            showError("Amount can't be less than "+minBet);
        }
    }

    @Override
    public void onSliderValueChanged(float value) {
        float betValue = PokerUtils.getValue(value);
        setBetAmount(betValue);
        setTextFieldValue(betValue);
    }

    @Override
    public void onShow() {
        clearError();
        setUserTable(omahaWorld.omahaUserPlayer);
        setRaiseButton(omahaWorld.screen.raiseButton);
        slider.setValue(minBet, true);
        setTextFieldValue(minBet);
    }

    @Override
    public void onHide() {
        setBetAmount(minBet);
    }

    @Override
    public boolean isValid() {
        if (betAmount < minBet) {
            showError("Amount can't be less than \u20b9 " + minBet);
            return false;
        }

        if (betAmount > maxBet) {
            showError("Amount can't be greater than \u20b9 " + maxBet);
            return false;
        }
        return true;
    }

    public void setBetAmount(float amount) {
        betAmount = PokerUtils.getValue(amount);
        if (betAmount == maxBet) {
            omahaWorld.screen.raiseButton.updateButton(RaiseButton.RAISE, maxBet);
        } else if (betAmount < maxBet) {
            omahaWorld.screen.raiseButton.updateButton(RaiseButton.RAISE, betAmount);
        }
    }
}

