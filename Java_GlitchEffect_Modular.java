
import java.util.ArrayList;
import java.util.Random;


/*
  HOW TO USE - Written on Line 105 (At the end of Code)
*/

class GlitchyAnimation{
    static Random random = new Random();
    static int threshHoldStarting = 20;  // Can Change if you want how much time you want it to end (Starting) (Only Optional)
    static int threshHoldEnding = 31;    // Can Change if you want how much time you want it to end (Ending) (Only Optional)


    public static void play(String word){
           
        String ranWord = getRanWord(word, null);

        //showLoadingEffect();

                
        int attempt = 0;
        int threshHold = random.nextInt(threshHoldStarting, threshHoldEnding);

        do {
            if (attempt >= threshHold) ranWord = word;
            else ranWord = getRanWord(word, ranWord);
            wait(1);
            attempt++;
            System.out.print("\r" + ranWord);
            System.out.flush();
            
        } while (!ranWord.equals(word));
        
        System.out.println();
    }

    
    static char getRandomChar(){
        String words = "abcdefghijklmnopqrstuvwxyz";

        return words.charAt(random.nextInt(words.length()));
    }

    static String getRanWord(String word, String ranWord){
        ArrayList<Character> wordSet = new ArrayList<>(); 

        for (int i = 0; i < word.length(); i++){
            char currentChar = word.charAt(i);

            if (ranWord != null &&
                word.charAt(i)==ranWord.charAt(i) 
                 ) {
                    wordSet.add(currentChar);
                }
            else if (word.charAt(i) == ' '){
                wordSet.add(' ');
            } else if (word.charAt(i) >= 'a' && word.charAt(i) <= 'z'){
                wordSet.add(getRandomChar());
            } else if (word.charAt(i) >= 'A' && word.charAt(i) <= 'Z') {
                wordSet.add(Character.toUpperCase(getRandomChar()));
            } else {
                wordSet.add(word.charAt(i));
            }
        }

        StringBuilder stringBuilder = new StringBuilder();
        for (char c : wordSet){
            stringBuilder.append(c);
        }

        return stringBuilder.toString();
    }

    static void wait(int sec){
        try{
            Thread.sleep(sec*50);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    static void showLoadingEffect(){
           String[] loading = {
            "Getting Brute Force Ready....",
            "Instializing Brutee Force....",
            "Calculating Total Possibilites....",
            "Thinking Best Algorithm to Apply....",
            "Starting....                        "
        };

        for (String s : loading){ 
            System.out.print("\r" + s);
            wait(30);
        }

        System.out.println();
    }
}



/*
                        HOW TO USE

Just keep this feel in the same project as your project and then just write this line in the project you're working on:

GlitchyAnimation.play("Any Sentence here!");

You can also call GlitchyAnimation.showLoadingEffect(); for a coool loading effect



This was all made BY AJAY Kumar (Ajeyyyyyyyyyy Yayyyyyy :D)

 */
