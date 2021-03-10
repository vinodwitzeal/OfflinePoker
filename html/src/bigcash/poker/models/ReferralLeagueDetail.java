package bigcash.poker.models;

import com.badlogic.gdx.utils.TimeUtils;

public class ReferralLeagueDetail {

    private int referralWinningAmount;
    private long referralLeaderboardRemainingTime;
    private long referralTotalParticipant, lastUpdatedTime;
    private boolean referralMonthlyContestStatus;

    public int getReferralWinningAmount() {
        return referralWinningAmount;
    }

    public void setReferralWinningAmount(int referralWinningAmount) {
        this.referralWinningAmount = referralWinningAmount;
    }

    public long getReferralLeaderboardRemainingTime() {
        return referralLeaderboardRemainingTime;
    }

    public void setReferralLeaderboardRemainingTime(long referralLeaderboardRemainingTime) {
        this.referralLeaderboardRemainingTime = referralLeaderboardRemainingTime;
        setLastUpdatedTime(TimeUtils.millis());
    }

    public long getReferralTotalParticipant() {
        return referralTotalParticipant;
    }

    public void setReferralTotalParticipant(long referralTotalParticipant) {
        this.referralTotalParticipant = referralTotalParticipant;
    }

    public long getLastUpdatedTime() {
        return lastUpdatedTime;
    }

    public void setLastUpdatedTime(long lastUpdatedTime) {
        this.lastUpdatedTime = lastUpdatedTime;
    }

    public boolean isReferralMonthlyContestStatus() {
        return referralMonthlyContestStatus;
    }

    public void setReferralMonthlyContestStatus(boolean referralMonthlyContestStatus) {
        this.referralMonthlyContestStatus = referralMonthlyContestStatus;
    }
}
