package bigcash.poker.dialogs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.TimeUtils;
import com.google.gwt.user.client.ui.FileUpload;

import bigcash.poker.constants.Constant;
import bigcash.poker.font.Font;
import bigcash.poker.font.FontPool;
import bigcash.poker.font.FontType;
import bigcash.poker.models.UIScreen;
import bigcash.poker.network.ApiHandler;
import bigcash.poker.network.GdxListener;
import bigcash.poker.network.HFormData;
import bigcash.poker.utils.DrawableBuilder;
import bigcash.poker.utils.GamePreferences;
import bigcash.poker.utils.PokerUtils;
import bigcash.poker.widgets.HtmlTextField;

public class KYCDialog extends IDialog {
    private FileUpload panUpload;
    private float errorWidth;
    private Label errorLabel;
    private Table errorTable;
    private GdxListener<String> listener;

    private  final String NAME_ERROR_MESSAGE = "Please enter valid Name.";
    private  final String DOB_ERROR_MESSAGE = "Please enter valid DOB(DD/MM/YYYY).";
    private  final String PAN_NUMBER_ERROR_MESSAGE = "Please enter valid Pan Number.";
    private  final String PAN_ID_ERROR_MESSAGE = "Please Upload Pan Card.";
    private  final String GOV_ID_ERROR_MESSAGE = "Please upload Government Id";


    private boolean isNameChanged, isDobChanged, isPanNoChanged;
    public KYCDialog(UIScreen screen, GdxListener<String> listener) {
        super(screen);
        this.listener = listener;
        buildDialog();
    }

    @Override
    public void init() {

    }

