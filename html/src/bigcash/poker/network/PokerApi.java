package bigcash.poker.network;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.HttpRequestBuilder;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.HashMap;

import bigcash.poker.constants.Constant;
import bigcash.poker.dialogs.MaintenanceDialog;
import bigcash.poker.gwt.PokerGame;
import bigcash.poker.models.PokerContest;
import bigcash.poker.models.QRContests;
import bigcash.poker.models.QrInfo;
import bigcash.poker.screens.PokerContestScreen;
import bigcash.poker.utils.GamePreferences;
import bigcash.poker.utils.GeolocationPosition;
import bigcash.poker.utils.JsonParseKeys;
import bigcash.poker.utils.PokerConstants;
import bigcash.poker.utils.PokerUtils;

public class PokerApi {
    public static final String POKER_BASE_URL = "https://test1.bigcash.live/bulbsmashpro/api";
//        public static final String POKER_BASE_URL = "https://poker.bigcash.live/bulbsmashpro/api";
    public static final String POKER_LIVE_API = "/poker/contests/live";
    public static final String POKER_USER_LOCATION_API = "/poker/set/userLocation";
    public static final String POKER_REFILL_POKER_BALANCE = "/poker/refill/pokerBalance";
    public static final String QR_INFO = "/qr/getinfo";

    public static final String VERSION_1 = "/v1";

    public static final String NETWORK_ISSUE = "Please check your internet connection.";
    public static final String TRY_AGAIN = "Try Again..";
    public static final String SUCCESS = "Success";
    public static final String SERVER_BUSY = "Server Busy..";
    public static final String NO_CONTENT = "No live tournaments..";
    public static final String UNDER_MAINTENANCE = "Under maintenance.";
    public static final String NOT_ACCEPTABLE = "Not Acceptable.";
    public static final String ALREADY_REGISTERED = "Already registered.";
    public static final String FORBIDDEN = "Already Full.";

    public static Array<PokerContest> pokerContestMax2 = new Array<PokerContest>();
    public static Array<PokerContest> pokerContestMax5 = new Array<PokerContest>();
    private static boolean useLocation;
    private static boolean fetchFromGpsLocation;
    private static boolean fetchFromGeoLocation;
    private static boolean showBestPossibleTips;
    private static PokerApi instance;

    public static boolean isUseLocation() {
        return useLocation;
    }

    public static boolean isFetchFromGpsLocation() {
        return fetchFromGpsLocation;
    }

    public static boolean isFetchFromGeoLocation() {
        return fetchFromGeoLocation;
    }

    public static boolean isShowBestPossibleTips() {
        return showBestPossibleTips;
    }

