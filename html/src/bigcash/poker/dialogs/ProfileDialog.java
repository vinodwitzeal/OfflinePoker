package bigcash.poker.dialogs;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

import bigcash.poker.constants.Constant;
import bigcash.poker.font.FontPool;
import bigcash.poker.font.FontType;
import bigcash.poker.models.ReferralLeague;
import bigcash.poker.models.UIDialog;
import bigcash.poker.models.UIScreen;
import bigcash.poker.network.ApiHandler;
import bigcash.poker.network.GdxListener;
import bigcash.poker.utils.AssetsLoader;
import bigcash.poker.utils.DrawableBuilder;
import bigcash.poker.widgets.MaskedImage;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

public class ProfileDialog extends UIDialog {
    private Texture backgroundTexture;
    private TextureAtlas atlas;
    private Label bonusLabel;

    public ProfileDialog(UIScreen screen) {
        super(screen);
        setKeepWithinStage(false);
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
        Table mainTable = getContentTable();
        Drawable background = null;
        mainTable.setBackground(background);
        Table contentTable = new Table();
        float width = this.width * 0.75f;
        TextureRegionDrawable screenBackground = DrawableBuilder.getDrawable(backgroundTexture, width, height);
        contentTable.defaults().space(0);
        contentTable.top();
        contentTable.setBackground(screenBackground);
        mainTable.add(contentTable).width(width).fillX();

        Table blankTable = new Table();
        blankTable.setFillParent(true);
        mainTable.add(blankTable).width(this.width * 0.25f).height(height).fill();
        blankTable.setTouchable(Touchable.enabled);
        blankTable.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hide();
            }
        });


        Stack imageStack = new Stack();
        float imageSize = width * 0.3f;
        Table imageBackTable = new Table();
        Table imageTable = new Table();
        MaskedImage userImage = new MaskedImage(screen,AssetsLoader.instance().userRegion,AssetsLoader.instance().circleMaskLayer);
        imageTable.add(userImage).width(imageSize).height(imageSize);
        if (Constant.userProfile.getImageUrl() != null && !Constant.userProfile.getImageUrl().isEmpty()) {
            pokerGame.downloadImage(Constant.userProfile.getImageUrl(), userImage);
        }
        imageBackTable.add(imageTable).padTop(height * 0.05f).padBottom(4 * density).row();

        final Label.LabelStyle menuStyle = new Label.LabelStyle();
        menuStyle.font = FontPool.obtain(FontType.ROBOTO_REGULAR, 8);
        menuStyle.fontColor = Color.WHITE;
        if (Constant.userProfile.getName() != null && !Constant.userProfile.getName().isEmpty()) {
            imageBackTable.add(new Label(Constant.userProfile.getName(), menuStyle)).row();
        } else {
            imageBackTable.add(new Label("G" + Constant.userProfile.getUserId(), menuStyle)).row();
        }
        final Table walletTable = new Table();
        TextureRegion walletTexture = atlas.findRegion("icon_wwallet");
        float walletHeight = menuStyle.font.getCapHeight();
        float walletWidth = walletHeight * walletTexture.getRegionWidth() / walletTexture.getRegionHeight();
        walletTable.add(new Image(walletTexture)).width(walletWidth).height(walletHeight).padRight(4 * density);
        final Label balanceLabel = new Label(Constant.userProfile.getPaytmBalance() + "", menuStyle);
        walletTable.add(balanceLabel).row();
        bonusLabel = null;


        imageBackTable.add(walletTable).padBottom(height * 0.03f);


        imageStack.add(imageBackTable);

        contentTable.add(imageBackTable).pad(width * 0.02f).expandX().fillX().row();

        NinePatchDrawable menuBackground = new NinePatchDrawable(new NinePatch(atlas.findRegion("bg_menu"), 19, 19, 24, 0));

        Table menuTable = new Table();
        menuTable.setBackground(menuBackground);
        menuTable.top();

        contentTable.add(menuTable).width(width).expandY().fill();

        TextureRegionDrawable lineDrawable = new DrawableBuilder(4, 4).color(Color.DARK_GRAY).createDrawable();

        Table accountButton = menuButton("icon_account", "My Account", menuStyle);
        menuTable.add(accountButton).width(width).row();
        menuTable.add(new Image(lineDrawable)).width(width).row();

        Table addCashButton = menuButton("icon_addcash", "Add Cash", menuStyle);
        menuTable.add(addCashButton).width(width).row();
        menuTable.add(new Image(lineDrawable)).width(width).row();

        Table gameRulesButton = menuButton("icon_rules", "Game Rules", menuStyle);
        //  menuTable.add(gameRulesButton).width(width).row();
        //menuTable.add(new Image(lineDrawable)).width(width).row();

        Table inviteButton = menuButton("icon_winvite", "Invite & Earn", menuStyle);
        menuTable.add(inviteButton).width(width).row();
        menuTable.add(new Image(lineDrawable)).width(width).row();

        Table redeemButton = menuButton("icon_redeem", "Redeem", menuStyle);
        menuTable.add(redeemButton).width(width).row();
        menuTable.add(new Image(lineDrawable)).width(width).row();

        Table transactionsButton = menuButton("icon_transaction", "Transactions", menuStyle);
        menuTable.add(transactionsButton).width(width).row();
        menuTable.add(new Image(lineDrawable)).width(width).row();

        Table settingsButton = menuButton("icon_settings", "Settings", menuStyle);
        menuTable.add(settingsButton).width(width).row();
        menuTable.add(new Image(lineDrawable)).width(width).row();

        Table feedbackButton = menuButton("icon_feedback", "Feedback", menuStyle);
        menuTable.add(feedbackButton).width(width).row();
        menuTable.add(new Image(lineDrawable)).width(width).row();


        ClickListener profileClickListener = new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                final ProcessDialog processDialog = new ProcessDialog(screen);
                processDialog.show(screen.stage);
                ApiHandler.callProfileApi(true,new GdxListener<String>(){
                    @Override
                    public void onSuccess(String s) {
                        processDialog.hide();
                        new AccountDialog(screen).show(screen.stage);
                    }

                    @Override
                    public void onFail(String reason) {
                        processDialog.hide();
                        toast(reason);
                    }

                    @Override
                    public void onError(String error) {
                        processDialog.hide();
                        toast(error);
                    }
                });
            }
        };

        imageBackTable.setTouchable(Touchable.enabled);
        imageBackTable.addListener(profileClickListener);

        accountButton.setTouchable(Touchable.enabled);
        accountButton.addListener(profileClickListener);

        addCashButton.setTouchable(Touchable.enabled);
        addCashButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                new CashAddDialog(screen, new GdxListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        balanceLabel.setText(Constant.userProfile.getPaytmBalance() + "");
                    }

                    @Override
                    public void onFail(String reason) {

                    }

                    @Override
                    public void onError(String errorMessage) {

                    }
                }).show(screen.getStage());
            }
        });

        gameRulesButton.setTouchable(Touchable.enabled);
        gameRulesButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                //TODO
