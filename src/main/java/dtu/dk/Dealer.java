package dtu.dk;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.List;

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
    private Object[] prevAction;
    private Card cards = new Card(null);
    ArrayList<Card> deck = cards.deck();

    private String typeOfTable;
    private String prevPlayerMove;
    int amountOfPlayers;
    private int peopleAlive;
    int handSize = 4;

    RemoteSpace currentPrivatePlayerSpace;
    RemoteSpace previousPrivatePlayerSpace;
    RemoteSpace privateSpaceOfPlayer0;
    RemoteSpace privateSpaceOfPlayer1;
    RemoteSpace privateSpaceOfPlayer2;
    RemoteSpace privateSpaceOfPlayer3;
    RemoteSpace privateSpaceOfPlayer4;
    RemoteSpace privateSpaceOfPlayer5;

    Random random = new Random();

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
                        continue;
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
            dealCards(privateSpaceOfPlayer0, handSize,deck); //the hand size is currently 4
            dealCards(privateSpaceOfPlayer1, handSize,deck);
            dealCards(privateSpaceOfPlayer2, handSize,deck);
            dealCards(privateSpaceOfPlayer3, handSize,deck);

            //dealCards(privateSpaceOfPlayer4, handSize);
            //dealCards(privateSpaceOfPlayer5, handSize);
            while(true){
                prevPlayerMove = "";
                System.out.println(peopleAlive);
                if(peopleAlive == 1){
                    while(true)
                    { 
                    deathCount = currentPrivatePlayerSpace.getp(new ActualField("DeathcountUp"));
                    if(deathCount != null){
                        peopleAlive --;
                        System.out.println(peopleAlive);
                    }
                        whichPlayerTurn((turnCounter%seats));
                        deathPlaceHolder = currentPrivatePlayerSpace.query(new ActualField("youDied"),new ActualField(currentPlayer[2]),new ActualField(currentPlayer[0]), new FormalField(Boolean.class));
                        if(deathPlaceHolder[3].equals(true)){ //mangler condition
                            turnCounter++;
                        }else {
                            //announce win
                            //something about a player has won
                            guestlistSpace.getAll();
                            System.out.println("GAME HAS ENDED"); //casten
                            tableSpace.put("gameHasEnded");
                            break;
                        }
                    }
                    break;
                }
                //type of table
               // ArrayList<Card> newDeck = gainCards(tableSpace, deck);
                int tableInt = random.nextInt(1,5);
                switch (tableInt){
                    case 1:
                        typeOfTable = "KING";
                        break;
                    case 2:
                        typeOfTable = "QUEEN";
                        break;
                    case 3:
                        typeOfTable = "ACE";
                        break;
                    case 4:
                        typeOfTable = "JACK";
                        break;
                }
                tableSpace.put("tableType", typeOfTable);
                deathCount = tableSpace.getp(new ActualField("DeathcountUp"));
                if(deathCount != null){
                    peopleAlive --;
                    System.out.println(peopleAlive);
                }

                //deal cards
                whichPlayerTurn(turnCounter % seats);
                System.out.println((String) currentPlayer[2] + " " + (String) currentPlayer[0]);
                System.out.println((String) currentPlayer[3]);

                deathPlaceHolder = currentPrivatePlayerSpace.query(new ActualField("youDied"),new ActualField(currentPlayer[2]),new ActualField(currentPlayer[0]), new FormalField(Boolean.class));

                if(deathPlaceHolder[3].equals(true)){ //mangler condition
                    turnCounter++;
                }else {
                    System.out.println((String) currentPlayer[2] + " " + (String) currentPlayer[0]);
                    System.out.println((String) currentPlayer[3]);
                    sendFirstTurn(currentPrivatePlayerSpace);
                    //reports to a space, (playerMove, the id of player, the seat of player, what player did, if player punch or cards)
                    gameSpace.get(new ActualField("playerMove"), new ActualField(currentPlayer[0]), new ActualField(currentPlayer[1]), new FormalField(String.class), new FormalField(String.class));
                    previousPrivatePlayerSpace = currentPrivatePlayerSpace;
                    turnCounter++;
                    do {
                        System.out.println(peopleAlive);
                        if(peopleAlive == 1){
                            while(true)
                            {
                                whichPlayerTurn((turnCounter%seats));
                                deathPlaceHolder = currentPrivatePlayerSpace.query(new ActualField("youDied"),new ActualField(currentPlayer[2]),new ActualField(currentPlayer[0]), new FormalField(Boolean.class));
                                if(deathPlaceHolder[3].equals(true)){ //mangler condition
                                    turnCounter++;
                                }else {
                                    //announce win
                                    //something about a player has won
                                    guestlistSpace.getAll();
                                    System.out.println("GAME HAS ENDED"); //casten
                                    tableSpace.put("gameHasEnded");
                                    break;
                                }
                            }
                            break;
                        }
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
                        //taking previous player turn move
                        prevAction = playerMove;
                        TimeUnit.MILLISECONDS.sleep(25);
                        if(prevAction != null){
                            prevPlayerMove = (String)prevAction[1];
                        }



                        //who has next turn
                        whichPlayerTurn((turnCounter%seats));
                        deathPlaceHolder = currentPrivatePlayerSpace.query(new ActualField("youDied"),new ActualField(currentPlayer[2]),new ActualField(currentPlayer[0]), new FormalField(Boolean.class));
                        System.out.println("" + deathPlaceHolder[2] + deathPlaceHolder[3]);

                        if(deathPlaceHolder[3].equals(true)){
                            turnCounter++;
                        }else {
                            //currentPrivatePlayerSpace.query(new ActualField(currentPlayer[0]), new ActualField(currentPlayer[1]), new ActualField(currentPlayer[2]), new ActualField(currentPlayer[3]),new ActualField(currentPlayer[0]), new ActualField(currentPlayer[1]),);
                            //currentPrivatePlayerSpace.ActualField("dead");
                            System.out.println((String) currentPlayer[2] + " " + (String) currentPlayer[0]);
                            System.out.println((String) currentPlayer[3]);
                            sendTurn(currentPrivatePlayerSpace);
                            TimeUnit.MILLISECONDS.sleep(50);
                            //guestlistSpace.get(new FormalField(String.class), new ActualField(turnCounter%seats), new FormalField(String.class));
                            deathCount = currentPrivatePlayerSpace.getp(new ActualField("DeathcountUp"));
                            if(deathCount != null){
                                peopleAlive --;
                                System.out.println(peopleAlive);
                            }
                            //anounce turn result to all players
                            System.out.println("the player should make a move");
                            gameSpace.get(new ActualField("playerMove"), new ActualField(currentPlayer[0]), new ActualField(currentPlayer[1]), new FormalField(String.class), new FormalField(String.class));
                            
                            //if player dies the
                            previousPrivatePlayerSpace = currentPrivatePlayerSpace;
                            turnCounter++;
                        }
                    } while (!playerMove[2].equals("punch"));
                    

                    
                }
                tableSpace.get(new ActualField("tableType"), new FormalField(String.class));
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
        try {
            // Signal the player's turn
            playerSpace.put("itIsYourTurn", typeOfTable);
            System.out.println("Your turn sent to: " + currentPlayer[0]);
    
            // Wait for acknowledgment with timeout
            playerSpace.get(new ActualField("canIAction"), new ActualField((String)currentPlayer[0]));
            playerSpace.put("doAction", (String)currentPlayer[0]);
            playerSpace.put("turnType", false);
            playerMove = playerSpace.get(new ActualField("thisIsMyAction"), new FormalField(String.class), new FormalField(String.class));
            System.out.println((String)playerMove[1]);
           // System.out.println("THIS IS A TEST TEST TEST");
            System.out.println((String) playerMove[2]);
            if(playerMove[2].equals("punch")){
                Card newcards = new Card(null);
                ArrayList<Card> newDeck = newcards.deck();
                System.out.println("Are we there yet?????");
                System.out.println("WHAT ABOUT NOW!!!!!!!!!!");
                deleteHand(privateSpaceOfPlayer0);
                deleteHand(privateSpaceOfPlayer1);
                deleteHand(privateSpaceOfPlayer2);
                //removeCards(privateSpaceOfPlayer3);
                //removeCards(privateSpaceOfPlayer4);
               //removeCards(privateSpaceOfPlayer5);
               System.out.println("THIS IS SEEING THE NEW DECK");
               for(Card card: newDeck){
                System.out.println(card);
               }
               System.out.println("DEALING NEW CARDS");
               dealCards(privateSpaceOfPlayer0, handSize,newDeck);
               dealCards(privateSpaceOfPlayer1, handSize,newDeck);
               dealCards(privateSpaceOfPlayer2, handSize,newDeck);
//               dealCards(privateSpaceOfPlayer3, handSize);
//               dealCards(privateSpaceOfPlayer4, handSize);
//               dealCards(privateSpaceOfPlayer5, handSize);

                if(prevPlayerMove.equals(typeOfTable)){
                    currentPrivatePlayerSpace.put("punchResult", true);
                    previousPrivatePlayerSpace.put("otherPunchResult",false);

                }else{
                    currentPrivatePlayerSpace.put("punchResult", false);
                    previousPrivatePlayerSpace.put("otherPunchResult",true);
                }
            }else{
                previousPrivatePlayerSpace.put("otherPunchResult",false);
            }
            System.out.println("Player action: " + playerMove[1] + ", " + playerMove[2]);
            // Report the action to the game space
            gameSpace.put("playerMove", currentPlayer[0], currentPlayer[1], playerMove[1], playerMove[2]);

            // Signal turn completion
            gameSpace.put("turnComplete", currentPlayer[0]);
            System.out.println("Turn completed for player: " + currentPlayer[0]);
        } catch (Exception e) {
            System.out.println("Error in sendTurn: " + e.getMessage());
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
            previousPrivatePlayerSpace.put("otherPunchResult",false);
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

    void dealCards(RemoteSpace playerCardSpace, int handSize, ArrayList<Card> deck){
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
        
    void deleteHand(RemoteSpace playerCardSpace){
        try {
            List<Object[]> trashCan = playerCardSpace.getAll(new ActualField("Card"), new FormalField(Card.class));
            
        } catch (Exception e) {
            System.out.println("Error while returning cards: " + e.getMessage());
        }
    }


    
}
