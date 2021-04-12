package bigcash.poker.game.poker.dialogs;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

import java.util.ArrayList;

import bigcash.poker.font.Font;
import bigcash.poker.font.FontPool;
import bigcash.poker.font.FontType;
import bigcash.poker.models.UIDialog;
import bigcash.poker.models.UIScreen;
import bigcash.poker.utils.AssetsLoader;
import bigcash.poker.utils.DrawableBuilder;
import bigcash.poker.utils.TextureDrawable;

public class PokerRulesDialog extends UIDialog {
    private TextureAtlas uiAtlas;
    private ArrayList<buildtable> headertable;
    private ButtonGroup<Button> radioButtonButtonGroup;
    public Label.LabelStyle headingStyle, detailsStyle, subheadingStyle, notestyle, alertStyle;
    private float dialogWidth;
    private TextureRegionDrawable pointBackground;
    private NinePatchDrawable separatorBackground;
    private Image separatorImage;

    public PokerRulesDialog(UIScreen screen) {
        super(screen);
        dismissOnBack(true);
        buildDialog();
    }

    @Override
    public void init() {
        uiAtlas = AssetsLoader.instance().uiAtlas;
        radioButtonButtonGroup = new ButtonGroup<Button>();
        radioButtonButtonGroup.setMinCheckCount(0);
        radioButtonButtonGroup.setMaxCheckCount(1);
    }

    @Override
    public void buildDialog() {
        dialogWidth = width * .9f;
        Table contentTable = getContentTable();
        contentTable.top();
        NinePatchDrawable blueBackground = new DrawableBuilder(20, 20).color(Color.valueOf("223b64")).createNinePatch();
        contentTable.setBackground(blueBackground);
        buildHeaderTable(contentTable);


        separatorBackground = new NinePatchDrawable(new NinePatch(uiAtlas.findRegion("img_dot"), 1, 1, 1, 1));

        Label.LabelStyle inviteTextStyle = new Label.LabelStyle();
        inviteTextStyle.font = FontPool.obtain(FontType.ROBOTO_REGULAR, 7);
        inviteTextStyle.fontColor = Color.DARK_GRAY;

        Table scrollTable = new Table();
        scrollTable.top();
        pointBackground = new TextureRegionDrawable(uiAtlas.findRegion("bullet"));

        Font headingFont = FontPool.obtain(FontType.ROBOTO_BOLD, 9f);
        headingFont.getData().markupEnabled = true;
        headingStyle = new Label.LabelStyle(headingFont, null);

        subheadingStyle = new Label.LabelStyle();
        subheadingStyle.font = FontPool.obtain(FontType.ROBOTO_BOLD, 7);
        subheadingStyle.fontColor = Color.WHITE;

        Font font7 = FontPool.obtain(FontType.ROBOTO_BOLD, 7.4f);
        font7.getData().markupEnabled = true;
        notestyle = new Label.LabelStyle(font7, null);

        Font font = FontPool.obtain(FontType.ROBOTO_BOLD, 7);
        font.getData().markupEnabled = true;
        alertStyle = new Label.LabelStyle(font, null);

        detailsStyle = new Label.LabelStyle();
        detailsStyle.font = FontPool.obtain(FontType.ROBOTO_REGULAR, 7.4f);
        detailsStyle.fontColor = Color.WHITE;

        buildtable goalTable = (new buildtable("Goal", 1));
        buildtable gamePlayTable = (new buildtable("Game Play", 2));
        buildtable handStrengthTable = (new buildtable("Hand Strength", 3));
        buildtable turnActiontable = (new buildtable("Turn Action", 4));
        buildtable turnTimerTable = (new buildtable("Turn Timer", 5));
        buildtable PLO = (new buildtable("Pot Limit Omaha-PLO", 6));
        buildtable playResponsiblytable = (new buildtable("Play Responsibly", 7));
        headertable = new ArrayList<buildtable>();
        headertable.add(goalTable);
        headertable.add(gamePlayTable);
        headertable.add(handStrengthTable);
        headertable.add(turnActiontable);
        headertable.add(turnTimerTable);
        headertable.add(PLO);
        headertable.add(playResponsiblytable);

        for (int i = 0; i < headertable.size(); i++) {
            radioButtonButtonGroup.add(headertable.get(i).downButton);
            scrollTable.add(headertable.get(i)).padTop(5 * density).row();
        }
        ScrollPane dataScrollPane = new ScrollPane(scrollTable);
        dataScrollPane.setScrollingDisabled(true, false);
        contentTable.add(dataScrollPane).padTop(4 * density).expand().fill();

    }


    public void setUncheckAll() {
        for (int i = 0; i < headertable.size(); i++) {
            if (!headertable.get(i).downButton.isChecked()) {
                headertable.get(i).down = true;
                headertable.get(i).contestContainer.clear();
                headertable.get(i).contestContainer.setVisible(false);
            }
        }

    }

    public class buildtable extends Table {
        ImageButton downButton;
        public boolean down;
        public final Table contestContainer;

