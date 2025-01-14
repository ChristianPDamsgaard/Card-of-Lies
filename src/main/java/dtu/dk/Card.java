package dtu.dk;

import java.util.ArrayList;

public class Card {
    private enum Rank{JACK, QUEEN, KING, ACE};       // OBS ADD JOKERS LATER, NOTE THAT THERE MUST BE ONLY 2 JOKERS
    private Rank rank;
    

    // Constructor
     public Card(Rank cardRank) {
        this.rank = cardRank;

    }

    public static ArrayList<Card> deck(){ //forms a deck of cards from the different cards, with each rank having 6 identical cards.
        ArrayList<Card> deck = new ArrayList<>();
        for(Rank Rank:Rank.values()){
            for(int i = 0; i<6;i++){
                deck.add(new Card(Rank));
            }
        }
            return deck; 
        }

    // Getters
    public Rank getRank() { //returns the rank
        return rank;
    }

    @Override
    public String toString() {
        return rank.toString();
    }


    public void setCard(Rank rank){ //changes a card into a specified Rank & suit. 
        this.rank = rank;
    }


/*     public int getValue(boolean aceAsHigh) { //needs to return a value depending if the ace is highest or lowest
        if ("Ace".equals(rank.getName())) { // Check if it's an Ace
            return aceAsHigh ? rank.getSecondaryValue() : rank.getPrimaryValue(); // the "?" and ":" is an (if-else) statement, which means that if aceAsHigh is correct return the second Value (the higher) otherwise return the smaller primary value. 
        }
        return rank.getPrimaryValue(); //if its not ace, then return its value.
    }  */

}
