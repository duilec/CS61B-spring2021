package byow.TileEngine;

public class TETileWrapper {
    // Tile type
    private TETile tile;
    // booleanValue
    private boolean marked;
    private boolean isRoom;
    private boolean isHallway;
    // x, y
    private int x;
    private int y;

    public TETileWrapper(TETile tile, int x, int y) {
        this.tile = tile;
        this.x = x;
        this.y = y;
        this.marked = false;
        this.isRoom = false;
        this.isHallway = false;
    }

    public void setTile(TETile tile) {
        this.tile = tile;
    }

    public TETile getTile() {
        return tile;
    }

    // mark a tile
    public void markTile(boolean markedValue) {
        marked = markedValue;
    }
    // the tile be marked in world?
    public boolean isMarked() {
        return marked;
    }

    // mark a room
    public void markRoom() {
        isRoom = true;
    }
    // the tile is room in world?
    public boolean isRoom() {
        return isRoom;
    }

    // set x
    public void setX(Integer x) {
        this.x = x;
    }
    // get x
    public int getX() {
        return x;
    }

    // set y
    public void setY(Integer y) {
        this.y = y;
    }
    // get y
    public int getY() {
        return y;
    }
}
