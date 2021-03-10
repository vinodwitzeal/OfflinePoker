package bigcash.poker.appwarp.events;

import com.google.gwt.core.client.JavaScriptObject;

public class LiveRoomInfoEvent extends JavaScriptObject {
    protected LiveRoomInfoEvent() {
        super();
    }

    public final native RoomData  getData()/*-{
        return this.getData();
    }-*/;

    public final native int  getResult()/*-{
        return this.getResult();
    }-*/;

    public final native String[]  getJoinedUsers()/*-{
        return this.getUsers();
    }-*/;

    public final native String  getCustomData()/*-{
        return this.getCustomData();
    }-*/;

    public final native String  getProperties()/*-{
        return this.getProperties();
    }-*/;
}
