package bigcash.poker.network;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.FileUpload;

public class HFormData extends JavaScriptObject {

    protected HFormData() {
        super();
    }

    public static native HFormData newFormData()/*-{
        return new FormData();
    }-*/;

    public final native void append(String name, String value)/*-{
        this.append(name,value);
    }-*/;

    public final native void append(String name, Element element)/*-{
        this.append(name, element.files[0]);
    }-*/;


    public final void appendFileElement(String name, FileUpload fileUpload) {
        append(name, fileUpload.getElement());
    }


    public static native void sendFormData(String url, HFormData formData,final HFormListener listener)/*-{
        var request = new XMLHttpRequest();
        request.open("POST", url);
        request.onload=function(event){
            if(request.readyState==4 && request.status==200){
                if(request.status==200){
                     listener.@bigcash.poker.network.HFormData.HFormListener::onSuccess(Ljava/lang/String;)(request.responseText);
                }else{
                    if(request.status==226){
                        listener.@bigcash.poker.network.HFormData.HFormListener::onSuccess(Ljava/lang/String;)(request.responseText);
                    }else{
                        listener.@bigcash.poker.network.HFormData.HFormListener::onError(Ljava/lang/String;)("Try Again.");
                    }
               }
            }
        };
        request.send(formData);
    }-*/;


    public  interface HFormListener{
        void onSuccess(String response);
        void onError(String error);
    }
}
