package HaliteParty;

import java.util.Random;

public enum Direction {
    STILL, NORTH, EAST, SOUTH, WEST;

    public static final Direction[] DIRECTIONS = new Direction[]{STILL, NORTH, EAST, SOUTH, WEST};
    public static final Direction[] CARDINALS = new Direction[]{NORTH, EAST, SOUTH, WEST};

    public static Direction randomDirection() {
        Direction[] values = values();
        return values[new Random().nextInt(values.length)];
    }

    public static Direction opposite(Direction direction){
        switch (direction){
            case SOUTH: return NORTH;
            case NORTH: return SOUTH;
            case EAST: return WEST;
            case WEST: return EAST;
            default: return STILL;
        }
    }

}
