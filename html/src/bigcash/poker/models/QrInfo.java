package bigcash.poker.models;

import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

public class QrInfo {
    private String qrId;
    private String placeId,tableId;
    private String logoImageUrl;

    public QrInfo(String qrId,String jsonString){
        this.qrId=qrId;
        JsonValue value=new JsonReader().parse(jsonString);
        placeId=value.getString("placeId");
        tableId=value.getString("tableId");
        logoImageUrl=value.getString("logoImageUrl");
    }

    public String getQrId() {
        return qrId;
    }

    public String getPlaceId() {
        return placeId;
    }

    public String getTableId() {
        return tableId;
    }

    public String getLogoImageUrl() {
        return logoImageUrl;
    }
}
