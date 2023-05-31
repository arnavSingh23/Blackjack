import java.util.*;

public class Dealer extends Player {
    /*
     * New instance field for the dealer class
     */
    private boolean revealCard;

    /*
     * Constructor
     */
    public Dealer() {
        super("dealer");
        this.revealCard = false;
    }

    /*
     * Takes no parameters and changes the value of the called object’s boolean
     * field to indicate that the dealer’s first card should now be revealed.
     */
    public void revealFirstCard() {
        this.revealCard = true;
    }

    /*
     * If the boolean field indicates that the first card should not be revealed,
     * then it should display the String “XX” in place of the first card’s usual
     * string.
     * If the boolean field indicates that the first card should be displayed, then
     * this method should produce the same output as the superclass version of the
     * method.
     */
    public void printHand() {

        StringBuilder x = new StringBuilder("XX  ");
        if (!this.revealCard) {
            for (int i = 1; i < this.getNumCards(); i++) {
                x.append(getCard(i).toString()).append("  ");
            }

            System.out.println(x);
        }

        else {
            StringBuilder str = new StringBuilder();

            for (int i = 0; i < getNumCards(); i++) {
                str.append(getCard(i).toString()).append("  ");

            }

            System.out.println(str + "(value = " + getHandValue() + ")");
        }

    }

    /*
     * Determine if the dealer should give herself another hit, and return true or
     * false accordingly.
     */
    public boolean wantsHit(Scanner console, Player othPlayer) {

        if (othPlayer.getHandValue() < 17 && this.getHandValue() <= othPlayer.getHandValue()) {
            return true;
        }

        else if (othPlayer.getHandValue() >= 17 && this.getHandValue() < othPlayer.getHandValue()) {
            return true;
        }

        else {
            return false;
        }
    }

    /*
     * This version of the method should have the same effect as the inherited
     * version of the method.
     * In addition, it should reset the called object’s boolean field to its
     * original value, so that the first card in the dealer’s next hand will not be
     * revealed when the hand is first printed.
     */
    public void discardCards() {
        super.discardCards();
        this.revealCard = false;
    }
}