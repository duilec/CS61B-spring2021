package byow.lab13;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.*;

public class TestStdDraw {
    public static void main(String[] args) {
        StdDraw.clear(Color.black);
        Font font = new Font("Arial", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.setPenColor(Color.white);
        StdDraw.text(0.5, 0.5, "Hello, World");
        StdDraw.show();
    }
}