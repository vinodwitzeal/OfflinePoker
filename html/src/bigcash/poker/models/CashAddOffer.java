package bigcash.poker.models;

public class CashAddOffer {
    private int addCash, lives;

    public CashAddOffer(int addCash, int lives) {
        this.addCash = addCash;
        this.lives = lives;
    }

    public int getAddCash() {
        return addCash;
    }

    public void setAddCash(int addCash) {
        this.addCash = addCash;
    }

    public int getLives() {
        return lives;
    }

    public void setLives(int lives) {
        this.lives = lives;
    }
}
