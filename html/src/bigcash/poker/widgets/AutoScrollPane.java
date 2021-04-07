package bigcash.poker.widgets;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class AutoScrollPane extends ScrollPane {
    private final float scrollTime = 3.0f;
    private float currentTime;
    private Table scrollTable;
    private boolean autoScroll;

    public AutoScrollPane(Table widget) {
        super(widget);
        this.autoScroll=true;
        this.scrollTable = widget;
        currentTime = 0;
        addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                autoScroll=false;
                return super.touchDown(event, x, y, pointer, button);
            }
        });
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (autoScroll) {
            currentTime += delta;
            if (currentTime >= scrollTime) {
                if (!isFlinging() && !isDragging() && !isPanning()) {
                    float scrollX = getScrollX();
                    float totalWidth = scrollTable.getWidth();
                    int childrens = scrollTable.getChildren().size;
                    float stepScroll = totalWidth / childrens;
                    float targetScroll = scrollX + stepScroll;
                    if (targetScroll >= getMaxX() + stepScroll) {
                        scrollX(0);
                    } else {
                        scrollX(targetScroll);
                    }
                }
                currentTime = 0;
            }
        }
    }
}
