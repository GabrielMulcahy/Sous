import java.io.*;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.SynchronousQueue;

/**
 * through instances of LanguageProcessor, SpeechToText and TextToSpeech
 * this class coordinates the functionality of the program
 * and interacts with the user
 * @author Gabriel Mulcahy
 * @since 13-02-2019
 */
public class VoiceAssistant implements Runnable {

    private SpeechToText stt;
    private LanguageProcessor lp;
    private TextToSpeech tts;
    private Boolean recipeRunning;

    public  SynchronousQueue<String> toRecipe;
    public  SynchronousQueue<String> fromRecipe;
    public  SynchronousQueue<String> generalQueue;
    public  String  input;

    private Timer timer;
    private Thread tt;
    private Boolean timing; // is true if a timer is currently running

    VoiceAssistant() {
        stt           = new SpeechToText();
        tts           = new TextToSpeech();
        lp            = new LanguageProcessor();
        recipeRunning = false;
        toRecipe      = new SynchronousQueue<String>(); // sends information to a running recipe
        fromRecipe    = new SynchronousQueue<String>(); // receives information from a running recipe
        generalQueue  = new SynchronousQueue<String>();
        timer         = new Timer();
        tt            = new Thread(timer);
        timing        = false;
    }


    @Override
    public void run() {

        tts.say("Hello Gabriel! What would you like me to help you with today?");

        // wait for the user to prompt some other form of behaviour
        while (true) {
            input  = stt.getText();
            String action = lp.process(input);

            switch (action) {
                case "exit" :
                    exit();
                    break;
                case "email list" :
                    sendList();
                    break;
                    case "create list" :
                    createList();
                    break;
                case "add list"   :
                    addToList();
                    break;
                case "check list" :
                    checkList();
                    break;
                case "add"        :
                    addIngredient();
                    break;
                case "check ingredients" :
                    checkIngredient();
                    break;
                case "rate"      :
                    rateRecipe();
                    break;
                case "timer"     :
                    startTimer();
                    break;
                case "cancel timer" :
                    if (timing) {
                    tt.interrupt(); // interrupt the timer
                    tts.say("Ok, that's cancelled.");
                    timing = false;
                    } else {
                        tts.say("You currently don't have a timer.");
                    }
                    break;
                // everything that could be said while running a recipe
                case "cancel recipe" :
                case "yes"       :
                case "no"        :
                case "next"      :
                case "current"   :
                case "last"      :
                    if (recipeRunning) {
                        try {
                            // give the action to the recipe
                            toRecipe.put(action);
                            // take the response from the recipe
                            String output = fromRecipe.take();
                            tts.say(output);
                            //change recipeRunning to false if they recipe has ended for any reason
                            if (output.equals("Enjoy!") || action.equals("cancel recipe")) recipeRunning = false;
                        } catch (java.lang.InterruptedException e) {
                            System.err.println("Recipe interrupted " + e.getMessage());
                            tts.say("Oops! Something went wrong. Please try again.");
                        }
                    } else {
                        tts.say("You are currently not making a recipe.");
                    }
                    break;
                case "new recipe":
                    startCreator();
                    break;
                case "recipe run":
                    try {
                        startRecipe();
                    } catch (InterruptedException e) {
                        System.err.println("VA interrupted: " + e.getMessage());
                        tts.say("Oops! Something went wrong. Please try again.");
                    } catch (IOException e) {
                        System.err.println("VA IO: " + e.getMessage());
                        tts.say("Oops! Something went wrong. Please try again.");
                    }
                    break;
                default:
                    tts.say("I'm sorry, I can't help with that right now.");

            }
        }
    }


