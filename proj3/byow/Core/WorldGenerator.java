package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.TETileWrapper;
import byow.TileEngine.Tileset;

import java.util.ArrayDeque;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;


public class WorldGenerator {
    private static final int RoomNum = 16;  // the number of room
    private final int N;                   // dimension of worldWrappers(we assume worldWrappers is N*N)
    private static Random RANDOM;
    private static Long seed;

    // set the worldWrappers
    private final TETileWrapper[][] worldWrappers;
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

    public WorldGenerator(Long seed, TETile[][] world, boolean alwaysCenterTarget) {
        WorldGenerator.seed = seed;
        WorldGenerator.RANDOM = new Random(seed);

        this.height = world[0].length;
        this.width = world.length;
        this.N = Math.max(width, height);
        this.edgeTo = new int[V()];
        this.distTo = new int[V()];
        this.targetFound = false;

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
        ConnectRooms();
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

    private void ConnectRooms() {
        for (int i = 0; i < RoomNum; i += 1){
            Room room = new Room(worldWrappers, seed);
            room.makeRoom();
            connectRoomToTarget(room);
            reset();
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
        TETileWrapper randomExit = room.getRandomExitByDoors();
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
            while (!targetFound) {
                setRandomTargetAndSource(room);
                astar();
            }
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
                   // you can't mark target by markTile()!!!
                   worldWrappers[x][y].setTile(Tileset.FLOOR);
                   isFirst = false;
                   // set first source
                   TETileWrapper randomExit = room.getRandomExitByDoors();
                   setSource(randomExit.getX(), randomExit.getY());
                   return;
               }
               num += 1;
            }
        }
    }

    private void setRandomTargetAndSource(Room room){
        LinkedList<TETileWrapper> NotRoomButFloors = notRoomButFloors();
        LinkedList<TETileWrapper> exits = room.getExits();
        int randomNum1, randomNum2;
        // set random target
        randomNum1 = RANDOM.nextInt(NotRoomButFloors.size());
        TETileWrapper tileWrapper = NotRoomButFloors.get(randomNum1);
        setTarget(tileWrapper.getX(), tileWrapper.getY());
        // set random source
        randomNum2 = RANDOM.nextInt(exits.size());
        TETileWrapper randomExit = exits.get(randomNum2);
        int x = randomExit.getX();
        int y = randomExit.getY();
        setSource(x, y);
        // set door in room as floor by exit
        room.setDoorAsFloorByExit(randomExit.getX(), randomExit.getY());
    }

    private LinkedList<TETileWrapper> notRoomButFloors() {
        LinkedList<TETileWrapper> NotRoomButFloors = new LinkedList<>();
        for (int x = 1; x < width - 1; x += 1) {
            for (int y = 1; y < height - 1; y += 1 ) {
                if (!worldWrappers[x][y].isRoom() && worldWrappers[x][y].getTile().equals(Tileset.FLOOR)) {
                    NotRoomButFloors.add(worldWrappers[x][y]);
                }
            }
        }
        return NotRoomButFloors;
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

    private void setSource(int sourceX, int sourceY) {
        this.source = xyTo1D(sourceX, sourceY);
    }

    private void setTarget(int x, int y) {
        this.target = xyTo1D(x, y);
    }

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
