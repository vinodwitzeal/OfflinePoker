package bigcash.poker.models;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import bigcash.poker.utils.GamePreferences;
import bigcash.poker.utils.PokerUtils;

public class UserProfile {
    private String userId, name, imageId, emailId, msisdn, paytmLinkedNo;
    private String imageUrl, paytmWalletText, alertMessage, pendingBonusPolicy;
    private float paytmBalance, paytmAccountBalance, paytmPostPaidBalance, totalCashAdded;
    private float pokerBalance, pendingBonus, withdrawable, deposited;
    private int life, totalFBFriends;
    private boolean paytmAccountLinked, paytmPostPaidActive, cashGameLocked, msisdnVerified, emailVerified, panCardVerified;
    private boolean witzealMobileVerification;
    private List<CashBackDto> cashBackDtos;
    public CashBackDto cashBackDto;
    private String panNumber, panStatus, upiType, redeemThreshold, status, redeemErrorMessage, dob, fullName, panNumberConfig;
    private Array<FriendsDto> friendsDtos;
    private ReferralLeagueDetail referralLeagueDetail;
    public float casualWinnings;


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        Gdx.app.error("Image Id",imageId);
        this.imageId = imageId;
    }

    public String getImageUrl() {
        if (imageId == null || imageId.isEmpty()) {
            return "";
        } else {
            return "https://graph.facebook.com/" + imageId + "/picture?type=normal";
        }
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public float getPaytmBalance() {
        return paytmBalance;
    }

    public void setPaytmBalance(float paytmBalance) {
        this.paytmBalance = paytmBalance;
    }

    public float getPokerBalance() {
        return pokerBalance;
    }

    public void setPokerBalance(float pokerBalance) {
        this.pokerBalance = pokerBalance;
    }

    public int getLife() {
        return life;
    }

    public void setLife(int life) {
        this.life = life;
    }

    public boolean isPaytmAccountLinked() {
        return paytmAccountLinked;
    }

    public void setPaytmAccountLinked(boolean paytmAccountLinked) {
        this.paytmAccountLinked = paytmAccountLinked;
    }

    public float getPaytmAccountBalance() {
        return paytmAccountBalance;
    }

    public void setPaytmAccountBalance(float paytmAccountBalance) {
        this.paytmAccountBalance = paytmAccountBalance;
    }

    public boolean isPaytmPostPaidActive() {
        return paytmPostPaidActive;
    }

    public void setPaytmPostPaidActive(boolean paytmPostPaidActive) {
        this.paytmPostPaidActive = paytmPostPaidActive;
    }

    public float getPaytmPostPaidBalance() {
        return paytmPostPaidBalance;
    }

    public void setPaytmPostPaidBalance(float paytmPostPaidBalance) {
        this.paytmPostPaidBalance = paytmPostPaidBalance;
    }

    public String getPaytmLinkedNo() {
        return paytmLinkedNo;
    }

    public void setPaytmLinkedNo(String paytmLinkedNo) {
        this.paytmLinkedNo = paytmLinkedNo;
    }

    public String getPaytmWalletText(float price) {
        String text = "";
        if (isPaytmAccountLinked()) {
            if (price == 0 || price <= getPaytmAccountBalance()) {
                float twoDigitsF = PokerUtils.getValue(getPaytmAccountBalance());
                text = "(\u20b9" + twoDigitsF + ")";
            } else if (price <= (getPaytmAccountBalance() + getPaytmPostPaidBalance())) {
                text = "(Wallet:\u20b9";
                float twoDigitsF = PokerUtils.getValue(getPaytmAccountBalance());
                text = text + twoDigitsF;
                if (isPaytmPostPaidActive()) {
                    twoDigitsF = PokerUtils.getValue(getPaytmPostPaidBalance());
                    text = text + " + Postpaid:\u20b9" + twoDigitsF + ")";
                } else {
                    text = text + ")";
                }
            } else {
                float twoDigitsF = PokerUtils.getValue(getPaytmAccountBalance());
                text = "(\u20b9" + twoDigitsF + ")";
            }
        }

        return text;
    }

    public void setPaytmWalletText(String paytmWalletText) {
        this.paytmWalletText = paytmWalletText;
    }

    public String getPaytmWalletText() {
        return paytmWalletText;
    }

    public List<CashBackDto> getCashBackDto() {
        return cashBackDtos;
    }

    public void setCashBackDto(List<CashBackDto> cashBackDtos) {
        if (cashBackDtos == null) {
            this.cashBackDtos = null;
            this.cashBackDto=null;
            return;
        }
        cashBackDtos.sort(new Comparator<CashBackDto>() {
            @Override
            public int compare(CashBackDto dto, CashBackDto t1) {
                return dto.getMinimumAmount() - t1.getMinimumAmount();
            }
        });

        this.cashBackDtos = new ArrayList<CashBackDto>();
        for (CashBackDto cashBackDto : cashBackDtos) {
            if (!cashBackDto.getCashBackType().matches("DAILY_SPIN")) {
                this.cashBackDtos.add(cashBackDto);
            }
        }
        if (this.cashBackDtos.size() > 0) {
            this.cashBackDto = this.cashBackDtos.get(0);
        }
    }

//    public Array<CashBackDto> getDailySpinCashBackDto() {
//        Array<CashBackDto> arrCashBackDto = new Array<CashBackDto>();
//        try {
//            if(TimeUtils.millis() - GamePreferences.instance().getDailySpinTimer() < getPrizeTimer()*1000) {
//                if (cashBackDtos != null && cashBackDtos.size() > 1) {
//                    for (CashBackDto cashBackDto : cashBackDtos) {
//                        if (cashBackDto.getCashBackType().matches("DAILY_SPIN") && cashBackDto.getOfferInPercentage() == GamePreferences.instance().getPrizeValue()) {
//                            arrCashBackDto.add(cashBackDto);
//                        }
//                    }
//                }
//            }
//        }catch (Exception e){
//
//        }
//        return arrCashBackDto;
//    }

    public long getPrizeTimer() {
        return GamePreferences.instance().getPrizeTimer();
    }

    public void setPrizeTimer(long prizeTimer) {
        GamePreferences.instance().setPrizeTimer(prizeTimer);
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public boolean isMsisdnVerified() {
        return msisdnVerified;
    }

    public void setMsisdnVerified(boolean msisdnVerified) {
        this.msisdnVerified = msisdnVerified;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getAlertMessage() {
        return alertMessage;
    }

    public void setAlertMessage(String alertMessage) {
        this.alertMessage = alertMessage;
    }

    public boolean isCashGameLocked() {
        return cashGameLocked;
    }

    public void setCashGameLocked(boolean cashGameLocked) {
        this.cashGameLocked = cashGameLocked;
    }

    public String getPendingBonusPolicy() {
        return pendingBonusPolicy;
    }

    public void setPendingBonusPolicy(String pendingBonusPolicy) {
        this.pendingBonusPolicy = pendingBonusPolicy;
    }

    public float getPendingBonus() {
        return pendingBonus;
    }

    public void setPendingBonus(float pendingBonus) {
        this.pendingBonus = pendingBonus;
    }

    public float getWithdrawable() {
        return withdrawable;
    }

    public void setWithdrawable(float withdrawable) {
        this.withdrawable = withdrawable;
    }

    public float getDeposited() {
        return deposited;
    }

    public void setDeposited(float deposited) {
        this.deposited = deposited;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public boolean isPanCardVerified() {
        return panCardVerified;
    }

    public void setPanCardVerified(boolean panCardVerified) {
        this.panCardVerified = panCardVerified;
    }

    public String getPanNumber() {
        return panNumber;
    }

    public void setPanNumber(String panNumber) {
        this.panNumber = panNumber;
    }

    public String getPanStatus() {
        return panStatus;
    }

    public void setPanStatus(String panStatus) {
        this.panStatus = panStatus;
    }

    public Array<FriendsDto> getFriendsDtos() {
        return friendsDtos;
    }

    public void setFriendsDtos(Array<FriendsDto> friendsDtos) {
        this.friendsDtos = friendsDtos;
    }

    public String getUpiType() {
        return upiType;
    }

    public void setUpiType(String upiType) {
        this.upiType = upiType;
    }

    public boolean isWitzealMobileVerification() {
        return witzealMobileVerification;
    }

    public void setWitzealMobileVerification(boolean witzealMobileVerification) {
        this.witzealMobileVerification = witzealMobileVerification;
    }

    public float getTotalCashAdded() {
        return totalCashAdded;
    }

    public void setTotalCashAdded(float totalCashAdded) {
        this.totalCashAdded = totalCashAdded;
    }

    public int getTotalFBFriends() {
        return totalFBFriends;
    }

    public void setTotalFBFriends(int totalFBFriends) {
        this.totalFBFriends = totalFBFriends;
    }

    public int getRedeemThreshold() {
        try {
            return Integer.parseInt(decryptPreferenceValue(redeemThreshold));
        } catch (Exception e) {
            return 100;
        }
    }

    public void setRedeemThreshold(String redeemThreshold) {
        this.redeemThreshold = redeemThreshold;
    }

    public ReferralLeagueDetail getReferralLeagueDetail() {
        return referralLeagueDetail;
    }

    public void setReferralLeagueDetail(ReferralLeagueDetail referralLeagueDetail) {
        this.referralLeagueDetail = referralLeagueDetail;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRedeemErrorMessage() {
        return redeemErrorMessage;
    }

    public void setRedeemErrorMessage(String redeemErrorMessage) {
        this.redeemErrorMessage = redeemErrorMessage;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPanNumberConfig() {
        return panNumberConfig;
    }

    public void setPanNumberConfig(String panNumberConfig) {
        this.panNumberConfig = panNumberConfig;
    }

    public void setCasualWinnings(float casualWinnings) {
        this.casualWinnings = casualWinnings;
    }

    public float getCasualWinnings() {
        return this.casualWinnings;
    }


    private String decryptPreferenceValue(String value) {
        String otp = "";
        try {
            otp = PokerUtils.decrypt(value, "wit@" + getUserId());
        } catch (Exception e) {

        }
        return otp;
    }

    private String encryptPreferenceValue(String value) {
        String otp = "";
        try {
            otp = PokerUtils.encrypt(value, "wit@" + getUserId());
        } catch (Exception e) {

        }
        return otp;
    }
}
