package bigcash.poker.network;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.TimeUtils;
import com.google.gwt.core.client.JavaScriptObject;

import bigcash.poker.constants.Constant;
import bigcash.poker.utils.GamePreferences;
import bigcash.poker.utils.PokerUtils;

public class Payment {
    public static final int REST=0,PROCESSING=1,COMPLETED=2;
    public static final String MERCHANT_MID = "Techea83643642560567";
    public static final String CHANNEL_ID = "WEB";
    public static final String WEBSITE = "TecheaWEB";
    public static final String INDUSTRY_TYPE_ID = "Retail109";
    public static final String CALLBACK_URL = "https://securegw.paytm.in/theia/paytmCallback?ORDER_ID=";
    public static final String REQUEST_TYPE = "ADD_MONEY";
    public static final String ACTION="https://securegw.paytm.in/order/process?ORDER_ID=";
    public static final String CARD = "card";
    public static final String NET_BANKING = "netBanking";
    public static final String UPI = "upi";
    public static final String OTHER_WALLET = "otherWallet";

    public static void startPaytmProcess(String ssoToken,String orderId,String userId,String amount,String callbackUrl,String checksum,String cashbackId,String source,GdxListener<String> listener){
        StringBuilder outputHtml = new StringBuilder();
        String url=ACTION+orderId;
        outputHtml.append("<html>");
        outputHtml.append("<head>");
        outputHtml.append("<title>Merchant Checkout Page</title>");
        outputHtml.append("</head>");
        outputHtml.append("<body>");
        outputHtml.append("<center><h1>Please do not refresh this page...</h1></center>");
        outputHtml.append("<form method='post' action='" + url + "' name='paytm_form'>");
        outputHtml.append(getInputString("MID",Payment.MERCHANT_MID));
        outputHtml.append(getInputString("REQUEST_TYPE",Payment.REQUEST_TYPE));
        outputHtml.append(getInputString("SSO_TOKEN",ssoToken));
        outputHtml.append(getInputString("ORDER_ID",orderId));
        outputHtml.append(getInputString("CHANNEL_ID",Payment.CHANNEL_ID));
        outputHtml.append(getInputString("CUST_ID",userId));
        outputHtml.append(getInputString("TXN_AMOUNT",amount));
        outputHtml.append(getInputString("WEBSITE",Payment.WEBSITE));
        outputHtml.append(getInputString("INDUSTRY_TYPE_ID",Payment.INDUSTRY_TYPE_ID));
        outputHtml.append(getInputString("CALLBACK_URL",callbackUrl));
        outputHtml.append(getInputString("CHECKSUMHASH",checksum));
        outputHtml.append("</form>");
        outputHtml.append("<script type='text/javascript'>");
        outputHtml.append("document.paytm_form.submit();");
        outputHtml.append("</script>");
        outputHtml.append("</body>");
        outputHtml.append("</html>");
        startPaymentProcess(outputHtml.toString(), "width=" + Gdx.graphics.getWidth() + ",height=" + Gdx.graphics.getHeight(),
                new PaymentHandler() {
                    @Override
                    public void onCompleted(String jsonResponse) {
                        JsonValue response=new JsonReader().parse(jsonResponse);
                        String transactionId = response.getString("ORDERID");
                        String status = response.getString("STATUS");
                        if (status.matches("TXN_SUCCESS")) {
                            ApiHandler.callAddCashApi("PAYTM",amount,jsonResponse,orderId,source,cashbackId,listener);
                        } else {
                            listener.setFail("Transaction failed");
                        }
                    }

                    @Override
                    public void onError(String message) {
                    }
                });
    }

    private static String getInputString(String key,String value){
        return "<input type='hidden' name='" + key + "' value='" + value + "'>";
    }

    private static native void startPaymentProcess(String outputHtml, String params, PaymentHandler handler)/*-{
        $wnd.paymentCompleted=$entry(function(response){
            handler.@bigcash.poker.network.Payment.PaymentHandler::onCompleted(Ljava/lang/String;)(response);
        });
        var processWindow=$wnd.startPaytmProcess(outputHtml,params);
    }-*/;

    public static void payWithRazor(float amount,String cashbackId,String source,GdxListener<String> listener){
        String totalAmount= PokerUtils.getValue(amount*100)+"";
        String receiptId="Razor_"+ GamePreferences.instance().getUserId()+"-"+ TimeUtils.millis();
        ApiHandler.callRazorRequestOrder(totalAmount, receiptId, new GdxListener<JsonValue>() {
            @Override
            public void onSuccess(JsonValue jsonValue) {
                String userName= Constant.userProfile.getName();
                String userEmail="";
                String userContact="";
                if (Constant.userProfile.getEmailId()!=null){
                    userEmail=Constant.userProfile.getEmailId();
                }
                if (Constant.userProfile.getMsisdn()!=null){
                    userContact=Constant.userProfile.getMsisdn();
                }
                String orderId=jsonValue.getString("id");
                payWithRazor(totalAmount, orderId, userName, userEmail, userContact, new PaymentHandler() {
                    @Override
                    public void onCompleted(String response) {
                        JsonValue razorJson=new JsonReader().parse(response);
                        String paymentId=razorJson.getString("razorpay_payment_id");
                        ApiHandler.callAddCashApi("RAZORPAY",amount+"",paymentId,paymentId,source,cashbackId,listener);
                    }

                    @Override
                    public void onError(String message) {
                        listener.setError(message);
                    }
                });
            }

            @Override
            public void onFail(String reason) {
                listener.onFail(reason);
            }

            @Override
            public void onError(String error) {
                listener.onError(error);
            }
        });
    }


