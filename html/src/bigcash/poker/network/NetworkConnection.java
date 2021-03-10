package bigcash.poker.network;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.HttpRequestBuilder;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by harshit on 12/19/2016.
 */
public class NetworkConnection {

    public static void performGetCallWithParams(Net.HttpResponseListener httpResponseListener, String url, HashMap<String, String> params) {
        HttpRequestBuilder requestBuilder = new HttpRequestBuilder();
        Net.HttpRequest httpGet =requestBuilder.newRequest()
                .method(Net.HttpMethods.GET)
                .timeout(10000)
                .header("Content-Type","application/x-www-form-urlencoded")
                .url(url)
                .content(getParamString(checkParams(params)))
                .build();
        Gdx.net.sendHttpRequest(httpGet,httpResponseListener);
    }

    public static void performPostCallWithParams(Net.HttpResponseListener httpResponseListener, String url, HashMap<String, String> params) {
        HttpRequestBuilder requestBuilder = new HttpRequestBuilder();
        Net.HttpRequest httpPost =requestBuilder.newRequest()
                .method(Net.HttpMethods.POST)
                .timeout(10000)
                .header("Content-Type","application/x-www-form-urlencoded")
                .header("Access-Control-Allow-Origin","*")
                .header("crossorigin","anonymous")
                .url(url)
                .content(getParamString(checkParams(params)))
                .build();
        Gdx.net.sendHttpRequest(httpPost,httpResponseListener);
    }



    private static String getParamString(HashMap<String, String> params) {
        StringBuilder sbParams = new StringBuilder();
        int i = 0;
        for (String key : params.keySet()) {
            try {
                if (i != 0) {
                    sbParams.append("&");
                }
                sbParams.append(key).append("=")
                        .append(URLEncoder.encode(params.get(key), "UTF-8"));

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            i++;
        }
        return sbParams.toString();
    }

    private static HashMap<String, String> checkParams(HashMap<String, String> map) {
        Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> pairs = it.next();
            if (pairs.getValue() == null) {
                map.put(pairs.getKey(), "");
            }
        }
        return map;
    }

    public static void performGetCallWithParamsLessTimeOut(Net.HttpResponseListener httpResponseListener, String url, HashMap<String, String> params) {

        Net.HttpRequest httpGet = new Net.HttpRequest(Net.HttpMethods.GET);
        httpGet.setUrl(url);
        httpGet.setTimeOut(5000);
        httpGet.setContent(getParamString(checkParams(params)));
        Gdx.net.sendHttpRequest(httpGet, httpResponseListener);

        Gdx.net.sendHttpRequest(httpGet,httpResponseListener);
    }

    public static void performPostCallWithParamsIncreaseTimeOut(Net.HttpResponseListener httpResponseListener, String url, HashMap<String, String> params) {
        Net.HttpRequest httpPost = new Net.HttpRequest(Net.HttpMethods.POST);
        httpPost.setUrl(url);
        httpPost.setTimeOut(150000);
        httpPost.setContent(getParamString(checkParams(params)));
        Gdx.net.sendHttpRequest(httpPost,httpResponseListener);
    }

    public static void performPostCallWithJson(Net.HttpResponseListener httpResponseListener, String url, JsonValue jsonValue) {
        HttpRequestBuilder requestBuilder = new HttpRequestBuilder();
        Net.HttpRequest httpPost =requestBuilder.newRequest()
                .method(Net.HttpMethods.POST)
                .timeout(10000)
                .header("content-type","application/json")
                .url(url)
                .content(jsonValue.toJson(JsonWriter.OutputType.json))
                .build();
        Gdx.net.sendHttpRequest(httpPost,httpResponseListener);
    }

}
