package bigcash.poker.game;

import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import bigcash.poker.utils.PokerConstants;
import bigcash.poker.utils.PokerUtils;

public class PokerJoinMessage {
    public String currentTurn;
    public long remainingTime;
    public int[] userCards;
    public int[] openCards;
    public TimerMessage timerMessage;
    public float[] pricePool;
    public float totalGameBet, totalRoundBet;
    private LinkedHashMap<String, PokerPlayerCustomData> users;
    public HashMap<String, PokerPlayerData> playerDataMap;
    public List<PokerPlayerData> players;
    public String[] joinedPlayersArray;
    public boolean gameRunning,lifeUsed;
    public float maxBetAmount;

    public PokerJoinMessage(JsonValue jsonValue, float maxBetAmount) {
        this.gameRunning = false;
        JsonValue messageData = new JsonReader().parse(jsonValue.toJson(JsonWriter.OutputType.json));
        currentTurn = messageData.getString("currentTurn", "");
        remainingTime = messageData.getLong("remainingTime", 0);
        lifeUsed=messageData.getBoolean("isCurrentTurnUserLifeUsed",false);
        this.maxBetAmount= PokerUtils.getValue(maxBetAmount);
        if (messageData.has("secondsLeft")) {
            timerMessage = new TimerMessage(messageData.getInt("secondsLeft"));
        }
        if (messageData.has("pricePool")) {
            pricePool = messageData.get("pricePool").asFloatArray();
        }
        totalGameBet = 0.0f;
        if (pricePool != null && pricePool.length > 0) {
            for (int i = 0; i < pricePool.length; i++) {
                totalGameBet = totalGameBet + PokerUtils.getValue(pricePool[i]);
            }
        }
        if (remainingTime > 0) {
            remainingTime = TimeUnit.SECONDS.toMillis((PokerConstants.TURN_TIME - remainingTime));
        }
        if (messageData.has("userCards")) {
            userCards = messageData.get("userCards").asIntArray();
        }

        if (messageData.has("openCards")) {
            openCards = messageData.get("openCards").asIntArray();
        }
        if (!currentTurn.isEmpty()) {
            gameRunning = true;
        }


        if (openCards != null && openCards.length > 0) {
            gameRunning = true;
        }

        users = new LinkedHashMap<String, PokerPlayerCustomData>();
        if (messageData.has("users")) {
            JsonValue usersArray = messageData.get("users");
            JsonValue.JsonIterator usersArrayIterator = usersArray.iterator();
            while (usersArrayIterator.hasNext()) {
                PokerPlayerCustomData pokerPlayerCustomData = new PokerPlayerCustomData(usersArrayIterator.next());
                users.put(pokerPlayerCustomData.id, pokerPlayerCustomData);
            }
        }

        playerDataMap = new HashMap<String, PokerPlayerData>();
        players = new ArrayList<PokerPlayerData>();
        if (messageData.has("players")) {
            JsonValue playersArray = messageData.get("players");
            JsonValue.JsonIterator playersArrayIterator = playersArray.iterator();
            totalRoundBet = 0.0f;
            while (playersArrayIterator.hasNext()) {
                PokerPlayerData pokerPlayerData = new PokerPlayerData(playersArrayIterator.next());
                PokerPlayerCustomData pokerPlayerCustomData = users.get(pokerPlayerData.id);
                if (pokerPlayerCustomData != null) {
                    pokerPlayerData.imageUrl = pokerPlayerCustomData.imageUrl;
                    pokerPlayerData.name = pokerPlayerCustomData.name;
                    users.remove(pokerPlayerData.id);
                    players.add(pokerPlayerData);
                    playerDataMap.put(pokerPlayerData.id, pokerPlayerData);
                }
                totalRoundBet = totalRoundBet + PokerUtils.getValue(pokerPlayerData.betAmount);
            }
        }

        if (pricePool==null || pricePool.length==0){
            totalGameBet=totalRoundBet;
        }
        PokerPlayerCustomData.UserCustomData userCustomData = new PokerPlayerCustomData.UserCustomData();
        if (!users.containsKey(userCustomData.id)) {
            users.put(userCustomData.id, userCustomData);
        }

        if (pricePool == null || pricePool.length == 0) {
            pricePool = new float[]{totalRoundBet};
        } else {
            pricePool[pricePool.length - 1] = pricePool[pricePool.length - 1] + totalRoundBet;
        }

        if (players.size() > 0) {
            gameRunning = true;
        }

        if (userCards != null && userCards.length > 0) {
            gameRunning = true;
        }

        if (players.size()==0){
            gameRunning=false;
        }

        for (PokerPlayerCustomData pokerPlayerCustomData : users.values()) {
            PokerPlayerData pokerPlayerData;
            if (gameRunning) {
                pokerPlayerData = new PokerPlayerData(pokerPlayerCustomData, PokerConstants.PLAYER_WATCHING);
            } else {
                pokerPlayerData = new PokerPlayerData(pokerPlayerCustomData, PokerConstants.PLAYER_PLAYING);
            }
            players.add(pokerPlayerData);
            playerDataMap.put(pokerPlayerData.id, pokerPlayerData);
        }

        joinedPlayersArray = new String[players.size()];
        for (int i = 0; i < joinedPlayersArray.length; i++) {
            joinedPlayersArray[i] = players.get(i).id;
        }
    }
}
