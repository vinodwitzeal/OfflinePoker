package bigcash.poker.game.poker;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bigcash.poker.appwarp.AppWarp;
import bigcash.poker.appwarp.WarpClient;
import bigcash.poker.appwarp.WarpResponseListener;
import bigcash.poker.appwarp.codes.WarpResponseResultCode;
import bigcash.poker.appwarp.events.ChatEvent;
import bigcash.poker.appwarp.events.LiveRoomInfoEvent;
import bigcash.poker.appwarp.events.LiveUserInfoEvent;
import bigcash.poker.appwarp.events.MatchedRoomEvent;
import bigcash.poker.appwarp.events.MoveEvent;
import bigcash.poker.appwarp.events.RoomData;
import bigcash.poker.appwarp.events.RoomEvent;
import bigcash.poker.constants.Constant;
import bigcash.poker.game.poker.messages.PokerMessage;
import bigcash.poker.gwt.PokerGame;
import bigcash.poker.models.PokerContest;
import bigcash.poker.network.ApiHandler;
import bigcash.poker.network.GdxListener;
import bigcash.poker.utils.GamePreferences;
import bigcash.poker.utils.GeolocationPosition;
import bigcash.poker.utils.PokerConstants;
import bigcash.poker.utils.PokerInterval;
import bigcash.poker.utils.PokerUtils;
import bigcash.poker.utils.TimeoutHandler;

public abstract class PokerWarpController implements WarpResponseListener {
    private static final String API_KEY = "c11aca99-2eb8-4419-9", API_ADDRESS = "192.168.1.105";
    public final int REST = 0, CONNECTING = 1, RECONNECT = 2, RECOVER_SESSION = 3, RECOVER_CONNECTION = 4;

    public final int CONNECTED = 11, SESSION_RECOVERED = 22, CONNECTION_RECOVERED = 33;
    public int recoverAttempts;
    public WarpClient client;
    public int requestState, responseState, reconnectAttempts;
    public String roomId, myID, opponentId;
    public AssetManager manager;
    private boolean connected, isLimitedAccess, opponentOffline;
    public PokerGame pokerGame;
    private int turnTime;
    private Map<String, MessageListener> messageListenerHashMap = new HashMap<String, MessageListener>();
    public int contestId;
    public float bettingAmount;
    private String latitude, longitude;
    private PokerContest contest;
    private int maxUsers;
    private float smallBlind, bigBlind;
    private float minAddAmount, maxAddAmount;

    private int gettingRoomWithProperty = 0, matchingTry = 0;
    private boolean isRoomJoinAfterLowBalance;
    public boolean isUserSwitching;
    public List<String> listGetRoomWithProperty;
    public List<String> listPreviousJoinRoom;
    //    private HashMap<String, String> matchProperties, matchPropertiesForRoomArray;
    private JsonValue matchProperties, matchPropertiesForRoom;
    private PokerWarpListener listener;

    public PokerWarpController(PokerGame proGame, GeolocationPosition position, PokerContest contest) {
        this.pokerGame = proGame;
        this.requestState = REST;
        this.responseState = REST;
        this.reconnectAttempts = 5;
        this.myID = Constant.userProfile.getUserId();
        if (position == null) {
            this.latitude = "0";
            this.longitude = "0";
        } else {
            this.latitude = position.getLatitude();
            this.longitude = position.getLongitude();
        }

        this.contest = contest;
        if (this.contest != null) {
            this.contestId = this.contest.getContestId();
            this.maxUsers = this.contest.getMaxUsersPerTable();
            this.smallBlind = this.contest.getBetValue();
            this.bigBlind = this.contest.getBetValue() * 2;
            this.minAddAmount = this.contest.getMinJoiningFee();
            this.maxAddAmount = this.contest.getMaxJoiningFee();
        }
        turnTime = PokerConstants.TURN_TIME + PokerConstants.BUFFER_TIME;
        this.opponentOffline = false;
        GamePreferences preferences = GamePreferences.instance();
        //Test
        AppWarp.initializeSecure(preferences.getPokerKey(), "poker-web-prod.bigcashlive.com");
//        Production
//        AppWarp.initializeSecure(preferences.getPokerKey(), "prod-web-poker.bigcash.live");

        //Local
//        AppWarp.initialize("2b38a38e-5714-490c-a","192.168.1.30");
        client = AppWarp.getInstance();
        client.resetListeners();
        client.setListeners(this);
    }


    public float getMaxAddAmount() {
        return maxAddAmount;
    }

