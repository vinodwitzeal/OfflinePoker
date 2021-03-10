package bigcash.poker.qr;

import com.google.gwt.core.client.JavaScriptObject;

public class Camera extends JavaScriptObject {
    protected Camera() {
        super();
    }

    public final native String getId()/*-{
        return this.id;
    }-*/;

    public final native String getLabel()/*-{
        return this.label;
    }-*/;
}
