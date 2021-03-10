package bigcash.poker.game.omaha;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
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
import com.badlogic.gdx.utils.Json;
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
import bigcash.poker.appwarp.events.MoveEvent;
import bigcash.poker.constants.Constant;
import bigcash.poker.dialogs.EmojiDialog;
import bigcash.poker.dialogs.ProcessDialog;
import bigcash.poker.font.FontPool;
import bigcash.poker.font.FontType;
import bigcash.poker.game.EmojiMessage;
import bigcash.poker.game.PokerAbstractScreen;
import bigcash.poker.game.PokerMessage;
import bigcash.poker.game.PokerQuitDialog;
import bigcash.poker.game.TimerMessage;
import bigcash.poker.game.UpdateBalance;
import bigcash.poker.game.omaha.controllers.OmahaWarpController;
import bigcash.poker.game.omaha.messages.OmahaDealerMessage;
import bigcash.poker.game.omaha.messages.OmahaResultMessage;
import bigcash.poker.game.omaha.widgets.OmahaCard;
import bigcash.poker.game.omaha.widgets.OmahaOpenCards;
import bigcash.poker.game.omaha.widgets.OmahaPlayer;
import bigcash.poker.game.omaha.widgets.OmahaRaiseTable;
import bigcash.poker.game.omaha.widgets.OmahaUserPlayer;
import bigcash.poker.game.omaha.widgets.buttons.OCallAnyButton;
import bigcash.poker.game.omaha.widgets.buttons.OCallButton;
import bigcash.poker.game.omaha.widgets.buttons.OCheckButton;
import bigcash.poker.game.omaha.widgets.buttons.OCheckedCallAny;
import bigcash.poker.game.omaha.widgets.buttons.OFoldButton;
import bigcash.poker.game.omaha.widgets.buttons.OFoldCheckButton;
import bigcash.poker.game.omaha.widgets.buttons.ORaiseButton;
import bigcash.poker.game.omaha.widgets.buttons.OmahaButton;
import bigcash.poker.gwt.PokerGame;
import bigcash.poker.screens.PokerContestScreen;
import bigcash.poker.utils.GamePreferences;
import bigcash.poker.utils.PokerConstants;
import bigcash.poker.utils.PokerTimer;
import bigcash.poker.utils.PokerUtils;
import bigcash.poker.utils.TextureDrawable;
import bigcash.poker.utils.TimeoutHandler;
import bigcash.poker.widgets.Emoji;
import bigcash.poker.widgets.StyledLabel;

public class OmahaGameScreen extends PokerAbstractScreen {
    public OmahaWorld omahaWorld;
    public OmahaWarpController warpController;
    private ProcessDialog dialog;
    public OmahaRaiseTable omahaRaiseTable;
    private TotalBetLabel totalBetLabel;
    private int contestId;
    private float buttonPad, sidePad, buttonHeight;
    private Button soundButton, infoButton, resultButton;
    private OmahaOpenCards omahaOpenCards;
    private Table timerTable;
    private Label timerLabel;
    private PokerTimer.PokerTimerUpdater timerUpdater;
    private PokerTimer pokerTimer;
    public boolean screenPaused;
    private OmahaResultDialog resultDialog;
    private Table mainTable;
    public Button autoRefillButton;
    public Button backButton;
    private LinkedHashMap<Integer, OmahaButton> buttonHashMap;
    private OmahaButton callAnyButton, callButton, checkButton, foldButton, foldCheckButton, checkedCallAny;
    public OmahaButton raiseButton;
    private int gameState;
    private float betCenterX, betCenterY;
    private boolean lowBalance;
    private Label walletLabel;
    public Texture bgSelectedAmount;
    private EmojiDialog emojiDialog;
    private Emoji.EmojiErrorListener emojiErrorListener;

    public OmahaGameScreen(PokerGame pokerGame, OmahaWarpController warpController, int gameState, int contestId) {
        super(pokerGame, warpController, "OmahaGameScreen");
        this.warpController = warpController;
        this.contestId = contestId;
        this.gameState=gameState;
    }

