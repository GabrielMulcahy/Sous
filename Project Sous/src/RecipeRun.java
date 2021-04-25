import java.io.*;
import java.util.List;
import java.util.Random;
import java.util.concurrent.SynchronousQueue;

/**
 * Selects a recipe
 * Sets up a new Recipe instance ready to be interacted with
 * Reads it to the user step by step
 * @author Gabriel Mulcahy
 * @since 05-02-2019
 */
public class RecipeRun implements Runnable {

    private Recipe                   recipe;
    private String                   id;
    private String                   suggestions;
    public  SynchronousQueue<String> toRecipe;
    public  SynchronousQueue<String> fromRecipe;

    RecipeRun(int time, int calories, SynchronousQueue<String> toRecipe, SynchronousQueue<String> fromRecipe) throws IOException {
        recipe    = new Recipe(suggestRecipe(time, calories));
        this.toRecipe   = toRecipe;
        this.fromRecipe = fromRecipe;

    }

    RecipeRun(String tags, int time, int calories, SynchronousQueue<String> toRecipe, SynchronousQueue<String> fromRecipe) throws IOException {
        recipe    = new Recipe(suggestRecipe(tags, time, calories));
        this.toRecipe   = toRecipe;
        this.fromRecipe = fromRecipe;
    }


    @Override
    public void run(){

        try{

            fromRecipe.put("Would you like to make " + recipe.getTitle() + "?\n" +
                    "It should take " + recipe.getTime());

            // create a loop until the user is presented with a recipe they would like
            String response = toRecipe.take();
            while (!response.equals("yes")) {
                if (response.equals("no")) {
                    String filename  = recipeReject(); // find a new recipe
                    if (filename.equals("end")) {
                        // no more recipes can be made
                        fromRecipe.put("Sorry, that's all I have available for now.");
                        return;
                    }
                    recipe = new Recipe(filename);
                    fromRecipe.put("How about " + recipe.getTitle() + "?\n" +
                            "It should take " + recipe.getTime());
                } else {
                    fromRecipe.put("Please let me know whether you would like to make "
                            + recipe.getTitle() + " before we do anything else.");
                }
                response = toRecipe.take();
            }

            fromRecipe.put(recipe.getCurrent());
        } catch (InterruptedException e) {
            System.err.println("Error in synchronous queue: " + e.getMessage());
            System.exit(-1);
        } catch (IOException e) {
            System.err.println("IO error in RecipeRun: " + e.getMessage());
            System.exit(-1);
        }


        //read out ingredients to the user
        while (!(recipe.getNext().equals("**INGREDIENTS END**"))) {

            try {
                String action = toRecipe.take();

                switch (action) {
                    case "next":
                        recipe.stepCompleted();
                        fromRecipe.put(recipe.nextIngredient());
                        break;
                    case "current":
                        fromRecipe.put(recipe.currentIngredient());
                        break;
                    case "last":
                        fromRecipe.put(recipe.prevIngredient());
                        break;
                    case "cancel recipe":
                        fromRecipe.put("Cancelling recipe");
                        return;
                    default:
                        fromRecipe.put("I don't understand!");
                }
            } catch (InterruptedException e) {
                System.err.println("Error in synchronous queue: " + e.getMessage());
                System.exit(-1);
            }


        }

        //read method to the user
        while (!(recipe.getCurrent().equals("Enjoy!"))) {

            try {
                String action = toRecipe.take();

                switch (action) {
                    case "next" :
                        recipe.stepCompleted();
                        fromRecipe.put(recipe.getCurrent());
                        break;
                    case "current" :
                        fromRecipe.put(recipe.getCurrent());
                        break;
                    case "last" :
                        if (recipe.getPrev().equals("**INGREDIENTS END**")) {
                            fromRecipe.put("This is the first step!");
                            break;
                        } else {
                            fromRecipe.put(recipe.getPrev());
                            break;
                        }
                    case "cancel recipe":
                        fromRecipe.put("Cancelling recipe");
                        return;
                    default :
                        fromRecipe.put("I don't understand!");
                }
            } catch (InterruptedException e) {
                System.err.println("Error in synchronous queue: " + e.getMessage());
                System.exit(-1);
            }

        }


        //remove ingredients used from the database
        List<String[]> ingredients = recipe.getIngredients();
        String ingamount;
        for (int i = 0; i < ingredients.size(); i++) {
            //add a minus sign to the ingredient amount
            ingamount = ingredients.get(i)[0] + " -" + ingredients.get(i)[1]; //CAN DOUBLE THE AMOUNT HERE TO SUBTRACT
            try {
                Runtime.getRuntime().exec("python IngredientUpdate.py " + ingamount); //different python script?
            } catch (Exception e) {
                System.err.println("Could not update ingredients: " + e.getMessage());
            }
        }

        // make a note of the completed recipe so that it can be rated later
        try {
            PrintWriter pw = new PrintWriter(new FileOutputStream("last_recipe.txt", false));
            pw.println(recipe.getFilename().replace(".txt","").replace("recipes/",""));
            pw.println(recipe.getTitle());
            System.out.println("making a note of the recipe id...");
            pw.close();
        } catch (IOException e) {
            System.err.println("Error writing to last_recipe.txt: " + e.getMessage());
        }
    }


    /**
     * Suggest a recipe based on user preferences
     * @param time maximum amount of time taken to cook recipe
     * @param calories maximum number of calories per serving
     * @return String the name of a text file that contains a recipe
     */
    private String suggestRecipe (int time, int calories) {
        try {
            Runtime.getRuntime().exec("python RecipeSelector.py " + time + " " + calories);
            return recipeSelect();
        } catch (Exception e) {
            System.err.println("Something went wrong with the suggestion: " + e.getMessage());
            return "";
        }
    }

    /**
     * Suggest a recipe based on user preferences
     * @param tags a comma separated string ot tags
     * @param time maximum amount of time taken to cook recipe
     * @param calories maximum number of calories per serving
     * @return String the name of a text file that contains a recipe
     */
    private String suggestRecipe (String tags, int time, int calories) {
        try {
            System.out.println(tags);
            Runtime.getRuntime().exec("python TagSelector.py " + tags + " " + time + " " + calories);
            return recipeSelect();
        } catch (Exception e) {
            System.out.println("Something went wrong with the suggestion");
            return "Damn";
        }
    }

    /**
     * Processes the text file containing all possible recipes to avoid code duplication in suggestRecipe
     * @return String the name of a text file that contains a recipe
     */
    private String recipeSelect () {
        try {
            Thread.sleep(1500); //allow time for RecipeSelector.py to finish
            BufferedReader file = new BufferedReader(new FileReader("result.txt"));
            suggestions = file.readLine();
            file.close();
            String[] split = suggestions.split(" ");
            id = randomRecipe(split);
            return "recipes/" +  id + ".txt";
        } catch (Exception e) {
            System.err.println("Something went wrong with the suggestion: " + e.getMessage());
            System.exit(-1);
            return null;
        }
    }

    /**
     * removes the last suggest recipe from the list of possible suggestions
     * @return String the name of a text file that contains a recipe
     */
    private String recipeReject() {
        suggestions = suggestions.replace(id + " ","");
        String[] split = suggestions.split(" ");
        if (split.length == 1) return "end";
        id = randomRecipe(split);
        return "recipes/" + id + ".txt";
    }

    /**
     * takes an array of strings an returns one random element
     * @param split array of recipes
     * @return String one randomly chosen recipe
     */
    private String randomRecipe (String[] split) {
        int rnd = new Random().nextInt(split.length);
        return split[rnd];
    }


}
