package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

//import static byow.Core.Room.*;

public class Engine {
    TERenderer ter = new TERenderer();
    static Long seed;

    /* Feel free to change the width and height. */
    public static final int WIDTH = 80;
    public static final int HEIGHT = 30;

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
    }

    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     *
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     *
     * In other words, both of these calls:
     *   - interactWithInputString("n123sss:q")
     *   - interactWithInputString("lww")
     *
     * should yield the exact same world state as:
     *   - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    // For phase 1, your project does not need to support interactWithKeyboard()
    // but it must support interactWithInputString()

    // "N" and "n" should both initiate the process of world generation
    // You should NOT render any tiles or play any sound when using interactWithInputString()

    // but for the autograder, interactWithInputString() should not render the world,
    // only returning the row as a TETile array.
    public TETile[][] interactWithInputString(String input) {
        // TODO: Fill out this method so that it run the engine using the input
        // passed in as an argument, and return a 2D tile representation of the
        // world that would have been drawn if the same inputs had been given
        // to interactWithKeyboard().
        //
        // See proj3.byow.InputDemo for a demo of how you can make a nice clean interface
        // that works for many different input types.

        // Your world generator should be able to handle any positive seed up to 9,223,372,036,854,775,807.
        input = input.toUpperCase();
        int stepIndex = input.indexOf("S");
        String number = input.substring(1, stepIndex);
        Long seed = Long.parseLong(number);
        TETile[][] finalWorldFrame = new TETile[WIDTH][HEIGHT];
        WorldGenerator worldGenerator = new WorldGenerator(seed, finalWorldFrame, false);
        return worldGenerator.generateWorld();
    }

    // render(and display) the world
    public void renderWorld(TETile[][] world) {
        ter.initialize(WIDTH, HEIGHT);
        ter.renderFrame(world);
    }

}
