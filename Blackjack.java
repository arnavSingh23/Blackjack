/*
 * Blackjack.java
 * 
 * Computer Science 112, Boston University
 * 
 * The main class for a program that plays the game of Blackjack.
 * Will not compile and run until after you have completed Tasks 1 and 2.
 *
 * modified by: Arnav Singh, asingh23@bu.edu
 */

import java.util.*;

public class Blackjack {
    // the largest number of cards that each player could have
    public static int MAX_CARDS_PER_PLAYER = 11;

    public static void main(String[] args) {
        Scanner console = new Scanner(System.in);
        System.out.println("Welcome to the game of Blackjack!");
        System.out.print("What's your first name? ");
        String name = console.nextLine();

        Deck deck = initializeDeck(args);
        Player dealer = new Dealer();
        Player user = new Player(name);

        String choice;
        do {
            System.out.println();
            System.out.println("---------------------------------");
            playRound(dealer, user, console, deck);

            System.out.print("Play another round (y/n)? ");
            choice = console.nextLine();

            if (deck.getCardsLeft() < 2 * MAX_CARDS_PER_PLAYER) {
                deck.reset();
                deck.shuffle();
            }
        } while (choice.equalsIgnoreCase("y"));
    }

    /*
     * initializeDeck - creates, shuffles, and returns a
     * new deck of cards. If the user has specified a random
     * seed in the command-line args (the ones passed as
     * parameters to the main method), we use that seed when
     * creating the Deck object.
     */
    public static Deck initializeDeck(String[] args) {
        int seed = -1;
        if (args.length > 0) {
            seed = Integer.parseInt(args[0]);
        }
        Deck deck = new Deck(seed);
        deck.shuffle();
        return deck;
    }

    /*
     * dealCard - take a card from the specified deck and
     * give it to the specified player.
     */
    public static void dealCard(Deck deck, Player player) {
        if (deck.isEmpty()) {
            deck.reset();
            deck.shuffle();
        }

        player.addCard(deck.dealCard());
    }

    /*
     * playRound - play a single round of the game, using the
     * specified players, the specified Scanner for the console,
     * and the specified deck.
     */
    public static void playRound(Player dealer, Player user, Scanner console, Deck deck) {
        // deal the initial cards
        dealCard(deck, dealer);
        dealCard(deck, dealer);
        dealCard(deck, user);
        dealCard(deck, user);
        displayHands(dealer, user);

        // user's turn
        while (user.getHandValue() < 21 && user.wantsHit(console, dealer)) {
            dealCard(deck, user);
            displayHands(dealer, user);
        }

        System.out.println("revealing the dealer's first card:");
        ((Dealer) dealer).revealFirstCard();
        displayHands(dealer, user);

        // dealer's turn -- only happens if the user has not gone over
        if (user.getHandValue() <= 21) {
            while (dealer.getHandValue() < 21 && dealer.wantsHit(console, user)) {
                System.out.println("dealer takes a hit:");
                dealCard(deck, dealer);
                displayHands(dealer, user);
            }
        }

        printResult(dealer, user);

        dealer.discardCards();
        user.discardCards();
    }

    /*
     * printResult - prints the result of a round of the game,
     * given the specified player and dealer hands.
     */
    public static void printResult(Player dealer, Player user) {

        if (user.getHandValue() > 21) {
            System.out.println("You lose, " + user.getName() + ".");
        }

        else if (user.getHandValue() == 21 && dealer.getHandValue() != 21) {
            System.out.println("Blackjack!");
        }

        else if (user.getHandValue() == dealer.getHandValue()) {
            System.out.println("Push!");
        }

        else if (dealer.getHandValue() > 21) {
            System.out.println("You win, " + user.getName() + "!");
        }

        else if (dealer.getHandValue() > user.getHandValue()) {
            System.out.println("You lose, " + user.getName() + ".");
        }

        else {
            System.out.println("You win, " + user.getName() + "!");
        }
    }

    /*
     * displayHands - display the current contents of the specified
     * players' hands
     */
    public static void displayHands(Player dealer, Player user) {
        /*
        System.out.print(dealer + ": ");
        dealer.printHand();
        System.out.print(user + ": ");
        user.printHand();
        System.out.println()
         */

    }

}
