package bigcash.poker.utils;

import com.google.gwt.core.client.JavaScriptObject;

public class CookieData extends JavaScriptObject {
    protected CookieData() {
        super();
    }

    public final native String getOTP()/*-{
        return this.otp;
    }-*/;

    public final native String getName()/*-{
        return this.name;
    }-*/;

    public final native String getUserId()/*-{
        return this.userId;
    }-*/;


    public static native CookieData getCookieData()/*-{
        return $wnd.cookieData;
    }-*/;


    public static native String getAppLaunchResponse()/*-{
        return $wnd.appLaunchResponse;
    }-*/;
}
