package dtu.dk;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.RemoteSpace;

import java.util.Scanner;

public class test1 {

    //the main space that contains the other spaces
    //space that keeps track of the game and the players
    //space that keeps track of the players private spaces
    static TextClassForAllText text = new TextClassForAllText();
    //variables
    static String id = "";
    static String seatUrl;
    static String nameOfUrl;

    public static void main(String[] args){

        //add the tableSpace to main space
        //make server option available
        //starting new thread for the dealer
        Scanner userInput = new Scanner(System.in);
        try {
            RemoteSpace userInputSpace = new RemoteSpace("tcp://localhost:42069/userInput?keep");
            RemoteSpace spaceTables = new RemoteSpace("tcp://localhost:42069/table?keep");
            new Thread(new Lobby()).start();
            //Player player0 = new Player("frank", "frank1");
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
                spaceTables.put("addPlayer");
                spaceTables.get(new ActualField("userHasConnected"));
                System.out.println("yaaaaay ^ _ ^");

            }else if (id.equals("host")) {
                do {
                    text.hostInstructions();
                    userInputSpace.put("hostChoice", userInput.nextLine());
                } while (spaceTables.queryp(new ActualField("gameHasStarted")) == null);
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}