        public buildtable(String title, int number) {
            Table buttonTable = new Table();
            Label.LabelStyle titleStyle = new Label.LabelStyle();
            titleStyle.font = FontPool.obtain(FontType.ROBOTO_BOLD, 7);
            titleStyle.fontColor = Color.valueOf("535353");
            float downSize = titleStyle.font.getLineHeight() * 1.7f;
            TextureRegionDrawable up = new TextureRegionDrawable(uiAtlas.findRegion("icon_down"));
            TextureRegionDrawable checked = new TextureRegionDrawable(uiAtlas.findRegion("icon_up"));
            downButton = new ImageButton(up, up, checked);
            downButton.setOrigin(downSize / 2f, downSize / 2f);
            NinePatchDrawable whiteBackground = new NinePatchDrawable(new NinePatch(AssetsLoader.instance().uiAtlas.findRegion("bg_details_white"),1,1,1,1));
            whiteBackground.setMinSize(2,2);
            NinePatchDrawable detailsBackground = new NinePatchDrawable(new NinePatch(uiAtlas.findRegion("details_bg"), 3, 3, 3, 3));
            NinePatchDrawable grayBackground = new NinePatchDrawable(new NinePatch(uiAtlas.findRegion("downbutton_bg"), 0, 3, 3, 3));
            setBackground(whiteBackground);
            buttonTable.add(new Label(title, titleStyle)).align(Align.left).padLeft(5 * density).expandX();
            Table downtable = new Table();
            grayBackground.setMinWidth(2);
            grayBackground.setMinHeight(2);
            downtable.setBackground(grayBackground);
            downtable.add(downButton).width(downSize).height(downSize).pad(5 * density, 7 * density, 5 * density, 5 * density).align(Align.right);
            buttonTable.add(downtable).align(Align.right).row();

            detailsBackground.setMinHeight(2);
            detailsBackground.setMinWidth(2);
            contestContainer = new Table();
            contestContainer.setTransform(true);
            contestContainer.setVisible(false);

            final Table contestTable = new Table();
            contestTable.setBackground(detailsBackground);
            contestTable.add(adTable(number)).width(dialogWidth);

            Table mainTable = new Table();
            mainTable.setTouchable(Touchable.enabled);
            mainTable.add(buttonTable).width(dialogWidth).row();
            mainTable.add(contestContainer).width(dialogWidth).row();
            add(mainTable);
            down = true;
            mainTable.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (down) {
                        contestContainer.setVisible(true);
                        contestContainer.add(contestTable).width(dialogWidth);
                        downButton.setChecked(true);
                        down = false;
                        setUncheckAll();
                    } else {
                        downButton.setChecked(false);
                        contestContainer.setVisible(false);
                        contestContainer.clear();
                        down = true;
                    }
                }
            });
            if (number == 1) {
                downButton.setChecked(true);
                contestContainer.setVisible(true);
                contestContainer.add(contestTable).width(dialogWidth);
                down = false;
            }
        }
    }

    public Table adTable(int number) {
        Table adtable;
        switch (number) {
            case 1:
                adtable = goalTable();
                break;
            case 2:
                adtable = gamePlayTable();
                break;
            case 3:
                adtable = handStrengthTable();
                break;
            case 4:
                adtable = turnActiontable();
                break;
            case 5:
                adtable = turnTimertable();
                break;
            case 6:
                adtable = potLimitOmahaTable();
                break;
            default:
                adtable = playResponsiblyTable();
                break;

        }
        return adtable;
    }

    float padLeft = width * .07f;
    float pointSize = width * .025f;

    public Table goalTable() {
        Table contentTable = new Table();
        String goalLabel = "The objective of poker is to earn money by capturing the pot which contain " +
                "bets made by various players during the hand. A player wagers a bet in hopes that he has the " +
                "best hand, or to give the impression that the holds a strong hand and thus convince his " +
                "opponent to fold (abandon) their hands.";

        Label msg1 = new Label(goalLabel, detailsStyle);
        msg1.setWrap(true);
        Table first = new Table();
        first.add(new Image(pointBackground)).width(pointSize).height(pointSize).pad(padLeft * .3f, padLeft * .4f, 0, 0).align(Align.topLeft);
        first.add(msg1).width(dialogWidth * .82f).pad(padLeft * .1f, padLeft * .4f, 0, 0);
        contentTable.add(first).padTop(padLeft * .6f).padBottom(padLeft).width(dialogWidth).row();
        return contentTable;
    }

    public Table gamePlayTable() {
        Table contentTable = new Table();
        String msg1 = "An ordinary game of Poker proceeds as follow:";
        Label gamePlaycontent = new Label(msg1, subheadingStyle);
        gamePlaycontent.setWrap(true);
        contentTable.add(gamePlaycontent).padLeft(padLeft * .1f).padTop(padLeft * .4f).width(dialogWidth * .9f).row();

        String blindstring = "A Small Blind and Big Blind are fixed amount for a table which are posted by two" +
                " players to the left of Dealer (D) respectively before any card is dealt on the table." +
                " These are mandatory bets. Two cards (called the hole cards) are then dealt to each player.";
        Label blindMsg = new Label(blindstring, detailsStyle);
        blindMsg.setWrap(true);
        Table blindTable = new Table();
        blindTable.add(new Label("[#ffffff]1.", headingStyle)).align(Align.topLeft).padTop(padLeft * .4f);
        blindTable.add(new Label("[#ffffff]Blinds", headingStyle)).padLeft(padLeft * .3f).padTop(padLeft * .4f).width(dialogWidth * .7f).row();
        contentTable.add(blindTable).align(Align.left).padLeft(padLeft * .4f).padTop(padLeft).row();
        contentTable.add(blindMsg).padTop(10 * density).width(dialogWidth * .9f).row();

        Image blindImage = new Image();
        String setimageUrl = "https://1101993670.rsc.cdn77.org/img/pokerClub/Rules/blind.png";
        screen.pokerGame.downloadImage(setimageUrl, blindImage);
        contentTable.add(blindImage).width(dialogWidth * .7f).height(height * .04f).padBottom(padLeft * .4f).padTop(padLeft).row();
        separatorImage = new Image(separatorBackground);
        separatorImage.setColor(Color.valueOf("ffffff"));
        contentTable.add(separatorImage).width(dialogWidth * .9f).height(3 * density).padBottom(padLeft * .3f).row();

//pre flop
        String preFlopstring = "A round of betting takes place based on the Player’s cards where the bets can be called" +
                " or raised by the Players or the players can drop out (fold) from the game. A round of betting begins, " +
                "starting from the player to the left of the Big Blind (subsequently the action moves by clockwise direction)." +
                " Each player has the following options:";
        Label preFlopdMsg = new Label(preFlopstring, detailsStyle);
        preFlopdMsg.setWrap(true);
        Table preFlopTable = new Table();
        preFlopTable.add(new Label("[#ffffff]2.", headingStyle)).align(Align.topLeft).padTop(padLeft * .4f);
        preFlopTable.add(new Label("[#ffffff]Pre Flop", headingStyle)).padLeft(padLeft * .3f).padTop(padLeft * .4f).width(dialogWidth * .7f).row();
        contentTable.add(preFlopTable).align(Align.left).padLeft(padLeft * .4f).padTop(padLeft).row();
        contentTable.add(preFlopdMsg).padTop(10 * density).width(dialogWidth * .9f).row();

        String foldString = "Discard his 2 hole cards (The user will no longer be a part of this hand after this action and " +
                "loses all rights to contend for the amount accumulated in the pot).";
        String callString = "Contribute an amount equivalent to the amount posted by the Big Blind.";
        String raiseString = "Contribute of amount greater than the big Blind (Restriction apply to the minimum amount user has" +
                " to put extra to raise). When a player chooses to raise all the remaining players in the hands are required " +
                "to at least match the raised amount to continue to be a part of a hand.(Exception- when a user does not have" +
                " enough amount to match the raise amount he can put his entire table balance in the pot and go ALL IN " +
                "(explained in the following points).\n";
        String preFlopString2 = "This round of betting ends when all players who choose not to fold or go all-in, match the " +
                "highest individual contribution to the pot of player. (If all player except one folds then the hand ends and " +
                "all the money in the pot id awarded to the remaining player after any rake deductions that may apply)." +
                " If 2 or more players remain in the hand after the end of this round, players move to FLOP (see below)";

        Label foldLabel = new Label(foldString, detailsStyle);
        Label callLabel = new Label(callString, detailsStyle);
        Label raiseLabel = new Label(raiseString, detailsStyle);
        Label preFlopLabel2 = new Label(preFlopString2, detailsStyle);
        foldLabel.setWrap(true);
        callLabel.setWrap(true);
        raiseLabel.setWrap(true);
        preFlopLabel2.setWrap(true);

        Table foldtable = new Table();
        foldtable.add(new Label("Fold", alertStyle)).align(Align.topLeft).padTop(10 * density).row();
        foldtable.add(foldLabel).padTop(5 * density).width(dialogWidth * .8f).row();
        contentTable.add(foldtable).width(dialogWidth * .9f).row();
        Table callTable = new Table();
        callTable.add(new Label("Call", alertStyle)).align(Align.topLeft).padTop(10 * density).row();
        callTable.add(callLabel).padTop(5 * density).width(dialogWidth * .8f).row();
        contentTable.add(callTable).width(dialogWidth * .9f).row();

        Table raiseTable = new Table();
        raiseTable.add(new Label("Raise", alertStyle)).align(Align.topLeft).padTop(10 * density).row();
        raiseTable.add(raiseLabel).padTop(5 * density).width(dialogWidth * .8f).row();
        contentTable.add(raiseTable).width(dialogWidth * .9f).row();

        contentTable.add(preFlopLabel2).padTop(10 * density).width(dialogWidth * .9f).row();
        Image preFlopImage = new Image();
        String setPreFlopimageUrl = "https://1101993670.rsc.cdn77.org/img/pokerClub/Rules/pre_flop.png";
        screen.pokerGame.downloadImage(setPreFlopimageUrl, preFlopImage);
        contentTable.add(preFlopImage).width(dialogWidth * .8f).height(height * .1f).padBottom(padLeft).padTop(padLeft * .5f).row();
        separatorImage = new Image(separatorBackground);
        separatorImage.setColor(Color.valueOf("ffffff"));
        contentTable.add(separatorImage).width(dialogWidth * .9f).height(3 * density).padBottom(padLeft * .3f).row();

//flop
        String flopstring = "After the completion of previous round of betting, 3 community cards are dealt [see image below] on " +
                "the table [These 3 cards are collectively called the flop]. A new round of betting begins, the player who " +
                "posted the small blind in the previous round plays first. (This players is first to play in all subsequent " +
                "turns in this hand.) All the players have the addition option to CHECK, they choose not to contribute any " +
                "money into the pot.This action in only available to you if all players who played before you in this round " +
                "of betting also chose to CHECK if any player decide to add money in the pot all checks are made invalid and " +
                "all the remaining players in the hand must at least match the amount to be able to continue participate in the " +
                "hand.\n\n This round of betting ends when all players who chose not to fold or go all-in, match the highest " +
                "individual contribution to the pot by a player (If all player except one folds then the hand ends and all " +
                "the money in the pot id awarded to the remaining player after any rake deductions that may apply). \n\n " +
                "If 2 or more players remain in the hand after the end of this round, players move to TURN (see below)";
        Label flopdMsg = new Label(flopstring, detailsStyle);
        flopdMsg.setWrap(true);
        Table flopTable = new Table();
        flopTable.add(new Label("[#ffffff]3.", headingStyle)).align(Align.topLeft).padTop(padLeft * .4f);
        flopTable.add(new Label("[#ffffff]Flop", headingStyle)).padLeft(padLeft * .3f).padTop(padLeft * .4f).width(dialogWidth * .7f).row();
        contentTable.add(flopTable).align(Align.left).padLeft(padLeft * .4f).padTop(padLeft).row();
        contentTable.add(flopdMsg).padTop(10 * density).width(dialogWidth * .9f).row();

        Image flopImage = new Image();
        String setFlopimageUrl = "https://1101993670.rsc.cdn77.org/img/pokerClub/Rules/flop.png";
        screen.pokerGame.downloadImage(setFlopimageUrl, flopImage);
        contentTable.add(flopImage).width(dialogWidth * .8f).height(height * .1f).padBottom(padLeft).padTop(padLeft * .5f).row();
        separatorImage = new Image(separatorBackground);
        separatorImage.setColor(Color.valueOf("ffffff"));
        contentTable.add(separatorImage).width(dialogWidth * .9f).height(3 * density).padBottom(padLeft * .3f).row();

//Turn
        String turnstring = "After the completion of the previous round of betting, 1 community card is dealt " +
                "[see image below] on the table (This card is called the TURN). A new round of betting begins with the " +
                "same rules that applied to the flop round of betting.";
        Label turnMsg = new Label(turnstring, detailsStyle);
        turnMsg.setWrap(true);
        Table turnTable = new Table();
        turnTable.add(new Label("[#ffffff]4.", headingStyle)).align(Align.topLeft).padTop(padLeft * .4f);
        turnTable.add(new Label("[#ffffff]Turn", headingStyle)).padLeft(padLeft * .3f).padTop(padLeft * .4f).width(dialogWidth * .7f).row();
        contentTable.add(turnTable).align(Align.left).padLeft(padLeft * .4f).padTop(padLeft).row();
        contentTable.add(turnMsg).padTop(10 * density).width(dialogWidth * .9f).row();

        Image turnImage = new Image();
        String setTurnimageUrl = "https://1101993670.rsc.cdn77.org/img/pokerClub/Rules/turn1.png";
        screen.pokerGame.downloadImage(setTurnimageUrl, turnImage);
        contentTable.add(turnImage).width(dialogWidth * .8f).height(height * .1f).padBottom(padLeft).padTop(padLeft * .5f).row();
        separatorImage = new Image(separatorBackground);
        separatorImage.setColor(Color.valueOf("ffffff"));
        contentTable.add(separatorImage).width(dialogWidth * .9f).height(3 * density).padBottom(padLeft * .3f).row();


        //River
        String riverstring = "After the completion of the previous round of betting, 1 community card is dealt (This is a " +
                "last card of dealt [see image below] on the table (This card is called the RIVER). A final round of betting" +
                " begins with the same rules that applied to FLOP and TURN." +
                "\n\n If 2 or more players remain in the hand after the end of this round, the players move to SHOW DOWN (see below)";
        Label riverMsg = new Label(riverstring, detailsStyle);
        riverMsg.setWrap(true);
        Table riverTable = new Table();
        riverTable.add(new Label("[#ffffff]5.", headingStyle)).align(Align.topLeft).padTop(padLeft * .4f);
        riverTable.add(new Label("[#ffffff]River", headingStyle)).padLeft(padLeft * .3f).padTop(padLeft * .4f).width(dialogWidth * .7f).row();
        contentTable.add(riverTable).align(Align.left).padLeft(padLeft * .4f).padTop(padLeft).row();
        contentTable.add(riverMsg).padTop(10 * density).width(dialogWidth * .9f).row();

        Image riverImage = new Image();
        String setriverimageUrl = "https://1101993670.rsc.cdn77.org/img/pokerClub/Rules/river.png";
        screen.pokerGame.downloadImage(setriverimageUrl, riverImage);
        contentTable.add(riverImage).width(dialogWidth * .8f).height(height * .1f).padBottom(padLeft).padTop(padLeft * .5f).row();
        separatorImage = new Image(separatorBackground);
        separatorImage.setColor(Color.valueOf("ffffff"));
        contentTable.add(separatorImage).width(dialogWidth * .9f).height(3 * density).padBottom(padLeft * .3f).row();

        //showdown
        String showDownstring = "After the completion of the previous round of betting, 1 community card is dealt (This is a " +
                "last card of dealt [see image below] on the table (This card is called the RIVER). A final round of betting" +
                " begins with the same rules that applied to FLOP and TURN. If 2 or more players remain in the hand after " +
                "the end of this round, players move to SHOW DOWN (see below)\n";
        Label showDownMsg = new Label(showDownstring, detailsStyle);
        showDownMsg.setWrap(true);
        Table showDownTable = new Table();
        showDownTable.add(new Label("[#ffffff]6.", headingStyle)).align(Align.topLeft).padTop(padLeft * .4f);
        showDownTable.add(new Label("[#ffffff]ShowDown", headingStyle)).padLeft(padLeft * .3f).padTop(padLeft * .4f).width(dialogWidth * .7f).row();
        contentTable.add(showDownTable).align(Align.left).padLeft(padLeft * .4f).padTop(padLeft).row();
        contentTable.add(showDownMsg).padTop(10 * density).width(dialogWidth * .9f).row();

        Image showDownImage = new Image();
        String setShowDownimageUrl = "https://1101993670.rsc.cdn77.org/img/pokerClub/Rules/show_down.png";
        screen.pokerGame.downloadImage(setShowDownimageUrl, showDownImage);
        contentTable.add(showDownImage).width(dialogWidth * .8f).height(height * .12f).padBottom(padLeft).padTop(padLeft * .5f).row();
        return contentTable;
    }


    public Table handStrengthTable() {
        Table contentTable = new Table();
        String msg1 = "Hand strength in poker determine by the following order (Best to Worst)";
        Label handStrengthcontent = new Label(msg1, subheadingStyle);
        handStrengthcontent.setWrap(true);
        contentTable.add(handStrengthcontent).padLeft(padLeft * .1f).padTop(padLeft * .4f).width(dialogWidth * .9f).row();

        //royal flush
        String royalFlushstring = "The strongest poker hand is the Royal Flush. It consists of Ten, Jack, Queen, King and Ace," +
                " All of the same suit.\n Ex. Ace";
        Label royalFlushMsg = new Label(royalFlushstring, detailsStyle);
        royalFlushMsg.setWrap(true);
        Table royalFlushTable = new Table();
        royalFlushTable.add(new Label("[#ffffff]1.", headingStyle)).align(Align.topLeft).padTop(padLeft * .4f);
        royalFlushTable.add(new Label("[#ffffff]Royal Flush", headingStyle)).padLeft(padLeft * .3f).padTop(padLeft * .4f).width(dialogWidth * .7f).row();
        contentTable.add(royalFlushTable).align(Align.left).padLeft(padLeft * .4f).padTop(padLeft).row();
        contentTable.add(royalFlushMsg).padTop(10 * density).width(dialogWidth * .9f).row();

        Image royalFlushMsgImage = new Image();
        String setroyalFlushMsgimageUrl = "https://1101993670.rsc.cdn77.org/img/pokerClub/Rules/royal_flush.png";
        screen.pokerGame.downloadImage(setroyalFlushMsgimageUrl, royalFlushMsgImage);
        contentTable.add(royalFlushMsgImage).width(dialogWidth * .8f).height(height * .1f).padBottom(padLeft).padTop(padLeft * .5f).row();
        separatorImage = new Image(separatorBackground);
        separatorImage.setColor(Color.valueOf("ffffff"));
        contentTable.add(separatorImage).width(dialogWidth * .9f).height(3 * density).padBottom(padLeft * .3f).row();

        //Straight flush
        String StraightFlushstring = "The second strongest hand in poker is the straight flush. It is composed of five " +
                "consecutive cards of the same suit. If two player have a straight flush, the one with the highest cards wins.";
        Label StraightFlushMsg = new Label(StraightFlushstring, detailsStyle);
        StraightFlushMsg.setWrap(true);
        Table StraightFlushTable = new Table();
        StraightFlushTable.add(new Label("[#ffffff]2.", headingStyle)).align(Align.topLeft).padTop(padLeft * .2f);
        StraightFlushTable.add(new Label("[#ffffff]Straight Flush", headingStyle)).padLeft(padLeft * .3f).padTop(padLeft * .2f).width(dialogWidth * .7f).row();
        contentTable.add(StraightFlushTable).align(Align.left).padLeft(padLeft * .2f).padTop(padLeft).row();
        contentTable.add(StraightFlushMsg).padTop(10 * density).width(dialogWidth * .9f).row();

        Image StraightFlushMsgImage = new Image();
        String setStraightFlushMsgimageUrl = "https://1101993670.rsc.cdn77.org/img/pokerClub/Rules/straight_flush.png";
        screen.pokerGame.downloadImage(setStraightFlushMsgimageUrl, StraightFlushMsgImage);
        contentTable.add(StraightFlushMsgImage).width(dialogWidth * .8f).height(height * .1f).padBottom(padLeft).padTop(padLeft * .5f).row();
        separatorImage = new Image(separatorBackground);
        separatorImage.setColor(Color.valueOf("ffffff"));
        contentTable.add(separatorImage).width(dialogWidth * .9f).height(3 * density).padBottom(padLeft * .3f).row();

        //4 of kind
        String fourOfKindstring = "A four-of-kind is four cards of same rank, e.g. Four Ace. If two players have " +
                "four-of-a-kind, then the one with the highest four-of-a-kind wins.";
        Label fourOfKindMsg = new Label(fourOfKindstring, detailsStyle);
        fourOfKindMsg.setWrap(true);
        Table fourOfKindTable = new Table();
        fourOfKindTable.add(new Label("[#ffffff]3.", headingStyle)).align(Align.topLeft).padTop(padLeft * .2f);
        fourOfKindTable.add(new Label("[#ffffff]Four of a Kind", headingStyle)).padLeft(padLeft * .3f).padTop(padLeft * .2f).width(dialogWidth * .7f).row();
        contentTable.add(fourOfKindTable).align(Align.left).padLeft(padLeft * .2f).padTop(padLeft).row();
        contentTable.add(fourOfKindMsg).padTop(10 * density).width(dialogWidth * .9f).row();

        Image fourOfKindMsgImage = new Image();
        String setfourOfKindMsgimageUrl = "https://1101993670.rsc.cdn77.org/img/pokerClub/Rules/four_of_a_kind.png";
        screen.pokerGame.downloadImage(setfourOfKindMsgimageUrl, fourOfKindMsgImage);
        contentTable.add(fourOfKindMsgImage).width(dialogWidth * .8f).height(height * .1f).padBottom(padLeft).padTop(padLeft * .5f).row();
        separatorImage = new Image(separatorBackground);
        separatorImage.setColor(Color.valueOf("ffffff"));
        contentTable.add(separatorImage).width(dialogWidth * .9f).height(3 * density).padBottom(padLeft * .3f).row();

        //full house
        String fullHousestring = "A four-of-kind is four cards of same rank, e.g. Four Ace. If two players have " +
                "four-of-a-kind, then the one with the highest four-of-a-kind wins.";
        Label fullHouseMsg = new Label(fullHousestring, detailsStyle);
        fullHouseMsg.setWrap(true);
        Table fullHouseTable = new Table();
        fullHouseTable.add(new Label("[#ffffff]4.", headingStyle)).align(Align.topLeft).padTop(padLeft * .2f);
        fullHouseTable.add(new Label("[#ffffff]Full house", headingStyle)).padLeft(padLeft * .3f).padTop(padLeft * .2f).width(dialogWidth * .7f).row();
        contentTable.add(fullHouseTable).align(Align.left).padLeft(padLeft * .2f).padTop(padLeft).row();
        contentTable.add(fullHouseMsg).padTop(10 * density).width(dialogWidth * .9f).row();

        Image fullHousemsgImage = new Image();
        String setfullHouseMsgimageUrl = "https://1101993670.rsc.cdn77.org/img/pokerClub/Rules/full_house.png";
        screen.pokerGame.downloadImage(setfullHouseMsgimageUrl, fullHousemsgImage);
        contentTable.add(fullHousemsgImage).width(dialogWidth * .8f).height(height * .1f).padBottom(padLeft).padTop(padLeft * .5f).row();
        separatorImage = new Image(separatorBackground);
        separatorImage.setColor(Color.valueOf("ffffff"));
        contentTable.add(separatorImage).width(dialogWidth * .9f).height(3 * density).padBottom(padLeft * .3f).row();

        //flush
        String flushstring = "Five cards of a same suit make a flush. If two player have a flush, then the one with the highest cards wins.";
        Label flushMsg = new Label(flushstring, detailsStyle);
        flushMsg.setWrap(true);
        Table flushTable = new Table();
        flushTable.add(new Label("[#ffffff]5.", headingStyle)).align(Align.topLeft).padTop(padLeft * .2f);
        flushTable.add(new Label("[#ffffff]Flush", headingStyle)).padLeft(padLeft * .3f).padTop(padLeft * .2f).width(dialogWidth * .7f).row();
        contentTable.add(flushTable).align(Align.left).padLeft(padLeft * .2f).padTop(padLeft).row();
        contentTable.add(flushMsg).padTop(10 * density).width(dialogWidth * .9f).row();

        Image flushmsgImage = new Image();
        String setflushMsgimageUrl = "https://1101993670.rsc.cdn77.org/img/pokerClub/Rules/flush.png";
        screen.pokerGame.downloadImage(setflushMsgimageUrl, flushmsgImage);
        contentTable.add(flushmsgImage).width(dialogWidth * .8f).height(height * .1f).padBottom(padLeft).padTop(padLeft * .5f).row();
        separatorImage = new Image(separatorBackground);
        separatorImage.setColor(Color.valueOf("ffffff"));
        contentTable.add(separatorImage).width(dialogWidth * .9f).height(3 * density).padBottom(padLeft * .3f).row();

        //Straight
        String Straightstring = "Five consecutive cards are called a straight. If two players have straight, the one with the highest cards win.";
        Label StraightMsg = new Label(Straightstring, detailsStyle);
        StraightMsg.setWrap(true);
        Table StraightTable = new Table();
        StraightTable.add(new Label("[#ffffff]6.", headingStyle)).align(Align.topLeft).padTop(padLeft * .2f);
        StraightTable.add(new Label("[#ffffff]Straight", headingStyle)).padLeft(padLeft * .3f).padTop(padLeft * .2f).width(dialogWidth * .7f).row();
        contentTable.add(StraightTable).align(Align.left).padLeft(padLeft * .2f).padTop(padLeft).row();
        contentTable.add(StraightMsg).padTop(10 * density).width(dialogWidth * .9f).row();

        Image StraightmsgImage = new Image();
        String setStraightMsgimageUrl = "https://1101993670.rsc.cdn77.org/img/pokerClub/Rules/straight1.png";
        screen.pokerGame.downloadImage(setStraightMsgimageUrl, StraightmsgImage);
        contentTable.add(StraightmsgImage).width(dialogWidth * .8f).height(height * .1f).padBottom(padLeft).padTop(padLeft * .5f).row();
        separatorImage = new Image(separatorBackground);
        separatorImage.setColor(Color.valueOf("ffffff"));
        contentTable.add(separatorImage).width(dialogWidth * .9f).height(3 * density).padBottom(padLeft * .3f).row();

        //threeOfKind

        String threeOfKindstring = "A Three of a kind is a poker combination of three cards of the same rank. If two" +
                " players have the same Three-of-a-kind, then the other cards, or both cards, determine the winners.";
        Label threeOfKindMsg = new Label(threeOfKindstring, detailsStyle);
        threeOfKindMsg.setWrap(true);
        Table threeOfKindTable = new Table();
        threeOfKindTable.add(new Label("[#ffffff]7.", headingStyle)).align(Align.topLeft).padTop(padLeft * .2f);
        threeOfKindTable.add(new Label("[#ffffff]Three Of Kind", headingStyle)).padLeft(padLeft * .3f).padTop(padLeft * .2f).width(dialogWidth * .7f).row();
        contentTable.add(threeOfKindTable).align(Align.left).padLeft(padLeft * .2f).padTop(padLeft).row();
        contentTable.add(threeOfKindMsg).padTop(10 * density).width(dialogWidth * .9f).row();

        Image threeOfKindmsgImage = new Image();
        String setthreeOfKindMsgimageUrl = "https://1101993670.rsc.cdn77.org/img/pokerClub/Rules/three_of_a_kind.png";
        screen.pokerGame.downloadImage(setthreeOfKindMsgimageUrl, threeOfKindmsgImage);
        contentTable.add(threeOfKindmsgImage).width(dialogWidth * .8f).height(height * .1f).padBottom(padLeft).padTop(padLeft * .5f).row();
        separatorImage = new Image(separatorBackground);
        separatorImage.setColor(Color.valueOf("ffffff"));
        contentTable.add(separatorImage).width(dialogWidth * .9f).height(3 * density).padBottom(padLeft * .3f).row();

        //twoPair
        String twoPairstring = "Two-pair hands are, of course, composed of two of two pair. If two player have two pair," +
                " the rank of the highest pair determine the winner. If they have the same higher pair, then the lower " +
                "one count. If that is also the same then the fifth cards counts.";
        Label twoPairMsg = new Label(twoPairstring, detailsStyle);
        twoPairMsg.setWrap(true);
        Table twoPairTable = new Table();
        twoPairTable.add(new Label("[#ffffff]8.", headingStyle)).align(Align.topLeft).padTop(padLeft * .2f);
        twoPairTable.add(new Label("[#ffffff]Two Pair", headingStyle)).padLeft(padLeft * .3f).padTop(padLeft * .2f).width(dialogWidth * .7f).row();
        contentTable.add(twoPairTable).align(Align.left).padLeft(padLeft * .2f).padTop(padLeft).row();
        contentTable.add(twoPairMsg).padTop(10 * density).width(dialogWidth * .9f).row();

        Image twoPairmsgImage = new Image();
        String twoPairMsgimageUrl = "https://1101993670.rsc.cdn77.org/img/pokerClub/Rules/two_pair.png";
        screen.pokerGame.downloadImage(twoPairMsgimageUrl, twoPairmsgImage);
        contentTable.add(twoPairmsgImage).width(dialogWidth * .8f).height(height * .1f).padBottom(padLeft).padTop(padLeft * .5f).row();
        separatorImage = new Image(separatorBackground);
        separatorImage.setColor(Color.valueOf("ffffff"));
        contentTable.add(separatorImage).width(dialogWidth * .9f).height(3 * density).padBottom(padLeft * .3f).row();

        //pair
        String Pairstring = "A Pair is composed of two cards of the same rank. Since winning poker hands are always " +
                "composed of five cards, the other three cards are so called KICKERS. In case two players have the" +
                " same pair, then the one with the highest kickers wins.";
        Label PairMsg = new Label(Pairstring, detailsStyle);
        PairMsg.setWrap(true);
        Table PairTable = new Table();
        PairTable.add(new Label("[#ffffff]9.", headingStyle)).align(Align.topLeft).padTop(padLeft * .2f);
        PairTable.add(new Label("[#ffffff]Pair", headingStyle)).padLeft(padLeft * .3f).padTop(padLeft * .2f).width(dialogWidth * .7f).row();
        contentTable.add(PairTable).align(Align.left).padLeft(padLeft * .2f).padTop(padLeft).row();
        contentTable.add(PairMsg).padTop(10 * density).width(dialogWidth * .9f).row();

        Image PairmsgImage = new Image();
        String PairMsgimageUrl = "https://1101993670.rsc.cdn77.org/img/pokerClub/Rules/pair.png";
        screen.pokerGame.downloadImage(PairMsgimageUrl, PairmsgImage);
        contentTable.add(PairmsgImage).width(dialogWidth * .8f).height(height * .1f).padBottom(padLeft).padTop(padLeft * .5f).row();
        separatorImage = new Image(separatorBackground);
        separatorImage.setColor(Color.valueOf("ffffff"));
        contentTable.add(separatorImage).width(dialogWidth * .9f).height(3 * density).padBottom(padLeft * .3f).row();

        //High Card
        String highCardstring = "Five different cards are called a high card. If two players have high card, the one with the highest cards win.";
        Label highCardMsg = new Label(highCardstring, detailsStyle);
        highCardMsg.setWrap(true);
        Table highCardTable = new Table();
        highCardTable.add(new Label("[#ffffff]10.", headingStyle)).align(Align.topLeft).padTop(padLeft * .2f);
        highCardTable.add(new Label("[#ffffff]High Card", headingStyle)).padLeft(padLeft * .3f).padTop(padLeft * .2f).width(dialogWidth * .7f).row();
        contentTable.add(highCardTable).align(Align.left).padLeft(padLeft * .2f).padTop(padLeft).row();
        contentTable.add(highCardMsg).padTop(10 * density).width(dialogWidth * .9f).row();

        Image highCardmsgImage = new Image();
        String highCardMsgimageUrl = "https://1101993670.rsc.cdn77.org/img/pokerClub/Rules/high_card.png";
        screen.pokerGame.downloadImage(highCardMsgimageUrl, highCardmsgImage);
        contentTable.add(highCardmsgImage).width(dialogWidth * .8f).height(height * .1f).padBottom(padLeft).padTop(padLeft * .5f).row();
        return contentTable;
    }

    public Table turnActiontable() {
        Table contentTable = new Table();
        String msg1 = "On your turn, you can carry out the following actions:";
        Label turnActioncontent = new Label(msg1, subheadingStyle);
        turnActioncontent.setWrap(true);
        contentTable.add(turnActioncontent).padLeft(padLeft * .1f).padTop(padLeft * .4f).width(dialogWidth * .9f).row();

        //check
        String checkstring = "When a bet is matched and you do not want to bet more, you CHECK for the round to proceed." +
                " You can still bet later in the round.";
        Label checkMsg = new Label(checkstring, detailsStyle);
        checkMsg.setWrap(true);
        Table checkTable = new Table();
        checkTable.add(new Label("[#ffffff]Check", headingStyle)).padLeft(padLeft * .1f).padTop(padLeft * .4f).width(dialogWidth * .7f).row();
        contentTable.add(checkTable).align(Align.left).padLeft(padLeft * .2f).padTop(padLeft).row();
        contentTable.add(checkMsg).padTop(10 * density).width(dialogWidth * .9f).row();
        Image checkImage = new Image();
        String checkimageUrl = "https://1101993670.rsc.cdn77.org/img/pokerClub/Rules/check.png";
        screen.pokerGame.downloadImage(checkimageUrl, checkImage);
        contentTable.add(checkImage).width(dialogWidth * .3f).height(height * .06f).padBottom(padLeft).padTop(padLeft * .5f).row();
        separatorImage = new Image(separatorBackground);
        separatorImage.setColor(Color.valueOf("ffffff"));
        contentTable.add(separatorImage).width(dialogWidth * .9f).height(3 * density).padBottom(padLeft * .3f).row();
//fold
        String foldstring = "When you do not want to play a round, you FOLD to forfeit that round. You can join the game on the next round.";
        Label foldMsg = new Label(foldstring, detailsStyle);
        foldMsg.setWrap(true);
        Table foldTable = new Table();
        foldTable.add(new Label("[#ffffff]Fold", headingStyle)).padLeft(padLeft * .1f).padTop(padLeft * .4f).width(dialogWidth * .7f).row();
        contentTable.add(foldTable).align(Align.left).padLeft(padLeft * .2f).padTop(padLeft).row();
        contentTable.add(foldMsg).padTop(10 * density).width(dialogWidth * .9f).row();
        Image foldImage = new Image();
        String foldimageUrl = "https://1101993670.rsc.cdn77.org/img/pokerClub/Rules/fold.png";
        screen.pokerGame.downloadImage(foldimageUrl, foldImage);
        contentTable.add(foldImage).width(dialogWidth * .3f).height(height * .06f).padBottom(padLeft).padTop(padLeft * .5f).row();
        separatorImage = new Image(separatorBackground);
        separatorImage.setColor(Color.valueOf("ffffff"));
        contentTable.add(separatorImage).width(dialogWidth * .9f).height(3 * density).padBottom(padLeft * .3f).row();

        //raise
        String raisestring = "When you want to increase the stakes by betting more, you RAISE the bet to your desire amount.";
        Label raiseMsg = new Label(raisestring, detailsStyle);
        raiseMsg.setWrap(true);
        Table raiseTable = new Table();
        raiseTable.add(new Label("[#ffffff]Raise", headingStyle)).padLeft(padLeft * .1f).padTop(padLeft * .4f).width(dialogWidth * .7f).row();
        contentTable.add(raiseTable).align(Align.left).padLeft(padLeft * .2f).padTop(padLeft).row();
        contentTable.add(raiseMsg).padTop(10 * density).width(dialogWidth * .9f).row();
        Image raiseImage = new Image();
        String raiseimageUrl = "https://1101993670.rsc.cdn77.org/img/pokerClub/Rules/raise.png";
        screen.pokerGame.downloadImage(raiseimageUrl, raiseImage);
        contentTable.add(raiseImage).width(dialogWidth * .3f).height(height * .06f).padBottom(padLeft).padTop(padLeft * .5f).row();
        separatorImage = new Image(separatorBackground);
        separatorImage.setColor(Color.valueOf("ffffff"));
        contentTable.add(separatorImage).width(dialogWidth * .9f).height(3 * density).padBottom(padLeft * .3f).row();

        //call
        String callstring = "When the previous player raises the bet, you CALL their raise to stay in the round.";
        Label callMsg = new Label(callstring, detailsStyle);
        callMsg.setWrap(true);
        Table callTable = new Table();
        callTable.add(new Label("[#ffffff]Call", headingStyle)).padLeft(padLeft * .1f).padTop(padLeft * .4f).width(dialogWidth * .7f).row();
        contentTable.add(callTable).align(Align.left).padLeft(padLeft * .2f).padTop(padLeft).row();
        contentTable.add(callMsg).padTop(10 * density).width(dialogWidth * .9f).row();
        Image callImage = new Image();
        String callimageUrl = "https://1101993670.rsc.cdn77.org/img/pokerClub/Rules/call.png";
        screen.pokerGame.downloadImage(callimageUrl, callImage);
        contentTable.add(callImage).width(dialogWidth * .3f).height(height * .06f).padBottom(padLeft).padTop(padLeft * .5f).row();

        return contentTable;
    }


    public Table turnTimertable() {
        Table contentTable = new Table();

        String msg1 = "There are two type of timers available to the players:";
        Label turncontent = new Label(msg1, subheadingStyle);
        turncontent.setWrap(true);
        contentTable.add(turncontent).padLeft(padLeft * .1f).padTop(padLeft * .4f).width(dialogWidth * .9f).row();

        //primary
        String primarystring = "The player has this timer to play each turn. This timer reset at every turn.";
        Label primaryMsg = new Label(primarystring, detailsStyle);
        primaryMsg.setWrap(true);
        Table primaryTable = new Table();
        primaryTable.add(new Label("[#ffffff]Primary Timer", headingStyle)).padLeft(padLeft * .1f).padTop(padLeft * .4f).width(dialogWidth * .7f).row();
        contentTable.add(primaryTable).align(Align.left).padLeft(padLeft * .1f).padTop(padLeft).row();
        contentTable.add(primaryMsg).padTop(10 * density).width(dialogWidth * .9f).row();


        Image primaryImage = new Image();
        String primaryimageUrl = "https://1101993670.rsc.cdn77.org/img/pokerClub/Rules/primary_secondary_timer2.png";
        screen.pokerGame.downloadImage(primaryimageUrl, primaryImage);
        contentTable.add(primaryImage).width(dialogWidth * .57f).height(height * .35f).padBottom(padLeft).padTop(padLeft * .5f).row();

        String secondarystring = "This timer start when primary timer expires. Secondary timer acts as a global timer. " +
                "It is associate with table. Once expired is does not reset until the player has started new session. " +
                "This timer does not reset at every turn.";
        Label secondaryMsg = new Label(secondarystring, detailsStyle);
        secondaryMsg.setWrap(true);
        Table secondaryTable = new Table();
        secondaryTable.add(new Label("[#ffffff]Secondary Timer", headingStyle)).padLeft(padLeft * .1f).padTop(padLeft * .4f).width(dialogWidth * .7f).row();
        contentTable.add(secondaryTable).align(Align.left).padLeft(padLeft * .1f).padTop(padLeft).row();
        contentTable.add(secondaryMsg).padTop(10 * density).width(dialogWidth * .9f).padBottom(padLeft).row();

        return contentTable;
    }


    public Table potLimitOmahaTable() {
        Table contentTable = new Table();
        String msg1 = "Pot limit Omaha is a popular variant of Poker.(Look for the PL Omaha sign on the lobby page.)";
        String msg2 = "How pot limit Omaha differ from Poker:";
        Label termDetails1 = new Label(msg1, subheadingStyle);
        Label termDetails2 = new Label(msg2, subheadingStyle);
        termDetails1.setWrap(true);
        termDetails2.setWrap(true);

        Table first = new Table();
        first.add(termDetails1).padLeft(padLeft * .1f).padTop(padLeft * .4f).width(dialogWidth * .8f).row();
        contentTable.add(first).width(dialogWidth).row();


        String msg2_1 = "4 hole cards are dealt instead of 2 to each player.";
        String msg2_2 = "A player must always used to card from the 4 cards to him/her" +
                " and 3 cards from the 5 community cards to make best 5 cards.";
        String msg2_3 = "A limit is placed on the maximum amount that can be raised in the user's turn.A " +
                "user may raise any amount less than or equal to the pot value.(calculated after assuming " +
                "that the user called the pot).\n For eg: player A,B,C and D are playing a hand";

        String msg3_1 = "If player A bets Rs.10";
        String msg3_2 = "If player B bets Rs.10";
        String msg3_3 = "If player C bets Rs.20";
        String msg3_4 = "Then player D can Bet Rs. 80 (calculated in the following manner if player D want" +
                " to call he would have to put Rs.20 in the pot. (now the pot value (10+10+20+20) Rs. 60. " +
                "So player D can put an additional Rs. 60(max pot value after call) in the pot.Hence " +
                "the maximum total amount that can be bet by Player D is Rs. 80(20+60).";

        Label termDetails2_1 = new Label(msg2_1, detailsStyle);
        Label termDetails2_2 = new Label(msg2_2, detailsStyle);
        Label termDetails2_3 = new Label(msg2_3, detailsStyle);
        Label termDetails2_4 = new Label(msg3_1, detailsStyle);
        Label termDetails2_5 = new Label(msg3_2, detailsStyle);
        Label termDetails2_6 = new Label(msg3_3, detailsStyle);
        Label termDetails2_7 = new Label(msg3_4, detailsStyle);

        termDetails2_1.setWrap(true);
        termDetails2_2.setWrap(true);
        termDetails2_3.setWrap(true);
        termDetails2_4.setWrap(true);
        termDetails2_5.setWrap(true);
        termDetails2_6.setWrap(true);
        termDetails2_7.setWrap(true);

        Table termSecond = new Table();
        termSecond.add(termDetails2).padLeft(padLeft * .1f).padTop(padLeft * .3f).width(dialogWidth * .8f).row();
        contentTable.add(termSecond).padTop(10 * density).width(dialogWidth).row();

        Table second_one = new Table();
        second_one.add(new Label("1.", subheadingStyle)).align(Align.topLeft).padTop(2 * density);
        second_one.add(termDetails2_1).padLeft(padLeft * .3f).padTop(padLeft * .1f).width(dialogWidth * .7f).row();
        contentTable.add(second_one).padTop(10 * density).width(dialogWidth).row();

        Table second_two = new Table();
        second_two.add(new Label("2.", subheadingStyle)).align(Align.topLeft).padTop(2 * density);
        second_two.add(termDetails2_2).padLeft(padLeft * .3f).padTop(padLeft * .1f).width(dialogWidth * .7f).row();
        contentTable.add(second_two).padTop(10 * density).width(dialogWidth).row();

        Table second_three = new Table();
        second_three.add(new Label("3.", subheadingStyle)).align(Align.topLeft).padTop(2 * density);
        second_three.add(termDetails2_3).padLeft(padLeft * .3f).padTop(padLeft * .1f).width(dialogWidth * .7f).row();
        contentTable.add(second_three).padTop(10 * density).width(dialogWidth).row();

        Table second_four = new Table();
        second_four.add(new Label("1.", subheadingStyle)).align(Align.topLeft).padLeft(padLeft*.3f).padTop(2 * density);
        second_four.add(termDetails2_4).padLeft(padLeft * .3f).padTop(padLeft * .1f).width(dialogWidth * .6f).row();
        contentTable.add(second_four).padTop(10 * density).width(dialogWidth).row();

        Table second_five = new Table();
        second_five.add(new Label("2.", subheadingStyle)).align(Align.topLeft).padLeft(padLeft*.3f).padTop(2 * density);
        second_five.add(termDetails2_5).padLeft(padLeft * .3f).padTop(padLeft * .1f).width(dialogWidth * .6f).row();
        contentTable.add(second_five).padTop(10 * density).width(dialogWidth).row();

        Table second_six = new Table();
        second_six.add(new Label("3.", subheadingStyle)).align(Align.topLeft).padLeft(padLeft*.3f).padTop(2 * density);
        second_six.add(termDetails2_6).padLeft(padLeft * .3f).padTop(padLeft * .1f).width(dialogWidth * .6f).row();
        contentTable.add(second_six).padTop(10 * density).width(dialogWidth).row();


        Table second_seven = new Table();
        second_seven.add(new Label("4.", subheadingStyle)).align(Align.topLeft).padLeft(padLeft*.3f).padTop(2 * density);
        second_seven.add(termDetails2_7).padLeft(padLeft * .3f).padTop(padLeft * .1f).width(dialogWidth * .6f).row();
        contentTable.add(second_seven).padTop(10 * density).width(dialogWidth).padBottom(5*density).row();


        return contentTable;
    }



    public Table playResponsiblyTable() {
        Table contentTable = new Table();
        String msg1 = "[#ffffff]IMPORTANT: The player must be at least 18 YEAR OF AGE to play poker in BIG CASH.";
        String msg2 = "Here are some of the best practices to help you play responsibly:";
        String msg3 = "Apart from the Restricted states of Assam, Odisha, Nagaland, Sikkim, Gujarat and Telengana, " +
                "Big Cash poker currently not available to persons residing in the state of kerala. we reserve the" +
                " right to update the Restricted States for Big Cash poker as required by Applicable Law or court Rulling.";

        Label termDetails1 = new Label(msg1, headingStyle);
        Label termDetails2 = new Label(msg2, subheadingStyle);
        Label termDetails3 = new Label(msg3, subheadingStyle);
        termDetails1.setWrap(true);
        termDetails2.setWrap(true);
        termDetails3.setWrap(true);

        Table first = new Table();
        first.add(termDetails1).padLeft(padLeft * .1f).padTop(padLeft * .4f).width(dialogWidth * .8f).row();
        contentTable.add(first).width(dialogWidth).row();


        String msg2_1 = "Play poker in moderation and only for entertainment.";
        String msg2_2 = "Do not play poker to make money or escape problem.";
        String msg2_3 = "Never chase your losses while playing poker.";
        String msg2_4 = "Set aside an entertainment budget for poker.";
        String msg2_5 = "Keep track of the time and monitor the amount of money you spend.";
        String msg2_6 = "Balance the time you spend on playing online poker with other leisure activities.";

        Label termDetails2_1 = new Label(msg2_1, detailsStyle);
        Label termDetails2_2 = new Label(msg2_2, detailsStyle);
        Label termDetails2_3 = new Label(msg2_3, detailsStyle);
        Label termDetails2_4 = new Label(msg2_4, detailsStyle);
        Label termDetails2_5 = new Label(msg2_5, detailsStyle);
        Label termDetails2_6 = new Label(msg2_6, detailsStyle);

        termDetails2_1.setWrap(true);
        termDetails2_2.setWrap(true);
        termDetails2_3.setWrap(true);
        termDetails2_4.setWrap(true);
        termDetails2_5.setWrap(true);
        termDetails2_6.setWrap(true);

        Table termSecond = new Table();
        termSecond.add(termDetails2).padLeft(padLeft * .1f).padTop(padLeft * .3f).width(dialogWidth * .8f).row();
        contentTable.add(termSecond).padTop(10 * density).width(dialogWidth).row();

        Table second_one = new Table();
        second_one.add(new Label("1.", subheadingStyle)).align(Align.topLeft).padTop(2 * density);
        second_one.add(termDetails2_1).padLeft(padLeft * .3f).padTop(padLeft * .1f).width(dialogWidth * .7f).row();
        contentTable.add(second_one).padTop(10 * density).width(dialogWidth).row();

        Table second_two = new Table();
        second_two.add(new Label("2.", subheadingStyle)).align(Align.topLeft).padTop(2 * density);
        second_two.add(termDetails2_2).padLeft(padLeft * .3f).padTop(padLeft * .1f).width(dialogWidth * .7f).row();
        contentTable.add(second_two).padTop(10 * density).width(dialogWidth).row();

        Table second_three = new Table();
        second_three.add(new Label("3.", subheadingStyle)).align(Align.topLeft).padTop(2 * density);
        second_three.add(termDetails2_3).padLeft(padLeft * .3f).padTop(padLeft * .1f).width(dialogWidth * .7f).row();
        contentTable.add(second_three).padTop(10 * density).width(dialogWidth).row();

        Table second_four = new Table();
        second_four.add(new Label("4.", subheadingStyle)).align(Align.topLeft).padTop(2 * density);
        second_four.add(termDetails2_4).padLeft(padLeft * .3f).padTop(padLeft * .1f).width(dialogWidth * .7f).row();
        contentTable.add(second_four).padTop(10 * density).width(dialogWidth).row();

        Table second_five = new Table();
        second_five.add(new Label("5.", subheadingStyle)).align(Align.topLeft).padTop(2 * density);
        second_five.add(termDetails2_5).padLeft(padLeft * .3f).padTop(padLeft * .1f).width(dialogWidth * .7f).row();
        contentTable.add(second_five).padTop(10 * density).width(dialogWidth).row();

        Table second_six = new Table();
        second_six.add(new Label("6.", subheadingStyle)).align(Align.topLeft).padTop(2 * density);
        second_six.add(termDetails2_6).padLeft(padLeft * .3f).padTop(padLeft * .1f).width(dialogWidth * .7f).row();
        contentTable.add(second_six).padTop(10 * density).width(dialogWidth).row();


        Table third = new Table();
        third.add(termDetails3).padLeft(padLeft * .1f).padTop(padLeft * .3f).width(dialogWidth * .8f).row();
        contentTable.add(third).padTop(10 * density).padBottom(padLeft).width(dialogWidth).row();
        return contentTable;
    }





    private void buildHeaderTable(Table contentTable) {
        Table headerTable = new Table();
        NinePatchDrawable headerBackground = new NinePatchDrawable(new NinePatch(uiAtlas.findRegion("bg_header_bar"), 1, 1, 1, 6));
        headerTable.setBackground(headerBackground);

        Label.LabelStyle headerLabelStyle = new Label.LabelStyle();
        headerLabelStyle.font = FontPool.obtain(FontType.ROBOTO_BOLD, 7);
        headerLabelStyle.fontColor = Color.WHITE;

        Label.LabelStyle titleStyle = new Label.LabelStyle();
        titleStyle.font = FontPool.obtain(FontType.ROBOTO_BOLD, 9);
        titleStyle.fontColor = Color.WHITE;

        TextureRegion backTexture = uiAtlas.findRegion("btn_wback");
        float backHeight = headerLabelStyle.font.getLineHeight()*1.6f;
        float backWidth = backHeight * backTexture.getRegionWidth() / backTexture.getRegionHeight();
        TextureRegionDrawable menuDrawable = TextureDrawable.getDrawable(backTexture, backWidth, backHeight);
        final Button backButton = new Button(menuDrawable, menuDrawable, menuDrawable);
        headerTable.add(backButton).width(backWidth).height(backHeight).padLeft(8 * density);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hide();
            }
        });


        headerTable.add(new Label("Poker : How to play?", titleStyle)).expandX();
        headerTable.add().width(backWidth).padRight(8*density);
        float headerHeight=backHeight*2.0f;
        contentTable.add(headerTable).width(width).height(headerHeight).row();
    }

    @Override
    public void hide() {
        super.hide();
    }

}
