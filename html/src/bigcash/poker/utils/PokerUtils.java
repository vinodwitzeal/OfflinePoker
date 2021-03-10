package bigcash.poker.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.gwt.GwtGraphics;
import com.google.gwt.core.client.JavaScriptException;
import com.google.gwt.user.client.Window;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import bigcash.poker.gwt.PokerGame;
import bigcash.poker.network.ApiHandler;
import bigcash.poker.network.LocationHandler;

public class PokerUtils {

    private static String userId = "";
    public static  boolean mobileDevice;

    public static BestPossibleCombination getBestPossibleCombination(ArrayList<Integer> userCards, ArrayList<Integer> openCards, String userId) {
        if (userCards == null || userCards.size() != 2) {
            throw new IllegalArgumentException("userCards is required or incorrect");
        }
        if (openCards == null || openCards.size() < 3) {
            throw new IllegalArgumentException("openCards is required or incorrect");
        }
        PokerUtils.userId = userId;
        ArrayList<Integer> allCards = new ArrayList<Integer>();
        allCards.addAll(userCards);
        allCards.addAll(openCards);
        ArrayList<Integer> clubsCards = new ArrayList<Integer>();
        ArrayList<Integer> heartsCards = new ArrayList<Integer>();
        ArrayList<Integer> diamondsCards = new ArrayList<Integer>();
        ArrayList<Integer> spadesCards = new ArrayList<Integer>();
        for (int card : allCards) {
            if (card > 0 && card <= 13) {
                clubsCards.add(card);
            } else if (card > 13 && card <= 26) {
                heartsCards.add(card);
            } else if (card > 26 && card <= 39) {
                spadesCards.add(card);
            } else if (card > 39 && card <= 52) {
                diamondsCards.add(card);
            }
        }

        if (isHavingFlush(clubsCards)) {
            return checkForFlushOrRoyalFlush(clubsCards);
        } else if (isHavingFlush(heartsCards)) {
            return checkForFlushOrRoyalFlush(heartsCards);
        } else if (isHavingFlush(spadesCards)) {
            return checkForFlushOrRoyalFlush(spadesCards);
        } else if (isHavingFlush(diamondsCards)) {
            return checkForFlushOrRoyalFlush(diamondsCards);
        } else {
            return checkForOtherPokerCombination(allCards);
        }
    }

    public static BestPossibleCombination checkForFlushOrRoyalFlush(ArrayList<Integer> flushCards) {
        int aceCardNo = 0;
        for (int card : flushCards) {
            if (card % 13 == 1) {
                aceCardNo = card;
                break;
            }
        }
        if (aceCardNo != 0) {
            flushCards.remove(new Integer(aceCardNo));
        }
        Collections.sort(flushCards, new SortCard());
        ArrayList<Integer> straightArray = new ArrayList<Integer>();
        boolean isStraight = false;
        for (int i = 0; i < flushCards.size(); i++) {
            if (straightArray.size() == 0) {
                straightArray.add(flushCards.get(i));
            } else {
                if ((straightArray.get(straightArray.size() - 1) - 1) == flushCards.get(i)) {
                    straightArray.add(flushCards.get(i));
                } else {
                    if (straightArray.size() >= 5) {
                        break;
                    } else {
                        straightArray.clear();
                        straightArray.add(flushCards.get(i));
                    }
                }
            }
            if (aceCardNo != 0 && straightArray.size() == 4
                    && (straightArray.get(straightArray.size() - 1) % 13 == 2 || straightArray.get(0) % 13 == 0)) {
                isStraight = true;
                break;
            }
        }
        if (straightArray.size() >= 5) {
            isStraight = true;
        }

        if (isStraight) {
            if (aceCardNo != 0) {
                if (straightArray.get(0) % 13 == 0) {
                    straightArray.add(0, aceCardNo);
                    return new BestPossibleCombination(straightArray, PokerUtils.userId, PokerCombinations.ROYAL_FLUSH);
                } else if (straightArray.get(straightArray.size() - 1) % 13 == 2) {
                    while (straightArray.size() > 5) {
                        straightArray.remove(straightArray.size() - 1);
                    }
                    if (straightArray.size() == 5) {
                        return new BestPossibleCombination(straightArray, PokerUtils.userId, PokerCombinations.STRAIGHT_FLUSH);
                    } else {
                        straightArray.add(aceCardNo);
                        return new BestPossibleCombination(straightArray, PokerUtils.userId, PokerCombinations.STRAIGHT_FLUSH);
                    }
                } else {
                    while (straightArray.size() > 5) {
                        straightArray.remove(straightArray.size() - 1);
                    }
                    return new BestPossibleCombination(straightArray, PokerUtils.userId, PokerCombinations.STRAIGHT_FLUSH);
                }
            } else {
                while (straightArray.size() > 5) {
                    straightArray.remove(straightArray.size() - 1);
                }
                return new BestPossibleCombination(straightArray, PokerUtils.userId, PokerCombinations.STRAIGHT_FLUSH);
            }
        } else {
            if (aceCardNo != 0) {
                ArrayList<Integer> arrBestFlushCards = new ArrayList<Integer>();
                for (int i = 0; i < 4; i++) {
                    arrBestFlushCards.add(flushCards.get(i));
                }
                arrBestFlushCards.add(aceCardNo);
                return new BestPossibleCombination(arrBestFlushCards, PokerUtils.userId, PokerCombinations.FLUSH);
            } else {
                ArrayList<Integer> arrBestFlushCards = new ArrayList<Integer>();
                for (int i = 0; i < 5; i++) {
                    arrBestFlushCards.add(flushCards.get(i));
                }
                return new BestPossibleCombination(arrBestFlushCards, PokerUtils.userId, PokerCombinations.FLUSH);
            }
        }
    }