//                getProGame().config.openWebView(RestApis.GAME_RULES);
            }
        });

        inviteButton.setTouchable(Touchable.enabled);
        inviteButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                    final ProcessDialog processDialog = new ProcessDialog(screen);
                    processDialog.show(getStage());
                    ApiHandler.callReferralLeaderboardApi(false,new GdxListener<ReferralLeague>(){
                        @Override
                        public void onSuccess(ReferralLeague referralLeague) {
                            processDialog.hide();
                            new InviteDialog(screen,referralLeague).show(screen.stage);
                        }

                        @Override
                        public void onFail(String reason) {
                            processDialog.hide();
                            toast(reason);
                        }

                        @Override
                        public void onError(String error) {
                            processDialog.hide();
                            toast(error);
                        }
                    });

            }
        });

        redeemButton.setTouchable(Touchable.enabled);
        redeemButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                //TODO
                final ProcessDialog processDialog = new ProcessDialog(screen);
                processDialog.show(screen.getStage());
                ApiHandler.callRedeemConfigApi(new GdxListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        processDialog.hide();
                        new RedeemDialog(screen, new GdxListener<Float>() {
                            @Override
                            public void onSuccess(Float aFloat) {
                                balanceLabel.setText(aFloat.toString());
                            }

                            @Override
                            public void onFail(String reason) {

                            }

                            @Override
                            public void onError(String errorMessage) {

                            }
                        }).show(screen.stage);

                    }

                    @Override
                    public void onFail(String reason) {
                        processDialog.hide();
                        toast(reason);
                    }

                    @Override
                    public void onError(String error) {
                        processDialog.hide();
                        toast(error);
                    }
                });
            }
        });

        transactionsButton.setTouchable(Touchable.enabled);
        transactionsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                final ProcessDialog processDialog = new ProcessDialog(screen);
                processDialog.show(screen.stage);
                ApiHandler.callAccountSummaryApi(0,new GdxListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        processDialog.hide();
                        new TransactionDialog(screen).show(screen.stage);
                    }

                    @Override
                    public void onFail(String reason) {
                        processDialog.hide();
                        toast(reason);
                    }

                    @Override
                    public void onError(String errorMessage) {
                        processDialog.hide();
                        toast(errorMessage);
                    }
                });
            }
        });

        settingsButton.setTouchable(Touchable.enabled);
        settingsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                new SettingsDialog(screen).show(screen.stage);
            }
        });

        feedbackButton.setTouchable(Touchable.enabled);
        feedbackButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                new FeedbackDialog(screen).show(screen.stage);
            }
        });
    }


    private Table menuButton(String iconName, String labelName, Label.LabelStyle labelStyle) {
        Table menuButton = new Table();
        TextureRegion iconTexture = atlas.findRegion(iconName);
        float iconWidth = width * 0.07f;
        float iconHeight = iconWidth * iconTexture.getRegionHeight() / iconTexture.getRegionWidth();

        menuButton.add(new Image(iconTexture)).width(iconWidth).height(iconHeight).padLeft(iconWidth * 0.6f).padRight(iconWidth * 0.6f);
        menuButton.add(new Label(labelName, labelStyle)).expandX().align(Align.left);

        menuButton.padTop(iconWidth * 0.3f).padBottom(iconWidth * 0.3f);
        return menuButton;
    }


    @Override
    public Dialog show(Stage stage) {
        pack();
        setPosition(-getWidth(), Math.round((screen.height -getHeight()) / 2));
        show(stage, sequence(Actions.moveTo(0, Math.round((screen.height - getHeight()) / 2), 0.3f)));
        return this;
    }

    @Override
    public void hide() {
        hide(Actions.moveTo(-getWidth(), getY(), 0.3f));
    }
}
