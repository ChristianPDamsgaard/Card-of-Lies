package dtu.dk;

import org.jspace.Space;

import java.util.concurrent.atomic.AtomicBoolean;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.RandomSpace;
import org.jspace.RemoteSpace;
import org.jspace.SequentialSpace;


public class Dealer implements Runnable {
    private SequentialSpace tableSpace;
    private SequentialSpace userInputSpace;
    private SequentialSpace guestlistSpace;
    private boolean gameStart = false;

    public Dealer(SequentialSpace table, SequentialSpace userInput, SequentialSpace guestlist){
        this.tableSpace = table;
        this.userInputSpace = userInput;
        this.guestlistSpace = guestlist;
    }
    public void run(){
     
        try {
            Object[] hostResponse = userInputSpace.get(new ActualField("hostChoice"), new FormalField(String.class));
            String choice = (String) hostResponse[1];
            switch (choice) {
                case "s": //start game
                    gameStart();
                    break;
                case "p": //look at participants
                    participants();
                    break;
                case "g": //change gamemode
                    changeGameMode();
                    break;
                case "k": //kick player
                    break; //optional only if we have extra time on our hands
                default:
                    break;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }
    private void gameStart(){
        int seatAmount = 2;
        int turnCounter = 0;
        System.out.println("Game starts!");
        System.out.println("Game mode is set to default!");
        try {
            while(true){
            
                Object[] turn1 = guestlistSpace.get(new FormalField(String.class), new ActualField(turnCounter%seatAmount), new FormalField(String.class));
                String urlOfTurn1 = (String) turn1[2];
                RemoteSpace turn1RemoteSpace = new RemoteSpace(urlOfTurn1);
                turn1RemoteSpace.put("yourCards", "cardInformation");
                Object[] cardsOfTurn1 = turn1RemoteSpace.get(new ActualField("choice"), new FormalField(String.class)); 
                tableSpace.put("gameplayTurnResult", turnCounter%seatAmount, (String) cardsOfTurn1[1]);
                turnCounter++;
                while(true){
                    
    
                    /*
                    * DEALER MUST DEAL CARDS TO PLAYERS 
                    * ANNOUNCE WHAT IS PLAYED
                    * START PLAYER 1 TURN
                    * \\\\\\\ PLAYER 1 PLAYS CARD
                    * \\\\\\\ PLAYER 1 ENDS TURN
                    * \\\\\\\ PLAYER 2 HAS TWO CHOICES 
                    * \\\\\\\ EITHER DETERMINE LIE OR PLAY CARD   
                    * CONTINUE TURN ORDER
                    * 
                    */
    
                    break;
                }
                
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
    }
    private void participants(){

    }
    private void changeGameMode(){

    }
    

        

}
