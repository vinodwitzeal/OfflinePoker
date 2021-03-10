package bigcash.poker.game.holdem.controllers;

import com.badlogic.gdx.utils.JsonValue;

import bigcash.poker.game.PokerWarpController;
import bigcash.poker.gwt.PokerGame;
import bigcash.poker.models.PokerContest;
import bigcash.poker.utils.GeolocationPosition;
import bigcash.poker.utils.PokerConstants;

public class HoldemWarpController extends PokerWarpController{
    private int tableType;
    private String qrId;

    public HoldemWarpController(PokerGame proGame, GeolocationPosition position, PokerContest contest) {
        this(proGame, position, contest,"",PokerConstants.PUBLIC_TABLE);
    }

    public HoldemWarpController(PokerGame proGame, GeolocationPosition position, PokerContest contest, String qrId, int tableType) {
        super(proGame, position, contest);
        this.qrId=qrId;
        this.tableType=tableType;
    }

    @Override
    public void addGameProperties(JsonValue properties) {
        if(tableType== PokerConstants.PRIVATE_TABLE) {
            properties.addChild("game", new JsonValue(PokerConstants.QR_HOLDEM));
            properties.addChild("qrId", new JsonValue(qrId));
        }
    }
}