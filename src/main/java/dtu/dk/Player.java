package dtu.dk;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.RemoteSpace;
import org.jspace.SequentialSpace;
import org.jspace.Space;

import java.util.Arrays;
import java.util.Scanner;

public class Player implements Runnable{
    private String playerName;
    private String playerId;
    private RemoteSpace seat;

    private TextClassForAllText text = new TextClassForAllText();

    public Player(String yourName, String yourId){
        Scanner playerInput = new Scanner(System.in);
        this.playerName = yourName;
        this.playerId = yourId;
    }

    public void run(){
        getPrivateSpace();

        try{
            text.waitingForGame("waiting");
            //table.query(new ActualField("gameHasStarted"));
            text.waitingForGame("found");
            //welcome to game text
            /*
            while(true){

                //check for turn
                Object[] checkForFirstTurn = seat.getp(new ActualField("youAreFirstTurner"), new FormalField(String.class));
                Object[] checkForTurn = seat.getp(new ActualField("itIsYourTurn"), new FormalField(String.class));

                if(checkForFirstTurn[0] != null){
                    String typeOfTable = (String) checkForFirstTurn[1];
                    //give rules and information about first turn
                    // play first turn

                    //check if move is legal
                    //something about looking at cards or checking cards.
                    //something about playing a card
                    seat.put("thisIsMyAction", playerInput.nextLine(), "cards");
                } else if (checkForTurn[0] != null) {
                    String typeOfTable = (String) checkForTurn[1];

                    //give rules and information about turn
                    // play turn
                    // check for legal move
                    //make a choice to discern lie or picking up cards

                        //if picking up cards
                    //something about looking at cards or checking cards.
                    //something about playing a card
                    seat.put("thisIsMyAction", playerInput.nextLine(), "cards");

                    //if discerning lies
                    seat.put("thisIsMyAction", playerInput.nextLine(), "punch");
                }
            }
*/

        }catch (Exception e){

        }
    }


    void playTurn(){

    }

    void getPrivateSpace(){
        try {
            System.out.println("a player has connected to the server");
            //connects to mainSpace
            RemoteSpace table = new RemoteSpace("tcp://localhost:42069/table?keep");
            //makes a seat request
            table.put("seatRequest", playerName, playerId);

            System.out.println("THISDUCK");
            //checks if the requested seat is occupied
            Object[] occupiedResponse =  table.queryp(new ActualField("occupiedSeat"), new ActualField(playerName), new ActualField(playerId));
            if(occupiedResponse != null){
                // Få spillere til at ændre sit playerId, i scenariet hvor to spillere har samme playerId.
            }

            //gets the response from table, to get the url for the new private space
            table.put("seatNumber");
            Object[] response = table.get(new ActualField("seatNumber"), //OBS ændre navn senere!
                    new ActualField(playerId), new FormalField(String.class), new FormalField(String.class));
            System.out.println("GELLODUCK");

            //getting url
            String seatUrl = (String) response[3];
            //connecting to new space
            this.seat = new RemoteSpace(seatUrl);
            table.put("userHasConnected");
            //check if the connection is established
            seat.put("we succeeded");
            seat.get(new ActualField("we succeeded"));
            System.out.println("\u001B[31mSTEEL DUCK");

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
                System.out.println(seatUrl);
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


}
