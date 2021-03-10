package bigcash.poker.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.backends.gwt.GwtFiles;

public class GamePreferences {
    private static GamePreferences instance;

    public static GamePreferences instance(){
        if (instance==null){
            instance=new GamePreferences();
        }

        return instance;
    }

    private Preferences preferences;
    private GamePreferences(){
        preferences= Gdx.app.getPreferences("poker-cash");
    }

    public void setUserId(String userId){
        preferences.putString("userId",userId);
        preferences.flush();
    }

    public String getUserId(){
        return preferences.getString("userId","");
    }

    public void setOtp(String otp){
        preferences.putString("otp",otp);
        preferences.flush();
    }

    public String getOtp(){
        return preferences.getString("otp");
    }

    public void setFBToken(String token){
        preferences.putString("token",token);
        preferences.flush();
    }

    public String getFBToken(){
        return preferences.getString("token");
    }

    public void setAppVersion(int appVersion){
        preferences.putInteger("appVersion",appVersion);
        preferences.flush();
    }

    public int getAppVersion(){
        return preferences.getInteger("appVersion",0);
    }

    public long getConfigUpdateTime() {
        return preferences.getLong("configUpdateTime", 0);
    }

    public void setConfigUpdateTime(long configUpdateTime) {
        preferences.putLong("configUpdateTime", configUpdateTime);
        preferences.flush();
    }

    public boolean isShowPaytmLink() {
        return preferences.getBoolean("paytmLinked",false);
    }

    public void setShowPaytmLink(boolean flag) {
        preferences.putBoolean("paytmLinked",flag);
        preferences.flush();
    }

    public String getPaytmLinkDescription() {
        return preferences.getString("paytmLinkDescription","Link your Paytm account and get \u20b9 10 joining bonus.");
    }

    public void setPaytmLinkDescription(String paytmLinkDescription) {
        preferences.putString("paytmLinkDescription", paytmLinkDescription);
        preferences.flush();
    }

    public String getGenuineAndNew() {
        return decryptPreferenceValue(preferences.getString("genuineAndNew", ""));
    }

    public void setGenuineAndNew(String isGenuineAndNew) {

        preferences.putString("genuineAndNew", encryptPreferenceValue(isGenuineAndNew));
        preferences.flush();
    }

    public String getFirstAddCashCode() {
        return decryptPreferenceValue(preferences.getString("firstAddCashCode", ""));
    }

    public void setFirstAddCashCode(String firstAddCashReferralCode) {
        preferences.putString("firstAddCashCode", encryptPreferenceValue(firstAddCashReferralCode));
        preferences.flush();
    }

    public String getFirstAddCashPopupText() {
        return decryptPreferenceValue(preferences.getString("addCashDialogText", ""));
    }

    public void setFirstAddCashPopupText(String firstAddCashPopupText) {
        preferences.putString("addCashDialogText",encryptPreferenceValue(firstAddCashPopupText));
        preferences.flush();
    }

    public String getInviteText() {
        return decryptPreferenceValue(preferences.getString("inviteText",""));
    }

    public void setInviteText(String inviteText) {
        preferences.putString("inviteText",inviteText);
        preferences.flush();
    }

    public void setFBInviteText(String fbInviteText){
        preferences.putString("fbInviteText",fbInviteText);
        preferences.flush();
    }

    public String getFBInviteText(){
        return preferences.getString("fbInviteText","");
    }

    public void setDisplayInviteText(String displayInviteText){
        preferences.putString("displayInviteText",displayInviteText);
        preferences.flush();
    }

    public String getDisplayInviteText(){
        return preferences.getString("displayInviteText","");
    }

    public void setPaytmInviteText(String paytmInviteText){
        preferences.putString("paytmInviteText",paytmInviteText);
        preferences.flush();
    }

    public String getPaytmInviteText(){
        return preferences.getString("paytmInviteText","");
    }

    public void setEmojiString(String emojiString){
        preferences.putString("emojiString",emojiString);
        preferences.flush();
    }

    public String getEmojiString(){
        return preferences.getString("emojiString","");
    }

