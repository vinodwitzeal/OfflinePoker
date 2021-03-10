package bigcash.poker.models;
public class PaytmOtpDetail {
    private int otpLocation;
    private String message;
    private String state;

    public int getOtpLocation() {
        return otpLocation;
    }

    public void setOtpLocation(int otpLocation) {
        this.otpLocation = otpLocation;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}