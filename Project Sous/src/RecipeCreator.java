
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.io.File;

/**
 * Allows the user to create a new recipe
 * Prompts them to enter information and creates a text file in the correct format
 * @author Gabriel Mulcahy
 * @since 06/02/2019
 */
public class RecipeCreator implements Runnable {

    private Scanner sc;         //takes user input
    private String input;       //user input
    private String filename;    //name of file for new recipe to be written to
    private PrintWriter recipe; //writer to the file
    private File f;             //the file
    private int counter;        //used for counting how many recipes already exist

    RecipeCreator() {
        sc       = new Scanner(System.in);
        counter  = 1;
        filename = counter + ".txt";
        f        = new File(filename);

        //check how many recipes already exists so that the new file can be named appropriately
        while (f.exists()) {
            filename = "recipes/" + ++counter + ".txt";
            f = new File(filename);
        }

        //create the text file to be written to
        try {
            recipe = new PrintWriter(filename, "UTF-8");
        } catch (IOException e) {
            System.out.println("Error creating recipe file");
        }
    }

    /**
     * Creates one new recipe from user input
     */
    @Override
    public void run() {

        System.out.println("Enter the name of your recipe: ");
        input = sc.nextLine();
        recipe.println("Title: " + input);

        System.out.println("How many minutes does it take to cook?");
        input = sc.nextLine();
        recipe.println("Time: " + input);

        System.out.println("How many calories per serving?");
        input = sc.nextLine();
        recipe.println("Calories: " + input);

        System.out.println("How many people does this recipe serve?");
        input = sc.nextLine();
        recipe.println("Serves: " + input);
        recipe.println();

        recipe.println("**INGREDIENTS START**");

        System.out.println("Enter the ingredients, then the amount, then the unit (if applicable). \nType 'X' when finished.");

        while (true) {
            System.out.println("Ingredient:");
            input = sc.nextLine().replace(" ","_") + " - "; //replace spaces with underscores
            if (input.equals("X - ")) { break; }
            System.out.println("Amount:");
            input = input + sc.nextLine() + " ";
            System.out.println("Unit:");
            input = input + sc.nextLine();
            recipe.println(input.toLowerCase());
        }

        recipe.println("**INGREDIENTS END**");
        recipe.println();
        recipe.println("**TAGS START**");

        System.out.println("Enter any words to describe this recipe such as 'Spicy' or 'Italian'. \nType 'X' when finished.");

        while (true) {
            System.out.println("Tag:");
            input = sc.nextLine();
            if (input.equals("X")) { break; }
            recipe.println(input.toLowerCase());
        }

        recipe.println("**TAGS END**");
        recipe.println();
        recipe.println("**METHOD START**");

        System.out.println("Enter each step of the recipe. Hit enter after each step is complete. \nType 'X' when finished.");

        while (true) {
            input = sc.nextLine();
            if (input.equals("X")) { break; }
            recipe.println(input);
        }

        recipe.print("Enjoy!");

        recipe.close();

        //add recipe to the database and update the text file of tags, units and ingredients
        try {
            Runtime.getRuntime().exec("python RecipeInput.py");
            Thread.sleep(1000); //allow time for the recipes to enter the database
            Runtime.getRuntime().exec("python WriteTags.py");
            Runtime.getRuntime().exec("python WriteUnits.py");
            Runtime.getRuntime().exec("python WriteIngredients.py");
        } catch (Exception e) {
            System.err.println("Error creating find files..." + e.getMessage());
        }

    }
}
