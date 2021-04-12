package bigcash.poker.game.poker.omaha.widgets;

import java.util.List;

import bigcash.poker.game.poker.omaha.messages.OmahaResultMessage;
import bigcash.poker.widgets.AmountLabel;

public class OmahaWinningLabel extends AmountLabel {
    private List<OmahaResultMessage.PokerPoolPlayer> winners;
    public OmahaWinningLabel(OmahaResultMessage.PokerWinning pokerWinning, PokerLabelStyle style) {
        super(pokerWinning.amount, style);
        updateAmount(pokerWinning.amount);
        this.winners=pokerWinning.winners;
    }


    public List<OmahaResultMessage.PokerPoolPlayer> getWinners(){
        return this.winners;
    }

}
