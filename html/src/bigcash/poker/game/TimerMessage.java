package bigcash.poker.game;

import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.concurrent.TimeUnit;

public class TimerMessage {
    public long leftSeconds;
    public long receivedTime;

    public TimerMessage(JsonValue jsonValue){
        leftSeconds= TimeUnit.SECONDS.toMillis(jsonValue.getInt("secondsLeft"));
        receivedTime= TimeUtils.millis();
    }

    public TimerMessage(int leftSeconds){
        this.leftSeconds=TimeUnit.SECONDS.toMillis(leftSeconds);
        receivedTime= TimeUtils.millis();
    }

    public long elapsedTime(){
        return TimeUtils.millis()-receivedTime;
    }

    public long remainingTime(){
        long remainingTime=leftSeconds-elapsedTime();
        if (remainingTime>0){
            return remainingTime;
        }

        return 0;
    }
}
