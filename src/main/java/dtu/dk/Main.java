package dtu.dk;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.SequentialSpace;
import org.jspace.SpaceRepository;

public class Main {


    public static void main(String[] args){
        SpaceRepository spaceRepository = new SpaceRepository(); //obs evt. ændre navn på object!
        SequentialSpace tableSpace = new SequentialSpace();
        SequentialSpace guestSpace = new SequentialSpace();
        spaceRepository.add("table",tableSpace);
        spaceRepository.addGate("tcp://localhost:42069/?keep");
        int seatNumber = 0;
        String seatUrl;
        String urlName;
        
        new Thread(new Dealer(tableSpace)).start(); 
        
       while(true){
        try{
            System.out.println("STONEDUCK");
            Object[] request = tableSpace.get(new ActualField("seatRequest"), new FormalField(String.class), new  FormalField(String.class)); //OBS NAVN Ændring
            String guest = (String) request[1]; 
            String playerId = (String) request[2]; // Muligvis fjernes
                
            Object[] theSeat = guestSpace.queryp(new ActualField("roomId"), new FormalField(String.class), new FormalField(String.class));
            if(theSeat != null){
                System.out.println("This seat is already taken, get the fuck out!"); //Rude
                tableSpace.put("occupiedSeat", guest, playerId);
            } else {
                System.out.println("Seating guest " + guest + " at " + playerId);
                seatUrl = ("tcp://localhost:42069/seat" + seatNumber + "?keep"); //ikke sikker skal muligvis ændres for at virke.
                System.out.println(seatUrl);
                SequentialSpace newSeat = new SequentialSpace();
                urlName = "seat" + seatNumber;
                spaceRepository.add(urlName, newSeat);
                guestSpace.put(playerId, seatNumber);
                seatNumber++;
                tableSpace.put("seatNumber", playerId, guest, seatUrl);

                break;
            }
            
            
        } catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
    }




}
