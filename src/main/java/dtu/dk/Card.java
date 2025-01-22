package dtu.dk;

import java.util.ArrayList;

public class Card {
    public enum Rank{JACK, QUEEN, KING, ACE};      
    public Rank rank;
    

    // Constructor
     public Card(Rank cardRank) {
        this.rank = cardRank;

    }

    public  ArrayList<Card> deck(){ //forms a deck of cards from the different cards, with each rank having 6 identical cards.
        ArrayList<Card> deck = new ArrayList<>();
        for(Rank Rank:Rank.values()){
            for(int i = 0; i<8;i++){
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
    public String toString() { //Overrides the standard tostring for objects
        return rank.toString();
    }


    public void setCard(Rank rank){ //changes a card into a specified Rank & suit. 
        this.rank = rank;
    }
    @Override
    public boolean equals(Object obj) { //// Overrides the standard equals
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Card card = (Card) obj;
        return rank.equals(card.rank);
    }



}