    /**
     * creates a new thread of RecipeRun
     */
    private void startRecipe() throws InterruptedException, IOException{

        // check if the last recipe has been rated before they continue
        Runtime.getRuntime().exec("python CheckRated.py");
        Thread.sleep(500);
        BufferedReader br = new BufferedReader(new FileReader("check_rated.txt"));
        if (br.readLine().equals("false")){
            tts.say("You haven't rated your last recipe, would you like to before continuing?");
            String answer = stt.getResponse();
            // wait for the user to say a definitive yes or no in response
            while (!lp.process(answer).equals("no")){
                if (lp.process(answer).equals("yes")) {
                    rateRecipe();
                    break;
                }
                tts.say("Sorry I didn't catch that, do you want to rate the recipe?");
                answer = stt.getResponse();
            }
        }

        RecipeRun rr;
        String response;

        //determine if the user made any preference of known tags, time or calories
        String tags     = lp.tagFinder(input);
        int    time     = lp.timeFinder(input);
        int    calories = lp.calorieFinder(input);
        if(tags.equals("")){
            response = "Finding something for you to cook...";
            tts.say(response);
            rr  = new RecipeRun(time, calories, toRecipe, fromRecipe);
        } else {
            // determine if the user specified multiple tags, adding an 'and' to the response if necessary
            String[] split = tags.split(",");
            if (split.length == 1) {
                response = "Finding something " + split[0] + " for you to cook...";
            } else {
                response = "Finding something ";
                for (int i = 0; i < (split.length - 1); i++) {
                    response = response + " " + split[i];
                }
                response = response + " and " + split[split.length-1] + " for you to cook...";
            }
            tts.say(response);
            rr  = new RecipeRun(tags, time, calories, toRecipe, fromRecipe);
        }

        Thread thread = new Thread(rr);
        thread.start();

        recipeRunning = true;

        String output = fromRecipe.take();
        tts.say(output);
    }

    /**
     *  creates a new thread of RecipeCreator
     */
    private void startCreator() {
        tts.say("Ok, let's enter a new recipe.");
        RecipeCreator rc = new RecipeCreator();
        rc.run();
    }

    /**
     * allows the user to rate the last recipe they completed
     */
    private void rateRecipe() {

        String title;
        try {
            BufferedReader br = new BufferedReader(new FileReader("last_recipe.txt"));
            br.readLine(); //skip filename
            title = br.readLine();
        } catch (IOException e) {
            tts.say("Error finding last recipe, please try again later.");
            return;
        }
        tts.say("You last made " + title + "." + "How would you rate it out of five?");
        int rating = lp.intFinder(stt.getResponse());
        try {
            Runtime.getRuntime().exec("python RateRecipe.py " + rating);
            switch (rating) {
                case 5 :
                    tts.say("Noted! You must have loved it!");
                    break;
                case 4 :
                    tts.say("I'll write that down. I'm glad you liked it!");
                    break;
                case 3 :
                    tts.say("Alright, not bad then!");
                    break;
                case 2 :
                    tts.say("Sorry it wasn't the best, I'll try to suggest other things next time.");
                    break;
                case 1 :
                    tts.say("Okay, let's hope we don't see this one again!");
                    break;
                case 0 :
                    tts.say("Oh dear! I'll make sure never to recommend this again.");
                    break;
            }

        } catch (IOException e) {
            tts.say("Error rating recipe. Please try again later.");
        }
    }

    /**
     * starts a timer for the number of minutes said by the user
     * will only run if a timing isn't already running
     */
    private void startTimer() {
        // removed being able to have multiple timers in order to be able to cancel them
        if (timing) {
            tts.say("I'm sorry, I can only manage one timer at a time");
            return;
        }
        int time = lp.intFinder(input);
        timer.setTime(time);
        tts.say("Starting timer for " + time + " minutes...");
        tt.start();
        timing = true;
    }

