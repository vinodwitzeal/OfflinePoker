package bigcash.poker.game.holdem.messages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import bigcash.poker.constants.Constant;
import bigcash.poker.game.PokerPlayerData;
import bigcash.poker.game.holdem.HoldemWorld;
import bigcash.poker.game.holdem.widgets.HoldemPlayer;
import bigcash.poker.utils.PokerUtils;


public class HoldemResultMessage {
    private HoldemWorld holdemWorld;
    public LastPlayerMove lastPlayerMove;
    public int[] openCards;
    public List<PokerWinning> winnings;
    public LinkedHashMap<String, PokerResultPlayer> resultPlayerHashMap;
    public float totalBetAmount;
    public float[] winningAmounts;

    public HoldemResultMessage(HoldemWorld holdemWorld, JsonValue jsonValue) {
        JsonValue resultData = new JsonReader().parse(jsonValue.toJson(JsonWriter.OutputType.json));
        if (resultData.has("openCards")) {
            openCards = resultData.get("openCards").asIntArray();
        }

        if (resultData.has("lastPlayerMoveData")) {
            lastPlayerMove = new LastPlayerMove(resultData.get("lastPlayerMoveData"));
        }

        resultPlayerHashMap = new LinkedHashMap<String, PokerResultPlayer>();
        JsonValue playersArray = resultData.get("players");
        JsonValue.JsonIterator playersArrayIterator = playersArray.iterator();
        while (playersArrayIterator.hasNext()) {
            PokerResultPlayer pokerResultPlayer = new PokerResultPlayer(playersArrayIterator.next());
            if (pokerResultPlayer.refundAmount>0) {
                HoldemPlayer holdemPlayer = holdemWorld.getPlayer(pokerResultPlayer.id);
                if (holdemPlayer != null) {
                    holdemPlayer.addBalanceAmount(pokerResultPlayer.refundAmount);
                    holdemPlayer.updateBalanceAmount();
                }
            }
            pokerResultPlayer.updateData(holdemWorld.getPlayer(pokerResultPlayer.id));
            resultPlayerHashMap.put(pokerResultPlayer.id, pokerResultPlayer);
        }

        winnings = new ArrayList<PokerWinning>();
        JsonValue winningsArray = resultData.get("winnings");
        JsonValue.JsonIterator winningsArrayIterator = winningsArray.iterator();
        while (winningsArrayIterator.hasNext()) {
            winnings.add(new PokerWinning(winningsArrayIterator.next()));
        }


        this.totalBetAmount = 0.0f;
        this.winningAmounts = new float[winnings.size()];

        for (PokerWinning pokerWinning : winnings) {
            this.totalBetAmount = this.totalBetAmount + pokerWinning.amount;
        }
    }

    public class LastPlayerMove {
        public String player;
        public String moveType;
        public float betAmount;

        public LastPlayerMove(JsonValue move) {
            player = move.getString("id");
            moveType = move.getString("movType");
            betAmount = move.getFloat("betAmount", 0.0f);
        }
    }

    public class PokerResultPlayer {
        public String id;
        public String name;
        public String imageUrl;
        public int[] cards, handCards;
        public int groupType;
        public float totalWinningAmount, refundAmount, balance, pokerBalance;
        public boolean winner;

        public PokerResultPlayer(JsonValue jsonValue) {
            JsonValue playerResultData = new JsonReader().parse(jsonValue.toJson(JsonWriter.OutputType.json));
            this.id = playerResultData.getString("id");
            if (playerResultData.has("cards")) {
                this.cards = playerResultData.get("cards").asIntArray();
            }

            if (playerResultData.has("handCards")) {
                this.handCards = playerResultData.get("handCards").asIntArray();
            }
            totalWinningAmount = playerResultData.getFloat("winningAmount", 0.0f);
            refundAmount = playerResultData.getFloat("refundAmount", 0.0f);
            balance = playerResultData.getFloat("balance", 0.0f);
            pokerBalance = playerResultData.getFloat("pokerBalance", 0.0f);
            if (cards == null || cards.length < 2) {
                groupType = -1;
            } else {
                groupType = playerResultData.getInt("groupType", -1);
            }

        }


        public void updateData(HoldemPlayer holdemPlayer) {
            PokerPlayerData playerData = null;
            if (holdemPlayer != null) {
                playerData = holdemPlayer.getPlayerData();
            }
            if (playerData != null) {
                this.name = playerData.name;
                this.imageUrl = playerData.imageUrl;
            } else {
                this.name = "G" + id;
                this.imageUrl = "";
            }
        }
    }

    public class PokerPoolPlayer {
        //        public float poolWinAmount,amount;
        public float amount, betAmount, winning;
        public int winState;
        public String id;
        public PokerResultPlayer pokerResultPlayer;

        public PokerPoolPlayer(String id, PokerResultPlayer resultPlayer) {
            this.id = id;
            this.pokerResultPlayer = resultPlayer;
        }
    }

    public class PokerWinning {
        public float amount;
        public boolean allFolded;
        public List<PokerPoolPlayer> players;
        public ArrayList<PokerPoolPlayer> winners;

        public PokerWinning(JsonValue jsonValue) {
            JsonValue poolData = new JsonReader().parse(jsonValue.toJson(JsonWriter.OutputType.json));
            amount = poolData.getFloat("amount", 0.0f);
            this.players = new ArrayList<PokerPoolPlayer>();
            this.winners = new ArrayList<PokerPoolPlayer>();
            JsonValue playersArray = poolData.get("eligiblePlayer");
            JsonValue.JsonIterator playersArrayIterator = playersArray.iterator();;
            int foldedPlayers=0;
            while (playersArrayIterator.hasNext()) {
                JsonValue playerData = playersArrayIterator.next();
                String id = playerData.getString("id");
                float amount = playerData.getFloat("amount", 0.0f);
                float betAmount = playerData.getFloat("betAmount", 0.0f);
                int winner = playerData.getInt("isWinner", 0);
                PokerPoolPlayer pokerPoolPlayer = new PokerPoolPlayer(id, resultPlayerHashMap.get(id));
                pokerPoolPlayer.amount = PokerUtils.getValue(amount);
                pokerPoolPlayer.betAmount = PokerUtils.getValue(betAmount);
                pokerPoolPlayer.winState = winner;
                pokerPoolPlayer.winning = pokerPoolPlayer.amount;
                this.players.add(pokerPoolPlayer);
                if (pokerPoolPlayer.winState==1){
                    winners.add(pokerPoolPlayer);
                }

                if (pokerPoolPlayer.winState==-1){
                    foldedPlayers++;
                }
            }
            allFolded=foldedPlayers==players.size()-1;
            players=sortPlayers(players);
        }
    }

    private List<PokerPoolPlayer> sortPlayers(List<PokerPoolPlayer> players){
        String myID= Constant.userProfile.getUserId();
        List<PokerPoolPlayer> winners=new ArrayList<PokerPoolPlayer>();
        PokerPoolPlayer myDetail=null;
        List<PokerPoolPlayer> others=new ArrayList<PokerPoolPlayer>();
        for (PokerPoolPlayer player:players){
            if (player.winState==1){
                winners.add(player);
            }else {
                if (player.id.matches(myID)){
                    myDetail=player;
                }else {
                    others.add(player);
                }
            }
        }
        List<PokerPoolPlayer> allPlayers=new ArrayList<PokerPoolPlayer>();
        allPlayers.addAll(winners);

        if (myDetail!=null){
            allPlayers.add(myDetail);
        }
        allPlayers.addAll(others);
        return allPlayers;
    }
}

