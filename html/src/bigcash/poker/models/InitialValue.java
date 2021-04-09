package bigcash.poker.models;

import com.badlogic.gdx.Gdx;

import bigcash.poker.utils.GamePreferences;
import bigcash.poker.utils.PokerUtils;

public class InitialValue {
    private String zero, one, two, three, four, five, six, nine, ccImages, bscImages, bbImages, khImages, twelve, sixty, twoHundred, fourHundred, eightThousand, tenThousand, eight;

    public InitialValue() {
    }

    public static String decrypt(String value) {
        String decrypt = "";
        try {
            decrypt = PokerUtils.decrypt(value, GamePreferences.instance().getOtp());
        } catch (Exception e) {
        }
        return decrypt;
    }

    public static String encrypt(String value) {
        String encrypt = "";
        try {
            encrypt = PokerUtils.encrypt(value, GamePreferences.instance().getOtp());
        } catch (Exception e) {
        }
        return encrypt;
    }

    public int getZero() {
        return Integer.parseInt(decrypt(zero));
    }

    public void setZero(String zero) {
        this.zero = zero;
    }

    public int getOne() {
        return Integer.parseInt(decrypt(one));
    }

    public void setOne(String one) {
        this.one = one;
    }

    public int getTwo() {
        return Integer.parseInt(decrypt(two));
    }

    public void setTwo(String two) {
        this.two = two;
    }

    public int getThree() {
        return Integer.parseInt(decrypt(three));
    }

    public void setThree(String three) {
        this.three = three;
    }

    public int getFour() {
        return Integer.parseInt(decrypt(four));
    }

    public void setFour(String four) {
        this.four = four;
    }

    public int getFive() {
        return Integer.parseInt(decrypt(five));
    }

    public void setFive(String five) {
        this.five = five;
    }

    public int getSix() {
        return Integer.parseInt(decrypt(six));
    }

    public void setSix(String six) {
        this.six = six;
    }

    public int getTwelve() {
        return Integer.parseInt(decrypt(twelve));
    }

    public void setTwelve(String twelve) {
        this.twelve = twelve;
    }

    public int getNine() {
        return Integer.parseInt(decrypt(nine));
    }

    public void setNine(String nine) {
        this.nine = nine;
    }

    public int getEight() {
        return Integer.parseInt(decrypt(eight));
    }

    public void setEight(String eight) {
        this.eight = eight;
    }

    public int getTenThousand() {
        return Integer.parseInt(decrypt(tenThousand));
    }

    public void setTenThousand(String tenThousand) {
        this.tenThousand = tenThousand;
    }

    public int getEightThousand() {
        return Integer.parseInt(decrypt(eightThousand));
    }

    public void setEightThousand(String eightThousand) {
        this.eightThousand = eightThousand;
    }

    public int getSixty() {
        return Integer.parseInt(decrypt(sixty));
    }

    public void setSixty(String sixty) {
        this.sixty = sixty;
    }

    public int getTwoHundred() {
        return Integer.parseInt(decrypt(twoHundred));
    }

    public void setTwoHundred(String twoHundred) {
        this.twoHundred = twoHundred;
    }

    public int getFourHundred() {
        return Integer.parseInt(decrypt(fourHundred));
    }

    public void setFourHundred(String fourHundred) {
        this.fourHundred = fourHundred;
    }

//    public String getCcImages() {
//        return decrypt(GamePreferences.instance().getCcImages());
//    }
//
//    public void setCcImages(String ccImages) {
//        GamePreferences.instance().setCcImages(ccImages);
//    }
//
//    public String getBbImages() {
//        return decrypt(GamePreferences.instance().getBbImages());
//    }
//
//    public void setBbImages(String bbImages) {
//        GamePreferences.instance().setBbImages(bbImages);
//    }
//
//    public String getCarRaceImages() {
//        return decrypt(GamePreferences.instance().getCarRaceImages());
//    }
//
//    public void setCarRaceImages(String bbImages) {
//        GamePreferences.instance().setCarRaceImages(bbImages);
//    }
//
//    public String getKhImages() {
//        return decrypt(GamePreferences.instance().getKnivesHitImages());
//    }
//
//    public void setKhImages(String khImages) {
//        GamePreferences.instance().setKnivesHitImages(khImages);
//    }
//
//    public String getSoccerImages() {
//        return decrypt(GamePreferences.instance().getSoccerImages());
//    }
//
//    public void setSoccerImages(String soccerImages) {
//        GamePreferences.instance().setSoccerImages(soccerImages);
//    }
//
//    public String getFruitChopImages() {
//        return decrypt(GamePreferences.instance().getFruitChopImages());
//    }
//
//    public void setFruitChopImages(String fruitChopImages) {
//        GamePreferences.instance().setFruitChopImages(fruitChopImages);
//    }
//
//    public String getEggTossImages() {
//        return decrypt(GamePreferences.instance().getEggTossImages());
//    }
//
//    public void setEggTossImages(String eggTossImages) {
//        GamePreferences.instance().setEggTossImages(eggTossImages);
//    }
//
//    public String getIceBlasterImages() {
//        return decrypt(GamePreferences.instance().getIceBlasterImages());
//    }
//
//    public void setIceBlasterImages(String iceBlasterImages) {
//        GamePreferences.instance().setIceBlasterImages(iceBlasterImages);
//    }
//
//    public String getEightBallPoolImages() {
//        return decrypt(GamePreferences.instance().getEightBallPoolImages());
//    }
//
//    public void setEightBallPoolImages(String bbImages) {
//        GamePreferences.instance().setEightBallPoolImages(bbImages);
//    }
//
//    public String getBscImages() {
//        return decrypt(GamePreferences.instance().getBscImages());
//    }
//
//    public void setBscImages(String bscImages) {
//        GamePreferences.instance().setBscImages(bscImages);
//    }
//
//    public String getKMultiPlayerImages() {
//        return decrypt(GamePreferences.instance().getKMultiPlayerImages());
//    }
//
//    public void setKMultiPlayerImages(String kMultiPlayerImages) {
//        GamePreferences.instance().setKMultiPlayerImages(kMultiPlayerImages);
//    }
}
