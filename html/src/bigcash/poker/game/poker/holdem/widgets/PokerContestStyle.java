package bigcash.poker.game.poker.holdem.widgets;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import bigcash.poker.font.FontPool;
import bigcash.poker.font.FontStyle;
import bigcash.poker.font.FontType;
import bigcash.poker.models.UIScreen;
import bigcash.poker.widgets.StyledLabel;

public class PokerContestStyle {
    public NinePatchDrawable background,blueButton;
    public TextureRegion onlineUserIcon;
    public float tableWidth;
    public TextureRegion onlineRegion,pokerBg;
    public TextureRegionDrawable twoPlayersBg, fivePlayersBg;
    public Label.LabelStyle onlineStyle, twoPlayerStyle, fivePlayerStyle,winnersStyle,bigLabelStyle;
    public Label.LabelStyle qrLabelStyle;
    public Label.LabelStyle winningsStyle,offerStyle;
    public StyledLabel.StyledLabelStyle pokerBigWhiteLabelStyle,pokerWhiteLabelStyleForHighEntry1,smallWhiteLabelStyle;
    public FontStyle fontStyle;
    public TextureRegionDrawable downDrawable;

    public PokerContestStyle(TextureAtlas uiAtlas, UIScreen screen) {
        float density=screen.density;
        tableWidth=screen.width*0.9f;
        background = new NinePatchDrawable(new NinePatch(uiAtlas.findRegion("bg_content_full"), 8, 8, 8, 8));
        blueButton = new NinePatchDrawable(new NinePatch(uiAtlas.findRegion("btn_cblue"), 4, 4, 4, 4));
        bigLabelStyle = new Label.LabelStyle();
        bigLabelStyle.font = FontPool.obtain(FontType.ROBOTO_REGULAR, 6);
        downDrawable = new TextureRegionDrawable(uiAtlas.findRegion("icon_down"));
        winningsStyle = new Label.LabelStyle();
        winningsStyle.font = FontPool.obtain(FontType.ROBOTO_BOLD, 8);
        winningsStyle.fontColor = Color.SCARLET;

        pokerWhiteLabelStyleForHighEntry1 = new StyledLabel.StyledLabelStyle(FontType.ROBOTO_BOLD,5.3f);
        pokerWhiteLabelStyleForHighEntry1.fontColor = Color.WHITE;
        fontStyle = new FontStyle();
        fontStyle.setShadowX(1.0f);
        fontStyle.setShadowY(1.0f);
        fontStyle.setShadowColor(Color.BLACK);


        pokerBigWhiteLabelStyle = new StyledLabel.StyledLabelStyle(FontType.ROBOTO_BOLD, 5.9f);
        pokerBigWhiteLabelStyle.fontColor = Color.WHITE;

        qrLabelStyle=new Label.LabelStyle();
        qrLabelStyle.font=FontPool.obtain(FontType.ROBOTO_BOLD,5.9f);
        qrLabelStyle.fontColor=Color.WHITE;

        smallWhiteLabelStyle = new StyledLabel.StyledLabelStyle(FontType.ROBOTO_BOLD, 4.5f);
        smallWhiteLabelStyle.fontColor = Color.WHITE;

        offerStyle = new Label.LabelStyle();
        offerStyle.font = FontPool.obtain(FontType.ROBOTO_BOLD, 5);
        offerStyle.fontColor = Color.SCARLET;
//        bigLabelStyle.fontColor = Color.valueOf("535353");
        bigLabelStyle.fontColor = Color.BLACK;
        winnersStyle=new Label.LabelStyle();
        winnersStyle.font = FontPool.obtain(FontType.ROBOTO_BOLD, 8);
        winnersStyle.fontColor=Color.DARK_GRAY;
        onlineStyle = new Label.LabelStyle();
        onlineStyle.font = FontPool.obtain(FontType.ROBOTO_BOLD, 5);
        onlineStyle.fontColor = Color.valueOf("176e1b");
        twoPlayerStyle = new Label.LabelStyle();
        twoPlayerStyle.font = FontPool.obtain(FontType.ROBOTO_BOLD, 6);
        twoPlayerStyle.fontColor = Color.WHITE;
        twoPlayerStyle.background = new TextureRegionDrawable(uiAtlas.findRegion("two_players"));
        twoPlayerStyle.background.setLeftWidth(10 * density);
        twoPlayerStyle.background.setRightWidth(10 * density);
        twoPlayerStyle.background.setTopHeight(2 * density);
        twoPlayerStyle.background.setBottomHeight(5 * density);
        fivePlayerStyle = new Label.LabelStyle();
        fivePlayerStyle.font = FontPool.obtain(FontType.ROBOTO_BOLD, 6);
        fivePlayerStyle.fontColor = Color.WHITE;
        fivePlayerStyle.background = new TextureRegionDrawable(uiAtlas.findRegion("five_players"));
        fivePlayerStyle.background.setLeftWidth(10 * density);
        fivePlayerStyle.background.setRightWidth(10 * density);
        fivePlayerStyle.background.setTopHeight(2 * density);
        fivePlayerStyle.background.setBottomHeight(5 * density);

        pokerBg = uiAtlas.findRegion("bg_poker_header");

        twoPlayersBg = new TextureRegionDrawable(uiAtlas.findRegion("two_players"));
        fivePlayersBg = new TextureRegionDrawable(uiAtlas.findRegion("five_players"));
        onlineRegion=uiAtlas.findRegion("online_group");
    }
}
