package byow.Core;

import edu.princeton.cs.introcs.StdDraw;

import java.awt.*;
import java.io.Serializable;

public class Menu implements Serializable {
    /** The width of the menu of window of this game. */
    private int width;
    /** The height of the menu of window of this game. */
    private int height;

    private final String titleContent = "My World";
    private final String[] menuContents = {"New Game (N)", "Load Game (L)", "Quit (Q)", "Primary Features (P)"};

    public Menu(int width, int height) {
        /* Sets up StdDraw so that it has a width by height grid of 16 by 16 squares as its canvas
         * Also sets up the scale so the top left is (0,0) and the bottom right is (width, height)
         */
        this.width = width;
        this.height = height;
        StdDraw.setCanvasSize(this.width * 16, this.height * 16);
        StdDraw.setXscale(0, this.width);
        StdDraw.setYscale(0, this.height);
        StdDraw.clear(Color.BLACK);
        StdDraw.enableDoubleBuffering();
    }

    public void drawMenu() {
        // Take the string and display it in the center of the screen
        StdDraw.clear(Color.black);
        StdDraw.setPenColor(Color.white);
        // title
        Font font = new Font("Monaco", Font.BOLD, 50);
        StdDraw.setFont(font);
        StdDraw.text(this.width / 2.0, this.height * (2.0 / 3.0), titleContent);
        // menu
        font = new Font("Monaco", Font.BOLD, 20);
        StdDraw.setFont(font);
        int distance = 0;
        for (String menuContent : menuContents) {
            StdDraw.text(this.width / 2.0, this.height / 2.0 - distance, menuContent);
            // distance bias is 2
            distance += 2;
        }
        StdDraw.show();
    }

}
