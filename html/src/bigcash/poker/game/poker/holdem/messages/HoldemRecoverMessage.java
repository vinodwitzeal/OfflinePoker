package bigcash.poker.game.poker.holdem.messages;

import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import bigcash.poker.game.poker.messages.PokerPlayerCustomData;
import bigcash.poker.game.poker.messages.PokerPlayerData;
import bigcash.poker.game.poker.messages.TimerMessage;
import bigcash.poker.utils.PokerConstants;
import bigcash.poker.utils.PokerUtils;

public class HoldemRecoverMessage {

    public String currentTurn;
    public long remainingTime;
    public TimerMessage timerMessage;
    public int[] userCards;
    public int[] openCards;
    public float[] pricePool;
    public float totalGameBet, totalRoundBet;
    private LinkedHashMap<String, PokerPlayerCustomData> users;
    public HashMap<String, PokerPlayerData> playerDataMap;
    public List<PokerPlayerData> players;
    public String[] joinedPlayersArray;
    public boolean gameRunning,lifeUsed;
    public float maxBetAmount;

    public HoldemRecoverMessage(JsonValue jsonValue, float maxBetAmount) {
        this.gameRunning = false;
        JsonValue messageData = new JsonReader().parse(jsonValue.toJson(JsonWriter.OutputType.json));
        currentTurn = messageData.getString("currentTurn", "");
        remainingTime = messageData.getLong("remainingTime", 0);
        this.maxBetAmount= PokerUtils.getValue(maxBetAmount);
        lifeUsed=messageData.getBoolean("isCurrentTurnUserLifeUsed",false);

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

        if (userCards != null && userCards.length > 0) {
            gameRunning = true;
        }

        if (openCards != null && openCards.length > 0) {
            gameRunning = true;
        }

        JsonValue usersArray = messageData.get("users");
        JsonValue.JsonIterator usersArrayIterator = usersArray.iterator();
        users = new LinkedHashMap<String, PokerPlayerCustomData>();
        while (usersArrayIterator.hasNext()) {
            PokerPlayerCustomData pokerPlayerCustomData = new PokerPlayerCustomData(usersArrayIterator.next());
            users.put(pokerPlayerCustomData.id, pokerPlayerCustomData);
        }


        JsonValue playersArray = messageData.get("players");
        JsonValue.JsonIterator playersArrayIterator = playersArray.iterator();
        playerDataMap = new HashMap<String, PokerPlayerData>();
        players = new ArrayList<PokerPlayerData>();
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

        if (pricePool==null || pricePool.length==0){
            totalGameBet=totalRoundBet;
        }

        if (pricePool == null || pricePool.length == 0) {
            pricePool = new float[]{totalRoundBet};
        } else {
            pricePool[pricePool.length - 1] = pricePool[pricePool.length - 1] + totalRoundBet;
        }

        if (players.size() > 0) {
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
