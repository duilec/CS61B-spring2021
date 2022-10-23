package byow.lab12;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.Random;

public class Hexagon {
    private int size;
    private int height;
    private static final long SEED = 2873123;
    private static final Random RANDOM = new Random(SEED);

    public Hexagon(int size) {
        this.size = size;
        this.height = size * 2;
    }

    public void addSameHexagon(TETile[][] tiles, int xPosition) {
        int startHeight = tiles[0].length;
        addHexagon(tiles, xPosition, startHeight, Tileset.WALL);
    }

    public void addDiffHexagon(TETile[][] tiles, int xPosition, int startHeight) {
        addHexagon(tiles, xPosition, startHeight, randomTile());
    }

    // adds a hexagon of side length xPosition to a given position in the world.
    public void addHexagon(TETile[][] tiles, int xPosition, int startHeight, TETile tile) {
        int changeSize = size;
        // Upper half triangle
        for (int y = startHeight - 1; y >= startHeight - size; y -= 1) {
            for (int x = xPosition; x < xPosition + changeSize; x += 1) {
                tiles[x][y] = tile;
            }
            changeSize += 2;
            xPosition -= 1;
        }
        // Bottom half triangle
        changeSize -= 2;
        xPosition += 1;
        for (int y = startHeight - 1 - size; y >= startHeight - height; y -= 1) {
            for (int x = xPosition; x < xPosition + changeSize; x += 1) {
                tiles[x][y] = tile;
            }
            changeSize -= 2;
            xPosition += 1;
        }
    }

    /** Picks a RANDOM tile with a 33% change of being
     *  a wall, 33% chance of being a flower, and 33%
     *  chance of being empty space.
     */
    private static TETile randomTile() {
        int tileNum = RANDOM.nextInt(5);
        switch (tileNum) {
            case 0: return Tileset.WALL;
            case 1: return Tileset.FLOWER;
            case 2: return Tileset.GRASS;
            case 3: return Tileset.WATER;
            case 4: return Tileset.TREE;
            default: return Tileset.NOTHING;
        }
    }
}
