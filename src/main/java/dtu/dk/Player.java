package dtu.dk;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.RemoteSpace;
import org.jspace.SequentialSpace;
import org.jspace.Space;

public class Player {
    private String playerName;
    private String playerId;
    private RemoteSpace seat;
    private RemoteSpace table;

    public Player(String yourName, String yourId){
        this.playerName = yourName;
        this.playerId = yourId;
        try {
            System.out.println("a player has connected to the server");
            //connects to mainSpace
            this.table = new RemoteSpace("tcp://localhost:42069/table?keep");
            table.put("userHasConnected");
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
            String seatUrl = (String) response[3];
            //connecting to new space
            this.seat = new RemoteSpace(seatUrl);

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
