package dtu.dk;

import org.jspace.*;

import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Lobby implements  Runnable{
    TextClassForAllText text = new TextClassForAllText();
    RemoteSpace userInputSpace;

    //make another thread that creates a player, but in the lobby
    public Lobby(){
        try {
            this.userInputSpace = new RemoteSpace("tcp://10.209.242.31:42069/userInput?keep");
        }catch (Exception e){

        }
    }

    public void startLobby(){
        while (true){
            text.startPrompt();
            try {
                Object[] userResponseToIdentity = userInputSpace.get(new ActualField("userIdentityResponse"), new FormalField(String.class));
                String identityResponse = (String) userResponseToIdentity[1];
                if(identityResponse.equals("h")){
                    //???
                    //blah blah blah
                    text.welcomeHost();
                    userInputSpace.put("personHaveId", "host");
                    break;
                }else if(identityResponse.equals("p")){
                    //make player
                    //blah blah blah
                    text.welcomePlayer();
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

    public void createPlayer(){
        try{
            Object[] userResponseToName = userInputSpace.get(new ActualField("userNameResponse"), new FormalField(String.class));
            String name = (String) userResponseToName[1];
            text.writeAnId();
            Object[] userResponseToId = userInputSpace.get(new ActualField("userIdResponse"), new FormalField(String.class));
            String id = (String) userResponseToId[1];
            new Thread(new Player(name, id)).start();
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public void run(){
        startLobby();
    }
    
}
