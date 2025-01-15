package dtu.dk;

public class Card {
    private Rank rank;       // Use the Rank class for card rank
    private enum suit{DIAMONDS, CLUBS, HEARTS, SPADES}; //Remember this is only for defining it
    
    private suit suit; // Field for the suit


    // Constructor
     public Card(Rank rank, suit suit) {
        this.rank = rank;
        this.suit = suit;
    }


    // Getters
    public Rank getRank() { //returns the rank
        return rank;
    }

    public suit getSuit() { //Returns suits (good show!)
        return suit;
    }


    public void setCard(Rank rank, suit suit){ //changes a card into a specified Rank & suit. 
        this.rank = rank;
        this.suit = suit;
    }


    public int getValue(boolean aceAsHigh) { //needs to return a value depending if the ace is highest or lowest
        if ("Ace".equals(rank.getName())) { // Check if it's an Ace
            return aceAsHigh ? rank.getSecondaryValue() : rank.getPrimaryValue(); // the "?" and ":" is an (if-else) statement, which means that if aceAsHigh is correct return the second Value (the higher) otherwise return the smaller primary value. 
        }
        return rank.getPrimaryValue(); //if its not ace, then return its value.
    } 

}
