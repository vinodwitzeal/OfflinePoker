package bigcash.poker.game.holdem.widgets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import bigcash.poker.constants.Constant;
import bigcash.poker.font.FontPool;
import bigcash.poker.font.FontType;
import bigcash.poker.game.PokerPlayerData;
import bigcash.poker.game.PokerPlayerTurnImage;
import bigcash.poker.game.holdem.HoldemWorld;
import bigcash.poker.utils.PokerConstants;
import bigcash.poker.utils.PokerTimer;
import bigcash.poker.utils.PokerUtils;
import bigcash.poker.utils.TextureDrawable;
import bigcash.poker.widgets.AmountLabel;

public class HoldemPlayer extends Group {
    public HoldemWorld holdemWorld;
    public PokerPlayerStyle playerStyle;
    public Label nameLabel, timerLabel, balanceLabel, stateLabel;
    public AmountLabel betLabel;
    public PokerPlayerTurnImage playerTurnImage;
    public Image dealerImage;
    public Table detailsTable;
    public PokerPlayerCards playerCards;
    public PokerPlayerData playerData;
    public String playerId;
    public float betAmount, balanceAmount,betPadding,detailsPadding;
    public int playerState;

    public int prevTime;
    public boolean playAlarm;
    public long totalTime;
    public float cardXOffset;
    public HashMap<Integer, HoldemCard> cardHashMap;
    public float namePadding;
    public Vector2 betPosition;
    public Button giftButton;
    public int giftAlign;
    public boolean addGiftButton;
    public String lastMoveStatus;
    private PokerTimer pokerTimer;
    private PokerTimer.PokerTimerUpdater timerUpdater;

    public HoldemPlayer(HoldemWorld holdemWorld, PokerPlayerStyle playerStyle) {
        this(holdemWorld,playerStyle,true);
    }

