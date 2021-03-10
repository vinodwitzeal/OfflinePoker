package bigcash.poker.dialogs;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import bigcash.poker.font.FontPool;
import bigcash.poker.font.FontType;
import bigcash.poker.models.UIScreen;

public class PolicyDialog extends IDialog{
    private String policies;
    public PolicyDialog(UIScreen screen,String policies) {
        super(screen);
        this.policies=policies;
        buildDialog();
    }

    @Override
    public void buildDialog() {
        Table contentTable=getContentTable();
        float dialogWidth=width*0.9f;

        Label.LabelStyle headingStyle=new Label.LabelStyle();
        headingStyle.font= FontPool.obtain(FontType.ROBOTO_BOLD,10);
        headingStyle.fontColor= Color.valueOf("505050");
        float headingPad=headingStyle.font.getCapHeight();
        dataTable.add(new Label("Terms & Conditions",headingStyle)).pad(headingPad).row();

        Label.LabelStyle policyStyle=new Label.LabelStyle();
        policyStyle.font=FontPool.obtain(FontType.ROBOTO_REGULAR,7);
        policyStyle.fontColor=Color.valueOf("828282");

        Table scrollTable = new Table();
        scrollTable.top();
        ScrollPane scrollPane = new ScrollPane(scrollTable);
        scrollPane.setScrollingDisabled(true, false);

        Label policyLabel=new Label(policies,policyStyle);
        policyLabel.setWrap(true);
        policyLabel.setAlignment(Align.left);
        scrollTable.add(policyLabel).expandX().fillX();

        dataTable.add(scrollPane).width(dialogWidth*0.9f).height(height*0.5f).row();

        TextButton.TextButtonStyle closeButtonStyle=new TextButton.TextButtonStyle();
        closeButtonStyle.font=FontPool.obtain(FontType.ROBOTO_BOLD,6);
        closeButtonStyle.fontColor=Color.WHITE;
        closeButtonStyle.up=redDrawable;

        float buttonHeight=closeButtonStyle.font.getLineHeight()*3f;
        float buttonWidth=dialogWidth*0.5f;

        TextButton closeButton=new TextButton("Close",closeButtonStyle);
        closeButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hide();
            }
        });
        buttonTable.add(closeButton).width(buttonWidth).height(buttonHeight).padTop(headingPad).padBottom(headingPad);

        dataTable.add(buttonTable).width(dialogWidth);
        contentTable.add(dataTable).width(dialogWidth);
    }
}
