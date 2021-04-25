import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents all necessary information about a recipe
 * Generated from a text file
 *
 * @author Gabriel Mulcahy
 * @since 23/10/2018
 */
public class Recipe {

    private String title;                 // name of the recipe
    private String time;                  // how long the recipe will take to make
    private String prev;                  // previous step of the recipe
    private String current;               // current step of the recipe
    private String next;                  // next step of the recipe
    private String filename;              // name of the file of the recipe
    private BufferedReader file;          // text containing entire recipe
    private List<String[]> ingredients;   // list of ingredients, their amounts and units
    private int ingredientCounter = 0;    // used for iterating through the list of ingredients
    private int serves;                   // used to change ingredient amounts based on how many portions are needed


    Recipe(String filename) throws IOException {

        this.filename = filename;

        ingredients = new ArrayList<String[]>();

        fillIngredients();

        try {
            file = new BufferedReader(new FileReader(filename));
        } catch (IOException e) {
            System.err.println(filename + " does not exist");
            return;
        }

        // get just the name of the recipe
        title = file.readLine().replace("Title: ", "");
        // get just the length of the recipe
        time  = file.readLine().replace("Time: ", "") + " minutes";

        // skip to the first ingredient
        while (!(file.readLine().equals("**INGREDIENTS START**"))){ }

        prev    = "You are at the first step.";
        current = "Great! The Ingredients you will need are " + file.readLine();
        next    = file.readLine();
    }


    /**
     * After the user has finished a step of the recipe, update all of the steps
     */
    public void stepCompleted() {

        if (next == null) {return;}

        //skip from the end of the ingredients to the beginning of the method
        if (next.equals("**INGREDIENTS END**")) {
            try {
                while (!(file.readLine().equals("**METHOD START**"))){ }
            } catch (IOException e) {
                System.out.println("Error in completing step");
            }
        }
        prev    = current;
        current = next;
        try {
            next = file.readLine();
        } catch (IOException e) {
            next = "end";
        }
    }

    /**
     * @return String name of the recipe
     */
    public String getTitle() {
        return title;
    }

    /**
     * @return String filename containing the recipe
     */
    public String getFilename() {
        return filename;
    }

    /**
     * @return String length of the recipe
     */
    public String getTime() {
        return time;
    }

    /**
     * @return String previous step of the recipe
     */
    public String getPrev(){
        return prev;
    }

    /**
     * skips steps that aren't to be read to the user
     * @return String current step of the recipe
     */
    public String getCurrent(){
        //check if the current step is for file navigation and not to be read to user
        if (current.contains("**")){
            stepCompleted();
        }
        return current;
    }

    /**
     * @return String next step of the recipe
     */
    public String getNext(){
        return next;
    }

    /**
     * @return List list of ingredients, their amounts and units
     */
    public List<String[]> getIngredients() {
        return ingredients;
    }


    /**
     * find the current ingredient in the list along with it's amount and unit
     * @return String of the name, amount and unit combined
     */
    public String currentIngredient() {
        return ingredients.get(ingredientCounter)[0] + " - " + ingredients.get(ingredientCounter)[1]
                + " " + ingredients.get(ingredientCounter)[2];
    }

    /**
     * find the next ingredient in the list along with it's amount and unit
     * @return String of the name, amount and unit combined
     */
    public String nextIngredient() {
        ingredientCounter++;
        return ingredients.get(ingredientCounter)[0] + " - " + ingredients.get(ingredientCounter)[1]
                           + " " + ingredients.get(ingredientCounter)[2];
    }

    /**
     * find the previous ingredient in the list along with it's amount and unit
     * tells the user they can't go back if they are at the first ingredient
     * @return String of the name, amount and unit combined
     */
    public String prevIngredient() {
        try {
            return ingredients.get(ingredientCounter-1)[0] + " - " + ingredients.get(ingredientCounter-1)[1]
                    + " " + ingredients.get(ingredientCounter-1)[2];
        } catch (IndexOutOfBoundsException e) {
            return "Can't go back. You are at the first ingredient.";
        }
    }

    /**
     * Creates a separate BufferedReader of the recipe file in order to populate ingredients
     * populates with name, amount and unit (if applicable)
     */
    private void fillIngredients() {

        try {
            BufferedReader ifile = new BufferedReader(new FileReader(filename));
            while (!(ifile.readLine().equals("**INGREDIENTS START**"))){ } //skip to the start of the ingredients
            while (true) {
                String line      = ifile.readLine();           //get the line of the ingredient
                if (line.equals("**INGREDIENTS END**")) {      //stop if the end of the ingredients has been reached
                    break;
                }
                String[] split   = line.split(" ");       //split it into its separate elements
                //check if the ingredient has a unit or not
                if (split.length == 4) {
                    //take only the ingredient, amount and unit
                    String[] ingAmount = new String[]{split[0], split[2], split[3]};
                    ingredients.add(ingAmount); //add to the list
                } else {
                    //leave the unit blank
                    String[] ingAmount = new String[]{split[0], split[2], ""};
                    ingredients.add(ingAmount); //add to the list
                }
            }
        } catch (IOException e1) {
            System.err.println(filename + " does not exist");
        }

    }
}











