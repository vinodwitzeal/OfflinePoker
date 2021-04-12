package bigcash.poker.game.poker.holdem;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import bigcash.poker.appwarp.codes.WarpResponseResultCode;
import bigcash.poker.appwarp.events.LiveRoomInfoEvent;
import bigcash.poker.appwarp.events.LiveUserInfoEvent;
import bigcash.poker.appwarp.events.MatchedRoomEvent;
import bigcash.poker.appwarp.events.MoveEvent;
import bigcash.poker.constants.Constant;
import bigcash.poker.dialogs.EmojiDialog;
import bigcash.poker.dialogs.ProcessDialog;
import bigcash.poker.font.FontPool;
import bigcash.poker.font.FontType;
import bigcash.poker.game.poker.messages.EmojiMessage;
import bigcash.poker.game.poker.PokerAbstractScreen;
import bigcash.poker.game.poker.messages.PokerMessage;
import bigcash.poker.game.poker.dialogs.PokerQuitDialog;
import bigcash.poker.game.poker.dialogs.PokerResultDialog;
import bigcash.poker.game.poker.messages.TimerMessage;
import bigcash.poker.game.poker.messages.UpdateBalance;
import bigcash.poker.game.poker.holdem.controllers.HoldemWarpController;
import bigcash.poker.game.poker.holdem.messages.HoldemDealerMessage;
import bigcash.poker.game.poker.holdem.messages.HoldemResultMessage;
import bigcash.poker.game.poker.holdem.widgets.HoldemCard;
import bigcash.poker.game.poker.holdem.widgets.HoldemOpenCards;
import bigcash.poker.game.poker.holdem.widgets.HoldemPlayer;
import bigcash.poker.game.poker.holdem.widgets.HoldemRaiseTable;
import bigcash.poker.game.poker.holdem.widgets.HoldemUserPlayer;
import bigcash.poker.game.poker.holdem.widgets.buttons.CallAnyButton;
import bigcash.poker.game.poker.holdem.widgets.buttons.CallButton;
import bigcash.poker.game.poker.holdem.widgets.buttons.CheckButton;
import bigcash.poker.game.poker.holdem.widgets.buttons.CheckedCallAny;
import bigcash.poker.game.poker.holdem.widgets.buttons.FoldButton;
import bigcash.poker.game.poker.holdem.widgets.buttons.FoldCheckButton;
import bigcash.poker.game.poker.holdem.widgets.buttons.GameButton;
import bigcash.poker.game.poker.holdem.widgets.buttons.RaiseButton;
import bigcash.poker.gwt.PokerGame;
import bigcash.poker.screens.PokerContestScreen;
import bigcash.poker.utils.PokerConstants;
import bigcash.poker.utils.PokerTimer;
import bigcash.poker.utils.PokerUtils;
import bigcash.poker.utils.TextureDrawable;
import bigcash.poker.widgets.Emoji;
import bigcash.poker.widgets.StyledLabel;

public class HoldemGameScreen extends PokerAbstractScreen { ;
    public HoldemWorld holdemWorld;
    public HoldemWarpController warpController;
    private ProcessDialog dialog;
    public HoldemRaiseTable holdemRaiseTable;
    private TotalBetLabel totalBetLabel;
    private int contestId;
    private float buttonPad, sidePad, buttonHeight;
    private Button soundButton, infoButton, resultButton;
    private HoldemOpenCards holdemOpenCards;
    private Table timerTable;
    private Label timerLabel;
    public boolean screenPaused;
    private PokerResultDialog resultDialog;
    private Table mainTable;
    public Button autoRefillButton;
    public Button backButton;
    private LinkedHashMap<Integer, GameButton> buttonHashMap;
    private GameButton callAnyButton, callButton, checkButton, foldButton, foldCheckButton, checkedCallAny;
    public GameButton raiseButton;
    private float betCenterX, betCenterY;
    private boolean lowBalance;
    private Label walletLabel;
    private EmojiDialog emojiDialog;
    private Emoji.EmojiErrorListener emojiErrorListener;
    private PokerTimer pokerTimer;
    private PokerTimer.PokerTimerUpdater timerUpdater;
    private String qrId;
    private String tableId;
    private int tableType;
    private int gameState;

    public HoldemGameScreen(final PokerGame pokerGame, HoldemWarpController warpController, int gameState, int contestId,String qrId, int tableType) {
        super(pokerGame,warpController,"HoldemGameScreen");
        this.warpController = warpController;
        this.contestId = contestId;
        this.gameState=gameState;
        this.qrId=qrId;
        this.tableType=tableType;
    }

    @Override
    public void initStage() {
        super.initStage(); holdemWorld = new HoldemWorld(this, warpController.getMaxUsers(), gameState);
        this.dialog = new ProcessDialog(this, "");
        NinePatchDrawable emojiDialogBackground = new NinePatchDrawable(new NinePatch(uiAtlas.findRegion("poker_dialog"), 33, 33, 30, 30));
        emojiDialog = new EmojiDialog(this, emojiDialogBackground, uiAtlas.findRegion("poker_emoji"), uiAtlas.findRegion("icon_close"), uiAtlas.findRegion("poker_arrow"));
        build();
        emojiErrorListener = new Emoji.EmojiErrorListener() {
            @Override
            public void onDownloadFailed(Emoji emoji) {
                pokerGame.downloadEmoji(emoji.getEmojiName(), emoji);
            }
        };

        if (Constant.emojis!=null) {
            for (String emoji : Constant.emojis) {
                addEmoji(emoji);
            }
        }
    }

