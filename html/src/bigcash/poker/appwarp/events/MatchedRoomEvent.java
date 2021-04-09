package bigcash.poker.appwarp.events;

import com.google.gwt.core.client.JavaScriptObject;

public class MatchedRoomEvent extends JavaScriptObject {
    protected MatchedRoomEvent() {
        super();
    }

    public final native int getResult()/*-{
        return this.getResult();
    }-*/;

    public final native RoomData[] getRoomsData()/*-{
        return this.getRooms();
    }-*/;
}
