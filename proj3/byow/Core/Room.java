package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.Random;

public class Room {
    private static final int smallestSide = 3;
    private static TETile[][] world;
    private static Random RANDOM;
    private static Long seed;

    private int width;
    private int height;
    private int x;
    private int y;
    private int xInFloor;
    private int yInFloor;
    private int randomXInFloor;
    private int randomYInFloor;

    public Room(TETile[][] world, Long seed) {
        Room.world = world;
        Room.seed = seed;
        this.RANDOM = new Random(seed);
        this.width = randomRoomSide();
        this.height = randomRoomSide();
        this.x = randomRoomPosition(true);
        this.y = randomRoomPosition(false);
        // the position is [x,y] then the position of left and up corner in floor is [x+1, y-1]
        this.xInFloor = x + 1;
        this.yInFloor = y - 1;
        this.randomXInFloor = -1;
        this.randomYInFloor = -1;
    }

    public Room(int x, int y, int width, int height, TETile[][] world, Long seed) {
        Room.world = world;
        this.RANDOM = new Random(seed);
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;
        // the position is [x,y] then the position of left and up corner in floor is [x+1, y-1]
        this.xInFloor = x + 1;
        this.yInFloor = y - 1;
        this.randomXInFloor = -1;
        this.randomYInFloor = -1;
    }

    public TETile[][] makeRoom() {
        // avoid wrong side and marked tiles
        correctRoom();
        // fill all tiles in room
        fillAllTilesInRoom();
        return world;
    }

    public void clearRoom() {
        fillAllTilesInRoom();
    }

    // fill all tiles in room
    private void fillAllTilesInRoom() {
        // bottom
        int level = height - 1; //11 -> 1time
        for (int i = x; i < x + width; i += 1) {
            fillOneTileInRoom(i, y - level, Tileset.WALL);
        }
        // middle
        level -= 1; // 10time
        while (level > 0){
            for (int i = x; i < x + width; i += 1 ) {
                if (i == x || i == x + width - 1) {
                    fillOneTileInRoom(i, y - level, Tileset.WALL);
                } else {
                    fillOneTileInRoom(i, y - level, Tileset.FLOOR);
                }
            }
            level -= 1;
        }
        // top
        for (int i = x; i < x + width; i += 1) {
            fillOneTileInRoom(i, y, Tileset.WALL);
        }
    }

    // fill one tile in room
    private void fillOneTileInRoom(int x, int y, TETile tileType) {
        world[x][y] = tileType;
        world[x][y].markTile();
        world[x][y].markRoom();
    }

    // avoid wrong side and marked tiles
    private void correctRoom() {
        while (!validRoom()) {
            width = randomRoomSide();
            height = randomRoomSide();
            x = randomRoomPosition(true);
            y = randomRoomPosition(false);
        }
        xInFloor = x + 1;
        yInFloor = y - 1;
    }

    // it is valid room?
    private boolean validRoom() {
        int worldWidth = world.length;
        // int worldHeight = world[0].length;
        // choose left and up corner as [x,y]
        if (x + width >= worldWidth || y - height < 0) {
            return false;
        }
        // check all tiles
        for (int i = x; i < x + width; i += 1) {
            for (int j = y; j > y - height; j -= 1) {
                if (world[i][j].isMarked()) {
                    return false;
                }
            }
        }
        return true;
    }

    // get random side of room
    private int randomRoomSide() {
        // length of side from 3 to 12, inclusive 3 but exclusive 9
        int side = RANDOM.nextInt(13);
        while (side < smallestSide) {
            side = RANDOM.nextInt(13);
        }
        return side;
    }

    // get random position of room
    private int randomRoomPosition(boolean isXPosition) {
        int lengthLimit = world.length;
        if (!isXPosition) {
            lengthLimit = world[0].length;
        }
        // size of x position from 0 to WIDTH, inclusive 0 but exclusive WIDTH
        // or size of y position from 0 to HEIGHT, inclusive 0 but exclusive HEIGHT
        return RANDOM.nextInt(lengthLimit);
    }

    // build a random link room
    // magic: n-w and s-e => true, but n-e and s-w => false
    public Room buildARandomLinkRoom(int minX, int maxX, int minY, int maxY, boolean magic) {
        // we have two cases of building link room
        int randomNum = RANDOM.nextInt(2);
        if (magic) {
            if (randomNum == 0) {
                return buildALinkRoom(maxX, maxY);
            }
            return buildALinkRoom(minX, minY);
        }
        if (randomNum == 0) {
            return buildALinkRoom(maxX, minY);
        }
        return buildALinkRoom(minX, maxY);
    }

    // connect two rooms maybe need build a link room(it is a 3*3 room i.e. the smallest room)
    private Room buildALinkRoom(int xInFloor, int yInFloor) {
        return new Room(xInFloor - 1, yInFloor + 1, smallestSide, smallestSide, world, seed);
    }

