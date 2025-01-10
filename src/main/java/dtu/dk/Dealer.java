package dtu.dk;

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

    private String typeOfTable;
    int amountOfPlayeres;
    RemoteSpace currentPrivatePlayerSpace;
    RemoteSpace privateSpaceOfPlayer0;
    RemoteSpace privateSpaceOfPlayer1;
    RemoteSpace privateSpaceOfPlayer2;
    RemoteSpace privateSpaceOfPlayer3;
    RemoteSpace privateSpaceOfPlayer4;
    RemoteSpace privateSpaceOfPlayer5;

    private boolean gameStart = false;

    public Dealer(SequentialSpace table, SequentialSpace userInput, SequentialSpace guestlist){
        this.tableSpace = table;
        this.userInputSpace = userInput;
        this.guestlistSpace = guestlist;
    }
    public void run(){
        try {
            amountOfPlayeres = (guestlistSpace.size()-1);
            Object[] hostResponse = userInputSpace.get(new ActualField("hostChoice"), new FormalField(String.class));
            String choice = (String) hostResponse[1];
            switch (choice) {
                case "s": //start game
                    gameStart(amountOfPlayeres);
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
    private void gameStart(int seats){

        int turnCounter = 0;
        Boolean firstTurn = false;


        try {
            tableSpace.put("gameHasStarted");
            generatePrivateSpaces();
            System.out.println("Game starts!");
            System.out.println("Game mode is set to default!");
            while(true){
                //deal cards
                firstTurn = true;
                whichPlayerTurn(turnCounter%seats);
                sendFirstTurn(currentPrivatePlayerSpace);

                //anounce turn result to all players
                turnCounter++;
                do {

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

                    firstTurn = false;
                    whichPlayerTurn(turnCounter % seats);
                    sendTurn(currentPrivatePlayerSpace);
                    //anounce turn result to all players

                    //if player dies the
                    //guestlistSpace.get(new FormalField(String.class), new ActualField(turnCounter%seats), new FormalField(String.class));

                } while (!playerMove[2].equals("punch"));
                if(guestlistSpace.size() < 2){
                    //announce win
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

    void generatePrivateSpaces(){
        try{
            Object[] player0 = guestlistSpace.query(new FormalField(String.class), new ActualField(0), new FormalField(String.class));
            String urlOfPlayer0 = (String) player0[2];
            RemoteSpace privateSpaceOfPlayer0 = new RemoteSpace(urlOfPlayer0);
            Object[] player1 = guestlistSpace.query(new FormalField(String.class), new ActualField(1), new FormalField(String.class));
            String urlOfPlayer1 = (String) player1[2];
            RemoteSpace privateSpaceOfPlayer1= new RemoteSpace(urlOfPlayer1);
            Object[] player2 = guestlistSpace.query(new FormalField(String.class), new ActualField(2), new FormalField(String.class));
            String urlOfPlayer2 = (String) player2[2];
            RemoteSpace privateSpaceOfPlayer2= new RemoteSpace(urlOfPlayer2);
            Object[] player3 = guestlistSpace.query(new FormalField(String.class), new ActualField(3), new FormalField(String.class));
            String urlOfPlayer3 = (String) player3[2];
            RemoteSpace privateSpaceOfPlayer3= new RemoteSpace(urlOfPlayer3);
            Object[] player4 = guestlistSpace.query(new FormalField(String.class), new ActualField(4), new FormalField(String.class));
            String urlOfPlayer4 = (String) player4[2];
            RemoteSpace privateSpaceOfPlayer4= new RemoteSpace(urlOfPlayer4);
            Object[] player5 = guestlistSpace.query(new FormalField(String.class), new ActualField(5), new FormalField(String.class));
            String urlOfPlayer5 = (String) player5[2];
            RemoteSpace privateSpaceOfPlayer5= new RemoteSpace(urlOfPlayer5);
        }catch (Exception e){

        }

    }

    void sendTurn(RemoteSpace playerSpace){
        try{
            playerSpace.put("itIsYourTurn", typeOfTable);
            playerMove =  playerSpace.get(new ActualField("thisIsMyAction"), new FormalField(String.class), new FormalField(String.class));
            //reports to a space, (playerMove, the id of player, the seat of player, what player did, if player punch or cards)
            gameSpace.put("playerMove", currentPlayer[0], currentPlayer[1], playerMove[1], playerMove[2]);
        }catch (Exception e){

        }

    }

    void sendFirstTurn(RemoteSpace playerSpace){
        try{
            playerSpace.put("youAreFirstTurner", typeOfTable);
            playerMove =  playerSpace.get(new ActualField("thisIsMyAction"), new FormalField(String.class), new FormalField(String.class));
            //reports to a space, (playerMove, the id of player, the seat of player, what player did, if player punch or cards)
            gameSpace.put("playerMove", currentPlayer[0], currentPlayer[1], playerMove[1], playerMove[2]);


        }catch (Exception e){

        }

    }

    void whichPlayerTurn(int whichPlayer){
        switch (whichPlayer){
            case 0:
                try {
                    currentPrivatePlayerSpace = privateSpaceOfPlayer0;
                    currentPlayer = guestlistSpace.query(new FormalField(String.class), new ActualField(0), new FormalField(String.class));
                }catch (Exception e){

                }
                break;
            case 1:
                try {
                    currentPrivatePlayerSpace = privateSpaceOfPlayer1;
                    currentPlayer = guestlistSpace.query(new FormalField(String.class), new ActualField(1), new FormalField(String.class));
                }catch (Exception e){

                }
                break;
            case 2:
                try {
                    currentPrivatePlayerSpace = privateSpaceOfPlayer2;
                    currentPlayer = guestlistSpace.query(new FormalField(String.class), new ActualField(2), new FormalField(String.class));
                }catch (Exception e){

                }
                break;
            case 3:
                try {
                    currentPrivatePlayerSpace = privateSpaceOfPlayer3;
                    currentPlayer = guestlistSpace.query(new FormalField(String.class), new ActualField(3), new FormalField(String.class));
                }catch (Exception e){

                }
                break;
            case 4:
                try {
                    currentPrivatePlayerSpace = privateSpaceOfPlayer4;
                    currentPlayer = guestlistSpace.query(new FormalField(String.class), new ActualField(4), new FormalField(String.class));
                }catch (Exception e){

                }
                break;
            case 5:
                try {
                    currentPrivatePlayerSpace = privateSpaceOfPlayer5;
                    currentPlayer = guestlistSpace.query(new FormalField(String.class), new ActualField(5), new FormalField(String.class));
                }catch (Exception e){

                }
                break;
        }
    }

    void determineTypeOfTable(){

    }
}
