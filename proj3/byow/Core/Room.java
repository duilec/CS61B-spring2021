package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.TETileWrapper;
import byow.TileEngine.Tileset;

import java.util.LinkedList;
import java.util.Random;

public class Room {
    private static final int smallestSide = 3;
    private static TETileWrapper[][] worldWrappers;
    private static Random RANDOM;
    private static Long seed;

    private int width;
    private int height;
    private int x;
    private int y;

    public Room(TETileWrapper[][] worldWrappers, Long seed) {
        Room.worldWrappers = worldWrappers;
        Room.seed = seed;
        Room.RANDOM = new Random(seed);
        this.width = randomRoomSide();
        this.height = randomRoomSide();
        this.x = randomRoomPosition(true);
        this.y = randomRoomPosition(false);
    }

    public TETileWrapper[][] makeRoom() {
        // avoid wrong side and marked tiles
        correctRoom();
        // fill all tiles in room
        fillAllTilesInRoom();
        return worldWrappers;
    }

    // fill all tiles in room
    private void fillAllTilesInRoom() {
        // bottom
        int level = height - 1;
        for (int i = x; i < x + width; i += 1) {
            fillOneTileInRoom(i, y - level, Tileset.WALL);
        }
        // middle
        level -= 1;
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
        worldWrappers[x][y].setTile(Tileset.WATER);
    }

    // fill one tile in room
    private void fillOneTileInRoom(int x, int y, TETile tileType) {
        worldWrappers[x][y].setTile(tileType);
        worldWrappers[x][y].markRoom();
        worldWrappers[x][y].markTile(true);
    }

    // avoid wrong side and marked tiles
    private void correctRoom() {
        while (!validRoom()) {
            width = randomRoomSide();
            height = randomRoomSide();
            x = randomRoomPosition(true);
            y = randomRoomPosition(false);
        }
    }

