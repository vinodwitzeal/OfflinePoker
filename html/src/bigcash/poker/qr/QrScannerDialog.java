package bigcash.poker.qr;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.gwt.GwtGraphics;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import bigcash.poker.models.UIScreen;
import bigcash.poker.network.GdxListener;
import bigcash.poker.utils.PokerUtils;
import bigcash.poker.utils.TimeoutHandler;

public class QrScannerDialog extends PopupPanel {
    private QrScanner qrScanner;
    private int boxSize;
    private GdxListener<String> listener;
    private Label errorLabel;
    private Button cameraButton,fileButton;
    private boolean isScanningFromCamera;
    private FileUpload fileUpload;
    public QrScannerDialog(UIScreen screen, GdxListener<String> listener){
        super(true);
        setGlassEnabled(true);
        setStyleName("dialog-box");
        this.listener=listener;
        int screenWidth=(int) (screen.width/ GwtGraphics.getNativeScreenDensity())-20;
        int screenHeight=Window.getClientHeight();
        VerticalPanel verticalPanel=new VerticalPanel();
        verticalPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        Label titleLabel=new Label();
        titleLabel.setText("SCANNING");
        titleLabel.setStyleName("label-qr");
        verticalPanel.add(titleLabel);

        errorLabel=new Label();
        errorLabel.setStyleName("label-error");
        verticalPanel.add(errorLabel);




        HorizontalPanel horizontalPanel=new HorizontalPanel();
        horizontalPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
        horizontalPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        horizontalPanel.setWidth(screenWidth-20+"px");
        horizontalPanel.setHeight(screenWidth-20+"px");
        boxSize=(int)((screenWidth-20)*0.75f);
        horizontalPanel.getElement().setAttribute("id","qr-reader");
        verticalPanel.add(horizontalPanel);


        HorizontalPanel buttonPanel=new HorizontalPanel();
        cameraButton=new Button();
        cameraButton.setText("Scan with Camera");
        cameraButton.setStyleName("btn-scan");

        cameraButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (isScanningFromCamera){
                    stopScanning();
                    hide();
                }else {
                    startScanningFromCamera();
                }
            }
        });

        cameraButton.setVisible(false);

        fileUpload=new FileUpload();
        fileUpload.getElement().setAttribute("accept","image/*");
//        fileUpload.getElement().setAttribute("capture","");
        buttonPanel.add(fileUpload);
        fileUpload.setVisible(false);
        fileButton=new Button();
        fileButton.setText("Scan From File");
        fileButton.setStyleName("btn-submit");
        fileButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                stopScanning();
                fileUpload.click();
            }
        });

        fileUpload.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                startScanningFromFile(fileUpload.getFilename());
            }
        });

        buttonPanel.add(cameraButton);
        buttonPanel.add(fileButton);
        verticalPanel.add(buttonPanel);
        setWidget(verticalPanel);
        addAttachHandler(new AttachEvent.Handler() {
            @Override
            public void onAttachOrDetach(AttachEvent event) {
                if (event.isAttached()){
                    PokerUtils.setTimeOut(3000, new TimeoutHandler() {
                        @Override
                        public void onTimeOut() {
                            fileButton.setVisible(true);
                        }
                    });
                    setTopPosition();
                    startScanningFromCamera();
                }else {
                    stopScanning();
                }
            }
        });
    }

    private QrScanner getQrScanner(){
        if (qrScanner==null){
            qrScanner=QrScanner.newScanner("qr-reader");
        }
        return qrScanner;
    }

    private void stopScanning(){
        getQrScanner().stop();
        if (isScanningFromCamera) {
            isScanningFromCamera = false;
            cameraButton.setText("Scan with Camera");
            cameraButton.setStyleName("btn-scan");
        }
    }



    private void startScanningFromCamera(){
        getQrScanner().start(100, boxSize, new QrScanner.QrHandler() {
            @Override
            public void onSuccess() {
                isScanningFromCamera=true;
                cameraButton.setText("Stop Scanning");
                cameraButton.setStyleName("btn-stop");
                cameraButton.setVisible(true);
            }

            @Override
            public void onFail() {

            }

            @Override
            public void onMessage(String message) {
                try {
                    String messages[]=message.split("qr=");
                    if (messages.length==2) {
                        listener.setSuccess(messages[1]);
                        qrScanner.stop();
                        qrScanner=null;
                        hide();
                    }else {
                        errorLabel.setText("Invalid QR");
                    }
                }catch (Exception e){

                }

            }

            @Override
            public void onError(String message) {
                errorLabel.setText("QR Not Found");
            }
        });
    }


    private void startScanningFromFile(String fileName){
        getQrScanner().stop();
        getQrScanner().scanFile(fileName, new QrScanner.QrHandler() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFail() {

            }

            @Override
            public void onMessage(String message) {
                try {
                    String messages[]=message.split("qr=");
                    if (messages.length==2) {
                        listener.setSuccess(messages[1]);
                        qrScanner.stop();
                        qrScanner=null;
                        hide();
                    }else {
                        errorLabel.setText("Invalid QR");
                    }
                }catch (Exception e){

                }

            }

            @Override
            public void onError(String message) {
                errorLabel.setText("QR Not Found");
            }
        });
    }

    private void setTopPosition(){
        int left = (Window.getClientWidth() - getOffsetWidth()) >> 1;
        int top = 0;
        setPopupPosition(Math.max(Window.getScrollLeft() + left, 0), Math.max(
                Window.getScrollTop() + top, 0));
    }

    @Override
    public void show() {
        super.show();
        setTopPosition();
    }
}
