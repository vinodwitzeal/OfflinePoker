package bigcash.poker.dialogs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

import bigcash.poker.constants.Constant;
import bigcash.poker.font.FontPool;
import bigcash.poker.font.FontType;
import bigcash.poker.models.FriendsDto;
import bigcash.poker.models.UIDialog;
import bigcash.poker.models.UIScreen;
import bigcash.poker.network.ApiHandler;
import bigcash.poker.network.GdxListener;
import bigcash.poker.utils.AssetsLoader;
import bigcash.poker.utils.DrawableBuilder;
import bigcash.poker.utils.TextureDrawable;
import bigcash.poker.widgets.MaskedImage;

public class AccountDialog extends UIDialog {
    private Texture backgroundTexture;
    private TextureAtlas atlas;

    public AccountDialog(UIScreen screen) {
        super(screen);
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

        Label.LabelStyle errorLabelStyle = new Label.LabelStyle();
        errorLabelStyle.font = FontPool.obtain(FontType.ROBOTO_REGULAR, 6);
        errorLabelStyle.fontColor = Color.RED;

        final Label errorLabel = new Label("", errorLabelStyle);
        errorLabel.setAlignment(Align.center);
        errorLabel.setWrap(true);

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

        TextureRegion backTexture = atlas.findRegion("btn_wback");
        float backHeight = FontPool.obtain(FontType.ROBOTO_BOLD,7).getLineHeight()*1.6f;
        float backWidth = backHeight * backTexture.getRegionWidth() / backTexture.getRegionHeight();
        TextureRegionDrawable backDrawable = TextureDrawable.getDrawable(backTexture, backWidth, backHeight);
        ImageButton backButton = new ImageButton(backDrawable, backDrawable, backDrawable);
        Table buttonTable = new Table();
        buttonTable.top().left();
        buttonTable.add(backButton).width(backWidth).height(backHeight);

        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hide();
            }
        });

        imageStack.add(imageBackTable);
        imageStack.add(buttonTable);
        contentTable.add(imageStack).pad(height * 0.01f).expandX().fillX().row();


        if (Constant.userProfile.getFriendsDtos() != null && Constant.userProfile.getFriendsDtos().size > 0) {
            TextureRegionDrawable friendsBackground = new DrawableBuilder(10, 10).color(Color.valueOf("072441")).createDrawable();

            Table friendsTable = new Table();
            friendsTable.setBackground(friendsBackground);
            friendsTable.padTop(10 * density);
            friendsTable.padBottom(10 * density);

            Label.LabelStyle friendTitleStyle = new Label.LabelStyle();
            friendTitleStyle.font = FontPool.obtain(FontType.ROBOTO_BOLD, 8);
            friendTitleStyle.fontColor = Color.valueOf("7c90be");
            Label.LabelStyle friendsStyle = new Label.LabelStyle();

            friendsStyle.font = FontPool.obtain(FontType.ROBOTO_REGULAR, 6);
            friendsStyle.fontColor = Color.WHITE;

            friendsTable.add(new Label("Facebook Friends", friendTitleStyle)).padBottom(8 * density).row();

            Table scrollTable = new Table();
            ScrollPane scrollPane = new ScrollPane(scrollTable);
            float friendImageSize = width * 0.2f;
            float friendImagePad = friendImageSize * 0.2f;
            for (FriendsDto friendsDto : Constant.userProfile.getFriendsDtos()) {
                Table friendTable = new Table();
                MaskedImage friendImage = new MaskedImage(screen,AssetsLoader.instance().userRegion,AssetsLoader.instance().circleMaskLayer);
                pokerGame.downloadImage(friendsDto.getProfilePicUrl(), friendImage);
                Label friendNameLabel = new Label(getFirstName(friendsDto.getName()), friendsStyle);
                friendNameLabel.setAlignment(Align.center);
                friendTable.add(friendImage).width(friendImageSize).height(friendImageSize).row();
                friendTable.add(friendNameLabel);
                scrollTable.add(friendTable).pad(friendImagePad);
            }
            scrollPane.setScrollingDisabled(false, true);
            friendsTable.add(scrollPane).width(width);

            contentTable.add(friendsTable).width(width).padTop(10 * density).row();
        } else {
            contentTable.add().height(height * 0.15f).row();
        }
        Gdx.app.error("Passed","139");

        final Label.LabelStyle verifyLabelStyle = new Label.LabelStyle();
        verifyLabelStyle.font = FontPool.obtain(FontType.ROBOTO_REGULAR, 7);
        verifyLabelStyle.fontColor = Color.WHITE;
        verifyLabelStyle.background = new NinePatchDrawable(new NinePatch(atlas.findRegion("button_red"), 12, 12, 12, 16));
        verifyLabelStyle.background.setMinWidth(16);
        verifyLabelStyle.background.setMinHeight(16);

        final Label.LabelStyle verifiedLabelStyle = new Label.LabelStyle();
        verifiedLabelStyle.font = FontPool.obtain(FontType.ROBOTO_BOLD, 7);
        verifiedLabelStyle.fontColor = Color.GREEN;

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = FontPool.obtain(FontType.ROBOTO_REGULAR, 6);
        labelStyle.fontColor = Color.WHITE;

        NinePatchDrawable menuBackground = new NinePatchDrawable(new NinePatch(atlas.findRegion("bg_menu"), 19, 19, 24, 0));

        Table menuTable = new Table();
        menuTable.setBackground(menuBackground);
        menuTable.top();
        menuTable.padTop(20 * density);

        contentTable.add(menuTable).width(width).expandY().fill();


        float iconWidth = width * 0.07f;
        float iconHeight;

        TextureRegion emailTexture = atlas.findRegion("icon_mail");
        iconHeight = iconWidth * emailTexture.getRegionHeight() / emailTexture.getRegionWidth();
        final Label emailLabel;
        if (Constant.userProfile.getEmailId() != null && !Constant.userProfile.getEmailId().isEmpty()) {
            menuTable.add(new Image(emailTexture)).width(iconWidth).height(iconHeight).padLeft(iconWidth * 0.5f).padRight(iconWidth * 0.3f);
            emailLabel = new Label(Constant.userProfile.getEmailId(), labelStyle);
            emailLabel.setEllipsis(true);
            menuTable.add(emailLabel).expandX().fillX().align(Align.left);
        }

        menuTable.add().uniformX().row();

        menuTable.add().colspan(3).height(10 * density).row();


        TextureRegion phoneTexture = atlas.findRegion("icon_phone");
        iconHeight = iconWidth * phoneTexture.getRegionHeight() / phoneTexture.getRegionWidth();


        menuTable.add(new Image(phoneTexture)).width(iconWidth).height(iconHeight).padLeft(iconWidth * 0.5f).padRight(iconWidth * 0.3f);

        final Label phoneLabel;
        if (Constant.userProfile.getMsisdn() != null && !Constant.userProfile.getMsisdn().isEmpty()) {
            phoneLabel = new Label(Constant.userProfile.getMsisdn(), labelStyle);
        } else {
            phoneLabel = new Label("---", labelStyle);
        }
        menuTable.add(phoneLabel).expandX().align(Align.left);

        final Label phoneVerifyLabel;
        if (Constant.userProfile.isMsisdnVerified()) {
            phoneVerifyLabel = new Label("Verified", verifiedLabelStyle);
        } else {
            phoneVerifyLabel = new Label("Verify", verifyLabelStyle);
            phoneVerifyLabel.setTouchable(Touchable.enabled);
            phoneVerifyLabel.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    try {
                        new MobileVerificationDialog(screen, new GdxListener<String>() {
                            @Override
                            public void onSuccess(String s) {
                                phoneLabel.setText(Constant.userProfile.getMsisdn());
                                phoneVerifyLabel.setStyle(verifiedLabelStyle);
                                phoneVerifyLabel.setText("Verified");
                                phoneVerifyLabel.clearListeners();
                            }

                            @Override
                            public void onFail(String reason) {
                                toast(reason);
                            }

                            @Override
                            public void onError(String error) {
                                errorLabel.setText(error);
                            }
                        }).show(screen.stage);
                    }catch (Exception e){
                        Gdx.app.error("Error",e.getMessage());
                    }

                }
            });
        }
        menuTable.add(phoneVerifyLabel).uniformX().row();

        if (Constant.userProfile.getPanStatus() != null && !Constant.userProfile.getPanStatus().matches("NOT_REQUIRED")) {
            menuTable.add().colspan(3).height(10 * density).row();
            Constant.userProfile.setPanStatus("VERIFY");
            TextureRegion panTexture = atlas.findRegion("icon_card");
            iconHeight = iconWidth * panTexture.getRegionHeight() / panTexture.getRegionWidth();

            menuTable.add(new Image(panTexture)).width(iconWidth).height(iconHeight).padLeft(iconWidth * 0.5f).padRight(iconWidth * 0.3f);

            final Label panLabel;
            if (Constant.userProfile.getPanNumber() != null && !Constant.userProfile.getPanNumber().isEmpty()) {
                panLabel = new Label(Constant.userProfile.getPanNumber(), labelStyle);
            } else {
                panLabel = new Label("---", labelStyle);
            }

            panLabel.setEllipsis(true);
            menuTable.add(panLabel).expandX().align(Align.left);

            Label.LabelStyle uploadLabelStyle = new Label.LabelStyle();
            uploadLabelStyle.font = FontPool.obtain(FontType.ROBOTO_REGULAR, 7);
            uploadLabelStyle.fontColor = Color.WHITE;
            uploadLabelStyle.background = new NinePatchDrawable(new NinePatch(atlas.findRegion("button_blue"), 12, 12, 12, 16));
            uploadLabelStyle.background.setMinWidth(16);
            uploadLabelStyle.background.setMinHeight(16);

            final Label.LabelStyle pendingLabelStyle = new Label.LabelStyle();
            pendingLabelStyle.font = FontPool.obtain(FontType.ROBOTO_REGULAR, 7);
            pendingLabelStyle.fontColor = Color.WHITE;
            pendingLabelStyle.background = new NinePatchDrawable(new NinePatch(atlas.findRegion("button_orange"), 12, 12, 12, 16));
            pendingLabelStyle.background.setMinWidth(16);
            pendingLabelStyle.background.setMinHeight(16);

            final Label panVerifyLabel;
            if (Constant.userProfile.getPanStatus().matches("REQUIRED")) {
                panVerifyLabel = new Label("Upload", uploadLabelStyle);
            } else if (Constant.userProfile.getPanStatus().matches("VERIFIED")) {
                panVerifyLabel = new Label("Verified", verifiedLabelStyle);
            } else if (Constant.userProfile.getPanStatus().matches("PENDING")) {
                panVerifyLabel = new Label("Pending", pendingLabelStyle);
            } else {
                panVerifyLabel = new Label("Verify", verifyLabelStyle);
                panVerifyLabel.setTouchable(Touchable.enabled);
//                panVerifyLabel.addListener(new ClickListener() {
//                    @Override
//                    public void clicked(InputEvent event, float x, float y) {
//                        EffectsHelper.playTouchSound();
//                        new PCVerificationDialog(getModelScreen(), new GdxListener<String>() {
//                            @Override
//                            public void onSuccess(String s) {
//                                panVerifyLabel.setText("Pending");
//                                panVerifyLabel.setStyle(pendingLabelStyle);
//                                panLabel.setText(Constant.userProfile.getPanNumber());
//                            }
//
//                            @Override
//                            public void onFail(String reason) {
//                                if (reason == null || reason.isEmpty()) {
//                                    ToastMessage.showCenter("Please try again later.");
//                                } else {
//                                    ToastMessage.showCenter(reason);
//                                }
//                            }
//
//                            @Override
//                            public void onError(String errorMessage) {
//                                errorLabel.setText(errorMessage);
//                            }
//                        }).show(getModelScreen().getStage());
//                    }
//                });
            }
            menuTable.add(panVerifyLabel).uniformX().row();
       }

        TextureRegion paytmTexture = atlas.findRegion("icon_paytm_square");
        iconHeight = iconWidth * paytmTexture.getRegionHeight() / paytmTexture.getRegionWidth();
        menuTable.add(new Image(paytmTexture)).width(iconWidth).height(iconHeight).padTop(5 * density).padLeft(iconWidth * 0.5f).padRight(iconWidth * 0.3f);
        final Label paytmLinkLabel = new Label("PAYTM", labelStyle);

        menuTable.add(paytmLinkLabel).padTop(5 * density).expandX().align(Align.left);

        final Label paytmVarifyLabel;
        if (Constant.userProfile.isPaytmAccountLinked()) {
            paytmLinkLabel.setText(Constant.userProfile.getPaytmLinkedNo());
            paytmVarifyLabel = new Label("Unlink", verifiedLabelStyle);
        } else {
            paytmVarifyLabel = new Label(" Link ", verifyLabelStyle);
        }
        paytmVarifyLabel.setTouchable(Touchable.enabled);
        paytmVarifyLabel.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (Constant.userProfile.isPaytmAccountLinked()) {
                    final ProcessDialog processDialog = new ProcessDialog(screen);
                    processDialog.show(getStage());
                    ApiHandler.callPaytmAutoDebitUnlinkApi(new GdxListener<String>() {
                        @Override
                        public void onSuccess(String s) {
                            processDialog.hide();
                            paytmVarifyLabel.setStyle(verifyLabelStyle);
                            paytmLinkLabel.setText("PAYTM");
                            paytmVarifyLabel.setText(" Link ");

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
                } else {
                    new PaytmNumberVerifiedDialog(screen, true, Constant.userProfile.getMsisdn(), new GdxListener<String>() {
                        @Override
                        public void onSuccess(String s) {
                            paytmVarifyLabel.setStyle(verifiedLabelStyle);
                            paytmLinkLabel.setText(Constant.userProfile.getPaytmLinkedNo());
                            paytmVarifyLabel.setText("Unlink");
                            screen.updateBalance();
                            if (Constant.userProfile.getAlertMessage() != null) {
                                //TODO
//                                new AlertDialog(getModelScreen(), Constant.userProfile.getAlertMessage()).show(getStage());
                                Constant.userProfile.setAlertMessage(null);
                            }
                            if (Constant.userProfile.getMsisdn() != null && Constant.userProfile.isMsisdnVerified()) {
                                phoneLabel.setText(Constant.userProfile.getMsisdn());
                                phoneVerifyLabel.setStyle(verifiedLabelStyle);
                                phoneVerifyLabel.setText("Verified");
                                phoneVerifyLabel.clearListeners();
                            }
                        }

                        @Override
                        public void onFail(String reason) {

                        }

                        @Override
                        public void onError(String errorMessage) {

                        }
                    }).show(getStage());
                }

            }
        });
        menuTable.add(paytmVarifyLabel).padTop(5 * density).uniformX().row();

        menuTable.add(errorLabel).width(width * 0.8f).expandY().colspan(3).row();


        TextButton likeButton = getTextButton("button_blue", "Like us on facebook");
        TextButton followButton = getTextButton("button_blue", "Follow us on Twitter");

        menuTable.add(likeButton).width(width * 0.7f).colspan(3).padBottom(height * 0.05f).row();
//        if (getProGame().config.isPackageExisted("com.twitter.android")) {
//            menuTable.add(followButton).width(width * 0.7f).colspan(3).padBottom(height * 0.05f);
//        }

        likeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
//                if (getProGame().config.isNetworkConnected())
//                    getProGame().config.openFacebookPageInApp();
//                else
//                    new NetworkErrorDialog(getModelScreen()).show(getStage());

            }
        });
        followButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
//                if (getProGame().config.isNetworkConnected())
//                    getProGame().config.followUsOnTwitter();
//                else
//                    new NetworkErrorDialog(getModelScreen()).show(getStage());

            }
        });
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


    private String getFirstName(String name) {
        String[] s = name.split(" ");
        return s[0];
    }

}
