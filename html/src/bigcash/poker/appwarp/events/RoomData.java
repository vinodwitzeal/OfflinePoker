package bigcash.poker.appwarp.events;

import com.google.gwt.core.client.JavaScriptObject;

public class RoomData extends JavaScriptObject {
    protected RoomData() {
        super();
    }

    public final native String getId()/*-{
        return this.id;
    }-*/;
}
