package dtu.dk;

import org.jspace.*;

import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Lobby implements  Runnable{
    //Space and other variables
    private String ip;
    private String postal;
    TextClassForAllText text = new TextClassForAllText();
    RemoteSpace userInputSpace;

    //make another thread that creates a player, but in the lobby
    public Lobby(String ip, String postal){
        this.ip = ip;
        this.postal = postal;
        try { //create remoteSpace connected to the ip
            this.userInputSpace = new RemoteSpace("tcp://" + ip + ":" + postal + "/userInput?keep");
        }catch (Exception e){

        }
    }

    public void startLobby(){ //obtain players username
        while (true){
            text.startPrompt(); // small introduction
            try {
                Object[] userResponseToIdentity = userInputSpace.get(new ActualField("userIdentityResponse"), new FormalField(String.class));
                String identityResponse = (String) userResponseToIdentity[1]; //gets users response
                if(identityResponse.equals("p")){
                    //make player
                    text.welcomePlayer(); //write username instruction
                    userInputSpace.put("personHaveId", "player");
                    createPlayer();
                    break;
                }else{
                    //error type again
                    text.wrongText();
                    userInputSpace.put("personDoNotHaveId");
                }
            }catch (Exception e){
                System.out.println(e.getMessage());
            }
        }
    }

    public void createPlayer(){ //obtain players id and thereby creates layer
        try{
            Object[] userResponseToName = userInputSpace.get(new ActualField("userNameResponse"), new FormalField(String.class));
            String name = (String) userResponseToName[1]; //gets username
            text.writeAnId();
            Object[] userResponseToId = userInputSpace.get(new ActualField("userIdResponse"), new FormalField(String.class));
            String id = (String) userResponseToId[1]; //gets id
            new Thread(new Player(name, id, ip, postal)).start(); // creates new player thread
        }catch (Exception e){
            System.out.println(e.getMessage()); //error
        }
    }

    public void run(){
        startLobby();
    }
}