    static class SortCard implements Comparator<Integer> {
        // Used for sorting in descending order
        public int compare(Integer a, Integer b) {
            return b - a;
        }
    }

    static class SortCardWithMod implements Comparator<Integer> {
        // Used for sorting in descending order

        public int compare(Integer a, Integer b) {
            int bMod = b % 13 == 0 ? 13 : b % 13;
            int aMod = a % 13 == 0 ? 13 : a % 13;
            return bMod - aMod;
        }
    }

    public static BestPossibleCombination checkForOtherPokerCombination(ArrayList<Integer> allCards) {
        BestPossibleCombination bestPossibleCombination = null;
        bestPossibleCombination = checkForFourOfKind(allCards);
        if (bestPossibleCombination != null) {
            return bestPossibleCombination;
        }

        ArrayList<PairCard> pairCards = new ArrayList<PairCard>();
        boolean havingThreeCards = false;
        ArrayList<Integer> remainingCard = new ArrayList<Integer>();
        remainingCard.addAll(allCards);
        for (int card : allCards) {
            int i = 1;
            PairCard pairCard = new PairCard();
            pairCard.addCards(card);
            for (int card1 : remainingCard) {
                if (card != card1) {
                    if (card % 13 == card1 % 13) {
                        pairCard.addCards(card1);
                        i++;
                    }
                }
            }
            if (i >= 2) {
                pairCards.add(pairCard);
                remainingCard.removeAll(pairCard.getCards());
                if (i == 3) {
                    havingThreeCards = true;
                }
            }
        }
        if (havingThreeCards && pairCards.size() > 1) {
            bestPossibleCombination = checkForFullHouse(pairCards);
            if (bestPossibleCombination != null) {
                return bestPossibleCombination;
            }
        }

        bestPossibleCombination = checkForStraight(allCards);
        if (bestPossibleCombination != null) {
            return bestPossibleCombination;
        }

        int aceCardNo = getAceCardInRemainingCard(remainingCard);

        if (pairCards.size() == 1 && pairCards.get(0).getCards().size() == 3) {
            bestPossibleCombination = checkForThreeOfKind(pairCards, remainingCard, aceCardNo);
            if (bestPossibleCombination != null) {
                return bestPossibleCombination;
            }
        }

        if (pairCards.size() >= 2) {
            bestPossibleCombination = checkForTwoPair(pairCards, remainingCard, aceCardNo);
            if (bestPossibleCombination != null) {
                return bestPossibleCombination;
            }
        }

        if (pairCards.size() == 1 && pairCards.get(0).getCards().size() == 2) {
            bestPossibleCombination = checkForOnePair(pairCards, remainingCard, aceCardNo);
            if (bestPossibleCombination != null) {
                return bestPossibleCombination;
            }
        }

        if (pairCards.size() == 0) {
            bestPossibleCombination = checkForHighCard(remainingCard, aceCardNo);
            if (bestPossibleCombination != null) {
                return bestPossibleCombination;
            }
        }

        return null;
    }

    private static int getAceCardInRemainingCard(ArrayList<Integer> remainingCard) {
        int aceCardNo = 0;
        for (int card : remainingCard) {
            if (card % 13 == 1) {
                aceCardNo = card;
                break;
            }
        }
        return aceCardNo;
    }

    private static BestPossibleCombination checkForFourOfKind(ArrayList<Integer> allCards) {
        int i = 1;
        ArrayList<Integer> sameFourCard = new ArrayList<Integer>();
        for (int card : allCards) {
            sameFourCard.add(card);
            for (int card1 : allCards) {
                if (card != card1) {
                    if (card % 13 == card1 % 13) {
                        sameFourCard.add(card1);
                        i++;
                    }
                }
            }
            if (i == 4) {
                break;
            } else {
                i = 1;
                sameFourCard.clear();
            }
        }
        if (i == 4) {
            allCards.removeAll(sameFourCard);
            int highCard = 0;
            int cardMod = 0;
            for (int card : allCards) {
                int currentCardMod = card % 13 == 0 ? 13 : card % 13;
                if (currentCardMod == 1) {
                    highCard = card;
                    break;
                } else {
                    if (currentCardMod > cardMod) {
                        cardMod = currentCardMod;
                        highCard = card;
                    }
                }
            }
            sameFourCard.add(highCard);
            BestPossibleCombination possibleCombination = new BestPossibleCombination(sameFourCard, PokerUtils.userId, PokerCombinations.FOUR_OF_A_KIND);
            possibleCombination.setHighPairCard(sameFourCard.get(0));
            return possibleCombination;
        } else {
            return null;
        }
    }

