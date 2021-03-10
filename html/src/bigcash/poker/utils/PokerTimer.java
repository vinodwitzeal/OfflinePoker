package bigcash.poker.utils;

import com.google.gwt.core.client.JavaScriptObject;

public class PokerTimer {
    private JavaScriptObject timer;
    private long totalTime;
    private int timeStep;
    private PokerTimerUpdater timerUpdater;
    public PokerTimer(long totalTime,int timeStep,PokerTimerUpdater timerUpdater){
        this.totalTime=totalTime;
        this.timeStep=timeStep;
        this.timerUpdater=timerUpdater;
        this.timerUpdater.leftTime=totalTime;
        this.timerUpdater.timeStep=timeStep;
        this.timerUpdater.timer=this;
    }


    public void start(){
        timer=startTimer(timeStep,timerUpdater);
    }

    private final native JavaScriptObject startTimer(int timeStep,PokerTimerUpdater updater)/*-{
        return setInterval(function(){
            updater.@bigcash.poker.utils.PokerTimer.PokerTimerUpdater::onInterval()();
        },timeStep);
    }-*/;

    public void cancel(){
        cancelTimer(timer);
    }

    private final native void cancelTimer(JavaScriptObject timer)/*-{
        if(timer){
            clearInterval(timer);
        }
    }-*/;







    public static abstract class PokerTimerUpdater{
        private PokerTimer timer;
        private long leftTime,timeStep;
        public abstract void onTick(long millis);
        public abstract void onFinish();

        public void onInterval(){
            leftTime=leftTime-timeStep;
            if (leftTime<=0){
                timer.cancelTimer(timer.timer);
                onFinish();
            }else {
                onTick(leftTime);
            }
        }
    }
}
