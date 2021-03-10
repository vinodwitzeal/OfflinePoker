package bigcash.poker.models;

public class CasualPlayerDto {
    private String userId;
    private String userName;
    private String userImageUrl;
    private int userRank;
    private float winningAmount;
    private boolean myDetail;


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserImageUrl() {
        return userImageUrl;
    }

    public void setUserImageUrl(String userImageUrl) {
        this.userImageUrl = userImageUrl;
    }

    public int getUserRank() {
        return userRank;
    }

    public void setUserRank(int userRank) {
        this.userRank = userRank;
    }

    public float getWinningAmount() {
        return (int)winningAmount;
    }

    public void setWinningAmount(float winningAmount) {
        this.winningAmount = winningAmount;
    }

    public boolean isMyDetail() {
        return myDetail;
    }

    public void setMyDetail(boolean myDetail) {
        this.myDetail = myDetail;
    }
}