    public static void callPokerContestApi(GeolocationPosition position, String pokerType, final GdxListener<String> listener) {
        String transactionId = TimeUtils.millis() + "";
        GamePreferences preference = GamePreferences.instance();
        HashMap<String, String> params = new HashMap();
        params.put("userId", preference.getUserId());
        params.put("otp", PokerUtils.encryptValue(transactionId, preference.getOtp()));
        if (position == null) {
            params.put("gpsLatitude", "0");
            params.put("gpsLongitude", "0");
            params.put("isFromMock", "false");

        } else {
            params.put("gpsLatitude", position.getLatitude() + "");
            params.put("gpsLongitude", position.getLongitude() + "");
            params.put("isFromMock", "false");
        }
        params.put("transactionId", transactionId);
        params.put("pokerType", pokerType);

        HttpRequestBuilder requestBuilder = new HttpRequestBuilder();
        Net.HttpRequest httpGet = requestBuilder.newRequest()
                .method(Net.HttpMethods.GET)
                .timeout(10000)
                .formEncodedContent(params)
                .url(POKER_BASE_URL + VERSION_1 + POKER_LIVE_API)
                .build();

        Gdx.app.log("Params", params.toString());

        Gdx.net.sendHttpRequest(httpGet, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                int statusCode = httpResponse.getStatus().getStatusCode();
                String resultString = httpResponse.getResultAsString();
                if (errorCodeHandling(statusCode)) {
                    String result = "";
                    try {
                        JsonValue jsonValue = new JsonReader().parse(resultString);
                        result = PokerUtils.decompress(jsonValue.getString("response"));
                        String emojis = jsonValue.getString("emojis", "");
                        if (emojis.isEmpty()) {
                            Constant.emojis = null;
                            GamePreferences.instance().setEmojiString("");
                        } else {
                            GamePreferences.instance().setEmojiString(emojis);
                            Constant.emojis = emojis.split("\\,");
                        }
                    } catch (Exception e) {
                    }
                    parsePokerContestApi(result);
                    listener.setSuccess(SUCCESS);
                } else {
                    if (statusCode == 204) {
                        listener.setError(SERVER_BUSY);
                    } else if (statusCode == 406) {
                        listener.setError(UNDER_MAINTENANCE);
                    } else {
                        if (statusCode != 401)
                            listener.setError(SERVER_BUSY);
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



    private static void parsePokerContestApi(String result) {
        Gdx.app.error("ContestApi", result);
        pokerContestMax2.clear();
        pokerContestMax5.clear();
        JsonValue resultJson = new JsonReader().parse(result);
        JsonValue jsonValue = resultJson.get(JsonParseKeys.CONTEST_DTOS);
        fetchFromGeoLocation = resultJson.getBoolean(JsonParseKeys.FETCH_FROM_GEO_LOCATION);
        fetchFromGpsLocation = resultJson.getBoolean(JsonParseKeys.FETCH_FROM_GPS_LOCATION);
        if (resultJson.has(JsonParseKeys.SHOW_BEST_POSSIBLE_TIPS)) {
            showBestPossibleTips = resultJson.getBoolean(JsonParseKeys.SHOW_BEST_POSSIBLE_TIPS);
        }
        if (resultJson.has(JsonParseKeys.PAYTM_BALANCE)) {
            Constant.userProfile.setPaytmBalance(resultJson.getFloat(JsonParseKeys.PAYTM_BALANCE));
        }
        useLocation = resultJson.getBoolean(JsonParseKeys.USE_LOCATION);
        if (resultJson.has(JsonParseKeys.POKER_TURN_TIME)) {
            PokerConstants.TURN_TIME = resultJson.getInt(JsonParseKeys.POKER_TURN_TIME);
        }
        JsonValue.JsonIterator iterator = jsonValue.iterator();
        while (iterator.hasNext()) {
            PokerContest pokerContest = new PokerContest(iterator.next());
            if (pokerContest.getMaxUsersPerTable() == 2) {
                pokerContestMax2.add(pokerContest);
            } else {
                pokerContestMax5.add(pokerContest);
            }
        }
    }

    public static void callPrivateContestApi(GeolocationPosition position,int qrContestId, final GdxListener<QRContests> listener) {
        String transactionId = TimeUtils.millis() + "";
        GamePreferences preference = GamePreferences.instance();
        HashMap<String, String> params = new HashMap();
        params.put("userId", preference.getUserId());
        params.put("otp", PokerUtils.encryptValue(transactionId, preference.getOtp()));
        if (position == null) {
            params.put("gpsLatitude", "0");
            params.put("gpsLongitude", "0");
            params.put("isFromMock", "false");

        } else {
            params.put("gpsLatitude", position.getLatitude() + "");
            params.put("gpsLongitude", position.getLongitude() + "");
            params.put("isFromMock", "false");
        }
        params.put("transactionId", transactionId);
        params.put("pokerType", "QR_NL_HOLDEM");

        HttpRequestBuilder requestBuilder = new HttpRequestBuilder();
        Net.HttpRequest httpGet = requestBuilder.newRequest()
                .method(Net.HttpMethods.GET)
                .timeout(10000)
                .formEncodedContent(params)
                .url(POKER_BASE_URL + VERSION_1 + POKER_LIVE_API)
                .build();

        Gdx.app.log("Params", params.toString());

        Gdx.net.sendHttpRequest(httpGet, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                int statusCode = httpResponse.getStatus().getStatusCode();
                String resultString = httpResponse.getResultAsString();
                if (errorCodeHandling(statusCode)) {
                    String result = "";
                    try {
                        JsonValue jsonValue = new JsonReader().parse(resultString);
                        result = PokerUtils.decompress(jsonValue.getString("response"));
                        String emojis = jsonValue.getString("emojis", "");
                        if (emojis.isEmpty()) {
                            Constant.emojis = null;
                            GamePreferences.instance().setEmojiString("");
                        } else {
                            GamePreferences.instance().setEmojiString(emojis);
                            Constant.emojis = emojis.split("\\,");
                        }
                    } catch (Exception e) {
                    }
                    listener.setSuccess(parseQrContests(result,qrContestId));
                } else {
                    if (statusCode == 204) {
                        listener.setError(SERVER_BUSY);
                    } else if (statusCode == 406) {
                        listener.setError(UNDER_MAINTENANCE);
                    } else {
                        if (statusCode != 401)
                            listener.setError(SERVER_BUSY);
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

    private static QRContests parseQrContests(String result,int qrContestId) {
        Gdx.app.error("ContestApi", result);
        QRContests qrContests=new QRContests(qrContestId);
        JsonValue resultJson = new JsonReader().parse(result);
        JsonValue jsonValue = resultJson.get(JsonParseKeys.CONTEST_DTOS);
        fetchFromGeoLocation = resultJson.getBoolean(JsonParseKeys.FETCH_FROM_GEO_LOCATION);
        fetchFromGpsLocation = resultJson.getBoolean(JsonParseKeys.FETCH_FROM_GPS_LOCATION);
        if (resultJson.has(JsonParseKeys.SHOW_BEST_POSSIBLE_TIPS)) {
            showBestPossibleTips = resultJson.getBoolean(JsonParseKeys.SHOW_BEST_POSSIBLE_TIPS);
        }
        if (resultJson.has(JsonParseKeys.PAYTM_BALANCE)) {
            Constant.userProfile.setPaytmBalance(resultJson.getFloat(JsonParseKeys.PAYTM_BALANCE));
        }
        useLocation = resultJson.getBoolean(JsonParseKeys.USE_LOCATION);
        if (resultJson.has(JsonParseKeys.POKER_TURN_TIME)) {
            PokerConstants.TURN_TIME = resultJson.getInt(JsonParseKeys.POKER_TURN_TIME);
        }
        JsonValue.JsonIterator iterator = jsonValue.iterator();
        while (iterator.hasNext()) {
            PokerContest pokerContest = new PokerContest(iterator.next());
            qrContests.addContest(pokerContest);
        }
        return qrContests;
    }

    public static void callUserLocationApi(GeolocationPosition position, final GdxListener<String> listener) {
        String transactionId = TimeUtils.millis() + "";
        GamePreferences preference = GamePreferences.instance();
        HashMap<String, String> params = new HashMap();
        params.put("userId", preference.getUserId());
        params.put("otp", PokerUtils.encryptValue(transactionId, preference.getOtp()));

        params.put("gpsLatitude", "0");
        params.put("gpsLongitude", "0");
        params.put("isFromMock", false + "");

        if (position == null) {
            params.put("geoLatitude", "0");
            params.put("geoLongitude", "0");
            params.put("accuracy", "0");
        } else {
            params.put("geoLatitude", position.getLatitude());
            params.put("geoLongitude", position.getLongitude());
            Double accuracy = Double.parseDouble(position.getAccuracy());
            params.put("accuracy", accuracy.intValue() + "");
        }

        params.put("transactionId", transactionId);
        params.put("isMockAppRunning", "false");
//        String mockApps = proGame.config.getMockLocationApps();
//        if (mockApps != null && !mockApps.isEmpty()) {
//            params.put("mockApps", mockApps);
//        }

        Gdx.app.log("Locations Params", params.toString());

        HttpRequestBuilder requestBuilder = new HttpRequestBuilder();
        Net.HttpRequest httpPost = requestBuilder.newRequest()
                .method(Net.HttpMethods.POST)
                .timeout(10000)
                .formEncodedContent(params)
                .url(POKER_BASE_URL + VERSION_1 + POKER_USER_LOCATION_API)
                .build();

        Gdx.net.sendHttpRequest(httpPost, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                int statusCode = httpResponse.getStatus().getStatusCode();
                String resultString = httpResponse.getResultAsString();
                if (errorCodeHandling(statusCode)) {
                    listener.setSuccess(resultString);
                } else {
                    if (statusCode != 401)
                        listener.setError(statusCode + "");
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

    public static void callRefillApi(String contestId, String amount, final GdxListener<String> listener) {
        String transactionId = TimeUtils.millis() + "";
        GamePreferences preference = GamePreferences.instance();
        HashMap<String, String> params = new HashMap();
        params.put("userId", preference.getUserId());
        params.put("otp", PokerUtils.encryptValue(transactionId, preference.getOtp()));
        params.put("transactionId", transactionId);
        params.put("contestId", contestId);
        params.put("amount", amount);

        HttpRequestBuilder requestBuilder = new HttpRequestBuilder();
        Net.HttpRequest httpPost = requestBuilder.newRequest()
                .method(Net.HttpMethods.POST)
                .timeout(10000)
                .formEncodedContent(params)
                .url(POKER_BASE_URL + VERSION_1 + POKER_REFILL_POKER_BALANCE)
                .build();

        Gdx.net.sendHttpRequest(httpPost, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                int statusCode = httpResponse.getStatus().getStatusCode();
                String resultString = httpResponse.getResultAsString();
                if (errorCodeHandling(statusCode)) {
                    JsonValue jsonValue = new JsonReader().parse(resultString);
                    Constant.userProfile.setPaytmBalance(jsonValue.getFloat("balance", Constant.userProfile.getPaytmBalance()));
                    Constant.userProfile.setPokerBalance(jsonValue.getFloat("pokerBalance", Constant.userProfile.getPokerBalance()));
                    listener.setSuccess(resultString);
                } else {
                    if (statusCode != 401)
                        listener.setError(statusCode + "");
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

    public static void getQRInfo(String qrId, GdxListener<QrInfo> listener){
        String transactionId = TimeUtils.millis() + "";
        GamePreferences preference = GamePreferences.instance();
        HashMap<String, String> params = new HashMap();
        params.put("userId", preference.getUserId());
        params.put("otp", PokerUtils.encryptValue(transactionId, preference.getOtp()));
        params.put("transactionId", transactionId);
        params.put("qrId", qrId);

        Gdx.app.error("QR Params",params.toString());

        HttpRequestBuilder requestBuilder = new HttpRequestBuilder();
        Net.HttpRequest httpPost = requestBuilder.newRequest()
                .method(Net.HttpMethods.POST)
                .timeout(10000)
                .formEncodedContent(params)
                .url(POKER_BASE_URL + VERSION_1 + QR_INFO)
                .build();

        Gdx.net.sendHttpRequest(httpPost, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                int statusCode = httpResponse.getStatus().getStatusCode();
                String resultString = httpResponse.getResultAsString();
                Gdx.app.error("QR Info0",resultString);
                if (errorCodeHandling(statusCode)) {
                    if (resultString!=null && !resultString.isEmpty()) {
                        listener.setSuccess(new QrInfo(qrId, resultString));
                    }else {
                        listener.setError("");
                    }
                } else {
                    if (statusCode != 401)
                        listener.setError(statusCode + "");
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


    public static boolean errorCodeHandling(int code) {
        if (code == 200) {
            return true;
        } else if (code == 500 || code == 226) {
            return false;
        } else if (code == 503) {
            openServerMaintenance();
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

    private static void openServerMaintenance() {
        if (!Constant.isSetMaintenanceInThisSession) {
            Constant.isSetMaintenanceInThisSession = true;
            Gdx.app.postRunnable(new Runnable() {
                @Override
                public void run() {
                    Constant.isShowMaintenance = true;
                    PokerGame pokerGame=(PokerGame) Gdx.app.getApplicationListener();
                    pokerGame.setScreen(new PokerContestScreen(pokerGame){
                        @Override
                        public void show() {
                            super.show();
                            new MaintenanceDialog(this){
                                @Override
                                public void hide() {
                                    super.hide();
                                    Constant.isSetMaintenanceInThisSession = false;
                                }
                            }.show(stage);
                        }
                    });
                }
            });
        }
    }
}
