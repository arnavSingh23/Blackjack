import java.util.*;

public class Player {
    /*
     * Instance fields
     */
    private final String name;
    private final List<Card> hand;

    /*
     * Constructor
     */
    public Player(String name) {
        this.name = name;
        this.hand = new ArrayList<>();
    }

    public List<Card> getHand() {
        return this.hand;
    }

    /*
     * Returns the player's name
     */
    public String getName() {
        return this.name;
    }

    /*
     * Returns the current number of cards in the player's hand.
     */
    public int getNumCards() {
        return this.hand.size();
    }

    /*
     * Returns the player's name
     */
    public String toString() {
        return this.name;
    }

    /*
     * Takes a Card object as a parameter and adds the specified card to the
     * player's hand.
     */
    public void addCard(Card card) {
        if (card == null) {
            throw new IllegalArgumentException("Cannot add a null card.");
        }
        hand.add(card);
    }

    /*
     * Takes an integer index as a parameter and returns the Card at the specified
     * position in the player's hand, without actually removing the card from the
     * hand.
     */
    public Card getCard(int index) {
        if (index >= 0 && index < hand.size()) {
            return hand.get(index);
        } else {
            throw new IllegalArgumentException("Invalid card index.");
        }
    }

    /*
     * Computes and returns the total value of the player's current hand – i.e., the
     * sum of the values of the individual cards.
     */
    public int getHandValue() {
        int value = 0;
        int numAces = 0;

        for (Card card : hand) {
            value += card.getValue();
            if (card.isAce()) {
                numAces++;
            }
        }

        while (value > 21 && numAces > 0) {
            value -= 10;
            numAces--;
        }

        return value;
    }

    /*
     * Prints the current contents of the player's hand, followed by the value of
     * the player's hand.
     */
    public void printHand() {
        StringBuilder sb = new StringBuilder();

        for (Card card : hand) {
            sb.append(card.toString()).append("  ");
        }

        System.out.println(sb + "(value = " + getHandValue() + ")");
    }

    /*
     * Returns true if the player has Blackjack (a two-card hand with a value of
     * 21), and false otherwise.
     */
    public boolean hasBlackjack() {
        return hand.size() == 2 && getHandValue() == 21;
    }

    /*
     * Returns true if the player wants another hit, and false if the player does
     * not want another hit.
     */
    public boolean wantsHit(Scanner console, Player dealer) {
        System.out.print("Want another hit, " + name + " (y/n)? ");
        String want = console.nextLine();

        return want.equalsIgnoreCase("y");
    }

    /*
     * Gets rid of all the cards in the player's hand, to prepare for a new round
     * of the game.
     */
    public void discardCards() {
        hand.clear();
    }
}