    /**
     * checks the how much of an ingredient is in stock and tells the user
     */
    private void checkIngredient(){
        try{
            String ingredient = lp.ingredientFinder(input);
            if (ingredient.equals("none")){
                tts.say("I'm sorry, I'm not aware of that ingredient");
                return;
            }
            Runtime.getRuntime().exec("python IngredientCheck.py " + ingredient);
            Thread.sleep(200);
            BufferedReader br = new BufferedReader(new FileReader("check.txt"));
            String result = br.readLine();
            tts.say(result);
        } catch (InterruptedException | IOException e) {
            System.err.println("Error checking ingredient: " + e.getMessage());
            tts.say("Sorry, I couldn't check that ingredient right now");
        }


    }

    /**
     * updates the database to increase the amount in stock based on user input
     */
    private void addIngredient(){
        try{
            String ingredient = lp.ingredientFinder(input);
            if (ingredient.equals("none")){
                tts.say("I'm sorry, I'm not aware of that ingredient");
                return;
            }
            String amount     = Integer.toString(lp.intFinder(input));

            Runtime.getRuntime().exec("python IngredientUpdate.py " + ingredient + " " + amount);
            tts.say("Adding more " + ingredient + " to my database...");
        } catch (IOException e) {
            e.printStackTrace();
            tts.say("Sorry, something went wrong with updating the ingredient.");
        }
    }

    /**
     * adds an amount of an ingredient to the shopping list
     * calls python script in order to write the information and determine the unit
     */
    private void addToList() {
        try {
            String ingredient = lp.ingredientFinder(input);
            int    amount     = lp.intFinder(input);
            String unit       = lp.unitFinder(input);

            tts.say("Adding " + ingredient + "to your shopping list");

            PrintWriter pw    = new PrintWriter(new FileOutputStream("shopping.txt", true));
            pw.println(ingredient + " " + amount + " " + unit);
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
            tts.say("Sorry, something went wrong with updating your shopping list");
        }
    }

    /**
     * reads the user's shopping list to them one item at a time
     * leaves a 1 second pause between saying each item
     */
    private void checkList() {
        try {
            tts.say("Checking your shopping list");
            BufferedReader br = new BufferedReader(new FileReader("shopping.txt"));
            String item;
            while ((item = br.readLine()) != null) {
                tts.say(item);
                Thread.sleep(500);
            }
        } catch (Exception e) {
            e.printStackTrace();
            tts.say("Error reading shopping list!");
        }
    }

    /**
     * determines how many meals the user wants
     * adds ingredients for that many random recipes to the shopping list
     */
    private void createList() {
        int meals = lp.intFinder(input);
        if (meals == 0) {
            tts.say("How many meals do you want to make?");
            meals = lp.intFinder(stt.getResponse());
        }
        try {
            Runtime.getRuntime().exec("python ListCreator.py " + meals);
            tts.say("List created!");
        } catch (IOException e) {
            tts.say("Error creating a shopping list for you.");
            e.printStackTrace();
        }
    }

    /**
     * emails the shopping list to the user
     * if they haven't already entered their email address, they will be prompted to do so
     */
    private void sendList() {
        try{
            BufferedReader br = new BufferedReader(new FileReader("email_address.txt"));
            String address    = br.readLine(); // user's email address
            // check if they've already provided an email address
            if (address == null) {
                tts.say("Please enter your email address.");
                Scanner sc        = new Scanner(System.in);
                address           = sc.nextLine();
                PrintWriter email = new PrintWriter("email_address.txt", "UTF-8");
                email.println(address);
                email.close();
            }
            Runtime.getRuntime().exec("python Email.py " + address);
            tts.say("I've sent you your shopping list. It should be in your inbox soon!");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * asks the user if they want to stop the execution of the program
     */
    private void exit(){
        tts.say("Would you like me to shut down for now?");
        String response = lp.process(stt.getResponse());
        while (!(response.equals("no") || response.equals("yes"))) {
            tts.say("Sorry, I didn't catch that. Would you like me to shut down?");
            response = lp.process(stt.getResponse());
        }
        if (response.equals("yes")) {
            tts.say("Okay, goodbye!");
            System.exit(1);
        } else {
            tts.say("Alright, what would you like me to help you with then?");
        }
    }


}