    public void setMaxAddAmount(float maxAddAmount) {
        this.maxAddAmount = maxAddAmount;
    }

    public float getMinAddAmount() {
        return minAddAmount;
    }

    public void setMinAddAmount(float minAddAmount) {
        this.minAddAmount = minAddAmount;
    }

    public int getMaxUsers() {
        return maxUsers;
    }

    public void setMaxUsers(int maxUsers) {
        this.maxUsers = maxUsers;
    }

    public void setContestId(int contestId) {
        this.contestId = contestId;
    }

    public int getContestId() {
        return this.contestId;
    }

    public float getSmallBlind() {
        return smallBlind;
    }

    public float getBigBlind() {
        return bigBlind;
    }

    private String getRoomProperties() {
//        return PokerUtils.getRoomProperties(contestId + "", PokerConstants.FRESH_ROOM_STATUS, smallBlind + "", bigBlind + "", minAddAmount + "", maxAddAmount + "");
        JsonValue properties = new JsonValue(JsonValue.ValueType.object);
        properties.addChild("variant", new JsonValue(contestId));
        properties.addChild(PokerConstants.KEY_ROOM_STATUS, new JsonValue("FRESH"));
        properties.addChild("smallBlind", new JsonValue(smallBlind + ""));
        properties.addChild("bigBlind", new JsonValue(bigBlind + ""));
        properties.addChild("minAddAmount", new JsonValue(minAddAmount + ""));
        properties.addChild("maxAddAmount", new JsonValue(maxAddAmount + ""));
        addGameProperties(properties);
        return properties.toJson(JsonWriter.OutputType.json);
    }

    public abstract void addGameProperties(JsonValue properties);

    public void setListener(PokerWarpListener l) {
        listener = l;
    }

    private String getAuthData() {
        JsonValue authData = new JsonValue(JsonValue.ValueType.object);
        JsonValue details = new JsonValue(JsonValue.ValueType.object);
        details.addChild("name", new JsonValue(Constant.userProfile.getName()));
        details.addChild("id", new JsonValue(Constant.userProfile.getUserId()));
        details.addChild("imageId", new JsonValue(Constant.userProfile.getImageId()));
        details.addChild("pokerBalance", new JsonValue(Constant.userProfile.getPokerBalance()));
        details.addChild("balance", new JsonValue(Constant.userProfile.getPaytmBalance()));
        details.addChild("variant", new JsonValue(contestId));
        if (latitude != null && longitude != null) {
            JsonValue position = new JsonValue(JsonValue.ValueType.object);
            position.addChild("lat", new JsonValue(latitude));
            position.addChild("lng", new JsonValue(longitude));
            details.addChild("location", position);
        }
        authData.addChild("otp", new JsonValue(GamePreferences.instance().getOtp()));
        authData.addChild("data", new JsonValue(details.toJson(JsonWriter.OutputType.json)));
        authData.addChild("variant", new JsonValue(contestId));
        return authData.toJson(JsonWriter.OutputType.json);
    }

    public void connect() {
        if (client.isConnected()) {
            requestState = RECONNECT;
            connected = true;
            client.disconnect();
        } else {
            requestState = CONNECTING;
            connected = false;
            client.connect(myID, getAuthData());
        }
    }

    public void disconnect() {
        client.disconnect();
    }

    public void recoverGame(PokerWarpListener l) {
        listener = l;
        requestState = RECOVER_SESSION;
        //TODO Session Id
//        client.recoverConnectionWithSessionId(preferences.getPokerSession(), myID);
    }

    @Override
    public void onConnectDone(int result) {
        switch (result) {
            case WarpResponseResultCode.SUCCESS:
            case WarpResponseResultCode.SUCCESS_RECOVERED:
                onConnected();
                reconnectAttempts = 5;
                break;
            case WarpResponseResultCode.CONNECTION_ERROR_RECOVERABLE:
                connected = false;
                onRecoverConnectionError();
                break;
            case WarpResponseResultCode.AUTH_ERROR:
            case WarpResponseResultCode.BAD_REQUEST:
                connected = false;
                onConnectionFailed();
                break;
            default:
                connected = false;
                onConnectionError();
                break;
        }
    }

    private void onConnected() {
        connected = true;
        switch (requestState) {
            case RECOVER_SESSION:
                responseState = SESSION_RECOVERED;
                onSessionRecovered();
                requestState = CONNECTING;
                break;
            case RECOVER_CONNECTION:
                responseState = CONNECTION_RECOVERED;
                onConnectionRecovered();
                break;
            default:
                responseState = CONNECTED;
                onSuccessfullyConnected();
                break;
        }
    }

