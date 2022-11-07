package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import java.io.*;

import static byow.Core.MyUtils.*;


public class Engine implements Serializable {
    // the render of TE
    TERenderer ter = new TERenderer();
    // the seed
    private Long seed;

    /** Feel free to change the width and height. */
    public static final int WIDTH = 90;
    public static final int HEIGHT = 50;

    // the menu
    private Menu menu = new Menu(40, 40);

    // it is game init ?
    private boolean gameInit = true;

    // the world and the generator of world
    private TETile[][] world = new TETile[WIDTH][HEIGHT];
    private WorldGenerator worldGenerator;

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .save directory. */
    public static final File SAVE_DIR = join(CWD, ".save");

    // n(include!) -> n#s -> create new world
    // l -> load an old world
    // q -> quit the game

    // todo maybe ??? don't care wrong situation ???
    // i.e. not error checking
    //  (warning: if input = "" <- "NS" NOT numbers)

    public Engine() {
    }

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
        // if it is the initialization of game, we should draw menu
        if (gameInit) {
            menu.drawMenu();
        }
        String inputString = "";
        char typedKey;
        // using loop(or "dead loop") to wait the typed input from player!
        // note: "x" is string(double queue); 'x' is char(single queue)
        while (true) {
            typedKey = MyUtils.getNextKey();
            // don't care about other char
            if (isNumber(typedKey) || isValidChar(typedKey)) {
                inputString += typedKey;
            }
            // if it is the initialization of game and getting 'S',
            // then, we can get seed(number) to create a new world
            if (gameInit && typedKey =='S'){
                int stepIndex = inputString.indexOf("S");
                inputString = inputString.substring(1, stepIndex);
                break;
            }
            if (gameInit) {
                if (typedKey == 'Q') {
                    System.exit(0);
                }
                if (typedKey == 'N') {
                    continue;
                }
                if (typedKey == 'L') {
                    load();
                    break;
                }
                // why not work when using switch()? -> just break condition fo 'if'
                // because we need 'break;' statement
            }
            // if not game init, we should either move avatar or quit/save the game
            // :Q,W,S,A,D
            if (!gameInit && inputString.equals(":Q")) {
                saveAndQuit();
            }
            if (!gameInit && inputString.length() == 1) {
                if (inputString.equals("W") || inputString.equals("S")
                        || inputString.equals("A") || inputString.equals("D")
                        || inputString.equals("P")) {
                    break;
                }
            }
        }
        renderWorld(inputString);
        // next typed in keyboard
        interactWithKeyboard();
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
        // passed in as an argument, and return a 2D tile representation of the
        // world that would have been drawn if the same inputs had been given
        // to interactWithKeyboard().
        //
        // See proj3.byow.InputDemo for a demo of how you can make a nice clean interface
        // that works for many different input types.
        // Your world generator should be able to handle any positive seed up to 9,223,372,036,854,775,807.
        // W,S,A,D,:Q
        switch (input) {
            case "W":
                return worldGenerator.moveAvatarThenGenerateWorld("W");
            case "S":
                return worldGenerator.moveAvatarThenGenerateWorld("S");
            case "A":
                return worldGenerator.moveAvatarThenGenerateWorld("A");
            case "D":
                return worldGenerator.moveAvatarThenGenerateWorld("D");
            case "P":
                return worldGenerator.turnOnOrOffLightInRooms();
        }
        // # -> seed -> create a new world
        if (seed == null) {
            seed = Long.parseLong(input);
            worldGenerator = new WorldGenerator(seed, world, false);
            return worldGenerator.generateWorld();
        }
        // load old world
        return world;
    }

    // load engine of saving
    private void load() {
        // read obj, then get some variables
        Engine loadEngine = readObject(join(SAVE_DIR, "saveEngine.txt"), Engine.class);
        worldGenerator = loadEngine.getWorldGenerator();
        world = loadEngine.getWorld();
        ter = loadEngine.getTer();
        seed = loadEngine.getSeed();
    }

    // save engine and quit process
    private void saveAndQuit() {
        // save obj in file
        // note: we must serialize all classes (i.e. implements Serializable)
        if (!SAVE_DIR.exists()) {
            SAVE_DIR.mkdir();
        }
        writeObject(join(SAVE_DIR, "saveEngine.txt"), this);
        System.exit(0);
    }

    // render(and display) the world
    public void renderWorld(String inputString) {
        // get the world
        world = interactWithInputString(inputString);
        // if game init, we should set 'gameInit' to false, then create window of world
        // else, we just move avatar in window of world
        if (gameInit) {
            gameInit = false;
            renderWorldWithBeginning(world);
        } else {
            renderWorldWithMoving(world);
        }
    }

    // render(and display) the world with beginning (i.e. creating or loading)
    public void renderWorldWithBeginning(TETile[][] world) {
        ter.initialize(WIDTH, HEIGHT);
        ter.renderFrame(world);
    }

    // render(and display) the world with moving
    public void renderWorldWithMoving(TETile[][] world) {
        ter.renderFrame(world);
    }

    public TETile[][] getWorld() {
        return world;
    }

    public WorldGenerator getWorldGenerator() {
        return worldGenerator;
    }

    public Long getSeed() {
        return seed;
    }

    public TERenderer getTer() {
        return ter;
    }
}
