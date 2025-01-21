package dtu.dk;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.SequentialSpace;
import org.jspace.SpaceRepository;

import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Main {
    //the main space that contains the other spaces
    static SpaceRepository mainSpace = new SpaceRepository();
    //space that keeps track of the game and the players
    static SequentialSpace tableSpace = new SequentialSpace();
    //space that keeps track of the players private spaces
    static SequentialSpace guestRegistry = new SequentialSpace();
    static SequentialSpace userInputSpace = new SequentialSpace();
    static SequentialSpace trashCan = new SequentialSpace();
    static TextClassForAllText text = new TextClassForAllText();
    static Object[] playagain;


    //variables
    static int seatNumber;
    static String id = "";
    static String url;
    static String nameOfUrl;
    static String ip;
    static String postalCode;

    public static void main(String[] args){
        ip = "localhost";
        //ip = "10.209.242.31";
        postalCode = "42069";

        //add the tableSpace to main space
        mainSpace.add("table",tableSpace);
        mainSpace.add("userInput", userInputSpace);
        mainSpace.add("trash", trashCan);
        //make server option available
        mainSpace.addGate("tcp://" + "ip"+":"+ postalCode + "/?keep");
        //starting new thread for the dealer
        new Thread(new Dealer(tableSpace, userInputSpace,guestRegistry, ip, postalCode)).start();
        new Thread(new Host(ip, postalCode)).start();
        Scanner userInput = new Scanner(System.in);

        while(true){
            try {
                playagain = tableSpace.getp(new ActualField("playAgain"), new FormalField(String.class), new FormalField(String.class), new FormalField(String.class));
                Object[] addPlay = tableSpace.getp(new ActualField("addPlayer"));

                TimeUnit.MILLISECONDS.sleep(10);
                if(addPlay != null){
                    addPlayer();
                }else if(playagain != null){

                        int seat = guestRegistry.size();
                        guestRegistry.put(playagain[1], seat, playagain[2], playagain[3], "guest");
                }

            }catch (Exception e){
            }
        }
    }

    //makes it possible to add players to play
    static void addPlayer(){
        try{
            seatNumber = guestRegistry.size();
            //checks of there is a new player created and gets their id and name
            Object[] seatRequest = tableSpace.get(new ActualField("seatRequest"), new FormalField(String.class), new  FormalField(String.class));
            String guestName = (String) seatRequest[1];
            String playerId = (String) seatRequest[2];
            //checks if a player with same id exist, and then tells the tablespace
            Object[] occupiedSeating = guestRegistry.queryp(new ActualField(playerId), new FormalField(String.class));// instead of room id there should be player id
            if(occupiedSeating != null){
                text.seatTaken();
                tableSpace.put("occupiedSeat", guestName, playerId);
            } else {
                //sets up the url for the player and creates a private space for that player and creates a name for the new space to use in url
                text.seatGuest(guestName, playerId, seatNumber);
                url = ("tcp://" + ip+":"+ postalCode +"/" + guestName + playerId + "?keep");
                SequentialSpace newSeatSpace = new SequentialSpace();
                nameOfUrl = guestName + playerId;
                //put in mainSpace so that it can be referred to in the player code
                mainSpace.add(nameOfUrl, newSeatSpace);
                //put in to the guestRegistry space
                guestRegistry.put(playerId, seatNumber, guestName, url, "guest");
                //coordinates with the player to get the url for the new space
                tableSpace.put("seatNumber", playerId, guestName, url);
            }
        } catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
}
