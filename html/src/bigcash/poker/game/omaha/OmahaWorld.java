package bigcash.poker.game.omaha;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import bigcash.poker.appwarp.events.MoveEvent;
import bigcash.poker.constants.Constant;
import bigcash.poker.dialogs.LandscapeCashAddDialog;
import bigcash.poker.font.FontPool;
import bigcash.poker.font.FontType;
import bigcash.poker.game.CombinationLabel;
import bigcash.poker.game.PokerAddBalanceDialog;
import bigcash.poker.game.PokerBalanceListener;
import bigcash.poker.game.PokerJoinMessage;
import bigcash.poker.game.PokerPlayerCustomData;
import bigcash.poker.game.PokerPlayerData;
import bigcash.poker.game.TimerMessage;
import bigcash.poker.game.UpdateBalance;
import bigcash.poker.game.omaha.controllers.OmahaWarpController;
import bigcash.poker.game.omaha.messages.OmahaDealerMessage;
import bigcash.poker.game.omaha.messages.OmahaRecoverMessage;
import bigcash.poker.game.omaha.messages.OmahaResultMessage;
import bigcash.poker.game.omaha.widgets.OmahaCard;
import bigcash.poker.game.omaha.widgets.OmahaPlayer;
import bigcash.poker.game.omaha.widgets.OmahaUserPlayer;
import bigcash.poker.game.omaha.widgets.OmahaWinningLabel;
import bigcash.poker.game.omaha.widgets.buttons.OmahaButton;
import bigcash.poker.network.GdxListener;
import bigcash.poker.network.PokerApi;
import bigcash.poker.utils.AssetsLoader;
import bigcash.poker.utils.BestPossibleCombination;
import bigcash.poker.utils.PokerCombinations;
import bigcash.poker.utils.PokerConstants;
import bigcash.poker.utils.PokerUtils;
import bigcash.poker.utils.TimeoutHandler;
import bigcash.poker.widgets.AmountLabel;
import bigcash.poker.widgets.LayerMaskCShader;
import bigcash.poker.widgets.MaskLayer;

public class OmahaWorld extends Group {
    public OmahaGameScreen screen;
    public OmahaWarpController warpController;
    public TextureAtlas atlas;
    public HashMap<Integer, String> suitMap;
    public float cardCenterX, cardCenterY, cardWidth, cardHeight;
    public TextureRegion backCardRegion, dealerRegion;

    private OmahaPlayer.PokerPlayerStyle opponentPlayerStyle, userPlayerStyle;
    public AmountLabel.PokerLabelStyle betLabelStyle;
    private LinkedHashMap<Integer, OmahaPlayer> seatMap;
    private HashMap<String, OmahaPlayer> playerMap;
    public HashMap<Integer, String> cardRankingMap;
    public MaskLayer maskLayer;
    public PokerPlayerData userPlayerData;
    public Label.LabelStyle combinationLabelStyle;
    public TextureRegion combinationStars;

    private int maxUsers;
    private int gameState;
    private int currentRound;
    private float smallBlind, bigBlind;
    private boolean userTurn, betPlaced;
    public Sound tickSound,checkSound;
    private long tickSoundId;
    public TimerMessage timerMessage;
    public boolean isRecovering, isShowingResult, isJoining;
    private HashMap<Integer, Vector2> positionMap;
    public float dealerX, dealerY, betPoolCenterX, betPoolCenterY, betPoolHeight,maxBet;
    public OmahaUserPlayer omahaUserPlayer;
    private Table betPoolTable;
    private AmountLabel mainBetPool;
    public Sound betSound, foldSound;
    private float minRaiseBet, maxRaiseBet;

    public OmahaWorld(OmahaGameScreen screen, int maxUsers, int gameState) {
        setTransform(false);
        this.screen = screen;
        this.warpController = screen.warpController;
        this.atlas = screen.atlas;
        this.maxUsers = maxUsers;
        this.dealerX = screen.width / 2f;
        this.dealerY = screen.height * 0.84f;

        this.backCardRegion = this.atlas.findRegion("back");
        this.dealerRegion = this.atlas.findRegion("img_dealer");
        this.betSound = AssetsLoader.instance().betSound;
        this.foldSound = AssetsLoader.instance().foldSound;
        tickSound = AssetsLoader.instance().tickSound;
        checkSound = AssetsLoader.instance().checkSound;

        this.combinationLabelStyle = new Label.LabelStyle();
        this.combinationLabelStyle.font = FontPool.obtain(FontType.ROBOTO_BOLD, 10);
        this.combinationLabelStyle.fontColor = Color.valueOf("492101");
        this.combinationLabelStyle.background = new TextureRegionDrawable(atlas.findRegion("bg_combination"));

        this.combinationStars = atlas.findRegion("stars_combination");

        this.cardHeight = this.screen.height * 0.15f;
        this.cardWidth = this.cardHeight * backCardRegion.getRegionWidth() / backCardRegion.getRegionHeight();

        this.cardCenterX = screen.width / 2f;
        this.cardCenterY = screen.height * 0.61f;

        this.suitMap = new HashMap<Integer, String>();
        this.suitMap.put(PokerConstants.SUIT_SPADE, "s");
        this.suitMap.put(PokerConstants.SUIT_HEART, "h");
        this.suitMap.put(PokerConstants.SUIT_CLUB, "c");
        this.suitMap.put(PokerConstants.SUIT_DIAMOND, "d");

        this.cardRankingMap = new HashMap<Integer, String>();
        this.cardRankingMap.put(PokerCombinations.HIGH_CARD, "High Cards");
        this.cardRankingMap.put(PokerCombinations.ONE_PAIR, "1 Pair");
        this.cardRankingMap.put(PokerCombinations.TWO_PAIR, "2 Pairs");
        this.cardRankingMap.put(PokerCombinations.THREE_OF_A_KIND, "3 Of A Kind");
        this.cardRankingMap.put(PokerCombinations.STRAIGHT, "Straight");
        this.cardRankingMap.put(PokerCombinations.FLUSH, "Flush");
        this.cardRankingMap.put(PokerCombinations.FULL_HOUSE, "Full House");
        this.cardRankingMap.put(PokerCombinations.FOUR_OF_A_KIND, "4 Of A Kind");
        this.cardRankingMap.put(PokerCombinations.STRAIGHT_FLUSH, "Straight Flush");
        this.cardRankingMap.put(PokerCombinations.ROYAL_FLUSH, "Royal Flush");

        Label.LabelStyle nameLabelStyle = new Label.LabelStyle();
        nameLabelStyle.font = FontPool.obtain(FontType.ROBOTO_BOLD, 4);
        nameLabelStyle.fontColor = Color.WHITE;

        this.maskLayer = new MaskLayer(new LayerMaskCShader(), atlas.findRegion("circle"));
        this.betLabelStyle = new AmountLabel.PokerLabelStyle(atlas.findRegion("bg_amount"), 23, 23);
        this.betLabelStyle.font = FontPool.obtain(FontType.ROBOTO_BOLD, 5.5f);
        this.betLabelStyle.fontColor = Color.WHITE;

        this.betPoolCenterX = screen.width / 2f;
        this.betPoolCenterY = screen.height / 2f;

        this.betPoolHeight = betLabelStyle.font.getLineHeight() * 1.5f;

        this.opponentPlayerStyle = new OmahaPlayer.PokerPlayerStyle(this, maskLayer);
        this.opponentPlayerStyle.balanceStyle = new Label.LabelStyle();
        this.opponentPlayerStyle.balanceStyle.font = FontPool.obtain(FontType.ROBOTO_BOLD, 5.5f);
        this.opponentPlayerStyle.balanceStyle.fontColor = Color.valueOf("01dbb1");

        this.userPlayerStyle = new OmahaPlayer.PokerPlayerStyle(atlas.findRegion("img_user"), opponentPlayerStyle);
        this.userPlayerStyle.balanceStyle = new Label.LabelStyle();
        this.userPlayerStyle.balanceStyle.font = FontPool.obtain(FontType.ROBOTO_BOLD, 5.5f);
        this.userPlayerStyle.balanceStyle.fontColor = Color.valueOf("e8c100");


        this.seatMap = new LinkedHashMap<Integer, OmahaPlayer>();
        this.playerMap = new HashMap<String, OmahaPlayer>();
        this.betPoolTable=new Table();
        this.betPoolTable.setSize(screen.width,betPoolHeight);
        this.betPoolTable.setPosition(0,betPoolCenterY);
        this.betPoolTable.setTouchable(Touchable.disabled);


        this.userPlayerData = new PokerPlayerData(Constant.userProfile.getUserId(), Constant.userProfile.getName(), Constant.userProfile.getImageId(), Constant.userProfile.getPaytmBalance(), Constant.userProfile.getPokerBalance());

        smallBlind = warpController.getSmallBlind();
        bigBlind = warpController.getBigBlind();
        setPositions();
        rebuildSeats();
        setGameState(gameState);
        setCurrentRound(-1);
    }

