package bigcash.poker.models;

/**
 * Created by harshit on 2/20/2018.
 */

public class PrizeRules {

    private int minRank, maxRank, prize;
    private String rules, prizeType;

    public int getMinRank() {
        return minRank;
    }

    public void setMinRank(int minRank) {
        this.minRank = minRank;
    }

    public int getMaxRank() {
        return maxRank;
    }

    public void setMaxRank(int maxRank) {
        this.maxRank = maxRank;
    }

    public int getPrize() {
        return prize;
    }

    public void setPrize(int prize) {
        this.prize = prize;
    }

    public String getRules() {
        return rules;
    }

    public void setRules(String rules) {
        this.rules = rules;
    }

    public String getPrizeType() {
        return prizeType;
    }

    public void setPrizeType(String prizeType) {
        this.prizeType = prizeType;
    }
}
