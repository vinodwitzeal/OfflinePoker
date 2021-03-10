package bigcash.poker.utils;

import com.google.gwt.core.client.JavaScriptObject;

public abstract class PokerInterval {
    private JavaScriptObject timer;
    private int interval;
    public PokerInterval(int interval){
        this.interval=interval;
    }

    public PokerInterval(){
        this(1000);
    }
    public void setInterval(int interval){
        this.interval=interval;
    }
    public abstract void onInterval();


    public void start(){
        timer=setInterval(interval,this);
    }

        private final native JavaScriptObject setInterval(int interval,PokerInterval updater)/*-{
        return setInterval(function(){
                updater.@bigcash.poker.utils.PokerInterval::onInterval()();
                },interval);
        }-*/;

    public void cancel(){
        clearInterval(timer);
    }

    private final native void clearInterval(JavaScriptObject timer)/*-{
        if(timer){
            clearInterval(timer);
        }
    }-*/;


}
