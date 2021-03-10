package bigcash.poker.game;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter;

public class PokerMessage {
    private int type;
    private JsonValue data;
    private JsonValue messageData;

    public PokerMessage(JsonValue messageData){
        this.messageData=messageData;
        this.type=messageData.getInt("type");
        this.data=messageData.get("data");
    }

    public PokerMessage(int type, JsonValue data){
        this.type=type;
        this.data=data;
    }
    public PokerMessage(int type, String data){
        this.type=type;
        this.data=new JsonValue(data);
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public JsonValue getData() {
        return data;
    }

    public JsonValue getMessageData(){
        return messageData;
    }

    public void setData(JsonValue data) {
        this.data = data;
    }

    public String sendUpdateString(){
        JsonValue value = new JsonValue(JsonValue.ValueType.object);
        value.addChild("type", new JsonValue(type));
        value.addChild("data", data);
        return value.toJson(JsonWriter.OutputType.json);
    }

    @Override
    public String toString() {
        JsonValue jsonValue=new JsonValue(JsonValue.ValueType.object);
        jsonValue.addChild("type",new JsonValue(type));
        jsonValue.addChild("data",data);
        return jsonValue.toJson(JsonWriter.OutputType.json);
    }

    public static PokerMessage parseMessage(String messageString){
        return new Json().fromJson(PokerMessage.class,messageString);
    }
}
