package bigcash.poker.game.poker.holdem.messages;

import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bigcash.poker.game.poker.messages.PokerPlayerCustomData;
import bigcash.poker.game.poker.messages.PokerPlayerData;
import bigcash.poker.utils.PokerConstants;
import bigcash.poker.utils.PokerUtils;


public class HoldemDealerMessage {
    private String dealer;
    private Blind smallBlind,bigBlind;
    public String[] joinedPlayersArray;
    public HashMap<String, PokerPlayerData> playerDataMap;
    public HoldemDealerMessage(JsonValue data){
        dealer=data.getString("dealer");
        smallBlind=new Blind(data.get("smallBlind"));
        bigBlind=new Blind(data.get("bigBlind"));


        playerDataMap=new HashMap<String, PokerPlayerData>();
        HashMap<String, PokerPlayerCustomData> usersMap=new HashMap<String, PokerPlayerCustomData>();

        JsonValue playerArrangementDto=new JsonReader().parse(data.getString("playerArrangementDto"));

        JsonValue usersArray=playerArrangementDto.get("users");
        JsonValue.JsonIterator usersArrayIterator=usersArray.iterator();
        while (usersArrayIterator.hasNext()){
            PokerPlayerCustomData pokerPlayerCustomData =new PokerPlayerCustomData(usersArrayIterator.next());
            usersMap.put(pokerPlayerCustomData.id, pokerPlayerCustomData);
        }

        List<PokerPlayerData> players=new ArrayList<PokerPlayerData>();

        JsonValue playersArray=playerArrangementDto.get("players");
        JsonValue.JsonIterator playersArrayIterator=playersArray.iterator();
        while (playersArrayIterator.hasNext()) {
            PokerPlayerData pokerPlayerData = new PokerPlayerData(playersArrayIterator.next());
            PokerPlayerCustomData pokerPlayerCustomData = usersMap.get(pokerPlayerData.id);
            if (pokerPlayerCustomData != null) {
                pokerPlayerData.imageUrl = pokerPlayerCustomData.imageUrl;
                pokerPlayerData.name = pokerPlayerCustomData.name;
                usersMap.remove(pokerPlayerData.id);
                players.add(pokerPlayerData);
                playerDataMap.put(pokerPlayerData.id, pokerPlayerData);
            }
        }


        for (PokerPlayerCustomData pokerPlayerCustomData :usersMap.values()){
            PokerPlayerData pokerPlayerData =new PokerPlayerData(pokerPlayerCustomData, PokerConstants.PLAYER_WATCHING);
            players.add(pokerPlayerData);
            playerDataMap.put(pokerPlayerData.id, pokerPlayerData);
        }

        joinedPlayersArray=new String[players.size()];
        for (int i=0;i<joinedPlayersArray.length;i++){
            joinedPlayersArray[i]=players.get(i).id;
        }
    }

    public String getDealer() {
        return dealer;
    }

    public Blind getSmallBlind() {
        return smallBlind;
    }

    public Blind getBigBlind() {
        return bigBlind;
    }

    public class Blind{
        private String user;
        private float bet;

        public Blind(JsonValue data){
            this.user=data.getString("user");
            this.bet= PokerUtils.getValue(data.getFloat("bet",0.0f));
        }
        public String getUser() {
            return user;
        }

        public float getBet() {
            return bet;
        }
    }
}
