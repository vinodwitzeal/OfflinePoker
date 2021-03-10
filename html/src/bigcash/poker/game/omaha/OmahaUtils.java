package bigcash.poker.game.omaha;

import java.util.ArrayList;
import java.util.List;

import bigcash.poker.utils.BestPossibleCombination;
import bigcash.poker.utils.PokerUtils;


public class OmahaUtils {
    public static BestPossibleCombination getBestPossibleCombination(List<Integer> userCards, List<Integer> openCards, String userId) {
        if (userCards == null || userCards.size() != 4) {
            throw new IllegalArgumentException("userCards is required or incorrect");
        }
        if (openCards == null || openCards.size() < 3) {
            throw new IllegalArgumentException("openCards is required or incorrect");
        }

        BestPossibleCombination bestPossibleCombination = null;
        List<ArrayList<Integer>> userCardCombinations = Combination.generate(userCards,userCards.size(),2);
        List<ArrayList<Integer>> openCardCombinations = Combination.generate(openCards,openCards.size(),3);
        ArrayList<BestPossibleCombination> possibleCombinationFinalList = new ArrayList<BestPossibleCombination>();
        for (ArrayList<Integer> userCardList : userCardCombinations) {
            ArrayList<BestPossibleCombination> possibleCombinationList = new ArrayList<BestPossibleCombination>();
            for (ArrayList<Integer> openCardList : openCardCombinations) {
                possibleCombinationList.add(PokerUtils.getBestPossibleCombination(userCardList, openCardList, userId));
            }
            if (1 < possibleCombinationList.size()) {
                possibleCombinationFinalList.add(PokerUtils.getWinner(possibleCombinationList).get(0));
            }else {
                possibleCombinationFinalList.add(possibleCombinationList.get(0));
            }
        }

        if (1 < possibleCombinationFinalList.size()) {
            bestPossibleCombination = PokerUtils.getWinner(possibleCombinationFinalList).get(0);
        }else {
            bestPossibleCombination = possibleCombinationFinalList.get(0);
        }

        return bestPossibleCombination;
    }
}