    public void playCheckSound(){
        screen.playSound(checkSound,1.0f);
    }


    private void setPositions() {
        float width = screen.width;
        float height = screen.height;
        positionMap = new HashMap<Integer, Vector2>();
        positionMap.put(0, new Vector2(0.466f * width, 0.3f * height));
        positionMap.put(1, new Vector2(0.214f * width, 0.412f * height));
        positionMap.put(2, new Vector2(0.214f * width, 0.732f * height));
        positionMap.put(3, new Vector2(0.775f * width, 0.732f * height));
        positionMap.put(4, new Vector2(0.775f * width, 0.412f * height));
    }

    public void setCurrentRound(int currentRound) {
        this.currentRound = currentRound;
    }

    public int getCurrentRound() {
        return this.currentRound;
    }

    public void setGameState(int gameState) {
        this.gameState = gameState;
    }


    public int getGameState() {
        return this.gameState;
    }

    public void setMaxUsers(int maxUsers) {
        this.maxUsers = maxUsers;
    }

    public int getMaxUsers() {
        return this.maxUsers;
    }

    public void setMaxBet(float maxBet){
        this.maxBet= PokerUtils.getValue(maxBet);
    }

    public void updateMaxBet(float amount){
        if(amount>maxBet){
            this.maxBet=PokerUtils.getValue(amount);
        }
    }


    public float getMaxBet() {
        if (this.maxBet<=0.0f){
            return this.getBigBlind();
        }else {
            return this.maxBet;
        }
    }

    private int getOtherPlayingPlayers(){
        int otherPlayerPlaying=0;
        for (OmahaPlayer omahaPlayer :playerMap.values()){
            if (!omahaPlayer.getPlayerId().matches(omahaUserPlayer.playerId) && omahaPlayer.getPlayerState()==PokerConstants.PLAYER_PLAYING){
                otherPlayerPlaying++;
            }
        }
        return otherPlayerPlaying;
    }

    public float getSmallBlind() {
        return smallBlind;
    }

    public void setSmallBlind(float smallBlind) {
        this.smallBlind = smallBlind;
    }

    public float getBigBlind() {
        return bigBlind;
    }

    public void setBigBlind(float bigBlind) {
        this.bigBlind = bigBlind;
    }

    public boolean isUserTurn() {
        return userTurn;
    }

    public void setUserTurn(boolean userTurn) {
        this.userTurn = userTurn;
    }

    public boolean isBetPlaced() {
        return betPlaced;
    }

    public void setBetPlaced(boolean betPlaced) {
        this.betPlaced = betPlaced;
    }

    public void rebuildSeats() {
        clear();
        seatMap.clear();
        betPoolTable.clear();
        playerMap.clear();

        if (maxUsers == 2) {
            rebuild2Seats();
        } else {
            rebuild5Seats();
        }

        addActor(betPoolTable);

        mainBetPool = getAmountLabel(0.0f);
        betPoolTable.add(mainBetPool).height(betPoolHeight);
    }

    public Vector2 getMainPoolPosition() {
        return screen.localToStageCoordinates(mainBetPool,new Vector2());
    }

    private AmountLabel getAmountLabel(float amount) {
        AmountLabel amountLabel = new AmountLabel(amount, betLabelStyle) {
            @Override
            public void updateAmount(float amount) {
                super.updateAmount(amount);
                setVisible(getAmount() > 0);
            }
        };
        amountLabel.updateAmount(amount);
        return amountLabel;
    }


    private void setMainPoolAmount(float amount) {
        mainBetPool.updateAmount(amount);
    }

    public void addToMainPool(float amount) {
        mainBetPool.updateAmount(mainBetPool.amount + amount);
    }

    private void splitPools(float[] pricePools) {
        betPoolTable.clear();
        List<AmountLabel> amountLabels=new ArrayList<AmountLabel>();
        for (int i = 0; i < pricePools.length - 1; i++) {
            float amount = PokerUtils.getValue(pricePools[i]);
            AmountLabel amountLabel = getAmountLabel(pricePools[i]);
            amountLabel.updateAmount(amount);
            amountLabels.add(amountLabel);
        }
        mainBetPool.updateAmount(pricePools[pricePools.length - 1]);
        amountLabels.add(mainBetPool);
        startSplitAnimation(amountLabels);
    }

    private void startSplitAnimation(List<? extends AmountLabel> amountLabels) {
        for (AmountLabel amountLabel : amountLabels) {
            betPoolTable.add(amountLabel).height(betPoolHeight).padLeft(betPoolHeight).padRight(betPoolHeight);
        }
    }

