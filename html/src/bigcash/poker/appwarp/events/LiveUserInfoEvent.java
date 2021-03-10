package bigcash.poker.appwarp.events;

import com.google.gwt.core.client.JavaScriptObject;

public class LiveUserInfoEvent extends JavaScriptObject {
    protected LiveUserInfoEvent() {
        super();
    }

    public final native int getResult()/*-{
        return this.getResult();
    }-*/;

    public final native boolean isLocationLobby()/*-{
        return this.isLocationLobby();
    }-*/;

    public final native String getLocationId()/*-{
        return this.getLocationId();
    }-*/;

    public final native String getName()/*-{
        return this.getName();
    }-*/;

    public final native String getCustomData()/*-{
        return this.getCustomData();
    }-*/;
}
