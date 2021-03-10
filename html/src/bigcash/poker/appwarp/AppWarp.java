package bigcash.poker.appwarp;

import com.google.gwt.core.client.JavaScriptObject;

public class AppWarp  extends JavaScriptObject {
    protected AppWarp() {
        super();
    }
    public static native void initialize(String apiKey, String address)/*-{
        $wnd.AppWarp.WarpClient.initialize(apiKey,address);
    }-*/;

    public static native void initializeSecure(String apiKey,String address)/*-{
        $wnd.AppWarp.WarpClient.initialize(apiKey,address,"12347",true);
    }-*/;

    public static native WarpClient getInstance()/*-{
        return $wnd.AppWarp.WarpClient.getInstance();
    }-*/;
}
