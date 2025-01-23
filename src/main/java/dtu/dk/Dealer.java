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
    TextClassForAllText text = new TextClassForAllText();

    private SequentialSpace tableSpace;
    private SequentialSpace userInputSpace;
    private SequentialSpace guestlistSpace;
    private SequentialSpace gameSpace = new SequentialSpace();
    private Object[] prevPlayer;
    private Object[] currentPlayer;
    private Object[] playerMove;
    private Object[] deathPlaceHolder;
    private Object[] deathCount;
    private Object[] prevAction;
    private Card cards = new Card(null);
    ArrayList<Card> deck = cards.deck();
    private boolean gameState;

    private String typeOfTable;
    private String prevPlayerMove;
    int amountOfPlayers;
    private int peopleAlive;
    int handSize = 4;
    int turnCounter;
    private String ip;
    private String postalCode;

    RemoteSpace currentPrivatePlayerSpace;
    RemoteSpace previousPrivatePlayerSpace;
    RemoteSpace privateSpaceOfPlayer0;
    RemoteSpace privateSpaceOfPlayer1;
    RemoteSpace privateSpaceOfPlayer2;
    RemoteSpace privateSpaceOfPlayer3;
    RemoteSpace privateSpaceOfPlayer4;
    RemoteSpace privateSpaceOfPlayer5;

    Random random = new Random();

    public Dealer(SequentialSpace table, SequentialSpace userInput, SequentialSpace guestlist, String ip, String postalCode){
        this.ip = ip;
        this.postalCode = postalCode;
        this.tableSpace = table;
        this.userInputSpace = userInput;
        this.guestlistSpace = guestlist;
    }
    //runs dealer when thread is created
    public void run(){
        try {
            //gets host input to do several optians
            while(true) {
                Object[] hostResponse = userInputSpace.get(new ActualField("hostChoice"), new FormalField(String.class));
                String choice = (String) hostResponse[1];
                switch (choice) {
                    //starts game
                    case "s":
                        amountOfPlayers = (guestlistSpace.size());
                        peopleAlive = guestlistSpace.size();
                        gameStart(amountOfPlayers);
                        continue;
                    //prints all participants
                    case "p":
                        participants();
                        continue;
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    //this is the game loop
    private void gameStart(int seats){
        gameState = true;
        turnCounter = 0;
        //deals the cards and connects to all player spaces there is up to 4 players that can play
        try {
            //makes a placeholder for this space
            previousPrivatePlayerSpace = new RemoteSpace("tcp://" + ip+":"+ postalCode + "/trash?keep");
            tableSpace.put("gameHasStarted");
            generatePrivateSpaces(seats);
            text.gameStart();
            //since 4 players gets card dealt up to 4 can play
            dealCards(privateSpaceOfPlayer0, handSize,deck); //the hand size is currently 4
            dealCards(privateSpaceOfPlayer1, handSize,deck);
            dealCards(privateSpaceOfPlayer2, handSize,deck);
            dealCards(privateSpaceOfPlayer3, handSize,deck);
            //if more players wants to play
            //dealCards(privateSpaceOfPlayer4, handSize);
            //dealCards(privateSpaceOfPlayer5, handSize);
            while(true){
                prevPlayerMove = "";
                if(peopleAlive == 1){
                    //if only one is alive unfreeze the previous and this player
                    previousPrivatePlayerSpace.put("doAction",(String)prevPlayer[0], false);
                    currentPrivatePlayerSpace.put("doAction",(String)currentPlayer[0], false);
                    //end game
                    while(true)
                    { 
                    deathCount = currentPrivatePlayerSpace.getp(new ActualField("DeathcountUp"));
                    if(deathCount != null){
                        peopleAlive --;
                    }
                        whichPlayerTurn((turnCounter%seats));
                        deathPlaceHolder = currentPrivatePlayerSpace.query(new ActualField("youDied"),new ActualField(currentPlayer[2]),new ActualField(currentPlayer[0]), new FormalField(Boolean.class));
                        //get the player who won by shifting through those who are unalive
                        if(deathPlaceHolder[3].equals(true)){ //mangler condition
                            turnCounter++;
                        }else {
                            //announce win
                            //something about a player has won
                            gameState = false;
                            previousPrivatePlayerSpace.put("doAction",prevPlayer[0], false);
                            previousPrivatePlayerSpace.put("otherPunchResult",false);
                            text.gameEnd();
                            tableSpace.get(new ActualField("gameHasStarted"));
                            tableSpace.put("gameHasEnded");
                            break;
                        }
                    }
                    break;
                }
                //determine type of table
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
                //check for dead players
                deathCount = tableSpace.getp(new ActualField("DeathcountUp"));
                if(deathCount != null){
                    peopleAlive --;
                }
                //chance player turn and checks if player is unalive
                whichPlayerTurn(turnCounter % seats);
                text.printPlayerInfo((String) currentPlayer[2], (String) currentPlayer[0], (String) currentPlayer[3]);
                deathPlaceHolder = currentPrivatePlayerSpace.query(new ActualField("youDied"),new ActualField(currentPlayer[2]),new ActualField(currentPlayer[0]), new FormalField(Boolean.class));
                //if unalive change player
                if(deathPlaceHolder[3].equals(true)){
                    turnCounter++;
                }else {
                    //send the first turn
                    text.printPlayerInfo((String) currentPlayer[2], (String) currentPlayer[0], (String) currentPlayer[3]);
                    sendFirstTurn(currentPrivatePlayerSpace);
                    previousPrivatePlayerSpace.put("otherPunchResult",false);
                    gameSpace.get(new ActualField("playerMove"), new ActualField(currentPlayer[0]), new ActualField(currentPlayer[1]), new FormalField(String.class), new FormalField(String.class));
                    //checks if game has ended
                    deathCount = tableSpace.getp(new ActualField("DeathcountUp"));
                    if(deathCount != null){
                        peopleAlive --;
                    }
                    if(peopleAlive == 1){
                        previousPrivatePlayerSpace.put("doAction",(String)prevPlayer[0], false);
                        currentPrivatePlayerSpace.put("doAction",(String)currentPlayer[0], false);
                        while(true)
                        {
                            deathCount = currentPrivatePlayerSpace.getp(new ActualField("DeathcountUp"));
                            if(deathCount != null){
                                peopleAlive --;
                            }
                            whichPlayerTurn((turnCounter%seats));
                            deathPlaceHolder = currentPrivatePlayerSpace.query(new ActualField("youDied"),new ActualField(currentPlayer[2]),new ActualField(currentPlayer[0]), new FormalField(Boolean.class));
                            if(deathPlaceHolder[3].equals(true)){
                                turnCounter++;
                            }else {
                                //announce win
                                //something about a player has won
                                gameState = false;
                                previousPrivatePlayerSpace.put("doAction",prevPlayer[0], false);
                                previousPrivatePlayerSpace.put("otherPunchResult",false);
                                text.gameEnd(); //Potential Carsten
                                tableSpace.get(new ActualField("gameHasStarted"));
                                tableSpace.put("gameHasEnded");
                                break;
                            }
                        }
                        break;
                    }
                    //saves previous player  and gives next turn
                    previousPrivatePlayerSpace = currentPrivatePlayerSpace;
                    prevPlayer = currentPlayer;
                    turnCounter++;
                    do {
                        //checks if game has ended and unfreeze previous player
                        if(peopleAlive == 1){
                            previousPrivatePlayerSpace.put("doAction",(String)prevPlayer[0], false);
                            currentPrivatePlayerSpace.put("doAction",(String)currentPlayer[0], false);
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
                                    gameState = false;
                                    previousPrivatePlayerSpace.put("doAction",prevPlayer[0], false);
                                    previousPrivatePlayerSpace.put("otherPunchResult",false);
                                    text.gameEnd(); //casten
                                    tableSpace.get(new ActualField("gameHasStarted"));
                                    tableSpace.put("gameHasEnded");
                                    break;
                                }
                            }
                            break;
                        }
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
                            //sends turn to next alive player
                            text.printPlayerInfo((String) currentPlayer[2], (String) currentPlayer[0], (String) currentPlayer[3]);
                            sendTurn(currentPrivatePlayerSpace);
                            TimeUnit.MILLISECONDS.sleep(50);
                            deathCount = currentPrivatePlayerSpace.getp(new ActualField("DeathcountUp"));
                            if(deathCount != null){
                                peopleAlive --;
                            }
                            //sends the move to common space
                            gameSpace.get(new ActualField("playerMove"), new ActualField(currentPlayer[0]), new ActualField(currentPlayer[1]), new FormalField(String.class), new FormalField(String.class));
                            //safe previous player and gives next turn
                            previousPrivatePlayerSpace = currentPrivatePlayerSpace;
                            prevPlayer = currentPlayer;
                            turnCounter++;
                        }
                        //if the player move was punch exit loop to start new round
                    } while (!playerMove[2].equals("punch"));
                }
                //creates new table type
                tableSpace.get(new ActualField("tableType"), new FormalField(String.class));
            }
            //when game ends remove all players from table
            guestlistSpace.getAll(new FormalField(String.class), new FormalField(Integer.class), new FormalField(String.class),new FormalField(String.class), new ActualField("guest"));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    //gets and prints all participants of the game
    private void participants(){
        try{
            for(int i = 0; i < guestlistSpace.size(); i++){
                Object[] partici = guestlistSpace.query(new FormalField(String.class), new ActualField(i), new FormalField(String.class), new FormalField(String.class),new ActualField("guest"));
                text.turnInfo(i, (String) partici[2], (String) partici[0]);
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
    //genereates a private space
    void generatePrivateSpaces(int seats){
        int counting = 1;
        //connecting to the player space by getting their url for all existing players
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
            System.out.println(e.getMessage());
        }
    }
    //sends the turn to the player
    void sendTurn(RemoteSpace playerSpace){
        try {
            // Signal the player's turn
            playerSpace.put("itIsYourTurn", typeOfTable);
            text.turnSentTo((String) currentPlayer[0]);
    
            // Wait for acknowledgment with timeout
            playerSpace.get(new ActualField("canIAction"), new ActualField((String)currentPlayer[0]));
            playerSpace.put("doAction", (String)currentPlayer[0], true);
            playerSpace.put("turnType", false);
            //get the players action
            playerMove = playerSpace.get(new ActualField("thisIsMyAction"), new FormalField(String.class), new FormalField(String.class));
            System.out.println((String) playerMove[1]);
            System.out.println((String) playerMove[2]);

            //if the player has called liar
            if(playerMove[2].equals("punch")){
                Card newcards = new Card(null);
                ArrayList<Card> newDeck = newcards.deck();
                //reset their cards
                deleteHand(privateSpaceOfPlayer0);
                deleteHand(privateSpaceOfPlayer1);
                deleteHand(privateSpaceOfPlayer2);

               System.out.println("THIS IS SEEING THE NEW DECK"); // Maybe Carsten
               for(Card card: newDeck){
                System.out.println(card);
               }
               System.out.println("DEALING NEW CARDS"); // Maybe Carsten
               dealCards(privateSpaceOfPlayer0, handSize,newDeck);
               dealCards(privateSpaceOfPlayer1, handSize,newDeck);
               dealCards(privateSpaceOfPlayer2, handSize,newDeck);

                //check if enemy lied or told the truth
                if(prevPlayerMove.equals(typeOfTable)){
                    previousPrivatePlayerSpace.put("otherPunchResult",false);
                    currentPrivatePlayerSpace.put("punchResult", true);


                }else{
                    currentPrivatePlayerSpace.put("punchResult", false);
                    previousPrivatePlayerSpace.put("otherPunchResult",true);
                }
                //if played cards
            }else{
                previousPrivatePlayerSpace.put("otherPunchResult",false);
            }
            text.playerAction((String) playerMove[1], (String) playerMove[2]);
            // Report the action to the game space
            gameSpace.put("playerMove", currentPlayer[0], currentPlayer[1], playerMove[1], playerMove[2]);
            // Signal turn completion
            gameSpace.put("turnComplete", currentPlayer[0]);
            text.turnComplete((String) currentPlayer[0]);
        } catch (Exception e) {
            System.out.println("Error in sendTurn: " + e.getMessage());
        }

    }
    //sends the first turn to player
    void sendFirstTurn(RemoteSpace playerSpace){
        //communication
        try{
            playerSpace.put("youAreFirstTurner", typeOfTable);
            playerSpace.get(new ActualField("canIAction"), new ActualField((String)currentPlayer[0]));
            playerSpace.put("doAction", (String)currentPlayer[0], true);
            playerSpace.put("turnType", true);
            playerMove =  playerSpace.get(new ActualField("thisIsMyAction"), new FormalField(String.class), new FormalField(String.class));
            //reports to a space, (playerMove, the id of player, the seat of player, what player did, if player punch or cards)
            gameSpace.put("playerMove", currentPlayer[0], currentPlayer[1], playerMove[1], playerMove[2]);
            text.printPlayerMove((String) currentPlayer[0], (Integer)currentPlayer[1], (String)playerMove[1], (String)playerMove[2]);

        }catch (Exception e){

        }
    }
    //contorls which players turn by a switch case
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
    //deals cards to the player
    void dealCards(RemoteSpace playerCardSpace, int handSize, ArrayList<Card> deck){
        System.out.println("Deck size before dealing: " + deck.size());
        for(Card card : deck){
            System.out.print(card);
        }
        System.out.println();
        Random random = new Random();
        for(int i = 0; i<handSize; i++){
            int randomIndex = random.nextInt(deck.size());
            Card newCard = deck.remove(randomIndex);
            System.out.println("Dealt card " + newCard.toString());
            try {
                playerCardSpace.put("Card",newCard);
            } catch (Exception e) {
                System.out.println("Error " + e.getMessage());
            }
        }
    }
        //removes the hand from the player
    void deleteHand(RemoteSpace playerCardSpace){
        try {
            List<Object[]> trashCan = playerCardSpace.getAll(new ActualField("Card"), new FormalField(Card.class));
        } catch (Exception e) {
            System.out.println("Error while returning cards: " + e.getMessage());
        }
    }
}