    public OmahaGameScreen(PokerGame app, OmahaWarpController warpController, int contestId) {
        this(app, warpController, PokerConstants.GAME_REST, contestId);
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
        omahaWorld = new OmahaWorld(this, warpController.getMaxUsers(), gameState);
        this.dialog = new ProcessDialog(this, "");
        NinePatchDrawable emojiDialogBackground = new NinePatchDrawable(new NinePatch(uiAtlas.findRegion("poker_dialog"), 33, 33, 30, 30));
        emojiDialog = new EmojiDialog(this, emojiDialogBackground, uiAtlas.findRegion("poker_emoji"), uiAtlas.findRegion("icon_close"), uiAtlas.findRegion("poker_arrow"));

        mainTable = new Table();
        TextureRegionDrawable background = new TextureRegionDrawable(atlas.findRegion("background"));
        mainTable.setBackground(background);
        omahaWorld.setPosition(0, 0);
        omahaWorld.setSize(width, height);
        mainTable.addActor(omahaWorld);

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

        omahaOpenCards = new OmahaOpenCards(omahaWorld);
        omahaOpenCards.setTouchable(Touchable.disabled);
        omahaOpenCards.setPosition(width / 2f, height * 0.59f);
        omahaOpenCards.setSize(width, height);
        mainTable.addActor(omahaOpenCards);

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
        soundButton.setChecked(GamePreferences.instance().getGameSoundStatus());

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
        autoRefillButton.setChecked(GamePreferences.instance().isAutoRefillButtonChecked());
        autoRefillTable.add(new Label("Auto Refill", autoRefillStyle));
        topTable.add(autoRefillTable).padRight(sidePad);
        autoRefillTable.setTouchable(Touchable.enabled);
        autoRefillTable.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                autoRefillButton.setChecked(!autoRefillButton.isChecked());
                GamePreferences.instance().setAutoRefillButtonChecked(autoRefillButton.isChecked());
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
                                if (omahaWorld.getPlayingPlayersCount() == 1) {
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
                        if (omahaWorld.getPlayingPlayersCount() == 1) {
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
        buttonHashMap = new LinkedHashMap<Integer, OmahaButton>();

        foldButton = new OFoldButton(omahaWorld, 1, buttonLabelStyle, uncheckedDrawable, checkedDrawable);
        buttonHashMap.put(1, foldButton);
        foldCheckButton = new OFoldCheckButton(omahaWorld, 2, buttonLabelStyle, uncheckedDrawable, checkedDrawable);
        buttonHashMap.put(2, foldCheckButton);
        checkButton = new OCheckButton(omahaWorld, 3, buttonLabelStyle, uncheckedDrawable, checkedDrawable);
        buttonHashMap.put(3, checkButton);
        callButton = new OCallButton(omahaWorld, 4, buttonLabelStyle, uncheckedDrawable, checkedDrawable);
        buttonHashMap.put(4, callButton);

        callAnyButton = new OCallAnyButton(omahaWorld, 5, buttonLabelStyle, uncheckedDrawable, checkedDrawable);
        buttonHashMap.put(5, callAnyButton);

        checkedCallAny = new OCheckedCallAny(omahaWorld, 6, buttonLabelStyle, uncheckedDrawable, checkedDrawable);
        buttonHashMap.put(6, checkedCallAny);

        raiseButton = new ORaiseButton(omahaWorld, 7, buttonLabelStyle, uncheckedDrawable, checkedDrawable);
        buttonHashMap.put(7, raiseButton);

        omahaRaiseTable = new OmahaRaiseTable(omahaWorld, warpController.getSmallBlind(), warpController.getBigBlind());
        omahaRaiseTable.setVisible(false);


        emojiErrorListener = new Emoji.EmojiErrorListener() {
            @Override
            public void onDownloadFailed(Emoji emoji) {
                pokerGame.downloadEmoji(emoji.getEmojiName(), emoji);
            }
        };

        if (Constant.emojis != null) {
            for (String emoji : Constant.emojis) {
                addEmoji(emoji);
            }
        }

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
                    showEmojiAnimation(omahaWorld.omahaUserPlayer, emoji, receiver);
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


    public void showEmojiAnimation(OmahaUserPlayer senderPlayer, Emoji emoji, String receiver) {
        OmahaPlayer receiverPlayer = omahaWorld.getPlayer(receiver);
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
            getStage().addActor(emoji2);
        }
    }

    public void showEmojiAnimation(String sender, String receiver, String emojiName) {
        OmahaPlayer senderPlayer = omahaWorld.getPlayer(sender);
        OmahaPlayer receiverPlayer = omahaWorld.getPlayer(receiver);
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
            getStage().addActor(emoji);
            pokerGame.downloadEmoji(emojiName, emoji);
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
        if (GamePreferences.instance().getGameVibration()) {
            Gdx.input.vibrate(millis);
        }
    }

    public void updateBalance() {
        walletLabel.setText(PokerUtils.getValue(Constant.userProfile.getPaytmBalance()) + "");
    }

    public void resetScreen() {
        omahaOpenCards.removeCards();
        setTotalBetAmount(0);
    }

    public void showPreviousResult() {
        if (previousResultMessage != null) {
            new OmahaResultDialog(this, previousResultMessage).show(getStage());
        }
    }

    private OmahaResultMessage previousResultMessage;

    public void showResult(OmahaResultMessage resultMessage) {
        if (resultDialog != null) {
            resultDialog.hide();
        }
        this.previousResultMessage = resultMessage;
        resultButton.setVisible(true);
        resultDialog = new OmahaResultDialog(this, resultMessage) {
            @Override
            public void hide() {
                super.hide();
                omahaWorld.requestAutoFill();
                resultDialog = null;
            }
        };
        resultDialog.show(getStage());
        omahaWorld.callAutoRefillApiInBackGround();
        omahaWorld.setGameState(PokerConstants.GAME_REST);
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
        pokerTimer = new PokerTimer(totalTime, 1000, timerUpdater);
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
        return localToStageCoordinates(totalBetLabel, new Vector2());
    }


    public void showDialog(final String message) {
        dialog.messageLabel.setText(message);
        dialog.show(getStage());
    }

    public void hideDialog() {
        dialog.hide();
    }


    public void hideButtons() {
        omahaRaiseTable.setVisible(false);
        removeButtons();
    }


    public void removeButtons() {
        for (OmahaButton gameButton : buttonHashMap.values()) {
            gameButton.updateButton(OmahaButton.NO_ACTION, 0.0f);
            mainTable.removeActor(gameButton);
        }
        mainTable.removeActor(omahaRaiseTable);
    }

    public void onButtonChecked(int id) {
        for (OmahaButton gameButton : buttonHashMap.values()) {
            if (gameButton.getId() != id && gameButton.getState() == OmahaButton.CHECKED) {
                gameButton.updateButton(OmahaButton.UNCHECKED, gameButton.getAmount());
            }
        }
    }


    public OmahaButton getCheckedButton() {
        for (OmahaButton gameButton : buttonHashMap.values()) {
            if (gameButton.getState() == OmahaButton.CHECKED) {
                return gameButton;
            }
        }
        return null;
    }


    public boolean processCheckedButton(OmahaButton gameButton) {
        boolean userTurn = omahaWorld.isUserTurn();
        boolean betPlaced = omahaWorld.isBetPlaced();
        float maxBet = PokerUtils.getValue(omahaWorld.getMaxBet());
        float balance = PokerUtils.getValue(omahaWorld.omahaUserPlayer.balanceAmount);
        float betAmount = PokerUtils.getValue(omahaWorld.omahaUserPlayer.betAmount);
        if (gameButton.processButton(userTurn, betPlaced, balance, betAmount, maxBet)) {
            gameButton.setState(OmahaButton.NO_ACTION);
            return true;
        }
        return false;
    }


    public void updateButtonsOnRound(OmahaButton checkedButton, boolean showAnimation) {
        int playerState = omahaWorld.omahaUserPlayer.getPlayerState();
        float amount = 0.0f;
        if (checkedButton != null) {
            amount = checkedButton.getAmount();
        }
        if (playerState != PokerConstants.PLAYER_PLAYING) {
            omahaRaiseTable.setVisible(false);
            hideButtons();
            return;
        }

        boolean userTurn = omahaWorld.isUserTurn();
        boolean betPlaced = omahaWorld.isBetPlaced();
        float maxBet = PokerUtils.getValue(omahaWorld.getMaxBet());
        float balance = PokerUtils.getValue(omahaWorld.omahaUserPlayer.balanceAmount);
        float betAmount = PokerUtils.getValue(omahaWorld.omahaUserPlayer.betAmount);

        if (balance <= 0.0f) {
            omahaRaiseTable.setVisible(false);
            hideButtons();
            return;
        }
        removeButtons();
        List<OmahaButton> gameButtons = new ArrayList<OmahaButton>();
        float maxButtonWidth = 0.0f;
        for (OmahaButton gameButton : buttonHashMap.values()) {
            if (gameButton.updateButton(userTurn, betPlaced, balance, betAmount, maxBet)) {
                gameButton.pack();
                float buttonWidth = gameButton.getWidth();
                if (maxButtonWidth < buttonWidth) {
                    maxButtonWidth = buttonWidth;
                }
                if (checkedButton != null) {
                    if (checkedButton.getId() == 2 && gameButton.getId() == 1) {
                        gameButton.updateButton(OmahaButton.CHECKED, gameButton.getAmount());
                    } else {
                        if (gameButton.getId() == checkedButton.getId()) {
                            if (checkedButton.getId() == 4) {
                                if (gameButton.getAmount() == amount) {
                                    gameButton.updateButton(OmahaButton.CHECKED, gameButton.getAmount());
                                }
                            } else {
                                gameButton.updateButton(OmahaButton.CHECKED, gameButton.getAmount());
                            }
                        }
                    }
                }
                gameButtons.add(gameButton);
            }
        }
        omahaRaiseTable.setVisible(false);
        float totalWidth = maxButtonWidth * gameButtons.size() + 2 * buttonPad * (gameButtons.size() - 1);
        float startX = (width - totalWidth) / 2f;
        float buttonY = buttonPad / 2f;
        float bottomY = -buttonHeight - buttonPad;
        for (OmahaButton gameButton : gameButtons) {
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

            Gdx.app.error(gameButton.getName(), "State:" + gameButton.getState());
        }

        if (raiseButton.getState() != OmahaButton.NO_ACTION) {
            omahaRaiseTable.pack();
            omahaRaiseTable.setSize(omahaRaiseTable.getWidth(), omahaRaiseTable.getHeight());
            omahaRaiseTable.setPosition(raiseButton.getX(), buttonY + raiseButton.getHeight());
            mainTable.addActor(omahaRaiseTable);
        }
    }

    public void sendMove(String moveType, float betAmount) {
        if (omahaWorld.isUserTurn()) {
            if (moveType.matches(PokerConstants.MOVE_CHECK)) {
                Gdx.app.error("SendMove PokerGameScreen", moveType);
            }
            omahaWorld.omahaUserPlayer.cancelTimer();
            omahaWorld.stopTickSound();
            JsonValue moveData = new JsonValue(JsonValue.ValueType.object);
            moveData.addChild("moveType", new JsonValue(moveType));
            moveData.addChild("betAmount", new JsonValue(betAmount));
            String moveMessage = moveData.toJson(JsonWriter.OutputType.json);
            warpController.client.sendMove(moveMessage);
            omahaWorld.setUserTurn(false);
            hideButtons();
        }
    }

    public void updateOpenCards(int[] cards) {
        omahaOpenCards.updateCards(cards);
    }

    public void openCards(int[] cards) {
        omahaOpenCards.openCards(cards);
    }

    public HashMap<Integer, OmahaCard> getOmahaOpenCards() {
        return omahaOpenCards.getCards();
    }


    @Override
    public void show() {
        build();
        super.show();
        warpController.setListener(this);
//        Constant.modelScreen = this;
        if (omahaWorld.getGameState() == PokerConstants.GAME_REST) {
            omahaWorld.isJoining = true;
            JsonValue matchProperties = new JsonValue(JsonValue.ValueType.object);
            matchProperties.addChild("variant", new JsonValue(contestId));
            matchProperties.addChild("game", new JsonValue(PokerConstants.OMAHA));
            matchProperties.addChild(PokerConstants.KEY_ROOM_STATUS, new JsonValue(PokerConstants.FRESH_ROOM_STATUS));
            warpController.joinGame(matchProperties);
            showDialog("Joining Table...");
        } else {
            omahaWorld.isRecovering = true;
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
            if (omahaWorld.getPlayingPlayersCount() == 1) {
                setRemainingGameStartTimeOnPause(20000);
            }
            omahaWorld.isJoining = false;
            omahaWorld.isShowingResult = false;
            omahaWorld.timerMessage = null;
            omahaWorld.isRecovering = true;
            omahaWorld.resetTimers(false);
        }
        hideResult();
        omahaWorld.rebuildSeats();
    }


    public void resume() {
        super.resume();
        resetScreen();
        omahaWorld.rebuildSeats();
        hideResult();
        if (!isLowBalance()) {
            omahaWorld.isRecovering = true;
            omahaWorld.setGameState(PokerConstants.GAME_RECOVERING);
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
        omahaWorld.stopTickSound();
    }

    @Override
    public void dispose() {
        super.dispose();
        omahaWorld.tickSound.dispose();
        omahaWorld.betSound.dispose();
        omahaWorld.foldSound.dispose();
        omahaWorld.checkSound.dispose();
    }

    @Override
    public void onBackKeyPressed() {
        if (!backButton.isVisible()) {
            return;
        }
        float betAmount = omahaWorld.omahaUserPlayer.betAmount;
        new PokerQuitDialog(this, atlas, betAmount) {
            @Override
            public void onLeave() {
                hide();
                if (!backButton.isVisible()) {
                    return;
                }
                showDialog("Leaving...");
                omahaWorld.resetTimers(true);
                warpController.disconnect();
                Constant.backFomPokerTime = TimeUtils.millis();
            }

            @Override
            public void hide() {
                super.hide();
            }
        }.show(getStage());
    }


    @Override
    public void onConnected() {

    }

    @Override
    public void onConnectionFailed() {
        warpController.setListener(null);
        omahaWorld.resetTimers(true);
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                pokerGame.setScreen(new PokerContestScreen(pokerGame));
//                PokerUtils.closeFullScreen(new TimeoutHandler() {
//                    @Override
//                    public void onTimeOut() {
//
//                    }
//                });
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
        omahaWorld.isJoining = false;
        omahaWorld.isShowingResult = false;
        omahaWorld.timerMessage = null;

        if (!screenPaused) {
            omahaWorld.isRecovering = true;
            omahaWorld.resetTimers(true);
            omahaWorld.setGameState(PokerConstants.GAME_RECOVERING);
            Gdx.app.postRunnable(new Runnable() {
                @Override
                public void run() {
                    hideResult();
                    resetScreen();
                    omahaWorld.rebuildSeats();
                    showDialog("Reconnecting...");


                    resumeConnection();
                }
            });
        }
    }

    @Override
    public void onRecoverConnectionFailed() {
        warpController.setListener(null);
        omahaWorld.resetTimers(true);
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                pokerGame.setScreen(new PokerContestScreen(pokerGame));
//                PokerUtils.closeFullScreen(new TimeoutHandler() {
//                    @Override
//                    public void onTimeOut() {
//                        pokerGame.setScreen(new PokerContestScreen(pokerGame));
//                    }
//                });
            }
        });
    }

