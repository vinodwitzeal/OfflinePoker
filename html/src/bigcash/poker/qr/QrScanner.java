package bigcash.poker.qr;

import com.google.gwt.core.client.JavaScriptObject;

public class QrScanner extends JavaScriptObject {

    public static native QrScanner newScanner(String element)/*-{
        return $wnd.getQRScanner(element);
    }-*/;

    protected QrScanner() {
        super();
    }

    public final native void start(int qrFPS,int qrBoxSize,QrHandler handler)/*-{
        this.start({ facingMode: "environment" },
            {
                fps:qrFPS,
                qrbox:qrBoxSize
            },
            function(qrMessage){
                handler.@bigcash.poker.qr.QrScanner.QrHandler::onMessage(Ljava/lang/String;)(qrMessage);
            },
            function(qrError){
               handler.@bigcash.poker.qr.QrScanner.QrHandler::onError(Ljava/lang/String;)(qrError);
            }
        ).then(function(){
            handler.@bigcash.poker.qr.QrScanner.QrHandler::onSuccess()();
        },function(error){
            handler.@bigcash.poker.qr.QrScanner.QrHandler::onFail()();
        });
    }-*/;

    public final native void scanFile(String fileName,QrHandler handler)/*-{
        this.scanFile(fileName,true)
            .then(function(qrMessage){
                handler.@bigcash.poker.qr.QrScanner.QrHandler::onMessage(Ljava/lang/String;)(qrMessage);
            },function(qrError){
                handler.@bigcash.poker.qr.QrScanner.QrHandler::onError(Ljava/lang/String;)(qrError);
            });
    }-*/;


    public final native void stop()/*-{
        this.stop().then(
            function(){

        },
            function(error){

        });
    }-*/;


    public interface QrHandler{
        void onSuccess();
        void onFail();
        void onMessage(String message);
        void onError(String message);
    }
}
