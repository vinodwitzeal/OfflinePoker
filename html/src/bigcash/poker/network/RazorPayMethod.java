package bigcash.poker.network;

import com.google.gwt.core.client.JavaScriptObject;

public class RazorPayMethod {
    public static native JavaScriptObject getCard()/*-{
        return {
                "card": "true",
                "netbanking": "false",
                "upi": "false",
                "emi": "false",
                "wallet": "false"
            };
    }-*/;

    public static native JavaScriptObject getNetBanking()/*-{
        return {
                "card": "false",
                "netbanking": "true",
                "upi": "false",
                "emi": "false",
                "wallet": "false"
            };
    }-*/;

    public static native JavaScriptObject getUPI()/*-{
        return {
                "card": "false",
                "netbanking": "false",
                "upi": "true",
                "emi": "false",
                "wallet": "false"
            };
    }-*/;

    public static native JavaScriptObject getEMI()/*-{
        return {
                "card": "false",
                "netbanking": "false",
                "upi": "false",
                "emi": "true",
                "wallet": "false"
            };
    }-*/;

    public static native JavaScriptObject getWallet()/*-{
        return {
                "card": "false",
                "netbanking": "false",
                "upi": "false",
                "emi": "false",
                "wallet": "true"
            };
    }-*/;
}