    @Override
    public void onDisconnected() {
        if (!isLowBalance()) {
            warpController.setListener(null);
            omahaWorld.resetTimers(true);
            Gdx.app.postRunnable(new Runnable() {
                @Override
                public void run() {
                    pokerGame.setScreen(new PokerContestScreen(pokerGame));
//                    PokerUtils.closeFullScreen(new TimeoutHandler() {
//                        @Override
//                        public void onTimeOut() {
//                            pokerGame.setScreen(new PokerContestScreen(pokerGame));
//                        }
//                    });
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

        if (omahaWorld.getGameState() == PokerConstants.GAME_RECOVERING) {
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
        if (omahaWorld.getGameState() == PokerConstants.GAME_RECOVERING) {
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
                omahaWorld.removePlayer(user);
            }
        });
    }

    @Override
    public void onGameStarted(String turnId, String startTime) {
        if (screenPaused) return;
        if (omahaWorld.getGameState() != PokerConstants.GAME_RECOVERING) {
            omahaWorld.onGameStarted(turnId);
        }
    }

    @Override
    public void onMoveCompleted(final MoveEvent moveEvent) {
        Gdx.app.error("onMoveCompleted", new Json().prettyPrint(moveEvent));
        if (omahaWorld.getGameState() != PokerConstants.GAME_RUNNING || screenPaused) return;
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                omahaWorld.onMoveCompleted(moveEvent);
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
                    omahaWorld.updateBalanceOnRefill(updateBalance.getId(), updateBalance.getPokerBalance());
                }
            });
        }

