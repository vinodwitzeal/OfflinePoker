package bigcash.poker.utils;

import java.util.ArrayList;

public class BestPossibleCombination {

    private ArrayList<Integer> bestCards;

    private String userId;

    private int groupType;

    private int cardsWeight;

    private int highPairCard;

    private int lowPairCard;

    public ArrayList<Integer> arrCardWeight;


    public BestPossibleCombination(ArrayList<Integer> bestCards, String userId, int groupType) {
        this.bestCards = bestCards;
        this.userId = userId;
        this.groupType = groupType;
        arrCardWeight = new ArrayList<Integer>();
        setCardWeight();
    }

    private void setCardWeight(){
        boolean hasAce = false, hasTwo=false;
        for (int card:bestCards){
            int mod = card%13==0?13:card%13;
            if(mod == 1){
                mod = 14;
                hasAce = true;
            }
            if(mod==2){
                hasTwo = true;
            }
            arrCardWeight.add(mod);
            cardsWeight = cardsWeight+mod;
        }
        if(groupType == PokerCombinations.STRAIGHT || groupType == PokerCombinations.STRAIGHT_FLUSH){
            if(hasAce && hasTwo){
                arrCardWeight.remove(new Integer(14));
                arrCardWeight.add(1);
                cardsWeight = cardsWeight - 13;
            }
        }
    }

    public int getCardsWeight(){
        return cardsWeight;
    }

    public ArrayList<Integer> getBestCards() {
        return bestCards;
    }

    public void setBestCards(ArrayList<Integer> bestCards) {
        this.bestCards = bestCards;
    }

    public int getGroupType() {
        return groupType;
    }

    public void setGroupType(int groupType) {
        this.groupType = groupType;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getHighPairCard() {
        return highPairCard;
    }

    public void setHighPairCard(int highPairCard) {
        highPairCard = highPairCard%13==0?13: highPairCard%13;
        if(highPairCard==1){
            highPairCard = 14;
        }
        this.highPairCard = highPairCard;
    }

    public int getLowPairCard() {
        return lowPairCard;
    }

    public void setLowPairCard(int lowPairCard) {
        lowPairCard = lowPairCard%13==0?13: lowPairCard%13;
        if(lowPairCard==1){
            lowPairCard = 14;
        }
        this.lowPairCard =lowPairCard;
    }
}