    private static BestPossibleCombination checkForFullHouse(ArrayList<PairCard> pairCards) {
        PairCard firstCardGroup = null, secondGroup = null;
        int firstGroupMod = 0;
        int secondGroupMod = 0;

        for (PairCard pairCard : pairCards) {
            int mod = pairCard.getCards().get(0) % 13;
            int cardMod = mod == 0 ? 13 : mod;
            if (pairCard.getCards().size() == 3 && (cardMod > firstGroupMod || cardMod == 1)) {
                if (firstGroupMod != 1) {
                    firstGroupMod = cardMod;
                    firstCardGroup = pairCard;
                } else {
                    break;
                }
            }
        }

        for (PairCard pairCard : pairCards) {
            int mod = pairCard.getCards().get(0) % 13;
            int cardMod = mod == 0 ? 13 : mod;
            if (pairCard.getCards().size() >= 2
                    && (cardMod > secondGroupMod || cardMod == 1)
                    && cardMod != firstGroupMod) {
                if (secondGroupMod != 1) {
                    secondGroupMod = cardMod;
                    secondGroup = pairCard;
                } else {
                    break;
                }
            }
        }

        ArrayList<Integer> fullHouseCards = new ArrayList<Integer>();
        fullHouseCards.addAll(firstCardGroup.getCards());
        if (secondGroup.getCards().size() > 2) {
            secondGroup.getCards().remove(0);
        }
        fullHouseCards.addAll(secondGroup.getCards());

        BestPossibleCombination possibleCombination = new BestPossibleCombination(fullHouseCards, PokerUtils.userId, PokerCombinations.FULL_HOUSE);
        possibleCombination.setHighPairCard(firstCardGroup.getCards().get(0));
        possibleCombination.setLowPairCard(secondGroup.getCards().get(0));
        return possibleCombination;
    }

    private static BestPossibleCombination checkForStraight(ArrayList<Integer> allCards) {
        int aceCardNo = 0;
        for (int card : allCards) {
            if (card % 13 == 1) {
                aceCardNo = card;
                break;
            }
        }
        if (aceCardNo != 0) {
            allCards.remove(new Integer(aceCardNo));
        }
        Collections.sort(allCards, new SortCardWithMod());
        ArrayList<Integer> straightArray = new ArrayList<Integer>();
        boolean isStraight = false;
        for (int i = 0; i < allCards.size(); i++) {
            if (straightArray.size() == 0) {
                straightArray.add(allCards.get(i));
            } else {
                int straightListItemMod = straightArray.get(straightArray.size() - 1) % 13;
                int mod1 = straightListItemMod == 0 ? 13 : straightListItemMod;
                int cardMod = allCards.get(i) % 13 == 0 ? 13 : allCards.get(i) % 13;
                if ((mod1 - 1) == cardMod) {
                    straightArray.add(allCards.get(i));
                } else {
                    if (straightArray.size() >= 5) {
                        break;
                    } else {
                        if (mod1 != cardMod) {
                            straightArray.clear();
                            straightArray.add(allCards.get(i));
                        }
                    }


                }
            }
            if (aceCardNo != 0 && straightArray.size() == 4
                    && (straightArray.get(straightArray.size() - 1) % 13 == 2 || straightArray.get(0) % 13 == 0)) {
                isStraight = true;
                break;
            }
        }
        if (straightArray.size() >= 5) {
            isStraight = true;
        }

        if (isStraight) {
            if (aceCardNo != 0) {
                if (straightArray.get(0) % 13 == 0) {
                    straightArray.add(0, aceCardNo);
                    return new BestPossibleCombination(straightArray, PokerUtils.userId, PokerCombinations.STRAIGHT);
                } else if (straightArray.get(straightArray.size() - 1) % 13 == 2) {
                    while (straightArray.size() > 5) {
                        straightArray.remove(straightArray.size() - 1);
                    }
                    if (straightArray.size() == 5) {
                        return new BestPossibleCombination(straightArray, PokerUtils.userId, PokerCombinations.STRAIGHT);
                    } else {
                        straightArray.add(aceCardNo);
                        return new BestPossibleCombination(straightArray, PokerUtils.userId, PokerCombinations.STRAIGHT);
                    }
                } else {
                    while (straightArray.size() > 5) {
                        straightArray.remove(straightArray.size() - 1);
                    }
                    return new BestPossibleCombination(straightArray, PokerUtils.userId, PokerCombinations.STRAIGHT);
                }
            } else {
                while (straightArray.size() > 5) {
                    straightArray.remove(straightArray.size() - 1);
                }
                return new BestPossibleCombination(straightArray, PokerUtils.userId, PokerCombinations.STRAIGHT);
            }
        }

        return null;
    }

