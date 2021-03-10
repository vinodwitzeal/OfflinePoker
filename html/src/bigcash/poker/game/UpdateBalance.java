package bigcash.poker.game;

import com.badlogic.gdx.utils.JsonValue;

public class UpdateBalance {
    private String id;
    private float pokerBalance;

    public UpdateBalance(JsonValue value){
        id=value.getString("id","");
        pokerBalance=value.getFloat("pokerBalance",0);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public float getPokerBalance() {
        return pokerBalance;
    }

    public void setPokerBalance(float pokerBalance) {
        this.pokerBalance = pokerBalance;
    }

    public static JsonValue buildData(String id,float pokerBalance){
        JsonValue jsonValue=new JsonValue(JsonValue.ValueType.object);
        jsonValue.addChild("id",new JsonValue(id));
        jsonValue.addChild("pokerBalance",new JsonValue(pokerBalance));
        return jsonValue;
    }
}
