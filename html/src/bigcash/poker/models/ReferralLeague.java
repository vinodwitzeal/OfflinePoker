package bigcash.poker.models;


import com.badlogic.gdx.utils.Array;

import java.util.HashMap;

public class ReferralLeague {
    private ReferralPlayerDto myDetail;
    private final HashMap<Integer,ReferralPlayerDto> referralPlayerDtos = new HashMap<Integer, ReferralPlayerDto>();
    private final Array<PrizeRules> prizeRules = new Array<PrizeRules>();

    public ReferralPlayerDto getMyDetail() {
        return myDetail;
    }

    public void setMyDetail(ReferralPlayerDto myDetail) {
        this.myDetail = myDetail;
    }

    public HashMap<Integer,ReferralPlayerDto> getReferralPlayerDtos() {
        return referralPlayerDtos;
    }

    public void addReferralPlayerDtos(ReferralPlayerDto referralPlayerDto) {
        referralPlayerDtos.put(referralPlayerDto.getUserRank(),referralPlayerDto);
    }

    public void clearCasualPlayerDtos(){
        referralPlayerDtos.clear();
    }

    public Array<PrizeRules> getPrizeRules() {
        return prizeRules;
    }

    public void addPrizeRules(PrizeRules prizeRule) {
        prizeRules.add(prizeRule);
    }
}
