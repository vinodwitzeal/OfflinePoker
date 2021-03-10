package bigcash.poker.appwarp.events;

import com.google.gwt.core.client.JavaScriptObject;

public class AllRoomEvent extends JavaScriptObject {
    protected AllRoomEvent() {
        super();
    }

    public final native int getResult()/*-{
        return this.getResult();
    }-*/;

    public final native String[] getRoomIds()/*-{
        return this.getRoomIds();
    }-*/;
}
