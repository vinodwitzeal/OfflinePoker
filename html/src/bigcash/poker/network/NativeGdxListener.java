package bigcash.poker.network;

public abstract class NativeGdxListener<T> extends GdxListener<T>{
    @Override
    public void setSuccess(T t) {
        onSuccess(t);
    }

    @Override
    public void setFail(String reason) {
        onFail(reason);
    }

    @Override
    public void setError(String error) {
        onError(error);
    }

}
