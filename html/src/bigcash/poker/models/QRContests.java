package bigcash.poker.models;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class QRContests {
    private int qrContestId;
    private LinkedHashMap<Integer, List<PokerContest>> matchedContests;
    private LinkedHashMap<Integer, List<PokerContest>> allContests;
    public QRContests(int qrContestId){
        this.qrContestId=qrContestId;
        this.matchedContests=new LinkedHashMap<Integer, List<PokerContest>>();
        this.allContests=new LinkedHashMap<Integer, List<PokerContest>>();
    }
    public void addContest(PokerContest contest){
        int maxPlayers=contest.getMaxUsersPerTable();
        if (contest.getContestId()==qrContestId){
            addContest(maxPlayers,matchedContests,contest);
        }
        addContest(maxPlayers,allContests,contest);
    }

    private void addContest(int maxPlayer,LinkedHashMap<Integer,List<PokerContest>> contestMap,PokerContest contest){
        if (!contestMap.containsKey(maxPlayer)){
           contestMap.put(maxPlayer,new ArrayList<PokerContest>());
        }
        contestMap.get(maxPlayer).add(contest);
    }

    public LinkedHashMap<Integer, List<PokerContest>> getMatchedContests() {
        return matchedContests;
    }

    public LinkedHashMap<Integer, List<PokerContest>> getAllContests() {
        return allContests;
    }
}
