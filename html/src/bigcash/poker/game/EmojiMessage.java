package bigcash.poker.game;

import com.badlogic.gdx.utils.JsonValue;

public class EmojiMessage {
    private String fromUserId, toUserId, emojiId;
    public EmojiMessage(JsonValue value){
        fromUserId=value.getString("fromUserId","");
        toUserId=value.getString("toUserId","");
        emojiId=value.getString("emojiId","");
    }

    public String getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(String fromUserId) {
        this.fromUserId = fromUserId;
    }

    public String getToUserId() {
        return toUserId;
    }

    public void setToUserId(String toUserId) {
        this.toUserId = toUserId;
    }

    public String getEmojiId() {
        return emojiId;
    }

    public void setEmojiId(String emojiId) {
        this.emojiId = emojiId;
    }

    public static JsonValue buildData(String myId, String otherUserId, String emojiId){
        JsonValue jsonValue=new JsonValue(JsonValue.ValueType.object);
        jsonValue.addChild("fromUserId",new JsonValue(myId));
        jsonValue.addChild("toUserId",new JsonValue(otherUserId));
        jsonValue.addChild("emojiId",new JsonValue(emojiId));
        return jsonValue;
    }
}
