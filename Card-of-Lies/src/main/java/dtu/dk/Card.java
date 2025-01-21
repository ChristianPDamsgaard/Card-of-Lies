package dtu.dk;

import java.util.ArrayList;

public class Card {
    public enum Rank{JACK, QUEEN, KING, ACE};       // OBS ADD JOKERS LATER, NOTE THAT THERE MUST BE ONLY 2 JOKERS
    public Rank rank;
    

    // Constructor
     public Card(Rank cardRank) {
        this.rank = cardRank;

    }

    public  ArrayList<Card> deck(){ //forms a deck of cards from the different cards, with each rank having 6 identical cards.
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
    @Override
    public boolean equals(Object obj) { //// forgot about this completely, very important!
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Card card = (Card) obj;
        return rank.equals(card.rank);
    }

/*     public int getValue(boolean aceAsHigh) { //needs to return a value depending if the ace is highest or lowest
        if ("Ace".equals(rank.getName())) { // Check if it's an Ace
            return aceAsHigh ? rank.getSecondaryValue() : rank.getPrimaryValue(); // the "?" and ":" is an (if-else) statement, which means that if aceAsHigh is correct return the second Value (the higher) otherwise return the smaller primary value. 
        }
        return rank.getPrimaryValue(); //if its not ace, then return its value.
    }  */

}