    private static BestPossibleCombination checkForThreeOfKind(ArrayList<PairCard> pairCards,
                                                               ArrayList<Integer> remainCard, int aceCardNo) {
        if (aceCardNo != 0) {
            remainCard.remove(new Integer(aceCardNo));
        }
        Collections.sort(remainCard, new SortCardWithMod());
        ArrayList<Integer> cards = new ArrayList<Integer>();
        cards.addAll(pairCards.get(0).getCards());
        if (aceCardNo != 0) {
            cards.add(aceCardNo);
            cards.add(remainCard.get(0));
        } else {
            cards.add(remainCard.get(0));
            cards.add(remainCard.get(1));
        }

        BestPossibleCombination possibleCombination = new BestPossibleCombination(cards, PokerUtils.userId, PokerCombinations.THREE_OF_A_KIND);
        possibleCombination.setHighPairCard(cards.get(0));
        return possibleCombination;
    }

    private static BestPossibleCombination checkForTwoPair(ArrayList<PairCard> pairCards,
                                                           ArrayList<Integer> remainCard, int aceCardNo) {
        if (aceCardNo != 0) {
            remainCard.remove(new Integer(aceCardNo));
        }
        PairCard firstCardGroup = null, secondGroup = null;
        int firstGroupMod = 0;
        int secondGroupMod = 0;

        for (PairCard pairCard : pairCards) {
            int mod = pairCard.getCards().get(0) % 13;
            int cardMod = mod == 0 ? 13 : mod;
            if (pairCard.getCards().size() == 2 && (cardMod > firstGroupMod || cardMod == 1)) {
                if (firstGroupMod != 1) {
                    firstGroupMod = cardMod;
                    firstCardGroup = pairCard;
                } else {
                    break;
                }
            }
        }

        for (PairCard pairCard : pairCards) {
            int mod = pairCard.getCards().get(0) % 13;
            int cardMod = mod == 0 ? 13 : mod;
            if (pairCard.getCards().size() == 2
                    && (cardMod > secondGroupMod || cardMod == 1)
                    && cardMod != firstGroupMod) {
                if (secondGroupMod != 1) {
                    secondGroupMod = cardMod;
                    secondGroup = pairCard;
                } else {
                    break;
                }
            }
        }
        pairCards.remove(firstCardGroup);
        pairCards.remove(secondGroup);
        if (pairCards.size() > 0) {
            remainCard.add(pairCards.get(0).getCards().get(0));
        }
        Collections.sort(remainCard, new SortCardWithMod());
        ArrayList<Integer> twoPairCards = new ArrayList<Integer>();
        twoPairCards.addAll(firstCardGroup.getCards());
        twoPairCards.addAll(secondGroup.getCards());
        if (aceCardNo != 0) {
            twoPairCards.add(aceCardNo);
        } else {
            twoPairCards.add(remainCard.get(0));
        }
        BestPossibleCombination bestPossibleCombination = new BestPossibleCombination(twoPairCards, PokerUtils.userId, PokerCombinations.TWO_PAIR);
        bestPossibleCombination.setHighPairCard(firstCardGroup.getCards().get(0));
        bestPossibleCombination.setLowPairCard(secondGroup.getCards().get(0));
        return bestPossibleCombination;
    }

    private static BestPossibleCombination checkForOnePair(ArrayList<PairCard> pairCards,
                                                           ArrayList<Integer> remainCard, int aceCardNo) {

        if (aceCardNo != 0) {
            remainCard.remove(new Integer(aceCardNo));
        }

        Collections.sort(remainCard, new SortCardWithMod());
        ArrayList<Integer> onePairCards = new ArrayList<Integer>();
        onePairCards.addAll(pairCards.get(0).getCards());

        if (aceCardNo != 0) {
            onePairCards.add(aceCardNo);
            onePairCards.add(remainCard.get(0));
            onePairCards.add(remainCard.get(1));
        } else {
            onePairCards.add(remainCard.get(0));
            onePairCards.add(remainCard.get(1));
            onePairCards.add(remainCard.get(2));
        }

        BestPossibleCombination possibleCombination = new BestPossibleCombination(onePairCards, PokerUtils.userId, PokerCombinations.ONE_PAIR);
        possibleCombination.setHighPairCard(pairCards.get(0).getCards().get(0));
        return possibleCombination;
    }

