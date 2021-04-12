package bigcash.poker.game.poker.messages;

import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter;

import bigcash.poker.utils.PokerConstants;

public class PokerPlayerData {
    public String id;
    public String name,imageUrl;
    public float balance,pokerBalance,betAmount;
    public int state;
    public String lastMoveStatus;
    public boolean isDealer;
    public PokerPlayerData(String playerData){
        JsonValue data=new JsonReader().parse(playerData);
        id=data.getString("id");
        name=data.getString("name","G"+id);
        String imageId=data.getString("imageId","");
        if (imageId.isEmpty()){
            imageUrl="";
        }else {
            this.imageUrl="https://graph.facebook.com/" + imageId + "/picture?type=normal";
        }
        state= PokerConstants.PLAYER_PLAYING;
        pokerBalance=data.getFloat("balance",0.0f);
        betAmount=data.getFloat("betAmount",0.0f);
    }

    public PokerPlayerData(JsonValue jsonValue){
        JsonValue data=new JsonReader().parse(jsonValue.toJson(JsonWriter.OutputType.json));
        id=data.getString("user");
        String status=data.getString("status","");

        lastMoveStatus=data.getString("lastMoveStatus","");
        if (!status.isEmpty()){
            if (status.matches("FOLD")){
                state=PokerConstants.PLAYER_FOLDED;
            }else if (status.matches("ALL_IN")){
                state=PokerConstants.PLAYER_ALL_IN;
            }else {
                state=PokerConstants.PLAYER_PLAYING;
            }
        }else {
            state=PokerConstants.PLAYER_PLAYING;
        }
        isDealer=data.getBoolean("isDealer",false);
        pokerBalance=data.getFloat("balance",0.0f);
        betAmount=data.getFloat("betAmount");
    }

    public PokerPlayerData(PokerPlayerCustomData pokerPlayerCustomData, int state){
        id= pokerPlayerCustomData.id;
        name= pokerPlayerCustomData.name;
        imageUrl= pokerPlayerCustomData.imageUrl;
        this.state=state;
        pokerBalance= pokerPlayerCustomData.pokerBalance;
        betAmount=0.0f;
    }

    public PokerPlayerData(String id, String name, String imageId, float balance, float pokerBalance){
        this.id=id;
        this.name=name;
        if (imageId==null || imageId.isEmpty()){
            this.imageUrl="";
        }else {
            this.imageUrl="https://graph.facebook.com/" + imageId + "/picture?type=normal";
        }
        this.state=PokerConstants.PLAYER_PLAYING;
        this.pokerBalance=pokerBalance;
    }
}
