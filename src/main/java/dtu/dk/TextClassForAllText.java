package dtu.dk;

import java.util.concurrent.TimeUnit;

public class TextClassForAllText {
    public TextClassForAllText(){

    }

    void smoothText(String myString){ //used to make it seem as if the text is being written
        for(int i = 0; i < myString.length();i++){
            System.out.print(myString.charAt(i));
            try{
                TimeUnit.MILLISECONDS.sleep(25);

            } catch(Exception e){}
        }
    }
    void smoothTextFast(String myString){ //faster version of smoothText
        for(int i = 0; i < myString.length();i++){
            System.out.print(myString.charAt(i));
            try{
                TimeUnit.MILLISECONDS.sleep(10);

            } catch(Exception e){}
        }
    }

    //Each function is used in the category they are under
    //For example is seatTaken used under the Main class
    //Most are used simply to add the smoothText effect

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
        System.out.println("\u001B[0mPlease type p for participant.");
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
    void typeOfTable(String typeOfTable) {
        String message = "It is the " + typeOfTable + " table";
        smoothText(message);
        System.out.println();
    }
    void firstTurn(){
        String message = "Because this is the first turn, you can only choose one of your cards.";
        smoothText(message);
        System.out.println();
    }
    void otherTurn(){
        String message = "Your turn!";
        String message2 = "You have the following cards:";
        smoothText(message);
        System.out.println();
        smoothText(message2);
        System.out.println();
    }
    void turnPlayed(){
        String message = "You have now played your turn.";
        smoothText(message);
        System.out.println();
    }
    void chooseOption(){ // Just using char for now
        String message = "Do you wish to call them out ('p') or keep playing ('c')?";
        smoothText(message);
        System.out.println();
    }
    void playerConnected(){
        System.out.print("A player has connected to the server");
    }
    void invalidChoice(){
        String message = "Invalid choice. Try again.";
        smoothText(message);
        System.out.println();
    }
    void playCard(){
        String message = "\nPlay a card";
        smoothText(message);
        System.out.println();
    }
    void playerDied(){
        String message = "You have died. Please sit back and wait until game has ended";
        smoothText(message);
        System.out.println();
    }
    void gameDone(){
        String message = "Game has ended!";
        smoothText(message);
        System.out.println();
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
    void gameEnd(){
        String message = "GAME HAS ENDED!";
        smoothText(message);
        System.out.println();
    }
    void turnInfo(int seat, String name, String id){
        String message = "Seating at seat " + seat + " we have " + name + " with id " + id;
        smoothTextFast(message);
        System.out.println();
    }
    void printPlayerInfo(String name, String id, String url){
        System.out.println(name + " " + id);
        System.out.println(url);
    }
    void turnSentTo(String player){
        String message = "Your turn sent to: " + player;
        smoothText(message);
        System.out.println();
    }
    void playerAction(String playerMove1, String playerMove2){
        String message = "Player action: " + playerMove1 + ", " + playerMove2;
        smoothText(message);
        System.out.println();
    }
    void turnComplete(String player){
        String message = "Turn completed for player: " + player;
        smoothText(message);
        System.out.println();
    }
    void printPlayerMove(String currentPlayer1, Integer currentPlayer2, String playerMove1, String playerMove2){
        String message = "playerMove "+ currentPlayer1 + " " + currentPlayer2 + " " + playerMove1 + " " + playerMove2;
        smoothText(message);
        System.out.println();
    }


    // Host
    void hostInstructions(){
        System.out.println("As Host you can start the game ('s') or see information on contestants ('p')"); // Carsten can fylde bagefter!
    }
}
