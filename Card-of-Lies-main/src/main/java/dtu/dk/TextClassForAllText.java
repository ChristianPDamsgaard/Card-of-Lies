package dtu.dk;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class TextClassForAllText {
    public TextClassForAllText(){

    }

    void smoothText(String myString){ //for later
        for(int i = 0; i < myString.length();i++){
            System.out.print(myString.charAt(i));
            try{
                TimeUnit.MILLISECONDS.sleep(25);

            } catch(Exception e){

            }
        }

    }
    void printCards(char[] cards){ //May not be needed
        System.out.print("[");
        for(int i = 0; i<cards.length; i++){
            if(i < cards.length-1){
                System.out.print(cards[i] + ", ");
            } else {
                System.out.print(cards[i]);
            }
        }
        System.out.print("]");
    }




    // Main
    void seatTaken(){
        String message = "This seat is already taken, please take another seat";
        smoothText(message);
        System.out.println();
    }
    void seatGuest(String guestName, String playerId, int seatNumber){
        String message = "Seating guest " + guestName + " with " + playerId + " at seat " + seatNumber;
        smoothText(message);
        System.out.println();
    }




    // Lobby
    void startPrompt(){
        String welcome = "Welcome to my humble abode, what kind of person are you dear?";
        smoothText(welcome);
        System.out.println();
        System.out.println("\u001B[0mPlease type h for host and p for participant.");
    }
    void welcomeHost(){
        String welcome = "Welcome back to the abode master.";
        smoothText(welcome);
        System.out.println();
    }
    void welcomePlayer(){
        String welcome = "Welcome participant, you will now have to sign this document to ensure that you cannot\u001B[31m sue\u001B[0m us afterwards.";
        String message = "Please sign your username here:";
        smoothText(welcome);
        System.out.println();
        smoothText(message);
        System.out.println();
    }
    void wrongText(){
        String message = "The words you typed are not in the package of choices we gave, please think carefully about your choices and response considering the options";
        smoothText(message);
        System.out.println();
    }
    void writeAnId(){
        String idText = "Please write an Id as well:";
        smoothText(idText);
        System.out.println();
    }




    // Player
    void waitingForGame(String a){
        if(a.equals("waiting")){
            System.out.println("waiting");
        }else if(a.equals("found")){
            System.out.println("found");
        }
    }
    void turnReceived(){
        String message = "Your turn received";
        smoothText(message);
        System.out.println();
    }
    void chooseCard(){
        String message = "You have to lay down one of your cards:";
        smoothText(message);
        System.out.println();
    }
    void chooseOption(char[] cards){ // Just using char for now
        String message = "Do you wish to call them out ('p') or keep playing ('c')?";
        smoothText(message);
        System.out.println();
        printCards(cards);
        System.out.println();
    }
    void playerConnected(){
        System.out.print("A player has connected to the server");
    }




    // Dealer
    void gameStart(){
        String startMessage = "Game starts!";
        String gameModeMessage = "Game mode is set to default!";
        smoothText(startMessage);
        System.out.println();
        smoothText(gameModeMessage);
        System.out.println();
    }
    void turnInfo(int turn, int seat){
        System.out.println("Turn number: " + turn);
        System.out.println("Seat: " + seat);
        System.out.println("Something to do with turn and seats: " + (turn%seat));
    }




    // test
    void hostInstructions(){
        System.out.println("Instructions there is p and s"); // Carsten can fylder bagefter!
    /* HVAD KAN HOSTEN? 
     * starte spillet? (aktiver dealer)- s
     * Se spillere og deres information (id, sædenummer & navn)- p
     * Bestemme gamemode- g
     * kick spillere (måske i fremtiden)- k
     * 
    */

    }





}