    private void onConnectionError() {
        if (requestState == RECOVER_SESSION || requestState == RECOVER_CONNECTION) {
            onConnectionFailed();
        } else {
            reconnectAttempts--;
            if (reconnectAttempts > 0) {
                PokerUtils.setTimeOut(2000, new TimeoutHandler() {
                    @Override
                    public void onTimeOut() {
                        client.connect(myID, getAuthData());
                    }
                });
            } else {
                onConnectionFailed();
            }
        }
    }

    private void onSuccessfullyConnected() {
        if (listener != null) {
            listener.onConnected();
        }
    }

    private void onSessionRecovered() {
        if (listener != null) {
            listener.onSessionRecovered();
        }
    }


    @Override
    public void onGetLiveUserInfoDone(LiveUserInfoEvent liveUserInfoEvent) {
        if (listener == null) return;
        if (liveUserInfoEvent.getResult() == WarpResponseResultCode.SUCCESS) {
            listener.onGetLiveUserInfo(liveUserInfoEvent);
        }
    }


    private void onConnectionRecovered() {
        if (listener != null) {
            listener.onConnectionRecovered();
        }
    }

    private void onConnectionFailed() {
        client.resetListeners();
        if (requestState == RECOVER_SESSION) {
            if (listener != null) {
                listener.onConnectionFailed();
            }
        } else {
            if (listener != null) {
                listener.onConnectionFailed();
            }
        }
    }


    private void onRecoverConnectionError() {
        if (requestState == RECOVER_SESSION) {
            onConnectionFailed();
        } else {
            requestState = RECOVER_CONNECTION;
            if (listener != null) {
                listener.onRecoverConnection();
            }
        }
    }

    public void onRecoverConnectionFailed() {
        client.resetListeners();
        if (listener != null) {
            listener.onRecoverConnectionFailed();
        }
    }

    @Override
    public void onDisconnectDone(int result) {
        if (requestState == RECONNECT) {
            connect();
        } else {
            if (responseState == SESSION_RECOVERED) {
                if (listener != null) {
                    listener.onDisconnected();
                }
            } else {
                if (listener != null) {
                    listener.onDisconnected();
                }
            }
        }
    }

    public void reset() {
        client.resetListeners();
    }

    public void joinRoomWithProperties(JsonValue matchProperties) {
//        JsonValue jsonValue = new JsonValue(JsonValue.ValueType.object);
//        for (String key : matchProperties.keySet()) {
//            jsonValue.addChild(key, new JsonValue(matchProperties.get(key)));
//        }
        client.joinRoomWithProperties(matchProperties.toJson(JsonWriter.OutputType.javascript));
    }

    public void joinGame(JsonValue matchProperties) {
        this.matchProperties = matchProperties;
        matchingTry = 0;
        joinRoomWithProperties(matchProperties);
    }

    public void leaveRoom() {
        if (roomId != null) {
            client.leaveRoom(roomId);
        }
    }

    @Override
    public void onLeaveRoomDone(RoomEvent roomEvent) {
        if (listener != null) {
            listener.onRoomLeft();
        }
    }

    boolean isJoinFromSwitch;
    boolean isJoinRoomChangedAfterLowBalance;

