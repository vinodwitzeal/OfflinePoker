package bigcash.poker.network;

/**
 * Created by harshit on 12/19/2016.
 */
public class Response {
    private int errorCode;
    private String result;

    public Response(){
    }

    public Response(String result, int errorCode){
        this.result = result;
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

}
