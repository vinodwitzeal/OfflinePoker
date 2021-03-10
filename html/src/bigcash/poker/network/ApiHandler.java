package bigcash.poker.network;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.HttpRequestBuilder;
import com.badlogic.gdx.net.HttpRequestHeader;
import com.badlogic.gdx.net.HttpStatus;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter;
import com.badlogic.gdx.utils.TimeUtils;
import com.google.gwt.core.client.GWT;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bigcash.poker.constants.Constant;
import bigcash.poker.dialogs.MaintenanceDialog;
import bigcash.poker.dialogs.ProcessDialog;
import bigcash.poker.gwt.PokerGame;
import bigcash.poker.models.AccountSummary;
import bigcash.poker.models.CashBackDto;
import bigcash.poker.models.CasualLeague;
import bigcash.poker.models.CasualPlayerDto;
import bigcash.poker.models.FriendsDto;
import bigcash.poker.models.InitialValue;
import bigcash.poker.models.PaytmOtpDetail;
import bigcash.poker.models.PrizeRules;
import bigcash.poker.models.ReferralLeague;
import bigcash.poker.models.ReferralLeagueDetail;
import bigcash.poker.models.ReferralPlayerDto;
import bigcash.poker.models.UIScreen;
import bigcash.poker.models.UserProfile;
import bigcash.poker.screens.PokerContestScreen;
import bigcash.poker.utils.GamePreferences;
import bigcash.poker.utils.JsonParseKeys;
import bigcash.poker.utils.PokerUtils;

public class ApiHandler {
    public static final String BASE_URL = "https://test1.bigcash.live/bulbsmashpro/api";
//        public static final String BASE_URL = "https://api.bigcash.live/bulbsmashpro/api";
    public static final String API_VERSION_1 = "/v1";
    public static final String API_VERSION_2 = "/v2";
    public static final String API_VERSION_3 = "/v3";
    public static final String API_VERSION_4 = "/v4";
    public static final String API_VERSION_5 = "/v5";
    public static final String API_VERSION_8 = "/v8";
    public static final String API_VERSION_9 = "/v9";
    public static final String API_VERSION_10 = "/v10";

    public static final String FB_LOGIN_API = "/user/fbLogin";
    public static final String EMAIL_LOGIN_API = "/user/emailLogin";
    public static final String FORGET_PASSWORD_API = "/user/forgotPassword";
    public static final String APP_LAUNCH_API = "/user/appLaunch";
    public static final String PAYTM_GENERATE_CHECK_SUM_API = "/paytm/autoDebit/generateCheckSum";
    public static final String PROFILE_API = "/user/profile";
    public static final String TRUE_CALLER_API = "/user/profile/truecaller/verifyMsisdnStatus";
    public static final String ADD_CASH_API = "/addCash";
    public static final String PAYTM_SEND_OTP_API = "/paytm/autoDebit/sendOtp";
    public static final String PAYTM_VALIDATE_OTP_API = "/paytm/autoDebit/validateOtp";
    public static final String PAYTM_AUTO_DEBIT_UNLINK_API = "/paytm/autoDebit/unlink";
    public static final String EMAIL_REGISTER_API = "/user/emailRegister";
    public static final String CURRENT_TIME_API = "/user/currenTime";
    public static final String RAZOR_PAY = "/razorpay/generateOrderId";
    public static final String EVENT_LOG_API = "/event/log";
    public static final String REFERRAL_LEADER_BOARD_API = "/referral/leaderBoard";
    public static final String REDEEM_CONFIG_API = "/redeem/config";
    public static final String REDEEM_API = "/redeem/payTm";
    public static final String OTP_API = "/user/profile/witzeal/otp";
    public static final String WITZEAL_MSISDN_API = "/user/profile/witzeal/verifyMsisdn";
    public static final String ACCOUNT_SUMMARY_API = "/user/account/summary";
    public static final String SETTING_API = "/user/setting";
    public static final String POLICY_API = "/policy";
    public static final String UPLOAD_FILE_API = "/user/uploadPan/";
    public static final String CASUAL_GAMES_LEADER_BOARD_API = "/casualGames/leaderBoard";
    public static final String FEEDBACK_API = "/feedback";

    public static final String NETWORK_ISSUE = "Please check your internet connection.";
    public static final String SERVER_BUSY = "Server Busy..";
    public static final String UNDER_MAINTENANCE = "Under Maintenance..";
    public static final String DATA_NOT_AVAILABLE = "Data not available.";
    public static final String TRY_AGAIN = "Try again..";
    public static final String FOREIGN_NUMBER_VERIFY = "Please verify INDIAN mobile number.";
    public static final String NUMBER_VERIFIED_MESSAGE = "Number verified successfully.";

    public static String FAQ = "http://bigcash.live/faqs.html";
    public static final String PRIVACY_POLICY = "http://bigcash.live/policy/";
    public static String TERMSANDCONDITION = "http://bigcash.live/terms.html";
    public static final String GAME_RULES = "http://bigcash.live/gamerules/";
    public static final String FACEBOOK_PAGE = "https://www.facebook.com/Bulb-Smash-Cash-131587280893139/?modal=composer";


    public static String parseToGetZipData(String response) {
        JsonValue object = new JsonReader().parse(response);
        if (Constant.userProfile != null) {
            Constant.userProfile.setPaytmBalance(object.getFloat("payTmBalance", Constant.userProfile.getPaytmBalance()));
            Constant.userProfile.setLife(object.getInt("life", Constant.userProfile.getLife()));
        }
        String result = object.getString("response", "");
        return result;
    }

