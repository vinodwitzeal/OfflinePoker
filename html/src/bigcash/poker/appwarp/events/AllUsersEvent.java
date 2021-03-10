package bigcash.poker.appwarp.events;

import com.google.gwt.core.client.JavaScriptObject;

public class AllUsersEvent extends JavaScriptObject {
    protected AllUsersEvent() {
        super();
    }

    public final native int getResult()/*-{
        return this.getResult();
    }-*/;

    public final native String[] getUserNames()/*-{
        return this.getUserNames();
    }-*/;
}
