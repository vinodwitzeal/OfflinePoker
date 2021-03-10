package bigcash.poker.dialogs;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

import java.util.ArrayList;
import java.util.List;

import bigcash.poker.font.FontPool;
import bigcash.poker.font.FontType;
import bigcash.poker.models.UIDialog;
import bigcash.poker.models.UIScreen;
import bigcash.poker.utils.AssetsLoader;
import bigcash.poker.utils.TextureDrawable;
import bigcash.poker.widgets.EqualDrawable;

public class MenuDialog extends UIDialog {
    private TextureAtlas splashAtlas;
    private int currentIndex;
    private List<MenuPage> pages;

    public MenuDialog(UIScreen screen) {
        super(screen);
        splashAtlas = AssetsLoader.instance().splashAtlas;
        buildDialog();
    }

    @Override
    public void init() {

    }

    @Override
    public void buildDialog() {
        this.pages = new ArrayList<MenuPage>();
        Stack mainStack = new Stack();
        MenuPage page1 = new MenuPage("https://pocket-syndicated-images.s3.amazonaws.com/5f4d569ebaa8d.jpg");
        MenuPage page2 = new MenuPage("https://pocket-syndicated-images.s3.amazonaws.com/5e9720121f721.jpg");
        MenuPage page3 = new MenuPage("https://pocket-syndicated-images.s3.amazonaws.com/5e9720121f721.jpg");
        MenuPage page4 = new MenuPage("https://pocket-syndicated-images.s3.amazonaws.com/5f4d569ebaa8d.jpg");
        this.currentIndex = 0;

        this.pages.add(page1);
        this.pages.add(page2);
        this.pages.add(page3);
        this.pages.add(page4);

        mainStack.addActor(page4);
        mainStack.addActor(page3);
        mainStack.addActor(page2);
        mainStack.addActor(page1);


        Label.LabelStyle pageStyle = new Label.LabelStyle();
        pageStyle.font = FontPool.obtain(FontType.ROBOTO_REGULAR, 4);
        pageStyle.fontColor = Color.WHITE;
        pageStyle.background = new EqualDrawable(splashAtlas.findRegion("menu_page"), 18);
        float labelHeight = pageStyle.font.getLineHeight() * 1.2f;

        Table controlTable = new Table();
        float tablePad = width * 0.01f;
        controlTable.pad(tablePad);

        TextureRegion crossTexture = splashAtlas.findRegion("menu_cross");
        float iconSize = labelHeight * 2;
        TextureRegionDrawable crossDrawable = TextureDrawable.getDrawable(crossTexture, iconSize, iconSize);
        Button crossButton = new Button(crossDrawable);
        controlTable.add(crossButton).width(iconSize).height(iconSize).expandX().align(Align.right);

        controlTable.row();

        Table centerTable = new Table();
        TextureRegion previousTexture = splashAtlas.findRegion("menu_previous");
        TextureRegionDrawable previousDrawable = TextureDrawable.getDrawable(previousTexture, iconSize, iconSize);
        Button previousButton = new Button(previousDrawable);
        centerTable.add(previousButton).width(iconSize).height(iconSize).expandX().align(Align.left);

        TextureRegion nextTexture = splashAtlas.findRegion("menu_next");
        TextureRegionDrawable nextDrawable = TextureDrawable.getDrawable(nextTexture, iconSize, iconSize);
        Button nextButton = new Button(nextDrawable);
        centerTable.add(nextButton).width(iconSize).height(iconSize).expandX().align(Align.right);
        controlTable.add(centerTable).expand().fillX();

        controlTable.row();

        Label pageLabel = new Label("1/3", pageStyle);
        controlTable.add(pageLabel).height(labelHeight).expandX().align(Align.right);

        mainStack.add(controlTable);

        Table zoomTable = new Table();
        zoomTable.pad(tablePad);
        zoomTable.bottom();
        TextureRegion zoomOutTexture = splashAtlas.findRegion("menu_zoomout");
        TextureRegionDrawable zoomOutDrawable = TextureDrawable.getDrawable(zoomOutTexture, iconSize, iconSize);
        Button zoomOutButton = new Button(zoomOutDrawable);
        zoomTable.add(zoomOutButton).width(iconSize).height(iconSize).padRight(labelHeight / 2f);

        TextureRegion zoomInTexture = splashAtlas.findRegion("menu_zoomin");
        TextureRegionDrawable zoomInDrawable = TextureDrawable.getDrawable(zoomInTexture, iconSize, iconSize);
        Button zoomInButton = new Button(zoomInDrawable);
        zoomTable.add(zoomInButton).width(iconSize).height(iconSize).padLeft(labelHeight / 2f);

        mainStack.add(zoomTable);

        getContentTable().add(mainStack).width(width).height(height);


        previousButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (currentIndex <= 0) return;
                int index = currentIndex - 1;
                MenuPage currentPage = pages.get(index);
                currentPage.setOrigin(0, 0);
                currentPage.addAction(Actions.scaleTo(1, 1, 0.2f));
                currentIndex = index;
                pageLabel.setText((currentIndex + 1) + "/" + pages.size());
                if (currentIndex<=0){
                    previousButton.setVisible(false);
                }
                if (currentIndex<pages.size()-1){
                    nextButton.setVisible(true);
                }
            }
        });

        nextButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (currentIndex >= (pages.size() - 1)) return;
                int index = currentIndex + 1;
                MenuPage previousPage = pages.get(currentIndex);
                previousPage.resetPage();
                previousPage.setOrigin(0, 0);
                previousPage.addAction(Actions.scaleTo(0, 1, 0.2f));
                MenuPage currentPage = pages.get(index);
                currentPage.resetPage();
                currentIndex = index;
                pageLabel.setText((currentIndex + 1) + "/" + pages.size());
                if (currentIndex>=(pages.size()-1)){
                    nextButton.setVisible(false);
                }
                if (currentIndex>0){
                    previousButton.setVisible(true);
                }
            }
        });

        zoomOutButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                MenuPage currentPage = pages.get(currentIndex);
                if (currentPage != null) {
                    currentPage.zoomOut();
                }
            }
        });

        zoomInButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                MenuPage currentPage = pages.get(currentIndex);
                if (currentPage != null) {
                    currentPage.zoomIn();
                }
            }
        });

        crossButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hide();
            }
        });

        previousButton.setVisible(false);
        if (pages.size()==1){
            nextButton.setVisible(false);
        }
    }


    private class MenuPage extends Image {
        private float pageScale;
        private float minX, minY, maxX, maxY;

        public MenuPage(String url) {
            super();
            screen.pokerGame.downloadImage(url, this);
            this.pageScale = 1.0f;
            setPosition(0, 0);
            setSize(width, height);
            setOrigin(width / 2f, height / 2f);
            addListener(new ActorGestureListener() {
                @Override
                public void zoom(InputEvent event, float initialDistance, float distance) {
//                    float scaledWidth = getWidth() * getScaleX();
//                    float targetWidth = scaledWidth + (distance - initialDistance);
//                    float targetScale = targetWidth / getWidth();
//                    if (targetScale > 2.0f) {
//                        targetScale = 2.0f;
//                    }
//                    setScale(targetScale);
                }

                @Override
                public void pinch(InputEvent event, Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
//                    calculateOrigin((initialPointer1.x + initialPointer2.x) / 2f, (initialPointer1.y + initialPointer2.y) / 2f);
//                    float initialDistance = initialPointer1.dst(initialPointer2);
//                    float distance = pointer1.dst(pointer2);
//                    float scaledWidth = getWidth() * getScaleX();
//                    float targetWidth = scaledWidth - (initialDistance - distance);
//                    float targetScale = targetWidth / getWidth();
//                    if (targetScale < 1.0f) {
//                        targetScale = 1.0f;
//                    }
//                    setScale(targetScale);
                }

                @Override
                public void pan(InputEvent event, float x, float y, float deltaX, float deltaY) {
                    calculateBounds();
                    float targetX = getX() + deltaX;
                    float targetY = getY() + deltaY;
                    targetX = MathUtils.clamp(targetX, minX, maxX);
                    targetY = MathUtils.clamp(targetY, minY, maxY);
                    setPosition(targetX, targetY);
                }
            });
        }

        private void calculateBounds() {
            float diffX=getScaleX()*getOriginX()-getOriginX();
            float diffY=getScaleY()*getOriginY()-getOriginY();
            minX=-diffX;
            minY=-diffY;
            maxX=diffX;
            maxY=diffY;
        }



        public void zoomIn() {
            this.pageScale = this.pageScale + 0.1f;
            if (this.pageScale > 2.0f) {
                this.pageScale = 2.0f;
            }
            setOrigin(width/2f,height/2f);
            setScale(pageScale);
        }


        public void zoomOut() {
            this.pageScale = this.pageScale - 0.1f;
            if (this.pageScale < 1.0f) {
                this.pageScale = 1.0f;
            }
            setOrigin(width/2f,height/2f);
            setScale(pageScale, pageScale);
            calculateBounds();
            float targetX = getX();
            float targetY = getY();
            targetX = MathUtils.clamp(targetX, minX, maxX);
            targetY = MathUtils.clamp(targetY, minY, maxY);
            setPosition(targetX, targetY);
        }

        public void resetPage() {
            this.pageScale = 1.0f;
            setOrigin(width / 2f, height / 2f);
            setPosition(0, 0);
            setSize(width, height);
            setScale(1.0f);
        }
    }
}
