package bigcash.poker.appwarp.events;

import com.google.gwt.core.client.JavaScriptObject;

public class RoomEvent extends JavaScriptObject {
    protected RoomEvent() {
        super();
    }

    public final native int getResult()/*-{
        return this.getResult();
    }-*/;

    public final native String getRoomId()/*-{
        return this.getRoomId();
    }-*/;

    public final native String getRoomOwner()/*-{
        return this.getRoomOwner();
    }-*/;

    public final native String getName()/*-{
        return this.getName();
    }-*/;

    public final native int getMaxUsers()/*-{
        return this.getMaxUsers();
    }-*/;
}