    public void randomFloorInRoom() {
        // 3*3 room => 1*1 floor; 3*4 => 1*2 floor; 4*5 => 2*3 floor
        // ...==> width*height room =>  (width-2)*(height-2) floor
        int floorNum = (width - 2) * (height - 2);
        // randomNum >= 0? => yes!
        int randomNum = RANDOM.nextInt(floorNum);
        int num = 0;
        for (int x = xInFloor; x < xInFloor + width - 2; x += 1) {
            for (int y = yInFloor; y > yInFloor - height + 2; y -= 1 ) {
                if (num == randomNum) {
                    randomXInFloor = x;
                    randomYInFloor = y;
                }
                num += 1;
            }
        }
    }

    // same y but different x
    static TETile[][] buildHorizontalHallway(int midY, int leftX, int rightX) {
        // note: we build hallway that has 1 width of floor
        int bottomY = midY - 1;
        int topY = midY + 1;
        for (int level = 0, y = bottomY; y <= topY; y += 1, level += 1) {
            TETile tileType;
            // 1 width of floor
            if (level == 1) {
                tileType = Tileset.FLOOR;
            } else {
                tileType = Tileset.WALL;
            }
            for (int x = leftX; x <= rightX; x += 1) {
                // if it is floor, we don't change it, otherwise, we change it
                if (!world[x][y].equals(Tileset.FLOOR)) {
                    world[x][y] = tileType;
                }
            }
        }
        return world;
    }

    // same x but different y
    static TETile[][] buildVerticalHallway(int midX, int bottomY, int topY) {
        // note: we build hallway that has 1 width of floor
        int leftX = midX - 1;
        int rightX = midX + 1;
        for (int level = 0, x = leftX; x <= rightX; x += 1, level += 1) {
            TETile tileType;
            // 1 width of floor
            if (level == 1) {
                tileType = Tileset.FLOOR;
            } else {
                tileType = Tileset.WALL;
            }
            for (int y = bottomY; y <= topY; y += 1) {
                // if it is floor, we don't change it, otherwise, we change it
                if (!world[x][y].equals(Tileset.FLOOR)) {
                    world[x][y] = tileType;
                }
            }
        }
        return world;
    }

    public void dropSomeWallsInWorld() {
        int worldWidth = world.length;
        int worldHeight = world[0].length;
        for (int x = 1; x < worldWidth - 1; x += 1) {
            for (int y = 1; y < worldHeight - 1; y += 1) {
                if (world[x][y].isRoom() && world[x][y].equals(Tileset.WALL)) {
                    if (world[x - 1][y].equals(Tileset.FLOOR) && world[x + 1][y].equals(Tileset.FLOOR)) {
                        world[x][y] = Tileset.FLOOR;
                    }
                    if  (world[x][y + 1].equals(Tileset.FLOOR) && world[x][y - 1].equals(Tileset.FLOOR)) {
                        world[x][y] = Tileset.FLOOR;
                    }
                }
            }
         }
    }

    public int countDoorsInRoom() {
        // we open three doors at most
//        int randomNum = RANDOM.nextInt(4);
//        if (randomNum == 0 ){
//            randomNum = RANDOM.nextInt(4);
//        }
        int worldWidth = world.length;
        int worldHeight = world[0].length;
        int doorCount = 0;
        // DON'T count four corners
        // left side
        for (int y = this.y - 1; y > this.y - height + 1; y -= 1) {
            if (x - 1 >= 0 && x + 1 < worldWidth) {
                if (world[x - 1][y].equals(Tileset.FLOOR) && world[x + 1][y].equals(Tileset.FLOOR)) {
//                world[x][y] = Tileset.FLOOR;
                    doorCount += 1;
                }
            }
        }
        // right side
        for (int y = this.y - 1; y > this.y - height + 1 ; y -= 1) {
            if (x + width - 2 >= 0 && x + width < worldWidth) {
                if (world[x + width - 2][y].equals(Tileset.FLOOR) && world[x + width][y].equals(Tileset.FLOOR)) {
//                world[x + width - 1][y] = Tileset.FLOOR;
                    doorCount += 1;
                }
            }
        }
        // top side
        for (int x = this.x + 1; x < this.x + width - 1; x += 1) {
            if (y + 1 < worldHeight && y - 1 >= 0) {
                if (world[x][y + 1].equals(Tileset.FLOOR) && world[x][y - 1].equals(Tileset.FLOOR)) {
//                world[x][y] = Tileset.FLOOR;
                    doorCount += 1;
                }
            }
        }
        // bottom side
        for (int x = this.x + 1; x < this.x + width - 1; x += 1) {
            if (y - height + 2 < worldHeight && y - height >= 0) {
                if (world[x][y - height + 2].equals(Tileset.FLOOR) && world[x][y - height].equals(Tileset.FLOOR)) {
//                world[x][y - height + 1] = Tileset.FLOOR;
                    doorCount += 1;
                }
            }
        }
        return doorCount;
    }

