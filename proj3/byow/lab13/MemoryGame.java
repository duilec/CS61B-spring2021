package byow.lab13;

import edu.princeton.cs.introcs.StdDraw;

import java.awt.Color;
import java.awt.Font;
import java.util.Random;

public class MemoryGame {
    /** The width of the window of this game. */
    private int width;
    /** The height of the window of this game. */
    private int height;
    /** The current round the user is on. */
    private int round;
    /** The Random object used to randomly generate Strings. */
    private Random rand;
    /** Whether or not the game is over. */
    private boolean gameOver;
    /** Whether or not it is the player's turn. Used in the last section of the
     * spec, 'Helpful UI'. */
    private boolean playerTurn;
    /** The characters we generate random Strings from. */
    private static final char[] CHARACTERS = "abcdefghijklmnopqrstuvwxyz".toCharArray();
    /** Encouraging phrases. Used in the last section of the spec, 'Helpful UI'. */
    private static final String[] ENCOURAGEMENT = {"You can do this!", "I believe in you!",
                                                   "You got this!", "You're a star!", "Go Bears!",
                                                   "Too easy for you!", "Wow, so impressive!"};
    // For this memory game it would look something like:
    // 1.Create the game window
    // 2.Randomly generate a target string
    // 3.Display target string on screen one character at a time
    // 4.Wait for player input until they type in as many characters as there are in the target
    // 5.Repeat from step 2 if player input matches the target string except with a longer random target string.
    // If no match, print a game over message and exit.

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Please enter a seed");
            return;
        }

        long seed = Long.parseLong(args[0]);
        MemoryGame game = new MemoryGame(40, 40, seed);
        game.startGame();
    }

    public MemoryGame(int width, int height, long seed) {
        /* Sets up StdDraw so that it has a width by height grid of 16 by 16 squares as its canvas
         * Also sets up the scale so the top left is (0,0) and the bottom right is (width, height)
         */
        this.width = width;
        this.height = height;
        StdDraw.setCanvasSize(this.width * 16, this.height * 16);
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.setXscale(0, this.width);
        StdDraw.setYscale(0, this.height);
        StdDraw.clear(Color.BLACK);
        StdDraw.enableDoubleBuffering();

        // Initialize random number generator
        this.rand = new Random(seed);
    }

    // using String
    // Generate random string of letters of length n
    public String generateRandomString(int n) {
        int randomNum;
        int times = 0;
        String randomString = "";
        while (times < n) {
            randomNum = this.rand.nextInt(CHARACTERS.length);
            randomString += CHARACTERS[randomNum];
            times += 1;
        }
        return randomString;
    }

    // using StringBuilder
//    public String generateRandomString(int n) {
//        StringBuilder s = new StringBuilder();
//        int randomNum = this.rand.nextInt(CHARACTERS.length);
//        int times = 0;
//        while (times < n) {
//            s.append(CHARACTERS[randomNum]);
//            randomNum = this.rand.nextInt(CHARACTERS.length);
//            times += 1;
//        }
//        return s.toString();
//    }

    public void drawFrame(String s) {
        // Take the string and display it in the center of the screen
        StdDraw.clear(Color.black);
        StdDraw.setPenColor(Color.white);
        // center
        StdDraw.text(this.width / 2.0, this.height / 2.0, s);
        drawUIHelper();
        StdDraw.show();
    }

    // If game is not over, display relevant game information at the top of the screen
    private void drawUIHelper() {
        if (!gameOver) {
            Font font = new Font("Monaco", Font.BOLD, 20);
            StdDraw.setFont(font);
            // the top left
            StdDraw.textLeft(0, this.height - 1, "Round: " + round);
            // the top center
            if (playerTurn) {
                StdDraw.text(this.width / 2.0, this.height - 1, "Typed!");
            } else {
                StdDraw.text(this.width / 2.0, this.height - 1, "Watch!");
            }
            // the top right
            String text = ENCOURAGEMENT[rand.nextInt(ENCOURAGEMENT.length)];
            StdDraw.textRight(this.width, this.height - 1, text);
            // line blow the top
            StdDraw.setPenColor(Color.white);
            StdDraw.line(0, this.height - 2, this.width, this.height - 2);
            font = new Font("Monaco", Font.BOLD, 30);
            StdDraw.setFont(font);
        }
    }

    // Display each character in letters, making sure to blank the screen between letters
    public void flashSequence(String letters) {
        int index = 0;
        while (index < letters.length()) {
            String s = Character.toString(letters.charAt(index));
            displayOneSecond(s);
            breakZeroDotFiveSecond("");
            index += 1;
        }
    }

    // Each character should be visible on the screen for 1 second
    private void displayOneSecond(String s) {
        drawFrame(s);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    // and there should be a brief 0.5 seconds break between characters where the screen is blank.
    private void breakZeroDotFiveSecond(String s) {
        drawFrame(s);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    // Read n letters of player input
    public String solicitNCharsInput(int n) {
        playerTurn = true;
        String playerInputString = "";
        drawFrame(playerInputString);
        // using loop(or "dead loop") to wait the typed input from player!
        while (n > 0) {
            if (StdDraw.hasNextKeyTyped()){
                n -= 1;
                playerInputString += StdDraw.nextKeyTyped();
                drawFrame(playerInputString);
            }
        }
        playerTurn = false;
        return playerInputString;
    }

    public void startGame() {
        // Set any relevant variables before the game starts
        round = 1;
        gameOver = false;
        playerTurn = false;
        // Establish Engine loop
        while (true) {
            displayOneSecond("Round: " + round);
            String randomString = generateRandomString(round);
            flashSequence(randomString);
            String playerInputString = solicitNCharsInput(round);
            breakZeroDotFiveSecond(playerInputString);
            checkCorrectOfInput(randomString, playerInputString);
            if (gameOver) {
                drawFrame("Game Over! You made it to round: " + round);
                break;
            }
            round += 1;
        }

    }

    // if game over return true, else return false
    private void checkCorrectOfInput(String randomString, String playerInputString) {
        gameOver = !randomString.equals(playerInputString);
    }
}
