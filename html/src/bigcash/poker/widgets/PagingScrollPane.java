package bigcash.poker.widgets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import bigcash.poker.models.UIScreen;

public class PagingScrollPane extends ScrollPane {
    public Table contentTable;
    public Table processTable;
    public Table dataTable;
    public Vector2 dataPosition, scrollPanePosition,processPosition;
    private boolean vertical;
    private boolean showingProcess;
    private ProcessView processView;
    private float processSize;
    private UIScreen screen;

    public PagingScrollPane(UIScreen screen, Table contentTable, boolean vertical) {
        super(contentTable);
        this.screen = screen;
        this.vertical = vertical;
        this.contentTable = contentTable;
        this.dataTable = new Table();
        this.processTable = new Table();
        setScrollingDisabled(vertical, !vertical);

        if (vertical) {
            contentTable.add(dataTable).row();
            contentTable.add(processTable);
        } else {
            contentTable.add(dataTable);
            contentTable.add(processTable);
        }
        dataPosition = new Vector2();
        scrollPanePosition = new Vector2();
        processPosition=new Vector2();
        processView = new ProcessView();
        if (vertical) {
            processSize = screen.width * 0.15f;
        } else {
            processSize = screen.height * 0.15f;
        }
    }

    public Table getDataTable(){
        return this.dataTable;
    }

    private void showProcess() {
        processTable.add(processView).width(processSize).height(processSize);
        showingProcess = true;
    }

    public void hideProcess() {
        processTable.clear();
        showingProcess = false;
    }


    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        if (!showingProcess) {
            scrollPanePosition.set(Vector2.Zero);
            dataPosition.set(Vector2.Zero);
            processPosition.set(Vector2.Zero);
            localToStageCoordinates(scrollPanePosition);
            dataTable.localToStageCoordinates(dataPosition);
            processTable.localToStageCoordinates(processPosition);
            if (vertical) {
                if (dataPosition.y >= scrollPanePosition.y) {
                    Gdx.app.postRunnable(new Runnable() {
                        @Override
                        public void run() {
                            showProcess();
                            onEndReached();
                        }
                    });
                }
            }else {
                if (processPosition.x<=(scrollPanePosition.x+getWidth())){
                    Gdx.app.postRunnable(new Runnable() {
                        @Override
                        public void run() {
                            showProcess();
                            onEndReached();
                        }
                    });
                }
            }
        }
    }

    public void onEndReached() {

    }
}