    public HoldemGameScreen(PokerGame pokerGame, HoldemWarpController warpController, int contestId, String qrId, int tableType) {
        this(pokerGame, warpController, PokerConstants.GAME_REST, contestId,qrId,tableType);
    }



    public void addEmoji(String name) {
        final Emoji emoji = emojiDialog.addEmoji(name, emojiErrorListener);
        emoji.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (emoji.isDownloaded()) {
                    String receiver = emojiDialog.getReceiver();
                    emojiDialog.hide();
                    sendEmoji(receiver, emoji.getEmojiName());
                    showEmojiAnimation(holdemWorld.holdemUserPlayer, emoji, receiver);
                }
            }
        });
        pokerGame.downloadEmoji(name, emoji);
    }

    public void showEmojiDialog(float x, float y, int align, String receiver) {
        emojiDialog.setReceiver(receiver);
        emojiDialog.show(x, y, align);
    }

    public void sendEmoji(String receiver, String emoji) {
        JsonValue jsonValue = EmojiMessage.buildData(warpController.myID, receiver, emoji);
        warpController.sendUpdate(PokerConstants.MESSAGE_EMOJI, jsonValue);
    }


    public void showEmojiAnimation(HoldemUserPlayer senderPlayer, Emoji emoji, String receiver) {
        HoldemPlayer receiverPlayer = holdemWorld.getPlayer(receiver);
        if (receiverPlayer != null) {
            float emojiSize = emojiDialog.emojiSize;
            final float toX = receiverPlayer.getX() - emojiSize / 2f;
            final float toY = receiverPlayer.getY() - emojiSize / 2f;
            final Emoji emoji2 = new Emoji(emoji);
            float fromX = senderPlayer.getX() - emojiSize / 2f;
            float fromY = senderPlayer.getY() - emojiSize / 2f;
            emoji2.setSize(emojiSize, emojiSize);
            emoji2.setPosition(fromX, fromY);
            emoji2.addAction(Actions.sequence(
                    Actions.delay(0.2f),
                    Actions.moveTo(toX, toY, 1.0f),
                    Actions.run(new Runnable() {
                        @Override
                        public void run() {
                            emoji2.play = true;
                        }
                    }),
                    Actions.delay(1.0f),
                    Actions.scaleTo(0, 0, 0.2f, Interpolation.circleIn),
                    Actions.removeActor()
            ));
            stage.addActor(emoji2);
        }
    }

    public void showEmojiAnimation(String sender, String receiver, String emojiName) {
        HoldemPlayer senderPlayer = holdemWorld.getPlayer(sender);
        HoldemPlayer receiverPlayer = holdemWorld.getPlayer(receiver);
        if (senderPlayer != null && receiverPlayer != null) {
            float emojiSize = emojiDialog.emojiSize;
            final float toX = receiverPlayer.getX() - emojiSize / 2f;
            final float toY = receiverPlayer.getY() - emojiSize / 2f;
            Emoji emoji = new Emoji(emojiName) {
                @Override
                public void setEmoji(TextureRegion textureRegion) {
                    super.setEmoji(textureRegion);
                    addAction(Actions.sequence(
                            Actions.scaleTo(0, 0, 0),
                            Actions.scaleTo(1, 1, 0.2f, Interpolation.circleOut),
                            Actions.moveTo(toX, toY, 1.0f),
                            Actions.run(new Runnable() {
                                @Override
                                public void run() {
                                    play = true;
                                }
                            }),
                            Actions.delay(1.0f),
                            Actions.scaleTo(0, 0, 0.2f, Interpolation.circleIn),

                            Actions.removeActor()
                    ));
                }
            };
            float fromX = senderPlayer.getX() - emojiSize / 2f;
            float fromY = senderPlayer.getY() - emojiSize / 2f;

            emoji.setSize(emojiSize, emojiSize);
            emoji.setPosition(fromX, fromY);
            stage.addActor(emoji);
            pokerGame.downloadEmoji(emojiName, emoji);
        }
    }


    public boolean isLowBalance() {
        return lowBalance;
    }

    public void setLowBalance(boolean lowBalance) {
        this.lowBalance = lowBalance;
    }

    @Override
    public void init() {
    }

    @Override
    public void build() {
        holdemWorld = new HoldemWorld(this, warpController.getMaxUsers(), gameState);
        this.dialog = new ProcessDialog(this, "");
        NinePatchDrawable emojiDialogBackground = new NinePatchDrawable(new NinePatch(uiAtlas.findRegion("poker_dialog"), 33, 33, 30, 30));
        emojiDialog = new EmojiDialog(this, emojiDialogBackground, uiAtlas.findRegion("poker_emoji"), uiAtlas.findRegion("icon_close"), uiAtlas.findRegion("poker_arrow"));

        mainTable = new Table();
        TextureRegionDrawable background = new TextureRegionDrawable(atlas.findRegion("background"));
        background.setMinSize(width,height);
        mainTable.setBackground(background);
        holdemWorld.setPosition(0, 0);
        holdemWorld.setSize(width, height);
        mainTable.addActor(holdemWorld);

        Table totalBetTable = new Table();


        Label.LabelStyle betLabelStyle = new Label.LabelStyle();
        betLabelStyle.font = FontPool.obtain(FontType.ROBOTO_BOLD, 6);
        betLabelStyle.fontColor = Color.WHITE;
        totalBetLabel = new TotalBetLabel(0, betLabelStyle);
        float labelHeight = betLabelStyle.font.getLineHeight() * 1.2f;
        totalBetLabel.setVisible(false);
        totalBetTable.setSize(width * 0.35f, labelHeight);
        totalBetTable.setPosition((width - totalBetTable.getWidth()) / 2f, height * 0.82f - labelHeight);
        totalBetTable.add(totalBetLabel).height(labelHeight);

        mainTable.addActor(totalBetTable);

        holdemOpenCards = new HoldemOpenCards(holdemWorld);
        holdemOpenCards.setTouchable(Touchable.disabled);
        holdemOpenCards.setPosition(width / 2f, height * 0.59f);
        holdemOpenCards.setSize(width, height);
        mainTable.addActor(holdemOpenCards);

        float buttonSize = height * 0.1f;
        sidePad = buttonSize * 0.1f;

        Table topTable = new Table();
        topTable.top();
        topTable.pad(sidePad);
        topTable.padBottom(height * 0.2f);
        topTable.setTouchable(Touchable.childrenOnly);
        TextureRegionDrawable backDrawable = TextureDrawable.getDrawable(atlas.findRegion("btn_back"), buttonSize, buttonSize);
        backButton = new Button(backDrawable, backDrawable, backDrawable);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                onBackKeyPressed();
            }
        });

        topTable.add(backButton).width(buttonSize).height(buttonSize).expandX().align(Align.left);

        TextureRegionDrawable soundOffDrawable = TextureDrawable.getDrawable(atlas.findRegion("btn_sound_off"), buttonSize, buttonSize);
        TextureRegionDrawable soundOnDrawable = TextureDrawable.getDrawable(atlas.findRegion("btn_sound_on"), buttonSize, buttonSize);

        soundButton = new Button(soundOffDrawable, soundOffDrawable, soundOnDrawable);
        soundButton.setChecked(pokerGame.isGameSoundOn());

        Table autoRefillTable = new Table();
        autoRefillTable.pad(sidePad * 2);
        autoRefillTable.setBackground(new NinePatchDrawable(new NinePatch(atlas.findRegion("bg_autorefill"), 11, 11, 11, 11)));
        Label.LabelStyle autoRefillStyle = new Label.LabelStyle();
        autoRefillStyle.font = FontPool.obtain(FontType.ROBOTO_REGULAR, 5);
        autoRefillStyle.fontColor = Color.WHITE;

        float autoRefillButtonSize = autoRefillStyle.font.getLineHeight();

        TextureRegionDrawable uncheckedDrawable = new TextureRegionDrawable(atlas.findRegion("box_uncheck"));
        TextureRegionDrawable checkedDrawable = new TextureRegionDrawable(atlas.findRegion("box_check"));

        autoRefillButton = new Button(uncheckedDrawable, uncheckedDrawable, checkedDrawable);
        autoRefillButton.setTouchable(Touchable.disabled);
        autoRefillTable.add(autoRefillButton).width(autoRefillButtonSize).height(autoRefillButtonSize).padRight(sidePad);
        autoRefillButton.setChecked(preferences.isAutoRefillButtonChecked());
        autoRefillTable.add(new Label("Auto Refill", autoRefillStyle));
        topTable.add(autoRefillTable).padRight(sidePad);
        autoRefillTable.setTouchable(Touchable.enabled);
        autoRefillTable.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                autoRefillButton.setChecked(!autoRefillButton.isChecked());
                pokerGame.setAutoRefillButtonChecked(autoRefillButton.isChecked());
            }
        });

        Table walletTable = new Table();
        walletTable.pad(sidePad * 2);
        walletTable.setBackground(new NinePatchDrawable(new NinePatch(atlas.findRegion("bg_autorefill"), 11, 11, 11, 11)));
        walletLabel = new Label(PokerUtils.getValue(Constant.userProfile.getPaytmBalance()) + "", autoRefillStyle);
        TextureRegion walletIcon = uiAtlas.findRegion("icon_wallet");
        float walletHeight = autoRefillButtonSize;
        float walletWidth = walletHeight * walletIcon.getRegionWidth() / walletIcon.getRegionHeight();
        walletTable.add(new Image(walletIcon)).width(walletWidth).height(walletHeight).padRight(sidePad);
        walletTable.add(walletLabel);
        topTable.add(walletTable);


        TextureRegionDrawable infoButtonDrawable = TextureDrawable.getDrawable(atlas.findRegion("btn_info"), buttonSize, buttonSize);
        infoButton = new Button(infoButtonDrawable, infoButtonDrawable, infoButtonDrawable);
        infoButton.setVisible(false);
        topTable.row();

        TextureRegionDrawable historyButtonDrawable = TextureDrawable.getDrawable(atlas.findRegion("btn_result"), buttonSize, buttonSize);
        resultButton = new Button(historyButtonDrawable, historyButtonDrawable, historyButtonDrawable);
        resultButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showPreviousResult();
            }
        });
        resultButton.setVisible(false);
        topTable.add(resultButton).width(buttonSize).height(buttonSize).colspan(2).expand().align(Align.bottomLeft);
        topTable.add(soundButton).width(buttonSize).height(buttonSize).align(Align.bottomRight);


        addLayer(mainTable);


        timerTable = new Table();
        timerTable.top();
        timerTable.padTop(height * 0.2f);
        Label.LabelStyle timerLabelStyle = new Label.LabelStyle();
        timerLabelStyle.font = FontPool.obtain(FontType.ROBOTO_BOLD, 5);
        timerLabelStyle.fontColor = Color.WHITE;
        timerLabelStyle.background = new TextureRegionDrawable(atlas.findRegion("bg_label"));
        timerLabel = new Label("", timerLabelStyle);
        timerLabel.setAlignment(Align.center);
        timerTable.add(timerLabel).width(width * 0.4f).height(timerLabelStyle.font.getLineHeight() * 1.5f);
        timerTable.setTouchable(Touchable.disabled);
        addLayer(timerTable);
        timerTable.setVisible(false);
        timerTable.setClip(false);

        timerUpdater = new PokerTimer.PokerTimerUpdater() {
            @Override
            public void onTick(final long millisUntilFinished) {
                final int remainingSeconds = (int) (millisUntilFinished / 1000);
                setRemainingGameStartTimeOnPause(millisUntilFinished);
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        if (remainingSeconds > 0) {
                            if (remainingSeconds <= 3) {
                                if (holdemWorld.getPlayingPlayersCount() == 1) {
                                    backButton.setVisible(true);
                                } else {
                                    backButton.setVisible(false);
                                }
                            }
                            timerLabel.setText("Game Begins in " + remainingSeconds + " " + (remainingSeconds > 1 ? "seconds" : "second"));
                        }
                    }
                });
            }

            @Override
            public void onFinish() {
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        stopTimer();
                        if (holdemWorld.getPlayingPlayersCount() == 1) {
                            backButton.setVisible(true);
                        } else {
                            backButton.setVisible(false);
                        }
                    }
                });
            }
        };

        addLayer(topTable);


        StyledLabel.StyledLabelStyle buttonLabelStyle = new StyledLabel.StyledLabelStyle(FontType.ROBOTO_BOLD, 7.0f);
        buttonLabelStyle.fontColor = Color.WHITE;
        buttonHeight = buttonLabelStyle.font.getLineHeight() * 2.5f;
        buttonPad = buttonLabelStyle.font.getLineHeight();
        buttonHashMap = new LinkedHashMap<Integer, GameButton>();

        foldButton = new FoldButton(holdemWorld, 1, buttonLabelStyle, uncheckedDrawable, checkedDrawable);
        buttonHashMap.put(1, foldButton);
        foldCheckButton = new FoldCheckButton(holdemWorld, 2, buttonLabelStyle, uncheckedDrawable, checkedDrawable);
        buttonHashMap.put(2, foldCheckButton);
        checkButton = new CheckButton(holdemWorld, 3, buttonLabelStyle, uncheckedDrawable, checkedDrawable);
        buttonHashMap.put(3, checkButton);
        callButton = new CallButton(holdemWorld, 4, buttonLabelStyle, uncheckedDrawable, checkedDrawable);
        buttonHashMap.put(4, callButton);

        callAnyButton = new CallAnyButton(holdemWorld, 5, buttonLabelStyle, uncheckedDrawable, checkedDrawable);
        buttonHashMap.put(5, callAnyButton);

        checkedCallAny = new CheckedCallAny(holdemWorld, 6, buttonLabelStyle, uncheckedDrawable, checkedDrawable);
        buttonHashMap.put(6, checkedCallAny);

        raiseButton = new RaiseButton(holdemWorld, 7, buttonLabelStyle, uncheckedDrawable, checkedDrawable);
        buttonHashMap.put(7, raiseButton);

        holdemRaiseTable = new HoldemRaiseTable(holdemWorld, warpController.getSmallBlind(), warpController.getBigBlind());
        holdemRaiseTable.setVisible(false);

        emojiErrorListener = new Emoji.EmojiErrorListener() {
            @Override
            public void onDownloadFailed(Emoji emoji) {
                pokerGame.downloadEmoji(emoji.getEmojiName(), emoji);
            }
        };

        if (Constant.emojis!=null) {
            for (String emoji : Constant.emojis) {
                addEmoji(emoji);
            }
        }

    }

    public void showMessage(String message) {
        timerLabel.setText(message);
        timerLabel.clearActions();
        timerLabel.addAction(Actions.repeat(RepeatAction.FOREVER,
                Actions.sequence(Actions.fadeIn(0.28f), Actions.delay(0.72f), Actions.fadeOut(0.28f))));
        timerTable.setVisible(true);
    }

    public void vibrate(int millis) {
    }

    public void updateBalance() {
        walletLabel.setText(PokerUtils.getValue(Constant.userProfile.getPaytmBalance()) + "");
    }

    public void resetScreen() {
        holdemOpenCards.removeCards();
        setTotalBetAmount(0);
    }

    public void showPreviousResult() {
        if (previousResultMessage != null) {
            new PokerResultDialog(this, previousResultMessage).show(stage);
        }
    }

    private HoldemResultMessage previousResultMessage;

    public void showResult(HoldemResultMessage resultMessage) {
        if (resultDialog != null) {
            resultDialog.hide();
        }
        this.previousResultMessage = resultMessage;
        resultButton.setVisible(true);
        resultDialog = new PokerResultDialog(this, resultMessage) {
            @Override
            public void hide() {
                super.hide();
                holdemWorld.requestAutoFill();
                resultDialog = null;
            }
        };
        resultDialog.show(stage);
        holdemWorld.callAutoRefillApiInBackGround();
        holdemWorld.setGameState(PokerConstants.GAME_REST);
    }

    public void hideResult() {
        if (resultDialog != null) {
            resultDialog.hide();
        }

        resultDialog = null;
    }

    public void showTimer(TimerMessage timerMessage) {
        long remainingTime = timerMessage.remainingTime();
        if (remainingTime > 0) {
            final int remainingSeconds = (int) (remainingTime / 1000);
            timerLabel.setText("Game Begins in " + remainingSeconds + " " + (remainingSeconds > 1 ? "seconds" : "second"));
            timerLabel.clearActions();
            timerLabel.addAction(Actions.fadeIn(0.0f));
            startTimer(remainingTime);
            timerTable.setVisible(true);
        }
    }

    public void stopTimer() {
        if (pokerTimer != null) {
            pokerTimer.cancel();
        }
        pokerTimer = null;
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                timerLabel.clearActions();
                timerTable.setVisible(false);
            }
        });

    }


    public void startTimer(long totalTime) {
        if (pokerTimer != null) {
            pokerTimer.cancel();
        }
        pokerTimer=new PokerTimer(totalTime,1000,timerUpdater);
        pokerTimer.start();
    }

    public long playSound(final Sound sound, final float volume) {
        if (soundButton.isChecked()) {
            return sound.play(volume);
        }
        return -1;
    }

    public void setTotalBetAmount(float totalBetAmount) {
        totalBetLabel.updateAmount(totalBetAmount);
    }

    public float getTotalBetAmount() {
        return totalBetLabel.amount;
    }

    public Vector2 getTotalBetLabelPosition() {
        return localToStageCoordinates(totalBetLabel,new Vector2());
    }





    public void showDialog(final String message) {
        dialog.messageLabel.setText(message);
        dialog.show(getStage());
    }

    public void hideDialog() {
        dialog.hide();
    }


    public void hideButtons() {
        holdemRaiseTable.setVisible(false);
        removeButtons();
    }


    public void removeButtons() {
        for (GameButton gameButton : buttonHashMap.values()) {
            gameButton.updateButton(GameButton.NO_ACTION, 0.0f);
            mainTable.removeActor(gameButton);
        }
        mainTable.removeActor(holdemRaiseTable);
    }

    public void onButtonChecked(int id) {
        for (GameButton gameButton : buttonHashMap.values()) {
            if (gameButton.getId() != id && gameButton.getState() == GameButton.CHECKED) {
                gameButton.updateButton(GameButton.UNCHECKED, gameButton.getAmount());
            }
        }
    }


    public GameButton getCheckedButton() {
        for (GameButton gameButton : buttonHashMap.values()) {
            if (gameButton.getState() == GameButton.CHECKED) {
                return gameButton;
            }
        }
        return null;
    }


    public boolean processCheckedButton(GameButton gameButton) {
        boolean userTurn = holdemWorld.isUserTurn();
        boolean betPlaced = holdemWorld.isBetPlaced();
        float maxBet = PokerUtils.getValue(holdemWorld.getMaxBet());
        float balance = PokerUtils.getValue(holdemWorld.holdemUserPlayer.balanceAmount);
        float betAmount = PokerUtils.getValue(holdemWorld.holdemUserPlayer.betAmount);
        if (gameButton.processButton(userTurn, betPlaced, balance, betAmount, maxBet)) {
            gameButton.setState(GameButton.NO_ACTION);
            return true;
        }
        return false;
    }


    public void updateButtonsOnRound(GameButton checkedButton, boolean showAnimation) {
        int playerState = holdemWorld.holdemUserPlayer.getPlayerState();
        float amount = 0.0f;
        if (checkedButton != null) {
            amount = checkedButton.getAmount();
        }
        if (playerState != PokerConstants.PLAYER_PLAYING) {
            holdemRaiseTable.setVisible(false);
            hideButtons();
            return;
        }

        boolean userTurn = holdemWorld.isUserTurn();
        boolean betPlaced = holdemWorld.isBetPlaced();
        float maxBet = PokerUtils.getValue(holdemWorld.getMaxBet());
        float balance = PokerUtils.getValue(holdemWorld.holdemUserPlayer.balanceAmount);
        float betAmount = PokerUtils.getValue(holdemWorld.holdemUserPlayer.betAmount);
        removeButtons();
        List<GameButton> gameButtons = new ArrayList<GameButton>();
        float maxButtonWidth = 0.0f;
        for (GameButton gameButton : buttonHashMap.values()) {
            if (gameButton.updateButton(userTurn, betPlaced, balance, betAmount, maxBet)) {
                gameButton.pack();
                float buttonWidth = gameButton.getWidth();
                if (maxButtonWidth < buttonWidth) {
                    maxButtonWidth = buttonWidth;
                }
                if (checkedButton != null) {
                    if (checkedButton.getId() == 2 && gameButton.getId() == 1) {
                        gameButton.updateButton(GameButton.CHECKED, gameButton.getAmount());
                    } else {
                        if (gameButton.getId() == checkedButton.getId()) {
                            if (checkedButton.getId() == 4) {
                                if (gameButton.getAmount() == amount) {
                                    gameButton.updateButton(GameButton.CHECKED, gameButton.getAmount());
                                }
                            } else {
                                gameButton.updateButton(GameButton.CHECKED, gameButton.getAmount());
                            }
                        }
                    }
                }
                gameButtons.add(gameButton);
            }
        }
        holdemRaiseTable.setVisible(false);
        float totalWidth = maxButtonWidth * gameButtons.size() + 2 * buttonPad * (gameButtons.size() - 1);
        float startX = (width - totalWidth) / 2f;
        float buttonY = buttonPad / 2f;
        float bottomY = -buttonHeight - buttonPad;
        for (GameButton gameButton : gameButtons) {
            gameButton.clearActions();
            gameButton.setSize(maxButtonWidth, buttonHeight);

            if (showAnimation) {
                gameButton.setPosition(startX, bottomY);
                gameButton.addAction(Actions.sequence(
                        Actions.fadeIn(0.0f)
                        , Actions.moveTo(
                                startX, buttonY, 0.2f
                        )
                ));
            } else {
                gameButton.setPosition(startX, buttonY);
                gameButton.getColor().a = 0.0f;
                gameButton.addAction(Actions.sequence(
                        Actions.fadeIn(0.2f)
                ));
            }
            mainTable.addActor(gameButton);
            startX = startX + 2 * buttonPad + maxButtonWidth;
        }

        if (raiseButton.getState() != GameButton.NO_ACTION) {
            holdemRaiseTable.pack();
            holdemRaiseTable.setSize(holdemRaiseTable.getWidth(), holdemRaiseTable.getHeight());
            holdemRaiseTable.setPosition(raiseButton.getX(), buttonY + raiseButton.getHeight());
            mainTable.addActor(holdemRaiseTable);
        }
    }

    public void sendMove(String moveType, float betAmount) {
        if (holdemWorld.isUserTurn()) {
            if (moveType.matches(PokerConstants.MOVE_CHECK)) {
            }
            holdemWorld.holdemUserPlayer.cancelTimer();
            holdemWorld.stopTickSound();
            JsonValue moveData = new JsonValue(JsonValue.ValueType.object);
            moveData.addChild("moveType", new JsonValue(moveType));
            moveData.addChild("betAmount", new JsonValue(betAmount));
            String moveMessage = moveData.toJson(JsonWriter.OutputType.json);
            warpController.client.sendMove(moveMessage);
            holdemWorld.setUserTurn(false);
            hideButtons();
        }
    }

    public void updateOpenCards(int[] cards) {
        holdemOpenCards.updateCards(cards);
    }

    public void openCards(int[] cards) {
        holdemOpenCards.openCards(cards);
    }

    public HashMap<Integer, HoldemCard> getHoldemOpenCards() {
        return holdemOpenCards.getCards();
    }


    @Override
    public void show() {
        build();
        super.show();
        if (holdemWorld.getGameState() == PokerConstants.GAME_REST) {
            holdemWorld.isJoining = true;
            JsonValue matchProperties=new JsonValue(JsonValue.ValueType.object);

            if(tableType == PokerConstants.PUBLIC_TABLE) {
                matchProperties.addChild("variant",new JsonValue(contestId));
                matchProperties.addChild(PokerConstants.KEY_ROOM_STATUS,new JsonValue(PokerConstants.FRESH_ROOM_STATUS));
            }else{
                matchProperties.addChild("variant",new JsonValue(contestId));
                matchProperties.addChild("qrId",new JsonValue(qrId));
                matchProperties.addChild(PokerConstants.KEY_ROOM_STATUS,new JsonValue(PokerConstants.FRESH_ROOM_STATUS));
            }
            warpController.joinGame(matchProperties);
            showDialog("Joining Table...");
        } else {
            holdemWorld.isRecovering = true;
            showDialog("Reconnecting...");
            if (warpController.client.isConnected()) {
                warpController.sendRecoverRequest();
            } else {
                resumeConnection();
            }
        }
    }

    @Override
    public void pause() {
        super.pause();
        resetScreen();
        if (!isLowBalance()) {
            screenPaused = true;
            if (holdemWorld.getPlayingPlayersCount() == 1) {
                setRemainingGameStartTimeOnPause(20000);
            }
            holdemWorld.isJoining = false;
            holdemWorld.isShowingResult = false;
            holdemWorld.timerMessage = null;
            holdemWorld.isRecovering = true;
            holdemWorld.resetTimers(false);
        }
        hideResult();
        holdemWorld.rebuildSeats();
    }


    public void resume() {
        super.resume();
        resetScreen();
        holdemWorld.rebuildSeats();
        hideResult();
        if (!isLowBalance()) {
            holdemWorld.isRecovering = true;
            holdemWorld.setGameState(PokerConstants.GAME_RECOVERING);
            showDialog("Reconnecting...");
            if (warpController.client.isConnected()) {
                warpController.sendRecoverRequest();
            } else {
                resumeConnection();
            }
            screenPaused = false;
        }
    }

    @Override
    public void hide() {
        super.hide();
        holdemWorld.stopTickSound();
    }

    @Override
    public void dispose() {
        super.dispose();
    }

    @Override
    public void onBackKeyPressed() {
        if (!backButton.isVisible()) {
            return;
        }
        float betAmount = holdemWorld.holdemUserPlayer.betAmount;
        new PokerQuitDialog(this,atlas, betAmount) {
            @Override
            public void onLeave() {
                hide();
                if (!backButton.isVisible()) {
                    return;
                }
                showDialog("Leaving...");
                holdemWorld.resetTimers(true);
                warpController.disconnect();
                pokerGame.backFomPokerTime = TimeUtils.millis();
            }

            @Override
            public void hide() {
                super.hide();
            }
        }.show(stage);
    }


    @Override
    public void onConnected() {

    }

    @Override
    public void onConnectionFailed() {
        warpController.setListener(null);
        holdemWorld.resetTimers(true);
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                warpController.reset();
                pokerGame.setScreen(new PokerContestScreen(pokerGame));
            }
        });
    }

    public void resumeConnection() {
        warpController.resumeConnection(32);
    }

    @Override
    public void onConnectionRecovered() {
        warpController.client.getLiveUserInfo(warpController.myID);
    }

    @Override
    public void onRecoverConnection() {
        holdemWorld.isJoining = false;
        holdemWorld.isShowingResult = false;
        holdemWorld.timerMessage = null;

        if (!screenPaused) {
            holdemWorld.isRecovering = true;
            holdemWorld.resetTimers(true);
            holdemWorld.setGameState(PokerConstants.GAME_RECOVERING);
            Gdx.app.postRunnable(new Runnable() {
                @Override
                public void run() {
                    hideResult();
                    resetScreen();
                    holdemWorld.rebuildSeats();
                    showDialog("Reconnecting...");
                    resumeConnection();
                }
            });
        }
    }

    @Override
    public void onRecoverConnectionFailed() {
        warpController.setListener(null);
        holdemWorld.resetTimers(true);
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                warpController.reset();
                pokerGame.setScreen(new PokerContestScreen(pokerGame));
            }
        });
    }

    @Override
    public void onDisconnected() {
        if (!isLowBalance()) {
            warpController.setListener(null);
            holdemWorld.resetTimers(true);
            Gdx.app.postRunnable(new Runnable() {
                @Override
                public void run() {
                    warpController.reset();
                    pokerGame.setScreen(new PokerContestScreen(pokerGame));
                }
            });
        }
    }

    @Override
    public void onRoomJoined() {
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                hideDialog();
//                warpController.sendRecoverRequest();
            }
        });
    }

    @Override
    public void onSessionRecovered() {

    }

    @Override
    public void onGetLiveRoomInfo(LiveRoomInfoEvent liveRoomInfoEvent) {

        if (holdemWorld.getGameState() == PokerConstants.GAME_RECOVERING) {
            if (liveRoomInfoEvent.getResult() == WarpResponseResultCode.SUCCESS) {
                if (liveRoomInfoEvent.getJoinedUsers().length > 1) {
                    warpController.sendRecoverRequest();
                } else {
                    warpController.client.disconnect();
                }
            } else {
                warpController.client.disconnect();
            }
        }
    }

    @Override
    public void onGetLiveUserInfo(LiveUserInfoEvent liveUserInfoEvent) {
        if (holdemWorld.getGameState() == PokerConstants.GAME_RECOVERING) {
            if (liveUserInfoEvent.getResult() == WarpResponseResultCode.SUCCESS) {
                if (liveUserInfoEvent.getName().matches(warpController.myID)) {
                    if (liveUserInfoEvent.isLocationLobby()) {
                        warpController.client.disconnect();
                    } else {
                        warpController.client.getLiveRoomInfo(liveUserInfoEvent.getLocationId());
                    }
                } else {
                    warpController.client.disconnect();
                }
            } else {
                warpController.client.disconnect();
            }
        } else {
            warpController.client.disconnect();
        }
    }

    @Override
    public void onUserJoined(final String user) {

    }

    @Override
    public void onUserLeft(final String user) {
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                holdemWorld.removePlayer(user);
            }
        });
    }

    @Override
    public void onGameStarted(String turnId, String startTime) {
        if (screenPaused) return;
        if (holdemWorld.getGameState() != PokerConstants.GAME_RECOVERING) {
            holdemWorld.onGameStarted(turnId);
        }
    }

    @Override
    public void onMoveCompleted(final MoveEvent moveEvent) {
        if (holdemWorld.getGameState() != PokerConstants.GAME_RUNNING || screenPaused) return;
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                holdemWorld.onMoveCompleted(moveEvent);
            }
        });
    }


    @Override
    public void onGameStopped() {

    }

    @Override
    public void handleClientMessage(String messageData, String sender) {
        final PokerMessage pokerMessage = new PokerMessage(new JsonReader().parse(messageData));
        int type = pokerMessage.getType();
        final JsonValue message = pokerMessage.getData();
        if (type == PokerConstants.MESSAGE_UPDATE_BALANCE) {
            Gdx.app.postRunnable(new Runnable() {
                @Override
                public void run() {
                    UpdateBalance updateBalance = new UpdateBalance(message);
                    holdemWorld.updateBalanceOnRefill(updateBalance.getId(), updateBalance.getPokerBalance());
                }
            });
        }
        if (screenPaused || holdemWorld.getGameState() == PokerConstants.GAME_RECOVERING) return;
        if (type == PokerConstants.MESSAGE_EMOJI) {
            final EmojiMessage emojiMessage = new EmojiMessage(message);
            if (!emojiMessage.getFromUserId().matches(warpController.myID))
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        showEmojiAnimation(emojiMessage.getFromUserId(), emojiMessage.getToUserId(), emojiMessage.getEmojiId());
                    }
                });
        }
    }

    @Override
    public void handleServerMessage(final String messageData, String sender) {
        if (screenPaused) {
            parseTimerMessage(messageData);
            return;
        }
        final JsonValue messageValue = new JsonReader().parse(messageData);
        final PokerMessage pokerMessage = new PokerMessage(messageValue);
        int type = pokerMessage.getType();
        final JsonValue message = pokerMessage.getData();
        if (type == PokerConstants.MESSAGE_RECOVER_RESPONSE) {
            Gdx.app.postRunnable(new Runnable() {
                @Override
                public void run() {
                    holdemWorld.handleRecoverResponse(message, messageValue.getFloat("maxBetAmount", 0.0f));
                    hideDialog();
                }
            });
        }
        if (holdemWorld.getGameState() == PokerConstants.GAME_RECOVERING) return;
        switch (type) {
            case PokerConstants.MESSAGE_JOIN_RESPONSE:
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        holdemWorld.onJoinDone(message, messageValue.getFloat("maxBetAmount", 0.0f));
                    }
                });
                break;

            case PokerConstants.MESSAGE_LEAVE:
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        String leftUserId = message.getString("leftUserId");
                        if (leftUserId.matches(warpController.myID)){
                            onRoomLeft();
                        }else {
                            onUserLeft(leftUserId);
                        }
                    }
                });
                break;

            case PokerConstants.MESSAGE_NEW_USER_JOIN:
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        holdemWorld.onUserJoined(message);
                    }
                });
                break;

            case PokerConstants.MESSAGE_CARDS:
                final int[] cards = message.get("cards").asIntArray();
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        holdemWorld.setPlayerCards(cards);
                    }
                });
                break;

            case PokerConstants.MESSAGE_DEALER:
                holdemWorld.setGameState(PokerConstants.GAME_RUNNING);
                final HoldemDealerMessage holdemDealerMessage = new HoldemDealerMessage(message);
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        backButton.setVisible(true);
                        holdemWorld.setDealer(holdemDealerMessage);
                    }
                });
                break;

            case PokerConstants.MESSAGE_RESULT:
                holdemWorld.isShowingResult = true;
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        holdemWorld.handleResult(message);
                    }
                });
                break;

            case PokerConstants.MESSAGE_LOW_BALANCE:
                String status = message.getString("status", "LOW_BALANCE");
                String errorText = message.getString("errorText");

                if (status.matches(PokerConstants.STATUS_FAIL)
                        || status.matches(PokerConstants.STATUS_MAINTENANCE)) {
                    warpController.disconnect();
                } else if (status.matches(PokerConstants.STATUS_LOW_BALANCE)) {
                    if (resultDialog != null) {
                        Gdx.app.postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                resultDialog.hide();
                            }
                        });

                    } else {
                        Gdx.app.postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                holdemWorld.requestAutoFill();
                            }
                        });

                    }
                } else {
                    warpController.disconnect();
                }
                break;


            case PokerConstants.MESSAGE_TIMER:
                if (holdemWorld.isRecovering || holdemWorld.isShowingResult || holdemWorld.isJoining) {
                    holdemWorld.timerMessage = new TimerMessage(message);
                } else {
                    Gdx.app.postRunnable(new Runnable() {
                        @Override
                        public void run() {
                            showTimer(new TimerMessage(message));
                        }
                    });
                }
                parseTimerMessage(message);
                break;
        }
    }

    private void parseTimerMessage(JsonValue jsonValue) {
        setRemainingGameStartTimeOnPause(TimeUnit.SECONDS.toMillis(jsonValue.getInt("secondsLeft")));
    }


    @Override
    public void onSwitchRoom() {

    }

    @Override
    public void resetOnSwitch() {

    }

    @Override
    public void onSwitchIfNoRoomAvailable() {

    }

    @Override
    public void setSwitchRoomListener() {

    }

    @Override
    public void onSwitchRoomJoined() {
        holdemWorld.isRecovering = true;
        showDialog("Reconnecting...");
        if (warpController.client.isConnected()) {
            warpController.sendRecoverRequest();
        } else {
            resumeConnection();
        }
    }

    public void onJoinAfterAdCash() {
        showDialog("Joining Table...");
        warpController.joinGameAfterAddCash();
    }

    @Override
    public void onAgainJoinAfterLowBalance() {
        holdemWorld.isJoining = true;
        JsonValue matchProperties=new JsonValue(JsonValue.ValueType.object);
        if(tableType == PokerConstants.PUBLIC_TABLE) {
            matchProperties.addChild("variant",new JsonValue(contestId));
            matchProperties.addChild(PokerConstants.KEY_ROOM_STATUS,new JsonValue(PokerConstants.FRESH_ROOM_STATUS));
        }else{
            matchProperties.addChild("variant",new JsonValue(contestId));
            matchProperties.addChild("game",new JsonValue(PokerConstants.QR_HOLDEM));
            matchProperties.addChild("qrId",new JsonValue(qrId));
            matchProperties.addChild(PokerConstants.KEY_ROOM_STATUS,new JsonValue(PokerConstants.FRESH_ROOM_STATUS));
        }
        warpController.joinGame(matchProperties);
        showDialog("Joining Table...");
    }

    @Override
    public void onRoomLeft() {
        warpController.client.disconnect();
    }

    private class TotalBetLabel extends Label {
        public float amount;

        public TotalBetLabel(float amount, LabelStyle style) {
            super("", style);
            updateAmount(amount);
        }

        public void updateAmount(float amount) {
            this.amount = PokerUtils.getValue(amount);
            setText("Total Bet : \u20b9" + PokerUtils.getValue(amount));
            setVisible(this.amount > 0.0f);
        }
    }

    public void setRemainingGameStartTimeOnPause(long remainingTimeOnPause) {
        if (screenPaused && holdemWorld.getGameState() == PokerConstants.GAME_REST && remainingTimeOnPause >= 3000) {
            warpController.disconnect();
        }
    }

    private void parseTimerMessage(String messageData) {
        try {
            final PokerMessage pokerMessage = new PokerMessage(new JsonReader().parse(messageData));
            int type = pokerMessage.getType();
            final JsonValue message = pokerMessage.getData();
            if (type == PokerConstants.MESSAGE_TIMER) {
                setRemainingGameStartTimeOnPause(TimeUnit.SECONDS.toMillis(message.getInt("secondsLeft")));
            }
        } catch (Exception e) {

        }
    }

    @Override
    public void onGetMatchedRoomsDone(MatchedRoomEvent matchedRoomsEvent) {

    }
}
