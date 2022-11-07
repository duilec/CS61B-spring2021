package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.TETileWrapper;
import byow.TileEngine.Tileset;

import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class WorldGenerator implements Serializable {
    private static final int RoomNum = 35;  // the number of room
    private final int N;                    // dimension of worldWrappers(we assume worldWrappers is N*N)
    private Random RANDOM;
    private Long seed;

    // set the worldWrappers
    private TETileWrapper[][] worldWrappers;
    private final int width;
    private final int height;

    // using in A*
    private int source;
    private int target;
    private final int[] edgeTo;
    private final int[] distTo;
    // we found target?
    private boolean targetFound;

    // it is the source connect to target at first time?
    private boolean isFirst;
    // we always use center as target
    private boolean alwaysCenterTarget;

    // avatar
    private TETileWrapper avatar;

    // all rooms
    private LinkedList<Room> rooms = new LinkedList<>();

    // turn on/off in room
    // turn == true => turn on; turn == false => turn off
    private boolean turn = true;
    // true => turn on; false => turn off
    private LinkedList<Room> randomRooms = new LinkedList<>();

    public WorldGenerator(Long seed, TETile[][] world, boolean alwaysCenterTarget) {
        this.seed = seed;
        this.RANDOM = new Random(seed);

        this.height = world[0].length;
        this.width = world.length;
        this.N = Math.max(width, height);
        this.edgeTo = new int[V()];
        this.distTo = new int[V()];
        this.targetFound = false;

        // init world
        this.worldWrappers = new TETileWrapper[width][height];
        for (int x = 0; x < width; x += 1) {
            for (int y = 0; y < height; y += 1) {
                worldWrappers[x][y] = new TETileWrapper(Tileset.NOTHING, x, y);
            }
        }
        reset();

        this.source = 0;
        // set target at center
        setTarget(width / 2, height / 2);
        this.isFirst = true;
        this.alwaysCenterTarget = alwaysCenterTarget;
    }

    public TETile[][] generateWorld() {
        connectRooms();
        fillSomeWalls();
        randomAvatar();
        createRandomLightInRooms();
        return getWorldByWorldWrappers();
    }

    public TETile[][] moveAvatarThenGenerateWorld(String direction) {
        moveAvatar(direction);
        return getWorldByWorldWrappers();
    }

    private TETile[][] getWorldByWorldWrappers(){
        TETile[][] world = new TETile[width][height];
        for (int x = 0; x < width; x += 1) {
            for (int y = 0; y < height; y += 1) {
                world[x][y] = worldWrappers[x][y].getTile();
            }
        }
        return world;
    }

    // reset all edgeTo, distTo and worldWrappers with not room
    private void reset() {
        for (int i = 0; i < V(); i += 1) {
            edgeTo[i] = Integer.MAX_VALUE;
            distTo[i] = Integer.MAX_VALUE;
        }
        for (int x = 0; x < width; x += 1) {
            for (int y = 0; y < height; y += 1) {
                if (!worldWrappers[x][y].isRoom()){
                    worldWrappers[x][y].markTile(false);
                }
            }
        }
    }

    private void connectRooms() {
        for (int i = 0; i < RoomNum; i += 1){
            // note: we pass an object of 'worldWrappers', we modify it by makeRoom() and
            // the result also appear in parent(i.e. connectRooms() is parent of makeRoom())
            // *pass an object* aka. we don't care about pointer or other

            // set room number from 0 to RoomNum - 1 in room
            Room room = new Room(worldWrappers, i, seed);
            rooms.add(room);
            room.makeRoom();
            reset();
            connectRoomToTarget(room);
        }
    }

    private void connectRoomToTarget(Room room) {
        if (alwaysCenterTarget) {
            connectRoomToTargetByCenterTarget(room);
        } else {
            connectRoomToTargetByRandomTarget(room);
        }
    }

    private void connectRoomToTargetByCenterTarget(Room room) {
        targetFound = false;
        TETileWrapper randomExit = room.getRandomExitByDoorsInRoom();
        setSource(randomExit.getX(), randomExit.getY());
        astar();
        buildHallwayByShortestPath(target);
    }

    private void connectRoomToTargetByRandomTarget(Room room) {
        if (isFirst) {
            setFirstTargetAndSource(room);
            astar();
        } else {
            targetFound = false;
            setRandomTargetAndSource(room);
            astar();
        }
        buildHallwayByShortestPath(target);
    }

    private void setFirstTargetAndSource(Room room) {
        // set first target
        int randomNum = RANDOM.nextInt((width - 2) * (height - 2));
        int num = 0;
        // don't choose target as floor in limbo
        for (int x = 1; x < width - 1; x += 1) {
            for (int y = 1; y < height - 1; y += 1 ) {
               if (num == randomNum) {
                   setTarget(x, y);
                   // you can't mark target by markTile() in this method
                   worldWrappers[x][y].setTile(Tileset.FLOOR);
                   isFirst = false;
                   // set first source
                   TETileWrapper randomExit = room.getRandomExitByDoorsInRoom();
                   setSource(randomExit.getX(), randomExit.getY());
                   return;
               }
               num += 1;
            }
        }
    }

    private void setRandomTargetAndSource(Room room){
        LinkedList<TETileWrapper> notRoomButFloors = notRoomButFloors();
        LinkedList<TETileWrapper> exits = room.getExitsInRoom();
        int randomNum1, randomNum2;
        // set random target
        randomNum1 = RANDOM.nextInt(notRoomButFloors.size());
        TETileWrapper tileWrapper = notRoomButFloors.get(randomNum1);
        setTarget(tileWrapper.getX(), tileWrapper.getY());
        // set random source
        randomNum2 = RANDOM.nextInt(exits.size());
        TETileWrapper randomExit = exits.get(randomNum2);
        int x = randomExit.getX();
        int y = randomExit.getY();
        setSource(x, y);
        // set door in room as floor by exit
        room.setDoorAsFloorByExitInRoom(x, y);
    }

    private LinkedList<TETileWrapper> notRoomButFloors() {
        LinkedList<TETileWrapper> notRoomButFloors = new LinkedList<>();
        for (int x = 1; x < width - 1; x += 1) {
            for (int y = 1; y < height - 1; y += 1 ) {
                if (!worldWrappers[x][y].isRoom() && worldWrappers[x][y].getTile().equals(Tileset.FLOOR)) {
                    notRoomButFloors.add(worldWrappers[x][y]);
                }
            }
        }
        return notRoomButFloors;
    }

    /** Estimate of the distance from v to the target. */
    private int h(int v) {
        return Math.abs(toX(v) - toX(target)) + Math.abs(toY(v) - toY(target));
    }

    /** Finds vertex estimated to be closest to target. */
    private int findMinimumUnmarked(Queue<Integer> queue) {
        int minimumVertex = queue.peek();
        int minimumPath = distTo[minimumVertex] + h(minimumVertex);
        for (int vertex : queue) {
            if (distTo[vertex] + h(vertex) < minimumPath) {
                minimumVertex = vertex;
            }
        }
        return minimumVertex;
    }

    /** Performs an A star search from vertex s. */
    private void astar() {
        Queue<Integer> fringe = new ArrayDeque<>();
        fringe.add(source);
        setMarkInWorldWrappers(source, true);
        while (!fringe.isEmpty()){
            int v = findMinimumUnmarked(fringe);
            fringe.remove(v);
            for (TETileWrapper tileWrapper : tileNeighbors(v)){
                if (!tileWrapper.isMarked()) {
                    int w = xyTo1D(tileWrapper.getX(), tileWrapper.getY());
                    fringe.add(w);
                    setMarkInWorldWrappers(w, true);
                    edgeTo[w] = v;
                    distTo[w] = distTo[v] + 1;
                    if (w == target) {
                        targetFound = true;
                    }
                    if (targetFound) {
                        return;
                    }
                }
            }
        }
    }

    private LinkedList<TETileWrapper> tileNeighbors(int v) {
        LinkedList<TETileWrapper> neighbors = new LinkedList<>();
        int x = toX(v);
        int y = toY(v);
        // north (x, y + 1)
        if (isNeighbor(x, y + 1)) {
            neighbors.add(worldWrappers[x][y + 1]);
        }
        // south (x, y - 1)
        if (isNeighbor(x, y - 1)) {
            neighbors.add(worldWrappers[x][y - 1]);
        }
        // west (x - 1, y)
        if (isNeighbor(x - 1, y)) {
            neighbors.add(worldWrappers[x - 1][y]);
        }
        // east (x + 1, y)
        if (isNeighbor(x + 1, y)) {
            neighbors.add(worldWrappers[x + 1][y]);
        }
        return neighbors;
    }

    private boolean isNeighbor(int x, int y) {
        // can't choose side of limbo and room as floor
        return x < width - 1 && x > 0
                && y < height - 1 && y > 0
                && !worldWrappers[x][y].isRoom();
    }

    private void buildHallwayByShortestPath(int v) {
        // center as floor
        int x = toX(v);
        int y = toY(v);
        worldWrappers[x][y].setTile(Tileset.FLOOR);
        // four directions as wall
        // north (x, y + 1)
        if (isHallwayWall(x, y + 1)) {
            worldWrappers[x][y + 1].setTile(Tileset.WALL);
        }
        // south (x, y - 1)
        if (isHallwayWall(x, y - 1)) {
            worldWrappers[x][y - 1].setTile(Tileset.WALL);
        }
        // west (x - 1, y)
        if (isHallwayWall(x - 1, y)) {
            worldWrappers[x - 1][y].setTile(Tileset.WALL);
        }
        // east (x + 1, y)
        if (isHallwayWall(x + 1, y)) {
            worldWrappers[x + 1][y].setTile(Tileset.WALL);
        }
        if (v == source) {
            return;
        }
        buildHallwayByShortestPath(edgeTo[v]);
    }

    private boolean isHallwayWall(int x, int y) {
        // can choose side of limbo and room as wall of hallway
        // but can't choose floor in already build hallway as wall of hallway
        return x < width  && x >= 0
                && y < height && y >= 0
                && !worldWrappers[x][y].isRoom()
                && !worldWrappers[x][y].getTile().equals(Tileset.FLOOR);
    }

    private void setSource(int x, int y) {
        this.source = xyTo1D(x, y);
    }

    private void setTarget(int x, int y) {
        this.target = xyTo1D(x, y);
    }

    // you may meet some hallways with width of bigger than 2 (i.e. 3*3 floors or more)
    // so, we add a wall in the center of hallways with 3*3 floors
    private void fillSomeWalls() {
        for (int x = 0; x <= width - 3; x += 1) {
            for (int y = 2; y <= height - 1; y += 1) {
                if (!worldWrappers[x][y].isRoom()
                        && worldWrappers[x][y].getTile().equals(Tileset.FLOOR)) {
                    int floorCount = 0;
                    // note: we should reset loop in each loop (i.e. int i = x; int j = y;)
                    for (int i = x; i <= x + 2; i += 1) {
                        for (int j = y; j >= y - 2; j -= 1) {
                            if (worldWrappers[i][j].getTile().equals(Tileset.FLOOR)) {
                                floorCount += 1;
                            }
                        }
                    }
                    // if it is 3*3 floors, then center set as wall
                    if (floorCount == 9) {
                        worldWrappers[x + 1][y - 1].setTile(Tileset.WALL);
                    }
                }
            }
        }
    }

    // create random avatar
    private void randomAvatar() {
        LinkedList<TETileWrapper> floors = new LinkedList<>();
        for (int x = 0; x <= width - 1; x += 1) {
            for (int y = 0; y <= height - 1; y += 1) {
                if (worldWrappers[x][y].getTile().equals(Tileset.FLOOR)) {
                    floors.add(worldWrappers[x][y]);
                }
            }
        }
        int randomNum = RANDOM.nextInt(floors.size());
        TETileWrapper avatarTemp = floors.get(randomNum);
        worldWrappers[avatarTemp.getX()][avatarTemp.getY()].setTile(Tileset.AVATAR);
        this.avatar = worldWrappers[avatarTemp.getX()][avatarTemp.getY()];
    }

    // move avatar
    private void moveAvatar(String direction) {
        // W, up
        if (direction.equals("W") && validDirection("W")) {
            moveTo("W");
        }
        // S, down
        if (direction.equals("S") && validDirection("S")) {
            moveTo("S");
        }
        // A, left
        if (direction.equals("A") && validDirection("A")) {
            moveTo("A");
        }
        // D, right
        if (direction.equals("D") && validDirection("D")) {
            moveTo("D");
        }
        keepLightingWithAvatarInRoom();
    }

    private boolean validDirection(String direction) {
        int x = avatar.getX();
        int y = avatar.getY();
        // W,S,A,D
        TETileWrapper tileWrapper = switch (direction) {
            case "W" -> worldWrappers[x][y + 1];
            case "S" -> worldWrappers[x][y - 1];
            case "A" -> worldWrappers[x - 1][y];
            case "D" -> worldWrappers[x + 1][y];
            default -> null;
        };
        return tileWrapper.getTile().equals(Tileset.FLOOR)
                || tileWrapper.getTile().description().equals(Tileset.LIGHTS[0].description());
    }

    private void moveTo(String direction) {
        int x = avatar.getX();
        int y = avatar.getY();
        // W,S,A,D
        switch (direction) {
            case "W":
                worldWrappers[x][y].setTile(Tileset.FLOOR);
                worldWrappers[x][y + 1].setTile(Tileset.AVATAR);
                // note: you should reset this.avatar
                this.avatar = worldWrappers[x][y + 1];
                break;
            case "S":
                worldWrappers[x][y].setTile(Tileset.FLOOR);
                worldWrappers[x][y - 1].setTile(Tileset.AVATAR);
                this.avatar = worldWrappers[x][y - 1];
                break;
            case "A":
                worldWrappers[x][y].setTile(Tileset.FLOOR);
                worldWrappers[x - 1][y].setTile(Tileset.AVATAR);
                this.avatar = worldWrappers[x - 1][y];
                break;
            case "D":
                worldWrappers[x][y].setTile(Tileset.FLOOR);
                worldWrappers[x + 1][y].setTile(Tileset.AVATAR);
                this.avatar = worldWrappers[x + 1][y];
                break;
        }
    }

    // create random light in room
    private void createRandomLightInRooms() {
        // lighten in rooms
        for (Room room : rooms) {
            int x = room.getX();
            int y = room.getY();
            int wight = room.getWidth();
            int height = room.getHeight();
            int randomNum = RANDOM.nextInt((wight - 2) * (height - 2));
            int count = 0;
            for (int i = x + 1; i <= x + wight - 2; i += 1) {
                for(int j = y - 1; j >= y - height + 2; j -= 1) {
                    if (count == randomNum) {
                        // set x with light and y with light in room
                        room.setXWithLight(i);
                        room.setYWithLight(j);
                        turnOnOrOffLightInRoom(i, j, room.getRoomNum(), room.getTurnOn());
                    }
                    count += 1;
                }
            }
        }
    }

    private void keepLightingWithAvatarInRoom() {
        // lighten with avatar in room
        if (avatar.isRoom()) {
            Room room = rooms.get(avatar.getRoomNum());
            //  if turn == false but room is lighting(i.e. turn on), we should keep lighting
            //  if turn == false but room is not lighting(i.e. turn off), we should keep not lighting
            turnOnOrOffLightInRoom(room.getXWithLight(), room.getYWithLight(), room.getRoomNum(), room.getTurnOn());
        }
    }

    // randomly number of room to turn on/off light
    public TETile[][] turnOnOrOffLightInRooms() {
        // 0. turn on -> 1. turn off -> 2. turn on -> 3. turn off -> 4. turn on -> ...
        // i.e. 0. turn = true -> 1. turn = false -> 2. turn = true -> 3. turn = false -> 4. turn = true -> ...
        // flip turn by typed "P"
        turn = !turn;
        // if turn off, we should reset random rooms to next to turn on/off
        if (!turn) {
            resetRandomRooms();
        }
        // turn on/off light in rooms
        for (Room room : randomRooms) {
            room.setTurnOn(turn);
            turnOnOrOffLightInRoom(room.getXWithLight(), room.getYWithLight(), room.getRoomNum(), room.getTurnOn());
        }
        return getWorldByWorldWrappers();
    }

    // reset random rooms to turn on/off
    private void resetRandomRooms() {
        // get total of rooms to turn on/off but not 0
        int turnTotal = RANDOM.nextInt(RoomNum + 1);
        while (turnTotal == 0) {
            turnTotal = RANDOM.nextInt(RoomNum + 1);
        }
        int randomRoomNum = RANDOM.nextInt(turnTotal);
        // randomly get roomNum to turn on/off but not same
        LinkedList<Integer> turnRoomNumbers = new LinkedList<>();
        while(turnRoomNumbers.size() != turnTotal) {
            if (!turnRoomNumbers.contains(randomRoomNum)) {
                turnRoomNumbers.add(randomRoomNum);
            }
            randomRoomNum = RANDOM.nextInt(turnTotal);
        }
        // set rooms to turn on/off
        randomRooms.clear();
        for (int roomNum : turnRoomNumbers) {
            randomRooms.add(rooms.get(roomNum));
        }
    }

    private void turnOnOrOffLightInRoom(int xWithLight, int yWithLight, int roomNum, boolean turn) {
        // if turn on, we should generate light with background(blue/yellow) in rooms
        // otherwise, we should generate light without background(blue/yellow) in rooms
        if (turn) {
            Tileset.generateLightWithBlue();
        } else {
            Tileset.generateLightWithoutBackground();
        }
        // length of side: 1, 3, 5, 7, 9, 11...
        for (int x = xWithLight, y = yWithLight, levelWithLight = 0, sideLength = 1;
             y < yWithLight + Tileset.levelWithLights;
             x -= 1, y += 1, levelWithLight += 1, sideLength += 2) {
            TETileWrapper tileWrapper;
            // bottom
            int level = sideLength - 1;
            for (int i = x; i < x + sideLength; i += 1) {
                setFloorToLightInRoom(i, y - level, roomNum, levelWithLight);
            }
            // middle
            level -= 1;
            while (level > 0){
                for (int i = x; i < x + sideLength; i += 1 ) {
                    if (i == x || i == x + sideLength - 1) {
                        setFloorToLightInRoom(i, y - level, roomNum, levelWithLight);
                    }
                }
                level -= 1;
            }
            // top, level = 0
            for (int i = x; i < x + sideLength; i += 1) {
                setFloorToLightInRoom(i, y - level, roomNum, levelWithLight);
            }
        }
    }

    // In room, tile must acquire some conditions below, then we can set the floor to the light
    // 1. tile is floor or light(light maybe not background, so we should use description())
    // 2. tile in room
    // 3. tile is not in around of room(i.e. not walls, door and four corners in room)
    // 4. tile has same roomNum(i.e. we must ensure tile in current room)
    private void setFloorToLightInRoom(int x, int y, int roomNum, int levelWithLight) {
        if (validTileInWorld(x, y)) {
            TETileWrapper tileWrapper = worldWrappers[x][y];
            if ((tileWrapper.getTile().equals(Tileset.FLOOR)
                    || (tileWrapper.getTile().description().equals("light")))
                    && !tileWrapper.getTile().equals(Tileset.AVATAR)
                    && tileWrapper.isRoom()
                    && !tileWrapper.getIsAround()
                    && tileWrapper.getRoomNum() == roomNum) {
                tileWrapper.setTile(Tileset.LIGHTS[levelWithLight]);
            }
        }
    }

    // it is valid tile in world?
    private boolean validTileInWorld(int x, int y) {
        return  x >= 0 && x < width && y >= 0 && y < height;
    }

    // set mark in worldWrapper
    private void setMarkInWorldWrappers(int v, boolean markedValue) {
        worldWrappers[toX(v)][toY(v)].markTile(markedValue);
    }

    // when we use toX(), toY() in the limbo that maybe lead to some *error*
    // you can see reason in the end

    /**
     * Returns x coordinate for given vertex.
     * For example if N = 10, and V = 12, returns 2.
     */
    private int toX(int v) {
        return v % N + 1;
    }

    /**
     * Returns y coordinate for given vertex.
     * For example if N = 10, and V = 12, returns 1.
     */
    private int toY(int v) {
        return v / N + 1;
    }

    /**
     * Returns one dimensional coordinate for vertex in position x, y.
     */
    private int xyTo1D(int x, int y) {
        return (y - 1) * N + (x - 1);
    }

    /**
     * Returns number of spaces in the maze.
     */
    private int V() {
        return N * N;
    }

    // 2022.10.26
    // the limbo has four side
    // left side: form [0, 0] to [0, height - 1]; right side: form [width - 1, 0] to [width - 1, height - 1];
    // bottom side: form [0, 0] to [width - 1, 0]; right side: form [0, height - 1] to [width - 1, height - 1];

    // when we use toX(), toY() in the limbo that maybe lead to some *error*
    // e.g. for [0, 11] in left side of limbo, then xyto1D(0, 11) = 799,
    // but toX(799) = 80(error!), toY(799) = 11
    // so, we use x and y but not v in buildHallwayByShortestPath(int x, int y)

//    public void testError() {
//        for (int y = 0; y < height; y += 1) {
//            if (worldWrappers[0][y].getTile().equals(Tileset.WALL) || worldWrappers[0][y].getTile().equals(Tileset.WATER)) {
//                System.out.println("YES");
//                System.out.println(worldWrappers[0][y].getY());
//            }
//        }
//        // left
//        System.out.println("left ");
//        int v = xyTo1D(0,11);
//        System.out.println("o " + toX(v) + " " + toY(v));
//        System.out.println("c " + worldWrappers[0][11].getX() + " " + worldWrappers[0][11].getY());
//        v = xyTo1D(0,10);
//        System.out.println("o " + toX(v) + " " + toY(v));
//        System.out.println("c " + worldWrappers[0][11].getX() + " " + worldWrappers[0][11].getY());
//        // right
//        System.out.println("right ");
//        v = xyTo1D(79,11);
//        System.out.println("o " + toX(v) + " " + toY(v));
//        System.out.println("c " + worldWrappers[0][11].getX() + " " + worldWrappers[0][11].getY());
//        v = xyTo1D(79,10);
//        System.out.println("o " + toX(v) + " " + toY(v));
//        System.out.println("c " + worldWrappers[0][11].getX() + " " + worldWrappers[0][11].getY());
//        // top
//        System.out.println("top");
//        v = xyTo1D(11,29);
//        System.out.println("o " + toX(v) + " " + toY(v));
//        System.out.println("c " + worldWrappers[0][11].getX() + " " + worldWrappers[0][11].getY());
//        v = xyTo1D(10,29);
//        System.out.println("o " + toX(v) + " " + toY(v));
//        System.out.println("c " + worldWrappers[0][11].getX() + " " + worldWrappers[0][11].getY());
//        // bottom
//        System.out.println("bottom ");
//        v = xyTo1D(11,0);
//        System.out.println("o " + toX(v) + " " + toY(v));
//        System.out.println("c " + worldWrappers[0][11].getX() + " " + worldWrappers[0][11].getY());
//        v = xyTo1D(10,0);
//        System.out.println("o " + toX(v) + " " + toY(v));
//        System.out.println("c " + worldWrappers[0][11].getX() + " " + worldWrappers[0][11].getY());
//        worldWrappers[1][11].setTile(Tileset.WATER);
//    }

    // 2022.10.26
    // old version with "direct hallway" but new version with Astar not has this bug
    // Different rooms and different hallways will across in my world
    // so my world will have a bug:
    // A room maybe has many doors(a floor in side of room to connect hallway)
    // then, if more rooms(RoomNum bigger), the rooms and hallways will not more distinct in my world

    // dfs but find many paths, so we can't use it
//    private void dfs(int v, int parent) {
//        setMarkInWorldWrappers(v);
//        if (v == target) {
//            System.out.println("yes");
//            targetFound = true;
//        }
//
//        if (targetFound) {
//            return;
//        }
//
//        for (TETileWrapper tileWrapper : tileNeighbors(v)) {
//            if (!tileWrapper.isMarked()) {
//                int w = xyTo1D(tileWrapper.getX(), tileWrapper.getY());
//                dfs(w, v);
//                if (targetFound) {
//                    return;
//                }
//            }
//        }
//    }

}
