package dtu.dk;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.RemoteSpace;
import org.jspace.SequentialSpace;

import java.util.Scanner;
import java.util.concurrent.TimeUnit;

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
            System.out.println("Welcome host press h to get your options");
            userInputSpace.put("hostResponse", userInput.nextLine().toLowerCase().replaceAll(" ", ""));
            TimeUnit.MILLISECONDS.sleep(50);
            Object[] userResponseToIdentity = userInputSpace.get(new ActualField("hostResponse"), new FormalField(String.class));
            String identityResponse = (String) userResponseToIdentity[1];
            while (true) {
                try {
                    if (identityResponse.equals("h")) {
                        //???
                        //blah blah blah
                        text.welcomeHost();
                        userInputSpace.put("personHaveId", "host");
                        break;
                    } else {
                        //error type again
                        text.wrongText();
                        userInputSpace.put("personDoNotHaveId");
                        System.out.println("Welcome host press h to get your options");
                        userInputSpace.put("hostResponse", userInput.nextLine().toLowerCase().replaceAll(" ", ""));
                    }
                    userResponseToIdentity = userInputSpace.get(new ActualField("hostResponse"), new FormalField(String.class));
                    identityResponse = (String) userResponseToIdentity[1];
                }catch (Exception e) {
                }
            }
            while (true){
                try{
                    Object[] result = userInputSpace.getp(new ActualField("personHaveId"), new FormalField(String.class));
                    if (result != null) {
                        id = (String) result[1];
                        break;
                    }else if(userInputSpace.getp(new ActualField("personDoNotHaveId")) != null){
                        userInputSpace.put("userIdentityResponse", userInput.nextLine().toLowerCase().replaceAll(" ", ""));
                    }
                }catch (Exception e){}
            }
            if (id.equals("host")) {
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

