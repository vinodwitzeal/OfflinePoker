package bigcash.poker.network;

import com.badlogic.gdx.Gdx;

import bigcash.poker.utils.GeolocationPosition;

public class LocationHandler {
    private GeolocationPosition position;
    public LocationHandler(){
        position=null;
    }

    public void setSuccess(final GeolocationPosition position){
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                onSuccess(position);
            }
        });
    }

    public void setFailed(){
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                onFailed();
            }
        });
    }
    public void onSuccess(GeolocationPosition position){};
    public void onFailed(){};
}
