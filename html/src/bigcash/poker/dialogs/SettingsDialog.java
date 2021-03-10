package bigcash.poker.dialogs;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

import bigcash.poker.font.FontPool;
import bigcash.poker.font.FontType;
import bigcash.poker.gwt.PokerGame;
import bigcash.poker.models.UIDialog;
import bigcash.poker.models.UIScreen;
import bigcash.poker.network.ApiHandler;
import bigcash.poker.utils.AssetsLoader;
import bigcash.poker.utils.DrawableBuilder;
import bigcash.poker.utils.GamePreferences;
import bigcash.poker.utils.PokerUtils;
import bigcash.poker.utils.TextureDrawable;
import bigcash.poker.widgets.StyledLabel;


public class SettingsDialog extends UIDialog {
    private Texture backgroundTexture;
    private TextureAtlas atlas;
    private ImageButton notificationSoundButton, gameSoundButton, vibrationButton;
    private GamePreferences preferences;

    public SettingsDialog(UIScreen screen) {
        super(screen);
        preferences = GamePreferences.instance();
        dismissOnBack(true);
        buildDialog();
    }

    @Override
    public void init() {
        backgroundTexture = AssetsLoader.instance().screenBackground;
        atlas = AssetsLoader.instance().uiAtlas;
    }

    @Override
    public void buildDialog() {
        TextureRegionDrawable screenBackground = DrawableBuilder.getDrawable(backgroundTexture, width, height);
        Table contentTable = getContentTable();
        contentTable.defaults().space(0);
        contentTable.top();
        contentTable.setBackground(screenBackground);

        Table headerTable = new Table();
        NinePatchDrawable headerBackground = new NinePatchDrawable(new NinePatch(atlas.findRegion("bg_header_bar"), 1, 1, 1, 6));
        headerTable.setBackground(headerBackground);

        Label.LabelStyle headerLabelStyle = new Label.LabelStyle();
        headerLabelStyle.font = FontPool.obtain(FontType.ROBOTO_BOLD, 7);
        headerLabelStyle.fontColor = Color.WHITE;

        Label.LabelStyle titleStyle = new Label.LabelStyle();
        titleStyle.font = FontPool.obtain(FontType.ROBOTO_BOLD, 9);
        titleStyle.fontColor = Color.WHITE;

        TextureRegion backTexture = atlas.findRegion("btn_wback");
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


        headerTable.add(new Label("Settings", titleStyle)).expandX();
        headerTable.add().width(backWidth).padRight(8*density);
        float headerHeight=backHeight*2.0f;
        contentTable.add(headerTable).width(width).height(headerHeight).row();

        TextureRegionDrawable stripDrawable = DrawableBuilder.getDrawable(atlas.findRegion("bg_strip"), 20, 20);
        stripDrawable.setTopHeight(8 * density);
        stripDrawable.setBottomHeight(8 * density);
        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = FontPool.obtain(FontType.ROBOTO_REGULAR, 8);
        labelStyle.fontColor = Color.WHITE;

        TextureRegion onTexture = atlas.findRegion("btn_on");
        TextureRegion offTexture = atlas.findRegion("btn_off");
        float switchHeight = labelStyle.font.getLineHeight();
        float switchWidth = switchHeight * onTexture.getRegionWidth() / onTexture.getRegionHeight();

        TextureRegionDrawable onDrawable = DrawableBuilder.getDrawable(onTexture, switchWidth, switchHeight);
        TextureRegionDrawable offDrawable = DrawableBuilder.getDrawable(offTexture, switchWidth, switchHeight);

        float iconWidth = width * 0.07f;
        float iconHeight;

        Table notificationSoundTable = new Table();
        notificationSoundTable.setBackground(stripDrawable);
        TextureRegion notificationSoundTexture = atlas.findRegion("icon_not_sound");
        iconHeight = iconWidth * notificationSoundTexture.getRegionHeight() / notificationSoundTexture.getRegionWidth();

        notificationSoundTable.add(new Image(notificationSoundTexture)).width(iconWidth).height(iconHeight).padLeft(iconWidth).padRight(iconWidth);
        notificationSoundTable.add(new Label("Notification Sound", labelStyle)).expandX().align(Align.left);

        notificationSoundButton = new ImageButton(offDrawable, offDrawable, onDrawable);
        notificationSoundTable.add(notificationSoundButton).width(switchWidth).height(switchHeight).padRight(switchHeight);

        Table gameSoundTable = new Table();
        gameSoundTable.setBackground(stripDrawable);
        TextureRegion gameSoundTexture = atlas.findRegion("icon_game_sound");
        iconHeight = iconWidth * gameSoundTexture.getRegionHeight() / gameSoundTexture.getRegionWidth();

        gameSoundTable.add(new Image(gameSoundTexture)).width(iconWidth).height(iconHeight).padLeft(iconWidth).padRight(iconWidth);
        gameSoundTable.add(new Label("Game Sound", labelStyle)).expandX().align(Align.left);

        gameSoundButton = new ImageButton(offDrawable, offDrawable, onDrawable);
        gameSoundTable.add(gameSoundButton).width(switchWidth).height(switchHeight).padRight(switchHeight);

        Table vibrationTable = new Table();
        vibrationTable.setBackground(stripDrawable);
        TextureRegion vibrationTexture = atlas.findRegion("icon_vibrate");
        iconHeight = iconWidth * vibrationTexture.getRegionHeight() / vibrationTexture.getRegionWidth();

        vibrationTable.add(new Image(vibrationTexture)).width(iconWidth).height(iconHeight).padLeft(iconWidth).padRight(iconWidth);
        vibrationTable.add(new Label("Vibration", labelStyle)).expandX().align(Align.left);

        vibrationButton = new ImageButton(offDrawable, offDrawable, onDrawable);
        vibrationTable.add(vibrationButton).width(switchWidth).height(switchHeight).padRight(switchHeight);

        contentTable.add(notificationSoundTable).expandX().fillX().padBottom(10 * density).padTop(height * 0.1f).row();
        contentTable.add(gameSoundTable).expandX().fillX().padBottom(10 * density).row();
        contentTable.add(vibrationTable).expandX().fillX().padBottom(height * 0.1f).row();

        NinePatchDrawable menuBackground = new NinePatchDrawable(new NinePatch(atlas.findRegion("bg_menu"), 19, 19, 24, 0));

        Table buttonTable = new Table();
        buttonTable.setBackground(menuBackground);

        contentTable.add(buttonTable).expand().fill();


        TextButton likeButton = getTextButton("button_blue", "Like us on facebook");
        TextButton followButton = getTextButton("button_blue", "Follow us on Twitter");
        TextButton termsButton = getTextButton("button_red", "Terms & Condition");
        TextButton faqButton = getTextButton("button_orange", "FAQs");
        TextButton logoutButton = getTextButton("button_navy", "Logout");

        buttonTable.add(likeButton).width(width * 0.7f).padBottom(12 * density).padTop(height * 0.05f).row();
//        if (getProGame().config.isPackageExisted("com.twitter.android")) {
//            buttonTable.add(followButton).width(width * 0.7f).padBottom(12 * density).row();
//        }
        buttonTable.add(termsButton).width(width * 0.7f).padBottom(12 * density).row();
        buttonTable.add(faqButton).width(width * 0.7f).padBottom(12 * density).row();
        buttonTable.add(logoutButton).width(width * 0.7f).expandY().align(Align.top);


        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hide();
            }
        });


        notificationSoundButton.setChecked(preferences.getNotificationSound());
        gameSoundButton.setChecked(preferences.getGameSoundStatus());
        vibrationButton.setChecked(preferences.getGameVibration());


        notificationSoundButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                preferences.setNotificationSound(notificationSoundButton.isChecked());
            }
        });


        gameSoundButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                preferences.setGameSoundStatus(gameSoundButton.isChecked());
            }
        });

        vibrationButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                preferences.setGameVibration(vibrationButton.isChecked());
            }
        });

        likeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                PokerUtils.openUrl(ApiHandler.FACEBOOK_PAGE);

            }
        });
        followButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                PokerUtils.openUrl("https://twitter.com/witzealcash?lang=en");
            }
        });

        termsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                PokerUtils.openUrl(ApiHandler.TERMSANDCONDITION);
            }
        });

        logoutButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                PokerGame.resetAppWhenErrorGet();

            }
        });

        faqButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                PokerUtils.openUrl(ApiHandler.FAQ);
            }
        });
    }


    @Override
    public void hide(Action action) {
        preferences.setGameSoundStatus(gameSoundButton.isChecked());
        preferences.setNotificationSound(notificationSoundButton.isChecked());
        preferences.setGameVibration(vibrationButton.isChecked());
        ApiHandler.callSettingApi(
                notificationSoundButton.isChecked(),
                gameSoundButton.isChecked(),
                vibrationButton.isChecked()
        );
        super.hide(action);
    }

    private TextButton getTextButton(String drawable, String name) {
        NinePatchDrawable ninePatchDrawable = new NinePatchDrawable(new NinePatch(atlas.findRegion(drawable), 9, 9, 12, 16));
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = FontPool.obtain(FontType.ROBOTO_REGULAR, 7);

        ninePatchDrawable.setMinWidth(20);
        ninePatchDrawable.setMinHeight(buttonStyle.font.getLineHeight() * 2.0f);

        buttonStyle.fontColor = Color.WHITE;
        buttonStyle.downFontColor = Color.LIGHT_GRAY;
        buttonStyle.up = ninePatchDrawable;
        buttonStyle.down = ninePatchDrawable;
        return new TextButton(name, buttonStyle);
    }
}