    public static native void payWithRazor(String totalAmount,String orderId,String userName,String userEmail,String userContact,PaymentHandler rHandler)/*-{
        var options={
            "key":"rzp_live_cyERWMCbrpznL7",
            "amount":totalAmount,
            "currency":"INR",
            "name":"Big Cash",
            "description":"Add Cash",
            "image":"https://1101993670.rsc.cdn77.org/img/ic_launcher.png",
            "order_id":orderId,
            "handler":function(response){
                $wnd.console.log(response);
                rHandler.@bigcash.poker.network.Payment.PaymentHandler::onCompleted(Ljava/lang/String;)(JSON.stringify(response));
            },
            "modal": {
            "ondismiss": function(){
                rHandler.@bigcash.poker.network.Payment.PaymentHandler::onError(Ljava/lang/String;)("Cancelled");
            }
            },
            "prefill":{
                "name":userName,
                "email":userEmail,
                "contact":userContact
                },
            "notes": {
                "address": "Witzeal Technologies"
                },
            "theme": {
                "color": "#F37254"
                }
            }
           var rzp=$wnd.getRazorPay(options);
           rzp.on("payment.failed",function(response){
                 rHandler.@bigcash.poker.network.Payment.PaymentHandler::onError(Ljava/lang/String;)(JSON.stringify(response));
           });
           rzp.open();
    }-*/;

    public static void payWithRazorOptions(float amount, String cashbackId, JavaScriptObject method, String source, GdxListener<String> listener){
        String totalAmount= PokerUtils.getValue(amount*100)+"";
        String receiptId="Razor_"+ GamePreferences.instance().getUserId()+"-"+ TimeUtils.millis();

        ApiHandler.callRazorRequestOrder(totalAmount, receiptId, new GdxListener<JsonValue>() {
            @Override
            public void onSuccess(JsonValue jsonValue) {
                String userName= Constant.userProfile.getName();
                String userEmail="";
                String userContact="";
                if (Constant.userProfile.getEmailId()!=null){
                    userEmail=Constant.userProfile.getEmailId();
                }
                if (Constant.userProfile.getMsisdn()!=null){
                    userContact=Constant.userProfile.getMsisdn();
                }
                String orderId=jsonValue.getString("id");
                payWithRazorOptions(totalAmount, orderId, userName, userEmail, userContact,method, new PaymentHandler() {
                    @Override
                    public void onCompleted(String response) {
                        JsonValue razorJson=new JsonReader().parse(response);
                        String paymentId=razorJson.getString("razorpay_payment_id");
                        ApiHandler.callAddCashApi("RAZORPAY",amount+"",paymentId,paymentId,source,cashbackId,listener);
                    }

                    @Override
                    public void onError(String message) {
                        listener.setError(message);
                    }
                });
            }

            @Override
            public void onFail(String reason) {
                listener.onFail(reason);
            }

            @Override
            public void onError(String error) {
                listener.onError(error);
            }
        });
    }


    public static native void payWithRazorOptions(String totalAmount,String orderId,String userName,String userEmail,String userContact,JavaScriptObject methods,PaymentHandler rHandler)/*-{
        var options={
            "key":"rzp_live_cyERWMCbrpznL7",
            "amount":totalAmount,
            "currency":"INR",
            "name":"Big Cash",
            "description":"Add Cash",
            "image":"https://1101993670.rsc.cdn77.org/img/ic_launcher.png",
            "order_id":orderId,
            "handler":function(response){
                $wnd.console.log(response);
                rHandler.@bigcash.poker.network.Payment.PaymentHandler::onCompleted(Ljava/lang/String;)(JSON.stringify(response));
            },
            "modal": {
            "ondismiss": function(){
                rHandler.@bigcash.poker.network.Payment.PaymentHandler::onError(Ljava/lang/String;)("Cancelled");
            }
            },
            "prefill":{
                "name":userName,
                "email":userEmail,
                "contact":userContact
                },
            "notes": {
                "address": "Witzeal Technologies"
                },
            "theme": {
                "color": "#F37254"
                },
            "method":methods
            }
           var rzp=$wnd.getRazorPay(options);
           rzp.on("payment.failed",function(response){
                 rHandler.@bigcash.poker.network.Payment.PaymentHandler::onError(Ljava/lang/String;)(JSON.stringify(response));
           });
           rzp.open();
    }-*/;



    interface PaymentHandler {
        void onCompleted(String response);
        void onError(String message);
    }
}
