package dtu.dk;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.RemoteSpace;
import org.jspace.SequentialSpace;

import java.util.Scanner;

public class Host implements  Runnable{
    private RemoteSpace userInputSpace;
    private String id;
    private TextClassForAllText text = new TextClassForAllText();
    Scanner userInput = new Scanner(System.in);

    public Host(){
    }

    public  void run(){
        //add the tableSpace to main space
        //make server option available
        //starting new thread for the dealer
        Scanner userInput = new Scanner(System.in);
        try {
            RemoteSpace userInputSpace = new RemoteSpace("tcp://localhost:42069/userInput?keep");
            RemoteSpace spaceTables = new RemoteSpace("tcp://localhost:42069/table?keep");
            new Thread(new Lobby()).start();
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
            }if (id.equals("host")) {
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
