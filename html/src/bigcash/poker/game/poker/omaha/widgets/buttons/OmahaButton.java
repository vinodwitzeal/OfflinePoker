package bigcash.poker.game.poker.omaha.widgets.buttons;

import com.badlogic.gdx.scenes.scene2d.ui.Table;

import bigcash.poker.utils.PokerUtils;

public class OmahaButton extends Table {
    public static final int NO_ACTION=0,UNCHECKED=1,CHECKED=2;
    private int state;
    private int id;
    private float amount;
    public void setState(int state){
        this.state=state;
    }

    public int getState(){
        return state;
    }

    public int getId(){
        return id;
    }

    protected  void setId(int id){
        this.id=id;
    }

    public void updateButton(int state,float amount){

    }

    public boolean updateButton(boolean userTurn,boolean betPlaced,float balance,float betAmount,float maxBet){


        return false;
    }

    public boolean processButton(boolean userTurn,boolean betPlaced,float balance,float betAmount,float maxBet){
        return false;
    }

    public float getAmount(){
        return PokerUtils.getValue(amount);
    }

    public void setAmount(float amount){
        this.amount= PokerUtils.getValue(amount);
    }


}
