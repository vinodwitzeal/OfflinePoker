package bigcash.poker.game.omaha.controllers;


import com.badlogic.gdx.utils.JsonValue;

import bigcash.poker.game.PokerWarpController;
import bigcash.poker.gwt.PokerGame;
import bigcash.poker.models.PokerContest;
import bigcash.poker.utils.GeolocationPosition;

public class OmahaWarpController extends PokerWarpController {
    public OmahaWarpController(PokerGame proGame, GeolocationPosition position, PokerContest contest) {
        super(proGame, position, contest);
    }

    @Override
    public void addGameProperties(JsonValue properties) {
        properties.addChild("game",new JsonValue("omaha"));
    }
}