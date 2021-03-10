package bigcash.poker.dialogs;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

import bigcash.poker.font.FontPool;
import bigcash.poker.font.FontType;
import bigcash.poker.models.PrizeRules;
import bigcash.poker.models.UIDialog;
import bigcash.poker.models.UIScreen;
import bigcash.poker.utils.AssetsLoader;
import bigcash.poker.utils.GamePreferences;
import bigcash.poker.utils.PokerUtils;
import bigcash.poker.utils.TextureDrawable;
import bigcash.poker.widgets.StyledLabel;

public class ReferralRulesDialog extends UIDialog {
    private TextureAtlas uiAtlas;
    private Array<PrizeRules> rules;
    public ReferralRulesDialog(UIScreen screen, Array<PrizeRules> rules) {
        super(screen);
        this.uiAtlas=AssetsLoader.instance().uiAtlas;
        this.rules=rules;
        buildDialog();
    }

    @Override
    public void init() {

    }

    @Override
    public void buildDialog() {
        Table contentTable = getContentTable();
        contentTable.top();
        TextureRegionDrawable background= TextureDrawable.getDrawable(AssetsLoader.instance().screenBackground,width,height);
        contentTable.setBackground(background);

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
        headerTable.add(new Label("Rules", titleStyle)).expandX();
        headerTable.add().width(backWidth).padRight(8*density);
        float headerHeight=backHeight*2.0f;
        contentTable.add(headerTable).width(width).height(headerHeight).row();

        Table contestRuleTable=new Table();
        Label.LabelStyle contestRuleStyle=new Label.LabelStyle();
        contestRuleStyle.font=FontPool.obtain(FontType.ROBOTO_REGULAR,6);
        contestRuleStyle.fontColor=Color.WHITE;
        String[] arrRules = GamePreferences.instance().getReferralContestRules().split("\\|");

        float linePad=contestRuleStyle.font.getCapHeight();
        for (int i=0;i<arrRules.length;i++){
            int index=i+1;
            contestRuleTable.add(new Label("("+index+") ",contestRuleStyle)).padRight(linePad).padBottom(linePad);
            Label ruleLabel=new Label(arrRules[i],contestRuleStyle);
            ruleLabel.setAlignment(Align.left);
            ruleLabel.setWrap(true);
            contestRuleTable.add(ruleLabel).padBottom(linePad).width(width*0.8f);
            contestRuleTable.row();
        }
        contentTable.add(contestRuleTable).padTop(4*density).row();

        Table prizeTable=new Table();
        prizeTable.top();
        StyledLabel.StyledLabelStyle prizeHeaderStyle=new StyledLabel.StyledLabelStyle(FontType.ROBOTO_BOLD,8);
        prizeHeaderStyle.fontColor=Color.CORAL;

        StyledLabel rankHeaderLabel=new StyledLabel("RANK",prizeHeaderStyle);
        rankHeaderLabel.outline(0.2f,Color.WHITE);
        rankHeaderLabel.shadow(0.5f,0.5f,Color.DARK_GRAY);
        prizeTable.add(rankHeaderLabel).expandX();
        StyledLabel prizeHeaderLabel=new StyledLabel("PRIZE",prizeHeaderStyle);
        prizeHeaderLabel.outline(0.2f,Color.WHITE);
        prizeHeaderLabel.shadow(0.5f,0.5f,Color.DARK_GRAY);
        prizeTable.add(prizeHeaderLabel).expandX();
        prizeTable.row();

        StyledLabel.StyledLabelStyle rankStyle=new StyledLabel.StyledLabelStyle(FontType.ROBOTO_BOLD,7);
        rankStyle.fontColor=Color.WHITE;

        StyledLabel.StyledLabelStyle prizeStyle=new StyledLabel.StyledLabelStyle(FontType.ROBOTO_BOLD,7);
        prizeStyle.fontColor=Color.GOLD;

        for (int i=0;i<rules.size;i++){
            PrizeRules prizeRules=rules.get(i);
            StyledLabel rankLabel=new StyledLabel(prizeRules.getMinRank()+"-"+prizeRules.getMaxRank(),rankStyle);
            rankLabel.shadow(0.5f,0.5f,Color.DARK_GRAY);
            StyledLabel prizeLabel=new StyledLabel("\u20b9 "+ PokerUtils.formatValue(prizeRules.getPrize()),rankStyle);
            prizeLabel.shadow(0.5f,0.5f,Color.DARK_GRAY);

            prizeTable.add(rankLabel).expandX();
            prizeTable.add(prizeLabel).expandX();
            prizeTable.row();
        }

        ScrollPane scrollPane=new ScrollPane(prizeTable);
        scrollPane.setScrollingDisabled(true,false);
        contentTable.add(scrollPane).expand().fill().padTop(4*density);

    }
}
