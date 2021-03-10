package bigcash.poker.models;

import bigcash.poker.utils.PokerUtils;

/**
 * Created by harshit on 10/3/2017.
 */

public class AccountSummary {
    private String offerName, amount, createdTime;
    private long id;

    public String getOfferName() {
        return offerName;
    }

    public void setOfferName(String offerName) {
        this.offerName = offerName;
    }

    public String getAmount() {
        return PokerUtils.getValue(Float.parseFloat(amount))+"";
    }


    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}