    private static BestPossibleCombination checkForHighCard(ArrayList<Integer> remainCard, int aceCardNo) {
        if (aceCardNo != 0) {
            remainCard.remove(new Integer(aceCardNo));
        }

        Collections.sort(remainCard, new SortCardWithMod());
        ArrayList<Integer> highCards = new ArrayList<Integer>();

        if (aceCardNo != 0) {
            highCards.add(aceCardNo);
            highCards.add(remainCard.get(0));
            highCards.add(remainCard.get(1));
            highCards.add(remainCard.get(2));
            highCards.add(remainCard.get(3));
        } else {
            highCards.add(remainCard.get(0));
            highCards.add(remainCard.get(1));
            highCards.add(remainCard.get(2));
            highCards.add(remainCard.get(3));
            highCards.add(remainCard.get(4));
        }

        return new BestPossibleCombination(highCards, PokerUtils.userId, PokerCombinations.HIGH_CARD);
    }

    private static boolean isHavingFlush(ArrayList<Integer> cardGroups) {
        return (cardGroups == null || cardGroups.size() < 5) ? false : true;
    }

    static class PairCard {
        ArrayList<Integer> cards;

        PairCard() {
            cards = new ArrayList<Integer>();
        }

        public ArrayList<Integer> getCards() {
            return cards;
        }

        public void addCards(int card) {
            this.cards.add(card);
        }
    }

    public static ArrayList<BestPossibleCombination> getWinner(ArrayList<BestPossibleCombination> bestPossibleCombinations) {
        if (bestPossibleCombinations == null || bestPossibleCombinations.size() < 2) {
            throw new IllegalArgumentException("bestPossibleCombinations is required and size > 1.");
        }
        ArrayList<BestPossibleCombination> winners = new ArrayList<BestPossibleCombination>();
        ArrayList<BestPossibleCombination> possibleWinners = new ArrayList<BestPossibleCombination>();
        Collections.sort(bestPossibleCombinations, new SortCardWithGroupType());

        for (int i = 0; i < bestPossibleCombinations.size(); i++) {
            if (i + 1 < bestPossibleCombinations.size()) {
                if (bestPossibleCombinations.get(i).getGroupType() == bestPossibleCombinations.get(i + 1).getGroupType()) {
                    possibleWinners.add(bestPossibleCombinations.get(i));
                } else {
                    if (possibleWinners.size() > 0) {
                        possibleWinners.add(bestPossibleCombinations.get(i));
                    } else {
                        winners.add(bestPossibleCombinations.get(i));
                    }
                    break;
                }
            } else {
                possibleWinners.add(bestPossibleCombinations.get(i));
            }
        }
        if (possibleWinners.size() > 1) {
            if (possibleWinners.get(0).getGroupType() == PokerCombinations.FOUR_OF_A_KIND) {
                ArrayList<BestPossibleCombination> highCardWinners = getWinnersOnHighPairCard(possibleWinners);
                if (highCardWinners.size() > 1) {
                    return getWinnersOnCardWeight(highCardWinners);
                } else {
                    return highCardWinners;
                }
            }
            if (possibleWinners.get(0).getGroupType() == PokerCombinations.THREE_OF_A_KIND) {
                ArrayList<BestPossibleCombination> highCardWinners = getWinnersOnHighPairCard(possibleWinners);
                if (highCardWinners.size() > 1) {
                    return getWinnersOnCardWeight(highCardWinners);
                } else {
                    return highCardWinners;
                }
            }

            if (possibleWinners.get(0).getGroupType() == PokerCombinations.ONE_PAIR) {
                ArrayList<BestPossibleCombination> highCardWinners = getWinnersOnHighPairCard(possibleWinners);
                if (highCardWinners.size() > 1) {
                    return getWinnersOnCardWeight(highCardWinners);
                } else {
                    return highCardWinners;
                }
            }

            if (possibleWinners.get(0).getGroupType() == PokerCombinations.FULL_HOUSE) {
                ArrayList<BestPossibleCombination> highCardWinners = getWinnersOnHighPairCard(possibleWinners);
                if (highCardWinners.size() > 1) {
                    ArrayList<BestPossibleCombination> lowCardWinners = getWinnersOnLowPairCard(highCardWinners);
                    if (lowCardWinners.size() > 1) {
                        return getWinnersOnCardWeight(lowCardWinners);
                    } else {
                        return lowCardWinners;
                    }
                } else {
                    return highCardWinners;
                }
            }

            if (possibleWinners.get(0).getGroupType() == PokerCombinations.TWO_PAIR) {
                ArrayList<BestPossibleCombination> highCardWinners = getWinnersOnHighPairCard(possibleWinners);
                if (highCardWinners.size() > 1) {
                    ArrayList<BestPossibleCombination> lowCardWinners = getWinnersOnLowPairCard(highCardWinners);
                    if (lowCardWinners.size() > 1) {
                        return getWinnersOnCardWeight(lowCardWinners);
                    } else {
                        return lowCardWinners;
                    }
                } else {
                    return highCardWinners;
                }
            }
            Collections.sort(possibleWinners, new SortCardWithCardsWeight());
            return getWinnersOnCardWeight(possibleWinners);
        }
        return winners;
    }