    private int getRandomNum(int limit) {
        return RANDOM.nextInt(limit);
    }

    public void closeSomeDoorsInRoom() {
        int worldWidth = world.length;
        int worldHeight = world[0].length;
        // DON'T count four corners
        // left side
        for (int y = this.y - 1; y > this.y - height + 2; y -= 1) {
            if (x - 1 >= 0 && x + 1 < worldWidth) {
                if (world[x][y].equals(Tileset.FLOOR) && world[x][y - 1].equals(Tileset.FLOOR)) {
                    int bias = RANDOM.nextInt(2);
                    world[x][y - bias] = Tileset.WALL;
                }
            }
        }
        // right side
        for (int y = this.y - 1; y > this.y - height + 2; y -= 1) {
            if (x + width - 2 >= 0 && x + width < worldWidth) {
                if (world[x + width - 1][y].equals(Tileset.FLOOR) && world[x + width - 1][y - 1].equals(Tileset.FLOOR)) {
                    int bias = RANDOM.nextInt(2);
                    world[x + width - 1][y - bias] = Tileset.WALL;
                }
            }
        }
        // top side
        for (int x = this.x + 1; x < this.x + width - 2; x += 1) {
            if (y + 1 < worldHeight && y - 1 >= 0) {
                if (world[x][y].equals(Tileset.FLOOR) && world[x + 1][y].equals(Tileset.FLOOR)) {
                    int bias = RANDOM.nextInt(2);
                    world[x + bias][y] = Tileset.WALL;
                }
            }
        }
        // bottom side
        for (int x = this.x + 1; x < this.x + width - 2; x += 1) {
            if (y - height + 2 < worldHeight && y - height >= 0) {
                if (world[x][y - height + 1].equals(Tileset.FLOOR) && world[x - 1][y - height + 1].equals(Tileset.FLOOR)) {
                    int bias = RANDOM.nextInt(2);
                    world[x + bias][y - height + 1] = Tileset.WALL;
                }
            }
        }
    }

    public static void buildExist() {
        int worldWidth = world.length;
        int worldHeight = world[0].length;
        int countWall = 0;
        for (int x = 1; x < worldWidth - 1; x += 1) {
            for (int y = 1; y < worldHeight - 1; y += 1) {
                if (world[x][y].equals(Tileset.WALL)) {
                    if (world[x + 1][y].equals(Tileset.FLOOR)
                            || world[x - 1][y].equals(Tileset.FLOOR)
                            || world[x][y + 1].equals(Tileset.FLOOR)
                            || world[x][y - 1].equals(Tileset.FLOOR)) {
                        countWall += 1;
                    }
                }
            }
        }
        int randomNum = RANDOM.nextInt(countWall);
        int num = 0;
        for (int x = 1; x < worldWidth - 1; x += 1) {
            for (int y = 1; y < worldHeight - 1; y += 1) {
                if (world[x][y].equals(Tileset.WALL)) {
                    if (world[x + 1][y].equals(Tileset.FLOOR)
                            || world[x - 1][y].equals(Tileset.FLOOR)
                            || world[x][y + 1].equals(Tileset.FLOOR)
                            || world[x][y - 1].equals(Tileset.FLOOR)) {
                        num += 1;
                        if (num == randomNum) {
                            world[x][y] = Tileset.LOCKED_DOOR;
                        }
                    }
                }
            }
        }
    }

    // todo: random walls
    public static void randomWallInWorld() {
        int worldWidth = world.length;
        int worldHeight = world[0].length;
        for (int x = 0; x < worldWidth; x += 1) {
            for (int y = 0; y < worldHeight; y += 1) {
                if (world[x][y].equals(Tileset.WALL)) {
                    world[x][y] = randomTile();
                }
            }
        }
    }

    private static TETile randomTile() {
        int tileNum = RANDOM.nextInt(4);
        switch (tileNum) {
            case 0: return Tileset.WALL;
            case 1: return Tileset.FLOWER;
            case 2: return Tileset.GRASS;
            case 3: return Tileset.SAND;
            default: return Tileset.NOTHING;
        }
    }

    public int getRandomXInFloor() {
        if (randomXInFloor == -1) {
            randomFloorInRoom();
        }
        return randomXInFloor;
    }

    public int getRandomYInFloor() {
        if (randomYInFloor == -1) {
            randomFloorInRoom();
        }
        return randomYInFloor;
    }

    public void setWorld(TETile[][] world) {
        Room.world = world;
    }

    public static TETile[][] getWorld() {
        return world;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void letPositionWater() {
        world[x][y] = Tileset.WATER;
    }

}