    public long getDailySpinTimer() {
        return preferences.getLong("daily_spin_time", 0);
    }

    public void setDailySpinTimer(long selif) {
        preferences.putLong("daily_spin_time", selif);
        preferences.flush();
    }

    public long getPrizeTimer() {
        return preferences.getLong("prizeTimer", 500);
    }

    public void setPrizeTimer(long selif) {
        preferences.putLong("prizeTimer", selif);
        preferences.flush();
    }

    public int getPrizeValue() {
        return preferences.getInteger("daily_spin_value", 0);
    }

    public void setPrizeValue(int selif) {
        preferences.putInteger("daily_spin_value", selif);
        preferences.flush();
    }

    public void setEmailLogin(boolean emailLogin){
        preferences.putBoolean("emailLogin",emailLogin);
        preferences.flush();
    }

    public boolean isEmailLogin(){
        return preferences.getBoolean("emailLogin",false);
    }

    public void setFacebookLogin(boolean facebookLogin){
        preferences.putBoolean("facebookLogin",facebookLogin);
        preferences.flush();
    }

    public boolean isFacebookLogin(){
        return preferences.getBoolean("facebookLogin",false);
    }

    public void setBalanceIncreased(boolean balanceIncreased){
        preferences.putBoolean("balancedIncreased",balanceIncreased);
        preferences.flush();
    }

    public boolean isBalanceIncreased(){
        return preferences.getBoolean("balanceIncreased");
    }

    public String getPokerIp() {
        return preferences.getString("pokerIP", "");
    }

    public void setPokerIp(String pokerIP) {
        preferences.putString("pokerIP", pokerIP);
        preferences.flush();
    }

    public String getPokerKey() {
        return preferences.getString("pokerKey", "");
    }

    public void setPokerKey(String pokerKey) {
        preferences.putString("pokerKey", pokerKey);
        preferences.flush();
    }

    public String getRedeemValues() {
        return preferences.getString("redeemValues","");
    }

    public void setRedeemValues(String redeemValues) {
        preferences.putString("redeemValues",redeemValues);
        preferences.flush();
    }

    public boolean getUseJenkinsTimeEnable() {
        return preferences.getBoolean("jenkinsEnabled", false);
    }

    public void setUseJenkinsTimeEnable(boolean useJenkinsTimeEnable) {
        preferences.putBoolean("jenkinsEnabled", useJenkinsTimeEnable);
        preferences.flush();
    }

    public boolean getGameSoundStatus() {
        return preferences.getBoolean("gameSound", true);
    }

    public void setGameSoundStatus(boolean status) {
        preferences.putBoolean("gameSound", status);
        preferences.flush();
    }

    public boolean getNotificationSound() {
        return preferences.getBoolean("notificationSound", true);
    }

    public void setNotificationSound(boolean status) {
        preferences.putBoolean("notificationSound", status);
        preferences.flush();
    }

    public boolean getGameVibration() {
        return preferences.getBoolean("gameVibration", true);
    }

    public void setGameVibration(boolean status) {
        preferences.putBoolean("gameVibration", status);
        preferences.flush();
    }

    public void setAutoRefillButtonChecked(boolean checked){
        preferences.putBoolean("autoRefillChecked",checked);
        preferences.flush();
    }

    public boolean isAutoRefillButtonChecked(){
        return preferences.getBoolean("autoRefillChecked",true);
    }

    public void setReferralContestRules(String rules){
        preferences.putString("referralContestRules",rules);
        preferences.flush();
    }

    public String getReferralContestRules(){
        return preferences.getString("referralContestRules","");
    }

    public void clear(){
        preferences.clear();
        GwtFiles.LocalStorage.clear();
    }

    private String encryptPreferenceValue(String value) {
        String otp = "";
        try {
            otp = PokerUtils.encrypt(value, "wit@" + getUserId());
        } catch (Exception e) {

        }
        return otp;
    }



    private String decryptPreferenceValue(String value) {
        String otp = "";
        try {
            otp = PokerUtils.decrypt(value, "wit@" + getUserId());
        } catch (Exception e) {

        }
        return otp;
    }



}
