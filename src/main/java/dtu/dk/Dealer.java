package dtu.dk;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.RemoteSpace;
import org.jspace.SequentialSpace;


public class Dealer implements Runnable {
    private SequentialSpace tableSpace;
    private SequentialSpace userInputSpace;
    private SequentialSpace guestlistSpace;
    private SequentialSpace gameSpace = new SequentialSpace();
    private Object[] currentPlayer;
    private Object[] playerMove;
    private Object[] deathPlaceHolder;
    private Object[] deathCount;
    private Card cards = new Card(null);
    ArrayList<Card> deck = cards.deck();

    private String typeOfTable;
    int amountOfPlayers;
    private int peopleAlive;
    int handSize = 4;

    RemoteSpace currentPrivatePlayerSpace;
    RemoteSpace privateSpaceOfPlayer0;
    RemoteSpace privateSpaceOfPlayer1;
    RemoteSpace privateSpaceOfPlayer2;
    RemoteSpace privateSpaceOfPlayer3;
    RemoteSpace privateSpaceOfPlayer4;
    RemoteSpace privateSpaceOfPlayer5;

    public Dealer(SequentialSpace table, SequentialSpace userInput, SequentialSpace guestlist){
        this.tableSpace = table;
        this.userInputSpace = userInput;
        this.guestlistSpace = guestlist;
    }
    public void run(){
        try {

            while(true) {
            Object[] hostResponse = userInputSpace.get(new ActualField("hostChoice"), new FormalField(String.class));
            String choice = (String) hostResponse[1];
                switch (choice) {
                    case "s": //start game
                        amountOfPlayers = (guestlistSpace.size());
                        peopleAlive = guestlistSpace.size();
                        System.out.println(guestlistSpace.size()); // print the amount of players who have entered the game
                        gameStart(amountOfPlayers);
                        break;
                    case "p": //look at participants
                        participants();
                        continue;
                    case "g": //change gamemode
                        changeGameMode();
                        continue;
                    case "k": //kick player
                        continue; //optional only if we have extra time on our hands
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }
    private void gameStart(int seats){
        int turnCounter = 0;
        try {
            tableSpace.put("gameHasStarted");
            generatePrivateSpaces(seats);
            System.out.println("Game starts!");
            System.out.println("Game mode is set to default!");
            dealCards(privateSpaceOfPlayer0, handSize); //the hand size is currently 4
            dealCards(privateSpaceOfPlayer1, handSize);
            dealCards(privateSpaceOfPlayer2, handSize);
            //dealCards(privateSpaceOfPlayer3, handSize);
            //dealCards(privateSpaceOfPlayer4, handSize);
            //dealCards(privateSpaceOfPlayer5, handSize);
            while(true){
                System.out.println(peopleAlive);
                if(peopleAlive == 1){
                    while(true)
                    {
                        whichPlayerTurn((turnCounter%seats));
                        deathPlaceHolder = currentPrivatePlayerSpace.query(new ActualField("youDied"),new ActualField(currentPlayer[2]),new ActualField(currentPlayer[0]), new FormalField(Boolean.class));
                        if(deathPlaceHolder[3].equals(true)){ //mangler condition
                            System.out.println("POISONDUCK");
                            turnCounter++;
                        }else {
                            //announce win
                            //something about a player has won
                            System.out.println("RUBBERDUCKERS");
                            System.out.println("GAME HAS ENDED");
                            tableSpace.put("gameHasEnded");
                            break;
                        }
                    }
                    break;
                }
                //type of table
                typeOfTable = "kings";
                //deal cards
                whichPlayerTurn(turnCounter % seats);
                System.out.println((String) currentPlayer[2] + " " + (String) currentPlayer[0]);
                System.out.println((String) currentPlayer[3]);

                deathPlaceHolder = currentPrivatePlayerSpace.query(new ActualField("youDied"),new ActualField(currentPlayer[2]),new ActualField(currentPlayer[0]), new FormalField(Boolean.class));

                if(deathPlaceHolder[3].equals(true)){ //mangler condition
                    turnCounter++;
                }else {
                    sendFirstTurn(currentPrivatePlayerSpace);

                    //reports to a space, (playerMove, the id of player, the seat of player, what player did, if player punch or cards)
                    gameSpace.get(new ActualField("playerMove"), new ActualField(currentPlayer[0]), new ActualField(currentPlayer[1]), new FormalField(String.class), new FormalField(String.class));
                    turnCounter++;
                    do {
                        System.out.println(peopleAlive);
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
                        whichPlayerTurn((turnCounter%seats));
                        deathPlaceHolder = currentPrivatePlayerSpace.query(new ActualField("youDied"),new ActualField(currentPlayer[2]),new ActualField(currentPlayer[0]), new FormalField(Boolean.class));
                        System.out.println("" + deathPlaceHolder[2] + deathPlaceHolder[3]);
                        System.out.println("jeg er en haffelaff");

                        if(deathPlaceHolder[3].equals(true)){
                            System.out.println("jeg er en alf");
                            turnCounter++;
                        }else {
                            //currentPrivatePlayerSpace.query(new ActualField(currentPlayer[0]), new ActualField(currentPlayer[1]), new ActualField(currentPlayer[2]), new ActualField(currentPlayer[3]),new ActualField(currentPlayer[0]), new ActualField(currentPlayer[1]),);
                            //currentPrivatePlayerSpace.ActualField("dead");
                            System.out.println((String) currentPlayer[2] + " " + (String) currentPlayer[0]);
                            System.out.println((String) currentPlayer[3]);
                            System.out.println("jeg er en troldmand fra oz");
                            sendTurn(currentPrivatePlayerSpace);

                            //anounce turn result to all players

                            gameSpace.get(new ActualField("playerMove"), new ActualField(currentPlayer[0]), new ActualField(currentPlayer[1]), new FormalField(String.class), new FormalField(String.class));
                            //if player dies the
                            TimeUnit.MILLISECONDS.sleep(50);
                            //guestlistSpace.get(new FormalField(String.class), new ActualField(turnCounter%seats), new FormalField(String.class));
                            deathCount = currentPrivatePlayerSpace.getp(new ActualField("DeathcountUp"));
                            System.out.println("WHYDUCK");
                            if(deathCount != null){
                                System.out.println("CATTYDUCK");
                                peopleAlive --;
                                System.out.println(peopleAlive);
                            }
                            turnCounter++;
                        }
                    } while (!playerMove[2].equals("punch"));
                }
            }

        } catch (Exception e) {
            // TODO: handle exception
        }
    }
    private void participants(){
        try{
            for(int i = 0; i < guestlistSpace.size(); i++){
                Object[] partici = guestlistSpace.query(new FormalField(String.class), new ActualField(i), new FormalField(String.class), new FormalField(String.class),new ActualField("guest"));
                System.out.println("Seating at seat " + i + " we have " + (String) partici[2] + " with id " + (String) partici[0]);
            }
        }catch (Exception e){

        }

    }
    private void changeGameMode(){

    }

    void generatePrivateSpaces(int seats){
        int counting = 1;
        try{
            Object[] player0 = guestlistSpace.query(new FormalField(String.class), new ActualField(0), new FormalField(String.class), new FormalField(String.class), new ActualField("guest"));
            String urlOfPlayer0 = (String) player0[3];
            privateSpaceOfPlayer0 = new RemoteSpace(urlOfPlayer0);
            counting++;
            Object[] player1 = guestlistSpace.query(new FormalField(String.class), new ActualField(1), new FormalField(String.class), new FormalField(String.class), new ActualField("guest"));
            String urlOfPlayer1 = (String) player1[3];
            privateSpaceOfPlayer1= new RemoteSpace(urlOfPlayer1);
            if(counting == seats){
                return;
            }
            counting++;
            Object[] player2 = guestlistSpace.query(new FormalField(String.class), new ActualField(2), new FormalField(String.class), new FormalField(String.class), new ActualField("guest"));
            String urlOfPlayer2 = (String) player2[3];
            privateSpaceOfPlayer2= new RemoteSpace(urlOfPlayer2);
            if(counting == seats){
                return;
            }
            counting++;
            Object[] player3 = guestlistSpace.query(new FormalField(String.class), new ActualField(3), new FormalField(String.class), new FormalField(String.class), new ActualField("guest"));
            String urlOfPlayer3 = (String) player3[3];
            privateSpaceOfPlayer3= new RemoteSpace(urlOfPlayer3);
            if(counting == seats){
                return;
            }
            counting++;
            Object[] player4 = guestlistSpace.query(new FormalField(String.class), new ActualField(4), new FormalField(String.class), new FormalField(String.class), new ActualField("guest"));
            String urlOfPlayer4 = (String) player4[3];
            privateSpaceOfPlayer4= new RemoteSpace(urlOfPlayer4);
            if(counting == seats){
                return;
            }
            counting++;
            Object[] player5 = guestlistSpace.query(new FormalField(String.class), new ActualField(5), new FormalField(String.class), new FormalField(String.class), new ActualField("guest"));
            String urlOfPlayer5 = (String) player5[3];
            privateSpaceOfPlayer5= new RemoteSpace(urlOfPlayer5);
            if(counting == seats){
                return;
            }
        }catch (Exception e){

        }

    }

    void sendTurn(RemoteSpace playerSpace){
        try{
            playerSpace.put("itIsYourTurn", typeOfTable);
            System.out.println("your turn sent");
            playerSpace.get(new ActualField("canIAction"), new ActualField((String)currentPlayer[0]));
            playerSpace.put("doAction", (String)currentPlayer[0]);
            playerSpace.put("turnType", false);
            playerMove =  playerSpace.get(new ActualField("thisIsMyAction"), new FormalField(String.class), new FormalField(String.class));
            //reports to a space, (playerMove, the id of player, the seat of player, what player did, if player punch or cards)
            gameSpace.put("playerMove", currentPlayer[0], currentPlayer[1], playerMove[1], playerMove[2]);
            System.out.println("playerMove "+ currentPlayer[0] + " " + currentPlayer[1] + " " + playerMove[1] + " " + playerMove[2]);

        }catch (Exception e){

        }

    }

    void sendFirstTurn(RemoteSpace playerSpace){
        try{
            playerSpace.put("youAreFirstTurner", typeOfTable);
            playerSpace.get(new ActualField("canIAction"), new ActualField((String)currentPlayer[0]));
            playerSpace.put("doAction", (String)currentPlayer[0]);
            playerSpace.put("turnType", true);
            playerMove =  playerSpace.get(new ActualField("thisIsMyAction"), new FormalField(String.class), new FormalField(String.class));
            //reports to a space, (playerMove, the id of player, the seat of player, what player did, if player punch or cards)
            gameSpace.put("playerMove", currentPlayer[0], currentPlayer[1], playerMove[1], playerMove[2]);
            System.out.println("playerMove "+ currentPlayer[0] + " " + currentPlayer[1] + " " + playerMove[1] + " " + playerMove[2]);

        }catch (Exception e){

        }
    }

    void whichPlayerTurn(int whichPlayer){
        switch (whichPlayer){
            case 0:
                try {
                    currentPrivatePlayerSpace = privateSpaceOfPlayer0;
                    currentPlayer = guestlistSpace.query(new FormalField(String.class), new ActualField(0), new FormalField(String.class), new FormalField(String.class), new ActualField("guest"));
                }catch (Exception e){

                }
                break;
            case 1:
                try {
                    currentPrivatePlayerSpace = privateSpaceOfPlayer1;
                    currentPlayer = guestlistSpace.query(new FormalField(String.class), new ActualField(1), new FormalField(String.class), new FormalField(String.class), new ActualField("guest"));
                }catch (Exception e){

                }
                break;
            case 2:
                try {
                    currentPrivatePlayerSpace = privateSpaceOfPlayer2;
                    currentPlayer = guestlistSpace.query(new FormalField(String.class), new ActualField(2), new FormalField(String.class), new FormalField(String.class), new ActualField("guest"));
                }catch (Exception e){

                }
                break;
            case 3:
                try {
                    currentPrivatePlayerSpace = privateSpaceOfPlayer3;
                    currentPlayer = guestlistSpace.query(new FormalField(String.class), new ActualField(3), new FormalField(String.class), new FormalField(String.class), new ActualField("guest"));
                }catch (Exception e){

                }
                break;
            case 4:
                try {
                    currentPrivatePlayerSpace = privateSpaceOfPlayer4;
                    currentPlayer = guestlistSpace.query(new FormalField(String.class), new ActualField(4), new FormalField(String.class), new FormalField(String.class), new ActualField("guest"));
                }catch (Exception e){

                }
                break;
            case 5:
                try {
                    currentPrivatePlayerSpace = privateSpaceOfPlayer5;
                    currentPlayer = guestlistSpace.query(new FormalField(String.class), new ActualField(5), new FormalField(String.class), new FormalField(String.class), new ActualField("guest"));
                }catch (Exception e){

                }
                break;
        }
    }
    void determineTypeOfTable(){
    }
        void dealCards(RemoteSpace playerCardSpace, int handSize){
        System.out.println("Deck size before dealing: " + deck.size());
        for(Card card : deck){
            System.out.print(card);
        }
        System.out.println("");
        Random random = new Random();
        for(int i = 0; i<handSize; i++){
            int randomIndex = random.nextInt(deck.size());
            Card newCard = deck.remove(randomIndex);
            System.out.println("dealt card" + newCard.toString());
            try {
                playerCardSpace.put("Card",newCard);
            } catch (Exception e) {
                System.out.println("Error " + e.getMessage());
            }
        }
        }
    void removeCards(RemoteSpace playerCardSpace){
        try {
            while(true){
            Object[] trashBin = playerCardSpace.get(new ActualField("card"),new FormalField(Card.class));
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }
}
