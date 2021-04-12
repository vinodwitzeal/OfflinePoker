package bigcash.poker.game.poker.messages;

import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter;

import bigcash.poker.constants.Constant;

public class PokerPlayerCustomData {
    public String id;
    public String name,imageId,imageUrl;
    public float balance,pokerBalance;

    public PokerPlayerCustomData(JsonValue value){
        JsonValue data=new JsonReader().parse(value.toJson(JsonWriter.OutputType.json));
        id=data.getString("id");
        name=data.getString("name","G"+id);
        imageId=data.getString("imageId","");
        if (this.imageId.isEmpty()){
            this.imageUrl="";
        }else {
            this.imageUrl="https://graph.facebook.com/" + this.imageId + "/picture?type=normal";
        }
        balance=data.getFloat("balance");
        pokerBalance=data.getFloat("pokerBalance");
    }

    public PokerPlayerCustomData(String id, String name, String imageId, float balance, float pokerBalance){
        this.id=id;
        this.name=name==null?"G"+id:name;
        this.imageId=imageId==null?"":imageId;
        if (this.imageId.isEmpty()){
            this.imageUrl="";
        }else {
            this.imageUrl="https://graph.facebook.com/" + this.imageId + "/picture?type=normal";
        }
        this.balance=balance;
        this.pokerBalance=pokerBalance;
    }

    public static class UserCustomData extends PokerPlayerCustomData {
        public UserCustomData() {
            super(Constant.userProfile.getUserId(),Constant.userProfile.getName(), Constant.userProfile.getImageId(), Constant.userProfile.getPaytmBalance(), Constant.userProfile.getPokerBalance());
        }
    }
}
