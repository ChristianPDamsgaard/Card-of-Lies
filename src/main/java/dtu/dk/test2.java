package dtu.dk;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.RemoteSpace;
import org.jspace.SequentialSpace;

import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class test2 {
    //the main space that contains the other spaces
    //space that keeps track of the game and the players
    //space that keeps track of the players private spaces
    static SequentialSpace userInputSpace = Main.userInputSpace;
    static TextClassForAllText text = new TextClassForAllText();
    static RemoteSpace tableSpace;

    //variables
    static String id = "";
    static String seatUrl;
    static String nameOfUrl;

    public static void main(String[] args){

        //add the tableSpace to main space
        //make server option available
        //starting new thread for the dealer
        Scanner userInput = new Scanner(System.in);
        int num = 0;
        try {
            tableSpace = new RemoteSpace("tcp://localhost:42069/table?keep");
            while(true) {
                if(num >5){
                    break;
                }
                new Thread(new Lobby()).start();
                //Player player0 = new Player("frank", "frank1");
                userInputSpace.put("userIdentityResponse", userInput.nextLine().toLowerCase().replaceAll(" ", ""));
                while (true) {
                    Object[] result = userInputSpace.getp(new ActualField("personHaveId"), new FormalField(String.class));
                    if (result != null) {
                        id = (String) result[1];
                        num++;
                        System.out.println(num);
                        break;
                    }
                    if(userInputSpace.getp(new ActualField("personDoNotHaveId")) != null){
                        userInputSpace.put("userIdentityResponse", userInput.nextLine().toLowerCase().replaceAll(" ", ""));
                    }
                }
                if(id.equals("player")){
                    userInputSpace.put("userNameResponse", userInput.nextLine().replaceAll(" ", ""));
                    userInputSpace.put("userIdResponse", userInput.nextLine().replaceAll(" ", ""));
                    tableSpace.get(new ActualField("userHasConnected"));
                    System.out.println("yaaaaay ^ _ ^");

                }else if (id.equals("host")) {
                    while(true){
                        text.hostInstructions();
                        userInputSpace.put("hostChoice",userInput.nextLine());
                    }
                }
                TimeUnit.MILLISECONDS.sleep(30);
                Main.addPlayer();
            }
            //Player player1 = new Player("anne", "anneMone");
            //Player player2 = new Player("bob", "bobby");
            //Player player3 = new Player("daniel", "danielle");
            //Player player4 = new Player("peter", "peterPetersen");
            //Player player5 = new Player("lune", "luna");
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}
