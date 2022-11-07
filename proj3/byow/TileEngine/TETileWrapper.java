package byow.TileEngine;

import java.io.Serializable;

public class TETileWrapper implements Serializable {
    // Tile type
    private TETile tile;
    // booleanValue
    private boolean marked;
    private boolean isRoom;
    // x, y
    private int x;
    private int y;

    // number of room
    private int roomNum;

    // it is around(i.e. four corners, walls and door) of room?
    // the floor is not around in room
    private boolean isAround = false;

    public TETileWrapper(TETile tile, int x, int y) {
        this.tile = tile;
        this.x = x;
        this.y = y;
        this.marked = false;
        this.isRoom = false;
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

    // set number of room
    public void setRoomNum(int roomNum) {
        this.roomNum = roomNum;
    }
    // get number of room
    public int getRoomNum() {
        return roomNum;
    }

    // set isAround
    public void setIsAround() {
        this.isAround = true;
    }
    // get isAround
    public boolean getIsAround() {
        return isAround;
    }
}
