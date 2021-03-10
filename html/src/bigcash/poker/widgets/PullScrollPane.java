package bigcash.poker.widgets;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;

import bigcash.poker.utils.AssetsLoader;

public abstract class PullScrollPane extends ScrollPane {
    private final int REST = 0, DRAW_START = 1, DRAW_END = 2;
    private Rectangle backgroundBounds, backgroundScissorBounds;
    private float maxOverScroll;
    private TextureRegion processTexture;
    private float processSize, processX, processY, processAngle;
    private float boundsHeight;
    private int state;
    private float previousY, currentY;
    private Rectangle widgetBounds;
    private boolean rotate;


    public PullScrollPane(Actor widget,ScrollPaneStyle scrollPaneStyle, float processSize) {
        super(widget, scrollPaneStyle);
        processTexture = AssetsLoader.instance().uiAtlas.findRegion("icon_refresh");
        this.processSize=processSize;
        init();
    }



    private void init() {
        backgroundBounds = new Rectangle();
        backgroundScissorBounds = new Rectangle();
        widgetBounds = new Rectangle();
        setOverscroll(false, true);
        setupOverscroll(4, 500, 500);
        maxOverScroll = processSize * 5;
        state = REST;
        previousY = 0;
        currentY = 0;
    }


    private boolean checkPullTime() {
        return onPullRefresh();
    }


//    @Override
//    public void draw(Batch batch, float parentAlpha) {
//        if (state == DRAW_START || state == DRAW_END) {
//            Color batchColor = batch.getColor();
//            super.draw(batch, 0.95f);
//            batch.setColor(batchColor);
//        } else {
//            super.draw(batch, parentAlpha);
//        }
//        widgetBounds.set(getX(), getY(), getWidth(), getHeight());
//        if (getScrollY() == -getOverscrollDistance()) {
//            if (state == REST) {
//                rotate = false;
//                processAngle = 0;
//                state = DRAW_START;
//            }
//        }
//
//        if (Gdx.input.isTouched()) {
//            float touchX = Gdx.input.getX();
//            float touchY = Gdx.graphics.getHeight() - Gdx.input.getY();
//            if (widgetBounds.contains(touchX, touchY)) {
//                currentY = touchY;
//                if (previousY == 0) {
//                    previousY = currentY;
//                }
//                if (currentY < previousY) {
//                    boundsHeight += previousY - currentY;
//                } else {
//                    float drag = currentY - previousY;
//                    if (drag < boundsHeight) {
//                        boundsHeight -= drag;
//                    } else {
//                        boundsHeight = 0;
//                    }
//                }
//                previousY = currentY;
//
//                if (boundsHeight >= maxOverScroll) {
//                    boundsHeight = maxOverScroll;
//                    if (state == DRAW_START) {
//                        state = DRAW_END;
//                        rotate = true;
//                    }
//                }
//
//                if (boundsHeight <= 0) {
//                    boundsHeight = 0;
//                    state = REST;
//                }
//            }
//        } else {
//            if (state == DRAW_START || state == DRAW_END) {
//                if (state == DRAW_END) {
//                    if (checkPullTime()) {
//                        state = REST;
//                        boundsHeight = 0;
//                    } else {
//                        state = DRAW_START;
//                        rotate = true;
//                    }
//                }
//                boundsHeight -= getHeight() * Gdx.graphics.getDeltaTime();
//                if (boundsHeight <= 0) {
//                    boundsHeight = 0;
//                    rotate = false;
//                    state = REST;
//                }
//            }
//        }
//
//        if (state == DRAW_START || state == DRAW_END) {
//            backgroundBounds.set(getX(), getY() + getHeight() - boundsHeight, getWidth(), boundsHeight);
//            layoutProcess();
//            getStage().calculateScissors(backgroundBounds, backgroundScissorBounds);
//            batch.flush();
//            if (ScissorStack.pushScissors(backgroundScissorBounds)) {
//                if (rotate) {
//                    processAngle -= 360 * Gdx.graphics.getDeltaTime();
//                }
//                batch.draw(processTexture, processX, processY, processSize / 2f, processSize / 2f, processSize, processSize, 1, 1, processAngle);
//                batch.flush();
//                ScissorStack.popScissors();
//            }
//        }
//    }

    private void layoutProcess() {
        processX = backgroundBounds.x + backgroundBounds.width * 0.5f - processSize * 0.5f;
        processY = backgroundBounds.y;
        float minProcessY = getY() + getHeight() - maxOverScroll * 0.5f - processSize * 0.5f;
        if (processY < minProcessY) {
            processY = minProcessY;
        }
    }

    public abstract boolean onPullRefresh();
}
