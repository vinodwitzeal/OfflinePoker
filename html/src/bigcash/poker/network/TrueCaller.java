package bigcash.poker.network;

import com.badlogic.gdx.utils.TimeUtils;

import java.io.IOException;
import java.io.StringWriter;

import bigcash.poker.constants.Constant;

public class TrueCaller {
    public static int count;
    public static void initializeVerification(GdxListener<String> listener){
        String requestId=Constant.userProfile.getUserId()+"-"+ TimeUtils.millis();
        count=3;
        try{
            TrueCallerRequestBuilder requestBuilder=new TrueCallerRequestBuilder(requestId);
            requestBuilder.number(Constant.userProfile.getMsisdn());
            callTrueCaller(requestBuilder.build(), new TrueCallerHandler() {
                @Override
                public void onProcessing() {

                }

                @Override
                public void onFailed() {
                    listener.setFail("App Not Installed");
                }
            });
        }catch (Exception e){
            listener.setFail(e.getMessage());
        }
    }

    public static void invokeTrueCaller(String requestId,TrueCallerHandler handler){
        try{
            TrueCallerRequestBuilder requestBuilder=new TrueCallerRequestBuilder(requestId);
            callTrueCaller(requestBuilder.build(),handler);
            requestBuilder.number(Constant.userProfile.getMsisdn());
        }catch (Exception e){
            handler.onFailed();
        }
    }

    private static native void callTrueCaller(String request,TrueCallerHandler handler)/*-{
        $wnd.location=request;
        setTimeout(function(){
            if($doc.hasFocus()){
                handler.@bigcash.poker.network.TrueCaller.TrueCallerHandler::onFailed()();
            }else{
                handler.@bigcash.poker.network.TrueCaller.TrueCallerHandler::onProcessing()();
            }
        },600);
    }-*/;

    public static class TrueCallerRequestBuilder{
        private StringWriter stringWriter;
        public TrueCallerRequestBuilder(String requestId) throws IOException {
            stringWriter=new StringWriter();
            stringWriter.append("truecallersdk://truesdk/web_verify");
            stringWriter.append("?requestNonce=").append(requestId);
            stringWriter.append("&partnerKey=").append("59Atd868d9996ef5f4daeb7dea07547d808c1");
            stringWriter.append("&partnerName=Big Cash");
            stringWriter.append("&lang=en");
            stringWriter.append("&title=Verify Number");
        }

        public TrueCallerRequestBuilder number(String number) throws IOException {
            stringWriter.append("&skipOption=").append("manualdetails");
            stringWriter.append("&ctaPrefix=").append("proceedwith");
            return this;
        }

        public String build(){
            return stringWriter.toString();
        }


    }

    public interface TrueCallerHandler{
        void onProcessing();
        void onFailed();
    }
}