    // it is valid room?
    private boolean validRoom() {
        int worldWidth = worldWrappers.length;
        //int worldHeight = worldWrappers[0].length;
        // choose left and up corner as [x,y]
        if (x + width >= worldWidth || y - height - 1 < 0) {
            return false;
        }
        // check all tiles
        for (int i = x; i < x + width; i += 1) {
            for (int j = y; j > y - height; j -= 1) {
                if (!worldWrappers[i][j].getTile().equals(Tileset.NOTHING)) {
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
        // return RandomUtils.uniform(RANDOM,3, 13);
    }

    // get random position of room
    private int randomRoomPosition(boolean isXPosition) {
        int lengthLimit = worldWrappers.length;
        if (!isXPosition) {
            lengthLimit = worldWrappers[0].length;
        }
        // size of x position from 0 to WIDTH, inclusive 0 but exclusive WIDTH
        // or size of y position from 0 to HEIGHT, inclusive 0 but exclusive HEIGHT
        return RANDOM.nextInt(lengthLimit);
    }

    private LinkedList<TETileWrapper> getDoorsInRoom() {
        LinkedList<TETileWrapper> doors = new LinkedList<>();
        // DON'T consider four corners
        // left side
        for (int y = this.y - 1; y > this.y - height + 1; y -= 1) {
            doors.add(worldWrappers[x][y]);
        }
        // right side
        for (int y = this.y - 1; y > this.y - height + 1 ; y -= 1) {
            doors.add(worldWrappers[x + width - 1][y]);
        }
        // top side
        for (int x = this.x + 1; x < this.x + width - 1; x += 1) {
            doors.add(worldWrappers[x][y]);
        }
        // bottom side
        for (int x = this.x + 1; x < this.x + width - 1; x += 1) {
            doors.add(worldWrappers[x][y - height + 1]);
        }
        return doors;
    }

    public LinkedList<TETileWrapper> getExits() {
        LinkedList<TETileWrapper> doors = getDoorsInRoom();
        LinkedList<TETileWrapper> exits = new LinkedList<>();
        for (TETileWrapper door : doors) {
            TETileWrapper exit = getExitOfDoor(door.getX(), door.getY());
            if (exit != null) {
                exits.add(exit);
            }
        }
        return exits;
    }

    public TETileWrapper getRandomExitByDoors() {
        LinkedList<TETileWrapper> doors = getDoorsInRoom();
        LinkedList<TETileWrapper> exits = new LinkedList<>();
        for (TETileWrapper door : doors) {
            TETileWrapper exit = getExitOfDoor(door.getX(), door.getY());
            if (exit != null) {
                exits.add(exit);
            }
        }
        int randomNum = RANDOM.nextInt(exits.size());
        TETileWrapper randomExit = exits.get(randomNum);
        int x = randomExit.getX();
        int y = randomExit.getY();
        // set exit as floor
        worldWrappers[x][y].setTile(Tileset.FLOOR);

        // set door as floor by exit
        setDoorAsFloorByExit(x, y);
        return worldWrappers[x][y];
    }

    public void setDoorAsFloorByExit(int x, int y) {
        // north (x, y + 1)
        if (worldWrappers[x][y + 1].isRoom()) {
            worldWrappers[x][y + 1].setTile(Tileset.FLOOR);
        }
        // south (x, y - 1)
        if (worldWrappers[x][y - 1].isRoom()) {
            worldWrappers[x][y - 1].setTile(Tileset.FLOOR);
        }
        // west (x - 1, y)
        if (worldWrappers[x - 1][y].isRoom()) {
            worldWrappers[x - 1][y].setTile(Tileset.FLOOR);
        }
        // east (x + 1, y)
        if (worldWrappers[x + 1][y].isRoom()) {
            worldWrappers[x + 1][y].setTile(Tileset.FLOOR);
        }
    }

    private TETileWrapper getExitOfDoor(int x, int y) {
        // north (x, y + 1)
        if (isExit(x, y + 1)) {
            return worldWrappers[x][y + 1];
        }
        // south (x, y - 1)
        if (isExit(x, y - 1)) {
            return worldWrappers[x][y - 1];
        }
        // west (x - 1, y)
        if (isExit(x - 1, y)) {
            return worldWrappers[x - 1][y];
        }
        // east (x + 1, y)
        if (isExit(x + 1, y)) {
            return worldWrappers[x + 1][y];
        }
        return null;
    }


    private boolean isExit(int x, int y) {
        int worldWidth = worldWrappers.length;
        int worldHeight = worldWrappers[0].length;
        // can't choose side of limbo as exit(floor)
        return x < worldWidth - 1 && x > 0
                && y < worldHeight - 1 && y > 0
                && !worldWrappers[x][y].isRoom();
    }

    public static void buildExit() {
        int worldWidth = worldWrappers.length;
        int worldHeight = worldWrappers[0].length;
        int countWall = 0;
        for (int x = 1; x < worldWidth - 1; x += 1) {
            for (int y = 1; y < worldHeight - 1; y += 1) {
                if (worldWrappers[x][y].getTile().equals(Tileset.WALL)) {
                    if (worldWrappers[x + 1][y].getTile().equals(Tileset.FLOOR)
                            || worldWrappers[x - 1][y].getTile().equals(Tileset.FLOOR)
                            || worldWrappers[x][y + 1].getTile().equals(Tileset.FLOOR)
                            || worldWrappers[x][y - 1].getTile().equals(Tileset.FLOOR)) {
                        countWall += 1;
                    }
                }
            }
        }
        int randomNum = RANDOM.nextInt(countWall);
        int num = 0;
        for (int x = 1; x < worldWidth - 1; x += 1) {
            for (int y = 1; y < worldHeight - 1; y += 1) {
                if (worldWrappers[x][y].getTile().equals(Tileset.WALL)) {
                    if (worldWrappers[x + 1][y].getTile().equals(Tileset.FLOOR)
                            || worldWrappers[x - 1][y].getTile().equals(Tileset.FLOOR)
                            || worldWrappers[x][y + 1].getTile().equals(Tileset.FLOOR)
                            || worldWrappers[x][y - 1].getTile().equals(Tileset.FLOOR)) {
                        num += 1;
                        if (num == randomNum) {
                            worldWrappers[x][y].setTile(Tileset.LOCKED_DOOR);
                        }
                    }
                }
            }
        }
    }

    // todo: random walls
    public static void randomWallInWorld() {
        int worldWidth = worldWrappers.length;
        int worldHeight = worldWrappers[0].length;
        for (int x = 0; x < worldWidth; x += 1) {
            for (int y = 0; y < worldHeight; y += 1) {
                if (worldWrappers[x][y].getTile().equals(Tileset.WALL)) {
                    worldWrappers[x][y].setTile(randomTile());
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

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