    static ArrayList<BestPossibleCombination> getWinnersOnCardWeight(ArrayList<BestPossibleCombination> possibleWinners) {
        ArrayList<BestPossibleCombination> winners = new ArrayList<BestPossibleCombination>();
        Collections.sort(possibleWinners, new SortCardWithCardsWeight());
        for (int i = 0; i < possibleWinners.size(); i++) {
            boolean isWinner = true;
            BestPossibleCombination winner = possibleWinners.get(i);
            Collections.sort(winner.arrCardWeight, new SortCard());
            for (int j = 0; j < possibleWinners.size(); j++) {
                BestPossibleCombination winner1 = possibleWinners.get(j);
                Collections.sort(winner1.arrCardWeight, new SortCard());
                if (i != j) {
                    for (int k = 0; k < 5; k++) {
                        if (winner.arrCardWeight.get(k) < winner1.arrCardWeight.get(k)) {
                            isWinner = false;
                            break;
                        } else if (winner.arrCardWeight.get(k) > winner1.arrCardWeight.get(k)) {
                            isWinner = true;
                            break;
                        }
                    }
                }
                if (!isWinner) {
                    break;
                }
            }
            if (isWinner) {
                winners.add(winner);
            }
        }
        return winners;
    }

    static ArrayList<BestPossibleCombination> getWinnersOnHighPairCard(ArrayList<BestPossibleCombination> possibleWinners) {
        ArrayList<BestPossibleCombination> winners = new ArrayList<BestPossibleCombination>();
        Collections.sort(possibleWinners, new SortCardWithHighPairCard());
        for (int i = 0; i < possibleWinners.size(); i++) {
            if (i + 1 < possibleWinners.size()) {
                if (possibleWinners.get(i).getHighPairCard() == possibleWinners.get(i + 1).getHighPairCard()) {
                    winners.add(possibleWinners.get(i));
                } else {
                    winners.add(possibleWinners.get(i));
                    break;
                }
            } else {
                winners.add(possibleWinners.get(i));
            }
        }

        return winners;
    }

    static ArrayList<BestPossibleCombination> getWinnersOnLowPairCard(ArrayList<BestPossibleCombination> possibleWinners) {
        ArrayList<BestPossibleCombination> winners = new ArrayList<BestPossibleCombination>();
        Collections.sort(possibleWinners, new SortCardWithLowPairCard());
        for (int i = 0; i < possibleWinners.size(); i++) {
            if (i + 1 < possibleWinners.size()) {
                if (possibleWinners.get(i).getLowPairCard() == possibleWinners.get(i + 1).getLowPairCard()) {
                    winners.add(possibleWinners.get(i));
                } else {
                    winners.add(possibleWinners.get(i));
                    break;
                }
            } else {
                winners.add(possibleWinners.get(i));
            }
        }

        return winners;
    }

    static class SortCardWithGroupType implements Comparator<BestPossibleCombination> {
        public int compare(BestPossibleCombination a, BestPossibleCombination b) {
            return b.getGroupType() - a.getGroupType();
        }
    }

    static class SortCardWithCardsWeight implements Comparator<BestPossibleCombination> {
        public int compare(BestPossibleCombination a, BestPossibleCombination b) {
            return b.getCardsWeight() - a.getCardsWeight();
        }
    }

    static class SortCardWithHighPairCard implements Comparator<BestPossibleCombination> {
        public int compare(BestPossibleCombination a, BestPossibleCombination b) {
            return b.getHighPairCard() - a.getHighPairCard();
        }
    }

    static class SortCardWithLowPairCard implements Comparator<BestPossibleCombination> {
        public int compare(BestPossibleCombination a, BestPossibleCombination b) {
            return b.getLowPairCard() - a.getLowPairCard();
        }
    }

    public static float getValue(float input) {
        return Math.round(input * 100) / 100.0f;
    }

    public static boolean contains(String[] array, String s) {
        for (int i = 0; i < array.length; i++) {
            if (s.matches(array[i])) {
                return true;
            }
        }
        return false;
    }

    public static String getTimer(long time) {
        return getTimer(time, " ");
    }


    public static String getTimer(long time, String separator) {
        return getTimer(time, separator, true);
    }

    public static String getTimer(long time, String separator, boolean h) {
        String timeText;
        long days = 0, hours = 0, minutes = 0, seconds = 0;
        if (time > 0) {
            days = TimeUnit.MILLISECONDS.toDays(time);
            time -= TimeUnit.DAYS.toMillis(days);
            hours = TimeUnit.MILLISECONDS.toHours(time);
            time -= TimeUnit.HOURS.toMillis(hours);
            minutes = TimeUnit.MILLISECONDS.toMinutes(time);
            time -= TimeUnit.MINUTES.toMillis(minutes);
            seconds = TimeUnit.MILLISECONDS.toSeconds(time);
            if (days > 0) {
                timeText = getAppendedString(days) + "D" + separator + getAppendedString(hours) + "H" + separator + getAppendedString(minutes) + "M" + separator + getAppendedString(seconds) + "S";
            } else {
                if (h) {
                    timeText = getAppendedString(hours) + "H" + separator + getAppendedString(minutes) + "M" + separator + getAppendedString(seconds) + "S";
                } else {
                    timeText = getAppendedString(minutes) + "M" + separator + getAppendedString(seconds) + "S";
                }
            }

        } else {
            timeText = "00H 00M 00S";
        }
        return timeText;
    }

