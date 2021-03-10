package bigcash.poker.game.holdem.widgets;

import com.badlogic.gdx.math.Vector2;

import java.util.List;

import bigcash.poker.game.holdem.messages.HoldemResultMessage;
import bigcash.poker.widgets.AmountLabel;

public class HoldemWinningLabel extends AmountLabel {
    private List<HoldemResultMessage.PokerPoolPlayer> winners;
    public HoldemWinningLabel(HoldemResultMessage.PokerWinning pokerWinning, PokerLabelStyle style) {
        super(pokerWinning.amount, style);
        updateAmount(pokerWinning.amount);
        this.winners=pokerWinning.winners;
    }


    public List<HoldemResultMessage.PokerPoolPlayer> getWinners(){
        return this.winners;
    }


    public Vector2 getStageCoordinates(){

        return localToStageCoordinates(new Vector2());
    }
}
