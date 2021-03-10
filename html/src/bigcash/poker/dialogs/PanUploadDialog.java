package bigcash.poker.dialogs;

import com.badlogic.gdx.backends.gwt.GwtGraphics;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import bigcash.poker.models.UIScreen;
import bigcash.poker.network.GdxListener;

public class PanUploadDialog extends PopupPanel {
    public PanUploadDialog(UIScreen screen, GdxListener<FileUpload> listener) {
        super(true);
        setGlassEnabled(true);
        setModal(true);
        int dialogWidth = (int) (0.9f * screen.width / GwtGraphics.getNativeScreenDensity());
        setWidth(dialogWidth + "px");
        setStyleName("file-top");

        VerticalPanel mainPanel = new VerticalPanel();
        mainPanel.setWidth("100%");
        mainPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        Label titleLabel = new Label();
        titleLabel.setText("Attach Pan Card");
        titleLabel.setStyleName("file-title");
        mainPanel.add(titleLabel);
        Image image = new Image("img/pan.png");
        image.addLoadHandler(new LoadHandler() {
            @Override
            public void onLoad(LoadEvent event) {
                int left = (Window.getClientWidth() - getOffsetWidth()) >> 1;
                int top = (Window.getClientHeight() - getOffsetHeight()) >> 1;
                setPopupPosition(Math.max(Window.getScrollLeft() + left, 0), Math.max(
                        Window.getScrollTop() + top, 0));
            }
        });
        image.setStyleName("file-image");
        image.setWidth("90%");
        mainPanel.add(image);

        Label messageLabel = new Label();
        messageLabel.setStyleName("file-message");
        messageLabel.setText("Image should be clear. Blurred image would not be accepted.");
        mainPanel.add(messageLabel);

        VerticalPanel buttonPanel = new VerticalPanel();
        buttonPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        buttonPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
        buttonPanel.setStyleName("file-bottom");
        buttonPanel.setWidth("100%");

        FileUpload galleryUpload = new FileUpload();
        galleryUpload.getElement().setAttribute("accept", "image/*");
        HorizontalPanel galleryButton = getButton("Upload from Gallery", "img/gallery.png");

        FileUpload cameraUpload = new FileUpload();
        cameraUpload.getElement().setAttribute("accept", "image/*");
        cameraUpload.getElement().setAttribute("capture", "true");
        cameraUpload.setVisible(false);
        HorizontalPanel cameraButton = getButton("Upload from Camera", "img/camera.png");

        buttonPanel.add(galleryUpload);
        galleryUpload.setVisible(false);
        buttonPanel.add(galleryButton);
        buttonPanel.add(cameraUpload);
        buttonPanel.add(cameraButton);

        galleryButton.addDomHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                galleryUpload.click();
            }
        }, ClickEvent.getType());

        cameraButton.addDomHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                cameraUpload.click();
            }
        }, ClickEvent.getType());

        galleryUpload.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                if (galleryUpload.getFilename() != null && !galleryUpload.getFilename().isEmpty()) {
                    listener.setSuccess(galleryUpload);
                    hide();
                }
            }
        });

        cameraUpload.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                if (cameraUpload.getFilename() != null && !cameraUpload.getFilename().isEmpty()) {
                    listener.setSuccess(cameraUpload);
                    hide();
                }
            }
        });

        mainPanel.add(buttonPanel);


        setWidget(mainPanel);


        addAttachHandler(new AttachEvent.Handler() {
            @Override
            public void onAttachOrDetach(AttachEvent event) {
                if (event.isAttached()) {
                    int left = (Window.getClientWidth() - getOffsetWidth()) >> 1;
                    int top = (Window.getClientHeight() - getOffsetHeight()) >> 1;
                    setPopupPosition(Math.max(Window.getScrollLeft() + left, 0), Math.max(
                            Window.getScrollTop() + top, 0));
                }
            }
        });
    }


    private HorizontalPanel getButton(String text, String iconUrl) {
        HorizontalPanel button = new HorizontalPanel();
        button.setStyleName("file-button");
        button.setWidth("70%");
        button.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
        Image iconImage = new Image(iconUrl);
        iconImage.setHeight("20px");
        button.add(iconImage);
        Label label = new Label();
        label.setStyleName("file-button-text");
        label.setText(text);
        button.add(label);
        return button;
    }
}
