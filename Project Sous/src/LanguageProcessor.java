import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * processes strings based on user input
 * returns values to other classes to determine their behaviour
 * @author Gabriel Mulcahy
 * @since 01/02/2019
 */
public class LanguageProcessor {

    /**
     * determines what the user's intent was based on key words
     * @param text user's input
     * @return a code that represents the user's intent
     */
    public String process(String text) {

        String outcome;

        // google Speech hears 'rate' as 'rape' frequently
        if ((text.contains("rate")) || (text.contains("rape"))){
            outcome = "rate";
        }
        else if (text.contains("exit") || text.contains("quit")) {
            outcome = "exit";
        }
        else if (text.contains("cancel") && text.contains("timer")) {
            outcome = "cancel timer";
        }
        else if (text.contains("cancel") || text.contains("stop")) {
            outcome = "cancel recipe";
        }
        else if ((text.contains("shopping") || text.contains("list")) && ((text.contains("create")) || text.contains("meals") || text.contains("make") || text.contains("put together")) ) {
            outcome = "create list";
        }
        else if ((text.contains("email") || text.contains("send")) && text.contains("list"))  {
            outcome = "email list";
        }
        else if ((text.contains("shopping") || text.contains("list")) && ((text.contains("what")) || text.contains("check") || text.contains("tell")) ) {
            outcome = "check list";
        }
        else if ((text.contains("shopping") || text.contains("list")) && (text.contains("add"))) {
            outcome = "add list";
        }
        else if (text.contains("add") || (text.contains("bought"))){
            outcome = "add";
        }
        else if (text.contains("how much") || text.contains("do I have") || text.contains("how many")) {
            outcome = "check ingredients";
        }
        else if (text.contains("next") || text.contains("done")) {
            outcome = "next";
        }
        else if (text.contains("last") || text.contains("lost") || text.contains("back") || text.contains("previous")){ // UK speakers saying 'last' sound like US speakers saying 'lost'
            outcome = "last";
        }
        else if (text.contains("current") || text.contains("repeat") || text.contains("again")) {
            outcome = "current";
        }
        else if (text.contains("suggest") || text.contains("what should") || text.contains("dinner") || text.contains("cook") || text.contains("make for") || text.contains("what can")) {
            outcome = "recipe run";
        }
        else if (text.contains("new recipe") || text.contains("custom") || text.contains("my own")) {
            outcome = "new recipe";
        }
        else if (text.contains("yes") || text.contains("yeah") || text.contains("ok") || text.contains("good")) {
            outcome = "yes";
        }
        else if (text.contains("no") || text.contains("nope") || text.contains("don't") || text.contains("else")) {
            outcome = "no";
        }
        else if (text.contains("timer") && text.contains("minute")) {
            outcome = "timer";
        }
        else {
            outcome = "unknown";
        }
        return outcome;
    }

    /**
     * An edited program taken from:
     * https://stackoverflow.com/questions/26948858/converting-words-to-numbers-in-java
     * converts an english number to a long consisting of digits
     * @param input string containing a number
     * @return long same number as entered but represented in digits
     */
    private int numberConverter(String input) {
        long result = 0;
        long finalResult = 0;

        if(input != null && input.length()> 0)
        {
            input = input.replaceAll("-", " ");
            input = input.toLowerCase().replaceAll(" and", " ");
            String[] splittedParts = input.trim().split("\\s+");

            for(String str : splittedParts)
            {
                if(str.equalsIgnoreCase("zero")) {
                    result += 0;
                }
                else if(str.equalsIgnoreCase("one")) {
                    result += 1;
                }
                else if(str.equalsIgnoreCase("two")) {
                    result += 2;
                }
                else if(str.equalsIgnoreCase("three")) {
                    result += 3;
                }
                else if(str.equalsIgnoreCase("four")) {
                    result += 4;
                }
                else if(str.equalsIgnoreCase("five")) {
                    result += 5;
                }
                else if(str.equalsIgnoreCase("six")) {
                    result += 6;
                }
                else if(str.equalsIgnoreCase("seven")) {
                    result += 7;
                }
                else if(str.equalsIgnoreCase("eight")) {
                    result += 8;
                }
                else if(str.equalsIgnoreCase("nine")) {
                    result += 9;
                }
                else if(str.equalsIgnoreCase("ten")) {
                    result += 10;
                }
                else if(str.equalsIgnoreCase("eleven")) {
                    result += 11;
                }
                else if(str.equalsIgnoreCase("twelve")) {
                    result += 12;
                }
                else if(str.equalsIgnoreCase("thirteen")) {
                    result += 13;
                }
                else if(str.equalsIgnoreCase("fourteen")) {
                    result += 14;
                }
                else if(str.equalsIgnoreCase("fifteen")) {
                    result += 15;
                }
                else if(str.equalsIgnoreCase("sixteen")) {
                    result += 16;
                }
                else if(str.equalsIgnoreCase("seventeen")) {
                    result += 17;
                }
                else if(str.equalsIgnoreCase("eighteen")) {
                    result += 18;
                }
                else if(str.equalsIgnoreCase("nineteen")) {
                    result += 19;
                }
                else if(str.equalsIgnoreCase("twenty")) {
                    result += 20;
                }
                else if(str.equalsIgnoreCase("thirty")) {
                    result += 30;
                }
                else if(str.equalsIgnoreCase("forty")) {
                    result += 40;
                }
                else if(str.equalsIgnoreCase("fifty")) {
                    result += 50;
                }
                else if(str.equalsIgnoreCase("sixty")) {
                    result += 60;
                }
                else if(str.equalsIgnoreCase("seventy")) {
                    result += 70;
                }
                else if(str.equalsIgnoreCase("eighty")) {
                    result += 80;
                }
                else if(str.equalsIgnoreCase("ninety")) {
                    result += 90;
                }
                else if(str.equalsIgnoreCase("hundred")) {
                    result *= 100;
                }
                else if(str.equalsIgnoreCase("thousand")) {
                    result *= 1000;
                    finalResult += result;
                    result=0;
                }
                else if(str.equalsIgnoreCase("million")) {
                    result *= 1000000;
                    finalResult += result;
                    result=0;
                }
                else if(str.equalsIgnoreCase("billion")) {
                    result *= 1000000000;
                    finalResult += result;
                    result=0;
                }
                else if(str.equalsIgnoreCase("trillion")) {
                    result *= 1000000000000L;
                    finalResult += result;
                    result=0;
                }
            }

            finalResult += result;
            result=0;

        }
        return (int) finalResult;
    }

