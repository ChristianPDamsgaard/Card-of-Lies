package dtu.dk;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.RemoteSpace;

import java.util.List;
import java.util.Random;
import java.util.Scanner;

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
            mySpace.put("youDied",playerName,playerId,false);
            while(true){
                //might need lock
                //check for turn
                System.out.println("your turn received");

                mySpace.put("canIAction", playerId);
                mySpace.get(new ActualField("doAction"), new ActualField(playerId));

                Object[] typeOfTurn =  mySpace.get(new ActualField("turnType"), new FormalField(Boolean.class));

                if((Boolean) typeOfTurn[1]){
                    Object[] checkForFirstTurn = mySpace.getp(new ActualField("youAreFirstTurner"), new FormalField(String.class));
                    System.out.println("something about turn");
                    String typeOfTable = (String) checkForFirstTurn[1];
                    System.out.println("you have first turn");
                    //give rules and information about first turn
                    //play first turn

                    //check if move is legal
                    //something about looking at cards or checking cards.
                    //something about playing a card
                    System.out.println("You have the following cards"); //simple print statement
                    List<Object[]> cards = mySpace.queryAll(new ActualField("Card"),new FormalField(Card.class)); //find all tuples named "Card"
                    for(Object[] tuple:cards){
                       Card card = (Card) tuple[1]; //find the card part of the tuple 
                       System.out.print(card.toString()+", "); // & print it
                    }
                    System.out.println(""); //ends line
                    System.out.println("write an action");
                    mySpace.put("thisIsMyAction", playerInput.nextLine(), "cards");
                }else{
                    Object[] checkForTurn = mySpace.getp(new ActualField("itIsYourTurn"), new FormalField(String.class));
                    String typeOfTable = (String) checkForTurn[1];
                    System.out.println("you have the turn");

                    //give rules and information about turn
                    //play turn
                    //check for legal move
                    //make a choice to discern lie or picking up cards
                    System.out.println("write an action either c or p");
                    String action = playerInput.nextLine();
                    while(true){
                        if(action.equals("c")){
                            //if picking up cards
                            //something about looking at cards or checking cards.
                            //something about playing a card
                            mySpace.put("thisIsMyAction", playerInput.nextLine(), "cards");
                            break;
                        } else if (action.equals("p")) {
                            //if discerning lies
                            mySpace.put("thisIsMyAction", playerInput.nextLine(), "punch");
                            if(!roulette(gunChamper)){
                                gunChamper--; //tjekke om der bliver skudt om det er dig selv eller modstander
                                //mySpace.put("youSurvived",playerName,playerId,false);
                            }else{
                                //person død
                                mySpace.get(new ActualField("youDied"), new ActualField(playerName), new ActualField(playerId), new ActualField(false));
                                mySpace.put("youDied",playerName,playerId,true);
                                mySpace.put("DeathcountUp");
                                playerDead = true;
                                System.out.println("you have died, waiting game to end");
                            }
                            break;
                        }
                    }

                }
                if(playerDead){
                    break;
                }
            }
            mySpace.get(new ActualField("gameHasEnded"));
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

}


