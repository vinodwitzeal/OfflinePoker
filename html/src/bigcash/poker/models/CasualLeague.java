package bigcash.poker.models;

import java.util.HashMap;

public class CasualLeague {
    private float totalWinningAmount;
    private CasualPlayerDto myDetail;
    private final HashMap<Integer,CasualPlayerDto> casualPlayerDtos = new HashMap<Integer, CasualPlayerDto>();

    public CasualPlayerDto getMyDetail() {
        return myDetail;
    }

    public void setMyDetail(CasualPlayerDto myDetail) {
        this.myDetail = myDetail;
    }

    public HashMap<Integer,CasualPlayerDto> getCasualPlayerDtos() {
        return casualPlayerDtos;
    }

    public void addCasualPlayerDtos(CasualPlayerDto casualPlayerDto) {
        casualPlayerDtos.put(casualPlayerDto.getUserRank(),casualPlayerDto);
    }

    public void clearCasualPlayerDtos(){
        casualPlayerDtos.clear();
    }

    public float getTotalWinningAmount() {
        return totalWinningAmount;
    }

    public void setTotalWinningAmount(float totalWinningAmount) {
        this.totalWinningAmount = totalWinningAmount;
    }
}