    public HoldemPlayer(final HoldemWorld holdemWorld, final PokerPlayerStyle playerStyle, boolean emptySeat) {
        setTransform(false);
        this.addGiftButton= Constant.emojis!=null;
        this.cardHashMap=new HashMap<Integer, HoldemCard>();
        this.holdemWorld = holdemWorld;
        this.playerStyle = playerStyle;
        this.betPadding=playerStyle.betPadding;
        this.namePadding=3* holdemWorld.screen.density;
        this.detailsPadding=playerStyle.imageSize*0.1f;
        this.betPosition=new Vector2();
        nameLabel = new Label("", playerStyle.nameStyle);
        timerLabel = new Label(PokerConstants.TURN_TIME + "", playerStyle.timerStyle);
        balanceLabel = new Label("0", playerStyle.balanceStyle);
        stateLabel = new Label("", playerStyle.stateStyle);
        betLabel = new AmountLabel(0, playerStyle.betStyle) {
            @Override
            public void updateAmount(float amount) {
                super.updateAmount(amount);
                setVisible(amount > 0);
            }
        };
        playerTurnImage = new PokerPlayerTurnImage(holdemWorld.screen,playerStyle.userRegion, playerStyle.crownRegion, playerStyle.imageSize);
        dealerImage = new Image(playerStyle.dealerRegion);
        dealerImage.setSize(playerStyle.dealerWidth, playerStyle.dealerHeight);
        stateLabel.setSize(playerStyle.stateWidth, playerStyle.stateHeight);
        stateLabel.setAlignment(Align.center);
        playerState = PokerConstants.PLAYER_PLAYING;
        timerLabel.setSize(playerStyle.timerSize, playerStyle.timerSize);
        timerLabel.setAlignment(Align.center);
        playerCards=new PokerPlayerCards();
        detailsTable=new Table(){
            @Override
            public void setColor(Color color) {
                super.setColor(color);
                for (Actor actor:getChildren()){
                    actor.setColor(color);
                }
            }
        };
        detailsTable.setBackground(playerStyle.detailsBackground);

        this.totalTime = TimeUnit.SECONDS.toMillis(PokerConstants.TURN_TIME);
        this.giftButton=new Button(playerStyle.giftDrawable);
        this.giftButton.setSize(playerStyle.giftSize,playerStyle.giftSize);
        this.giftButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                Vector2 position=new Vector2();
                holdemWorld.screen.localToStageCoordinates(giftButton,position);
                position.x=position.x+playerStyle.giftSize/2f;
                position.y=position.y+playerStyle.giftSize/2f;
                holdemWorld.screen.showEmojiDialog(position.x,position.y,giftAlign,getPlayerId());
            }
        });

        this.timerUpdater = new PokerTimer.PokerTimerUpdater() {
            @Override
            public void onTick(long millisUntilFinished) {
                int cTime = getTime(millisUntilFinished);
                if (cTime != prevTime) {
                    prevTime = cTime;
                    if (playAlarm && cTime <= 5) {
                        ringAlarm();
                        playAlarm = false;
                    }
                    Gdx.app.postRunnable(new Runnable() {
                        @Override
                        public void run() {
                            timerLabel.setText(prevTime + "");
                        }
                    });
                }
                playerTurnImage.updateTime(millisUntilFinished, totalTime);
            }

            @Override
            public void onFinish() {
                onTimerFinished();
            }
        };

        if (emptySeat){
            emptySeat();
        }
    }

    public void showBetAnimation(float amount){
        amount= PokerUtils.getValue(amount);
        if (amount>0){
            holdemWorld.screen.playSound(holdemWorld.betSound,1.0f);
            AmountLabel amountLabel=new AmountLabel(amount,playerStyle.betStyle);
            amountLabel.updateAmount(amount);
            setBetAmount(betAmount+amount);
            amountLabel.pack();
            amountLabel.setSize(amountLabel.getWidth(),playerStyle.betHeight);
            Vector2 startPosition=getBalancePosition();
            Vector2 endPosition=getBetPosition();
            amountLabel.setPosition(startPosition.x,startPosition.y);
            holdemWorld.addActor(amountLabel);
            amountLabel.addAction(Actions.sequence(
                    Actions.moveTo(endPosition.x,endPosition.y,0.2f),
                    Actions.run(new Runnable() {
                        @Override
                        public void run() {
                            updateBetAmount();
                        }
                    }),
                    Actions.removeActor()
            ));
        }
    }


    public void updateBetLabelPosition(){
        betLabel.pack();
        betLabel.setSize(betLabel.getWidth(),playerStyle.betHeight);
    }


    public void addBetToPool(){
        if (betAmount>0){
            final float amount=betAmount;
            AmountLabel amountLabel=new AmountLabel(amount,playerStyle.betStyle);
            amountLabel.updateAmount(amount);
            setBetAmount(0);
            amountLabel.pack();
            amountLabel.setSize(amountLabel.getWidth(),playerStyle.betHeight);
            Vector2 startPosition=getBetPosition();
            Vector2 endPosition= holdemWorld.getMainPoolPosition();
            amountLabel.setPosition(startPosition.x,startPosition.y);
            holdemWorld.addActor(amountLabel);
            amountLabel.addAction(Actions.sequence(
                    Actions.moveTo(endPosition.x,endPosition.y,0.2f),
                    Actions.run(new Runnable() {
                        @Override
                        public void run() {
                            Gdx.app.postRunnable(new Runnable() {
                                @Override
                                public void run() {
                                    holdemWorld.addToMainPool(amount);
                                    updateBetAmount();
                                }
                            });
                        }
                    }),
                    Actions.removeActor()
            ));
        }
    }



    public Vector2 getBalancePosition() {
        return holdemWorld.screen.localToStageCoordinates(balanceLabel,new Vector2());
    }

    public Vector2 getBetPosition(){
        return holdemWorld.screen.localToStageCoordinates(betLabel,new Vector2());
    }

    public int getTime(long millis) {
        int time = (int) (millis / 1000) + 1;
        if (time > PokerConstants.TURN_TIME) {
            time = PokerConstants.TURN_TIME;
        }
        return time;
    }

    public void ringAlarm() {

    }

    public void resetAll(){
        lastMoveStatus="";
        resetTimer();
        removeCards();
        playerTurnImage.resetImage();
        removeMoveState(false);
        hideWinner();
        setBetAmount(0);
        updateBetAmount();
        removeDealer();
        setPlayerState(PokerConstants.PLAYER_PLAYING);
    }


    public void startTimer(Color timerColor) {
        playAlarm = true;
        timerLabel.setText(PokerConstants.TURN_TIME + "");
        prevTime = PokerConstants.TURN_TIME;
        timerLabel.setVisible(true);
        playerTurnImage.setTimerColor(timerColor);
        pokerTimer=new PokerTimer(totalTime,50,timerUpdater);
        pokerTimer.start();
    }

    public boolean resumeTimer(long remainingTime, boolean lifeUsed) {
        if (remainingTime > 0) {
            playAlarm = true;
            prevTime = getTime(remainingTime);
            timerLabel.setText(prevTime + "");
            playerTurnImage.setTimerColor(lifeUsed ? Color.ORANGE : Color.GREEN);
            timerLabel.setVisible(true);
            pokerTimer=new PokerTimer(remainingTime,50,timerUpdater);
            pokerTimer.start();
            return true;
        }
        return false;
    }

    public void onTimerFinished() {
        pokerTimer = null;
        playerTurnImage.resetImage();
        timerLabel.setVisible(false);
    }

    public void cancelTimer() {
        if (pokerTimer != null) {
            pokerTimer.cancel();
        }
        pokerTimer = null;
    }

    public void resetTimer() {
        timerLabel.setVisible(false);
        cancelTimer();
        playerTurnImage.resetImage();
    }


    private void build() {
        emptySeat();
        playerTurnImage.setSize(playerStyle.imageSize,playerStyle.imageSize);
        playerTurnImage.setPosition(-playerStyle.imageSize/2f,-playerStyle.imageSize/2f);
        detailsTable.clear();
        detailsTable.pad(0);
        detailsTable.setSize(playerStyle.detailsWidth,playerStyle.detailsHeight);
        detailsTable.add(nameLabel).padBottom(namePadding).row();
        detailsTable.add(balanceLabel);
        dealerImage.setPosition(playerTurnImage.getX()-dealerImage.getWidth(),playerTurnImage.getY()+playerTurnImage.getHeight()-dealerImage.getHeight());
        stateLabel.setSize(playerStyle.stateWidth,playerStyle.stateHeight);
        stateLabel.setAlignment(Align.center);
        stateLabel.setPosition(-stateLabel.getWidth()/2f,playerTurnImage.getY());
        timerLabel.setSize(playerStyle.timerSize,playerStyle.timerSize);
        timerLabel.setAlignment(Align.center);
        timerLabel.setPosition(-playerStyle.timerSize/2f,-playerStyle.timerSize/2f);
        timerLabel.setVisible(false);
        betLabel.setAlignment(Align.center);
        setPositions();
        addActors();
    }

    public void setPositions(){

    }


    @Override
    public void setColor(Color color) {
        super.setColor(color);
        for (Actor actor:getChildren()){
            actor.setColor(color);
        }

    }

    public void addActors(){
        playerCards.setPosition(detailsTable.getX()+detailsTable.getWidth()/2f,detailsTable.getY()+detailsTable.getHeight()/2f);
        addActor(playerCards);
        addActor(detailsTable);
        addActor(betLabel);
        addActor(playerTurnImage);
        addActor(timerLabel);
        addActor(stateLabel);
        addActor(dealerImage);

        if (addGiftButton) {
            float giftX, giftY;
            giftY = -playerStyle.giftSize / 2f;
            if (giftAlign == Align.left) {
                giftX = playerTurnImage.getX() - playerStyle.giftSize / 2f;
            } else {
                giftX = playerTurnImage.getX() + playerTurnImage.getWidth() - playerStyle.giftSize / 2f;
            }
            giftButton.setPosition(giftX, giftY);
            addActor(giftButton);
        }
    }

    public boolean isEmpty() {
        return playerId.isEmpty();
    }

    public void emptySeat() {
        setColor(Color.WHITE);
        cardHashMap.clear();
        clearChildren();
        playerCards.clearChildren();
        detailsTable.clear();
        stateLabel.setVisible(false);
        dealerImage.setVisible(false);
        timerLabel.setVisible(false);
        setBetAmount(0);
        setBalanceAmount(0);
        updateBetAmount();
        updateBalanceAmount();
        playerTurnImage.resetImage();
        hideWinner();
        playerTurnImage.setDrawable(new TextureRegionDrawable(playerStyle.userRegion));
        playerState = PokerConstants.PLAYER_PLAYING;
        playerId = "";
        lastMoveStatus="";
        playerData = null;
    }

    public void setPlayerData(PokerPlayerData playerData) {
        build();
        this.playerData = playerData;
        this.playerId = playerData.id;
        nameLabel.setText(playerData.name);
        if(!playerData.imageUrl.isEmpty()){
            holdemWorld.screen.pokerGame.downloadImage(playerData.imageUrl,playerTurnImage);
        }
        setBalanceAmount(playerData.pokerBalance);
        setBetAmount(playerData.betAmount);
        updateBalanceAmount();
        updateBetAmount();
        dealerImage.setVisible(playerData.isDealer);
        setPlayerState(playerData.state);
    }

    public String getPlayerId() {
        return playerId;
    }

    public PokerPlayerData getPlayerData() {
        return playerData;
    }

    public void setDealer() {
        dealerImage.setVisible(true);
    }

    public void removeDealer() {
        dealerImage.setVisible(false);
    }

    public void showWinner() {
        playerTurnImage.showWinner();
    }

    public void hideWinner() {
        playerTurnImage.hideWinner();
    }

    public void updateCards(int[] cards) {
        playerCards.updateCards(cards);
    }


    public float dealCards(int[] cards,float delay){
        playerCards.dealCards(cards,delay);
        return 0;
    }

    public void setResultCards(int[] cards,float scale){
        playerCards.setResultCards(cards,scale);
    }


    public void setBetAmount(float betAmount) {
        this.betAmount = PokerUtils.getValue(betAmount);
    }

    public void updateBetAmount() {
        this.betLabel.updateAmount(betAmount);
        updateBetLabelPosition();
    }

    public void setBalanceAmount(float balanceAmount) {
        this.balanceAmount = PokerUtils.getValue(balanceAmount);
    }

    public void updateBalanceAmount() {
        this.balanceLabel.setText("\u20b9 " + PokerUtils.getValue(balanceAmount));
    }

    public void minusBalanceAmount(float amount) {
        this.balanceAmount = PokerUtils.getValue(this.balanceAmount) - PokerUtils.getValue(amount);
    }

    public void addBalanceAmount(float amount){
        this.balanceAmount=PokerUtils.getValue(this.balanceAmount)+PokerUtils.getValue(amount);
    }


    public void setPlayerState(int playerState) {
        this.playerState = playerState;
        switch (playerState) {
            case PokerConstants.PLAYER_FOLDED:
                stateLabel.setText("FOLD");
                stateLabel.setColor(Color.CYAN);
                stateLabel.setVisible(true);
                break;
            case PokerConstants.PLAYER_ALL_IN:
                stateLabel.setText("ALL IN");
                stateLabel.setColor(Color.CYAN);
                stateLabel.setVisible(true);
                break;
            case PokerConstants.PLAYER_WATCHING:
                stateLabel.setText("WATCHING");
                stateLabel.setColor(Color.WHITE);
                stateLabel.setVisible(true);
                break;
            default:
                if(lastMoveStatus != null && !lastMoveStatus.isEmpty()  && !lastMoveStatus.matches("PLAYING")){
                    stateLabel.setText(lastMoveStatus);
                    lastMoveStatus="";
                    stateLabel.setColor(Color.CYAN);
                    stateLabel.setVisible(true);
                }else {
                    stateLabel.setText("");
                    stateLabel.setColor(Color.WHITE);
                    stateLabel.setVisible(false);
                }


                break;
        }
    }

    public int getPlayerState() {
        return playerState;
    }

    public void updateMoveState(String moveType) {
        String moveText = "";
        Color moveColor = null;
        if (moveType.matches(PokerConstants.MOVE_FOLD)) {
            playerState = PokerConstants.PLAYER_FOLDED;
            moveText = "FOLD";
            moveColor = Color.CYAN;
            holdemWorld.screen.playSound(holdemWorld.foldSound,1.0f);
        } else if (moveType.matches(PokerConstants.MOVE_ALL_IN)) {
            playerState = PokerConstants.PLAYER_ALL_IN;
            moveText = "ALL IN";
            moveColor = Color.CYAN;
        } else if (moveType.matches(PokerConstants.MOVE_CHECK)) {
            playerState = PokerConstants.PLAYER_PLAYING;
            moveText = "CHECK";
            moveColor = Color.CYAN;
        } else if ((moveType.matches(PokerConstants.MOVE_CALL))) {
            playerState = PokerConstants.PLAYER_PLAYING;
            moveText = "CALL";
            moveColor = Color.CYAN;
        } else if (moveType.matches(PokerConstants.MOVE_RAISE)) {
            playerState = PokerConstants.PLAYER_PLAYING;
            moveText = "RAISE";
            moveColor = Color.CYAN;
        }
        if (!moveText.isEmpty()) {
            stateLabel.setText(moveText);
            stateLabel.setColor(moveColor);
            stateLabel.setVisible(true);
        }
    }

    public void removeMoveState(boolean onResult) {
        lastMoveStatus="";
        if (onResult){
            if (playerState == PokerConstants.PLAYER_PLAYING || playerState==PokerConstants.PLAYER_ALL_IN || playerState==PokerConstants.PLAYER_FOLDED) {
                stateLabel.setText("");
                stateLabel.setVisible(false);
            }
        }else {
            if (playerState == PokerConstants.PLAYER_PLAYING) {
                stateLabel.setText("");
                stateLabel.setVisible(false);
            }
        }
    }

    public void removeCards() {
        playerCards.removeCards();
    }

    public HashMap<Integer, HoldemCard> getHandCards(){
        return cardHashMap;
    }


    private class PokerPlayerCards extends WidgetGroup {
        public float cardYOffset;
        public PokerPlayerCards() {
            setTransform(false);
            cardYOffset=playerStyle.cardHeight*0.2f;
        }

        @Override
        public void setColor(Color color) {
            super.setColor(color);
            for (Actor actor:getChildren()){
                actor.setColor(color);
            }
        }

        public void updateCards(int[] cards) {
            removeCards();
            if (cards == null || cards.length < 2) return;
            HoldemCard card1 = new HoldemCard(holdemWorld, cards[0], cards[0] > 0 ? 1 : -1);
            card1.setSize(playerStyle.cardWidth, playerStyle.cardHeight);
            HoldemCard card2 = new HoldemCard(holdemWorld, cards[1], cards[1] > 0 ? 1 : -1);
            card2.setSize(playerStyle.cardWidth, playerStyle.cardHeight);

            if (card1.id>0){
                cardHashMap.put(card1.id,card1);
            }

            if (card2.id>0){
                cardHashMap.put(card2.id,card2);
            }


            card1.setOrigin(playerStyle.cardWidth/2f,0);
            card1.setRotation(8);

            card2.setOrigin(playerStyle.cardWidth/2f,0);
            card2.setRotation(-8);
            if (cardXOffset>0){
                card1.setPosition(cardXOffset/2f-playerStyle.cardWidth, cardYOffset);
                card2.setPosition(cardXOffset/2f-playerStyle.cardWidth/2f,cardYOffset);
            }else {
                card1.setPosition(cardXOffset/2f-playerStyle.cardWidth/2f, cardYOffset);
                card2.setPosition(cardXOffset/2f,cardYOffset);
            }

            addActor(card1);
            addActor(card2);
        }


        public void dealCards(int[] cards,float delay){
            removeCards();
            if (cards == null || cards.length < 2) return;
            HoldemCard card1 = new HoldemCard(holdemWorld, cards[0], cards[0] > 0 ? 1 : -1);
            card1.setSize(playerStyle.cardWidth, playerStyle.cardHeight);
            HoldemCard card2 = new HoldemCard(holdemWorld, cards[1], cards[1] > 0 ? 1 : -1);
            card2.setSize(playerStyle.cardWidth, playerStyle.cardHeight);
            card1.setOrigin(playerStyle.cardWidth/2f,0);
            card2.setOrigin(playerStyle.cardWidth/2f,0);
            float target1X,target1Y,target2X,target2Y;

            if (card1.id>0){
                cardHashMap.put(card1.id,card1);
            }

            if (card2.id>0){
                cardHashMap.put(card2.id,card2);
            }

            if (cardXOffset>0){
                target1X=cardXOffset/2f-playerStyle.cardWidth;
                target1Y=cardYOffset;

                target2X=cardXOffset/2f-playerStyle.cardWidth/2f;
                target2Y=cardYOffset;
            }else {

                target1X=cardXOffset/2f-playerStyle.cardWidth/2f;
                target1Y=cardYOffset;
                target2X=cardXOffset/2f;
                target2Y=cardYOffset;
            }

            Vector2 startPosition=new Vector2(holdemWorld.dealerX, holdemWorld.dealerY);
            startPosition=stageToLocalCoordinates(startPosition);
            startPosition.x=startPosition.x-playerStyle.cardWidth/2f;

            card1.setScaleX(-1);
            card2.setScaleY(1);

            card1.setPosition(startPosition.x,startPosition.y);
            card2.setPosition(startPosition.x,startPosition.y);
            addActor(card1);
            addActor(card2);
            if (card1.id>0){
               card1.addAction(Actions.sequence(
                       Actions.delay(delay),
                       Actions.moveTo(target1X,target1Y,0.3f),
                       Actions.scaleTo(1,1,0.1f),
                       Actions.rotateTo(8)
               ));
            }else {
                card1.addAction(Actions.sequence(
                        Actions.delay(delay),
                        Actions.moveTo(target1X,target1Y,0.4f),
                        Actions.rotateTo(8)
                ));
            }

            if (card2.id>0){
                card2.addAction(Actions.sequence(
                        Actions.delay(delay+0.25f),
                        Actions.moveTo(target2X,target2Y,0.3f),
                        Actions.scaleTo(1,1,0.1f),
                        Actions.rotateTo(-8,0.1f)
                ));
            }else {
                card2.addAction(Actions.sequence(
                        Actions.delay(delay+0.25f),
                        Actions.moveTo(target2X,target2Y,0.4f),
                        Actions.rotateTo(-8,0.1f)
                ));
            }
        }

        public void setResultCards(int[] cards,float scaleX){
            if (cards == null || cards.length < 2) return;
            removeCards();
            HoldemCard card1 = new HoldemCard(holdemWorld, cards[0], cards[0] > 0 ? 1 : -1);
            card1.setSize(playerStyle.cardWidth, playerStyle.cardHeight);
            HoldemCard card2 = new HoldemCard(holdemWorld, cards[1], cards[1] > 0 ? 1 : -1);
            card2.setSize(playerStyle.cardWidth, playerStyle.cardHeight);
            card1.setOrigin(playerStyle.cardWidth/2f,0);
            card2.setOrigin(playerStyle.cardWidth/2f,0);
            float target1X,target1Y,target2X,target2Y;

            if (card1.id>0){
                cardHashMap.put(card1.id,card1);
            }

            if (card2.id>0){
                cardHashMap.put(card2.id,card2);
            }

            if (cardXOffset>0){
                target1X=cardXOffset/2f-playerStyle.cardWidth;
                target1Y=cardYOffset;

                target2X=cardXOffset/2f-playerStyle.cardWidth/2f;
                target2Y=cardYOffset;
            }else {

                target1X=cardXOffset/2f-playerStyle.cardWidth/2f;
                target1Y=cardYOffset;
                target2X=cardXOffset/2f;
                target2Y=cardYOffset;
            }
            card1.setScale(scaleX,1);

            card1.setPosition(target1X,target1Y);
            card2.setPosition(target2X,target2Y);
            card1.setRotation(8);
            card2.setRotation(-8);
            addActor(card1);
            addActor(card2);
            if (card1.id>0){
                card1.addAction( Actions.scaleTo(1,1,0.2f));
            }

            if (card2.id>0){
                card2.addAction( Actions.scaleTo(1,1,0.2f));
            }
        }

        public void removeCards() {
            clearChildren();
            cardHashMap.clear();
        }
    }


    public static class PokerPlayerStyle {
        public TextureRegion userRegion, frameRegion, radialRegion, crownRegion, dealerRegion;
        public TextureRegionDrawable detailsBackground,giftDrawable;
        public Label.LabelStyle nameStyle, timerStyle, balanceStyle, stateStyle;
        public AmountLabel.PokerLabelStyle betStyle;

        public float imageSize,giftSize;
        public float dealerWidth, dealerHeight, cardWidth, cardHeight;
        public float detailsWidth, detailsHeight, betPadding, stateWidth, stateHeight, timerSize;
        public float betWidth,betHeight;

        public PokerPlayerStyle(HoldemWorld holdemWorld) {
            float screenWidth = holdemWorld.screen.width;
            float screenHeight = holdemWorld.screen.height;

            TextureAtlas atlas = holdemWorld.atlas;

            userRegion = atlas.findRegion("img_opponent");
            frameRegion = atlas.findRegion("img_opponent");
            radialRegion = atlas.findRegion("circle");
            crownRegion = atlas.findRegion("img_crown");
            dealerRegion = atlas.findRegion("img_dealer");

            imageSize = screenHeight * 0.16f;

            giftSize=imageSize*0.4f;
            giftDrawable= new TextureRegionDrawable(holdemWorld.screen.uiAtlas.findRegion("icon_gift"));
            dealerWidth = imageSize * 0.4f;
            dealerHeight = dealerWidth * dealerRegion.getRegionHeight() / dealerRegion.getRegionHeight();

            TextureRegion detailsTexture = atlas.findRegion("bg_details");

            detailsHeight = imageSize * 0.7f;
            detailsWidth = detailsHeight * detailsTexture.getRegionWidth() / detailsTexture.getRegionHeight();
            detailsBackground = TextureDrawable.getDrawable(detailsTexture, dealerWidth, dealerHeight);

            nameStyle = new Label.LabelStyle();
            nameStyle.font = FontPool.obtain(FontType.ROBOTO_BOLD, 4.5f);
            nameStyle.fontColor = Color.WHITE;

            timerStyle = new Label.LabelStyle();
            timerStyle.font = FontPool.obtain(FontType.ROBOTO_BOLD, 4.5f);
            timerStyle.fontColor = Color.WHITE;

            timerSize = timerStyle.font.getLineHeight() * 2.5f;
            timerStyle.background = TextureDrawable.getDrawable(atlas.findRegion("bg_timer"), timerSize, timerSize);


            stateStyle = new Label.LabelStyle();
            stateStyle.font = FontPool.obtain(FontType.ROBOTO_BOLD, 4);
            stateStyle.fontColor = Color.WHITE;

            TextureRegion stateTexture = atlas.findRegion("bg_state");
            stateWidth = imageSize;
            stateHeight = stateWidth * stateTexture.getRegionHeight() / stateTexture.getRegionWidth();
            stateStyle.background = TextureDrawable.getDrawable(stateTexture, stateWidth, stateHeight);

            TextureRegion cardRegion = holdemWorld.backCardRegion;
            cardHeight = imageSize * 0.8f;
            cardWidth = cardHeight * cardRegion.getRegionWidth() / cardRegion.getRegionHeight();

            betStyle = new AmountLabel.PokerLabelStyle(atlas.findRegion("bg_amount"), 23, 23);
            betStyle.font = FontPool.obtain(FontType.ROBOTO_BOLD, 5.5f);
            betStyle.fontColor = Color.WHITE;
            betPadding=betStyle.font.getLineHeight();

            betWidth=betStyle.font.getCapHeight()*5;
            betHeight=betStyle.font.getLineHeight()*1.5f;
        }

        public PokerPlayerStyle(TextureRegion userRegion, PokerPlayerStyle pokerPlayerStyle) {
            this.userRegion = userRegion;
            this.frameRegion = userRegion;
            this.giftSize=pokerPlayerStyle.giftSize;
            this.giftDrawable=pokerPlayerStyle.giftDrawable;
            radialRegion = pokerPlayerStyle.radialRegion;
            crownRegion = pokerPlayerStyle.crownRegion;
            dealerRegion = pokerPlayerStyle.dealerRegion;

            imageSize = pokerPlayerStyle.imageSize;
            dealerWidth = pokerPlayerStyle.dealerWidth;
            dealerHeight = pokerPlayerStyle.dealerHeight;

            detailsHeight = pokerPlayerStyle.detailsHeight;
            detailsWidth = pokerPlayerStyle.detailsWidth;
            detailsBackground = pokerPlayerStyle.detailsBackground;

            nameStyle = pokerPlayerStyle.nameStyle;

            timerStyle = pokerPlayerStyle.timerStyle;
            timerSize = pokerPlayerStyle.timerSize;

            stateStyle = pokerPlayerStyle.stateStyle;

            stateWidth = pokerPlayerStyle.stateWidth;
            stateHeight = pokerPlayerStyle.stateHeight;

            cardHeight = pokerPlayerStyle.cardHeight;
            cardWidth = pokerPlayerStyle.cardWidth;

            betStyle = pokerPlayerStyle.betStyle;
            betPadding=pokerPlayerStyle.betPadding;

            betWidth=pokerPlayerStyle.betWidth;
            betHeight=pokerPlayerStyle.betHeight;
        }
    }

    public String getLastMoveStatus() {
        return lastMoveStatus;
    }

    public void setLastMoveStatus(String lastMoveStatus) {
        this.lastMoveStatus = lastMoveStatus;
    }
}
