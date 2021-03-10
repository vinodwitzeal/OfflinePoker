package bigcash.poker.appwarp.events;

import com.google.gwt.core.client.JavaScriptObject;

public class LobbyEvent extends JavaScriptObject {
    protected LobbyEvent() {
        super();
    }

    public final native int getResult()/*-{
        return this.getResult();
    }-*/;

    public final native String getId()/*-{
        return this.getId();
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

    public final native boolean isPrimary()/*-{
        return this.isPrimary();
    }-*/;
}