    private static String getAppendedString(long timeUnit) {
        return timeUnit < 10 ? "0" + timeUnit : timeUnit + "";
    }


    public static boolean isValidEmail(String emailId) {
        final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        return emailId.matches(EMAIL_PATTERN);
    }

    public static boolean isValidPassword(String password) {
        final String PASSWORD_PATTERN = "^(?=.*[a-zA-Z])(?=.*[0-9]).{8,15}";
        return password.matches(PASSWORD_PATTERN);
    }

    public static boolean isValidUserName(String userName) {
        final String USER_NAME_PATTERN = "[a-zA-Z ]+";
        return userName.matches(USER_NAME_PATTERN);
    }

    public static boolean isValidDOB(String dob) {
        final String DOB_PATTERN = "^([0-2][0-9]|(3)[0-1])(\\/)(((0)[0-9])|((1)[0-2]))(\\/)\\d{4}$";
        return dob.matches(DOB_PATTERN);
    }

    public static boolean isValidMobileNumber(String mobileNo) {
        return mobileNo.matches("\\d{10}");
    }

    public static boolean isValidPanNumber(String panNumber, String lastName) {
        final String USER_NAME_PATTERN = "[a-zA-Z]{5}[0-9]{4}[a-zA-Z]{1}";
        if (panNumber.length() != 10) {
            return false;
        }
        return panNumber.matches(USER_NAME_PATTERN);
    }

    public static native String

    encrypt(String value,String key)/*-{
        return $wnd.encrypt(value,key);
    }-*/;

    public static native String decrypt(String value,String key)/*-{
        return $wnd.decrypt(value,key);
    }-*/;

    public static native String compress(String value)/*-{

    }-*/;

    public static native String decompress(String value)/*-{
        return $wnd.decompress(value);
    }-*/;



    public static native boolean isNetworkConnected()/*-{
        return $wnd.navigator.onLine;
    }-*/;

    public static String encryptValue(String transactionId, String value) {
        String encryptValue = "";
        try {
            encryptValue = encrypt(value, GamePreferences.instance().getOtp().substring(0, 3) + transactionId);
        } catch (Exception e) {
        }
        return encryptValue;
    }

    public static native void getLocation(LocationHandler locationHandler)/*-{
        $wnd.onSuccessLocation=$entry(function(position){
            console.log(position);
            locationHandler.@bigcash.poker.network.LocationHandler::setSuccess(Lbigcash/poker/utils/GeolocationPosition;)(position);
        });

        $wnd.onErrorLocation=$entry(function(error){
            console.log(error);
            locationHandler.@bigcash.poker.network.LocationHandler::setFailed()();
        });

        if($wnd.navigator.geolocation){
             $wnd.navigator.geolocation.getCurrentPosition($wnd.onSuccessLocation,$wnd.onErrorLocation);
        }else{
            locationHandler.@bigcash.poker.network.LocationHandler::setFailed()();
        }
    }-*/;


    public static native void setTimeOut(int time,TimeoutHandler handler)/*-{
        setTimeout(function(){
            handler.@bigcash.poker.utils.TimeoutHandler::onTimeOut()();
        },time);
    }-*/;

    public static void openFullScreen(TimeoutHandler timeoutHandler){
        try {
            openFullScreen();
        }catch (Exception e){

        }
        setTimeOut(1500,timeoutHandler);
    }

    private static native void openFullScreen() throws JavaScriptException/*-{
        $wnd.openFullScreen();
    }-*/;

    public static void closeFullScreen(TimeoutHandler timeoutHandler){
        try {
            closeFullScreen();
        }catch (Exception e){

        }
        setTimeOut(1500,timeoutHandler);
    }

    private static native void closeFullScreen() throws JavaScriptException/*-{
         $wnd.exitFullScreen();
    }-*/;

    public static native void setScreen(String screenName)/*-{
        $wnd.screenChanged=true;
        $wnd.previousScreen=$wnd.location.hash;
        $wnd.currentScreen=screenName;
        $wnd.location.hash=screenName;
    }-*/;


    public static native void catchBackKey(PokerGame pokerGame)/*-{
        $wnd.onhashchange=function(){
            if($wnd.screenChanged){
                $wnd.screenChanged=false;
                console.log("Reached 145");
            }else{
                var screen=$wnd.location.hash.replace("#","");
                var previousScreen=$wnd.previousScreen.replace("#","");
                if(screen==previousScreen){
                    $wnd.location.hash=$wnd.currentScreen;
                    $wnd.screenChanged=false;
                    pokerGame.@bigcash.poker.gwt.PokerGame::onBackKeyPressed()();
                    console.log("Reached 148");
                }
            }
        };
    }-*/;


