package bigcash.poker.constants;

import com.badlogic.gdx.utils.Array;

import bigcash.poker.models.AccountSummary;
import bigcash.poker.models.InitialValue;
import bigcash.poker.models.UserProfile;
import bigcash.poker.utils.PokerUtils;

public class Constant {
    public static InitialValue initialValue;
    public static UserProfile userProfile;
    public static String deviceId,androidId,paytmRules;
    public static long backFomPokerTime;
    public static String[] emojis=null;
    public static boolean isSetMaintenanceInThisSession;
    public static boolean isShowMaintenance,deviceTokenActive;
    public static float minAddCashThreshold;
    public static String RedeemMessage = "";
    public static long minId = 0;

    public static Array<AccountSummary> arrAccountSummary = null;


    public static String getDeviceType(){
        return "ANDROID";
    }
    public static String getVersionCode() {
       return "93";
    }

    public static String getNetworkOperator(){
        return "Android";
    }

    public static String getCanonicalPath(){
        return "/data/data/best.bulbsmash.cash/files";
    }

    public static String getAbsolutePath(){
        return "/data/user/0/best.bulbsmash.cash/files";
    }

    public static String getAppName(){
        return encryptValue("Big Cash");
    }

    public static String getPackageName(){
        return encryptValue("best.bulbsmash.cash");
    }

    public static String getKeyHash(){
        return encryptValue("s2p/V28mLKqemSK0vBKSzOf4yhA=");
    }

    public static String getAndroidVersion(){
        return "8.1.0";
    }

    public static String getDeviceId(){
        return deviceId;
    }

    public static String getAndroidId(){
        return androidId;
    }

    public static String encryptValue(String value){
        try {
            return PokerUtils.encrypt(value,"bsplay@");
        }catch (Exception e){

        }
        return "";
    }

}