        if (screenPaused || omahaWorld.getGameState() == PokerConstants.GAME_RECOVERING) return;
        if (type == PokerConstants.MESSAGE_EMOJI) {
            final EmojiMessage emojiMessage = new EmojiMessage(message);
//            if (!emojiMessage.getFromUserId().matches(warpController.myID))
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
        Gdx.app.error("Server Message", new JsonReader().parse(messageData).toJson(JsonWriter.OutputType.json));
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

                    omahaWorld.handleRecoverResponse(message, messageValue.getFloat("maxBetAmount", 0.0f),
                            messageValue.getFloat("minRaiseBet", 0.0f),
                            messageValue.getFloat("maxRaiseBet", 0.0f));
                    hideDialog();
                }
            });
        }
        if (omahaWorld.getGameState() == PokerConstants.GAME_RECOVERING) return;
        switch (type) {
            case PokerConstants.MESSAGE_JOIN_RESPONSE:
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        omahaWorld.onJoinDone(message, messageValue.getFloat("maxBetAmount", 0.0f));
                    }
                });
                break;

            case PokerConstants.MESSAGE_LEAVE:
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        Gdx.app.error("Server Message", "MESSAGE_LEAVE");
                        String leftUserId = message.getString("leftUserId");
                        Gdx.app.error("Server Message", leftUserId);
                        if (leftUserId.matches(warpController.myID)) {
                            Gdx.app.error("MESSAGE_LEAVE1", leftUserId);
                            onRoomLeft();
                        } else {
                            Gdx.app.error("MESSAGE_LEAVE2", leftUserId);
                            onUserLeft(leftUserId);
                        }
                    }
                });
                break;

            case PokerConstants.MESSAGE_NEW_USER_JOIN:
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        omahaWorld.onUserJoined(message);
                    }
                });
                break;

            case PokerConstants.MESSAGE_CARDS:
                final int[] cards = message.get("cards").asIntArray();
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        omahaWorld.setPlayerCards(cards);
                    }
                });
                break;

            case PokerConstants.MESSAGE_DEALER:
                omahaWorld.setGameState(PokerConstants.GAME_RUNNING);
                final OmahaDealerMessage dealerMessage = new OmahaDealerMessage(message);
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        backButton.setVisible(true);
                        omahaWorld.setMinRaiseBet(dealerMessage.getMinRaiseBet());
                        omahaWorld.setMaxRaiseBet(dealerMessage.getMaxRaiseBet());
                        omahaWorld.setDealer(dealerMessage);
                    }
                });
                break;

            case PokerConstants.MESSAGE_RESULT:
                omahaWorld.isShowingResult = true;
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        omahaWorld.handleResult(message);
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
                                omahaWorld.requestAutoFill();
                            }
                        });

                    }
                } else {
                    warpController.disconnect();
                }
                break;


            case PokerConstants.MESSAGE_TIMER:
                if (omahaWorld.isRecovering || omahaWorld.isShowingResult || omahaWorld.isJoining) {
                    omahaWorld.timerMessage = new TimerMessage(message);
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
        omahaWorld.isRecovering = true;
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
        omahaWorld.isJoining = true;
        JsonValue joiningProperties = new JsonValue(JsonValue.ValueType.object);
        joiningProperties.addChild("variant", new JsonValue(contestId));
        joiningProperties.addChild("game", new JsonValue(PokerConstants.OMAHA));
        joiningProperties.addChild(PokerConstants.KEY_ROOM_STATUS, new JsonValue(PokerConstants.FRESH_ROOM_STATUS));
        warpController.joinGame(joiningProperties);
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
        //  Gdx.app.error(pokerWorld.getGameState()+"",screenPaused+"-"+remainingTimeOnPause);
        if (screenPaused && omahaWorld.getGameState() == PokerConstants.GAME_REST && remainingTimeOnPause >= 3000) {
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
}