    public static native void addFullscreenEventListener()/*-{
        var doc=$wnd.document;
        doc.addEventListener("fullscreenchange", function() {
            console.log("fullscreenchange");
            if($wnd.onFullscreen){
                $wnd.onFullscreen();
                $wnd.onFullscreen=null;
            }
        });
        doc.addEventListener("mozfullscreenchange", function() {
            console.log("mozfullscreenchange");
            if($wnd.onFullscreen){
                $wnd.onFullscreen();
                $wnd.onFullscreen=null;
            }
        });
        doc.addEventListener("webkitfullscreenchange", function() {
            console.log("webkitfullscreenchange");
            if($wnd.onFullscreen){
                $wnd.onFullscreen();
                $wnd.onFullscreen=null;
            }
        });
        doc.addEventListener("msfullscreenchange", function() {
            console.log("mozfullscreenchange");
            if($wnd.onFullscreen){
                $wnd.onFullscreen();
                $wnd.onFullscreen=null;
            }
        });

        doc.addEventListener("fullscreenerror", function() {
            console.log("fullscreenerror");
            if($wnd.onFullscreen){
                $wnd.onFullscreen();
                $wnd.onFullscreen=null;
            }
        });
        doc.addEventListener("mozfullscreenerror", function() {
            console.log("mozfullscreenerror");
            if($wnd.onFullscreen){
                $wnd.onFullscreen();
                $wnd.onFullscreen=null;
            }
        });
        doc.addEventListener("webkitfullscreenerror", function() {
            console.log("webkitfullscreenerror");
            if($wnd.onFullscreen){
                $wnd.onFullscreen();
                $wnd.onFullscreen=null;
            }
        });
        doc.addEventListener("msfullscreenerror", function() {
            console.log("msfullscreenerror");
            if($wnd.onFullscreen){
                $wnd.onFullscreen();
                $wnd.onFullscreen=null;
            }
        });
    }-*/;


    public static float devicePixelRatio(){
        return (float) GwtGraphics.getNativeScreenDensity();
    }

    public static int convertIntoDevicePixel(float size){
        return (int)(size/devicePixelRatio());
    }


    public static native boolean isMobile()/*-{
        var userAgent=$wnd.navigator.userAgent;
         if (userAgent.match(/Android/i)
                || userAgent.match(/webOS/i)
                || userAgent.match(/iPhone/i)
                || userAgent.match(/iPad/i)
                || userAgent.match(/iPod/i)
                || userAgent.match(/BlackBerry/i)
                || userAgent.match(/Windows Phone/i)) {
                console.log("Mobile Browser");
                return true;
            } else {
                console.log("Desktop Browser");
                return false;
            }
    }-*/;


    public static void inviteOnWhatsapp(String inviteText){
        try {
            Gdx.app.error("Invite Text", inviteText);
            Window.open("whatsapp://send?text="+ URLEncoder.encode(inviteText,"UTF-8"),"","_blank");
        }catch (Exception e){

        }
    }

    public static void shareOnFacebook(String inviteText){
        try {
            String url="https://www.facebook.com/sharer/sharer.php?u="+URLEncoder.encode(inviteText,"UTF-8");
            Window.open(url,"","_blank");
        }catch (Exception e){

        }
    }

    public static void shareOnTwitter(String inviteText){
        try {
            String url="https://twitter.com/intent/tweet?text="+URLEncoder.encode(inviteText,"UTF-8");
            Window.open(url,"","_blank");
        }catch (Exception e){

        }
    }


    public static Map<String, String> getQueryMap(String query) {
        String[] params = query.split("&");
        Map<String, String> map = new HashMap<String, String>();

        for (String param : params) {
            String name = param.split("=")[0];
            String value = param.split("=")[1];
            map.put(name, value);
        }
        return map;
    }

    public static void openUrl(String url){
        Window.open(url,"_blank","");
    }

    public static native String formatValue(float value)/*-{
        return new Number(value).toLocaleString("en-IN",{minimumFractionDigits:0});
    }-*/;


    public static boolean setAppData(){
        CookieData cookieData=CookieData.getCookieData();
        if (cookieData==null)return false;
        String appLaunchResponse=CookieData.getAppLaunchResponse();
        if (appLaunchResponse==null)return false;
        GamePreferences preferences=GamePreferences.instance();
        preferences.setOtp(cookieData.getOTP());
        preferences.setUserId(cookieData.getUserId());
        String zipResult = ApiHandler.parseToGetZipData(appLaunchResponse);
        String result = "";
        try {
            result = PokerUtils.decompress(zipResult);
        } catch (Exception e) {

        }
        Gdx.app.error("AppLaunchResponse",result);
        if (result.matches("")){
            return false;
        }
        ApiHandler.parseAppLaunchResponse(result, false);
        return true;
    }




    public static native void resetApp()/*-{
        $wnd.resetApp();
    }-*/;



}
