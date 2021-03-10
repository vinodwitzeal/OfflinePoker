package bigcash.poker.models;

public class CashBackDto {
    private String cashBackId;
    private String description;
    private int minimumAmount;
    private int maximumCashBack;
    private int offerInPercentage;
    private String cashBackType;
    private long remainingTime, expiryTime;

    public String getCashBackId() {
        return cashBackId;
    }

    public void setCashBackId(String cashBackId) {
        this.cashBackId = cashBackId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getMinimumAmount() {
        return minimumAmount;
    }

    public void setMinimumAmount(int minimumAmount) {
        this.minimumAmount = minimumAmount;
    }

    public int getMaximumCashBack() {
        return maximumCashBack;
    }

    public void setMaximumCashBack(int maximumCashBack) {
        this.maximumCashBack = maximumCashBack;
    }

    public int getOfferInPercentage() {
        return offerInPercentage;
    }

    public void setOfferInPercentage(int offerInPercentage) {
        this.offerInPercentage = offerInPercentage;
    }

    public String getCashBackType() {
        return cashBackType;
    }

    public void setCashBackType(String cashBackType) {
        this.cashBackType = cashBackType;
    }

    public long getRemainingTime() {
        return remainingTime;
    }

    public void setRemainingTime(long remainingTime) {
        this.remainingTime = remainingTime;
    }

    public long getExpiryTime() {
        return expiryTime;
    }

    public void setExpiryTime(long expiryTime) {
        this.expiryTime = expiryTime;
    }
}
