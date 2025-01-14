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
    int amountOfPlayers;

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
                        System.out.println(guestlistSpace.size());
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
            while(true){
                //type of table
                typeOfTable = "kings";
                //deal cards
                whichPlayerTurn(turnCounter%seats);
                System.out.println((String) currentPlayer[2] + " " + (String) currentPlayer[0]);
                System.out.println((String) currentPlayer[3]);

                sendFirstTurn(currentPrivatePlayerSpace);

                //reports to a space, (playerMove, the id of player, the seat of player, what player did, if player punch or cards)
                gameSpace.get(new ActualField("playerMove"), new ActualField(currentPlayer[0]), new ActualField(currentPlayer[1]), new FormalField(String.class), new FormalField(String.class));
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
                    System.out.println(turnCounter);
                    System.out.println(seats);
                    System.out.println((turnCounter%seats));
                    whichPlayerTurn((turnCounter%seats));

                    currentPrivatePlayerSpace.query(new ActualField(currentPlayer[0]), new ActualField(currentPlayer[1]), new ActualField(currentPlayer[2]), new ActualField(currentPlayer[3]),new ActualField(currentPlayer[0]), new ActualField(currentPlayer[1]),);
                    currentPrivatePlayerSpace.ActualField("dead");
                    if(){
                        turnCounter++;
                    }else{
                        System.out.println((String) currentPlayer[2] + " " + (String) currentPlayer[0]);
                        System.out.println((String) currentPlayer[3]);
                        sendTurn(currentPrivatePlayerSpace);

                        //anounce turn result to all players

                        gameSpace.get(new ActualField("playerMove"), new ActualField(currentPlayer[0]), new ActualField(currentPlayer[1]), new FormalField(String.class), new FormalField(String.class));
                        //if player dies the
                        //guestlistSpace.get(new FormalField(String.class), new ActualField(turnCounter%seats), new FormalField(String.class));
                        turnCounter++;
                    }

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
        try{
            System.out.println(guestlistSpace.queryAll(new FormalField(String.class), new FormalField(Integer.class), new FormalField(String.class), new FormalField(String.class), new ActualField("guest")));
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
            System.out.println("GHOSTDUCK");
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
            System.out.println("BUTTERDUCK");
            playerSpace.get(new ActualField("canIAction"), new ActualField((String)currentPlayer[0]));
            System.out.println("GHOSTDUCK");
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
                    System.out.println("TABLEDUCK");
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

    public void life(){

    }



}
