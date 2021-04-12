package bigcash.poker.game.poker.widgets;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import bigcash.poker.font.FontPool;
import bigcash.poker.font.FontType;
import bigcash.poker.models.UIScreen;
import bigcash.poker.utils.TextureDrawable;
import bigcash.poker.widgets.StyledLabel;

public class PokerStyle {
    public float width,pad,bottomPad;

    public NinePatchDrawable prizeBackground;
    public Label.LabelStyle prizeStyle,prizeLabelStyle;
    public float prizeWidth;

    public NinePatchDrawable contestBackground;
    public float cardGap;
    public Label.LabelStyle labelStyle,valueStyle;

    public TextureRegion playerIconRegion;
    public float playerIconWidth,playerIconHeight;

    public TextureRegionDrawable buttonBackground;
    public StyledLabel.StyledLabelStyle entryLabelStyle;
    public StyledLabel.StyledLabelStyle entryStyle;
    public float buttonWidth,buttonHeight;


    public TextureRegion offlineBackgroundTexture;
    public float backgroundWidth,backgroundHeight;
    public float backgroundX,backgroundY;
    public float offlineWidth,offlineHeight;
    public float offlineIconX,offlineIconY,offlineIconSize;
    public NinePatchDrawable scanButtonBackground;
    public Label.LabelStyle scanButtonStyle;
    public float scanButtonHeight,scanButtonWidth;
    public float scanButtonX,scanButtonY;
    public TextureRegion qrIconRegion;
    public float qrIconWidth,qrIconHeight;

    public float logoWidth,logoHeight;
    public float logoX,logoY;
    public float topPad;


    public PokerStyle(UIScreen screen, TextureAtlas atlas){
        this.width=screen.width*0.95f;
        this.prizeWidth=this.width*0.2f;
        this.pad=this.width*0.02f;
        this.cardGap=this.width*0.035f;
        this.bottomPad=this.width*0.045f;
        this.prizeBackground=new NinePatchDrawable(new NinePatch(atlas.findRegion("contest_bg_prize"),9,9,9,9));
        this.prizeBackground.setMinSize(20,20);
        this.contestBackground=new NinePatchDrawable(new NinePatch(atlas.findRegion("contest_bg_detail"),9,9,9,9));
        this.contestBackground.setMinSize(20,20);
        this.prizeLabelStyle=new Label.LabelStyle();
        this.prizeLabelStyle.font= FontPool.obtain(FontType.ROBOTO_REGULAR,4);
        this.prizeLabelStyle.fontColor= Color.valueOf("323232");
        this.prizeStyle=new Label.LabelStyle();
        this.prizeStyle.font= FontPool.obtain(FontType.ROBOTO_BOLD,9f);
        this.prizeStyle.fontColor=Color.SCARLET;
        this.labelStyle=new Label.LabelStyle();
        this.labelStyle.font= FontPool.obtain(FontType.ROBOTO_REGULAR,5.5f);
        this.labelStyle.fontColor=Color.valueOf("323232");
        this.valueStyle=new Label.LabelStyle();
        this.valueStyle.font= FontPool.obtain(FontType.ROBOTO_BOLD,6.5f);
        this.valueStyle.fontColor=Color.valueOf("095ac9");
        this.playerIconRegion=atlas.findRegion("contest_player");
        this.playerIconHeight=this.valueStyle.font.getCapHeight();
        this.playerIconWidth=this.playerIconHeight*playerIconRegion.getRegionWidth()/playerIconRegion.getRegionHeight();

        this.entryLabelStyle=new StyledLabel.StyledLabelStyle(FontType.ROBOTO_BOLD,5.0f);
        this.entryLabelStyle.fontColor=Color.WHITE;
        this.entryStyle=new StyledLabel.StyledLabelStyle(FontType.ROBOTO_BOLD,7.f);
        this.entryStyle.fontColor=Color.WHITE;
        TextureRegion blueButtonTexture=atlas.findRegion("contest_btn");
        this.buttonHeight=entryStyle.font.getLineHeight()*1.75f;
        this.buttonWidth=this.buttonHeight*blueButtonTexture.getRegionWidth()/blueButtonTexture.getRegionHeight();
        this.buttonBackground= TextureDrawable.getDrawable(blueButtonTexture,buttonWidth,buttonHeight);


        offlineBackgroundTexture=atlas.findRegion("private_contest_bg");
        backgroundWidth=screen.width*0.92f;
        backgroundHeight=backgroundWidth*offlineBackgroundTexture.getRegionHeight()/offlineBackgroundTexture.getRegionWidth();
        float leftSpace=width-backgroundWidth;
        offlineIconSize=backgroundHeight*0.5f;
        offlineIconX=leftSpace+backgroundWidth*0.39f-offlineIconSize/2f;
        offlineIconY=backgroundHeight*0.1f;
        scanButtonBackground=new NinePatchDrawable(new NinePatch(atlas.findRegion("scan_btn_bg"),8,8,6,12));
        scanButtonStyle=new Label.LabelStyle();
        scanButtonStyle.font=FontPool.obtain(FontType.ROBOTO_BOLD,5);
        scanButtonStyle.fontColor=Color.WHITE;

        qrIconRegion=atlas.findRegion("ic_qr");
        qrIconHeight=scanButtonStyle.font.getCapHeight();
        qrIconWidth=qrIconHeight*qrIconRegion.getRegionWidth()/qrIconRegion.getRegionHeight();

        scanButtonHeight=scanButtonStyle.font.getLineHeight()*2.5f;
        scanButtonWidth=7*scanButtonStyle.font.getLineHeight();
        scanButtonX=leftSpace+backgroundWidth*0.95f-scanButtonWidth;
        scanButtonY=(backgroundHeight-scanButtonHeight)/2f;
        offlineWidth=width;
        offlineHeight=backgroundHeight;
        backgroundX=leftSpace;
        backgroundY=0;

        logoHeight=offlineHeight*0.5f;
        logoWidth=logoHeight*1.48f;
        logoX=0;
        logoY=offlineHeight-logoHeight*0.75f;
        topPad=logoHeight*0.3f;
    }
}