    public static void callEmailRegisterApi(String emailId, String name, String password, final String referralCode, final GdxListener<String> listener) {

        HashMap<String, String> params = new HashMap();
        params.put("emailId", emailId);
        params.put("deviceType", Constant.getDeviceType());
        params.put("appVersion", Constant.getVersionCode());
        params.put("deviceId", Constant.getDeviceId());
        params.put("androidId", Constant.getAndroidId());
        //  params.put("advertisingId", proGame.config.getAdvertisingID());
        params.put("androidVersion", Constant.getAndroidVersion());
        params.put("networkOperator", Constant.getNetworkOperator());
        params.put("isRooted", "false");
        params.put("name", name);
        params.put("password", password);
        params.put("referralCode", referralCode);
        params.put("aName", Constant.getAppName());
        params.put("pName", Constant.getPackageName());
        params.put("kH", Constant.getKeyHash());
        params.put("p", Constant.getAbsolutePath());
        params.put("cp", Constant.getCanonicalPath());

        params.put("sId", PokerUtils.encrypt(Constant.getVersionCode(), "bsplay@"));
        params.put("dn", PokerUtils.encrypt(Constant.getVersionCode(), "bsplay@"));
        params.put("rbs", false + "");


        HttpRequestBuilder requestBuilder = new HttpRequestBuilder();
        Net.HttpRequest httpPost = requestBuilder.newRequest()
                .method(Net.HttpMethods.POST)
                .timeout(10000)
                .formEncodedContent(params)
                .url(BASE_URL + API_VERSION_5 + EMAIL_REGISTER_API)
                .build();

        Gdx.net.sendHttpRequest(httpPost, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                int status = httpResponse.getStatus().getStatusCode();
                String resultString = httpResponse.getResultAsString();

                if (errorCodeHandlingForSplashScreen(status)) {
                    parseEmailRegistrationResponse(httpResponse.getResultAsString());
                    listener.setSuccess("Success");
                } else {
                    if (status == 226)
                        listener.setError(resultString);
                    if (status == 406)
                        listener.setError(status + "," + resultString);
                }
            }

            @Override
            public void failed(Throwable t) {
                listener.setFail(t.getLocalizedMessage());
            }

            @Override
            public void cancelled() {
                listener.setError("Cancelled");
            }
        });
    }


    public static void callEmailLoginApi(String emailId, String password, final GdxListener<String> listener) {
        HashMap<String, String> params = new HashMap();
        params.put("emailId", emailId);
        params.put("deviceType", Constant.getDeviceType());
        params.put("appVersion", Constant.getVersionCode());
        params.put("deviceId", Constant.getDeviceId());
        params.put("isRooted", "false");
        params.put("password", password);
        params.put("aName", Constant.getAppName());
        params.put("pName", Constant.getPackageName());
        params.put("kH", Constant.getKeyHash());
        params.put("p", Constant.getAbsolutePath());
        params.put("cp", Constant.getCanonicalPath());
        params.put("rbs", "false");

        HttpRequestBuilder requestBuilder = new HttpRequestBuilder();
        Net.HttpRequest httpPost = requestBuilder.newRequest()
                .method(Net.HttpMethods.POST)
                .timeout(10000)
                .formEncodedContent(params)
                .url(BASE_URL + API_VERSION_5 + EMAIL_LOGIN_API)
                .build();

        Gdx.app.error("Email-Login","Request");
//
        Gdx.net.sendHttpRequest(httpPost, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                int status = httpResponse.getStatus().getStatusCode();
                String resultString = httpResponse.getResultAsString();
                if (errorCodeHandling(status)) {
                    parseEmailRegistrationResponse(httpResponse.getResultAsString());
                    listener.setSuccess("Success");
                } else {
                    if (status == 226)
                        listener.setError(resultString);
                    if (status == 406)
                        listener.setError(status + "," + resultString);
                }
            }

            @Override
            public void failed(Throwable t) {
                listener.setFail(t.getLocalizedMessage());
            }

            @Override
            public void cancelled() {
                listener.setError("Cancelled");
            }
        });
    }

    public static void callFacebookLoginApi(String accessToken, GdxListener<String> listener) {
        HashMap<String, String> params = new HashMap();

        params.put("deviceType", Constant.getDeviceType());
        params.put("appVersion", Constant.getVersionCode());
        params.put("deviceId", Constant.getDeviceId());
        params.put("androidId", Constant.getAndroidId());
        params.put("androidVersion", Constant.getAndroidVersion());
        params.put("networkOperator", Constant.getNetworkOperator());
        //  params.put("advertisingId", proGame.config.getAdvertisingID());
        params.put("isRooted", false + "");
        params.put("accessToken", accessToken);
        params.put("referralCode", "");
        params.put("aName", Constant.getAppName());
        params.put("pName", Constant.getPackageName());
        params.put("kH", Constant.getKeyHash());
        params.put("p", Constant.getAbsolutePath());
        params.put("cp", Constant.getCanonicalPath());

        params.put("utmSource", "");
        params.put("utmMedium", "");
        params.put("utmContent", "");
        params.put("utmTerm", "");
        params.put("utmCampaign", "");
        params.put("downloadLink", "");
        String dn = Constant.getVersionCode() + "";
        String sId = Constant.getVersionCode() + "";
        params.put("rbs", false + "");
        try {
            params.put("sId", PokerUtils.encrypt(sId, "bsplay@"));
            params.put("dn", PokerUtils.encrypt(dn, "bsplay@"));
        } catch (Exception e) {

        }

        HttpRequestBuilder requestBuilder = new HttpRequestBuilder();
        Net.HttpRequest httpPost = requestBuilder.newRequest()
                .method(Net.HttpMethods.POST)
                .timeout(10000)
                .formEncodedContent(params)
                .url(BASE_URL + API_VERSION_5 + FB_LOGIN_API)
                .build();

        Gdx.net.sendHttpRequest(httpPost, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                Gdx.app.log("FBLoginResponse", httpResponse.getResultAsString());
                int status = httpResponse.getStatus().getStatusCode();
                String resultString = httpResponse.getResultAsString();
                if (errorCodeHandling(status)) {
                    parseEmailRegistrationResponse(httpResponse.getResultAsString());
                    listener.setSuccess("Success");
                } else {
                    if (status == 226)
                        listener.setError(resultString);
                    if (status == 406)
                        listener.setError(status + "," + resultString);
                }
            }

            @Override
            public void failed(Throwable t) {
                listener.setFail(t.getLocalizedMessage());
            }

            @Override
            public void cancelled() {
                listener.setError("Cancelled");
            }
        });
    }

    private static void parseEmailRegistrationResponse(String result) {
        try {
            GamePreferences preference = GamePreferences.instance();
            JsonValue jsonValue = new JsonReader().parse(result);
            String userId = jsonValue.getString(JsonParseKeys.USER_ID);
            String otp = jsonValue.getString(JsonParseKeys.OTP);
            otp = PokerUtils.decrypt(otp, "bsplay@");
            preference.setUserId(userId);
            preference.setOtp(otp);

            if (jsonValue.has(JsonParseKeys.IS_GENUINE_AND_NEW)) {
                preference.setGenuineAndNew(jsonValue.getString(JsonParseKeys.IS_GENUINE_AND_NEW));
                preference.setFirstAddCashCode(jsonValue.getString(JsonParseKeys.FIRST_ADD_CASH_REFERRAL_CODE));
                preference.setFirstAddCashPopupText(jsonValue.getString(JsonParseKeys.FIRST_ADD_CASH_POPUP_TEXT));
            } else {
                preference.setGenuineAndNew("false");
                preference.setFirstAddCashCode("");
                preference.setFirstAddCashPopupText("");
            }

            if (jsonValue.has(JsonParseKeys.SHOW_PAYTM_LINK)) {
                preference.setShowPaytmLink(jsonValue.getBoolean(JsonParseKeys.SHOW_PAYTM_LINK));
            }
            if (jsonValue.has(JsonParseKeys.PAYTM_LINK_DESCRIPTION)) {
                preference.setPaytmLinkDescription(jsonValue.getString(JsonParseKeys.PAYTM_LINK_DESCRIPTION));
            }
        } catch (Exception e) {
//            Gdx.app.log("Register error", "error");
        }
    }

    public static void callForgetPasswordApi(String emailId, GdxListener<String> listener) {
        HashMap<String, String> params = new HashMap();
        params.put("emailId", emailId);
        params.put("appVersion", Constant.getVersionCode());
        params.put("deviceId", Constant.getDeviceId());
        HttpRequestBuilder requestBuilder = new HttpRequestBuilder();
        Net.HttpRequest httpPost = requestBuilder.newRequest()
                .method(Net.HttpMethods.POST)
                .timeout(10000)
                .formEncodedContent(params)
                .url(BASE_URL + API_VERSION_5 + FORGET_PASSWORD_API)
                .build();

        Gdx.net.sendHttpRequest(httpPost, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                if (httpResponse.getStatus().getStatusCode() == 200) {
                    listener.setSuccess(httpResponse.getResultAsString());
                } else {
                    listener.setSuccess(httpResponse.getResultAsString());
                }
            }

            @Override
            public void failed(Throwable t) {
                listener.setFail(t.getMessage());
            }

            @Override
            public void cancelled() {
                listener.setFail("Cancelled");
            }
        });
    }

    public static void callAppLaunchApi(boolean isOpen, GdxListener<String> listener) {
        GamePreferences preference = GamePreferences.instance();
        HashMap<String, String> params = new HashMap<String, String>();
        String transactionId = TimeUtils.millis() + "";
        params.put("otp", PokerUtils.encryptValue(transactionId, preference.getOtp()));
        params.put("transactionId", transactionId + "");
        params.put("deviceType", Constant.getDeviceType());
        final int versionCode = Integer.parseInt(Constant.getVersionCode());
        params.put("appVersion", Constant.getVersionCode());
        params.put("deviceId", Constant.getDeviceId());
        params.put("androidVersion", Constant.getAndroidVersion());
        params.put("isOpen", isOpen + "");
        params.put("networkType", Constant.getNetworkOperator());
        params.put("isRooted", false + "");
        params.put("isHavingCloakingApps", false + "");
        params.put("multiWindowEnabled", false + "");
        boolean isUpdateDataBase = false;
        if (preference.getAppVersion() == 0 || preference.getAppVersion() < versionCode) {
            if (preference.getAppVersion() == 40) {
                isUpdateDataBase = true;
            }
            preference.setAppVersion(versionCode);
            preference.setConfigUpdateTime(0);
        }
        params.put("configUpdatedTime", preference.getConfigUpdateTime() + "");
        // Gdx.app.log("App Launch Response",proGame.config.getLatitude()+"-"+proGame.config.getLongitude());
        params.put("latitude", "");
        params.put("longitude", "");
        params.put("p", Constant.getAbsolutePath());
        params.put("cp", Constant.getCanonicalPath());
        params.put("parentActivity", "");
        params.put("rbs", false + "");
        params.put("sId", Constant.getVersionCode());

        HttpRequestBuilder requestBuilder = new HttpRequestBuilder();
        Net.HttpRequest httpPost = requestBuilder.newRequest()
                .method(Net.HttpMethods.POST)
                .timeout(10000)
                .formEncodedContent(params)
                .url(BASE_URL + API_VERSION_10 + APP_LAUNCH_API + "?userId=" + preference.getUserId())
                .build();

        Gdx.net.sendHttpRequest(httpPost, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                int status = httpResponse.getStatus().getStatusCode();
                String resultString = httpResponse.getResultAsString();
                if (errorCodeHandling(status)) {
                    String zipResult = parseToGetZipData(resultString);

                    String result = "";
                    try {
                        result = PokerUtils.decompress(zipResult);
                    } catch (Exception e) {

                    }
                    parseAppLaunchResponse(result, false);
                    listener.setSuccess("");
                } else {
                    if (status == 226)
                        listener.setError(resultString);
                    if (status == 406)
                        listener.setError(status + "," + resultString);
                }
            }

            @Override
            public void failed(Throwable t) {
                listener.setFail(t.getLocalizedMessage());
            }

            @Override
            public void cancelled() {
                listener.setError("Cancelled");

            }
        });
    }

    public static void parseAppLaunchResponse(String response, boolean isRefresh) {
        try {
            GamePreferences preference = GamePreferences.instance();
            JsonValue object = new JsonReader().parse(response);

            Constant.initialValue = new InitialValue();
            if (object.has(JsonParseKeys.CONFIG_DTO)) {
                parseConfigDto(preference, object.get(JsonParseKeys.CONFIG_DTO));
            }
            if (!isRefresh) {
                Constant.userProfile = new UserProfile();
            }
            Constant.userProfile.setUserId(preference.getUserId());
            if (object.has(JsonParseKeys.LIFE))
                Constant.userProfile.setLife(object.getInt(JsonParseKeys.LIFE));
            if (object.has(JsonParseKeys.PAYTM_BALANCE))
                Constant.userProfile.setPaytmBalance(object.getFloat(JsonParseKeys.PAYTM_BALANCE));
            if (object.has(JsonParseKeys.CASH_GAME_LOCKED)) {
                Constant.userProfile.setCashGameLocked(object.getBoolean(JsonParseKeys.CASH_GAME_LOCKED));
            }
            if (object.has(JsonParseKeys.PAYTM_LINK_DTO)) {
                parsePaytmLinkDto(object.get(JsonParseKeys.PAYTM_LINK_DTO));
            } else {
                Constant.userProfile.setPaytmAccountLinked(false);
            }
            if (object.has(JsonParseKeys.UPI_TYPE)) {
                Constant.userProfile.setUpiType(object.getString(JsonParseKeys.UPI_TYPE));
            }
            if (object.has(JsonParseKeys.IS_WITZEAL_MOBILE_VERIFICATION)) {
                Constant.userProfile.setWitzealMobileVerification(object.getBoolean(JsonParseKeys.IS_WITZEAL_MOBILE_VERIFICATION));
                //  Gdx.app.error("Mobile verification app launch ",Constant.userProfile.isWitzealMobileVerification()+"");
            }
//            if (object.has(JsonParseKeys.SHOW_HEAD_TO_HEAD_CASH)) {
//                Constant.userProfile.setShowHeadToHeadCash(object.getBoolean(JsonParseKeys.SHOW_HEAD_TO_HEAD_CASH));
//            }
//            if (object.has(JsonParseKeys.DEVICE_TOKEN_ACTIVE)) {
//                Constant.deviceTokenActive = object.getBoolean(JsonParseKeys.DEVICE_TOKEN_ACTIVE);
//                proGame.config.sendTokenToServer();
//            }
            if (object.has(JsonParseKeys.PAYTM_RULES))
                Constant.paytmRules = object.getString(JsonParseKeys.PAYTM_RULES);
            if (object.has(JsonParseKeys.IMAGE_URL))
                Constant.userProfile.setImageId(object.getString(JsonParseKeys.IMAGE_URL));
//            if (object.has(JsonParseKeys.LATEST_APK_CODE))
//                Constant.userProfile.setLatestApkVersionCode(object.getInt(JsonParseKeys.LATEST_APK_CODE));
            if (object.has(JsonParseKeys.NAME))
                Constant.userProfile.setName(object.getString(JsonParseKeys.NAME));
//            if (object.has(JsonParseKeys.DAILY_LEAGUE_REMAINING_TIME))
//                Constant.userProfile.setDailyLeagueRemainingTime(object.getInt(JsonParseKeys.DAILY_LEAGUE_REMAINING_TIME));
//            if (object.has(JsonParseKeys.DAILY_LEAGUE_ID))
//                Constant.userProfile.setDailyLeagueId(object.getInt(JsonParseKeys.DAILY_LEAGUE_ID));
//            if (object.has(JsonParseKeys.WEEKLY_LEAGUE_ID)) {
//                Constant.cricketCash = new CricketCash();
//                Constant.cricketCash.setWeeklyLeagueId(object.getInt(JsonParseKeys.WEEKLY_LEAGUE_ID));
//                Constant.cricketCash.setWeeklyLeagueRemainingTime(object.getInt(JsonParseKeys.WEEKLY_REMAINING_TIME));
//                if (object.has(JsonParseKeys.REMAINING_GAME))
//                    Constant.cricketCash.setRemainingGame(object.getInt(JsonParseKeys.REMAINING_GAME));
//                if (object.has(JsonParseKeys.MAX_PRIZE))
//                    Constant.cricketCash.setMaxPrize(object.getInt(JsonParseKeys.MAX_PRIZE));
//            }
            if (object.has(JsonParseKeys.PENDING_BONUS_BALANCE)) {
                Constant.userProfile.setPendingBonus(object.getFloat(JsonParseKeys.PENDING_BONUS_BALANCE));
            } else {
                Constant.userProfile.setPendingBonus(0);
            }
//            if (object.has(JsonParseKeys.CASH_LEAGUE_RANKING)) {
//                JsonValue jsonValue = object.get(JsonParseKeys.CASH_LEAGUE_RANKING);
//                Constant.userProfile.setCash(jsonValue.getFloat(JsonParseKeys.CASH));
//                Constant.userProfile.setCashLeagueRanking(jsonValue.getInt(JsonParseKeys.RANKING));
//                if (jsonValue.has(JsonParseKeys.PRIZE))
//                    Constant.userProfile.setCashLeaguePrize(jsonValue.getInt(JsonParseKeys.PRIZE));
//                else
//                    Constant.userProfile.setCashLeaguePrize(0);
//            }
            if (object.has(JsonParseKeys.EMAIL_ID))
                Constant.userProfile.setEmailId(object.getString(JsonParseKeys.EMAIL_ID));
            if (object.has(JsonParseKeys.MSISDN))
                Constant.userProfile.setMsisdn(object.getString(JsonParseKeys.MSISDN));
//            if (object.has(JsonParseKeys.CHIPS_LEAGUE_RANKING)) {
//                JsonValue jsonValue = object.get(JsonParseKeys.CHIPS_LEAGUE_RANKING);
//                Constant.userProfile.setChips(jsonValue.getInt(JsonParseKeys.CHIPS));
//                Constant.userProfile.setChipsLeagueRanking(jsonValue.getInt(JsonParseKeys.RANKING));
//                if (jsonValue.has(JsonParseKeys.GAME_PLAYED_IN_CHIPS_LEAGUE)) {
//                    Constant.userProfile.setGamePlayedInChipsLeague(jsonValue.getInt(JsonParseKeys.GAME_PLAYED_IN_CHIPS_LEAGUE));
//                }
//                if (jsonValue.has(JsonParseKeys.PRIZE))
//                    Constant.userProfile.setChipsLeaguePrize(jsonValue.getInt(JsonParseKeys.PRIZE));
//                else
//                    Constant.userProfile.setChipsLeaguePrize(0);
//            } else {
//                Constant.userProfile.setChips(0);
//                Constant.userProfile.setChipsLeagueRanking(0);
//            }
            if (object.has(JsonParseKeys.MIN_ADD_CASH_THRESHOLD)) {
                Constant.minAddCashThreshold = object.getInt(JsonParseKeys.MIN_ADD_CASH_THRESHOLD);
            }
            if (object.has(JsonParseKeys.TOTAL_CASH_ADDED)) {
                Constant.userProfile.setTotalCashAdded(object.getFloat(JsonParseKeys.TOTAL_CASH_ADDED));
            } else {

            }
//            if (object.has(JsonParseKeys.CHIPS_LEAGUE_ENTRY)) {
//                JsonValue jsonValue = object.get(JsonParseKeys.CHIPS_LEAGUE_ENTRY);
//                ChipsLeagueEntry chipsLeagueEntry = new ChipsLeagueEntry();
//                chipsLeagueEntry.setEntryFee(jsonValue.getFloat(JsonParseKeys.ENTRY_FEE));
//                chipsLeagueEntry.setDescription(jsonValue.getString(JsonParseKeys.DESCRIPTION));
//                chipsLeagueEntry.setOffer(jsonValue.getString(JsonParseKeys.OFFER));
//                if (jsonValue.has(JsonParseKeys.MAX_GAME_IN_CHIPS_LEAGUE)) {
//                    Constant.userProfile.setMaxGameInChipsLeague(jsonValue.getInt(JsonParseKeys.MAX_GAME_IN_CHIPS_LEAGUE));
//                }
//                Constant.chipsLeagueEntry = chipsLeagueEntry;
//            }
//
//            if (object.has(JsonParseKeys.cashGamePendingBonusRule)) {
//                String[] rule1 = object.getString(JsonParseKeys.cashGamePendingBonusRule).split("\\|");
//                Constant.mapPendingRule = new HashMap<String, Float>();
//                for (int i = 0; i < rule1.length; i++) {
//                    String[] rule2 = rule1[i].split(",");
//                    Constant.mapPendingRule.put(rule2[0], Float.parseFloat(rule2[1]));
//                }
//            }


            if (object.has(JsonParseKeys.TOTAL_FRIEND_IN_FB)) {
                Constant.userProfile.setTotalFBFriends(object.getInt(JsonParseKeys.TOTAL_FRIEND_IN_FB));
            } else {
                Constant.userProfile.setTotalFBFriends(0);
            }
//            if (object.has(JsonParseKeys.SWING_ON_TOP)) {
//                Constant.userProfile.setSwingOnTop(object.getBoolean(JsonParseKeys.SWING_ON_TOP));
//            } else {
//                Constant.userProfile.setSwingOnTop(false);
//            }
            if (object.has(JsonParseKeys.INITIAL_PARAMS)) {
                JsonValue jsonValue2 = object.get(JsonParseKeys.INITIAL_PARAMS);
                Constant.initialValue.setZero(jsonValue2.getString(JsonParseKeys.ZERO));
                Constant.initialValue.setOne(jsonValue2.getString(JsonParseKeys.ONE));
                Constant.initialValue.setTwo(jsonValue2.getString(JsonParseKeys.TWO));
                Constant.initialValue.setThree(jsonValue2.getString(JsonParseKeys.THREE));
                Constant.initialValue.setFour(jsonValue2.getString(JsonParseKeys.FOUR));
                Constant.initialValue.setFive(jsonValue2.getString(JsonParseKeys.FIVE));
                Constant.initialValue.setSix(jsonValue2.getString(JsonParseKeys.SIX));
                Constant.initialValue.setNine(jsonValue2.getString(JsonParseKeys.NINE));
                Constant.initialValue.setEight(jsonValue2.getString(JsonParseKeys.EIGHT));
                Constant.initialValue.setEightThousand(jsonValue2.getString(JsonParseKeys.EIGHT_THOUSAND));
                Constant.initialValue.setTenThousand(jsonValue2.getString(JsonParseKeys.TEN_THOUSAND));
                Constant.initialValue.setTwelve(jsonValue2.getString(JsonParseKeys.TWELVE));
                Constant.initialValue.setSixty(jsonValue2.getString(JsonParseKeys.SIXTY));
                Constant.initialValue.setTwoHundred(jsonValue2.getString(JsonParseKeys.TWO_HUNDRED));
                Constant.initialValue.setFourHundred(jsonValue2.getString(JsonParseKeys.FOUR_HUNDRED));
            }
//            if (object.has(JsonParseKeys.HOURLY_LEAGUE_REMAINING_TIME))
//                Constant.userProfile.setHourlyLeagueRemainingTime(object.getLong(JsonParseKeys.HOURLY_LEAGUE_REMAINING_TIME));
//            else
//                Constant.userProfile.setHourlyLeagueRemainingTime(1400000);


//            if (object.has(JsonParseKeys.LUCKY_WHEEL_DTOS)) {
//                JsonValue jsonValue = object.get(JsonParseKeys.LUCKY_WHEEL_DTOS);
//                JsonValue.JsonIterator iterator = jsonValue.iterator();
//                HashMap<String, PracticeDetail> map = new HashMap<String, PracticeDetail>();
//                while (iterator.hasNext()) {
//                    JsonValue practiceJson = iterator.next();
//                    PracticeDetail practiceDetail = new PracticeDetail();
//                    practiceDetail.setBestScore(practiceJson.getInt(JsonParseKeys.BEST_SCORE));
//                    practiceDetail.setTargetScore(practiceJson.getInt(JsonParseKeys.TARGET_SCORE));
//                    practiceDetail.setPrizeType(practiceJson.getString(JsonParseKeys.PRIZE_TYPE));
//                    practiceDetail.setPrizeValue(practiceJson.getString(JsonParseKeys.PRIZE_VALUE));
//                    practiceDetail.setGameType(practiceJson.getString(JsonParseKeys.GAME_TYPE));
//                    practiceDetail.setDisplayOnFirstPosition(practiceJson.getBoolean(JsonParseKeys.FIRST_POSITION));
//                    practiceDetail.setShowVideoAds(practiceJson.getString(JsonParseKeys.SHOW_VIDEO_ADS));
//                    map.put(practiceDetail.getGameType(), practiceDetail);
//                }
//                Constant.userProfile.setMapPracticeDetail(map);
//            }
//            if (object.has(JsonParseKeys.SHOW_DAILY_SPIN)) {
//                Constant.userProfile.setShowDailySpin(object.getBoolean(JsonParseKeys.SHOW_DAILY_SPIN));
//                if (object.has(JsonParseKeys.PRIZE_TYPE)) {
//                    Constant.userProfile.setPrizeType(object.getString(JsonParseKeys.PRIZE_TYPE));
//                }
//                if (object.has(JsonParseKeys.PRIZE_TIMER)) {
//                    Constant.userProfile.setPrizeTimer(object.getLong(JsonParseKeys.PRIZE_TIMER));
//                }
//                if (object.has(JsonParseKeys.PRIZE_VALUE)) {
//                    Constant.userProfile.setPrizeValue(encryptValue("bscSpin", object.getString(JsonParseKeys.PRIZE_VALUE)));
//                }
//            }

//            RestApis.sharefbBannerurl = null;
//            RestApis.shareWhatsAppBannerurl = null;
//            if (object.has((JsonParseKeys.BANNERS_DTOS))) {
//                JsonValue jsonValue = object.get(JsonParseKeys.BANNERS_DTOS);
//                JsonValue.JsonIterator iterator = jsonValue.iterator();
//                Array<BannerDto> array = new Array<BannerDto>();
//                while (iterator.hasNext()) {
//                    JsonValue bannerJson = iterator.next();
//                    BannerDto bannerDto = new BannerDto();
//                    bannerDto.setId(bannerJson.getLong(JsonParseKeys.ID));
//                    bannerDto.setScreenName(bannerJson.getString(JsonParseKeys.SCREEN_NAME));
//                    bannerDto.setUrl(bannerJson.getString(JsonParseKeys.URL));
//                    if (bannerJson.has(JsonParseKeys.IS_FANTASY))
//                        bannerDto.setFantasy(bannerJson.getBoolean(JsonParseKeys.IS_FANTASY));
//                    if (bannerJson.has(JsonParseKeys.EXPIRE_ON_CLICK))
//                        bannerDto.setExpireOnClick(bannerJson.getBoolean(JsonParseKeys.EXPIRE_ON_CLICK));
//                    if (bannerDto.getScreenName().matches("WTSP_TOKEN")) {
//                        if (proGame.config.isPackageExisted("com.whatsapp")) {
//                            RestApis.shareWhatsAppBannerurl = bannerDto.getUrl();
//                            array.add(bannerDto);
//                        }
//                    } else if (bannerDto.getScreenName().matches("FB_TOKEN")) {
//                        if (proGame.config.isPackageExisted("com.facebook.katana")) {
//                            RestApis.sharefbBannerurl = bannerDto.getUrl();
//                            array.add(bannerDto);
//                        }
//                    } else {
//                        array.add(bannerDto);
//                    }
//                }
//                if (array.size > 1) {
//                    array.insert(1, getBannerLocalDto(preference));
//                } else {
//                    array.add(getBannerLocalDto(preference));
//                }
//                Constant.userProfile.setArrBannerDto(array);
//            }
            if (object.has((JsonParseKeys.CASH_BACK_DTOS))) {
                JsonValue jsonValue = object.get(JsonParseKeys.CASH_BACK_DTOS);
                JsonValue.JsonIterator iterator = jsonValue.iterator();
                List<CashBackDto> cashBackDtos = new ArrayList<CashBackDto>();
                while (iterator.hasNext()) {
                    CashBackDto cashBackDto = new CashBackDto();
                    JsonValue jsonCashBackDto = iterator.next();
                    cashBackDto.setCashBackId(jsonCashBackDto.getString(JsonParseKeys.CASH_BACK_ID));
                    cashBackDto.setDescription(jsonCashBackDto.getString(JsonParseKeys.DESCRIPTION));
                    cashBackDto.setMaximumCashBack(jsonCashBackDto.getInt(JsonParseKeys.MAXIMUM_CASH_BACK));
                    cashBackDto.setMinimumAmount(jsonCashBackDto.getInt(JsonParseKeys.CASH_BACK_MINIMUM_AMOUNT));
                    if (jsonCashBackDto.has(JsonParseKeys.OFFER_IN_PERCENTAGE)) {
                        cashBackDto.setOfferInPercentage(jsonCashBackDto.getInt(JsonParseKeys.OFFER_IN_PERCENTAGE));
                    }
                    if (jsonCashBackDto.has(JsonParseKeys.CASH_BACK_TYPE)) {
                        cashBackDto.setCashBackType(jsonCashBackDto.getString(JsonParseKeys.CASH_BACK_TYPE));
                        cashBackDtos.add(cashBackDto);
                    } else {
                        cashBackDtos.add(cashBackDto);
                    }

                }
                Constant.userProfile.setCashBackDto(cashBackDtos);
            } else {
                Constant.userProfile.setCashBackDto(null);
            }

            if (object.has(JsonParseKeys.REDEEM_MINIMUM_THRESHOLD)) {
                Constant.userProfile.setRedeemThreshold(object.getString(JsonParseKeys.REDEEM_MINIMUM_THRESHOLD));
            }

            if (object.has(JsonParseKeys.CASUAL_WINNING_DETAILS)) {
                JsonValue winningsJsonValue = object.get(JsonParseKeys.CASUAL_WINNING_DETAILS);
                if (winningsJsonValue.has("POKER")) {
                    Constant.userProfile.setCasualWinnings(winningsJsonValue.getFloat("POKER"));
                }
            }

//                JsonValue winningsJsonValue = object.get(JsonParseKeys.CASUAL_WINNING_DETAILS);
//                if (winningsJsonValue.has(PageView.HeaderItem.CRICKET)) {
//                    Constant.userProfile.addCasualWinnings(PageView.HeaderItem.CRICKET,
//                            winningsJsonValue.getFloat(PageView.HeaderItem.CRICKET));
//                }
//                if (winningsJsonValue.has(PageView.HeaderItem.BASKETBALL)) {
//                    Constant.userProfile.addCasualWinnings(PageView.HeaderItem.BASKETBALL,
//                            winningsJsonValue.getFloat(PageView.HeaderItem.BASKETBALL));
//                }
//                if (winningsJsonValue.has(PageView.HeaderItem.CAR_RACE)) {
//                    Constant.userProfile.addCasualWinnings(PageView.HeaderItem.CAR_RACE,
//                            winningsJsonValue.getFloat(PageView.HeaderItem.CAR_RACE));
//                }
//                if (winningsJsonValue.has(PageView.HeaderItem.SOCCER)) {
//                    Constant.userProfile.addCasualWinnings(PageView.HeaderItem.SOCCER,
//                            winningsJsonValue.getFloat(PageView.HeaderItem.SOCCER));
//                }
//                if (winningsJsonValue.has("RUMMY")) {
//                    Constant.userProfile.addCasualWinnings("RUMMY",
//                            winningsJsonValue.getFloat("RUMMY"));
//                }
//                if (winningsJsonValue.has("POKER")) {
//                    Constant.userProfile.addCasualWinnings("POKER",
//                            winningsJsonValue.getFloat("POKER"));
//                }
//                if (winningsJsonValue.has(FantasyScreen.KABADDI)) {
//                    Constant.userProfile.addCasualWinnings(FantasyScreen.KABADDI,
//                            winningsJsonValue.getFloat(FantasyScreen.KABADDI));
//                }
//                if (winningsJsonValue.has(FantasyScreen.FANTASY_FOOTBALL)) {
//                    Constant.userProfile.addCasualWinnings(FantasyScreen.FANTASY_FOOTBALL,
//                            winningsJsonValue.getFloat(FantasyScreen.FANTASY_FOOTBALL));
//                }
//                if (winningsJsonValue.has(FantasyScreen.FANTASYCRICKET)) {
//                    Constant.userProfile.addCasualWinnings(FantasyScreen.FANTASYCRICKET,
//                            winningsJsonValue.getFloat(FantasyScreen.FANTASYCRICKET));
//                }
//                if (winningsJsonValue.has(PageView.HeaderItem.NINJA)) {
//                    Constant.userProfile.addCasualWinnings(PageView.HeaderItem.NINJA,
//                            winningsJsonValue.getFloat(PageView.HeaderItem.NINJA));
//                }
//                if (winningsJsonValue.has(PageView.HeaderItem.KNIFE)) {
//                    Constant.userProfile.addCasualWinnings(PageView.HeaderItem.KNIFE,
//                            winningsJsonValue.getFloat(PageView.HeaderItem.KNIFE));
//                }
//                if (winningsJsonValue.has(PageView.HeaderItem.EGGTOSS)) {
//                    Constant.userProfile.addCasualWinnings(PageView.HeaderItem.EGGTOSS,
//                            winningsJsonValue.getFloat(PageView.HeaderItem.EGGTOSS));
//                }
//                if (winningsJsonValue.has(PageView.HeaderItem.CHIPS)) {
//                    Constant.userProfile.addCasualWinnings(PageView.HeaderItem.CHIPS,
//                            winningsJsonValue.getFloat(PageView.HeaderItem.CHIPS));
//                }
//
//                if (winningsJsonValue.has(PageView.HeaderItem.ICEBLASTER)) {
//                    Constant.userProfile.addCasualWinnings(PageView.HeaderItem.ICEBLASTER,
//                            winningsJsonValue.getFloat(PageView.HeaderItem.ICEBLASTER));
//                } else {
//                    Constant.userProfile.addCasualWinnings(PageView.HeaderItem.ICEBLASTER,
//                            0);
//                }
//                if (winningsJsonValue.has(PageView.HeaderItem.EIGHTBALLPOOL)) {
//                    Constant.userProfile.addCasualWinnings(PageView.HeaderItem.EIGHTBALLPOOL,
//                            winningsJsonValue.getFloat(PageView.HeaderItem.EIGHTBALLPOOL));
//                } else {
//                    Constant.userProfile.addCasualWinnings(PageView.HeaderItem.EIGHTBALLPOOL,
//                            0);
//                }
//
//            }

//            if (object.has(JsonParseKeys.SHOW_INTERSITIALS_ON_EXIT)) {
//                Constant.userProfile.setShowIntersitialsOnExit(object.getBoolean(JsonParseKeys.SHOW_INTERSITIALS_ON_EXIT));
//            } else {
//                Constant.userProfile.setShowIntersitialsOnExit(false);
//            }
//            if (object.has(JsonParseKeys.SHOW_INTERSITIALS_IN_GAME)) {
//                Constant.userProfile.setShowIntersiltailsInGame(object.getBoolean(JsonParseKeys.SHOW_INTERSITIALS_IN_GAME));
//            } else {
//                Constant.userProfile.setShowIntersiltailsInGame(false);
//            }
//
//            if (object.has(JsonParseKeys.IS_INSTALL_WITH_PROVIDER)) {
//                Constant.userProfile.setInstallWithProvider(object.getBoolean(JsonParseKeys.IS_INSTALL_WITH_PROVIDER));
//            }
//
//            if (object.has(JsonParseKeys.MEGA_CONTEST_DTO)) {
//                Constant.userProfile.setMegaContest(parseMegaContest(object.get(JsonParseKeys.MEGA_CONTEST_DTO)), proGame.config);
//            }
            if (object.has(JsonParseKeys.REFERRAL_WINNING_AMOUNT)) {
                ReferralLeagueDetail referralLeagueDetail = new ReferralLeagueDetail();
                referralLeagueDetail.setReferralLeaderboardRemainingTime(object.getLong(JsonParseKeys.REFERRAL_LEADERBOARD_REMAINING_TIME));
                referralLeagueDetail.setReferralTotalParticipant(object.getLong(JsonParseKeys.REFERRAL_TOTAL_PARTICIPANT));
                referralLeagueDetail.setReferralWinningAmount(object.getInt(JsonParseKeys.REFERRAL_WINNING_AMOUNT));
                if (object.has(JsonParseKeys.REFERRAL_MONTHLY_CONTEST_STATUS)) {
                    referralLeagueDetail.setReferralMonthlyContestStatus(object.getBoolean(JsonParseKeys.REFERRAL_MONTHLY_CONTEST_STATUS));
                }
                Constant.userProfile.setReferralLeagueDetail(referralLeagueDetail);
            }
//            if (object.has(JsonParseKeys.BACKGROUND_APK_DOWNLOAD_VERSION_CODE)) {
//                Constant.userProfile.setLatestApkVersionCode(object.getInt(JsonParseKeys.BACKGROUND_APK_DOWNLOAD_VERSION_CODE));
//            }
//            if (object.has(JsonParseKeys.BACKGROUND_APK_DOWNLOAD_URL)) {
//                Constant.userProfile.setDownloadInBackGround(true);
//                Constant.GENERIC_POPUP_ACTION_URL = object.getString(JsonParseKeys.BACKGROUND_APK_DOWNLOAD_URL);
//            }

//            if (object.has(JsonParseKeys.BANNER_CONFIG_DTO)) {
//                JsonValue jsonValue = object.get(JsonParseKeys.BANNER_CONFIG_DTO);
//                if(jsonValue.has(JsonParseKeys.FANTASY_CRICKET)){
//                    parseFantasyBanner(jsonValue.get(JsonParseKeys.FANTASY_CRICKET));
//                }else{
//                    Constant.fantasyContestBanner = null;
//                }
//                if(jsonValue.has(JsonParseKeys.RUMMY_TORNEY)){
//                    parseRummyBanner(jsonValue.get(JsonParseKeys.RUMMY_TORNEY));
//                }else{
//                    Constant.rummyTournamentBanner = null;
//                }
//                Constant.GENERIC_POPUP_ACTION_URL = object.getString(JsonParseKeys.BACKGROUND_APK_DOWNLOAD_URL);
//            }
//            Gdx.app.error("App launch ", "Success");
        } catch (Exception e) {
            //   Gdx.app.error("App launch ", e.getMessage());
        }
    }

    private static void parseConfigDto(GamePreferences preference, JsonValue configDto) {
        try {
            Gdx.app.error("App Config", configDto.toJson(JsonWriter.OutputType.json));
            preference.setInviteText(configDto.getString(JsonParseKeys.INVITE_TEXT));
            preference.setFBInviteText(configDto.getString(JsonParseKeys.FB_INVITE_TEXT));
            preference.setDisplayInviteText(configDto.getString(JsonParseKeys.INVITE_DISPLAY_TEXT));
            preference.setPaytmInviteText(configDto.getString(JsonParseKeys.INVITE_PAYTM_CASH));

//            if (configDto.has(JsonParseKeys.RUMMY_KEY)) {
//                preference.setRummyKey(configDto.getString(JsonParseKeys.RUMMY_KEY));
//            }
//            if (configDto.has(JsonParseKeys.RUMMY_IP)) {
//                preference.setRummyIp(configDto.getString(JsonParseKeys.RUMMY_IP));
//            }
//            if (configDto.has(JsonParseKeys.POOL_KEY)) {
//                preference.setPoolKey(configDto.getString(JsonParseKeys.POOL_KEY));
//            }
//            }
//            if (configDto.has(JsonParseKeys.POOL_IP)) {
//                preference.setPoolIp(configDto.getString(JsonParseKeys.POOL_IP));
//            }
            if (configDto.has(JsonParseKeys.POKER_KEY)) {
                preference.setPokerKey(configDto.getString(JsonParseKeys.POKER_KEY));
            }
            if (configDto.has(JsonParseKeys.POKER_IP)) {
                preference.setPokerIp(configDto.getString(JsonParseKeys.POKER_IP));
            }

//            if (configDto.has(JsonParseKeys.TOURNERY_RUMMY_IP)) {
//                preference.setTourneyRummyIp(configDto.getString(JsonParseKeys.TOURNERY_RUMMY_IP));
//            }
//            if (configDto.has(JsonParseKeys.TOURNERY_RUMMY_KEY)) {
//                preference.setTourneyRummyKey(configDto.getString(JsonParseKeys.TOURNERY_RUMMY_KEY));
//            }
//
//            if (configDto.has(JsonParseKeys.BULB_SMASH_IP)) {
//                preference.setBulbSmashIp(configDto.getString(JsonParseKeys.BULB_SMASH_IP));
//            }
//            if (configDto.has(JsonParseKeys.BULB_SMASH_KEY)) {
//                preference.setBulbSmashKey(configDto.getString(JsonParseKeys.BULB_SMASH_KEY));
//            }
//
//            if (configDto.has(JsonParseKeys.KNIFE_HIT_IP)) {
//                preference.setKnifeHitIp(configDto.getString(JsonParseKeys.KNIFE_HIT_IP));
//            }
//            if (configDto.has(JsonParseKeys.KNIFE_HIT_KEY)) {
//                preference.setKnifeHitKey(configDto.getString(JsonParseKeys.KNIFE_HIT_KEY));
//            }
//
            if (configDto.has(JsonParseKeys.USE_JENKINS_TIME_ENABLE)) {
                preference.setUseJenkinsTimeEnable(configDto.getBoolean(JsonParseKeys.USE_JENKINS_TIME_ENABLE));
            } else {
                preference.setUseJenkinsTimeEnable(false);
            }
//            if (configDto.has(JsonParseKeys.RUMMY_SUPPORT_NUMBER)) {
//                preference.setRummySupportNumber(configDto.getString(JsonParseKeys.RUMMY_SUPPORT_NUMBER));
//            }
//            if (configDto.has(JsonParseKeys.GOOGLE_LOCATION_KEY)) {
//                preference.setGoogleLocationKey(configDto.getString(JsonParseKeys.GOOGLE_LOCATION_KEY));
//            }
//            if (configDto.has(JsonParseKeys.REDEEM_VALUES)) {
//                preference.setRedeemValues(configDto.getString(JsonParseKeys.REDEEM_VALUES));
//            } else {
//                preference.setRedeemValues("");
//            }
//            if (configDto.has(JsonParseKeys.CHIPS_LEAGUE_MAX_PRIZE))
//                preference.setChipsLeagueMaxPrize(configDto.getString(JsonParseKeys.CHIPS_LEAGUE_MAX_PRIZE));
//            else
//                preference.setChipsLeagueMaxPrize("");

            //Gdx.app.error(bettingCash,bettingCoins);
            //Gdx.app.error("Config","done");
            preference.setConfigUpdateTime(TimeUtils.millis());
//            Constant.initialValue.setCcImages(configDto.getString(JsonParseKeys.CRICKET_CONS));
//            if (configDto.has(JsonParseKeys.BASKETBALL_CONS))
//                Constant.initialValue.setBbImages(configDto.getString(JsonParseKeys.BASKETBALL_CONS));
//            if (configDto.has(JsonParseKeys.CAR_RACE_CONS))
//                Constant.initialValue.setCarRaceImages(configDto.getString(JsonParseKeys.CAR_RACE_CONS));
//            if (configDto.has(JsonParseKeys.KNIFE_HIT_CONS))
//                Constant.initialValue.setKhImages(configDto.getString(JsonParseKeys.KNIFE_HIT_CONS));
//            if (configDto.has(JsonParseKeys.SOCCER_CONS))
//                Constant.initialValue.setSoccerImages(configDto.getString(JsonParseKeys.SOCCER_CONS));
//            if (configDto.has(JsonParseKeys.FRUIT_CHOP_CONS))
//                Constant.initialValue.setFruitChopImages(configDto.getString(JsonParseKeys.FRUIT_CHOP_CONS));
//            if (configDto.has(JsonParseKeys.EGG_TOSS_CONS))
//                Constant.initialValue.setEggTossImages(configDto.getString(JsonParseKeys.EGG_TOSS_CONS));
//            if (configDto.has(JsonParseKeys.ICE_BLASTER_CONS))
//                Constant.initialValue.setIceBlasterImages(configDto.getString(JsonParseKeys.ICE_BLASTER_CONS));
            if (configDto.has(JsonParseKeys.REFERRAL_CONTEST_RULES))
                preference.setReferralContestRules(configDto.getString(JsonParseKeys.REFERRAL_CONTEST_RULES));
//            if (configDto.has(JsonParseKeys.POOL_CONS)) {
//                Constant.initialValue.setEightBallPoolImages(configDto.getString(JsonParseKeys.POOL_CONS));
//            }

//            if (configDto.has(JsonParseKeys.FANTASY_SOCCER_DISABLE)) {
//                preference.setFantasySoccerDisable(configDto.getBoolean(JsonParseKeys.FANTASY_SOCCER_DISABLE));
//            }
//            if (configDto.has(JsonParseKeys.GAME_ORDERING)) {
//                JsonValue gameOrdering = configDto.get(JsonParseKeys.GAME_ORDERING);
//                String[] arrTrending = gameOrdering.get(JsonParseKeys.TRENDING).asStringArray();
//                String trending = "";
//                for (String s : arrTrending) {
//                    if (trending.isEmpty()) {
//                        trending = s;
//                    } else {
//                        trending = trending + "," + s;
//                    }
//                }
//                preference.setTrending(trending);
//                String[] arrOneToOne = gameOrdering.get(JsonParseKeys.ONE_TO_ONE).asStringArray();
//                String oneToOne = "";
//                for (String s : arrOneToOne) {
//                    if (oneToOne.isEmpty()) {
//                        oneToOne = s;
//                    } else {
//                        oneToOne = oneToOne + "," + s;
//                    }
//                }
//                preference.setOneToOne(oneToOne);
//                String[] arrContests = gameOrdering.get(JsonParseKeys.CONTESTS).asStringArray();
//                String contests = "";
//                for (String s : arrContests) {
//                    if (contests.isEmpty()) {
//                        contests = s;
//                    } else {
//                        contests = contests + "," + s;
//                    }
//                }
//                preference.setContests(contests);
//            }
//            if (isUpdateDataBase) {
//                isUpdateDataBase = false;
//                // preference.saveMap(proGame.config.fetchAllResult());
//            }
        } catch (Exception e) {
            // Gdx.app.log("Config",e.getMessage());
        }
    }

    private static void parsePaytmLinkDto(JsonValue jsonValue) {
        try {
            Constant.userProfile.setPaytmAccountBalance(jsonValue.getFloat(JsonParseKeys.PAYTM_WALLET_BALNCE));
            Constant.userProfile.setPaytmWalletText(jsonValue.getString(JsonParseKeys.PAYTM_WALLET_BALNCE_TEXT));
            Constant.userProfile.setPaytmLinkedNo(jsonValue.getString(JsonParseKeys.MSISDN));
            if (jsonValue.has(JsonParseKeys.PAYTM_POST_PAID_BALANCE)) {
                Constant.userProfile.setPaytmPostPaidBalance(jsonValue.getFloat(JsonParseKeys.PAYTM_POST_PAID_BALANCE));
                Constant.userProfile.setPaytmPostPaidActive(true);
            } else {
                Constant.userProfile.setPaytmPostPaidActive(false);
            }
            Constant.userProfile.setPaytmAccountLinked(true);
        } catch (Exception e) {
            Constant.userProfile.setPaytmAccountLinked(false);
        }
    }


    public static void callChecksumApi(String orderId, float price, String cashBackId, ProcessDialog processDialog, String source, GdxListener<String> listener) {
        final float amount = PokerUtils.getValue(price);
        HashMap<String, String> params = new HashMap();
//        final String callbackUrl = "https://campaign.bigcash.live/paytmCb.php";
        final String callbackUrl = GWT.getHostPageBaseURL() + "checkout";
        Gdx.app.error("CallBackUrl", callbackUrl);
        String transactionId = TimeUtils.millis() + "";
        params.put("requestType", Payment.REQUEST_TYPE);
        params.put("userId", GamePreferences.instance().getUserId());
        params.put("otp", "abc");
        params.put("transactionId", transactionId + "");
        params.put("amount", amount + "");
        params.put("orderId", orderId);
        params.put("merchantMid", Payment.MERCHANT_MID);
        params.put("channelId", Payment.CHANNEL_ID);
        params.put("website", Payment.WEBSITE);
        params.put("industryTypeId", Payment.INDUSTRY_TYPE_ID);
        params.put("callbackUrl", callbackUrl);
        params.put("isWeb", "true");

        Gdx.app.log("Checksum Params", params.toString());

        HttpRequestBuilder requestBuilder = new HttpRequestBuilder();
        Net.HttpRequest httpPost = requestBuilder.newRequest()
                .method(Net.HttpMethods.POST)
                .timeout(10000)
                .formEncodedContent(params)
                .url(ApiHandler.BASE_URL + ApiHandler.API_VERSION_1 + ApiHandler.PAYTM_GENERATE_CHECK_SUM_API + "?userId=" + GamePreferences.instance().getUserId())
                .build();

        Gdx.net.sendHttpRequest(httpPost, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                Gdx.app.log("Checksum Response", httpResponse.getStatus().getStatusCode() + ":" + httpResponse.getResultAsString());
                int statusCode = httpResponse.getStatus().getStatusCode();
                if (errorCodeHandling(statusCode)) {
                    JsonValue jsonValue = new JsonReader().parse(httpResponse.getResultAsString());
                    String checksum = jsonValue.getString("checksum");
                    String sso_token = jsonValue.getString("sso_token");
                    Gdx.app.postRunnable(new Runnable() {
                        @Override
                        public void run() {
                            processDialog.hide();
                        }
                    });
                    Payment.startPaytmProcess(sso_token, orderId, GamePreferences.instance().getUserId(), amount + "", callbackUrl, checksum, cashBackId, source, listener);
                } else {
                    if (statusCode != HttpStatus.SC_UNAUTHORIZED) {
                        listener.setError("Server Error");
                    }
                }
            }

            @Override
            public void failed(Throwable t) {
                listener.setFail(t.getMessage());
            }

            @Override
            public void cancelled() {
                listener.setError("Cancelled");
            }
        });
    }


    public static void callRazorRequestOrder(String amount, String orderId, GdxListener<JsonValue> listener) {
        JsonValue orderRequest = new JsonValue(JsonValue.ValueType.object);
        orderRequest.addChild("amount", new JsonValue(amount));
        orderRequest.addChild("currency", new JsonValue("INR"));
        orderRequest.addChild("receipt", new JsonValue(orderId));
        HttpRequestBuilder requestBuilder = new HttpRequestBuilder();
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("postBodyJson", orderRequest.toJson(JsonWriter.OutputType.json));
        Net.HttpRequest httpPost = requestBuilder.newRequest()
                .method(Net.HttpMethods.POST)
                .formEncodedContent(params)
                .url(BASE_URL + API_VERSION_1 + RAZOR_PAY)
                .build();
        Gdx.net.sendHttpRequest(httpPost, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                int statusCode = httpResponse.getStatus().getStatusCode();
                String resultString = httpResponse.getResultAsString();
                JsonValue jsonResponse = new JsonReader().parse(resultString);
                if (statusCode == HttpStatus.SC_OK) {
                    listener.setSuccess(new JsonReader().parse(jsonResponse.toJson(JsonWriter.OutputType.json)));
                } else {
                    listener.setError(statusCode + "," + resultString);
                }
                Gdx.app.log("OrderRequest Response", httpResponse.getStatus().getStatusCode() + ":" + httpResponse.getResultAsString());
            }

            @Override
            public void failed(Throwable t) {
                listener.setFail(t.getMessage());
            }

            @Override
            public void cancelled() {
                listener.setError("Cancelled");
            }
        });
    }

    public static void callAddCashApi(final String purchaseChannel, String amount, String payload, String orderId, String purchaseSource,
                                      String cashBackId, final GdxListener<String> listener) {
        String transactionId = TimeUtils.millis() + "";
        GamePreferences preference = GamePreferences.instance();
        HashMap<String, String> params = new HashMap();
        params.put("otp", PokerUtils.encryptValue(transactionId, preference.getOtp()));
        params.put("amount", PokerUtils.encryptValue(transactionId, amount));
        params.put("payload", payload);
        params.put("orderId", orderId);
        params.put("purchaseSource", purchaseSource);
        if (cashBackId != null) {
            params.put("cashBackId", cashBackId);
        }
        params.put("transactionId", transactionId);
//        Gdx.app.log("cashBackId", cashBackId + "");
//        Constant.PROMO_CODE = "";

        HttpRequestBuilder requestBuilder = new HttpRequestBuilder();
        Net.HttpRequest httpPost = requestBuilder.newRequest()
                .method(Net.HttpMethods.POST)
                .timeout(10000)
                .formEncodedContent(params)
                .url(BASE_URL + API_VERSION_9 + "/" + purchaseChannel + ADD_CASH_API + "?userId=" + preference.getUserId())
                .build();

        Gdx.net.sendHttpRequest(httpPost, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                int statusCode = httpResponse.getStatus().getStatusCode();
                String resultString = httpResponse.getResultAsString();
                if (statusCode == HttpStatus.SC_OK) {
                    parseAddCashResponse(resultString);
                    listener.setSuccess("Success");
                } else {
                    if (statusCode == 402 && purchaseChannel.matches("PAYTM")) {
                        parseLowBalanceResponse(resultString);
                        listener.setError(statusCode + "");
                    } else {
                        listener.setError("Add Cash Failed");
                    }
                }
            }

            @Override
            public void failed(Throwable t) {
                listener.setFail("Connection Error! Please try again later.");
            }

            @Override
            public void cancelled() {
                listener.setFail("Cencelled");
            }
        });
    }

    private static void parseAddCashResponse(String response) {
        try {
            JsonValue object = new JsonReader().parse(response);
            if (Constant.userProfile != null) {
                //Constant.userProfile.setTotalCashAdded(1);
                Constant.userProfile.setCashGameLocked(false);
                Constant.userProfile.setPaytmBalance(object.getFloat(JsonParseKeys.PAYTM_BALANCE));
                if (object.has(JsonParseKeys.LIFE)) {
                    Constant.userProfile.setLife(object.getInt(JsonParseKeys.LIFE));
                }
                if (object.has(JsonParseKeys.PENDING_BONUS_BALANCE)) {
                    Constant.userProfile.setPendingBonus(object.getFloat(JsonParseKeys.PENDING_BONUS_BALANCE));
                }
                if (object.has(JsonParseKeys.PENDING_BONUS_POLICY)) {
                    Constant.userProfile.setPendingBonusPolicy(object.getString(JsonParseKeys.PENDING_BONUS_POLICY));
                }
                if (!GamePreferences.instance().isBalanceIncreased())
                    GamePreferences.instance().setBalanceIncreased(true);

                if (object.has(JsonParseKeys.PAYTM_LINK_DTO)) {
                    parsePaytmLinkDto(object.get(JsonParseKeys.PAYTM_LINK_DTO));
                }
            }
        } catch (Exception e) {

        }
    }

    private static void parseLowBalanceResponse(String response) {
        try {
            JsonValue jsonValue = new JsonReader().parse(response);
            parsePaytmLinkDto(jsonValue);
        } catch (Exception e) {
            Constant.userProfile.setPaytmAccountLinked(false);
        }
    }

    public static void callPaytmSendOtpApi(String msisdn, final GdxListener<PaytmOtpDetail> listener) {
        GamePreferences preference = GamePreferences.instance();
        HashMap<String, String> params = new HashMap();
        String transactionId = TimeUtils.millis() + "";
        params.put("otp", PokerUtils.encryptValue(transactionId, preference.getOtp()));
        params.put("transactionId", transactionId + "");
        params.put("msisdn", msisdn);

        HttpRequestBuilder requestBuilder = new HttpRequestBuilder();
        Net.HttpRequest httpPost = requestBuilder.newRequest()
                .method(Net.HttpMethods.POST)
                .timeout(10000)
                .formEncodedContent(params)
                .url(BASE_URL + API_VERSION_1 + PAYTM_SEND_OTP_API + "?userId=" + preference.getUserId())
                .build();


        Gdx.net.sendHttpRequest(httpPost, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                int statusCode = httpResponse.getStatus().getStatusCode();
                String resultString = httpResponse.getResultAsString();
                if (errorCodeHandling(statusCode)) {
                    listener.setSuccess(parsePaytmSendOtpApi(resultString));
                } else {
                    listener.setError(resultString);
                }
            }

            @Override
            public void failed(Throwable t) {
                listener.setFail("Please check your Internet.");
            }

            @Override
            public void cancelled() {
                listener.setFail("Please check your Internet.");
            }

        });
    }

    private static PaytmOtpDetail parsePaytmSendOtpApi(String response) {
        try {
            PaytmOtpDetail paytmOtpDetail = new PaytmOtpDetail();
            JsonValue jsonValue = new JsonReader().parse(response);
            paytmOtpDetail.setMessage(jsonValue.getString(JsonParseKeys.MESSAGE));
            paytmOtpDetail.setOtpLocation(jsonValue.getInt(JsonParseKeys.OTP_LOCATION));
            paytmOtpDetail.setState(jsonValue.getString(JsonParseKeys.STATE));
            return paytmOtpDetail;
        } catch (Exception e) {

        }
        return null;
    }

    public static void callPaytmValidateOtpApi(String msisdn, String paytmOtp, String state, boolean isJoiningBonus, final GdxListener<String> listener) {
        GamePreferences preference = GamePreferences.instance();
        HashMap<String, String> params = new HashMap();
        String transactionId = TimeUtils.millis() + "";
        params.put("otp", PokerUtils.encryptValue(transactionId, preference.getOtp()));
        params.put("state", state);
        params.put("paytmOtp", paytmOtp);
        params.put("isJoiningBonus", isJoiningBonus + "");
        params.put("transactionId", transactionId + "");
        params.put("msisdn", msisdn);
        params.put("isJoiningBonus", preference.isShowPaytmLink() + "");

        HttpRequestBuilder requestBuilder = new HttpRequestBuilder();
        Net.HttpRequest httpPost = requestBuilder.newRequest()
                .method(Net.HttpMethods.POST)
                .timeout(10000)
                .formEncodedContent(params)
                .url(BASE_URL + API_VERSION_1 + PAYTM_VALIDATE_OTP_API + "?userId=" + preference.getUserId())
                .build();

        Gdx.net.sendHttpRequest(httpPost, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                Response response = new Response();
                response.setResult(httpResponse.getResultAsString());
                response.setErrorCode(httpResponse.getStatus().getStatusCode());
                Gdx.app.log("Validate Api", response.getResult());
                if (errorCodeHandling(response.getErrorCode())) {
                    GamePreferences.instance().setShowPaytmLink(false);
                    parseValidateResponse(response.getResult());
                    listener.setSuccess(response.getResult());
                } else {
                    listener.setError(response.getResult());
                }
            }

            @Override
            public void failed(Throwable t) {
                listener.setFail("Please check your Internet.");
            }

            @Override
            public void cancelled() {
                listener.setFail("Please check your Internet.");
            }

        });
    }

    public static void callPaytmAutoDebitUnlinkApi(final GdxListener<String> listener) {
        GamePreferences preference = GamePreferences.instance();
        HashMap<String, String> params = new HashMap();
        String transactionId = TimeUtils.millis() + "";
        params.put("otp", PokerUtils.encryptValue(transactionId, preference.getOtp()));
        params.put("transactionId", transactionId + "");


        HttpRequestBuilder requestBuilder = new HttpRequestBuilder();
        Net.HttpRequest httpPost = requestBuilder.newRequest()
                .method(Net.HttpMethods.POST)
                .timeout(10000)
                .formEncodedContent(params)
                .url(BASE_URL + API_VERSION_1 + PAYTM_AUTO_DEBIT_UNLINK_API + "?userId=" + preference.getUserId())
                .build();

        Gdx.net.sendHttpRequest(httpPost, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                Response response = new Response();
                response.setResult(httpResponse.getResultAsString());
                response.setErrorCode(httpResponse.getStatus().getStatusCode());
                if (errorCodeHandling(response.getErrorCode())) {
                    Constant.userProfile.setPaytmAccountLinked(false);
                    listener.setSuccess("Success");
                } else {
                    if (response.getErrorCode() != 401) {
                        listener.setError("Server Error");
                    }
                }
            }

            @Override
            public void failed(Throwable t) {
                listener.setFail("Please check your Internet.");
            }

            @Override
            public void cancelled() {
                listener.setFail("Please check your Internet.");
            }

        });
    }

    public static void callEventLogApi(String screenName, String eventType, String eventDescription) {
        GamePreferences preference = GamePreferences.instance();
        HashMap<String, String> params = new HashMap();
        String userId = preference.getUserId();
        params.put("userId", userId);
        params.put("screenName", screenName);
        params.put("eventType", eventType);
        params.put("eventDescription", eventDescription);
        params.put("deviceId", Constant.getDeviceId());

        HttpRequestBuilder requestBuilder = new HttpRequestBuilder();
        Net.HttpRequest httpGet = requestBuilder.newRequest()
                .method(Net.HttpMethods.GET)
                .timeout(10000)
                .formEncodedContent(params)
                .url(BASE_URL + API_VERSION_1 + EVENT_LOG_API)
                .build();

        Gdx.net.sendHttpRequest(httpGet, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                Response response = new Response();
                response.setResult(httpResponse.getResultAsString());
                response.setErrorCode(httpResponse.getStatus().getStatusCode());
            }

            @Override
            public void failed(Throwable t) {
            }

            @Override
            public void cancelled() {
            }

        });
    }

    public static void callCurrentTimeApi(GdxListener<String> listener) {
        String transactionId = TimeUtils.millis() + "";
        GamePreferences preference = GamePreferences.instance();
        HashMap<String, String> params = new HashMap();
        params.put("userId", preference.getUserId());
        params.put("otp", PokerUtils.encryptValue(transactionId, preference.getOtp()));
        params.put("transactionId", transactionId);

        HttpRequestBuilder requestBuilder = new HttpRequestBuilder();
        Net.HttpRequest httpGet = requestBuilder.newRequest()
                .method(Net.HttpMethods.GET)
                .timeout(10000)
                .formEncodedContent(params)
                .url(BASE_URL + API_VERSION_8 + CURRENT_TIME_API)
                .build();
        Gdx.net.sendHttpRequest(httpGet, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {

                Response response = new Response();
                response.setResult(httpResponse.getResultAsString());
                response.setErrorCode(httpResponse.getStatus().getStatusCode());
                listener.setSuccess("Success");
            }

            @Override
            public void failed(Throwable t) {
                listener.setSuccess("Failed");
            }

            @Override
            public void cancelled() {
                listener.setSuccess("Error");
            }
        });
    }

    private static void parseValidateResponse(String response) {
        try {
            JsonValue jsonValue = new JsonReader().parse(response);
            parsePaytmLinkDto(jsonValue.get(JsonParseKeys.PAYTM_LINK_DTO));
            if (jsonValue.has(JsonParseKeys.PAYTM_BALANCE)) {
                Constant.userProfile.setPaytmBalance(jsonValue.getFloat(JsonParseKeys.PAYTM_BALANCE));
            }
            if (jsonValue.has(JsonParseKeys.LIFE)) {
                Constant.userProfile.setLife(jsonValue.getInt(JsonParseKeys.LIFE));
            }
            if (jsonValue.has(JsonParseKeys.ALERT_TEXT)) {
                Constant.userProfile.setAlertMessage(jsonValue.getString(JsonParseKeys.ALERT_TEXT));
            }
            if (jsonValue.has(JsonParseKeys.MSISDN)) {
                Constant.userProfile.setMsisdn(jsonValue.getString(JsonParseKeys.MSISDN));
                Constant.userProfile.setMsisdnVerified(true);
            }
        } catch (Exception e) {
            Constant.userProfile.setPaytmAccountLinked(false);
        }
    }

    public static void callProfileApi(boolean isShowFullProfile, final GdxListener<String> listener) {
        String transactionId = TimeUtils.millis() + "";
        GamePreferences preference = GamePreferences.instance();
        HashMap<String, String> params = new HashMap();
        params.put("userId", preference.getUserId());
        params.put("otp", PokerUtils.encryptValue(transactionId, preference.getOtp()));
        params.put("transactionId", transactionId);
        params.put("showFullProfile", isShowFullProfile + "");

        HttpRequestBuilder requestBuilder = new HttpRequestBuilder();
        Net.HttpRequest httpGet = requestBuilder.newRequest()
                .method(Net.HttpMethods.GET)
                .timeout(10000)
                .formEncodedContent(params)
                .url(BASE_URL + API_VERSION_4 + PROFILE_API)
                .build();

        Gdx.net.sendHttpRequest(httpGet, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                Response response = new Response();
                response.setResult(httpResponse.getResultAsString());
                response.setErrorCode(httpResponse.getStatus().getStatusCode());
                if (errorCodeHandling(response.getErrorCode())) {
                    parseProfileApi(response.getResult());
                    listener.setSuccess("");
                } else {
                    listener.setSuccess("");
                }
            }

            @Override
            public void failed(Throwable t) {
                listener.setFail(t.getMessage());
            }

            @Override
            public void cancelled() {
                listener.setError("Cancelled");
            }
        });
    }

    public static void callTrueCallerApi(String requestId, GdxListener<String> listener) {
        String transactionId = TimeUtils.millis() + "";
        GamePreferences preference = GamePreferences.instance();
        HashMap<String, String> params = new HashMap();
        params.put("userId", preference.getUserId());
        params.put("otp", PokerUtils.encryptValue(transactionId, preference.getOtp()));
        params.put("transactionId", transactionId);
        params.put("requestId", requestId);

        HttpRequestBuilder requestBuilder = new HttpRequestBuilder();
        Net.HttpRequest httpPost = requestBuilder.newRequest()
                .method(Net.HttpMethods.POST)
                .timeout(10000)
                .formEncodedContent(params)
                .url(BASE_URL + API_VERSION_4 + TRUE_CALLER_API)
                .build();

        Gdx.net.sendHttpRequest(httpPost, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                int statusCode = httpResponse.getStatus().getStatusCode();
                String resultString = httpResponse.getResultAsString();
                if (errorCodeHandling(statusCode)) {
                    parseProfileApi(resultString);
                    listener.setSuccess("");
                } else {
                    if (statusCode == 417) {
                        listener.setSuccess("417");
                    }
                    listener.setFail(statusCode + "," + resultString);
                }
            }

            @Override
            public void failed(Throwable t) {
                listener.setFail(t.getMessage());
            }

            @Override
            public void cancelled() {
                listener.setError("Cancelled");
            }
        });
    }

    private static void parseProfileApi(String response) {
        try {
            JsonValue object = new JsonReader().parse(response);
            if (Constant.userProfile != null) {
                Constant.userProfile.setUserId(object.getString(JsonParseKeys.USER_ID));
                if (object.has(JsonParseKeys.LIFE)) {
                    Constant.userProfile.setLife(object.getInt(JsonParseKeys.LIFE));
                }
                Constant.userProfile.setPaytmBalance(object.getFloat(JsonParseKeys.PAYTM_BALANCE));
                if (object.has(JsonParseKeys.WITHDRAWABLE_BALANCE)) {
                    Constant.userProfile.setWithdrawable(object.getFloat(JsonParseKeys.WITHDRAWABLE_BALANCE));
                }
                if (object.has(JsonParseKeys.DEPOSITED_BALANCE)) {
                    Constant.userProfile.setDeposited(object.getFloat(JsonParseKeys.DEPOSITED_BALANCE));
                }
                Constant.userProfile.setName(object.getString(JsonParseKeys.NAME));
                if (object.has(JsonParseKeys.EMAIL_ID))
                    Constant.userProfile.setEmailId(object.getString(JsonParseKeys.EMAIL_ID));
                Constant.userProfile.setEmailVerified(object.getBoolean(JsonParseKeys.EMAIL_VERIFIED));
                if (object.has(JsonParseKeys.PAN_CARD_VERIFIED))
                    Constant.userProfile.setPanCardVerified(object.getBoolean(JsonParseKeys.PAN_CARD_VERIFIED));
                Constant.userProfile.setMsisdnVerified(object.getBoolean(JsonParseKeys.MSISDN_VERIFIED));
                if (object.has(JsonParseKeys.MSISDN))
                    Constant.userProfile.setMsisdn(object.getString(JsonParseKeys.MSISDN));
                if (object.has(JsonParseKeys.PENDING_BONUS_BALANCE)) {
                    Constant.userProfile.setPendingBonus(object.getFloat(JsonParseKeys.PENDING_BONUS_BALANCE));
                }
                if (object.has(JsonParseKeys.PAN_NUMBER))
                    Constant.userProfile.setPanNumber(object.getString(JsonParseKeys.PAN_NUMBER));
                if (object.has(JsonParseKeys.PAN_STATUS)) {
                    Constant.userProfile.setPanStatus(object.getString(JsonParseKeys.PAN_STATUS));
                }
                if (object.has(JsonParseKeys.FRIENDS_DTOS)) {
                    Array<FriendsDto> array = new Array<FriendsDto>();
                    JsonValue jsonValue = object.get(JsonParseKeys.FRIENDS_DTOS);
                    JsonValue.JsonIterator iterator = jsonValue.iterator();
                    while (iterator.hasNext()) {
                        JsonValue friendsDtosJson = iterator.next();
                        FriendsDto friendsDto = new FriendsDto();
                        friendsDto.setFbUserId(friendsDtosJson.getString(JsonParseKeys.FB_USER_ID));
                        friendsDto.setName(friendsDtosJson.getString(JsonParseKeys.NAME));
                        friendsDto.setProfilePicUrl(friendsDtosJson.getString(JsonParseKeys.PROFILE_PIC_URL));
                        array.add(friendsDto);
                    }
                    Constant.userProfile.setFriendsDtos(array);
                }
            }
        } catch (Exception e) {
//            Gdx.app.error("Level Cross Api", "error");
        }
    }

    public static void callReferralLeaderboardApi(boolean isPrevious, final GdxListener<ReferralLeague> listener) {
        GamePreferences preference = GamePreferences.instance();
        HashMap<String, String> params = new HashMap();
        String userId = preference.getUserId();
        params.put("userId", userId);
        String transactionId = TimeUtils.millis() + "";
        params.put("otp", PokerUtils.encryptValue(transactionId, preference.getOtp()));
        params.put("transactionId", transactionId);
        params.put("isPrevious", isPrevious + "");
        HttpRequestBuilder requestBuilder = new HttpRequestBuilder();
        Net.HttpRequest httpGet = requestBuilder.newRequest()
                .method(Net.HttpMethods.GET)
                .timeout(10000)
                .formEncodedContent(params)
                .url(BASE_URL + API_VERSION_1 + REFERRAL_LEADER_BOARD_API)
                .build();
        Gdx.net.sendHttpRequest(httpGet, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                int status = httpResponse.getStatus().getStatusCode();
                String resultString = httpResponse.getResultAsString();
                if (errorCodeHandling(status)) {
                    String result = "";
                    try {
                        JsonValue jsonValue = new JsonReader().parse(resultString);
                        result = PokerUtils.decompress(jsonValue.getString(JsonParseKeys.RESPONSE));
                    } catch (Exception e) {

                    }
                    listener.setSuccess(parseReferralLeaderBoardApiResponse(result));
                } else {
                    if (status != 401) {
                        if (status == 204) {
                            listener.setSuccess(null);
                        } else if (status == 503) {
                            listener.setError(UNDER_MAINTENANCE);
                        } else {
                            listener.setError(SERVER_BUSY);
                        }
                    }
                }
            }

            @Override
            public void failed(Throwable t) {
                listener.setFail(NETWORK_ISSUE);
            }

            @Override
            public void cancelled() {
                listener.setFail(NETWORK_ISSUE);
            }

        });
    }

    private static ReferralLeague parseReferralLeaderBoardApiResponse(String response) {
        try {
            JsonValue object = new JsonReader().parse(response);
            ReferralLeague referralLeague = new ReferralLeague();
            //  referralLeague.setTotalWinningAmount(object.getFloat(JsonParseKeys.TOTAL_WINNING_AMOUNT));
            if (object.has(JsonParseKeys.USER_RANKING_DETAILS)) {
                JsonValue myDetailsJson = object.get(JsonParseKeys.USER_RANKING_DETAILS);
                ReferralPlayerDto myDetails = new ReferralPlayerDto();
                myDetails.setUserId(myDetailsJson.getString(JsonParseKeys.USER_ID));
                myDetails.setUserRank(myDetailsJson.getInt(JsonParseKeys.USER_RANK));
                myDetails.setUserName(myDetailsJson.getString(JsonParseKeys.USER_NAME));
                if (myDetailsJson.has(JsonParseKeys.WINNING_AMOUNT)) {
                    myDetails.setWinningAmount(myDetailsJson.getFloat(JsonParseKeys.WINNING_AMOUNT));
                }
                myDetails.setTotalPoint(myDetailsJson.getInt(JsonParseKeys.TOTAL_POINT));
                if (myDetailsJson.has(JsonParseKeys.USER_IMAGE_URL)) {
                    myDetails.setUserImageUrl(myDetailsJson.getString(JsonParseKeys.USER_IMAGE_URL));
                }
                referralLeague.setMyDetail(myDetails);
            }
            if (object.has(JsonParseKeys.PRIZE_RULES)) {
                JsonValue jsonValue1 = object.get(JsonParseKeys.PRIZE_RULES);
                JsonValue.JsonIterator iterator = jsonValue1.iterator();
                while (iterator.hasNext()) {
                    JsonValue prizeRulesJson = iterator.next();
                    PrizeRules prizeRules = new PrizeRules();
                    prizeRules.setMaxRank(prizeRulesJson.getInt(JsonParseKeys.MAX_RANK));
                    prizeRules.setMinRank(prizeRulesJson.getInt(JsonParseKeys.MIN_RANK));
                    prizeRules.setPrize(prizeRulesJson.getInt(JsonParseKeys.PRIZE));
                    referralLeague.addPrizeRules(prizeRules);
                }
            }
            if (object.has(JsonParseKeys.GAME_RANKING_DETAILS)) {
                JsonValue jsonValue = object.get(JsonParseKeys.GAME_RANKING_DETAILS);
                JsonValue.JsonIterator iterator = jsonValue.iterator();
                while (iterator.hasNext()) {
                    JsonValue casualPlayerDetailJson = iterator.next();
                    ReferralPlayerDto referralPlayerDto = new ReferralPlayerDto();
                    referralPlayerDto.setUserId(casualPlayerDetailJson.getString(JsonParseKeys.USER_ID));
                    referralPlayerDto.setUserRank(casualPlayerDetailJson.getInt(JsonParseKeys.USER_RANK));
                    referralPlayerDto.setUserName(casualPlayerDetailJson.getString(JsonParseKeys.USER_NAME));
                    if (casualPlayerDetailJson.has(JsonParseKeys.WINNING_AMOUNT)) {
                        referralPlayerDto.setWinningAmount(casualPlayerDetailJson.getFloat(JsonParseKeys.WINNING_AMOUNT));
                    }
                    referralPlayerDto.setTotalPoint(casualPlayerDetailJson.getInt(JsonParseKeys.TOTAL_POINT));
                    if (casualPlayerDetailJson.has(JsonParseKeys.USER_IMAGE_URL)) {
                        referralPlayerDto.setUserImageUrl(casualPlayerDetailJson.getString(JsonParseKeys.USER_IMAGE_URL));
                    }
                    if (referralLeague.getMyDetail().getUserId().matches(referralPlayerDto.getUserId())) {
                        referralPlayerDto.setMyDetail(true);
                    } else {
                        referralPlayerDto.setMyDetail(false);
                    }

                    referralLeague.addReferralPlayerDtos(referralPlayerDto);
                }
            }
            Gdx.app.error("referralLeague", "Success");
            return referralLeague;
        } catch (Exception e) {
            Gdx.app.error("referralLeague", "erroe =>" + e.getMessage());
            return null;
        }
    }

    public static void callCasualLeaderBoardApi(GdxListener<CasualLeague> listener) {
        GamePreferences preference = GamePreferences.instance();
        HashMap<String, String> params = new HashMap();
        String userId = preference.getUserId();
        params.put("userId", userId);
        String transactionId = TimeUtils.millis() + "";
        params.put("otp", PokerUtils.encryptValue(transactionId, preference.getOtp()));
        params.put("transactionId", transactionId);
        params.put("gameType", "POKER");

        HttpRequestBuilder requestBuilder = new HttpRequestBuilder();
        Net.HttpRequest httpGet = requestBuilder.newRequest()
                .method(Net.HttpMethods.GET)
                .timeout(10000)
                .formEncodedContent(params)
                .url(BASE_URL + API_VERSION_1 + CASUAL_GAMES_LEADER_BOARD_API)
                .build();
        Gdx.net.sendHttpRequest(httpGet, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                int status = httpResponse.getStatus().getStatusCode();
                String resultString = httpResponse.getResultAsString();
                if (errorCodeHandling(status)) {
                    String result = "";
                    try {
                        JsonValue jsonValue = new JsonReader().parse(resultString);
                        result = PokerUtils.decompress(jsonValue.getString(JsonParseKeys.RESPONSE));
                    } catch (Exception e) {

                    }
                    listener.setSuccess(parseCasualLeagueResponse(result));
                } else {
                    if (status != 401) {
                        if (status == 204) {
                            listener.setSuccess(null);
                        } else if (status == 503) {
                            listener.setError(UNDER_MAINTENANCE);
                        } else {
                            listener.setError(SERVER_BUSY);
                        }
                    }
                }
            }

            @Override
            public void failed(Throwable t) {
                listener.setFail(NETWORK_ISSUE);
            }

            @Override
            public void cancelled() {
                listener.setFail(NETWORK_ISSUE);
            }

        });
    }

    private static CasualLeague parseCasualLeagueResponse(String response) {
        try {
            JsonValue object = new JsonReader().parse(response);
            CasualLeague casualLeague = new CasualLeague();
            casualLeague.setTotalWinningAmount(object.getFloat(JsonParseKeys.TOTAL_WINNING_AMOUNT));
            Constant.userProfile.setCasualWinnings(casualLeague.getTotalWinningAmount());
            if (object.has(JsonParseKeys.USER_RANKING_DETAILS)) {
                JsonValue myDetailsJson = object.get(JsonParseKeys.USER_RANKING_DETAILS);
                CasualPlayerDto myDetails = new CasualPlayerDto();
                myDetails.setUserId(myDetailsJson.getString(JsonParseKeys.USER_ID));
                myDetails.setUserRank(myDetailsJson.getInt(JsonParseKeys.USER_RANK));
                myDetails.setUserName(myDetailsJson.getString(JsonParseKeys.USER_NAME));
                myDetails.setWinningAmount(myDetailsJson.getFloat(JsonParseKeys.WINNING_AMOUNT));
                if (myDetailsJson.has(JsonParseKeys.USER_IMAGE_URL)) {
                    myDetails.setUserImageUrl(myDetailsJson.getString(JsonParseKeys.USER_IMAGE_URL));
                }
                casualLeague.setMyDetail(myDetails);
            }
            if (object.has(JsonParseKeys.GAME_RANKING_DETAILS)) {
                JsonValue jsonValue = object.get(JsonParseKeys.GAME_RANKING_DETAILS);
                JsonValue.JsonIterator iterator = jsonValue.iterator();
                while (iterator.hasNext()) {
                    JsonValue casualPlayerDetailJson = iterator.next();
                    CasualPlayerDto casualPlayerDto = new CasualPlayerDto();
                    casualPlayerDto.setUserId(casualPlayerDetailJson.getString(JsonParseKeys.USER_ID));
                    casualPlayerDto.setUserRank(casualPlayerDetailJson.getInt(JsonParseKeys.USER_RANK));
                    casualPlayerDto.setUserName(casualPlayerDetailJson.getString(JsonParseKeys.USER_NAME));
                    casualPlayerDto.setWinningAmount(casualPlayerDetailJson.getFloat(JsonParseKeys.WINNING_AMOUNT));
                    if (casualPlayerDetailJson.has(JsonParseKeys.USER_IMAGE_URL)) {
                        casualPlayerDto.setUserImageUrl(casualPlayerDetailJson.getString(JsonParseKeys.USER_IMAGE_URL));
                    }
                    if (casualLeague.getMyDetail().getUserRank() == casualPlayerDto.getUserRank()) {
                        casualPlayerDto.setMyDetail(true);
                    } else {
                        casualPlayerDto.setMyDetail(false);
                    }

                    casualLeague.addCasualPlayerDtos(casualPlayerDto);
                }
            }
            Gdx.app.error("casualLeague", "Success");
            return casualLeague;
        } catch (Exception e) {
            Gdx.app.error("casualLeague", "erroe =>" + e.getMessage());
            return null;
        }

    }


    public static void callRedeemConfigApi(final GdxListener<String> listener) {
        String transactionId = TimeUtils.millis() + "";
        GamePreferences preference = GamePreferences.instance();
        HashMap<String, String> params = new HashMap();
        params.put("userId", preference.getUserId());
        params.put("otp", PokerUtils.encryptValue(transactionId, preference.getOtp()));
        params.put("transactionId", transactionId);

        HttpRequestBuilder requestBuilder = new HttpRequestBuilder();
        Net.HttpRequest httpGet = requestBuilder.newRequest()
                .method(Net.HttpMethods.GET)
                .timeout(10000)
                .formEncodedContent(params)
                .url(BASE_URL + API_VERSION_4 + REDEEM_CONFIG_API)
                .build();

        Gdx.net.sendHttpRequest(httpGet, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                int status = httpResponse.getStatus().getStatusCode();
                String resultString = httpResponse.getResultAsString();
                if (errorCodeHandling(status)) {
                    parseRedeemConfigApi(resultString);
                    listener.setSuccess("");
                } else {
                    listener.setError(TRY_AGAIN);
                }
            }

            @Override
            public void failed(Throwable t) {
                listener.setFail(NETWORK_ISSUE);
            }

            @Override
            public void cancelled() {
                listener.setFail(NETWORK_ISSUE);
            }
        });
    }

    private static void parseRedeemConfigApi(String response) {
        JsonValue object = new JsonReader().parse(response);
        if (object.has(JsonParseKeys.DEPOSITED_BALANCE)) {
            Constant.userProfile.setDeposited(object.getFloat(JsonParseKeys.DEPOSITED_BALANCE));
        }
        if (object.has(JsonParseKeys.WITHDRAWABLE_BALANCE)) {
            Constant.userProfile.setWithdrawable(object.getFloat(JsonParseKeys.WITHDRAWABLE_BALANCE));
        }
        if (object.has(JsonParseKeys.REDEEM_VALUES)) {
            GamePreferences.instance().setRedeemValues(object.getString(JsonParseKeys.REDEEM_VALUES));
        }
        if (object.has("status")) {
            Constant.userProfile.setStatus(object.getString("status"));
        }
        if (object.has("errorText")) {
            Constant.userProfile.setRedeemErrorMessage(object.getString("errorText"));
        }
        if (object.has("dob")) {
            Constant.userProfile.setDob(object.getString("dob"));
        } else {
            Constant.userProfile.setDob(null);
        }
        if (object.has("name")) {
            Constant.userProfile.setFullName(object.getString("name"));
        } else {
            Constant.userProfile.setFullName(null);
        }
        if (object.has("panNumber")) {
            Constant.userProfile.setPanNumberConfig(object.getString("panNumber"));
        } else {
            Constant.userProfile.setPanNumberConfig(null);
        }
    }

    public static void callPaytmApi(String mobileNumber, final String paytmCash, String type, GdxListener<String> listener) {
        HashMap<String, String> params = new HashMap<String, String>();
        GamePreferences preference = GamePreferences.instance();
        String transactionId = TimeUtils.millis() + "";
        params.put("otp", PokerUtils.encryptValue(transactionId, preference.getOtp()));
        params.put("transactionId", transactionId);
        params.put("msisdn", mobileNumber);
        params.put("payTmCash", paytmCash + "");
        params.put("type", type);
        HttpRequestBuilder requestBuilder = new HttpRequestBuilder();
        Net.HttpRequest httpPost = requestBuilder.newRequest()
                .method(Net.HttpMethods.POST)
                .timeout(10000)
                .formEncodedContent(params)
                .url(BASE_URL + API_VERSION_3 + REDEEM_API + "?userId=" + preference.getUserId())
                .build();
        Gdx.net.sendHttpRequest(httpPost, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                String resultString = httpResponse.getResultAsString();
                int statusCode = httpResponse.getStatus().getStatusCode();
                if (errorCodeHandling(statusCode)) {
                    Constant.userProfile.setPaytmBalance(Constant.userProfile.getPaytmBalance() - Float.parseFloat(paytmCash));
                    Constant.RedeemMessage = parseRedeemResponse(resultString);
                    listener.setSuccess("Success");
                } else {
                    if (httpResponse.getStatus().getStatusCode() == 424) {
                        listener.setSuccess(resultString);
                    } else {
                        listener.setSuccess("");
                    }
                }
            }

            @Override
            public void failed(Throwable t) {
                listener.setFail("");
            }

            @Override
            public void cancelled() {
                listener.setError("Cancelled");
            }
        });
    }

    private static String parseRedeemResponse(String response) {
        String result = "";
        try {
            JsonValue jsonValue = new JsonReader().parse(response);
            result = jsonValue.getString(JsonParseKeys.REDEEM_RESPONSE);
        } catch (Exception e) {
            result = "";
        }
        return result;
    }

    public static void callOtpApi(String msisdn, GdxListener<String> listener) {
        String transactionId = TimeUtils.millis() + "";
        GamePreferences preference = GamePreferences.instance();
        HashMap<String, String> params = new HashMap();
        params.put("userId", preference.getUserId());
        params.put("otp", PokerUtils.encryptValue(transactionId, preference.getOtp()));
        params.put("msisdn", msisdn);
        params.put("transactionId", transactionId);

        HttpRequestBuilder requestBuilder = new HttpRequestBuilder();
        Net.HttpRequest httpGet = requestBuilder.newRequest()
                .method(Net.HttpMethods.GET)
                .timeout(10000)
                .formEncodedContent(params)
                .url(BASE_URL + API_VERSION_4 + OTP_API)
                .build();

        Gdx.net.sendHttpRequest(httpGet, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                int status = httpResponse.getStatus().getStatusCode();
                String resultString = httpResponse.getResultAsString();
                if (errorCodeHandling(status)) {
                    listener.setSuccess("Success");
                } else {
                    if (status == 424) {
                        listener.setSuccess(resultString);
                    } else {
                        listener.setSuccess("");
                    }
                }
            }

            @Override
            public void failed(Throwable t) {
                listener.setFail(t.getMessage());
            }

            @Override
            public void cancelled() {
                listener.setError("Cancelled");
            }
        });
    }

    public static void callWitzealMsisdnApi(String msisdn, String smsOtp, String smsSource, GdxListener<String> listener) {
        String transactionId = TimeUtils.millis() + "";
        GamePreferences preference = GamePreferences.instance();
        HashMap<String, String> params = new HashMap();
        params.put("otp", PokerUtils.encryptValue(transactionId, preference.getOtp()));
        params.put("msisdn", msisdn);
        params.put("smsOtp", PokerUtils.encryptValue(transactionId, smsOtp));
        params.put("smsSource", smsSource);
        params.put("transactionId", transactionId);
        HttpRequestBuilder requestBuilder = new HttpRequestBuilder();
        Net.HttpRequest httpPost = requestBuilder.newRequest()
                .method(Net.HttpMethods.POST)
                .timeout(10000)
                .formEncodedContent(params)
                .url(BASE_URL + API_VERSION_4 + WITZEAL_MSISDN_API + "?userId=" + preference.getUserId())
                .build();
        // Gdx.app.error("callWitzealMsisdnApi",params.toString());
        Gdx.net.sendHttpRequest(httpPost, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                int status = httpResponse.getStatus().getStatusCode();
                String resultString = httpResponse.getResultAsString();
                //  Gdx.app.error("callWitzealMsisdnApi",response.getErrorCode()+"-"+response.getResult());
                if (errorCodeHandling(status)) {
                    parseProfileApi(resultString);
                    listener.setSuccess("Success");
                } else {
                    if (status == 424) {
                        listener.setSuccess(resultString);
                    } else {
                        listener.setSuccess("");
                    }
                }
            }

            @Override
            public void failed(Throwable t) {
                listener.setFail("");
            }

            @Override
            public void cancelled() {
                listener.setError("Cancelled");
            }
        });
    }

    public static void callAccountSummaryApi(long minId, final GdxListener<String> listener) {
        String transactionId = TimeUtils.millis() + "";
        GamePreferences preference = GamePreferences.instance();
        HashMap<String, String> params = new HashMap();
        params.put("userId", preference.getUserId());
        params.put("otp", PokerUtils.encryptValue(transactionId, preference.getOtp()));
        params.put("transactionId", transactionId);
        if (minId != 0) {
            params.put("minId", minId + "");
        }
        Gdx.app.error("Account Summary request", params.toString());

        HttpRequestBuilder requestBuilder = new HttpRequestBuilder();
        Net.HttpRequest httpGet = requestBuilder.newRequest()
                .method(Net.HttpMethods.GET)
                .timeout(10000)
                .formEncodedContent(params)
                .url(BASE_URL + API_VERSION_9 + ACCOUNT_SUMMARY_API)
                .build();

        Gdx.net.sendHttpRequest(httpGet, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                Response response = new Response();
                response.setResult(httpResponse.getResultAsString());
                response.setErrorCode(httpResponse.getStatus().getStatusCode());
                Gdx.app.error("Account Summary Api", response.getResult());
                if (errorCodeHandling(response.getErrorCode())) {
                    parseAccountSummaryResponse(response.getResult());
                    listener.setSuccess("Success");
                } else {
                    listener.setSuccess("");
                }
            }

            @Override
            public void failed(Throwable t) {
                listener.setFail(t.getMessage());
            }

            @Override
            public void cancelled() {
                listener.setError("Cancelled");
            }
        });
    }

    private static void parseAccountSummaryResponse(String response) {
        try {
            JsonValue object = new JsonReader().parse(response);
            Array<AccountSummary> array = new Array<AccountSummary>();
            Constant.minId = 0;
            JsonValue.JsonIterator iterator = object.iterator();
            while (iterator.hasNext()) {
                JsonValue jsonValue = iterator.next();
                AccountSummary accountSummary = new AccountSummary();
                accountSummary.setAmount(jsonValue.getString(JsonParseKeys.AMOUNT));
                accountSummary.setId(jsonValue.getLong(JsonParseKeys.ID));
                accountSummary.setCreatedTime(jsonValue.getString(JsonParseKeys.CREATED_TIME));
                accountSummary.setOfferName(jsonValue.getString(JsonParseKeys.OFFER_NAME));
                if (Constant.minId > accountSummary.getId() || Constant.minId == 0) {
                    Constant.minId = accountSummary.getId();
                }
                array.add(accountSummary);
            }
            Constant.arrAccountSummary = array;
        } catch (Exception e) {
//            Gdx.app.error("Account Summary Api", "error");
        }
    }

    public static void callSettingApi(boolean notificationSound, boolean gameSound, boolean vibration) {
        String transactionId = TimeUtils.millis() + "";
        GamePreferences preference = GamePreferences.instance();
        HashMap<String, String> params = new HashMap();
        params.put("otp", PokerUtils.encryptValue(transactionId, preference.getOtp()));
        params.put("transactionId", transactionId);
        params.put("notificationSound", notificationSound + "");
        params.put("gameSound", gameSound + "");
        params.put("vibration", vibration + "");

        HttpRequestBuilder requestBuilder = new HttpRequestBuilder();
        Net.HttpRequest httpPost = requestBuilder.newRequest()
                .method(Net.HttpMethods.POST)
                .timeout(10000)
                .formEncodedContent(params)
                .url(BASE_URL + API_VERSION_4 + SETTING_API + "?userId=" + preference.getUserId())
                .build();

        Gdx.net.sendHttpRequest(httpPost, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                Response response = new Response();
                response.setResult(httpResponse.getResultAsString());
                response.setErrorCode(httpResponse.getStatus().getStatusCode());
                if (errorCodeHandling(response.getErrorCode())) {
                }
            }

            @Override
            public void failed(Throwable t) {
            }

            @Override
            public void cancelled() {
            }
        });

    }

    public static void callPrivacyPolicyApi(String type, GdxListener<String> listener) {
        HashMap<String, String> params = new HashMap();
        GamePreferences preference = GamePreferences.instance();
        params.put("userId", preference.getUserId());
        params.put("policyType", type);

        HttpRequestBuilder requestBuilder = new HttpRequestBuilder();
        Net.HttpRequest httpGet = requestBuilder.newRequest()
                .method(Net.HttpMethods.GET)
                .timeout(10000)
                .formEncodedContent(params)
                .url(BASE_URL + API_VERSION_1 + POLICY_API)
                .build();

        Gdx.net.sendHttpRequest(httpGet, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                Response response = new Response();
                response.setResult(httpResponse.getResultAsString());
                response.setErrorCode(httpResponse.getStatus().getStatusCode());
                //    Gdx.app.error("Policy Api",response.getErrorCode()+"-"+response.getResult());
                if (errorCodeHandling(response.getErrorCode())) {
                    listener.setSuccess(parsePolicyApiResponse(response.getResult()));
                } else {
                    listener.setError("Error");
                }
            }

            @Override
            public void failed(Throwable t) {
                listener.setError("Error");
            }

            @Override
            public void cancelled() {
                listener.setError("Error");
            }
        });
    }

    private static String parsePolicyApiResponse(String response) {
        String result = "";
        try {
            JsonValue jsonValue = new JsonReader().parse(response);
            result = jsonValue.asString();
        } catch (Exception e) {

        }
        return result;
    }

    public static void callFeedbackApi(String feedback, String msisdn, final GdxListener<String> listener) {

        GamePreferences preference = GamePreferences.instance();
        HashMap<String, String> params = new HashMap();
        params.put("msisdn", msisdn);
        params.put("feedback", feedback);


        HttpRequestBuilder requestBuilder = new HttpRequestBuilder();
        Net.HttpRequest httpPost = requestBuilder.newRequest()
                .method(Net.HttpMethods.POST)
                .timeout(10000)
                .formEncodedContent(params)
                .url(BASE_URL + API_VERSION_1 + FEEDBACK_API + "?userId=" + preference.getUserId())
                .build();

        Gdx.net.sendHttpRequest(httpPost, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                Response response = new Response();
                response.setResult(httpResponse.getResultAsString());
                response.setErrorCode(httpResponse.getStatus().getStatusCode());
                //    Gdx.app.error("Policy Api",response.getErrorCode()+"-"+response.getResult());
                if (errorCodeHandling(response.getErrorCode())) {
                    listener.setSuccess("Success");
                } else {
                    listener.setSuccess("");
                }
            }

            @Override
            public void failed(Throwable t) {
                listener.setError("Error");
            }

            @Override
            public void cancelled() {
                listener.setError("Error");
            }
        });
    }


    private static boolean errorCodeHandlingForSplashScreen(int code) {
        if (code == 200) {
            return true;
        } else if (code == 500 || code == 226) {
            return false;
        } else if (code == 503) {
            openServerMaintenance(true);
            return false;
        } else if (code == 401) {
            PokerGame.resetAppWhenErrorGet();
            return false;
        } else if (code == 406) {
            return false;
        } else {
            return false;
        }

    }

    private static boolean errorCodeHandling(int code) {
        if (code == 200) {
            return true;
        } else if (code == 500 || code == 226) {
            return false;
        } else if (code == 503) {
            openServerMaintenance(false);
            return false;
        } else if (code == 401) {
            PokerGame.resetAppWhenErrorGet();
            return false;
        } else if (code == 406) {
            return false;
        } else {
            return false;
        }
    }

    private static void openServerMaintenance(boolean isSplash) {
        if (!Constant.isSetMaintenanceInThisSession) {
            Constant.isSetMaintenanceInThisSession = true;
            Gdx.app.postRunnable(new Runnable() {
                @Override
                public void run() {
                    PokerGame pokerGame = (PokerGame) Gdx.app.getApplicationListener();
                    if (isSplash) {
                        UIScreen screen = (UIScreen) pokerGame.getScreen();
                        new MaintenanceDialog(screen) {
                            @Override
                            public void hide() {
                                super.hide();
                                Constant.isSetMaintenanceInThisSession = false;
                            }
                        }.show(screen.stage);
                    } else {
                        pokerGame.setScreen(new PokerContestScreen(pokerGame) {
                            @Override
                            public void show() {
                                super.show();
                                new MaintenanceDialog(this) {
                                    @Override
                                    public void hide() {
                                        super.hide();
                                        Constant.isSetMaintenanceInThisSession = false;
                                    }
                                }.show(stage);
                            }
                        });
                    }
                }
            });
        }
    }


    public static void printLog(String log) {
        HttpRequestBuilder requestBuilder = new HttpRequestBuilder();
        Net.HttpRequest httpPost = requestBuilder.newRequest()
                .method(Net.HttpMethods.POST)
                .timeout(10000)
                .header(HttpRequestHeader.ContentType, "application/json")
                .content(log)
                .url(GWT.getHostPageBaseURL() + "log")
                .build();

        Gdx.net.sendHttpRequest(httpPost, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                Gdx.app.error("Log Response", httpResponse.getStatus().getStatusCode() + "");
            }

            @Override
            public void failed(Throwable t) {
            }

            @Override
            public void cancelled() {
            }
        });
    }


}
