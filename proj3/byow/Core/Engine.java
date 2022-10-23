package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import static byow.Core.Room.*;

public class Engine {
    TERenderer ter = new TERenderer();
    static Long seed;

    /* Feel free to change the width and height. */
    public static final int WIDTH = 80;
    public static final int HEIGHT = 30;
    public static final int RoomNum = 16;

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
        seed = Long.parseLong(number);
        TETile[][] finalWorldFrame = new TETile[WIDTH][HEIGHT];
        return finalWorldFrame;
    }

    // render(and display) the world
    public void renderWorld(TETile[][] world) {
        ter.initialize(WIDTH, HEIGHT);
        fillWithNOTHING(world);
        fillWithRooms(world);
        buildExist();
        ter.renderFrame(world);
    }

    // fill the world with NOTHING
    public static void fillWithNOTHING(TETile[][] tiles) {
        int height = tiles[0].length;
        int width = tiles.length;
        for (int x = 0; x < width; x += 1) {
            for (int y = 0; y < height; y += 1) {
                tiles[x][y] = Tileset.NOTHING;
            }
        }
    }

    // fill the world with Room
    public static TETile[][] fillWithRooms(TETile[][] tiles) {
        Room[] rooms = new Room[RoomNum];
        for (int i = 0; i < RoomNum; i += 1){
            Room room = new Room(tiles, seed);
            room.makeRoom();
            rooms[i] = room;
            if (i > 0) {
                connectTwoRooms(rooms[i], rooms[i - 1]);
            }
        }
        for (int i = 0; i < RoomNum; i += 1){
            rooms[i].closeSomeDoorsInRoom();
        }
//        for (int i = 0; i < RoomNum; i += 1) {
//            System.out.println(rooms[i].countDoorsInRoom());
//            if (rooms[i].countDoorsInRoom() == 0) {
//                System.out.println("xxxxxxxxxxxxx");
//                System.out.println("width " + rooms[i].getWidth());
//                System.out.println("height " + rooms[i].getHeight());
//                System.out.println("xxxxxxxxxxxxx");
//                rooms[i].letPositionWater();
//            }
//        }
        return Room.getWorld();
    }

    // connect rooms one by one, you should consider other rooms and other hallways
    // TODO: we can clear room after connect all rooms!
    public static TETile[][] connectTwoRooms(Room room1, Room room2) {
        // eight directions
        // North, South, East and West.
        // North-East, South-East, North-West and South-West.
        // turn to four situations
        int randomXInFloor1 = room1.getRandomXInFloor();
        int randomYInFloor1 = room1.getRandomYInFloor();
        int randomXInFloor2 = room2.getRandomXInFloor();
        int randomYInFloor2 = room2.getRandomYInFloor();
        int minY = Math.min(randomYInFloor1, randomYInFloor2);
        int maxY = Math.max(randomYInFloor1, randomYInFloor2);
        int minX = Math.min(randomXInFloor1, randomXInFloor2);
        int maxX = Math.max(randomXInFloor1, randomXInFloor2);
        // Vertical or Horizontal hallway
        if (randomXInFloor1 == randomXInFloor2 || randomYInFloor1 == randomYInFloor2) {
            // n and s => same x but different y ==> Vertical hallway
            if (randomXInFloor1 == randomXInFloor2) {
                return buildVerticalHallway(randomXInFloor1, minY, maxY);
            }
            // e and w => same y but different x ==> Horizontal hallway
            return buildHorizontalHallway(randomYInFloor1, minX, maxX);
        }
        // NOT Vertical or Horizontal, so we need link room
        // n-w and s-e
        boolean magic = (randomXInFloor1 > randomXInFloor2 && randomYInFloor1 < randomYInFloor2)
                || (randomXInFloor1 < randomXInFloor2 && randomYInFloor1 > randomYInFloor2);
//        // n-e and s-w
//        if ((randomXInFloor1 > randomXInFloor2 && randomYInFloor1 > randomYInFloor2)
//                || (randomXInFloor1 < randomXInFloor2 && randomYInFloor1 < randomYInFloor2)) {
//            magic = false;
//        }
        // todo: linkRoom not makeRoom() that will affect result?
        Room linkRoom = room1.buildARandomLinkRoom(minX, maxX, minY, maxY, magic);
        connectTwoRooms(room1, linkRoom);
        return connectTwoRooms(room2, linkRoom);
    }
}
