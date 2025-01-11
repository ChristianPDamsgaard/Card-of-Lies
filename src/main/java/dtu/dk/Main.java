package dtu.dk;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.SequentialSpace;
import org.jspace.SpaceRepository;

import java.util.Arrays;
import java.util.Scanner;

public class Main {
    //the main space that contains the other spaces
    static SpaceRepository mainSpace = new SpaceRepository();
    //space that keeps track of the game and the players
    static SequentialSpace tableSpace = new SequentialSpace();
    //space that keeps track of the players private spaces
    static SequentialSpace guestRegistry = new SequentialSpace();
    static SequentialSpace userInputSpace = new SequentialSpace();
    static TextClassForAllText text = new TextClassForAllText();

    //variables
    static int seatNumber = guestRegistry.size();
    static String id = "";
    static String seatUrl;
    static String nameOfUrl;

    public static void main(String[] args){
        System.out.println(1%4);
        //add the tableSpace to main space
        mainSpace.add("table",tableSpace);
        mainSpace.add("userInput", userInputSpace);
        //make server option available
        mainSpace.addGate("tcp://localhost:42069/?keep");
        //starting new thread for the dealer
        new Thread(new Dealer(tableSpace, userInputSpace,guestRegistry)).start();

        Scanner userInput = new Scanner(System.in);
        while(true){
            try {
                tableSpace.get(new ActualField("addPlayer"));
                addPlayer();
            }catch (Exception e){
            }
        }
        /*
        try {
            new Thread(new Lobby(userInputSpace)).start();
            userInputSpace.put("userIdentityResponse", userInput.nextLine().toLowerCase().replaceAll(" ", ""));
            while (true) {
                Object[] result = userInputSpace.getp(new ActualField("personHaveId"), new FormalField(String.class));
                if (result != null) {
                    id = (String) result[1];
                    break;
                }
                if(userInputSpace.getp(new ActualField("personDoNotHaveId")) != null){
                    userInputSpace.put("userIdentityResponse", userInput.nextLine().toLowerCase().replaceAll(" ", ""));
                }
            }
            if(id.equals("player")){
                userInputSpace.put("userNameResponse", userInput.nextLine().replaceAll(" ", ""));
                userInputSpace.put("userIdResponse", userInput.nextLine().replaceAll(" ", ""));
                addPlayer();
                tableSpace.get(new ActualField("userHasConnected"));
                System.out.println("yaaaaay ^ _ ^");

            }else if (id.equals("host")) {
                while(true){
                text.hostInstructions();
                userInputSpace.put("hostChoice",userInput.nextLine());
                }
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
         */
    }

    //makes it possible to add players to play
    static void addPlayer(){
        try{
            //checks of there is a new player created and gets their id and name
            Object[] seatRequest = tableSpace.get(new ActualField("seatRequest"), new FormalField(String.class), new  FormalField(String.class));
            String guestName = (String) seatRequest[1];
            String playerId = (String) seatRequest[2];

            //checks if a player with same id exist, and then tells the tablespace
            Object[] occupiedSeating = guestRegistry.queryp(new ActualField(playerId), new FormalField(String.class));// instead of room id there should be player id
            if(occupiedSeating != null){
                System.out.println("This seat is already taken, please take another seat");
                tableSpace.put("occupiedSeat", guestName, playerId);
            } else {
                //sets up the url for the player and creates a private space for that player and creates a name for the new space to use in url
                System.out.println("Seating guest " + guestName + " with " + playerId + " at seat " + seatNumber);
                seatUrl = ("tcp://localhost:42069/seat" + seatNumber + "?keep");
                SequentialSpace newSeatSpace = new SequentialSpace();
                nameOfUrl = "seat" + seatNumber;
                //put in mainSpace so that it can be referred to in the player code
                mainSpace.add(nameOfUrl, newSeatSpace);
                //put in to the guestRegistry space
                guestRegistry.put(playerId, seatNumber, guestName, seatUrl, "guest");
                //increase seatnumber to the next guest
                seatNumber++;
                //coordinates with the player to get the url for the new space
                tableSpace.put("seatNumber", playerId, guestName, seatUrl);
            }
        } catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

}
