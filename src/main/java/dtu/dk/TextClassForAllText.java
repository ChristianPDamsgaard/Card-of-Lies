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
            System.out.print(some[i]);
            try {
                TimeUnit.MILLISECONDS.sleep(25);
                textSpeed -= 5;
            }catch (Exception e){

            }
        }
        System.out.println();
        System.out.println("Please type h for host and p for participant.");
    }
    void welcomePlayer(){
        try{
            System.out.println("Welcome participant, you will now have to sign this document to ensure that you cannot sue us afterwards.");
            TimeUnit.SECONDS.sleep(1);
            System.out.print(".");
            TimeUnit.SECONDS.sleep(1);
            System.out.print(".");
            TimeUnit.SECONDS.sleep(1);
            System.out.println(".");
            TimeUnit.SECONDS.sleep(1);
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
    void wrongText(){
        System.out.println("The words you typed are not in the package of choices we gave, pls think carefully about your choices and response considering the options");
    }

}
