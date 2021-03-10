package bigcash.poker.appwarp.events;

import com.google.gwt.core.client.JavaScriptObject;

public class MoveEvent extends JavaScriptObject {
    protected MoveEvent() {
        super();
    }

    public final native String getSender()/*-{
        return this.getSender();
    }-*/;

    public final native String getMoveData()/*-{
            return this.getMoveData();
    }-*/;

    public final native String getRoomId()/*-{
        return this.getRoomId();
    }-*/;

    public final native String getNextTurn()/*-{
        return this.getNextTurn();
    }-*/;
}
