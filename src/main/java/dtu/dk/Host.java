package dtu.dk;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.RemoteSpace;
import org.jspace.SequentialSpace;

import java.util.Scanner;

public class Host implements  Runnable{
    RemoteSpace userInputSpace;
    private String id;
    private TextClassForAllText text = new TextClassForAllText();
    Scanner userInput = new Scanner(System.in);
    RemoteSpace spaceTables;


    private String ip;
    private String postalCode;
    public Host(String ip, String postal){
        this.ip = ip;
        this.postalCode = postal;
    }

    public  void run(){
        //add the tableSpace to main space
        //make server option available
        //starting new thread for the dealer
        Scanner userInput = new Scanner(System.in);
        try {
            userInputSpace = new RemoteSpace("tcp://" + ip+":"+ postalCode +"/userInput?keep");
            spaceTables = new RemoteSpace("tcp://" + ip+":"+ postalCode +"/table?keep");
            new Thread(new Lobby(ip, postalCode)).start();
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
                hostChoice();
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
    void hostChoice(){
        try{
            while (true){
                do {
                    text.hostInstructions();
                    userInputSpace.put("hostChoice", userInput.nextLine());
                } while (spaceTables.queryp(new ActualField("gameHasStarted")) == null);
               spaceTables.get(new ActualField("restart"));
            }
        }catch (Exception e){

        }
    }
}

