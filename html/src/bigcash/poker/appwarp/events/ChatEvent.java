package bigcash.poker.appwarp.events;

import com.google.gwt.core.client.JavaScriptObject;

public class ChatEvent extends JavaScriptObject {
    protected ChatEvent() {
        super();
    }

    public final native String getSender()/*-{
        return this.getSender();
    }-*/;

    public final native String getMessage()/*-{
        return this.getChat();
    }-*/;
    public final native String getLocationId()/*-{
        return this.getLocationId();
    }-*/;
    public final native boolean isLocationLobby()/*-{
        return this.isLocationLobby();
    }-*/;
}