    private void startResultSplitAnimation(final List<OmahaWinningLabel> winningLabels) {
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                betPoolTable.clear();
                startSplitAnimation(winningLabels);
            }
        });

    }


    private void rebuild2Seats() {
        OmahaPlayer player1 = new OmahaPlayer(this, opponentPlayerStyle) {
            @Override
            public void setPositions() {
                giftAlign= Align.right;
                cardRotation=8;
                float detailsPad = playerStyle.imageSize / 2f;
                detailsTable.padRight(detailsPad);
                detailsTable.setPosition(-detailsTable.getWidth(), playerTurnImage.getY() + detailsPadding);
                cardX=detailsTable.getX()+(playerStyle.detailsWidth-detailsPad)/2f+playerStyle.cardWidth/2f;
                betPosition.set(playerTurnImage.getX() + playerTurnImage.getWidth() + betPadding, playerTurnImage.getY() + playerTurnImage.getHeight() - playerStyle.betHeight - betPadding);
                updateBetLabelPosition();
            }

            @Override
            public void updateBetLabelPosition() {
                super.updateBetLabelPosition();
                betLabel.setPosition(betPosition.x, betPosition.y);
            }
        };

        Vector2 position = positionMap.get(2);
        player1.setPosition(position.x, position.y);


        omahaUserPlayer = new OmahaUserPlayer(this, userPlayerStyle) {
            @Override
            public void setPositions() {
                giftAlign= Align.right;
                float detailsPad = playerStyle.imageSize / 2f;
                detailsTable.padLeft(detailsPad);
                cardX=detailsPad+(playerStyle.detailsWidth-detailsPad)/2f-playerStyle.cardWidth/2f;
                detailsTable.setPosition(0, playerTurnImage.getY() + detailsPadding);
                betPosition.set(0, playerTurnImage.getY() + playerTurnImage.getHeight() + betPadding);
                updateBetLabelPosition();
            }

            @Override
            public void updateBetLabelPosition() {
                super.updateBetLabelPosition();
                betLabel.setPosition(betPosition.x - betLabel.getWidth() / 2f, betPosition.y);
            }

            @Override
            public void onTimerFinished() {
                stopTickSound();
                setUserTurn(false);
                warpController.client.sendMove("");
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        screen.hideButtons();
                    }
                });
            }

            @Override
            public void ringAlarm() {
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        tickSoundId = screen.playSound(tickSound, 0.8f);
                    }
                });
            }

            @Override
            public boolean isEmpty() {
                return false;
            }
        };

        position = positionMap.get(0);
        omahaUserPlayer.setPosition(position.x, position.y);

        addActor(player1);
        addActor(omahaUserPlayer);


        seatMap.put(0, omahaUserPlayer);
        omahaUserPlayer.setPlayerData(userPlayerData);
        playerMap.put(userPlayerData.id, omahaUserPlayer);
        seatMap.put(1, player1);
    }

    private void rebuild5Seats() {
        OmahaPlayer player2 = new OmahaPlayer(this, opponentPlayerStyle) {
            @Override
            public void setPositions() {
                giftAlign= Align.right;
                cardRotation=8;
                float detailsPad = playerStyle.imageSize / 2f;
                detailsTable.padRight(detailsPad);
                detailsTable.setPosition(-detailsTable.getWidth(), playerTurnImage.getY() + detailsPadding);
                cardX=detailsTable.getX()+(playerStyle.detailsWidth-detailsPad)/2f+playerStyle.cardWidth/2f;
                betPosition.set(playerTurnImage.getX() + playerTurnImage.getWidth() + betPadding, playerTurnImage.getY() + betPadding);
                updateBetLabelPosition();
            }

            @Override
            public void updateBetLabelPosition() {
                super.updateBetLabelPosition();
                betLabel.setPosition(betPosition.x, betPosition.y);
            }
        };
        Vector2 position = positionMap.get(2);
        player2.setPosition(position.x, position.y);

        OmahaPlayer player3 = new OmahaPlayer(this, opponentPlayerStyle) {
            @Override
            public void setPositions() {
                giftAlign= Align.left;
                cardRotation=-8;
                float detailsPad = playerStyle.imageSize / 2f;
                detailsTable.padLeft(detailsPad);
                cardX=detailsPad+(playerStyle.detailsWidth-detailsPad)/2f-playerStyle.cardWidth/2f;
                detailsTable.setPosition(0, playerTurnImage.getY() + detailsPadding);
                betPosition.set(playerTurnImage.getX() - betPadding, playerTurnImage.getY() + betPadding);
                updateBetLabelPosition();
            }

            @Override
            public void updateBetLabelPosition() {
                super.updateBetLabelPosition();
                betLabel.setPosition(betPosition.x - betLabel.getWidth(), betPosition.y);
            }
        };

        position = positionMap.get(3);
        player3.setPosition(position.x, position.y);


        OmahaPlayer player1 = new OmahaPlayer(this, opponentPlayerStyle) {
            @Override
            public void setPositions() {
                giftAlign= Align.right;
                cardRotation=8;
                float detailsPad = playerStyle.imageSize / 2f;
                detailsTable.padRight(detailsPad);
                detailsTable.setPosition(-detailsTable.getWidth(), playerTurnImage.getY() + detailsPadding);
                cardX=detailsTable.getX()+(playerStyle.detailsWidth-detailsPad)/2f+playerStyle.cardWidth/2f;
                betPosition.set(playerTurnImage.getX() + playerTurnImage.getWidth() + betPadding, playerTurnImage.getY() + playerTurnImage.getHeight() - playerStyle.betHeight - betPadding);
                updateBetLabelPosition();
            }

            @Override
            public void updateBetLabelPosition() {
                super.updateBetLabelPosition();
                betLabel.setPosition(betPosition.x, betPosition.y);
            }
        };

        position = positionMap.get(1);
        player1.setPosition(position.x, position.y);


        omahaUserPlayer = new OmahaUserPlayer(this, userPlayerStyle) {
            @Override
            public void setPositions() {
                giftAlign= Align.right;
                float detailsPad = playerStyle.imageSize / 2f;
                detailsTable.padLeft(detailsPad);
                cardX=detailsPad+(playerStyle.detailsWidth-detailsPad)/2f-playerStyle.cardWidth/2f;
                detailsTable.setPosition(0, playerTurnImage.getY() + detailsPadding);
                betPosition.set(0, playerTurnImage.getY() + playerTurnImage.getHeight() + betPadding);
                updateBetLabelPosition();
            }

            @Override
            public void updateBetLabelPosition() {
                super.updateBetLabelPosition();
                betLabel.setPosition(betPosition.x - betLabel.getWidth() / 2f, betPosition.y);
            }

            @Override
            public void onTimerFinished() {
                stopTickSound();
                setUserTurn(false);
                warpController.client.sendMove("");
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        screen.hideButtons();
                    }
                });
            }

            @Override
            public void ringAlarm() {
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        tickSoundId = screen.playSound(tickSound, 0.8f);
                    }
                });
            }

            @Override
            public boolean isEmpty() {
                return false;
            }
        };

        position = positionMap.get(0);
        omahaUserPlayer.setPosition(position.x, position.y);

        OmahaPlayer player4 = new OmahaPlayer(this, opponentPlayerStyle) {
            @Override
            public void setPositions() {
                giftAlign= Align.left;
                cardRotation=-8;
                float detailsPad = playerStyle.imageSize / 2f;
                detailsTable.padLeft(detailsPad);
                cardX=detailsPad+(playerStyle.detailsWidth-detailsPad)/2f-playerStyle.cardWidth/2f;
                detailsTable.setPosition(0, playerTurnImage.getY() + detailsPadding);
                betPosition.set(playerTurnImage.getX() - betPadding, playerTurnImage.getY() + playerTurnImage.getHeight() - playerStyle.betHeight - betPadding);
            }

            @Override
            public void updateBetLabelPosition() {
                super.updateBetLabelPosition();
                betLabel.setPosition(betPosition.x - betLabel.getWidth(), betPosition.y);
            }
        };

        position = positionMap.get(4);
        player4.setPosition(position.x, position.y);

        addActor(player1);
        addActor(player2);
        addActor(player3);
        addActor(player4);
        addActor(omahaUserPlayer);


        seatMap.put(0, omahaUserPlayer);
        omahaUserPlayer.setPlayerData(userPlayerData);
        playerMap.put(userPlayerData.id, omahaUserPlayer);
        seatMap.put(1, player1);
        seatMap.put(2, player2);
        seatMap.put(3, player3);
        seatMap.put(4, player4);

    }

    public void stopTickSound() {
        if (tickSoundId != -1) {
            try {
                tickSound.stop(tickSoundId);
            } catch (Exception e) {

            }
        }
        tickSoundId = -1;
    }


    public int getPlayingPlayersCount() {
        int playingPlayers = 0;
        for (OmahaPlayer player : playerMap.values()) {
            if (player.getPlayerState() == PokerConstants.PLAYER_PLAYING) {
                playingPlayers++;
            }
        }
        return playingPlayers;
    }

    public String[] getSittingOrder(String[] joinedUsers) {
        String[] sittingPlayers = new String[maxUsers];
        for (int i = 0; i < maxUsers; i++) {
            if (i == 0) {
                sittingPlayers[i] = omahaUserPlayer.getPlayerId();
            } else {
                sittingPlayers[i] = "";
            }
        }

        int index = -1;
        for (int i = 0; i < joinedUsers.length; i++) {
            String user = joinedUsers[i];
            if (user.matches(warpController.myID)) {
                index = i;
            }
        }
        if (index != -1) {
            for (int i = 0; i < sittingPlayers.length; i++) {
                if (i < joinedUsers.length) {
                    int newIndex = i - index;
                    if (newIndex < 0) {
                        newIndex = joinedUsers.length + newIndex;
                    }
                    sittingPlayers[newIndex] = joinedUsers[i];
                }
            }
        }
        return sittingPlayers;
    }


    public void onJoinDone(JsonValue jsonValue, float maxBetAmount) {
        PokerJoinMessage joinMessage = new PokerJoinMessage(jsonValue,maxBetAmount);
        String[] sittingOrder = getSittingOrder(joinMessage.joinedPlayersArray);
        for (int i = 1; i < sittingOrder.length; i++) {
            String playerId = sittingOrder[i];
            OmahaPlayer omahaPlayer = seatMap.get(i);
            if (playerId.isEmpty()) {
                seatMap.get(i).emptySeat();
            } else {
                PokerPlayerData pokerPlayerData = joinMessage.playerDataMap.get(playerId);
                if (playerMap.containsKey(playerId)) {
                    OmahaPlayer player = playerMap.get(playerId);
                    player.emptySeat();
                    omahaPlayer.setPlayerData(pokerPlayerData);
                    if(!playerId.matches(joinMessage.currentTurn)) {
                        omahaPlayer.setLastMoveStatus(pokerPlayerData.lastMoveStatus);
                    }
                    omahaPlayer.setPlayerState(pokerPlayerData.state);
                } else {
                    omahaPlayer.setPlayerData(pokerPlayerData);
                    if(!playerId.matches(joinMessage.currentTurn)) {
                        omahaPlayer.setLastMoveStatus(pokerPlayerData.lastMoveStatus);
                    }
                    omahaPlayer.setPlayerState(pokerPlayerData.state);
                    playerMap.put(playerId, omahaPlayer);
                }
            }
        }
        omahaUserPlayer.setPlayerData(joinMessage.playerDataMap.get(warpController.myID));
        if(!warpController.myID.matches(joinMessage.currentTurn)) {
            omahaUserPlayer.setLastMoveStatus(joinMessage.playerDataMap.get(warpController.myID).lastMoveStatus);
        }
        omahaUserPlayer.setPlayerState(joinMessage.playerDataMap.get(warpController.myID).state);
        screen.setTotalBetAmount(joinMessage.totalGameBet);

        if (joinMessage.openCards != null && joinMessage.openCards.length > 0) {
            screen.updateOpenCards(joinMessage.openCards);
        }

        if (joinMessage.openCards != null && joinMessage.openCards.length >= 3) {
            splitPools(joinMessage.pricePool);
        }

        if (joinMessage.gameRunning) {
            gameState = PokerConstants.GAME_RUNNING;
            for (OmahaPlayer player : playerMap.values()) {
                if (player.getPlayerState() == PokerConstants.PLAYER_PLAYING) {
                    player.updateCards(new int[]{-1, -1,-1,-1});
                }
            }
        } else {
            gameState = PokerConstants.GAME_REST;
        }

        if (joinMessage.userCards != null && joinMessage.userCards.length > 0 && omahaUserPlayer.getPlayerState() == PokerConstants.PLAYER_PLAYING) {
            omahaUserPlayer.updateCards(joinMessage.userCards);
        }

        if (joinMessage.currentTurn.isEmpty() && joinMessage.remainingTime <= 0) {
            setUserTurn(false);
        } else {
            setUserTurn(joinMessage.currentTurn.matches(warpController.myID));
        }

        setMaxBet(0.0f);
        updateMaxBet(joinMessage.maxBetAmount);

        setBetPlaced(joinMessage.totalRoundBet > 0.0f);
        if (!joinMessage.currentTurn.isEmpty()) {
            playerMap.get(joinMessage.currentTurn).resumeTimer(joinMessage.remainingTime, joinMessage.lifeUsed);
            screen.updateButtonsOnRound(null, true);
        } else {
            screen.hideButtons();
        }

        isJoining = false;

        if (timerMessage != null) {
            screen.showTimer(timerMessage);
        } else if (joinMessage.timerMessage != null && joinMessage.timerMessage.remainingTime() > 0) {
            screen.showTimer(joinMessage.timerMessage);
        }else{
            if(gameState == PokerConstants.GAME_REST) {
                if(playerMap == null || playerMap.size()==1){
                    screen.showMessage("Waiting for players");
                }else {
                    screen.showMessage("Game Begins Shortly");
                }
            }
        }
        timerMessage = null;

    }


    public void onUserJoined(JsonValue jsonValue) {
        PokerPlayerCustomData pokerPlayerCustomData =new PokerPlayerCustomData(jsonValue);
        PokerPlayerData pokerPlayerData = new PokerPlayerData(pokerPlayerCustomData, gameState == PokerConstants.GAME_REST ? PokerConstants.PLAYER_PLAYING : PokerConstants.PLAYER_WATCHING);
        addPlayer(pokerPlayerData);
    }


    public void addPlayer(PokerPlayerData playerData) {
        OmahaPlayer omahaPlayer = getEmptySeat();
        if (omahaPlayer != null) {
            if (playerMap.containsKey(playerData.id)) {
                OmahaPlayer player = playerMap.get(playerData.id);
                player.emptySeat();
                player.setPlayerData(playerData);
                player.setPlayerState(gameState == PokerConstants.GAME_REST ? PokerConstants.PLAYER_PLAYING : PokerConstants.PLAYER_WATCHING);

            } else {
                omahaPlayer.setPlayerData(playerData);
                omahaPlayer.setPlayerState(gameState == PokerConstants.GAME_REST ? PokerConstants.PLAYER_PLAYING : PokerConstants.PLAYER_WATCHING);
                playerMap.put(playerData.id, omahaPlayer);
            }
        }
    }

    public OmahaPlayer getEmptySeat() {
        for (int i = 0; i < maxUsers; i++) {
            OmahaPlayer playerTable = seatMap.get(i);
            if (playerTable.isEmpty()) {
                return playerTable;
            }
        }
        return null;
    }


    public void removePlayer(String playerId) {
        OmahaPlayer omahaPlayer = playerMap.get(playerId);
        if (omahaPlayer != null) {
            omahaPlayer.resetTimer();
            omahaPlayer.emptySeat();
        }
        playerMap.remove(playerId);
        if (playerMap.size() == 1) {
            screen.backButton.setVisible(true);
        }
    }

    public void setPlayerCards(int[] cards) {
        if (cards == null || cards.length == 0) {
            omahaUserPlayer.removeCards();
        } else {
            float delay = 0.0f;
            delay = omahaUserPlayer.dealCards(cards, delay);
            for (OmahaPlayer omahaPlayer : playerMap.values()) {
                if (!omahaPlayer.getPlayerId().matches(warpController.myID) && omahaPlayer.getPlayerState() == PokerConstants.PLAYER_PLAYING) {
                    delay = omahaPlayer.dealCards(new int[]{-1, -1,-1,-1}, delay);
                }
            }
        }
    }

    public void setDealer(OmahaDealerMessage dealerMessage) {
        screen.stopTimer();
        screen.updateOpenCards(null);
        screen.resetScreen();
        screen.hideResult();
        rebuildSeats();

        String[] sittingOrder = getSittingOrder(dealerMessage.joinedPlayersArray);
        for (int i = 1; i < sittingOrder.length; i++) {
            String playerId = sittingOrder[i];
            OmahaPlayer omahaPlayer = seatMap.get(i);
            if (playerId.isEmpty()) {
                seatMap.get(i).emptySeat();
            } else {
                PokerPlayerData pokerPlayerData = dealerMessage.playerDataMap.get(playerId);
                if (playerMap.containsKey(playerId)) {
                    OmahaPlayer player = playerMap.get(playerId);
                    player.emptySeat();
                    omahaPlayer.setPlayerData(pokerPlayerData);
                    omahaPlayer.setPlayerState(pokerPlayerData.state);
                } else {
                    omahaPlayer.setPlayerData(pokerPlayerData);
                    omahaPlayer.setPlayerState(pokerPlayerData.state);
                    playerMap.put(playerId, omahaPlayer);
                }
            }
        }
        omahaUserPlayer.setPlayerData(dealerMessage.playerDataMap.get(warpController.myID));
        omahaUserPlayer.setPlayerState(dealerMessage.playerDataMap.get(warpController.myID).state);

        OmahaPlayer omahaPlayer = playerMap.get(dealerMessage.getDealer());
        if (omahaPlayer != null) {
            omahaPlayer.setDealer();
        }
        smallBlind = dealerMessage.getSmallBlind().getBet();
        bigBlind = dealerMessage.getBigBlind().getBet();
        setMaxBet(getBigBlind());
        screen.setTotalBetAmount(smallBlind + bigBlind);
        setBetPlaced(true);
        setCurrentRound(PokerConstants.ROUND_BLIND);
        gameState = PokerConstants.GAME_RUNNING;
    }


    public void updateOpenCards(SequenceAction sequenceAction, JsonValue data) {
        final int[] openCards = data.get("openCards").asIntArray();
        final float[] priceList = data.get("priceList").asFloatArray();
        float totalBetAmount = 0;
        for (int i = 0; i < priceList.length; i++) {
            totalBetAmount = PokerUtils.getValue(priceList[i]) + totalBetAmount;
        }
        setMaxBet(0.0f);
        setBetPlaced(false);

        final float totalAmount = totalBetAmount;

        sequenceAction.addAction(Actions.run(new Runnable() {
            @Override
            public void run() {
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        for (OmahaPlayer player : playerMap.values()) {
                            if (player.getPlayerState() == PokerConstants.PLAYER_PLAYING) {
                                player.removeMoveState();
                            }
                            player.addBetToPool();
                        }
                    }
                });

            }
        }));
        sequenceAction.addAction(Actions.delay(0.4f));

        if (priceList.length > 1) {
            sequenceAction.addAction(Actions.run(new Runnable() {
                @Override
                public void run() {
                    Gdx.app.postRunnable(new Runnable() {
                        @Override
                        public void run() {
                            splitPools(priceList);
                        }
                    });
                }
            }));

        } else {
            sequenceAction.addAction(Actions.run(new Runnable() {
                @Override
                public void run() {
                    Gdx.app.postRunnable(new Runnable() {
                        @Override
                        public void run() {
                            mainBetPool.updateAmount(priceList[0]);
                        }
                    });

                }
            }));
        }
        sequenceAction.addAction(Actions.delay(0.2f));
        sequenceAction.addAction(Actions.run(new Runnable() {
            @Override
            public void run() {
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        screen.setTotalBetAmount(totalAmount);
                        screen.openCards(openCards);
                    }
                });

            }
        }));
        sequenceAction.addAction(Actions.delay(0.1f));
        sequenceAction.addAction(Actions.run(new Runnable() {
            @Override
            public void run() {
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        if ((omahaUserPlayer.getPlayerState() == PokerConstants.PLAYER_PLAYING || omahaUserPlayer.getPlayerState()==PokerConstants.PLAYER_ALL_IN) && omahaUserPlayer.getHandCards().size() >= 2 && screen.getOmahaOpenCards().size() >= 3) {
                            BestPossibleCombination bestPossibleCombination = OmahaUtils.getBestPossibleCombination(getCards(omahaUserPlayer.getHandCards().values()), getCards(screen.getOmahaOpenCards().values()), omahaUserPlayer.playerId);
                            omahaUserPlayer.updateCombinations(bestPossibleCombination.getBestCards(), cardRankingMap.get(bestPossibleCombination.getGroupType()));
                        }
                    }
                });

                switch (getCurrentRound()) {
                    case PokerConstants.ROUND_BLIND:
                        setCurrentRound(PokerConstants.ROUND_PRE_FLOP);
                        break;
                    case PokerConstants.ROUND_PRE_FLOP:
                        setCurrentRound(PokerConstants.ROUND_FLOP);
                        break;
                    case PokerConstants.ROUND_FLOP:
                        setCurrentRound(PokerConstants.ROUND_TURN);
                        break;
                    case PokerConstants.ROUND_TURN:
                        setCurrentRound(PokerConstants.ROUND_RIVER);
                        break;
                    default:
                        setCurrentRound(PokerConstants.INVALID);
                }
            }
        }));


    }

    public void resetTimers(boolean isStopGameTimer) {
        if(isStopGameTimer) {
            screen.stopTimer();
        }
        stopTickSound();
        for (OmahaPlayer omahaPlayer : playerMap.values()) {
            omahaPlayer.resetTimer();
        }
    }


    @Override
    public void setColor(Color color) {
        super.setColor(color);
        for (Actor actor : getChildren()) {
            actor.setColor(color);
        }
    }

    public void onGameStarted(final String playerId) {
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                setCurrentRound(PokerConstants.ROUND_PRE_FLOP);
                setUserTurn(playerId.matches(warpController.myID));
                screen.updateButtonsOnRound(null, true);
                for (OmahaPlayer omahaPlayer : playerMap.values()) {
                    if (omahaPlayer.getPlayerState() != PokerConstants.PLAYER_WATCHING) {
                        omahaPlayer.setPlayerState(PokerConstants.PLAYER_PLAYING);
                    }
                    omahaPlayer.resetTimer();
                }
                OmahaPlayer omahaPlayer = playerMap.get(playerId);
                if (omahaPlayer != null) {
                    omahaPlayer.startTimer(Color.GREEN);
                }
                gameState = PokerConstants.GAME_RUNNING;
            }
        });
    }


    public void onMoveCompleted(final MoveEvent moveEvent) {
        clearActions();
        boolean roundChanged = false;
        String nextTurn = moveEvent.getNextTurn();
        String sender = moveEvent.getSender();
        SequenceAction sequenceAction = Actions.sequence();
        for (OmahaPlayer omahaPlayer : playerMap.values()) {
            if (!moveEvent.getSender().matches(omahaPlayer.getPlayerId())) {
                omahaPlayer.resetTimer();
            } else {
                omahaPlayer.cancelTimer();
            }
        }
        if (moveEvent.getMoveData() != null && !moveEvent.getMoveData().isEmpty()) {
            final JsonValue moveData = new JsonReader().parse(moveEvent.getMoveData());
            String moveType = moveData.getString("moveType", PokerConstants.MOVE_CHECK);
            if (moveType.matches(PokerConstants.MOVE_CHECK) && !sender.matches(warpController.myID)){
                playCheckSound();
            }
            final float betAmount = moveData.getFloat("betAmount", 0.0f);
            if (betAmount > 0.0f) {
                setBetPlaced(true);
            }


            float totalGameBet = PokerUtils.getValue(moveData.getFloat("totalGameBet"));
            if(nextTurn != null && nextTurn.matches(warpController.myID)){
                setMaxRaiseBet(PokerUtils.getValue(moveData.getFloat("maxRaiseBet")));
                setMinRaiseBet(PokerUtils.getValue(moveData.getFloat("minRaiseBet")));
            }
            screen.setTotalBetAmount(totalGameBet);
            OmahaPlayer omahaPlayer = playerMap.get(sender);
            if (omahaPlayer != null) {
                omahaPlayer.minusBalanceAmount(betAmount);
                omahaPlayer.updateBalanceAmount();
                omahaPlayer.updateMoveState(moveType);
                omahaPlayer.showBetAnimation(betAmount);
                updateMaxBet(omahaPlayer.betAmount);
                sequenceAction.addAction(Actions.delay(0.2f));

                sequenceAction.addAction(Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        Gdx.app.postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                if (betPoolTable.getChildren().size > 1) {
                                    mainBetPool.updateAmount(mainBetPool.amount + betAmount);
                                }
                            }
                        });
                    }
                }));
            }

            if (moveData.has("openCards")) {
                updateOpenCards(sequenceAction, moveData);
                roundChanged = true;
            }
        }

        sequenceAction.addAction(Actions.run(new Runnable() {
            @Override
            public void run() {
                for (OmahaPlayer player : playerMap.values()) {
                    player.resetTimer();
                }
            }
        }));

        setNextTurn(sequenceAction, nextTurn, sender, roundChanged);

        addAction(sequenceAction);

    }

    private void setNextTurn(SequenceAction sequenceAction, final String nextTurn, final String sender, final boolean roundChanged) {
        setUserTurn(nextTurn.equals(warpController.myID));
        final OmahaButton checkedButton = screen.getCheckedButton();
        if (userTurn && checkedButton != null) {
            sequenceAction.addAction(Actions.run(new Runnable() {
                @Override
                public void run() {
                    if (screen.processCheckedButton(checkedButton)) {
                        screen.hideButtons();
                    } else {
                        screen.updateButtonsOnRound(null, userTurn);
                        OmahaPlayer omahaPlayer = playerMap.get(nextTurn);
                        if (omahaPlayer != null) {
                            if (roundChanged) {
                                omahaPlayer.removeMoveState();
                                omahaPlayer.startTimer(Color.GREEN);
                            } else {
                                omahaPlayer.removeMoveState();
                                omahaPlayer.startTimer(sender.matches(nextTurn) ? Color.ORANGE : Color.GREEN);
                            }
                        }
                    }
                }
            }));

        } else {
            sequenceAction.addAction(Actions.run(new Runnable() {
                @Override
                public void run() {
                    screen.updateButtonsOnRound(checkedButton, userTurn);
                }
            }));
            sequenceAction.addAction(Actions.delay(0.4f));
            sequenceAction.addAction(Actions.run(new Runnable() {
                @Override
                public void run() {
                    OmahaPlayer omahaPlayer = playerMap.get(nextTurn);
                    if (omahaPlayer != null) {
                        if (roundChanged) {
                            omahaPlayer.removeMoveState();
                            omahaPlayer.startTimer(Color.GREEN);
                        } else {
                            omahaPlayer.removeMoveState();
                            omahaPlayer.startTimer(sender.matches(nextTurn) ? Color.ORANGE : Color.GREEN);
                        }

                    }
                }
            }));
        }

    }


    public void handleRecoverResponse(JsonValue recoverResponse, float maxBetAmount, float minRaiseBet, float maxRaiseBet) {
        resetOnRecover();
        OmahaRecoverMessage recoverMessage = new OmahaRecoverMessage(recoverResponse,maxBetAmount);
        String[] sittingOrder = getSittingOrder(recoverMessage.joinedPlayersArray);
        for (int i = 1; i < sittingOrder.length; i++) {
            String playerId = sittingOrder[i];
            OmahaPlayer omahaPlayer = seatMap.get(i);
            if (playerId.isEmpty()) {
                seatMap.get(i).emptySeat();
            } else {
                PokerPlayerData pokerPlayerData = recoverMessage.playerDataMap.get(playerId);
                if (playerMap.containsKey(playerId)) {
                    OmahaPlayer player = playerMap.get(playerId);
                    player.emptySeat();
                    omahaPlayer.setPlayerData(pokerPlayerData);
                    if(!playerId.matches(recoverMessage.currentTurn)) {
                        omahaPlayer.setLastMoveStatus(pokerPlayerData.lastMoveStatus);
                    }
                    omahaPlayer.setPlayerState(pokerPlayerData.state);
                } else {
                    omahaPlayer.setPlayerData(pokerPlayerData);
                    if(!playerId.matches(recoverMessage.currentTurn)) {
                        omahaPlayer.setLastMoveStatus(pokerPlayerData.lastMoveStatus);
                    }
                    omahaPlayer.setPlayerState(pokerPlayerData.state);
                    playerMap.put(playerId, omahaPlayer);
                }
            }
        }
        omahaUserPlayer.setPlayerData(recoverMessage.playerDataMap.get(warpController.myID));
        if(!warpController.myID.matches(recoverMessage.currentTurn)) {
            omahaUserPlayer.setLastMoveStatus(recoverMessage.playerDataMap.get(warpController.myID).lastMoveStatus);
        }
        omahaUserPlayer.setPlayerState(recoverMessage.playerDataMap.get(warpController.myID).state);
        screen.backButton.setVisible(true);
        if (recoverMessage.openCards != null && recoverMessage.openCards.length >= 3) {
            splitPools(recoverMessage.pricePool);
        }
        screen.setTotalBetAmount(recoverMessage.totalGameBet);

        if (recoverMessage.openCards != null && recoverMessage.openCards.length > 0) {
            screen.updateOpenCards(recoverMessage.openCards);

        }

        if (recoverMessage.gameRunning) {
            gameState = PokerConstants.GAME_RUNNING;
            for (OmahaPlayer player : playerMap.values()) {
                if (player.getPlayerState() == PokerConstants.PLAYER_PLAYING || player.getPlayerState()==PokerConstants.PLAYER_ALL_IN) {
                    player.updateCards(new int[]{-1, -1,-1,-1});
                }
            }
        } else {
            gameState = PokerConstants.GAME_REST;
        }
        setMaxBet(0.0f);
        updateMaxBet(recoverMessage.maxBetAmount);

        if (recoverMessage.userCards != null && recoverMessage.userCards.length > 0
                && (omahaUserPlayer.getPlayerState() == PokerConstants.PLAYER_PLAYING || omahaUserPlayer.getPlayerState()==PokerConstants.PLAYER_ALL_IN || omahaUserPlayer.getPlayerState()==PokerConstants.PLAYER_FOLDED) ) {
            omahaUserPlayer.updateCards(recoverMessage.userCards);
            if (omahaUserPlayer.getPlayerState()!=PokerConstants.PLAYER_FOLDED) {
                if (screen.getOmahaOpenCards().size() >= 3) {
                    BestPossibleCombination bestPossibleCombination = OmahaUtils.getBestPossibleCombination(getCards(omahaUserPlayer.getHandCards().values()), getCards(screen.getOmahaOpenCards().values()), omahaUserPlayer.playerId);
                    omahaUserPlayer.updateCombinations(bestPossibleCombination.getBestCards(), cardRankingMap.get(bestPossibleCombination.getGroupType()));
                }
            }
        }

        if (recoverMessage.currentTurn.isEmpty() && recoverMessage.remainingTime <= 0) {
            setUserTurn(false);
        } else {
            setUserTurn(recoverMessage.currentTurn.matches(warpController.myID));
            if(recoverMessage.currentTurn.matches(warpController.myID)){
                setMaxRaiseBet(maxRaiseBet);
                setMinRaiseBet(minRaiseBet);
            }

        }
        setBetPlaced(recoverMessage.maxBetAmount > 0.0f);
        if (!recoverMessage.currentTurn.isEmpty()) {
            if (playerMap.get(recoverMessage.currentTurn).resumeTimer(recoverMessage.remainingTime, recoverMessage.lifeUsed)) {
                screen.updateButtonsOnRound(null, true);
            } else {
                screen.hideButtons();
            }
        } else {
            screen.hideButtons();
        }

        isRecovering = false;

        if (timerMessage != null) {
            screen.showTimer(timerMessage);
        } else if (recoverMessage.timerMessage != null && recoverMessage.timerMessage.remainingTime() > 0) {
            screen.showTimer(recoverMessage.timerMessage);
        }
        timerMessage = null;
    }


    public void handleResult(JsonValue jsonValue) {
        clearActions();
        SequenceAction sequenceAction = Actions.sequence();
        sequenceAction.addAction(Actions.delay(0.5f));
        sequenceAction.addAction(Actions.run(new Runnable() {
            @Override
            public void run() {
                for (OmahaPlayer omahaPlayer : seatMap.values()) {
                    omahaPlayer.resetTimer();
                }
                screen.hideButtons();
            }
        }));
        final OmahaResultMessage pokerResultMessage = new OmahaResultMessage(this, jsonValue);

        if (pokerResultMessage.lastPlayerMove != null) {
            OmahaResultMessage.LastPlayerMove lastPlayerMove = pokerResultMessage.lastPlayerMove;
            OmahaPlayer omahaPlayer = playerMap.get(lastPlayerMove.player);
            if (omahaPlayer != null) {
                omahaPlayer.minusBalanceAmount(lastPlayerMove.betAmount);
                omahaPlayer.updateBalanceAmount();
                omahaPlayer.updateMoveState(lastPlayerMove.moveType);
                omahaPlayer.showBetAnimation(lastPlayerMove.betAmount);
                sequenceAction.addAction(Actions.delay(0.2f));
            }
        }
        sequenceAction.addAction(Actions.run(new Runnable() {
            @Override
            public void run() {
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        if (pokerResultMessage.openCards != null && pokerResultMessage.openCards.length > 0) {
                            screen.openCards(pokerResultMessage.openCards);
                            if (omahaUserPlayer.getPlayerState()==PokerConstants.PLAYER_PLAYING || omahaUserPlayer.getPlayerState()==PokerConstants.PLAYER_ALL_IN){
                                OmahaResultMessage.PokerResultPlayer resultPlayer=pokerResultMessage.resultPlayerHashMap.get(omahaUserPlayer.getPlayerId());
                                if (resultPlayer!=null && resultPlayer.cards.length==5) {
                                    ArrayList<Integer> combinationCards = new ArrayList<Integer>();
                                    for (int i=0;i<5;i++){
                                        combinationCards.add(resultPlayer.cards[i]);
                                    }
                                    omahaUserPlayer.updateCombinations(combinationCards,cardRankingMap.get(resultPlayer.groupType));
                                }
                            }
                        }
                    }
                });
            }
        }));

        sequenceAction.addAction(Actions.delay(0.2f));

        sequenceAction.addAction(Actions.run(new Runnable() {
            @Override
            public void run() {
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        for (OmahaPlayer player : playerMap.values()) {
                            player.addBetToPool();
                        }
                    }
                });
            }
        }));
        sequenceAction.addAction(Actions.delay(0.4f));

        final List<OmahaWinningLabel> winningLabels = new ArrayList<OmahaWinningLabel>();
        for (OmahaResultMessage.PokerWinning pokerWinning : pokerResultMessage.winnings) {
            OmahaWinningLabel winningLabel = new OmahaWinningLabel(pokerWinning, betLabelStyle);
            winningLabels.add(winningLabel);
        }
        sequenceAction.addAction(Actions.run(new Runnable() {
            @Override
            public void run() {
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        startResultSplitAnimation(winningLabels);
                    }
                });
            }
        }));
        sequenceAction.addAction(Actions.delay(0.4f));
        sequenceAction.addAction(Actions.run(new Runnable() {
            @Override
            public void run() {
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        screen.setTotalBetAmount(pokerResultMessage.totalBetAmount);
                    }
                });

            }
        }));
        sequenceAction.addAction(Actions.delay(0.12f));
        sequenceAction.addAction(Actions.run(new Runnable() {
            @Override
            public void run() {
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        for (OmahaResultMessage.PokerResultPlayer player : pokerResultMessage.resultPlayerHashMap.values()) {
                            if (playerMap.containsKey(player.id)) {
                                OmahaPlayer omahaPlayer = playerMap.get(player.id);
                                if (player.id.matches(omahaUserPlayer.getPlayerId())) {
                                    omahaPlayer.setResultCards(player.handCards, 1.0f);
                                } else {
                                    omahaPlayer.setResultCards(player.handCards, -1.0f);
                                }
                            }
                        }
                    }
                });
            }
        }));

        sequenceAction.addAction(Actions.delay(0.3f));
        startWinningAnimation(sequenceAction, winningLabels, pokerResultMessage);
    }

    private void startWinningAnimation(SequenceAction sequenceAction, final List<OmahaWinningLabel> winningLabels, final OmahaResultMessage resultMessage) {
        float resultDelay = 2.0f;
        sequenceAction.addAction(
                Actions.run(
                        new Runnable() {
                            @Override
                            public void run() {
                                for (OmahaPlayer player : playerMap.values()) {
                                    if (player.getPlayerState() == PokerConstants.PLAYER_PLAYING)
                                        player.removeMoveState(true);
                                }
                                setColor(Color.GRAY);
                            }
                        }
                )
        );
        sequenceAction.addAction(Actions.delay(resultDelay));
        for (final OmahaWinningLabel winningLabel : winningLabels) {
            startPoolWinningAnimation(winningLabel, sequenceAction);
        }
//        sequenceAction.addAction(Actions.run(new Runnable() {
//            @Override
//            public void run() {
//                startRefundAnimation(resultMessage);
//            }
//        }));
        sequenceAction.addAction(Actions.run(new Runnable() {
            @Override
            public void run() {
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        betPoolTable.clear();
                    }
                });
            }
        }));
        sequenceAction.addAction(Actions.delay(0.5f));
        sequenceAction.addAction(Actions.run(new Runnable() {
            @Override
            public void run() {
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        for (final OmahaWinningLabel winningLabel : winningLabels) {
                            winningLabel.remove();
                        }
                        setResult(resultMessage);
                    }
                });
            }
        }));
        addAction(sequenceAction);
    }


    private void startPoolWinningAnimation(final OmahaWinningLabel winningLabel, final SequenceAction sequenceAction) {
        sequenceAction.addAction(Actions.run(new Runnable() {
            @Override
            public void run() {
                for (OmahaPlayer player : playerMap.values()) {
                    player.hideWinner();
                    player.setColor(getColor());
                }
            }
        }));
        for (int j = 0; j < winningLabel.getWinners().size(); j++) {
            final OmahaResultMessage.PokerPoolPlayer pokerWinner = winningLabel.getWinners().get(j);
            boolean lastPlayer = false;
            if (j == winningLabel.getWinners().size() - 1) {
                lastPlayer = true;
            }
            final boolean lastWinner = lastPlayer;
            if (playerMap.containsKey(pokerWinner.id)) {
                final OmahaPlayer omahaPlayer = playerMap.get(pokerWinner.id);
                sequenceAction.addAction(Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        omahaPlayer.setColor(Color.WHITE);
                    }
                }));
                sequenceAction.addAction(Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        omahaPlayer.showWinner();
                    }
                }));


                sequenceAction.addAction(Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        showCombinations(pokerWinner, omahaPlayer);
                    }
                }));

                sequenceAction.addAction(Actions.delay(2.0f));

                Color color = omahaPlayer.getColor();
                sequenceAction.addAction(Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        startPlayerWinningAnimation(pokerWinner.amount, omahaPlayer, winningLabel, lastWinner);
                    }
                }));
                sequenceAction.addAction(Actions.delay(1.0f));

            } else {
                if (lastPlayer) {
                    Gdx.app.postRunnable(new Runnable() {
                        @Override
                        public void run() {
                            winningLabel.remove();
                        }
                    });
                }
            }
        }
    }


    private void showCombinations(final OmahaResultMessage.PokerPoolPlayer pokerWinner, OmahaPlayer omahaPlayer) {
        if (pokerWinner.pokerResultPlayer.cards != null && pokerWinner.pokerResultPlayer.cards.length == 5) {
            HashMap<Integer, OmahaCard> allCards = new HashMap<Integer, OmahaCard>();
            allCards.putAll(omahaPlayer.getHandCards());
            allCards.putAll(screen.getOmahaOpenCards());

            List<OmahaCard> flashedCards = new ArrayList<OmahaCard>();
            List<OmahaCard> unflashedCards = new ArrayList<OmahaCard>();

            int[] combinationCards = pokerWinner.pokerResultPlayer.cards;
            for (int i = 0; i < combinationCards.length; i++) {
                if (allCards.containsKey(combinationCards[i])) {
                    flashedCards.add(allCards.get(combinationCards[i]));
                    allCards.remove(combinationCards[i]);
                }
            }
            unflashedCards.addAll(allCards.values());
            showPlayerCombination(pokerWinner.pokerResultPlayer.groupType);
            for (final OmahaCard pokerCard : flashedCards) {
                pokerCard.flashCard(true, 0.0f);
            }

            for (final OmahaCard pokerCard : unflashedCards) {
                pokerCard.flashCard(false, 0.0f);
            }
        }
    }


    private void startPlayerWinningAnimation(final float poolWinAmount, final OmahaPlayer omahaPlayer, final OmahaWinningLabel winningLabel, final boolean lastWinner) {
        if (omahaPlayer.isEmpty()) {
            if (lastWinner) winningLabel.remove();
            return;
        }
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                AmountLabel amountLabel = new AmountLabel(poolWinAmount, betLabelStyle);
                amountLabel.updateAmount(poolWinAmount);
                Vector2 endPosition = omahaPlayer.getBalancePosition();
                Vector2 startPosition = screen.localToStageCoordinates(winningLabel,new Vector2());
                amountLabel.setPosition(startPosition.x, startPosition.y);
                amountLabel.pack();
                amountLabel.setSize(amountLabel.getWidth(), betPoolHeight);
                getStage().addActor(amountLabel);
                amountLabel.addAction(
                        Actions.sequence(
                                Actions.delay(0.1f),
                                Actions.moveTo(endPosition.x, endPosition.y, 0.8f),
                                Actions.run(new Runnable() {
                                    @Override
                                    public void run() {
                                        omahaPlayer.addBalanceAmount(poolWinAmount);
                                        omahaPlayer.updateBalanceAmount();
                                        float leftAmount = winningLabel.amount - poolWinAmount;
                                        if (lastWinner) {
                                            winningLabel.remove();
                                        } else {
                                            winningLabel.updateAmount(leftAmount);
                                        }
                                    }
                                }),
                                Actions.removeActor()
                        )
                );
            }
        });
    }

    public void startRefundAnimation(OmahaResultMessage resultMessage) {
        Vector2 startPosition = screen.getTotalBetLabelPosition();
        for (OmahaResultMessage.PokerResultPlayer resultPlayer : resultMessage.resultPlayerHashMap.values()) {
            if (resultPlayer.refundAmount > 0 && playerMap.containsKey(resultPlayer.id)) {
                OmahaPlayer omahaPlayer = playerMap.get(resultPlayer.id);
                startPlayerRefundAnimation(resultPlayer.refundAmount, omahaPlayer, startPosition);
            }
        }
    }

    private void startPlayerRefundAnimation(final float refundAmount, final OmahaPlayer omahaPlayer, Vector2 startPosition) {
        AmountLabel amountLabel = new AmountLabel(refundAmount, betLabelStyle);
        amountLabel.updateAmount(refundAmount);

        Vector2 endPosition = omahaPlayer.getBalancePosition();
        amountLabel.setPosition(startPosition.x, startPosition.y);
        getStage().addActor(amountLabel);
        amountLabel.addAction(
                Actions.sequence(
                        Actions.delay(0.1f),
                        Actions.moveTo(endPosition.x, endPosition.y, 0.8f),
                        Actions.run(new Runnable() {
                            @Override
                            public void run() {
                                omahaPlayer.addBalanceAmount(refundAmount);
                                omahaPlayer.updateBalanceAmount();
                                float leftAmount = screen.getTotalBetAmount() - refundAmount;
                                screen.setTotalBetAmount(leftAmount);
                            }
                        }),
                        Actions.removeActor()
                )
        );
    }


    private void showPlayerCombination(final int groupType) {
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                CombinationLabel combinationLabel = new CombinationLabel(screen,combinationLabelStyle,combinationStars, cardRankingMap.get(groupType));
                combinationLabel.setOrigin(screen.width / 2f, screen.height / 2f);
                getStage().addActor(combinationLabel);
                combinationLabel.addAction(Actions.sequence(
                        Actions.delay(2.0f), Actions.removeActor()
                ));
            }
        });

    }


    private void setResult(final OmahaResultMessage resultMessage) {
        for (OmahaResultMessage.PokerResultPlayer player : resultMessage.resultPlayerHashMap.values()) {
            if (playerMap.containsKey(player.id)) {
                OmahaPlayer omahaPlayer = playerMap.get(player.id);
                omahaPlayer.setBalanceAmount(player.pokerBalance);
                if (player.id.matches(omahaUserPlayer.getPlayerId())) {
                    Constant.userProfile.setPaytmBalance(player.balance);
                }
            }
        }
        for (OmahaPlayer omahaPlayer : playerMap.values()) {
            omahaPlayer.updateBalanceAmount();
        }
        screen.resetScreen();
        PokerUtils.setTimeOut(1000, new TimeoutHandler() {
            @Override
            public void onTimeOut() {
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        resetAll();
                        screen.showResult(resultMessage);
                        gameState = PokerConstants.GAME_REST;
                        if (timerMessage != null) {
                            screen.showTimer(timerMessage);
                            timerMessage = null;
                        }
                        isShowingResult = false;
                    }
                });
            }
        });
    }

    private void resetAll() {
       betPoolTable.clear();
        setColor(Color.WHITE);
        for (OmahaPlayer omahaPlayer : playerMap.values()) {
            omahaPlayer.resetAll();
        }
        screen.resetScreen();
    }


    private void resetOnRecover() {
        rebuildSeats();
        screen.setTotalBetAmount(0.0f);
        screen.updateOpenCards(null);
    }


    public OmahaPlayer getPlayer(String playerId) {
        if (playerMap.containsKey(playerId)) {
            return playerMap.get(playerId);
        }
        return null;
    }

    public void updateBalanceOnRefill(String id, float pokerBalance) {
        OmahaPlayer omahaPlayer = playerMap.get(id);
        omahaPlayer.setBalanceAmount(pokerBalance);
        omahaPlayer.updateBalanceAmount();
    }

    PokerAddBalanceDialog addPokerWalletBalance;

    public void callAutoRefillApiInBackGround() {
        if (playerMap.get(warpController.myID).balanceAmount <= warpController.getBigBlind()) {
            if (screen.autoRefillButton.isChecked()) {
                float amount = warpController.getMaxAddAmount();
                if (Constant.userProfile.getPaytmBalance() >= warpController.getMinAddAmount()) {
                    if (Constant.userProfile.getPaytmBalance() < amount) {
                        amount = Constant.userProfile.getPaytmBalance();
                    }
                    callAutoRefillApi(false, amount, false);
                }
            }
        }
    }

    public void requestAutoFill() {
        if (playerMap.get(warpController.myID).balanceAmount <= warpController.getBigBlind()) {
            if (screen.autoRefillButton.isChecked()) {
                if (Constant.userProfile.getPaytmBalance() >= warpController.getMinAddAmount()) {

                } else {
                    screen.setLowBalance(true);
                    resetTimers(true);
                    warpController.disconnect();
                    addPokerWalletBalance = new PokerAddBalanceDialog(screen, warpController.getMaxAddAmount(), warpController.getMinAddAmount(), warpController.getSmallBlind()) {
                        boolean isAddItemClick;

                        @Override
                        public void addAmountClick(float amount) {
                            isAddItemClick = true;
                            if (amount <= Constant.userProfile.getPaytmBalance()) {
                                callAutoRefillApi(true, amount, true);
                            } else {
                                showAddCashDialog(amount);
                            }

                            if (addPokerWalletBalance != null) {
                                addPokerWalletBalance.hide();
                            }
                        }

                        @Override
                        public void hide() {
                            super.hide();
                            if (!isAddItemClick) {
                                OmahaWorld.this.screen.onConnectionFailed();
                            }
                        }
                    };
                    addPokerWalletBalance.show(getStage());
                }
            } else {
                screen.setLowBalance(true);
                resetTimers(true);
                warpController.disconnect();
                addPokerWalletBalance = new PokerAddBalanceDialog(screen, warpController.getMaxAddAmount(), warpController.getMinAddAmount(), warpController.getSmallBlind()) {
                    boolean isAddItemClick;

                    @Override
                    public void addAmountClick(float amount) {
                        isAddItemClick = true;
                        if (amount <= Constant.userProfile.getPaytmBalance()) {
                            callAutoRefillApi(true, amount, true);
                        } else {
                            showAddCashDialog(amount);
                        }

                        if (addPokerWalletBalance != null) {
                            addPokerWalletBalance.hide();
                        }
                    }

                    @Override
                    public void hide() {
                        super.hide();
                        if (!isAddItemClick) {
                            OmahaWorld.this.screen.onConnectionFailed();
                        }
                    }
                };
                addPokerWalletBalance.show(getStage());
            }
        }
    }

    private void showAddCashDialog(final float addAmount) {
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                new LandscapeCashAddDialog(screen,"OmahaGameScreen", addAmount,new GdxListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        if (Constant.userProfile.getPaytmBalance() >= addAmount) {
                            // showProcessDialog("Joining Game...", false);
                            if (addPokerWalletBalance != null) {
                                addPokerWalletBalance.hide();
                            }
                            callAutoRefillApi(true, addAmount, true);
                        } else {
                            screen.onConnectionFailed();
                        }
                    }

                    @Override
                    public void onFail(String reason) {
                        screen.onConnectionFailed();
                    }

                    @Override
                    public void onError(String errorMessage) {
                        screen.onConnectionFailed();
                    }
                }).show(getStage());
            }
        });

    }

    private void callAutoRefillApi(final boolean isLowBalance, float amount, final boolean isShowProgress) {
        if (isShowProgress) {
            screen.showDialog("Please wait..");
        }
        PokerApi.callRefillApi(warpController.contestId + "", amount + "", new GdxListener<String>() {
            @Override
            public void onSuccess(String s) {
                if (isShowProgress) {
                    screen.hideDialog();
                }
                if (isLowBalance) {
                    screen.setLowBalance(false);
                    PokerBalanceListener joinAfterLowBalanceSuccessListener
                            = new PokerBalanceListener(screen, warpController){
                        @Override
                        public void onJoinAfterAdCash() {
                            screen.onJoinAfterAdCash();
                        }
                    };
                    joinAfterLowBalanceSuccessListener.connect();
                } else {
                    warpController.sendUpdate(PokerConstants.MESSAGE_UPDATE_BALANCE, UpdateBalance.buildData(warpController.myID + "", Constant.userProfile.getPokerBalance()));
                }
                screen.updateBalance();
            }

            @Override
            public void onFail(String reason) {
                if (isShowProgress) {
                    screen.hideDialog();
                }
                resetTimers(true);
                warpController.disconnect();
            }

            @Override
            public void onError(String errorMessage) {
                if (isShowProgress) {
                    screen.hideDialog();
                }
                resetTimers(true);
                warpController.disconnect();
            }
        });
    }

    public ArrayList<Integer> getCards(Collection<OmahaCard> cards) {
        ArrayList<Integer> pokerCards = new ArrayList<Integer>();
        for (OmahaCard card : cards) {
            pokerCards.add(card.id);
        }
        return pokerCards;
    }

    public float getMinRaiseBet() {
        return PokerUtils.getValue(minRaiseBet);
    }

    public void setMinRaiseBet(float minRaiseBet) {
        this.minRaiseBet = minRaiseBet;
    }

    public float getMaxRaiseBet() {
        return PokerUtils.getValue(maxRaiseBet);
    }

    public void setMaxRaiseBet(float maxRaiseBet) {
        this.maxRaiseBet = maxRaiseBet;
    }
}
