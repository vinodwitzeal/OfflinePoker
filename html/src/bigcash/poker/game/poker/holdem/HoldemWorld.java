package bigcash.poker.game.poker.holdem;

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
import bigcash.poker.game.poker.widgets.CombinationLabel;
import bigcash.poker.game.poker.dialogs.PokerAddBalanceDialog;
import bigcash.poker.game.poker.PokerBalanceListener;
import bigcash.poker.game.poker.messages.PokerJoinMessage;
import bigcash.poker.game.poker.messages.PokerPlayerCustomData;
import bigcash.poker.game.poker.messages.PokerPlayerData;
import bigcash.poker.game.poker.messages.TimerMessage;
import bigcash.poker.game.poker.messages.UpdateBalance;
import bigcash.poker.game.poker.holdem.controllers.HoldemWarpController;
import bigcash.poker.game.poker.holdem.messages.HoldemDealerMessage;
import bigcash.poker.game.poker.holdem.messages.HoldemRecoverMessage;
import bigcash.poker.game.poker.holdem.messages.HoldemResultMessage;
import bigcash.poker.game.poker.holdem.widgets.HoldemCard;
import bigcash.poker.game.poker.holdem.widgets.HoldemPlayer;
import bigcash.poker.game.poker.holdem.widgets.HoldemUserPlayer;
import bigcash.poker.game.poker.holdem.widgets.HoldemWinningLabel;
import bigcash.poker.game.poker.holdem.widgets.buttons.GameButton;
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

public class HoldemWorld extends Group {
    public HoldemGameScreen screen;
    public HoldemWarpController warpController;
    public TextureAtlas atlas;
    public HashMap<Integer, String> suitMap;
    public float cardCenterX, cardCenterY, cardWidth, cardHeight;
    public TextureRegion backCardRegion, dealerRegion;

    private HoldemPlayer.PokerPlayerStyle opponentPlayerStyle, userPlayerStyle;
    public AmountLabel.PokerLabelStyle betLabelStyle;
    private LinkedHashMap<Integer, HoldemPlayer> seatMap;
    private HashMap<String, HoldemPlayer> playerMap;
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
    public Sound tickSound, checkSound;
    private long tickSoundId;
    public TimerMessage timerMessage;
    public boolean isRecovering, isShowingResult, isJoining;
    private HashMap<Integer, Vector2> positionMap;
    public float dealerX, dealerY, betPoolCenterX, betPoolCenterY, betPoolHeight, maxBet;
    public HoldemUserPlayer holdemUserPlayer;
    private Table betPoolTable;
    private AmountLabel mainBetPool;
    public Sound betSound, foldSound;

    public HoldemWorld(HoldemGameScreen screen, int maxUsers, int gameState) {
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

        this.opponentPlayerStyle = new HoldemPlayer.PokerPlayerStyle(this);
        this.opponentPlayerStyle.balanceStyle = new Label.LabelStyle();
        this.opponentPlayerStyle.balanceStyle.font = FontPool.obtain(FontType.ROBOTO_BOLD, 5.5f);
        this.opponentPlayerStyle.balanceStyle.fontColor = Color.valueOf("01dbb1");

        this.userPlayerStyle = new HoldemPlayer.PokerPlayerStyle(atlas.findRegion("img_user"), opponentPlayerStyle);
        this.userPlayerStyle.balanceStyle = new Label.LabelStyle();
        this.userPlayerStyle.balanceStyle.font = FontPool.obtain(FontType.ROBOTO_BOLD, 5.5f);
        this.userPlayerStyle.balanceStyle.fontColor = Color.valueOf("e8c100");


        this.seatMap = new LinkedHashMap<Integer, HoldemPlayer>();
        this.playerMap = new HashMap<String, HoldemPlayer>();
        this.betPoolTable = new Table();
        this.betPoolTable.setSize(screen.width, betPoolHeight);
        this.betPoolTable.setPosition(0, betPoolCenterY);
        this.betPoolTable.setTouchable(Touchable.disabled);

        this.userPlayerData = new PokerPlayerData(Constant.userProfile.getUserId(), Constant.userProfile.getName(), Constant.userProfile.getImageId(), Constant.userProfile.getPaytmBalance(), Constant.userProfile.getPokerBalance());

        smallBlind = warpController.getSmallBlind();
        bigBlind = warpController.getBigBlind();
        setPositions();
        rebuildSeats();
        setGameState(gameState);
        setCurrentRound(-1);
    }