    @Override
    public void onJoinRoomDone(RoomEvent roomEvent) {
        if (roomEvent.getResult() == WarpResponseResultCode.SUCCESS) {
            roomId = roomEvent.getRoomId();
            if (listener != null) {
                if (isUserSwitching || isJoinFromSwitch) {
                    isJoinFromSwitch = false;
                    listener.onSwitchRoomJoined();
                } else if (isRoomJoinAfterLowBalance || isJoinRoomChangedAfterLowBalance) {
                    isRoomJoinAfterLowBalance = false;
                    isJoinRoomChangedAfterLowBalance = false;
                    listener.onSwitchRoomJoined();
                } else {
                    listener.onRoomJoined();
                }
            }
            roomId = roomEvent.getRoomId();

            if (isUserSwitching) {
                isUserSwitching = false;
                listGetRoomWithProperty = null;
            }
            //   client.getLiveRoomInfo(roomId);
            client.subscribeRoom(roomEvent.getRoomId());
        } else if (isUserSwitching) {
            if (listGetRoomWithProperty.size() > 0) {
                listGetRoomWithProperty.remove(0);
            }
            if (listGetRoomWithProperty.size() > 0) {
                client.joinRoom(listGetRoomWithProperty.get(0));
            } else {
                if (listener != null) {
                    listener.onSwitchIfNoRoomAvailable();
                    isUserSwitching = false;
                    isJoinFromSwitch = true;
                    listGetRoomWithProperty = null;
                }
            }
        } else if (!isRoomJoinAfterLowBalance) {
            switch (matchingTry) {
                case 0:
                    if (matchProperties != null) {
                        matchingTry++;
                        matchProperties.addChild(PokerConstants.KEY_ROOM_STATUS, new JsonValue(PokerConstants.FINISH_ROOM_STATUS));
                        joinRoomWithProperties(matchProperties);
                    }
                    break;

                case 1:
                    if (matchProperties != null) {
                        matchingTry++;
                        matchProperties.addChild(PokerConstants.KEY_ROOM_STATUS, new JsonValue(PokerConstants.RUNNING_ROOM_STATUS));
                        joinRoomWithProperties(matchProperties);
                    }
                    break;
                case 2:
                    if (matchProperties != null) {
//                        matchProperties.put(PokerConstants.KEY_ROOM_STATUS, PokerConstants.FRESH_ROOM_STATUS);
                        matchingTry++;
                        String roomName = TimeUtils.millis() + "";
                        client.createTurnRoom(roomName, myID, maxUsers, getRoomProperties(), turnTime);
                    }
                    break;
            }

        } else {
            if (listener != null) {
                isRoomJoinAfterLowBalance = false;
                isJoinRoomChangedAfterLowBalance = true;
                listener.onAgainJoinAfterLowBalance();
            }
        }
    }


    @Override
    public void onCreateRoomDone(RoomEvent roomEvent) {
        if (roomEvent.getResult() == WarpResponseResultCode.SUCCESS) {
            client.joinRoom(roomEvent.getRoomId());
        } else {
            disconnect();
        }
    }

    @Override
    public void onSubscribeRoomDone(RoomEvent roomEvent) {
        if (roomEvent.getResult() == WarpResponseResultCode.SUCCESS) {
            roomId = roomEvent.getRoomId();
        }
    }

    @Override
    public void onUserJoinedRoom(RoomEvent roomData, String s) {
        if (listener == null) return;

    }

    @Override
    public void onGetLiveRoomInfoDone(LiveRoomInfoEvent liveRoomInfoEvent) {
        if (listener == null) return;
        if (liveRoomInfoEvent.getResult() == WarpResponseResultCode.SUCCESS) {
            listener.onGetLiveRoomInfo(liveRoomInfoEvent);
        }
    }

    @Override
    public void onRoomRPCDone(int result, String s) {

    }

    @Override
    public void onZoneRPCDone(int result, String s) {

    }

    //TODO RPC
//
//    @Override
//    public void onRPCDone(byte b, String s, Object o) {
//        MessageListener listener = messageListenerHashMap.get(s);
//        if (listener != null) {
//            listener.onDone(b, o);
//            messageListenerHashMap.remove(s);
//        }
//    }

    public boolean isNotEmpty(String string) {
        return string != null && !string.isEmpty();
    }


    @Override
    public void onUserLeftRoom(RoomEvent roomData, String s) {
        if (listener != null) {
            if (s.matches(myID)) {
                listener.onRoomLeft();
            } else {
                listener.onUserLeft(s);
            }
        }
    }


    @Override
    public void onChatReceived(ChatEvent chatEvent) {
        if (listener == null) return;
        String message = chatEvent.getMessage();
        if (message == null || message.isEmpty()) return;
        String sender = chatEvent.getSender();
        if (sender.matches(PokerConstants.POKER_SERVER)) {
            listener.handleServerMessage(message, sender);
        } else {
            listener.handleClientMessage(message, sender);
        }
    }


    @Override
    public void onUpdatePeersReceived(byte[] update) {
        if (listener == null) return;
        if (update != null && update.length > 0) {
            try {
                String message = new String(update);
                listener.handleClientMessage(message, "");
            } catch (Exception e) {
            }
        }
    }

    public void sendMessage(int type, JsonValue data) {
        String message = new PokerMessage(type, data).toString();
        client.sendChat(message);
    }


    public void sendMove(String moveData) {
        client.sendMove(moveData);
    }

    public boolean isNotConnected() {
        return !client.isConnected();
    }

    public void sendRecoverRequest() {
        sendRecoverRequest(0);
    }

    public void sendRecoverRequest(int recoverAttempts) {
        this.recoverAttempts = recoverAttempts;

        sendMessage(PokerConstants.MESSAGE_RECOVER_REQUEST, new JsonValue(JsonValue.ValueType.object));
    }

