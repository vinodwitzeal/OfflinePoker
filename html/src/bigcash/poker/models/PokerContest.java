package bigcash.poker.models;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter;
public class PokerContest {

    private int contestId;
    private String contestType,gameType;
    private float minJoiningFee, maxJoiningFee;
    private int totalWinnings;
    private String imageUrl;
    private String offerText;
    private float commissionInPercentage;
    private float betValue;
    private int onlineUsers;
    private boolean lock;
    private int maxUsersPerTable;

    public PokerContest(JsonValue jsonValue){
        contestId = jsonValue.getInt("contestId");
        contestType = jsonValue.getString("contestType");
        gameType=jsonValue.getString("gameType","");
        maxJoiningFee = jsonValue.getFloat("maxJoiningFee");
        minJoiningFee = jsonValue.getFloat("minJoiningFee");
        totalWinnings = jsonValue.getInt("totalWinnings");
        imageUrl = jsonValue.getString("imageUrl");
        if(jsonValue.has("offerText")) {
            offerText = jsonValue.getString("offerText");
        }

        commissionInPercentage = jsonValue.getFloat("commissionInPercentage");
        betValue = jsonValue.getFloat("betValue");
        if(jsonValue.has("onlinePlayers")) {
            onlineUsers = jsonValue.getInt("onlinePlayers");
        }
        if(jsonValue.has("isLocked")) {
            lock = jsonValue.getBoolean("isLocked");
        }
        if(jsonValue.has("maxUsersPerTable")) {
            maxUsersPerTable = jsonValue.getInt("maxUsersPerTable");
        }
    }

    public PokerContest(){
        maxUsersPerTable=5;
    }

    public int getContestId() {
        return contestId;
    }

    public String getContestType() {
        return contestType;
    }

    public int getTotalWinnings() {
        return totalWinnings;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getOfferText() {
        return offerText;
    }

    public float getCommissionInPercentage() {
        return commissionInPercentage;
    }


    public int getOnlineUsers() {
        return onlineUsers;
    }

    public boolean isLock() {
        return lock;
    }

    public int getMaxUsersPerTable() {
        return maxUsersPerTable;
    }

    public float getMinJoiningFee() {
        return minJoiningFee;
    }

    public float getMaxJoiningFee() {
        return maxJoiningFee;
    }

    public float getBetValue() {
        return betValue;
    }

    public String getGameType(){
        return gameType;
    }
}
