package bigcash.poker.paytm;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.HttpRequestBuilder;
import com.badlogic.gdx.net.HttpStatus;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.TimeUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;

import java.util.HashMap;

import bigcash.poker.constants.Constant;
import bigcash.poker.dialogs.MaintenanceDialog;
import bigcash.poker.dialogs.ProcessDialog;
import bigcash.poker.gwt.PokerGame;
import bigcash.poker.models.UIScreen;
import bigcash.poker.network.ApiHandler;
import bigcash.poker.network.GdxListener;
import bigcash.poker.network.Payment;
import bigcash.poker.screens.PokerContestScreen;
import bigcash.poker.utils.GamePreferences;
import bigcash.poker.utils.PokerUtils;

public class PaytmWindow extends JavaScriptObject {
    private static native PaytmWindow createWindow(Payment.PaymentHandler handler)/*-{
        $wnd.paymentCompleted=$entry(function(response){
            handler.@bigcash.poker.network.Payment.PaymentHandler::onCompleted(Ljava/lang/String;)(response);
        });
        return $wnd.open("","_blank","");
    }-*/;

    protected PaytmWindow() {
        super();
    }

    public final native void write(String output)/*-{
        this.document.write(output);
    }-*/;

    public final native void close()/*-{
        this.close();
    }-*/;

    public static void openWindow(String orderId, float price, String cashBackId, ProcessDialog processDialog, String source, GdxListener<String> listener){
        final float amount = PokerUtils.getValue(price);
        Payment.PaymentHandler handler=new Payment.PaymentHandler() {
            @Override
            public void onCompleted(String jsonResponse) {
                listener.setProcessing();
                JsonValue response=new JsonReader().parse(jsonResponse);
                String transactionId = response.getString("ORDERID");
                String status = response.getString("STATUS");
                if (status.matches("TXN_SUCCESS")) {
                    ApiHandler.callAddCashApi("PAYTM",amount+"",jsonResponse,orderId,source,cashBackId,listener);
                } else {
                    listener.setFail("Transaction failed");
                }
            }

            @Override
            public void onError(String message) {
                listener.setFail("Transaction failed");
            }
        };
        PaytmWindow paytmWindow=PaytmWindow.createWindow(handler);

        StringBuilder outputHtml = new StringBuilder();
        outputHtml.append("<html>");
        outputHtml.append("<head>");
        outputHtml.append("<title>Merchant Checkout Page</title>");
        outputHtml.append("</head>");
        outputHtml.append("<body>");
        outputHtml.append("<center><h1>Please do not refresh this page...</h1></center>");
        outputHtml.append("</body>");
        outputHtml.append("</html>");
        paytmWindow.write(outputHtml.toString());

        HashMap<String, String> params = new HashMap();
        final String callbackUrl = GWT.getHostPageBaseURL() + "checkout";
        String userId=GamePreferences.instance().getUserId();
        String transactionId = TimeUtils.millis() + "";
        params.put("requestType", Payment.REQUEST_TYPE);
        params.put("userId", userId);
        params.put("otp", "abc");
        params.put("transactionId", transactionId + "");
        params.put("amount", amount + "");
        params.put("orderId", orderId);
        params.put("merchantMid", Payment.MERCHANT_MID);
        params.put("channelId", Payment.CHANNEL_ID);
        params.put("website", Payment.WEBSITE);
        params.put("industryTypeId", Payment.INDUSTRY_TYPE_ID);
        params.put("callbackUrl", callbackUrl);
        params.put("isWeb", "true");

        HttpRequestBuilder requestBuilder = new HttpRequestBuilder();
        Net.HttpRequest httpPost = requestBuilder.newRequest()
                .method(Net.HttpMethods.POST)
                .timeout(10000)
                .formEncodedContent(params)
                .url(ApiHandler.BASE_URL + ApiHandler.API_VERSION_1 + ApiHandler.PAYTM_GENERATE_CHECK_SUM_API + "?userId=" + userId)
                .build();

        Gdx.net.sendHttpRequest(httpPost, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                int statusCode = httpResponse.getStatus().getStatusCode();
                if (errorCodeHandling(statusCode)) {
                    JsonValue jsonValue = new JsonReader().parse(httpResponse.getResultAsString());
                    String checksum = jsonValue.getString("checksum");
                    String ssoToken = jsonValue.getString("sso_token");

                    StringBuilder outputHtml = new StringBuilder();
                    String url=Payment.ACTION+orderId;
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
                    outputHtml.append(getInputString("TXN_AMOUNT",amount+""));
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
                    paytmWindow.write(outputHtml.toString());
                } else {
                    paytmWindow.close();
                    if (statusCode != HttpStatus.SC_UNAUTHORIZED) {
                        listener.setError("Server Error");
                    }
                }
            }
            @Override
            public void failed(Throwable t) {
                paytmWindow.close();
                listener.setFail(t.getMessage());
            }

            @Override
            public void cancelled() {
                paytmWindow.close();
                listener.setError("Cancelled");
            }
        });
    }

    private static String getInputString(String key,String value){
        return "<input type='hidden' name='" + key + "' value='" + value + "'>";
    }

    private static boolean errorCodeHandling(int code) {
        if (code == 200) {
            return true;
        } else if (code == 500 || code == 226) {
            return false;
        } else if (code == 503) {
            openServerMaintenance();
            return false;
        } else if (code == 401) {
            PokerGame.resetAppWhenErrorGet();
            return false;
        } else if (code == 406) {
            return false;
        } else {
            return false;
        }
    }

    private static void openServerMaintenance() {
        if (!Constant.isSetMaintenanceInThisSession) {
            Constant.isSetMaintenanceInThisSession = true;
            Gdx.app.postRunnable(new Runnable() {
                @Override
                public void run() {
                    PokerGame pokerGame = (PokerGame) Gdx.app.getApplicationListener();
                        pokerGame.setScreen(new PokerContestScreen(pokerGame) {
                            @Override
                            public void show() {
                                super.show();
                                new MaintenanceDialog(this) {
                                    @Override
                                    public void hide() {
                                        super.hide();
                                        Constant.isSetMaintenanceInThisSession = false;
                                    }
                                }.show(stage);
                            }
                        });
                }
            });
        }
    }
}
