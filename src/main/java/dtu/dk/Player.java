package dtu.dk;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.RemoteSpace;
import org.jspace.SequentialSpace;
import org.jspace.Space;

public class Player {
    private String myName;
    private String playerId;
    private RemoteSpace seat;
    private RemoteSpace table;

    public Player(){
        try {
            this.table = new RemoteSpace("tcp://localhost:42069/table?keep"); 
            table.put("seatRequest", myName, playerId);
            Object[] response = table.get(new ActualField("seatNumber"), //OBS ændre navn senere!
                    new ActualField(playerId), new FormalField(String.class), new FormalField(String.class)); 
            Object[] occupiedResponse =  table.queryp(new ActualField("occupiedSeat"), new ActualField(myName), new ActualField(playerId)); 
            if(occupiedResponse != null){
                    // Få spillere til at ændre sit playerId, i scenariet hvor to spillere har samme playerId. 
            }
            String seatUrl = (String) response[3];
            this.seat = new RemoteSpace(seatUrl);


        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
       
    }


    public static void main(String[] args) {
    while(true){
        try {
            String myName = "judas";
            String playerId = "13";
            RemoteSpace table = new RemoteSpace("tcp://localhost:42069/table?keep"); 
            table.put("seatRequest", myName, playerId);
            System.out.println("DUMBDUCK");
            Object[] response = table.get(new ActualField("seatNumber"), //OBS ændre navn senere!
                    new ActualField(playerId), new FormalField(String.class), new FormalField(String.class)); 
            System.out.println("CLEVERDUCK");
            Object[] occupiedResponse =  table.queryp(new ActualField("occupiedSeat"), new ActualField(myName), new ActualField(playerId)); 
            if(occupiedResponse != null){
                    // Få spillere til at ændre sit playerId, i scenariet hvor to spillere har samme playerId. 
            }
            String seatUrl = (String) response[3];
            System.out.println(seatUrl);
            RemoteSpace seat = new RemoteSpace(seatUrl);
            seat.put("we succeeded");
            seat.get(new ActualField("we succeeded"));
            System.out.println("STEELDUCK");

        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("RUBBERDUCK");
        }
    }
    }




}
