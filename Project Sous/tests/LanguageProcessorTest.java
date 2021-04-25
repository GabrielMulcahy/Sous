import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class LanguageProcessorTest {

    private LanguageProcessor lp;
    private String timerRequest;
    private String recipeRequest;
    private String ingredientRequest;

    @Before
    public void setUp() throws Exception {
        lp = new LanguageProcessor();
        timerRequest  = "set a timer for 5 minutes";
        recipeRequest = "I want something italian for dinner, " +
                "I only have fifteen minutes and it has to be under 200 calories";
        ingredientRequest = "How much pasta do I have";
    }

    @Test
    public void processTimer() {
       assertEquals("timer",lp.process(timerRequest));
    }

    @Test
    public void processRecipe() {
        assertEquals("recipe run", lp.process(recipeRequest));
    }

    @Test
    public void processIngredient() {
        assertEquals("check ingredients", lp.process(ingredientRequest));
    }

    @Test
    public void processUnknown() {
        assertEquals("unknown", lp.process(""));
    }

    @Test
    public void tagFinder() throws IOException {
        assertEquals("italian", lp.tagFinder(recipeRequest));
    }

    @Test
    public void tagBlank() throws IOException {
        assertEquals("", lp.tagFinder(ingredientRequest));
    }

    @Test
    public void calorieFinder() {
        assertEquals(200, lp.calorieFinder(recipeRequest));
    }

    @Test
    public void calorieBlank() {
        assertEquals(100000, lp.calorieFinder(ingredientRequest));
    }

    @Test
    public void timeFinder() {
        assertEquals(5, lp.timeFinder(timerRequest));
    }

    @Test
    public void timeBlank() {
        assertEquals(100000, lp.timeFinder(ingredientRequest));
    }

    @Test
    public void intFinderDigit() {
        assertEquals(5, lp.intFinder(timerRequest));
    }

    @Test
    public void intFinderWords() {
        assertEquals(178, lp.intFinder("my number is one hundred and seventy eight"));
    }

    @Test
    public void intFinderBlank() {
        assertEquals(0, lp.intFinder("no numbers here"));
    }

    @Test
    public void ingredientFinder() throws IOException {
        assertEquals("pasta", lp.ingredientFinder(ingredientRequest));
    }

    @Test
    public void unitFinderA() throws IOException {
        assertEquals("g", lp.unitFinder("50g of pasta"));
    }

    @Test
    public void unitFinderB() throws IOException {
        assertEquals("g", lp.unitFinder("50 g of pasta"));
    }

    @Test
    public void unitFinderC() throws IOException {
        assertEquals("grams", lp.unitFinder("50 grams of pasta"));
    }
}