package bigcash.poker.network;

import com.badlogic.gdx.Gdx;

public abstract class GdxListener<T> {

    public void setProcessing(){

    }

    public void setSuccess(final T t){
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                onSuccess(t);
            }
        });
    }
    public void setFail(final String reason){
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                onFail(reason);
            }
        });
    }

    public void setError(final String error){
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                onError(error);
            }
        });
    }

    public abstract void onSuccess(T t);
    public abstract void onFail(String reason);
    public abstract void  onError(String  error);
}
