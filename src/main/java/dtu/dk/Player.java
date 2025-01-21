package dtu.dk;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.RemoteSpace;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Player implements Runnable{
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
    private Card gameMode = new Card(Card.Rank.KING);
    private String typeOfTable;
    private Object[] otherPlayerResult;
    private Object[] discardCard;
    private boolean gameState;
    private String action;



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
            System.out.println(url);
            //welcome to game text
            Scanner playerInput = new Scanner(System.in);
            mySpace.put("youDied",playerName,playerId,false);
            while(true){
                while(true) {
                    //might need lock
                    //check for turn
                    System.out.println("your turn received");

                    mySpace.put("canIAction", playerId);
                    Object[] actionOne = mySpace.get(new ActualField("doAction"), new ActualField(playerId), new FormalField(Boolean.class));
                    if(!(Boolean)actionOne[2]){
                        break;
                    }
                    Object[] whichType = table.query(new ActualField("tableType"), new FormalField(String.class));
                    typeOfTable = (String) whichType[1];

                    Object[] typeOfTurn = mySpace.get(new ActualField("turnType"), new FormalField(Boolean.class));

                    if ((Boolean) typeOfTurn[1]) {
                        Object[] checkForFirstTurn = mySpace.getp(new ActualField("youAreFirstTurner"), new FormalField(String.class));
                        System.out.println("something about turn");
                        String typeOfTable = (String) checkForFirstTurn[1];
                        System.out.println("you have first turn");
                        //give rules and information about first turn
                        //play first turn

                        //check if move is legal
                        //something about looking at cards or checking cards.
                        //something about playing a card
                        //System.out.println("You have the following cards"); //simple print statement
                        List<Object[]> cards = mySpace.queryAll(new ActualField("Card"), new FormalField(Card.class)); //find all tuples named "Card"
                        List<Card> hand = new ArrayList<>();
                        int handCounter = 0;
                        //Card[] hand = new Card[cards.size()]; // this should create an array which has your cards it will make the following easier
                        System.out.println("it is the " + typeOfTable + " table");   //Carsten skal lave noget text
                        System.out.println("");   //Carsten skal lave noget text omkring hvad man skal gøre udfra den info ovenover
                        for (Object[] tuple : cards) {
                            handCounter++;
                            Card card = (Card) tuple[1]; //find the card part of the tuple
                            String komma = ",";
                            if(handCounter == cards.size()){
                                komma = "";
                            }
                            System.out.print(handCounter + ")" + " " + card.toString() + komma + " ");
                            // & print it
                        }
                        System.out.println(""); //new line

                        int cardChoice = selectCard(hand, cards, playerInput);
                        while (true) {
                            if (cardChoice < 0 || cardChoice >= hand.size()) {
                                System.out.println("Invalid choice. Try again.");
                                cardChoice = selectCard(hand, cards, playerInput);
                            } else {
                                break;
                            }
                        }

                        mySpace.put("thisIsMyAction", hand.get(cardChoice).toString(), "Cards");
                        System.out.println("you have now played your turn");
                        // Remove a specific tuple with a pattern
                        Object[] removedTuple = mySpace.get(new ActualField("Card"), new ActualField(cards.get(cardChoice)[1]));
                        System.out.println("MISSINGDUCk");
                        otherPlayerResult = mySpace.get(new ActualField("otherPunchResult"), new FormalField(Boolean.class));
                        if((Boolean)otherPlayerResult[1]){
                            if (!roulette(gunChamper)) {
                                gunChamper--; //tjekke om der bliver skudt om det er dig selv eller modstander
                                //mySpace.put("youSurvived",playerName,playerId,false);
                            } else {
                                //person død
                                mySpace.get(new ActualField("youDied"), new ActualField(playerName), new ActualField(playerId), new ActualField(false));
                                mySpace.put("youDied", playerName, playerId, true);
                                mySpace.put("DeathcountUp");
                                table.put("DeathcountUp");
                                playerDead = true;
                                System.out.println("you have died, waiting game to end");
                            }
                        }
                    } else {
                        Object[] checkForTurn = mySpace.getp(new ActualField("itIsYourTurn"), new FormalField(String.class));
                        String typeOfTable = (String) checkForTurn[1];
                        System.out.println("you have the turn");
                        //give rules and information about turn
                        //play turn
                        //check for legal move
                        //make a choice to discern lie or picking up cards
                        System.out.println("You have the following cards"); //simple print statement    Carsten skal lave noget text
                        List<Object[]> cards = mySpace.queryAll(new ActualField("Card"), new FormalField(Card.class)); //find all tuples named "Card"
                        int handCounter = 0;
                        List<Card> hand = new ArrayList<>(); // this should create an array which has your cards it will make the following easier
                        for (Object[] tuple : cards) {
                            handCounter++;
                            String komma = ",";
                            if(handCounter == cards.size()){
                                komma = "";
                            }
                            Card card = (Card) tuple[1]; //find the card part of the tuple
                            System.out.print(handCounter + ")" + " " + card.toString() + komma + " ");
                            // & print it
                        }
                        System.out.println(""); //new line
                        System.out.println("it is the " + typeOfTable + "'s table");   //Carsten skal lave noget text
                        System.out.println("");   //Carsten skal lave noget text omkring hvad man skal gøre udfra den info ovenover
                        System.out.println("write an action either c or p");
                        action = playerInput.nextLine();

                        while (true) {
                            
                            if (action.equals("c")) {
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
                                hand.remove(cardChoice);
                                Object[] removedTuple = mySpace.get(new ActualField("Card"), new ActualField(cards.get(cardChoice)[1]));
                                table.put(new ActualField("discardedCard"), new ActualField(cards.get(cardChoice)[1]));
                                   
                                System.out.println("you have now played your turn");

                                break;
                            } else if (action.equals("p")) {
                                try {
                                //if discerning lies
                                
                                } catch (Exception e) {
                                    // TODO: handle exception
                                }
                             
                                mySpace.put("thisIsMyAction", "callOut", "punch");

                                Object[] actionResult = mySpace.get(new ActualField("punchResult"), new FormalField(Boolean.class));
                                if((Boolean) actionResult[1]){
                                    if (!roulette(gunChamper)) {
                                        gunChamper--; //tjekke om der bliver skudt om det er dig selv eller modstander
                                        //mySpace.put("youSurvived",playerName,playerId,false);
                                    } else {
                                        //person død
                                        mySpace.get(new ActualField("youDied"), new ActualField(playerName), new ActualField(playerId), new ActualField(false));
                                        mySpace.put("youDied", playerName, playerId, true);
                                        mySpace.put("DeathcountUp");
                                        table.put("DeathcountUp");
                                        playerDead = true;
                                        System.out.println("you have died, waiting game to end");
                                    }
                                }else{
                                    System.out.println(); //Carsten skal lave noget text omkring man har callet en løgn og den anden person bliver straffet
                                }

                                System.out.println("you have now played your turn");
                                break;
                            }
                            System.out.println("that was not an option, try again");
                            action = playerInput.nextLine();
                        }
                        System.out.println("EnMandIDameTøj");
                        otherPlayerResult = mySpace.get(new ActualField("otherPunchResult"), new FormalField(Boolean.class));
                        System.out.println("FilipErSåFolt");
                        if((Boolean)otherPlayerResult[1]){
                            if (!roulette(gunChamper)) {
                                gunChamper--; //tjekke om der bliver skudt om det er dig selv eller modstander
                                //mySpace.put("youSurvived",playerName,playerId,false);
                            } else {
                                //person død
                                mySpace.get(new ActualField("youDied"), new ActualField(playerName), new ActualField(playerId), new ActualField(false));
                                
                                mySpace.put("youDied", playerName, playerId, true);
                                mySpace.put("DeathcountUp");
                                table.put("DeathcountUp");
                                playerDead = true;
                                System.out.println("you have died, waiting game to end");
                                
                            }
                        }

                    }
                    Object[] endChekker = table.queryp(new ActualField("gameHasEnded"));
                    TimeUnit.MILLISECONDS.sleep(25);
                    if(endChekker != null){
                        break;
                    }
                    if (playerDead) {
                        break;
                    }
                }
                System.out.println("PeterErEnKæmpeTaber");
                //play again or quit.... maybe return to lobby
                // Carsten skal lave systemprint til spillet er slut

                table.query(new ActualField("gameHasEnded"));
                while(true) {
                    System.out.println("press a or e");
                    String endMessage = playerInput.nextLine().toLowerCase();
                    System.out.println("NickErSuperSej");
                    if (endMessage.equals("a")) { //a for again
                        table.put("playAgain", playerName, playerId, url);
                        break;
                    } else if (endMessage.equals("e")) { //e for end
                        endGame = true;
                        //Carsten tak for spillet
                        break;
                    } else {
                        //Carsten du skrev ingen passende besked
                    }
                }
                if(endGame){
                    break;
                }
            }
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
            url = (String) response[3];
            //connecting to new space
            this.mySpace = new RemoteSpace(url);
            table.put("userHasConnected");

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


    public boolean roulette(int gunChamper){


        int bulletPlace = 1;
        Random some = new Random();
        int randomNumber = some.nextInt(1,(gunChamper+1));
        if(bulletPlace ==  randomNumber){
            System.out.println("rubberduck");
            return true; //player dies
        }else{
            System.out.println("steelduck");
            //put new amount of free chambers left
            return false;
        }
    }
        private int selectCard(List<Card> hand, List<Object[]> cards, Scanner playerInput) {
        for (Object[] tuple : cards) {
            Card card = (Card) tuple[1]; // Extract the Card object from the tuple
           // System.out.print((handCounter + 1) + ")" + " " + card.toString() + ", "); // Display the card (1-based index)
            hand.add(card); // Add the card to the hand array
        }
        System.out.println("\nPlay a card"); // Prompt for card selection
        return playerInput.nextInt() - 1; // Return 0-based index (subtract 1 for array indexing)
    }
}


