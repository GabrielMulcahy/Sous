import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class RecipeTest {

    private Recipe recipe;

    @Before
    public void setUp() throws Exception {
        recipe = new Recipe("tests/test_recipe.txt");
    }

    @Test
    public void getTitle() {
        assertEquals("Test Recipe", recipe.getTitle());
    }

    @Test
    public void getFilename() {
        assertEquals("tests/test_recipe.txt", recipe.getFilename());
    }

    @Test
    public void getTime() {
        assertEquals("2 minutes", recipe.getTime());
    }

    @Test
    public void getPrev() {
        assertEquals("You are at the first step.", recipe.getPrev());
    }

    @Test
    public void getCurrent() {
        assertEquals("Great! The Ingredients you will need are bread - 1 slice", recipe.getCurrent());
    }

    @Test
    public void getNext() {
        assertEquals("butter - 1 tbsp", recipe.getNext());
    }

    @Test
    public void currentIngredient() {
        assertEquals("bread - 1 slice", recipe.currentIngredient());
    }

    @Test
    public void nextIngredient() {
        assertEquals("butter - 1 tbsp", recipe.nextIngredient());
    }

    @Test
    public void prevIngredient() {
        assertEquals("Can't go back. You are at the first ingredient.", recipe.prevIngredient());
    }
}