    /**
     * determines if the user mentioned any known tags in their statement
     * @param input user's voice turned to text
     * @return String of all the tags they mentioned when asking for a recipe
     * @throws IOException
     */
    public String tagFinder(String input) throws IOException {

        BufferedReader file = new BufferedReader(new FileReader("tags.txt"));
        String   line = file.readLine();
        String[] tags = line.split(" ");
        String   s    = "";
        for (String t: tags) {
            // ensure an unexpected capital letter doesn't interfere with find a matching tag
            if (input.contains(t) || input.contains(t.toLowerCase())) {
                s = s + t + ",";
            }
        }
        // check if any tags were found
        if (s.length() == 0) {
            return s;
        } else if (s.charAt(s.length() - 1) == ','){
            // remove the comma at the end of the string
            return s.substring(0, s.length() - 1);
        } else {
            return s;
        }

    }

    /**
     * determines an upper limit to the number of calories the user wants in their meal
     * @param input user's voice turned into text
     * @return int number of calories
     */
    public int calorieFinder(String input) {
        int cal = 100000; // calorie count greater than any recipe
        String[] split = input.split(" ");
        for (int i = 0; i < split.length; i++){
            if (split[i].toLowerCase().equals("calories")){
                // assuming the word before 'calories' will always be the number of calories
                cal = Integer.parseInt(split[i - 1]);
                break;
            }
        }
        return  cal;
    }

    /**
     * determines an upper limit to the number of minutes the user has to cook a meal
     * @param input user's voice turned into text
     * @return int number of minutes
     */
    public int timeFinder(String input) {
        int time = 100000; // time greater than any recipe
        String[] split = input.split(" ");
        for (int i = 0; i < split.length; i++){
            if (split[i].toLowerCase().equals("minutes")){
                // assuming the word before 'minutes' will always be the number of minutes
                time = Integer.parseInt(split[i - 1]);
                break;
            }
        }
        return time;
    }

    /**
     * finds digits and a string and returns them as an int
     * @param input user's voice turned into text
     * @return int
     */
    public int intFinder(String input) {
        int result;
        try {
            result = Integer.parseInt(input.replaceAll("\\D+",""));
        } catch (NumberFormatException e) {
            // google stt is inconsistent with whether or not it produces word or digits in the text
            // this ensures that if no digits are found, we can read the words instead
            result  = numberConverter(input);
        }
        return result;
    }

    /**
     * finds a known ingredient in the user's inout
     * @param input user's voice turned into text
     * @return String name of ingredient
     * @throws IOException
     */
    public String ingredientFinder(String input) throws IOException {

        BufferedReader file = new BufferedReader(new FileReader("ingredients.txt"));
        String   line = file.readLine();
        String[] ings = line.split(" ");
        for (String i: ings) {
            // ensure an unexpected capital letter doesn't interfere with find a matching tag
            if (input.contains(i.replace("_", " "))) {
                return i;
            }
        }
        return "none";

    }

    /**
     * find if the user mentioned any known units in their input
     * currently does not work as units are written in short hand and STT produces full english
     * this would require a change to the formatting of the recipes in order to work
     * left here, unused for nor
     * @param input user's voise turned into
     * @return
     * @throws IOException
     */
    public String unitFinder(String input) throws IOException {
        BufferedReader file = new BufferedReader(new FileReader("units.txt"));
        String   line  = file.readLine();
        String[] units = line.split(" ");
        for (String u: units) {
            // google speech is inconsistent with how it handles units being said
            // fifty grams could be represent as 50g, 50 g or 50 grams
            // this line ensures none of these outcomes are missed whilst also not producing false positives
            if (input.replaceAll("\\d"," ").contains(" " + u + " ")) {
                return u;
            }
        }
        return "";
    }

}
