package bigcash.poker.utils;

import com.google.gwt.core.client.JavaScriptObject;

public class CurrencyFormatter extends JavaScriptObject {
    private static CurrencyFormatter instance;
    protected CurrencyFormatter() {
        super();
    }

    public static CurrencyFormatter getInstance(){
        if (instance==null){
            instance=newFormatter();
        }
        return instance;
    }

    public static native CurrencyFormatter newFormatter()/*-{
        return new Intl.NumberFormat('en-INR', {
                style: 'currency',
                currency: 'INR',
            });
    }-*/;

    public final native String format(float value)/*-{
        return this.format(4800)
    }-*/;
}
