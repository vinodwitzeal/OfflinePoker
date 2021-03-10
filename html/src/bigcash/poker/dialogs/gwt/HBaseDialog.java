package bigcash.poker.dialogs.gwt;

import com.badlogic.gdx.backends.gwt.GwtGraphics;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.PopupPanel;

import bigcash.poker.models.UIScreen;

public abstract class HBaseDialog extends PopupPanel {
    public int width,height;
    public HBaseDialog(UIScreen screen){
        super(true);
        setModal(true);
        setGlassEnabled(true);
        width=(int)(screen.width/ GwtGraphics.getNativeScreenDensity());
        height=(int)(screen.height/ GwtGraphics.getNativeScreenDensity());
        addAttachHandler(new AttachEvent.Handler() {
            @Override
            public void onAttachOrDetach(AttachEvent event) {
                if (event.isAttached()){
                    setToCenter();
                }
            }
        });
    }

    public abstract void build();

    public void setToCenter(){
        int left = (Window.getClientWidth() - getOffsetWidth()) >> 1;
        int top = (Window.getClientHeight() - getOffsetHeight()) >> 1;
        setPopupPosition(Math.max(Window.getScrollLeft() + left, 0), Math.max(
                Window.getScrollTop() + top, 0));
    }


    public int getPixelWidth(float percent){
        return (int)(width*percent);
    }

    public int getPixelHeight(float percent){
        return (int)(height*percent);
    }

    public int getPixelDimension(int dimension,float percent){
        return (int)(dimension*percent);
    }



}
