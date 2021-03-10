package bigcash.poker.dialogs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import bigcash.poker.font.FontPool;
import bigcash.poker.font.FontType;
import bigcash.poker.models.UIDialog;
import bigcash.poker.models.UIScreen;
import bigcash.poker.widgets.ProcessView;

/**
 * Created by Vinod on 09-09-2017.
 */

public class ProcessDialog extends UIDialog {
    public Label messageLabel;
    private String message;

    public ProcessDialog(UIScreen screen) {
        super(screen);
        message = "";
        buildDialog();
    }

    public ProcessDialog(UIScreen screen, String message) {
        super(screen);
        this.message = message;
        buildDialog();
    }

    @Override
    public void init() {
    }

    @Override
    public void buildDialog() {
        Table contentTable = getContentTable();
        Table processTable = new Table();
        ProcessView processView = new ProcessView();
        float processSize = height>width?width * 0.16f:height*0.16f;
        processTable.add(processView).width(processSize).height(processSize).row();
        Label.LabelStyle messageLabelStyle = new Label.LabelStyle();
        messageLabelStyle.font = FontPool.obtain(FontType.ROBOTO_REGULAR,6);
        messageLabelStyle.fontColor = Color.LIGHT_GRAY;
        messageLabel = new Label(message, messageLabelStyle);
        processTable.add(messageLabel);
        contentTable.add(processTable);
    }

    public void showMessage(final String message) {
        changeMessage(message);
    }

    public void changeMessage(final String message) {
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                messageLabel.setText(message);
            }
        });
    }
}
