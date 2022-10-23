package byow.lab12;
import org.junit.Test;
import static org.junit.Assert.*;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.Random;

/**
 * Draws a world consisting of hexagonal regions.
 */
public class HexWorld {
    private static final int WIDTH = 50;
    private static final int HEIGHT = 40;
    private static final int hexagonNumInTesselation = 19;

    public static void fillWithNOTHING(TETile[][] tiles) {
        int height = tiles[0].length;
        int width = tiles.length;
        for (int x = 0; x < width; x += 1) {
            for (int y = 0; y < height; y += 1) {
                tiles[x][y] = Tileset.NOTHING;
            }
        }
    }

    public static void main(String[] args) {
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);

        TETile[][] tiles = new TETile[WIDTH][HEIGHT];
        fillWithNOTHING(tiles);
        makeTesselation(tiles, 3, hexagonNumInTesselation);

        ter.renderFrame(tiles);
    }

    public static void makeTesselation(TETile[][] tiles, int size, int hexagonNumInTesselation) {
        // left half triangle include middle
        makeColumHalfTriangle(tiles, size, hexagonNumInTesselation, true);
        // right half triangle exclude middle
        makeColumHalfTriangle(tiles, size, hexagonNumInTesselation, false);
    }

    public static void makeColumHalfTriangle(TETile[][] tiles, int size, int hexagonNumInTesselation, boolean isLeft) {
        int minHexagonNumInYPosition = ((hexagonNumInTesselation + 1) / 5) - 1;
        int TilesHeight = tiles[0].length;
        int yPositionBias = size * 2;

        // left half triangle include middle
        int MaxHexagonNumInYPosition = ((hexagonNumInTesselation + 1) / 5) + 1;
        // positive bias at x position
        int xPositionBias = size * 2 - 1;
        int yPosition = TilesHeight - 1;
        int xPosition = tiles.length / 2;

        // right half triangle exclude middle
        if (!isLeft) {
            MaxHexagonNumInYPosition = ((hexagonNumInTesselation + 1) / 5);
            // negative bias at x position
            xPositionBias = -(size * 2 - 1);
            yPosition = (TilesHeight - 1) - size;
            xPosition = (tiles.length / 2) + xPositionBias;
        }

        // add hexagons from middle(include/exclude) to left/right
        while (MaxHexagonNumInYPosition >= minHexagonNumInYPosition) {
            int hexagonNumInYPosition = MaxHexagonNumInYPosition;
            int currentYPosition = yPosition;
            while (hexagonNumInYPosition > 0) {
                Hexagon hexagonWithSize = new Hexagon(size);
                hexagonWithSize.addDiffHexagon(tiles, xPosition, currentYPosition);
                currentYPosition -= yPositionBias;
                hexagonNumInYPosition -= 1;
            }
            xPosition += xPositionBias;
            yPosition -= size;
            MaxHexagonNumInYPosition -= 1;
        }
    }

    public static void makeSameHexagons(TETile[][] tiles) {
        int xPosition = 6;
        int count = 6;
        for (int i = 2; i <= 5; i++) {
            Hexagon hexagonWithSize = new Hexagon(i);
            hexagonWithSize.addSameHexagon(tiles, xPosition);
            xPosition += count + i;
            count += 1;
        }
    }
}
