package bigcash.poker.dialogs;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

import bigcash.poker.models.UIDialog;
import bigcash.poker.models.UIScreen;
import bigcash.poker.widgets.Emoji;

public class EmojiDialog extends UIDialog {
    public Table dataTable, emojiTable;
    private Button closeButton;
    public Image arrow;
    private TextureRegion emojiBGTexture, closeTexture, arrowTexture;
    private NinePatchDrawable dialogBackground;
    private TextureRegionDrawable emojiBackground;
    public float emojiSize;
    private float emojiBoxWidth, emojiBoxHeight;
    public float tableWidth, tableHeight;
    private float crossWidth, crossHeight, arrowWidth, arrowHeight, bottomPad;
    private int emojiCount;
    private String receiver;

    public EmojiDialog(UIScreen screen, NinePatchDrawable dialogBackground, TextureRegion emojiBGTexture, TextureRegion closeTexture, TextureRegion arrowTexture) {
        super(screen);
        this.dialogBackground = dialogBackground;
        this.emojiBGTexture = emojiBGTexture;
        this.emojiBackground = new TextureRegionDrawable(emojiBGTexture);
        this.closeTexture = closeTexture;
        this.arrowTexture = arrowTexture;
        buildDialog();
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    @Override
    public void init() {

    }

    @Override
    public void buildDialog() {
        tableWidth = width * 0.4f;
        tableHeight = height * 0.6f;
        emojiBoxWidth = tableWidth / 4f;
        emojiBoxHeight = emojiBoxWidth * emojiBGTexture.getRegionHeight() / emojiBGTexture.getRegionWidth();
        emojiSize = emojiBoxWidth * 0.8f;

        dataTable = new Table() {
            @Override
            protected void drawBackground(Batch batch, float parentAlpha, float x, float y) {
                Color color = getColor();
                batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
                dialogBackground.draw(batch, x, y, getWidth(), getHeight());
            }
        };


        emojiTable = new Table();
        emojiTable.top();
        ScrollPane scrollPane = new ScrollPane(emojiTable);
        scrollPane.setScrollingDisabled(true, false);
        dataTable.add(scrollPane).expand().fill();

        crossWidth = emojiSize * 0.5f;
        crossHeight = crossWidth * closeTexture.getRegionHeight() / closeTexture.getRegionWidth();
        bottomPad = crossWidth / 3f;
        dataTable.pad(bottomPad);

        arrowWidth = crossWidth / 2f;
        arrowHeight = arrowWidth * arrowTexture.getRegionHeight() / arrowTexture.getRegionWidth();

        TextureRegionDrawable closeDrawable = new TextureRegionDrawable(closeTexture);
        closeDrawable.setMinSize(crossWidth, crossHeight);

        closeButton = new Button(closeDrawable);
        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hide();
            }
        });
        closeButton.setSize(crossWidth, crossHeight);
        dataTable.setSize(tableWidth, tableHeight);

        this.arrow = new Image(arrowTexture);
        this.arrow.setSize(arrowWidth, arrowHeight);
        this.arrow.setOrigin(arrowWidth / 2f, arrowHeight / 2f);

        getContentTable().addActor(dataTable);
        getContentTable().addActor(arrow);
        getContentTable().addActor(closeButton);
        emojiCount = 0;
    }


    public Emoji addEmoji(String name, Emoji.EmojiErrorListener emojiErrorListener) {
        Emoji emoji = new Emoji(name);
        emoji.play = true;
        Table emojiBox = new Table();
        emojiBox.setBackground(emojiBackground);
        emojiBox.add(emoji).width(emojiSize).height(emojiSize);
        emojiTable.add(emojiBox).width(emojiBoxWidth).height(emojiBoxHeight).padBottom(bottomPad).expandX();
        emojiCount++;
        if (emojiCount % 3 == 0) {
            emojiTable.row();
        }
        emoji.setEmojiErrorListener(emojiErrorListener);
        return emoji;
    }

    public void showDialog(float pointerX, float pointerY) {
        float closeX = dataTable.getX() + tableWidth - crossWidth / 2f;
        float closeY = dataTable.getY() + tableHeight - crossHeight / 2f;
        closeButton.setPosition(closeX, closeY);
        show(screen.stage);
    }


    public void show(float pointerX, float pointerY, int align) {
        float dialogX, dialogY;
        dialogY = (height - tableHeight) / 2f;
        float maxY = dialogY + tableHeight;
        if (pointerY < dialogY) {
            dialogY = pointerY;
        }

        if (pointerY > maxY) {
            dialogY = dialogY + (pointerY - maxY);
        }

        if (align == Align.right) {
            dialogX = pointerX + arrowWidth;
            arrow.setRotation(0);
            arrow.setPosition(pointerX, pointerY);
        } else if (align == Align.left) {
            dialogX = pointerX - arrowWidth - tableWidth;
            arrow.setRotation(180);
            arrow.setPosition(pointerX - arrowWidth, pointerY);
        } else if (align == Align.top) {
            arrow.setRotation(270);
            arrow.setPosition(pointerX - arrowWidth / 2f, pointerY - arrowHeight / 2f);
            dialogY = arrow.getY() - tableHeight;
            dialogX = arrow.getX() - tableWidth + 2 * arrowWidth;
        } else {
            dialogX = (width - tableWidth) / 2f;
        }
        float closeX, closeY;
        closeX = dialogX + tableWidth - crossWidth / 2f;
        closeY = dialogY + tableHeight - crossHeight / 2f;
        closeButton.setPosition(closeX, closeY);
        dataTable.setPosition(dialogX, dialogY);
        show(screen.stage);
    }
}
