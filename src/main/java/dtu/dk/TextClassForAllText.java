package dtu.dk;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class TextClassForAllText {
    public TextClassForAllText(){

    }

    void startPrompt(){
        String welcome = "Welcome to my humble abode, what kind of person are you dear?";
        String[] some = welcome.split("");
        int textSpeed = 250;
        for(int i = 0; i < some.length; i++){
            if(textSpeed <= 0){
                textSpeed = 0;
            }
            System.out.print("\u001B[33m"+some[i]);
            try {
                TimeUnit.MILLISECONDS.sleep(25);
                textSpeed -= 5;
            }catch (Exception e){

            }
        }
        System.out.println();
        System.out.println("\u001B[0mPlease type h for host and p for participant.");
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


    void welcomePlayer(){
        try{
            System.out.println("Welcome participant, you will now have to sign this document to ensure that you cannot\u001B[31m sue\u001B[0m us afterwards.");
            /*
            TimeUnit.SECONDS.sleep(1);
            System.out.print(".");
            TimeUnit.SECONDS.sleep(1);
            System.out.print(".");
            TimeUnit.SECONDS.sleep(1);
            System.out.println(".");
            TimeUnit.SECONDS.sleep(1);*/
            System.out.println("Now you will have to give us your name");
        }catch (Exception e){

        }
    }
    void writeAnId(){
        //something about write id
        System.out.println("id");
    }
    void welcomeHost(){
        try{
            System.out.print("Welcome ");                TimeUnit.MILLISECONDS.sleep(250);
            System.out.print("back ");                   TimeUnit.MILLISECONDS.sleep(250);
            System.out.print("to ");                     TimeUnit.MILLISECONDS.sleep(250);
            System.out.print("the ");                    TimeUnit.MILLISECONDS.sleep(250);
            System.out.print("abode ");                  TimeUnit.MILLISECONDS.sleep(250);
            System.out.println("master.");               TimeUnit.MILLISECONDS.sleep(250);
        }catch (Exception e){

        }
    }
    void hostInstructions(){
        System.out.println("Instructions"); // Carsten can fylder bagefter! 
    /* HVAD KAN HOSTEN? 
     * starte spillet? (aktiver dealer)- s
     * Se spillere og deres information (id, sædenummer & navn)- p
     * Bestemme gamemode- g
     * kick spillere (måske i fremtiden)- k
     * 
    */

    }

    void wrongText(){
        System.out.println("The words you typed are not in the package of choices we gave, pls think carefully about your choices and response considering the options");
    }
    //game text
    void waitingForGame(String a){
        if(a.equals("waiting")){
            System.out.println("waiting");
        }else if(a.equals("found")){
            System.out.println("found");
        }
    }


}
