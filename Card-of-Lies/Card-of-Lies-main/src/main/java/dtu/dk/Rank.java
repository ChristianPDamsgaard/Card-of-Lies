package dtu.dk;

public class Rank {
    private String name;        
    private int primaryValue;   //in the case of multiple values, then this is the smaller value
    private int secondaryValue;  //in the case of multiple values, then this is the bigger value
        //////////// OBS I THINK THIS CLASS IS REDUNDANT KEEPING IT JUST IN CASE, BUT EVT. DELETE LATER
    

    //constructor for cards with one value
    public Rank(String name, int primaryValue) {
        this.name = name;
        this.primaryValue = primaryValue;
        this.secondaryValue = primaryValue; 

    }
    //constructor for cards with two values
    public Rank(String name, int primaryValue, int secondaryValue) {
        this.name = name;
        this.primaryValue = primaryValue;
        this.secondaryValue = secondaryValue;
    }

    // Getters
    public String getName() {
        return name;
    }

    public int getPrimaryValue() {
        return primaryValue;
    }

    public int getSecondaryValue() {
        return secondaryValue;
    }
}
