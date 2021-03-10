package bigcash.poker.utils;

import com.google.gwt.core.client.JavaScriptObject;

public class GeolocationPosition extends JavaScriptObject {
    protected GeolocationPosition() {
        super();
    }

    public final native String getLatitude()/*-{
        return this.coords.latitude+"";
    }-*/;
    public final native String getLongitude()/*-{
        return this.coords.longitude+"";
    }-*/;
    public final native String getAccuracy()/*-{
        return this.coords.accuracy+"";
    }-*/;
}
