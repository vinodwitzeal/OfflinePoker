package bigcash.poker.game.omaha.widgets;

import com.badlogic.gdx.utils.Array;

import bigcash.poker.game.PokerContestPage;
import bigcash.poker.game.PokerLoginListener;
import bigcash.poker.game.omaha.OmahaGameScreen;
import bigcash.poker.game.omaha.controllers.OmahaWarpController;
import bigcash.poker.models.PokerContest;
import bigcash.poker.screens.PokerContestScreen;
import bigcash.poker.utils.GeolocationPosition;
import bigcash.poker.widgets.PageView;

public class OmahaContestPage extends PokerContestPage {
    public OmahaContestPage(PokerContestScreen contestScreen, PageView pageView) {
        super(contestScreen, pageView, PokerContestScreen.OMAHA);
    }

    @Override
    public void onContestsFetched(Array<PokerContest> max5Contests, Array<PokerContest> max2Contests) {
        if (max5Contests!=null && max5Contests.size>0) {
            for (int i = 0; i < max5Contests.size; i++) {
                contentTable.add(getContestTable(max5Contests.get(i))).width(pokerStyle.width).padBottom(pokerStyle.bottomPad).row();
            }
        }

        if (max2Contests!=null && max2Contests.size>0) {
            contentTable.add().padTop(density * 10f).row();
            for (int i = 0; i < max2Contests.size; i++) {
                contentTable.add(getContestTable(max2Contests.get(i))).width(pokerStyle.width).padBottom(pokerStyle.bottomPad).row();
            }
        }
    }

    @Override
    public void enterGame(PokerContest contest, GeolocationPosition position) {
        OmahaWarpController omahaWarpController = new OmahaWarpController(pokerGame, position, contest);
        new PokerLoginListener(contestScreen, omahaWarpController){
            @Override
            public void setScreen() {
                pokerGame.setScreen(new OmahaGameScreen(pokerGame,omahaWarpController,contest.getContestId()));
            }
        }.connect();
    }
}
