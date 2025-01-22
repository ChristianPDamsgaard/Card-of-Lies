package dtu.dk;


import org.jspace.*;

import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class test {

    //the main space that contains the other spaces
    //space that keeps track of the game and the players
    //space that keeps track of the players private spaces
    static TextClassForAllText text = new TextClassForAllText();
    //variables
    static String id = "";
    static String seatUrl;
    static String nameOfUrl;
    static String ip;
    static String postalCode;

    public static void main(String[] args){
        ip = "localhost";
        //ip = "10.209.242.31";
        postalCode = "42069";
        //add the tableSpace to main space
        //make server option available
        //starting new thread for the dealer
        Scanner userInput = new Scanner(System.in);
        try {
            RemoteSpace userInputSpace = new RemoteSpace("tcp://" + ip+":"+ postalCode + "/userInput?keep");
            RemoteSpace spaceTables = new RemoteSpace( "tcp://" + ip+":"+ postalCode +"/table?keep");
            System.out.println("waiting for lock");
            spaceTables.get(new ActualField("lock"));
            System.out.println("lock has been taken");
                new Thread(new Lobby(ip, postalCode)).start();
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
                    System.out.println("lock has been put back");
                    spaceTables.put("lock");

                }else if (id.equals("host")) {
                    while (true){
                        do {
                            text.hostInstructions();
                            userInputSpace.put("hostChoice", userInput.nextLine());
                        } while (spaceTables.queryp(new ActualField("gameHasStarted")) == null);
                        spaceTables.get(new ActualField("restart"));
                    }
                }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}