    @Override
    public void onMoveCompleted(MoveEvent moveEvent) {
        if (listener != null) {
            listener.onMoveCompleted(moveEvent);
            //   proGame.config.appendLog(TimeUtils.millis()+"==move completed===>"+moveEvent.getMoveData()+"===next turn=="+moveEvent.getNextTurn()+"==roomId==="+moveEvent.getRoomId()+"==Sender=="+moveEvent.getSender());
        }
    }


    @Override
    public void onGameStarted(String s, String s1, String s2) {
        if (listener != null) {
            listener.onGameStarted(s2, s);
        }
    }

    @Override
    public void onGameStopped(String s, String s1) {
        if (listener != null) {
            listener.onGameStopped();
        }
    }

    @Override
    public void onGetMatchedRoomsDone(MatchedRoomEvent matchedRoomsEvent) {
        if (listener != null) {
            listener.onGetMatchedRoomsDone(matchedRoomsEvent);
        }
    }

    private void addRoomId(RoomData[] roomData, String status) {
        if (roomData == null) {
            return;
        }
        int range = roomData.length;
        if (range > 10) {
            range = 10;
        }
        if (listGetRoomWithProperty != null) {
            range = range - listGetRoomWithProperty.size();
        }
        for (int i = 0; i < range; i++) {
            if (listGetRoomWithProperty == null) {
                listGetRoomWithProperty = new ArrayList<String>();
                listGetRoomWithProperty.add(roomData[i].getId());
            } else {
                listGetRoomWithProperty.add(roomData[i].getId());
            }
        }
        if (listPreviousJoinRoom != null && listPreviousJoinRoom.size() > 0) {
            listGetRoomWithProperty.removeAll(listPreviousJoinRoom);
        }
        if (roomId != null) {
            listGetRoomWithProperty.remove(roomId);
        }

    }

    public void getMatchedRoom(JsonValue matchProperties) {
        gettingRoomWithProperty = PokerConstants.GET_ROOM_WITH_FRESH_PROPERTY;
        matchPropertiesForRoom = matchProperties;
        getRoomWithProperties(matchProperties);
    }


    public void getRoomWithProperties(JsonValue matchProperties) {
        client.getRoomWithProperties(matchProperties.toJson(JsonWriter.OutputType.javascript));
    }


    private boolean resumeConnectionRunning;

    public synchronized void resumeConnection(final int counterTime) {
        if (resumeConnectionRunning) {
            return;
        }
        resumeConnectionRunning = true;
        PokerUtils.setTimeOut(1000, new TimeoutHandler() {
            @Override
            public void onTimeOut() {
                final int count = counterTime;
                PokerInterval pokerInterval = new PokerInterval(2000) {
                    @Override
                    public void onInterval() {
                        if ((!PokerUtils.isNetworkConnected() || isLimitedAccess) && count > 0) {
                            ApiHandler.callCurrentTimeApi(new GdxListener<String>() {
                                @Override
                                public void onSuccess(String s) {
                                    cancel();
                                    isLimitedAccess = false;
                                }

                                @Override
                                public void onFail(String reason) {

                                }

                                @Override
                                public void onError(String error) {

                                }
                            });
                        } else {
                            cancel();
                        }
                    }
                };
                pokerInterval.start();
            }
        });
        connected = false;
    }


    public boolean isConnected() {
        return connected;
    }

    public boolean isOpponentOffline() {
        return opponentOffline;
    }

    public void setOpponentOffline(boolean opponentOffline) {
        this.opponentOffline = opponentOffline;
    }

    public boolean updateFromProperties(HashMap<String, Object> properties) {
        try {
            contestId = Integer.parseInt(properties.get("variant") + "");
            smallBlind = Float.parseFloat(properties.get("smallBlind") + "");
            bigBlind = Float.parseFloat(properties.get("bigBlind") + "");
            minAddAmount = Float.parseFloat(properties.get("minAddAmount") + "");
            maxAddAmount = Float.parseFloat(properties.get("maxAddAmount") + "");
            return true;
        } catch (Exception e) {

        }
        return false;
    }

    public void sendUpdate(int type, JsonValue data) {
        PokerMessage pokerMessage = new PokerMessage(type, data);
        String message = pokerMessage.sendUpdateString();
        client.sendUpdate(message.getBytes());
    }

    public void joinGameAfterAddCash() {
        if (roomId != null) {
            matchProperties = null;
            isRoomJoinAfterLowBalance = true;
            client.joinRoom(roomId);
        } else {
            if (listener != null) {
                listener.onAgainJoinAfterLowBalance();
            }
        }
    }
}