    @Override
    public void buildDialog() {
        Table contentTable = getContentTable();
        contentTable.top();
        Table headerTable = new Table();
        headerTable.left();

        Label.LabelStyle headerLabelStyle = new Label.LabelStyle();
        headerLabelStyle.font = FontPool.obtain(FontType.ROBOTO_BOLD, 7);
        headerLabelStyle.fontColor = Color.WHITE;
        headerTable.pad(0, density * 4, 0, density * 4);
        float headerHeight=headerLabelStyle.font.getLineHeight()*3.2f;
        TextureRegion bgHeaderTexture = uiAtlas.findRegion("bg_header_bar");
        final TextureRegionDrawable background = new TextureRegionDrawable(bgHeaderTexture);
        background.setMinHeight(10);
        background.setMinWidth(10);
        headerTable.setBackground(background);

        TextureRegion itemBGTexture = uiAtlas.findRegion("bg_items");
        NinePatchDrawable boxDrawable = new NinePatchDrawable(new NinePatch(itemBGTexture, 14, 14, 16, 15));
        boxDrawable.setMinHeight(headerHeight * 0.8f);
        boxDrawable.setMinWidth(50);

        TextureRegion backTexture = uiAtlas.findRegion("btn_wback");
        float backHeight = headerHeight/2f;
        float backWidth = backHeight * backTexture.getRegionWidth() / backTexture.getRegionHeight();
        TextureRegionDrawable backDrawable = DrawableBuilder.getDrawable(backTexture, backWidth, backHeight);
        ImageButton backButton = new ImageButton(backDrawable);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hide();
            }
        });

        headerTable.add(backButton).width(backWidth).height(backHeight).padRight(8*density);
        headerTable.add(new Label("KYC Details",headerLabelStyle)).expandX().align(Align.left);

        contentTable.add(headerTable).height(headerHeight).width(width).row();
        NinePatchDrawable whiteDrawable=new NinePatchDrawable(new NinePatch(uiAtlas.findRegion("img_dot"),1,1,1,1));

        Table detailsTable=new Table();
        detailsTable.setBackground(whiteDrawable);
        detailsTable.top();
        detailsTable.padTop(headerHeight);

        float columnWidth=width*0.85f;


        Font textFieldFont=FontPool.obtain(FontType.ROBOTO_BOLD,6);
        Label.LabelStyle labelStyle=new Label.LabelStyle();
        labelStyle.font=FontPool.obtain(FontType.ROBOTO_BOLD,5);
        labelStyle.fontColor= Color.BLACK;

        NinePatchDrawable cursorDrawable=new NinePatchDrawable(new NinePatch(uiAtlas.findRegion("img_cursor"),1,1,1,1));
        cursorDrawable.setMinSize(2*density,textFieldFont.getLineHeight());
        TextField.TextFieldStyle textFieldStyle = new TextField.TextFieldStyle();
        textFieldStyle.font = textFieldFont;
        textFieldStyle.fontColor = Color.GRAY;
        textFieldStyle.messageFontColor = Color.LIGHT_GRAY;
        textFieldStyle.cursor = cursorDrawable;

        Label.LabelStyle detailStyle=new Label.LabelStyle();
        detailStyle.font=textFieldFont;
        detailStyle.fontColor=Color.GRAY;
        NinePatchDrawable textFieldBackground=new NinePatchDrawable(new NinePatch(uiAtlas.findRegion("bg_gtextfield"),7,7,7,7));

        float iconHeight=textFieldFont.getLineHeight();
        float iconWidth;
        TextureRegion iconTexture;
        float fieldPad=8*density;

        Table nameTable=new Table();
        nameTable.setBackground(textFieldBackground);
        iconTexture=uiAtlas.findRegion("icon_user");
        iconWidth=iconHeight*iconTexture.getRegionWidth()/iconTexture.getRegionHeight();
        nameTable.add(new Image(iconTexture)).width(iconWidth).height(iconHeight).pad(fieldPad);
        Image nameLine=new Image(whiteDrawable);
        nameLine.setColor(Color.GRAY);
        nameTable.add(nameLine).width(2*density).height(iconHeight).pad(fieldPad);
        final TextField nameTextField = new TextField("", textFieldStyle);
        nameTextField.setMessageText("Enter Here");
        if(Constant.userProfile.getFullName()!= null && !Constant.userProfile.getFullName().isEmpty()){
            nameTextField.setText(Constant.userProfile.getFullName());
            nameTextField.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if(!isNameChanged){
                        isNameChanged=true;
                        nameTextField.setText("");
                    }
                }
            });
        }
        HtmlTextField.setTextInput(nameTextField,"Enter Your Name",HtmlTextField.TEXT);
        nameTable.add(nameTextField).expandX().fillX().align(Align.left);
        final Label nameLabel=new Label("Enter Your Full Name:",labelStyle);
        nameLabel.setAlignment(Align.left);
        detailsTable.add(nameLabel).width(columnWidth).row();
        detailsTable.add(nameTable).width(columnWidth).padBottom(fieldPad).row();

        Table dobTable=new Table();
        dobTable.setBackground(textFieldBackground);
        iconTexture=uiAtlas.findRegion("icon_calender");
        iconWidth=iconHeight*iconTexture.getRegionWidth()/iconTexture.getRegionHeight();
        dobTable.add(new Image(iconTexture)).width(iconWidth).height(iconHeight).pad(fieldPad);
        Image dobLine=new Image(whiteDrawable);

        dobLine.setColor(Color.GRAY);
        dobTable.add(dobLine).width(2*density).height(iconHeight).pad(fieldPad);
        final TextField dobTextField = new TextField("", textFieldStyle);
        dobTextField.setMessageText("DD/MM/YYYY");
        if(Constant.userProfile.getDob()!= null && !Constant.userProfile.getDob().isEmpty()){
            dobTextField.setText(Constant.userProfile.getDob());
            dobTextField.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if(!isDobChanged){
                        isDobChanged=true;
                        dobTextField.setText("");
                    }
                }
            });
        }
        HtmlTextField.setTextInput(dobTextField,"Enter DOB",HtmlTextField.TEXT);
        dobTable.add(dobTextField).expandX().fillX().align(Align.left);
        Label dobLabel=new Label("Enter DOB:",labelStyle);
        dobLabel.setAlignment(Align.left);
        detailsTable.add(dobLabel).width(columnWidth).row();
        detailsTable.add(dobTable).width(columnWidth).padBottom(fieldPad).row();

        Table panNoTable=new Table();
        panNoTable.setBackground(textFieldBackground);
        iconTexture=uiAtlas.findRegion("icon_id");
        iconWidth=iconHeight*iconTexture.getRegionWidth()/iconTexture.getRegionHeight();
        panNoTable.add(new Image(iconTexture)).width(iconWidth).height(iconHeight).pad(fieldPad);
        Image panNoLine=new Image(whiteDrawable);
        panNoLine.setColor(Color.GRAY);
        panNoTable.add(panNoLine).width(2*density).height(iconHeight).pad(fieldPad);
        final TextField panTextField = new TextField("", textFieldStyle);
        panTextField.setMessageText("Enter PAN No.");
        if(Constant.userProfile.getPanNumberConfig()!= null && !Constant.userProfile.getPanNumberConfig().isEmpty()){
            panTextField.setText(Constant.userProfile.getPanNumberConfig());
            panTextField.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if(!isPanNoChanged){
                        isPanNoChanged=true;
                        panTextField.setText("");
                    }
                }
            });
        }
        HtmlTextField.setTextInput(panTextField,"Enter PAN No.",HtmlTextField.TEXT);
        panNoTable.add(panTextField).expandX().fillX().align(Align.left);
        Label panNoLabel=new Label("Enter PAN No.:",labelStyle);
        panNoLabel.setAlignment(Align.left);
        detailsTable.add(panNoLabel).width(columnWidth).row();
        detailsTable.add(panNoTable).width(columnWidth).padBottom(fieldPad).row();

        Table panTable=new Table();
        panTable.setBackground(textFieldBackground);
        iconTexture=uiAtlas.findRegion("icon_id");
        iconWidth=iconHeight*iconTexture.getRegionWidth()/iconTexture.getRegionHeight();
        panTable.add(new Image(iconTexture)).width(iconWidth).height(iconHeight).pad(fieldPad);
        Image panLine=new Image(whiteDrawable);
        panLine.setColor(Color.GRAY);
        panTable.add(panLine).width(2*density).height(iconHeight).pad(fieldPad);
        final Label panDetailLabel = new Label("No File Chosen", detailStyle);
        panTable.add(panDetailLabel).expandX().fillX().align(Align.left);

        iconTexture=uiAtlas.findRegion("icon_camera");
        iconWidth=iconHeight*iconTexture.getRegionWidth()/iconTexture.getRegionHeight();
        panTable.add(new Image(iconTexture)).width(iconWidth).height(iconHeight).pad(fieldPad);

        Label panLabel=new Label("Attach PAN Card:",labelStyle);
        panLabel.setAlignment(Align.left);
        detailsTable.add(panLabel).width(columnWidth).row();
        detailsTable.add(panTable).width(columnWidth).padBottom(fieldPad).row();

        panTable.setTouchable(Touchable.enabled);
        panTable.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                new PanUploadDialog(screen, new GdxListener<FileUpload>() {
                    @Override
                    public void onSuccess(FileUpload fileUpload) {
                        panUpload=fileUpload;
                        panDetailLabel.setText("Successfully Chosen");
                    }

                    @Override
                    public void onFail(String reason) {

                    }

                    @Override
                    public void onError(String error) {

                    }
                }).center();

            }
        });


        final Label.LabelStyle mobileMessageStyle = new Label.LabelStyle();
        mobileMessageStyle.font = FontPool.obtain(FontType.ROBOTO_BOLD, 5);
        mobileMessageStyle.fontColor = Color.RED;

        errorWidth=columnWidth;
        errorTable=new Table();
        errorLabel = new Label("", mobileMessageStyle);
        errorLabel.setWrap(true);
        errorLabel.setAlignment(Align.center);

        detailsTable.add(errorTable).width(columnWidth).padBottom(fieldPad).row();


        TextButton.TextButtonStyle buttonStyle=new TextButton.TextButtonStyle();
        buttonStyle.font=FontPool.obtain(FontType.ROBOTO_BOLD,7);
        buttonStyle.fontColor= Color.WHITE;
        buttonStyle.up=greenDrawable;
        buttonStyle.down=greenDrawable;
        buttonStyle.checked=greenDrawable;

        TextButton submitButton=new TextButton("Submit",buttonStyle);
        float buttonHeight=buttonStyle.font.getCapHeight()*3;
        float buttonPad=buttonStyle.font.getLineHeight();

        submitButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(!isValidName(nameTextField.getText())){
                    setError(NAME_ERROR_MESSAGE);
                    return;
                }
                if(!PokerUtils.isValidDOB(dobTextField.getText())){
                    setError(DOB_ERROR_MESSAGE);
                    return;
                }
                if(!PokerUtils.isValidPanNumber(panTextField.getText(),"")){
                    setError(PAN_NUMBER_ERROR_MESSAGE);
                    return;
                }
                if(panUpload== null|| panUpload.getFilename()==null || panUpload.getFilename().isEmpty()){
                    setError(PAN_ID_ERROR_MESSAGE);
                    return;
                }
                callKycSubmitApi(nameTextField.getText(),nameTextField.getText(),panTextField.getText(),dobTextField.getText());
            }
        });

        detailsTable.add(submitButton).width(columnWidth).height(buttonHeight).padTop(buttonPad).padBottom(buttonPad);

        contentTable.add(detailsTable).width(width).expandY().fillY();
    }

    private boolean isValidName(String name){
        if(name == null || name.isEmpty() || !PokerUtils.isValidUserName(name)){
            return false;
        }
        return true;
    }


    private void callKycSubmitApi(String firstName,String lastName, String panNumber,String dob){
        final ProcessDialog processDialog = new ProcessDialog(screen);
        processDialog.show(getStage());
        HFormData formData=HFormData.newFormData();
        formData.append("userId",Constant.userProfile.getUserId());
        formData.append("name",firstName);
        formData.append("panNumber",panNumber);
        formData.append("dob",dob);
        String transactionId=TimeUtils.millis()+"";
        formData.append("otp",PokerUtils.encryptValue(transactionId, GamePreferences.instance().getOtp()));
        formData.append("transactionId",transactionId);
        formData.appendFileElement("panFile",panUpload);
        HFormData.sendFormData(ApiHandler.BASE_URL + ApiHandler.API_VERSION_2 + ApiHandler.UPLOAD_FILE_API, formData, new HFormData.HFormListener() {
            @Override
            public void onSuccess(String response) {
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        processDialog.hide();
                        hide();
                        listener.setSuccess(response);
                    }
                });
            }

            @Override
            public void onError(String error) {
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        processDialog.hide();
                        KYCDialog.this.setError(error);
                    }
                });

            }
        });
    }

    private void setError(final String messageText) {
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                errorTable.clear();
                errorLabel.setText(messageText);
                errorTable.add(errorLabel).width(errorWidth);
            }
        });
    }
}