    public void playCheckSound() {
        screen.playSound(checkSound, 1.0f);
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

    public void setMaxBet(float maxBet) {
        this.maxBet = PokerUtils.getValue(maxBet);
    }

    public void updateMaxBet(float amount) {
        if (amount > maxBet) {
            this.maxBet = PokerUtils.getValue(amount);
        }
    }


    public float getMaxBet() {
        if (this.maxBet <= 0.0f) {
            return this.getBigBlind();
        } else {
            return this.maxBet;
        }
    }

    private int getOtherPlayingPlayers() {
        int otherPlayerPlaying = 0;
        for (HoldemPlayer holdemPlayer : playerMap.values()) {
            if (!holdemPlayer.getPlayerId().matches(holdemUserPlayer.playerId) && holdemPlayer.getPlayerState() == PokerConstants.PLAYER_PLAYING) {
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
        List<AmountLabel> amountLabels = new ArrayList<AmountLabel>();
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

    private void startResultSplitAnimation(final List<HoldemWinningLabel> holdemWinningLabels) {
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                betPoolTable.clear();
                startSplitAnimation(holdemWinningLabels);
            }
        });

    }


//    private void rebuild2Seats() {
//        PokerPlayer player1 = new PokerPlayer(this, opponentPlayerStyle) {
//            @Override
//            public void setPositions() {
//                giftAlign=Align.right;
//                cardXOffset = playerStyle.imageSize / 2f;
//                detailsTable.padLeft(cardXOffset);
//                detailsTable.setPosition(0, playerTurnImage.getY() + detailsPadding);
//                betPosition.set(playerTurnImage.getX()+playerTurnImage.getWidth()/2f+detailsTable.getWidth()/2f, playerTurnImage.getY()-playerTurnImage.getHeight()/2f);
//                updateBetLabelPosition();
//            }
//
//            @Override
//            public void updateBetLabelPosition() {
//                super.updateBetLabelPosition();
//                betLabel.setPosition(betPosition.x, betPosition.y);
//            }
//        };
//
//        Vector2 position = positionMap.get(2);
//        player1.setPosition(position.x, position.y);
//
//
//        userPlayer = new UserPlayer(this, userPlayerStyle) {
//            @Override
//            public void setPositions() {
//                giftAlign = Align.right;
//                cardXOffset = playerStyle.imageSize / 2f;
//                detailsTable.padLeft(cardXOffset);
//                detailsTable.setPosition(0, playerTurnImage.getY() + detailsPadding);
//                betPosition.set(0, playerTurnImage.getY() + playerTurnImage.getHeight() + betPadding);
//                updateBetLabelPosition();
//            }
//
//            @Override
//            public void updateBetLabelPosition() {
//                super.updateBetLabelPosition();
//                betLabel.setPosition(betPosition.x - betLabel.getWidth() / 2f, betPosition.y);
//            }
//
//            @Override
//            public void onTimerFinished() {
//                stopTickSound();
//                setUserTurn(false);
//                warpController.client.sendMove("");
//                Gdx.app.postRunnable(new Runnable() {
//                    @Override
//                    public void run() {
//                        screen.hideButtons();
//                    }
//                });
//            }
//
//            @Override
//            public void ringAlarm() {
//                Gdx.app.postRunnable(new Runnable() {
//                    @Override
//                    public void run() {
//                        tickSoundId = screen.playSound(tickSound, 0.8f);
//                    }
//                });
//            }
//
//            @Override
//            public boolean isEmpty() {
//                return false;
//            }
//        };
//
//        position = positionMap.get(0);
//        userPlayer.setPosition(position.x, position.y);
//
//        addActor(player1);
//        addActor(userPlayer);
//
//
//        seatMap.put(0, userPlayer);
//        userPlayer.setPlayerData(userPlayerData);
//        playerMap.put(userPlayerData.id, userPlayer);
//        seatMap.put(1, player1);
//    }
//
//    private void rebuild5Seats() {
//        PokerPlayer player2 = new PokerPlayer(this, opponentPlayerStyle) {
//            @Override
//            public void setPositions() {
//                giftAlign=Align.right;
//                cardXOffset = playerStyle.imageSize / 2f;
//                detailsTable.padLeft(cardXOffset);
//                detailsTable.setPosition(0, playerTurnImage.getY() + detailsPadding);
//                betPosition.set(playerTurnImage.getX()+playerTurnImage.getWidth()/2f+detailsTable.getWidth()/2f, playerTurnImage.getY()-playerTurnImage.getHeight()/2f);
//                updateBetLabelPosition();
//            }
//
//            @Override
//            public void updateBetLabelPosition() {
//                super.updateBetLabelPosition();
//                betLabel.setPosition(betPosition.x, betPosition.y);
//            }
//        };
//        Vector2 position = positionMap.get(2);
//        player2.setPosition(position.x, position.y);
//
//        PokerPlayer player3 = new PokerPlayer(this, opponentPlayerStyle) {
//            @Override
//            public void setPositions() {
//                giftAlign=Align.left;
//                cardXOffset = playerStyle.imageSize / 2f;
//                detailsTable.padRight(cardXOffset);
//                cardXOffset = -cardXOffset;
//                detailsTable.setPosition(-detailsTable.getWidth(), playerTurnImage.getY() + detailsPadding);
//                betPosition.set(playerTurnImage.getX()+playerTurnImage.getWidth()/2f-detailsTable.getWidth()/2f, playerTurnImage.getY()-playerTurnImage.getHeight()/2f);
//                updateBetLabelPosition();
//            }
//
//            @Override
//            public void updateBetLabelPosition() {
//                super.updateBetLabelPosition();
//                betLabel.setPosition(betPosition.x - betLabel.getWidth(), betPosition.y);
//            }
//        };
//
//        position = positionMap.get(3);
//        player3.setPosition(position.x, position.y);
//
//
//        PokerPlayer player1 = new PokerPlayer(this, opponentPlayerStyle) {
//            @Override
//            public void setPositions() {
//                giftAlign=Align.right;
//                cardXOffset = playerStyle.imageSize / 2f;
//                detailsTable.padLeft(cardXOffset);
//                detailsTable.setPosition(0, playerTurnImage.getY() + detailsPadding);
//                betPosition.set(playerTurnImage.getX()+playerTurnImage.getWidth()/2f+detailsTable.getWidth()/2f, playerTurnImage.getY()+playerTurnImage.getHeight()+detailsTable.getHeight()/2f);
//                updateBetLabelPosition();
//            }
//
//            @Override
//            public void updateBetLabelPosition() {
//                super.updateBetLabelPosition();
//                betLabel.setPosition(betPosition.x, betPosition.y);
//            }
//        };
//
//        position = positionMap.get(1);
//        player1.setPosition(position.x, position.y);
//
//
//        userPlayer = new UserPlayer(this, userPlayerStyle) {
//            @Override
//            public void setPositions() {
//                giftAlign=Align.right;
//                cardXOffset = playerStyle.imageSize / 2f;
//                detailsTable.padLeft(cardXOffset);
//                detailsTable.setPosition(0, playerTurnImage.getY() + detailsPadding);
//                betPosition.set(0, playerTurnImage.getY() + playerTurnImage.getHeight() + betPadding);
//            }
//
//            @Override
//            public void updateBetLabelPosition() {
//                super.updateBetLabelPosition();
//                betLabel.setPosition(betPosition.x - betLabel.getWidth() / 2f, betPosition.y);
//            }
//
//            @Override
//            public void onTimerFinished() {
//                stopTickSound();
//                setUserTurn(false);
//                warpController.client.sendMove("");
//                Gdx.app.postRunnable(new Runnable() {
//                    @Override
//                    public void run() {
//                        screen.hideButtons();
//                    }
//                });
//            }
//
//            @Override
//            public void ringAlarm() {
//                Gdx.app.postRunnable(new Runnable() {
//                    @Override
//                    public void run() {
//                        tickSoundId = screen.playSound(tickSound, 0.8f);
//                    }
//                });
//            }
//
//            @Override
//            public boolean isEmpty() {
//                return false;
//            }
//        };
//
//        position = positionMap.get(0);
//        userPlayer.setPosition(position.x, position.y);
//
//        PokerPlayer player4 = new PokerPlayer(this, opponentPlayerStyle) {
//            @Override
//            public void setPositions() {
//                giftAlign=Align.left;
//                cardXOffset = playerStyle.imageSize / 2f;
//                detailsTable.padRight(cardXOffset);
//                cardXOffset = -cardXOffset;
//                detailsTable.setPosition(-detailsTable.getWidth(), playerTurnImage.getY() + detailsPadding);
//                betPosition.set(playerTurnImage.getX()+playerTurnImage.getWidth()/2f-detailsTable.getWidth()/2f, playerTurnImage.getY()+playerTurnImage.getHeight()+detailsTable.getHeight()/2f);
//                updateBetLabelPosition();
//            }
//
//            @Override
//            public void updateBetLabelPosition() {
//                super.updateBetLabelPosition();
//                betLabel.setPosition(betPosition.x - betLabel.getWidth(), betPosition.y);
//            }
//        };
//
//        position = positionMap.get(4);
//        player4.setPosition(position.x, position.y);
//
//        addActor(player1);
//        addActor(player2);
//        addActor(player3);
//        addActor(player4);
//        addActor(userPlayer);
//
//
//        seatMap.put(0, userPlayer);
//        userPlayer.setPlayerData(userPlayerData);
//        playerMap.put(userPlayerData.id, userPlayer);
//        seatMap.put(1, player1);
//        seatMap.put(2, player2);
//        seatMap.put(3, player3);
//        seatMap.put(4, player4);
//
//    }

    private void rebuild2Seats() {
        HoldemPlayer player1 = new HoldemPlayer(this, opponentPlayerStyle) {
            @Override
            public void setPositions() {
                giftAlign=Align.right;
                cardXOffset = playerStyle.imageSize / 2f;
                detailsTable.padRight(cardXOffset);
                cardXOffset = -cardXOffset;
                detailsTable.setPosition(-detailsTable.getWidth(), playerTurnImage.getY() + detailsPadding);
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


        holdemUserPlayer = new HoldemUserPlayer(this, userPlayerStyle) {
            @Override
            public void setPositions() {
                giftAlign=Align.right;
                cardXOffset = playerStyle.imageSize / 2f;
                detailsTable.padLeft(cardXOffset);
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
        holdemUserPlayer.setPosition(position.x, position.y);

        addActor(player1);
        addActor(holdemUserPlayer);


        seatMap.put(0, holdemUserPlayer);
        holdemUserPlayer.setPlayerData(userPlayerData);
        playerMap.put(userPlayerData.id, holdemUserPlayer);
        seatMap.put(1, player1);
    }

    private void rebuild5Seats() {
        HoldemPlayer player2 = new HoldemPlayer(this, opponentPlayerStyle) {
            @Override
            public void setPositions() {
                giftAlign=Align.right;
                cardXOffset = playerStyle.imageSize / 2f;
                detailsTable.padRight(cardXOffset);
                cardXOffset = -cardXOffset;
                detailsTable.setPosition(-detailsTable.getWidth(), playerTurnImage.getY() + detailsPadding);
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

        HoldemPlayer player3 = new HoldemPlayer(this, opponentPlayerStyle) {
            @Override
            public void setPositions() {
                giftAlign=Align.left;
                cardXOffset = playerStyle.imageSize / 2f;
                detailsTable.padLeft(cardXOffset);
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


        HoldemPlayer player1 = new HoldemPlayer(this, opponentPlayerStyle) {
            @Override
            public void setPositions() {
                giftAlign=Align.right;
                cardXOffset = playerStyle.imageSize / 2f;
                detailsTable.padRight(cardXOffset);
                cardXOffset = -cardXOffset;
                detailsTable.setPosition(-detailsTable.getWidth(), playerTurnImage.getY() + detailsPadding);
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


        holdemUserPlayer = new HoldemUserPlayer(this, userPlayerStyle) {
            @Override
            public void setPositions() {
                giftAlign=Align.right;
                cardXOffset = playerStyle.imageSize / 2f;
                detailsTable.padLeft(cardXOffset);
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
        holdemUserPlayer.setPosition(position.x, position.y);

        HoldemPlayer player4 = new HoldemPlayer(this, opponentPlayerStyle) {
            @Override
            public void setPositions() {
                giftAlign=Align.left;
                cardXOffset = playerStyle.imageSize / 2f;
                detailsTable.padLeft(cardXOffset);
                detailsTable.setPosition(0, playerTurnImage.getY() + detailsPadding);
                betPosition.set(playerTurnImage.getX() - betPadding, playerTurnImage.getY() + playerTurnImage.getHeight() - playerStyle.betHeight - betPadding);
                updateBetLabelPosition();
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
        addActor(holdemUserPlayer);


        seatMap.put(0, holdemUserPlayer);
        holdemUserPlayer.setPlayerData(userPlayerData);
        playerMap.put(userPlayerData.id, holdemUserPlayer);
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
        for (HoldemPlayer player : playerMap.values()) {
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
                sittingPlayers[i] = holdemUserPlayer.getPlayerId();
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
        PokerJoinMessage pokerJoinMessage = new PokerJoinMessage(jsonValue, maxBetAmount);
        String[] sittingOrder = getSittingOrder(pokerJoinMessage.joinedPlayersArray);
        for (int i = 1; i < sittingOrder.length; i++) {
            String playerId = sittingOrder[i];
            HoldemPlayer holdemPlayer = seatMap.get(i);
            if (playerId.isEmpty()) {
                seatMap.get(i).emptySeat();
            } else {
                PokerPlayerData pokerPlayerData = pokerJoinMessage.playerDataMap.get(playerId);
                if (playerMap.containsKey(playerId)) {
                    HoldemPlayer player = playerMap.get(playerId);
                    player.emptySeat();
                    holdemPlayer.setPlayerData(pokerPlayerData);
                    if (!playerId.matches(pokerJoinMessage.currentTurn)) {
                        holdemPlayer.setLastMoveStatus(pokerPlayerData.lastMoveStatus);
                    }
                    holdemPlayer.setPlayerState(pokerPlayerData.state);

                } else {
                    holdemPlayer.setPlayerData(pokerPlayerData);
                    if (!playerId.matches(pokerJoinMessage.currentTurn)) {
                        holdemPlayer.setLastMoveStatus(pokerPlayerData.lastMoveStatus);
                    }
                    holdemPlayer.setPlayerState(pokerPlayerData.state);
                    playerMap.put(playerId, holdemPlayer);
                }
            }
        }
        holdemUserPlayer.setPlayerData(pokerJoinMessage.playerDataMap.get(warpController.myID));
        if (!warpController.myID.matches(pokerJoinMessage.currentTurn)) {
            holdemUserPlayer.setLastMoveStatus(pokerJoinMessage.playerDataMap.get(warpController.myID).lastMoveStatus);
        }

        holdemUserPlayer.setPlayerState(pokerJoinMessage.playerDataMap.get(warpController.myID).state);
        screen.setTotalBetAmount(pokerJoinMessage.totalGameBet);

        if (pokerJoinMessage.openCards != null && pokerJoinMessage.openCards.length > 0) {
            screen.updateOpenCards(pokerJoinMessage.openCards);
        }

        if (pokerJoinMessage.openCards != null && pokerJoinMessage.openCards.length >= 3) {
            splitPools(pokerJoinMessage.pricePool);
        }

        if (pokerJoinMessage.gameRunning) {
            gameState = PokerConstants.GAME_RUNNING;
            for (HoldemPlayer player : playerMap.values()) {
                if (player.getPlayerState() == PokerConstants.PLAYER_PLAYING) {
                    player.updateCards(new int[]{-1, -1});
                }
            }
        } else {
            gameState = PokerConstants.GAME_REST;
        }

        if (pokerJoinMessage.userCards != null && pokerJoinMessage.userCards.length > 0 && holdemUserPlayer.getPlayerState() == PokerConstants.PLAYER_PLAYING) {
            holdemUserPlayer.updateCards(pokerJoinMessage.userCards);
        }

        if (pokerJoinMessage.currentTurn.isEmpty() && pokerJoinMessage.remainingTime <= 0) {
            setUserTurn(false);
        } else {
            setUserTurn(pokerJoinMessage.currentTurn.matches(warpController.myID));
        }

        setMaxBet(0.0f);
        updateMaxBet(pokerJoinMessage.maxBetAmount);

        setBetPlaced(pokerJoinMessage.totalRoundBet > 0.0f);
        if (!pokerJoinMessage.currentTurn.isEmpty()) {
            playerMap.get(pokerJoinMessage.currentTurn).resumeTimer(pokerJoinMessage.remainingTime, pokerJoinMessage.lifeUsed);
            screen.updateButtonsOnRound(null, true);
        } else {
            screen.hideButtons();
        }

        isJoining = false;

        if (timerMessage != null) {
            screen.showTimer(timerMessage);
        } else if (pokerJoinMessage.timerMessage != null && pokerJoinMessage.timerMessage.remainingTime() > 0) {
            screen.showTimer(pokerJoinMessage.timerMessage);
        } else {
            if (gameState == PokerConstants.GAME_REST) {
                if (playerMap == null || playerMap.size() == 1) {
                    screen.showMessage("Waiting for players");
                } else {
                    screen.showMessage("Game Begins Shortly");
                }
            }
        }
        timerMessage = null;

    }


    public void onUserJoined(JsonValue jsonValue) {
        PokerPlayerCustomData pokerPlayerCustomData = new PokerPlayerCustomData(jsonValue);
        PokerPlayerData pokerPlayerData = new PokerPlayerData(pokerPlayerCustomData, gameState == PokerConstants.GAME_REST ? PokerConstants.PLAYER_PLAYING : PokerConstants.PLAYER_WATCHING);
        addPlayer(pokerPlayerData);
    }


    public void addPlayer(PokerPlayerData playerData) {
        HoldemPlayer holdemPlayer = getEmptySeat();
        if (holdemPlayer != null) {
            if (playerMap.containsKey(playerData.id)) {
                HoldemPlayer player = playerMap.get(playerData.id);
                player.emptySeat();
                player.setPlayerData(playerData);
                player.setPlayerState(gameState == PokerConstants.GAME_REST ? PokerConstants.PLAYER_PLAYING : PokerConstants.PLAYER_WATCHING);

            } else {
                holdemPlayer.setPlayerData(playerData);
                holdemPlayer.setPlayerState(gameState == PokerConstants.GAME_REST ? PokerConstants.PLAYER_PLAYING : PokerConstants.PLAYER_WATCHING);
                playerMap.put(playerData.id, holdemPlayer);
            }
        }
    }

    public HoldemPlayer getEmptySeat() {
        for (int i = 0; i < maxUsers; i++) {
            HoldemPlayer playerTable = seatMap.get(i);
            if (playerTable.isEmpty()) {
                return playerTable;
            }
        }
        return null;
    }


    public void removePlayer(String playerId) {
        HoldemPlayer holdemPlayer = playerMap.get(playerId);
        if (holdemPlayer != null) {
            holdemPlayer.resetTimer();
            holdemPlayer.emptySeat();
        }
        playerMap.remove(playerId);
        if (playerMap.size() == 1) {
            screen.backButton.setVisible(true);
        }
    }

    public void setPlayerCards(int[] cards) {
        if (cards == null || cards.length == 0) {
            holdemUserPlayer.removeCards();
        } else {
            float delay = 0.0f;
            delay = holdemUserPlayer.dealCards(cards, delay);
            for (HoldemPlayer holdemPlayer : playerMap.values()) {
                if (!holdemPlayer.getPlayerId().matches(warpController.myID) && holdemPlayer.getPlayerState() == PokerConstants.PLAYER_PLAYING) {
                    delay = holdemPlayer.dealCards(new int[]{-1, -1}, delay);
                }
            }
        }
    }

    public void setDealer(HoldemDealerMessage holdemDealerMessage) {
        screen.stopTimer();
        screen.updateOpenCards(null);
        screen.resetScreen();
        screen.hideResult();
        rebuildSeats();

        String[] sittingOrder = getSittingOrder(holdemDealerMessage.joinedPlayersArray);
        for (int i = 1; i < sittingOrder.length; i++) {
            String playerId = sittingOrder[i];
            HoldemPlayer holdemPlayer = seatMap.get(i);
            if (playerId.isEmpty()) {
                seatMap.get(i).emptySeat();
            } else {
                PokerPlayerData pokerPlayerData = holdemDealerMessage.playerDataMap.get(playerId);
                if (playerMap.containsKey(playerId)) {
                    HoldemPlayer player = playerMap.get(playerId);
                    player.emptySeat();
                    holdemPlayer.setPlayerData(pokerPlayerData);
                    holdemPlayer.setPlayerState(pokerPlayerData.state);
                } else {
                    holdemPlayer.setPlayerData(pokerPlayerData);
                    holdemPlayer.setPlayerState(pokerPlayerData.state);
                    playerMap.put(playerId, holdemPlayer);
                }
            }
        }
        holdemUserPlayer.setPlayerData(holdemDealerMessage.playerDataMap.get(warpController.myID));
        holdemUserPlayer.setPlayerState(holdemDealerMessage.playerDataMap.get(warpController.myID).state);

        HoldemPlayer holdemPlayer = playerMap.get(holdemDealerMessage.getDealer());
        if (holdemPlayer != null) {
            holdemPlayer.setDealer();
        }
        smallBlind = holdemDealerMessage.getSmallBlind().getBet();
        bigBlind = holdemDealerMessage.getBigBlind().getBet();
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
                        for (HoldemPlayer player : playerMap.values()) {
                            if (player.getPlayerState() == PokerConstants.PLAYER_PLAYING) {
                                player.removeMoveState(false);
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
                        if ((holdemUserPlayer.getPlayerState() == PokerConstants.PLAYER_PLAYING || holdemUserPlayer.getPlayerState() == PokerConstants.PLAYER_ALL_IN) && holdemUserPlayer.getHandCards().size() == 2 && screen.getHoldemOpenCards().size() >= 3) {
                            BestPossibleCombination bestPossibleCombination = PokerUtils.getBestPossibleCombination(getCards(holdemUserPlayer.getHandCards().values()), getCards(screen.getHoldemOpenCards().values()), holdemUserPlayer.playerId);
                            holdemUserPlayer.updateCombinations(bestPossibleCombination.getBestCards(), cardRankingMap.get(bestPossibleCombination.getGroupType()));
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
        if (isStopGameTimer) {
            screen.stopTimer();
        }
        stopTickSound();
        for (HoldemPlayer holdemPlayer : playerMap.values()) {
            holdemPlayer.resetTimer();
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
                for (HoldemPlayer holdemPlayer : playerMap.values()) {
                    if (holdemPlayer.getPlayerState() != PokerConstants.PLAYER_WATCHING) {
                        holdemPlayer.setPlayerState(PokerConstants.PLAYER_PLAYING);
                    }
                    holdemPlayer.resetTimer();
                }
                HoldemPlayer holdemPlayer = playerMap.get(playerId);
                if (holdemPlayer != null) {
                    holdemPlayer.startTimer(Color.GREEN);
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
        for (HoldemPlayer holdemPlayer : playerMap.values()) {
            if (!moveEvent.getSender().matches(holdemPlayer.getPlayerId())) {
                holdemPlayer.resetTimer();
            } else {
                holdemPlayer.cancelTimer();
            }
        }
        if (moveEvent.getMoveData() != null && !moveEvent.getMoveData().isEmpty()) {
            final JsonValue moveData = new JsonReader().parse(moveEvent.getMoveData());
            String moveType = moveData.getString("moveType", PokerConstants.MOVE_CHECK);
            if (moveType.matches(PokerConstants.MOVE_CHECK) && !sender.matches(warpController.myID)) {
                playCheckSound();
            }
            final float betAmount = moveData.getFloat("betAmount", 0.0f);
            if (betAmount > 0.0f) {
                setBetPlaced(true);
            }


            float totalGameBet = PokerUtils.getValue(moveData.getFloat("totalGameBet"));
            screen.setTotalBetAmount(totalGameBet);
            HoldemPlayer holdemPlayer = playerMap.get(sender);
            if (holdemPlayer != null) {
                holdemPlayer.minusBalanceAmount(betAmount);
                holdemPlayer.updateBalanceAmount();
                holdemPlayer.updateMoveState(moveType);
                holdemPlayer.showBetAnimation(betAmount);
                updateMaxBet(holdemPlayer.betAmount);
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
                for (HoldemPlayer player : playerMap.values()) {
                    player.resetTimer();
                }
            }
        }));

        setNextTurn(sequenceAction, nextTurn, sender, roundChanged);
        addAction(sequenceAction);

    }

    private void setNextTurn(SequenceAction sequenceAction, final String nextTurn, final String sender, final boolean roundChanged) {
        setUserTurn(nextTurn.equals(warpController.myID));
        final GameButton checkedButton = screen.getCheckedButton();
        if (userTurn && checkedButton != null) {
            sequenceAction.addAction(Actions.run(new Runnable() {
                @Override
                public void run() {
                    if (screen.processCheckedButton(checkedButton)) {
                        screen.hideButtons();
                    } else {
                        screen.updateButtonsOnRound(null, userTurn);
                        HoldemPlayer holdemPlayer = playerMap.get(nextTurn);
                        if (holdemPlayer != null) {
                            if (roundChanged) {
                                holdemPlayer.removeMoveState(false);
                                holdemPlayer.startTimer(Color.GREEN);
                            } else {
                                holdemPlayer.removeMoveState(false);
                                holdemPlayer.startTimer(sender.matches(nextTurn) ? Color.ORANGE : Color.GREEN);
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
                    HoldemPlayer holdemPlayer = playerMap.get(nextTurn);
                    if (holdemPlayer != null) {
                        if (roundChanged) {
                            holdemPlayer.removeMoveState(false);
                            holdemPlayer.startTimer(Color.GREEN);
                        } else {
                            holdemPlayer.removeMoveState(false);
                            holdemPlayer.startTimer(sender.matches(nextTurn) ? Color.ORANGE : Color.GREEN);
                        }

                    }
                }
            }));
        }

    }


    public void handleRecoverResponse(JsonValue recoverResponse, float maxBetAmount) {
        resetOnRecover();
        HoldemRecoverMessage holdemRecoverMessage = new HoldemRecoverMessage(recoverResponse, maxBetAmount);
        String[] sittingOrder = getSittingOrder(holdemRecoverMessage.joinedPlayersArray);
        for (int i = 1; i < sittingOrder.length; i++) {
            String playerId = sittingOrder[i];
            HoldemPlayer holdemPlayer = seatMap.get(i);
            if (playerId.isEmpty()) {
                seatMap.get(i).emptySeat();
            } else {
                PokerPlayerData pokerPlayerData = holdemRecoverMessage.playerDataMap.get(playerId);
                if (playerMap.containsKey(playerId)) {
                    HoldemPlayer player = playerMap.get(playerId);
                    player.emptySeat();
                    holdemPlayer.setPlayerData(pokerPlayerData);
                    if (!playerId.matches(holdemRecoverMessage.currentTurn)) {
                        holdemPlayer.setLastMoveStatus(pokerPlayerData.lastMoveStatus);
                    }
                    holdemPlayer.setPlayerState(pokerPlayerData.state);
                } else {
                    holdemPlayer.setPlayerData(pokerPlayerData);
                    if (!playerId.matches(holdemRecoverMessage.currentTurn)) {
                        holdemPlayer.setLastMoveStatus(pokerPlayerData.lastMoveStatus);
                    }
                    holdemPlayer.setPlayerState(pokerPlayerData.state);
                    playerMap.put(playerId, holdemPlayer);
                }
            }
        }
        holdemUserPlayer.setPlayerData(holdemRecoverMessage.playerDataMap.get(warpController.myID));
        if (!warpController.myID.matches(holdemRecoverMessage.currentTurn)) {
            holdemUserPlayer.setLastMoveStatus(holdemRecoverMessage.playerDataMap.get(warpController.myID).lastMoveStatus);
        }
        holdemUserPlayer.setPlayerState(holdemRecoverMessage.playerDataMap.get(warpController.myID).state);
        screen.backButton.setVisible(true);
        if (holdemRecoverMessage.openCards != null && holdemRecoverMessage.openCards.length >= 3) {
            splitPools(holdemRecoverMessage.pricePool);
        }
        screen.setTotalBetAmount(holdemRecoverMessage.totalGameBet);

        if (holdemRecoverMessage.openCards != null && holdemRecoverMessage.openCards.length > 0) {
            screen.updateOpenCards(holdemRecoverMessage.openCards);

        }

        if (holdemRecoverMessage.gameRunning) {
            gameState = PokerConstants.GAME_RUNNING;
            for (HoldemPlayer player : playerMap.values()) {
                if (player.getPlayerState() == PokerConstants.PLAYER_PLAYING || player.getPlayerState() == PokerConstants.PLAYER_ALL_IN) {
                    player.updateCards(new int[]{-1, -1});
                }
            }
        } else {
            gameState = PokerConstants.GAME_REST;
        }
        setMaxBet(0.0f);
        updateMaxBet(holdemRecoverMessage.maxBetAmount);

        if (holdemRecoverMessage.userCards != null && holdemRecoverMessage.userCards.length > 0
                && (holdemUserPlayer.getPlayerState() == PokerConstants.PLAYER_PLAYING || holdemUserPlayer.getPlayerState() == PokerConstants.PLAYER_ALL_IN || holdemUserPlayer.getPlayerState() == PokerConstants.PLAYER_FOLDED)) {
            holdemUserPlayer.updateCards(holdemRecoverMessage.userCards);
            if (holdemUserPlayer.getPlayerState() != PokerConstants.PLAYER_FOLDED) {
                if (screen.getHoldemOpenCards().size() >= 3) {
                    BestPossibleCombination bestPossibleCombination = PokerUtils.getBestPossibleCombination(getCards(holdemUserPlayer.getHandCards().values()), getCards(screen.getHoldemOpenCards().values()), holdemUserPlayer.playerId);
                    holdemUserPlayer.updateCombinations(bestPossibleCombination.getBestCards(), cardRankingMap.get(bestPossibleCombination.getGroupType()));
                }
            }
        }

        if (holdemRecoverMessage.currentTurn.isEmpty() && holdemRecoverMessage.remainingTime <= 0) {
            setUserTurn(false);
        } else {
            setUserTurn(holdemRecoverMessage.currentTurn.matches(warpController.myID));
        }
        setBetPlaced(holdemRecoverMessage.maxBetAmount > 0.0f);
        if (!holdemRecoverMessage.currentTurn.isEmpty()) {
            if (playerMap.get(holdemRecoverMessage.currentTurn).resumeTimer(holdemRecoverMessage.remainingTime, holdemRecoverMessage.lifeUsed)) {
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
        } else if (holdemRecoverMessage.timerMessage != null && holdemRecoverMessage.timerMessage.remainingTime() > 0) {
            screen.showTimer(holdemRecoverMessage.timerMessage);
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
                for (HoldemPlayer holdemPlayer : seatMap.values()) {
                    holdemPlayer.resetTimer();
                }
                screen.hideButtons();
            }
        }));
        final HoldemResultMessage holdemResultMessage = new HoldemResultMessage(this, jsonValue);

        if (holdemResultMessage.lastPlayerMove != null) {
            HoldemResultMessage.LastPlayerMove lastPlayerMove = holdemResultMessage.lastPlayerMove;
            HoldemPlayer holdemPlayer = playerMap.get(lastPlayerMove.player);
            if (holdemPlayer != null) {
                holdemPlayer.minusBalanceAmount(lastPlayerMove.betAmount);
                holdemPlayer.updateBalanceAmount();
                holdemPlayer.updateMoveState(lastPlayerMove.moveType);
                holdemPlayer.showBetAnimation(lastPlayerMove.betAmount);
                sequenceAction.addAction(Actions.delay(0.2f));
            }
        }
        sequenceAction.addAction(Actions.run(new Runnable() {
            @Override
            public void run() {
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        if (holdemResultMessage.openCards != null && holdemResultMessage.openCards.length > 0) {
                            screen.openCards(holdemResultMessage.openCards);
                            if (holdemUserPlayer.getPlayerState() == PokerConstants.PLAYER_PLAYING || holdemUserPlayer.getPlayerState() == PokerConstants.PLAYER_ALL_IN) {
                                HoldemResultMessage.PokerResultPlayer resultPlayer = holdemResultMessage.resultPlayerHashMap.get(holdemUserPlayer.getPlayerId());
                                if (resultPlayer != null && resultPlayer.cards != null && resultPlayer.cards.length == 5) {
                                    ArrayList<Integer> combinationCards = new ArrayList<Integer>();
                                    for (int i = 0; i < 5; i++) {
                                        combinationCards.add(resultPlayer.cards[i]);
                                    }
                                    holdemUserPlayer.updateCombinations(combinationCards, cardRankingMap.get(resultPlayer.groupType));
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
                        for (HoldemPlayer player : playerMap.values()) {
                            player.addBetToPool();
                        }
                    }
                });
            }
        }));
        sequenceAction.addAction(Actions.delay(0.4f));

        final List<HoldemWinningLabel> holdemWinningLabels = new ArrayList<HoldemWinningLabel>();
        for (HoldemResultMessage.PokerWinning pokerWinning : holdemResultMessage.winnings) {
            HoldemWinningLabel holdemWinningLabel = new HoldemWinningLabel(pokerWinning, betLabelStyle);
            holdemWinningLabels.add(holdemWinningLabel);
        }
        sequenceAction.addAction(Actions.run(new Runnable() {
            @Override
            public void run() {
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        startResultSplitAnimation(holdemWinningLabels);
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
                        screen.setTotalBetAmount(holdemResultMessage.totalBetAmount);
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
                        for (HoldemResultMessage.PokerResultPlayer player : holdemResultMessage.resultPlayerHashMap.values()) {
                            if (playerMap.containsKey(player.id)) {
                                HoldemPlayer holdemPlayer = playerMap.get(player.id);
                                if (player.id.matches(holdemUserPlayer.getPlayerId())) {
                                    holdemPlayer.setResultCards(player.handCards, 1.0f);
                                } else {
                                    holdemPlayer.setResultCards(player.handCards, -1.0f);
                                }
                            }
                        }
                    }
                });
            }
        }));

        sequenceAction.addAction(Actions.delay(0.3f));
        startWinningAnimation(sequenceAction, holdemWinningLabels, holdemResultMessage);
    }

    private void startWinningAnimation(SequenceAction sequenceAction, final List<HoldemWinningLabel> holdemWinningLabels, final HoldemResultMessage resultMessage) {
        float resultDelay = 2.0f;
        sequenceAction.addAction(
                Actions.run(
                        new Runnable() {
                            @Override
                            public void run() {
                                for (HoldemPlayer player : playerMap.values()) {
                                    if (player.getPlayerState() == PokerConstants.PLAYER_PLAYING)
                                        player.removeMoveState(true);
                                }
                                setColor(Color.GRAY);
                            }
                        }
                )
        );
        sequenceAction.addAction(Actions.delay(resultDelay));
        for (final HoldemWinningLabel holdemWinningLabel : holdemWinningLabels) {
            startPoolWinningAnimation(holdemWinningLabel, sequenceAction);
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
                        for (final HoldemWinningLabel holdemWinningLabel : holdemWinningLabels) {
                            holdemWinningLabel.remove();
                        }
                        setResult(resultMessage);
                    }
                });
            }
        }));
        addAction(sequenceAction);
    }


    private void startPoolWinningAnimation(final HoldemWinningLabel holdemWinningLabel, final SequenceAction sequenceAction) {
        sequenceAction.addAction(Actions.run(new Runnable() {
            @Override
            public void run() {
                for (HoldemPlayer player : playerMap.values()) {
                    player.hideWinner();
                    player.setColor(getColor());
                }
            }
        }));
        for (int j = 0; j < holdemWinningLabel.getWinners().size(); j++) {
            final HoldemResultMessage.PokerPoolPlayer pokerWinner = holdemWinningLabel.getWinners().get(j);
            boolean lastPlayer = false;
            if (j == holdemWinningLabel.getWinners().size() - 1) {
                lastPlayer = true;
            }
            final boolean lastWinner = lastPlayer;
            if (playerMap.containsKey(pokerWinner.id)) {
                final HoldemPlayer holdemPlayer = playerMap.get(pokerWinner.id);
                sequenceAction.addAction(Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        holdemPlayer.setColor(Color.WHITE);
                    }
                }));
                sequenceAction.addAction(Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        holdemPlayer.showWinner();
                    }
                }));


                sequenceAction.addAction(Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        showCombinations(pokerWinner, holdemPlayer);
                    }
                }));

                sequenceAction.addAction(Actions.delay(2.0f));

                Color color = holdemPlayer.getColor();
                sequenceAction.addAction(Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        startPlayerWinningAnimation(pokerWinner.amount, holdemPlayer, holdemWinningLabel, lastWinner);
                    }
                }));
                sequenceAction.addAction(Actions.delay(1.0f));

            } else {
                if (lastPlayer) {
                    Gdx.app.postRunnable(new Runnable() {
                        @Override
                        public void run() {
                            holdemWinningLabel.remove();
                        }
                    });
                }
            }
        }
    }


    private void showCombinations(final HoldemResultMessage.PokerPoolPlayer pokerWinner, HoldemPlayer holdemPlayer) {
        if (pokerWinner.pokerResultPlayer.cards != null && pokerWinner.pokerResultPlayer.cards.length == 5) {
            HashMap<Integer, HoldemCard> allCards = new HashMap<Integer, HoldemCard>();
            allCards.putAll(holdemPlayer.getHandCards());
            allCards.putAll(screen.getHoldemOpenCards());

            List<HoldemCard> flashedCards = new ArrayList<HoldemCard>();
            List<HoldemCard> unflashedCards = new ArrayList<HoldemCard>();

            int[] combinationCards = pokerWinner.pokerResultPlayer.cards;
            for (int i = 0; i < combinationCards.length; i++) {
                if (allCards.containsKey(combinationCards[i])) {
                    flashedCards.add(allCards.get(combinationCards[i]));
                    allCards.remove(combinationCards[i]);
                }
            }
            unflashedCards.addAll(allCards.values());
            showPlayerCombination(pokerWinner.pokerResultPlayer.groupType);
            for (final HoldemCard holdemCard : flashedCards) {
                holdemCard.flashCard(true, 0.0f);
            }

            for (final HoldemCard holdemCard : unflashedCards) {
                holdemCard.flashCard(false, 0.0f);
            }
        }
    }


    private void startPlayerWinningAnimation(final float poolWinAmount, final HoldemPlayer holdemPlayer, final HoldemWinningLabel holdemWinningLabel, final boolean lastWinner) {
        if (holdemPlayer.isEmpty()) {
            if (lastWinner) holdemWinningLabel.remove();
            return;
        }
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                AmountLabel amountLabel = new AmountLabel(poolWinAmount, betLabelStyle);
                amountLabel.updateAmount(poolWinAmount);
                Vector2 endPosition = holdemPlayer.getBalancePosition();
                Vector2 startPosition =screen.localToStageCoordinates(holdemWinningLabel,new Vector2());
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
                                        holdemPlayer.addBalanceAmount(poolWinAmount);
                                        holdemPlayer.updateBalanceAmount();
                                        float leftAmount = holdemWinningLabel.amount - poolWinAmount;
                                        if (lastWinner) {
                                            holdemWinningLabel.remove();
                                        } else {
                                            holdemWinningLabel.updateAmount(leftAmount);
                                        }
                                    }
                                }),
                                Actions.removeActor()
                        )
                );
            }
        });
    }

    public void startRefundAnimation(HoldemResultMessage resultMessage) {
        Vector2 startPosition = screen.getTotalBetLabelPosition();
        for (HoldemResultMessage.PokerResultPlayer resultPlayer : resultMessage.resultPlayerHashMap.values()) {
            if (resultPlayer.refundAmount > 0 && playerMap.containsKey(resultPlayer.id)) {
                HoldemPlayer holdemPlayer = playerMap.get(resultPlayer.id);
                startPlayerRefundAnimation(resultPlayer.refundAmount, holdemPlayer, startPosition);
            }
        }
    }

    private void startPlayerRefundAnimation(final float refundAmount, final HoldemPlayer holdemPlayer, Vector2 startPosition) {
        AmountLabel amountLabel = new AmountLabel(refundAmount, betLabelStyle);
        amountLabel.updateAmount(refundAmount);

        Vector2 endPosition = holdemPlayer.getBalancePosition();
        amountLabel.setPosition(startPosition.x, startPosition.y);
        getStage().addActor(amountLabel);
        amountLabel.addAction(
                Actions.sequence(
                        Actions.delay(0.1f),
                        Actions.moveTo(endPosition.x, endPosition.y, 0.8f),
                        Actions.run(new Runnable() {
                            @Override
                            public void run() {
                                holdemPlayer.addBalanceAmount(refundAmount);
                                holdemPlayer.updateBalanceAmount();
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


    private void setResult(final HoldemResultMessage resultMessage) {
        for (HoldemResultMessage.PokerResultPlayer player : resultMessage.resultPlayerHashMap.values()) {
            if (playerMap.containsKey(player.id)) {
                HoldemPlayer holdemPlayer = playerMap.get(player.id);
                holdemPlayer.setBalanceAmount(player.pokerBalance);
                if (player.id.matches(holdemUserPlayer.getPlayerId())) {
                    Constant.userProfile.setPaytmBalance(player.balance);
                }
            }
        }
        for (HoldemPlayer holdemPlayer : playerMap.values()) {
            holdemPlayer.updateBalanceAmount();
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
        for (HoldemPlayer holdemPlayer : playerMap.values()) {
            holdemPlayer.resetAll();
        }
        screen.resetScreen();
    }


    private void resetOnRecover() {
        rebuildSeats();
        screen.setTotalBetAmount(0.0f);
        screen.updateOpenCards(null);
    }


    public HoldemPlayer getPlayer(String playerId) {
        if (playerMap.containsKey(playerId)) {
            return playerMap.get(playerId);
        }
        return null;
    }

    public void updateBalanceOnRefill(String id, float pokerBalance) {
        HoldemPlayer holdemPlayer = playerMap.get(id);
        holdemPlayer.setBalanceAmount(pokerBalance);
        holdemPlayer.updateBalanceAmount();
    }

    PokerAddBalanceDialog pokerAddBalanceDialog;

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
                    pokerAddBalanceDialog = new PokerAddBalanceDialog(screen, warpController.getMaxAddAmount(), warpController.getMinAddAmount(), warpController.getSmallBlind()) {
                        boolean isAddItemClick;

                        @Override
                        public void addAmountClick(float amount) {
                            isAddItemClick = true;
                            if (amount <= Constant.userProfile.getPaytmBalance()) {
                                callAutoRefillApi(true, amount, true);
                            } else {
                                showAddCashDialog(amount);
                            }

                            if (pokerAddBalanceDialog != null) {
                                pokerAddBalanceDialog.hide();
                            }
                        }

                        @Override
                        public void hide() {
                            super.hide();
                            if (!isAddItemClick) {
                                HoldemWorld.this.screen.onConnectionFailed();
                            }
                        }
                    };
                    pokerAddBalanceDialog.show(screen.stage);
                }
            } else {
                screen.setLowBalance(true);
                resetTimers(true);
                warpController.disconnect();
                pokerAddBalanceDialog = new PokerAddBalanceDialog(screen, warpController.getMaxAddAmount(), warpController.getMinAddAmount(), warpController.getSmallBlind()) {
                    boolean isAddItemClick;

                    @Override
                    public void addAmountClick(float amount) {
                        isAddItemClick = true;
                        if (amount <= Constant.userProfile.getPaytmBalance()) {
                            callAutoRefillApi(true, amount, true);
                        } else {
                            showAddCashDialog(amount);
                        }

                        if (pokerAddBalanceDialog != null) {
                            pokerAddBalanceDialog.hide();
                        }
                    }

                    @Override
                    public void hide() {
                        super.hide();
                        if (!isAddItemClick) {
                            HoldemWorld.this.screen.onConnectionFailed();
                        }
                    }
                };
                pokerAddBalanceDialog.show(screen.stage);
            }
        }
    }

    private void showAddCashDialog(final float addAmount) {
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                new LandscapeCashAddDialog(screen, "PokerGameScreen", addAmount, new GdxListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        if (Constant.userProfile.getPaytmBalance() >= addAmount) {
                            // showProcessDialog("Joining Game...", false);
                            if (pokerAddBalanceDialog != null) {
                                pokerAddBalanceDialog.hide();
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
                    PokerBalanceListener holdemBalanceListener = new PokerBalanceListener(screen, warpController){
                        @Override
                        public void onJoinAfterAdCash() {
                            screen.onJoinAfterAdCash();
                        }
                    };
                    holdemBalanceListener.connect();
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

    public ArrayList<Integer> getCards(Collection<HoldemCard> cards) {
        ArrayList<Integer> pokerCards = new ArrayList<Integer>();
        for (HoldemCard card : cards) {
            pokerCards.add(card.id);
        }
        return pokerCards;
    }

}
