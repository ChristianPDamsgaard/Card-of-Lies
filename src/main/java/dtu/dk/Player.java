package dtu.dk;

import org.apache.commons.lang3.ObjectUtils.Null;
import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.RemoteSpace;

import java.util.Random;
import java.util.Scanner;
import java.util.logging.Handler;
import java.util.List;


public class Player implements Runnable{
    private String playerName;
    private String playerId;
    private int gunChamper;
    private RemoteSpace mySpace;
    private RemoteSpace table;
    private String seatUrl;
    private String ip;
    private String postalCode;
    private Boolean playerDead = false;
    private Card gameMode = new Card(Card.Rank.KING);

    private TextClassForAllText text = new TextClassForAllText();

    public Player(String yourName, String yourId, String ip, String postalCode){
        Scanner playerInput = new Scanner(System.in);
        this.playerName = yourName;
        this.playerId = yourId;
        this.gunChamper = 6;
        this.ip = ip;
        this.postalCode = postalCode;

    }

    public void run(){
        getPrivateSpace();
        try{
            text.waitingForGame("waiting");
            table.query(new ActualField("gameHasStarted"));
            text.waitingForGame("found");
            System.out.println(seatUrl);
            //welcome to game text
            Scanner playerInput = new Scanner(System.in);
            while(true){
                //might need lock
                //check for turn
                System.out.println("your turn received");
                System.out.println("RUBBERDUCK");

                mySpace.put("canIAction", playerId);
                mySpace.get(new ActualField("doAction"), new ActualField(playerId));

                Object[] typeOfTurn =  mySpace.get(new ActualField("turnType"), new FormalField(Boolean.class));

                if((Boolean) typeOfTurn[1]){
                    Object[] checkForFirstTurn = mySpace.getp(new ActualField("youAreFirstTurner"), new FormalField(String.class));
                    System.out.println("FIRSTDUCK");
                    System.out.println("something about turn");
                    String typeOfTable = (String) checkForFirstTurn[1];
                    System.out.println("you have first turn");
                    //give rules and information about first turn
                    //play first turn

                    //check if move is legal
                    //something about looking at cards or checking cards.
                    //something about playing a card
                    System.out.println("You have the following cards");
                    List<Object[]> cards = mySpace.queryAll(new ActualField("Card"),new FormalField(Card.class));
                    for(Object[] tuple:cards){
                       Card card = (Card) tuple[1];
                       System.out.print(card.toString()+", ");
                    }
                    System.out.println(""); //ends line
                    System.out.println("write an action");
                    
                    mySpace.put("thisIsMyAction", playerInput.nextLine(), "cards");
                }else{
                    Object[] checkForTurn = mySpace.getp(new ActualField("itIsYourTurn"), new FormalField(String.class));
                    System.out.println("NOTFIRSTDUCK");
                    String typeOfTable = (String) checkForTurn[1];
                    System.out.println("you have the turn");

                    //give rules and information about turn
                    //play turn
                    //check for legal move
                    //make a choice to discern lie or picking up cards
                    System.out.println("You have the following cards"); //simple print statement
                    List<Object[]> cards = mySpace.queryAll(new ActualField("Card"),new FormalField(Card.class)); //find all tuples named "Card"
                    int handCounter = 0;
                    Card[] hand = new Card[cards.size()]; // this should create an array which has your cards it will make the following easier
                    for(Object[] tuple:cards){
                        handCounter++;
                        Card card = (Card) tuple[1]; //find the card part of the tuple 
                        
                        System.out.print(handCounter+")"+ " " + card.toString()+", ");
                         // & print it
                    }
                    System.out.println(""); //new line
                    System.out.println("write an action either c or p");
                    String action = playerInput.nextLine();
                    while(true){
                        if (action.equals("c")) {
                            System.out.println("Truth or Lie?");
                            String maybeTruth = playerInput.nextLine();
                        
                            int cardChoice = selectCard(hand, cards, playerInput);
                            if (cardChoice < 0 || cardChoice >= hand.length) {
                                System.out.println("Invalid choice. Try again.");
                                continue;
                            }
                        
                            if (maybeTruth.equalsIgnoreCase("Truth")) {
                                System.out.println("Attempting to play " + hand[cardChoice]);
                                if (hand[cardChoice].rank == gameMode.rank) {
                                    System.out.println("Success!");
                                    mySpace.put("PlayingCard", new ActualField("TRUE"));
                                    mySpace.put("thisIsMyAction", hand[cardChoice].toString(), "Cards", "TRUTH");
                                    hand[cardChoice] = null;
                                } else {
                                    System.out.println("Failure. Do you want to try again or pass your turn? (try/pass)");
                                    String retryOption = playerInput.nextLine();
                                    if (retryOption.equalsIgnoreCase("pass")) {
                                        mySpace.put("thisIsMyAction", "pass", "Cards", "TRUTH");
                                    }
                                }
                            } else if (maybeTruth.equalsIgnoreCase("Lie")) {
                                mySpace.put("PlayingCard", new ActualField("TRUE"));
                                System.out.println("The chosen card will appear as the correct one for the other players...");
                                mySpace.put("thisIsMyAction", hand[cardChoice].toString(), "Cards", "LIE");
                            }
                        
                                                    
                            
                            //if picking up cards
                            //something about looking at cards or checking cards.
                            //something about playing a card
                           // mySpace.put("thisIsMyAction", playerInput.nextLine(), "cards");
                            break;
                        } else if (action.equals("p")) {
                            //if discerning lies
                            mySpace.put("thisIsMyAction", playerInput.nextLine(), "punch");
                            if(!rulet(gunChamper)){
                                gunChamper--; //tjekke om der bliver skudt om det er dig selv eller modstander
                                System.out.println(gunChamper);

                            }else{
                                //person dø

                            }

                            break;
                        }
                    }
                }
            }
            
            //mySpace.get(new ActualField("gameHasEnded")); unreachable code?
            //play again or quit.... maybe return to lobby
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
}


    void playTurn(){

    }

    void getPrivateSpace(){
        try {
            System.out.println("a player has connected to the server");
            //connects to mainSpace
            table = new RemoteSpace("tcp://" + ip+":"+ postalCode + "/table?keep");
            //makes a seat request
            table.put("seatRequest", playerName, playerId);
            //checks if the requested seat is occupied
            Object[] occupiedResponse =  table.queryp(new ActualField("occupiedSeat"), new ActualField(playerName), new ActualField(playerId));
            if(occupiedResponse != null){
                // Få spillere til at ændre sit playerId, i scenariet hvor to spillere har samme playerId.
            }
            //gets the response from table, to get the url for the new private space
            Object[] response = table.get(new ActualField("seatNumber"), //OBS ændre navn senere!
                    new ActualField(playerId), new FormalField(String.class), new FormalField(String.class));

            //getting url
            seatUrl = (String) response[3];
            //connecting to new space
            this.mySpace = new RemoteSpace(seatUrl);
            table.put("userHasConnected");

            //check if the connection is established
            mySpace.put("we succeeded");
            mySpace.get(new ActualField("we succeeded"));

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
/*
made for testing purposes
    public static void main(String[] args) {
        while(true){
            try {
                String playerName = "judas";
                String playerId = "13";
                RemoteSpace table = new RemoteSpace("tcp://localhost:42069/table?keep");
                table.put("seatRequest", playerName, playerId);
                Object[] response = table.get(new ActualField("seatNumber"), //OBS ændre navn senere!
                        new ActualField(playerId), new FormalField(String.class), new FormalField(String.class));
                Object[] occupiedResponse =  table.queryp(new ActualField("occupiedSeat"), new ActualField(playerName), new ActualField(playerId));
                if(occupiedResponse != null){
                    // Få spillere til at ændre sit playerId, i scenariet hvor to spillere har samme playerId.
                }
                String seatUrl = (String) response[3];
                RemoteSpace seat = new RemoteSpace(seatUrl);

                //check if the connection is established
                seat.put("we succeeded");
                seat.get(new ActualField("we succeeded"));

            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }
*/


    public boolean rulet(int gunChamper){


        int bulletPlace = 1;
        Random some = new Random();
        int randomNumber = some.nextInt(1,(gunChamper+1));
        System.out.println(randomNumber);
        if(bulletPlace ==  randomNumber){
            System.out.println("rubberduck");

            return true; //player dies
        }else{

            System.out.println("steelduck");

            //put new amount of free chambers left
            return false;
        }
    }
    private int selectCard(Card[] hand, List<Object[]> cards, Scanner playerInput) {
        int handCounter = 0; // Initialize counter for hand array
        for (Object[] tuple : cards) {
            Card card = (Card) tuple[1]; // Extract the Card object from the tuple
            System.out.print((handCounter + 1) + ")" + " " + card.toString() + ", "); // Display the card (1-based index)
            hand[handCounter] = card; // Add the card to the hand array
            handCounter++; // Increment the hand counter
        }
        System.out.println("\nPlay a card"); // Prompt for card selection
        return playerInput.nextInt() - 1; // Return 0-based index (subtract 1 for array indexing)
    }
    
}


