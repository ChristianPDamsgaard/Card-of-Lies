package dtu.dk;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.RemoteSpace;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Player implements Runnable{ //creates everything the player needs
    private String playerName;
    private String playerId;
    private int gunChamper;
    private RemoteSpace mySpace;
    private RemoteSpace table;
    private String url;
    private String ip;
    private String postalCode;
    private Boolean playerDead = false;
    private Boolean endGame = false;
    // private Card gameMode = new Card(Card.Rank.KING); //obs remove
    private String typeOfTable;
    private Object[] otherPlayerResult;
   // private Object[] discardCard; //obs remove
    //private boolean gameState; //obs remove
    private String action; 

    private TextClassForAllText text = new TextClassForAllText();

    public Player(String yourName, String yourId, String ip, String postalCode){ //constructor the player object
        //Scanner playerInput = new Scanner(System.in); //obs remove
        this.playerName = yourName;
        this.playerId = yourId;
        this.gunChamper = 6;
        this.ip = ip;
        this.postalCode = postalCode;
    }

    public void run(){ //the loop
        getPrivateSpace(); //starts by getting its private space
        try{
            Scanner playerInput = new Scanner(System.in); //sets up scanner
            while(true){
            text.waitingForGame("waiting");
            table.query(new ActualField("gameHasStarted"));
            text.waitingForGame("found");
            System.out.println(url);
            //welcome to game text
            mySpace.put("youDied",playerName,playerId,false); //ensures there are no residue tuples that send the wrong token
                while(true) {
                    //might need lock
                    //check for turn
                    text.turnReceived();
                    mySpace.put("canIAction", playerId); //checks if it is possible to do action
                    Object[] actionOne = mySpace.get(new ActualField("doAction"), new ActualField(playerId), new FormalField(Boolean.class));
                    if(!((Boolean)actionOne[2])){ //if you can't do action skip all this
                        break;
                    }
                    Object[] whichType = table.query(new ActualField("tableType"), new FormalField(String.class));
                    typeOfTable = (String) whichType[1];
                    Object[] gameHasEnded = table.getp(new ActualField("gameHasEnded"));
                    TimeUnit.MILLISECONDS.sleep(25);
                    if(gameHasEnded != null){
                        break;
                    }

                    Object[] typeOfTurn = mySpace.get(new ActualField("turnType"), new FormalField(Boolean.class));

                    if ((Boolean) typeOfTurn[1]) { //runs a specific first turn, this happens in the beginning of the game, also after punch action has been done the previous turn
                        Object[] checkForFirstTurn = mySpace.getp(new ActualField("youAreFirstTurner"), new FormalField(String.class));
                        text.firstTurn();
                        String typeOfTable = (String) checkForFirstTurn[1];

                        List<Object[]> cards = mySpace.queryAll(new ActualField("Card"), new FormalField(Card.class)); //find all tuples named "Card"
                        List<Card> hand = new ArrayList<>(); //create a list of cards that is in your hand
                        int handCounter = 0;
                        text.typeOfTable(typeOfTable); //write what type of table is
                        for (Object[] tuple : cards) { //go through all cards
                            handCounter++;
                            Card card = (Card) tuple[1]; //find the card part of the tuple
                            String komma = ","; //add a komma between cards
                            if(handCounter == cards.size()){ //prevents fence posting
                                komma = "";
                            }
                            System.out.print(handCounter + ")" + " " + card.toString() + komma + " "); //prints the hand
                            // & print it
                        }
                        System.out.println(); //new line
                        for (Object[] tuple : cards) {
                            Card card = (Card) tuple[1]; // Extract the Card object from the tuple
                            hand.add(card); // Add the card to the hand array
                        }

                        int cardChoice = selectCard(hand, cards, playerInput); //return what choice you, also checks if you write a non integer such as "a"
                        while (true) {
                            if (cardChoice < 0 || cardChoice >= hand.size()) { //checks if the choice is out bounds for the hand
                                text.invalidChoice(); 
                                cardChoice = selectCard(hand, cards, playerInput); //run it again
                            } else {
                                break;
                            }
                        }

                        mySpace.put("thisIsMyAction", hand.get(cardChoice).toString(), "Cards"); //insert the choice into a tuple to be sent to dealer
                        text.turnPlayed();
                        // Remove a specific tuple with a pattern
                       // Object[] removedTuple = mySpace.get(new ActualField("Card"), new ActualField(cards.get(cardChoice)[1])); //obs remove 
                        
                        otherPlayerResult = mySpace.get(new ActualField("otherPunchResult"), new FormalField(Boolean.class)); //gets the punch result from the previous player
                        if((Boolean)otherPlayerResult[1]){ //checks if the other player was truthfull
                            if (!roulette(gunChamper)) { //if the shoot didn't hit, increase odds for next shot
                                gunChamper--;
                              
                            } else {
                                //person død
                                mySpace.get(new ActualField("youDied"), new ActualField(playerName), new ActualField(playerId), new ActualField(false));
                                mySpace.put("youDied", playerName, playerId, true);
                                mySpace.put("DeathcountUp");
                                table.put("DeathcountUp");
                                playerDead = true;
                                text.playerDied();
                            }
                        }
                    } else {
                        Object[] checkForTurn = mySpace.getp(new ActualField("itIsYourTurn"), new FormalField(String.class)); //checks if its your turn
                        String typeOfTable = (String) checkForTurn[1];
                        text.otherTurn();
                        List<Object[]> cards = mySpace.queryAll(new ActualField("Card"), new FormalField(Card.class)); //find all tuples named "Card"
                        int handCounter = 0;
                        List<Card> hand = new ArrayList<>(); // this should create an array which has your cards it will make the following easier
                        for (Object[] tuple : cards) { //some of this is the same as before, the difference is this is not a starting turn
                            handCounter++;
                            String komma = ",";
                            if(handCounter == cards.size()){
                                komma = "";
                            }
                            Card card = (Card) tuple[1]; //find the card part of the tuple
                            System.out.print(handCounter + ")" + " " + card.toString() + komma + " ");
                            // & print it
                        }
                        for (Object[] tuple : cards) {
                            Card card = (Card) tuple[1]; // Extract the Card object from the tuple
                            hand.add(card); // Add the card to the hand array
                        }
                        System.out.println(); //new line
                        text.typeOfTable(typeOfTable);
                        if(hand.isEmpty()){ //if the hand array is empty, give only the option to punch
                            System.out.println("you have no more cards left you can only call punch, so press (p)");
                        }else{
                            text.chooseOption();
                        }
                        action = playerInput.nextLine(); //write the players action

                        while (true) {
                            
                            if (action.equals("c") && !hand.isEmpty()) { // if their action equals c & their hand is empty
                                int cardChoice = selectCard(hand, cards, playerInput); 
                                while (true) {
                                    if (cardChoice < 0 || cardChoice >= hand.size()) {
                                        System.out.println("Invalid choice. Try again.");
                                        cardChoice = selectCard(hand, cards, playerInput);
                                    } else {
                                        break;
                                    }
                                }
                                mySpace.put("thisIsMyAction", hand.get(cardChoice).toString(), "cards");
                                hand.remove(cardChoice); //remove that card from the hand
                                Object[] removedTuple = mySpace.get(new ActualField("Card"), new ActualField(cards.get(cardChoice)[1]));
                               // table.put(new ActualField("discardedCard"), new ActualField(cards.get(cardChoice)[1])); // i think this is redundant REMOVE
                                   
                                text.turnPlayed();

                                break;
                            } else if (action.equals("p")) { //if you punch
                                mySpace.put("thisIsMyAction", "callOut", "punch");

                                Object[] actionResult = mySpace.get(new ActualField("punchResult"), new FormalField(Boolean.class));

                                if((Boolean) actionResult[1]){ // if it is true, shot yourself
                                    System.out.println("the other player didn't lie");
                                    if (!roulette(gunChamper)) { // doesn't hit, increase odds
                                        gunChamper--; 
                                    } else { // hits, you're dead!
                                        //other
                                        mySpace.get(new ActualField("youDied"), new ActualField(playerName), new ActualField(playerId), new ActualField(false));
                                        mySpace.put("youDied", playerName, playerId, true);
                                        table.put("DeathcountUp");
                                        playerDead = true;
                                        text.playerDied();
                                    }
                                }

                                text.turnPlayed();
                                break;
                            }
                            text.invalidChoice();
                            action = playerInput.nextLine();
                        }
                        otherPlayerResult = mySpace.get(new ActualField("otherPunchResult"), new FormalField(Boolean.class)); 

                        if((Boolean)otherPlayerResult[1]){ //same as before just outside the loop
                            System.out.println("the other player called out your lie");
                            if (!roulette(gunChamper)) {
                                gunChamper--; 
                                
                            } else {
                                //person død
                                mySpace.get(new ActualField("youDied"), new ActualField(playerName), new ActualField(playerId), new ActualField(false));
                                mySpace.put("youDied", playerName, playerId, true);
                                mySpace.put("DeathcountUp");
                                table.put("DeathcountUp");
                                playerDead = true;
                                text.playerDied();
                                
                            }
                        }

                    }
                    Object[] endChekker = table.queryp(new ActualField("gameHasEnded")); //checks if the game has ended
                    TimeUnit.MILLISECONDS.sleep(25);
                    if(endChekker != null){
                        break;
                    }
                    if (playerDead) {
                        break;
                    }
                }

                table.query(new ActualField("gameHasEnded"));
                text.gameDone();
                if(endGame){
                    break;
                }
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    void getPrivateSpace(){
        try {
            text.playerConnected();
            //connects to mainSpace
            table = new RemoteSpace("tcp://" + ip+":"+ postalCode + "/table?keep");
            //makes a seat request
            table.put("seatRequest", playerName, playerId);
            //checks if the requested seat is occupied
            Object[] occupiedResponse =  table.queryp(new ActualField("occupiedSeat"), new ActualField(playerName), new ActualField(playerId));
            //gets the response from table, to get the url for the new private space
            Object[] response = table.get(new ActualField("seatNumber"), 
                    new ActualField(playerId), new FormalField(String.class), new FormalField(String.class));

            //getting url
            url = (String) response[3];
            //connecting to new space
            this.mySpace = new RemoteSpace(url);
            table.put("userHasConnected");

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public boolean roulette(int gunChamper){ //returns if you're hit from russian roulette
        int bulletPlace = 1;
        Random some = new Random();
        int randomNumber = some.nextInt(1,(gunChamper+1));
        if(bulletPlace ==  randomNumber){
            return true; //player dies
        }else{
            return false;
        }
    }
    private int selectCard(List<Card> hand, List<Object[]> cards, Scanner playerInput) { //selects a card from a hand, ensures that it is correctly picked
        while (true) { // Loop until valid input is provided
            text.playCard(); // Prompt the user to play a card
            String input = playerInput.next(); // Read the input as a String
    
            try {
                int number = Integer.parseInt(input); // Try to convert the input to an integer
                if (number >= 1 && number <= hand.size()) { // Check if the number is within valid range
                    return number - 1; // Return 0-based index (subtract 1 for array indexing)
                } else {
                    System.out.println("Invalid choice. Please enter a number between 1 and " + hand.size() + ".");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }
    }
}


