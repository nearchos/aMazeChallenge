package org.inspirecenter.amazechallenge.model;

/**
 * @author Nearchos
 *         Created: 16-Aug-17
 */

public enum Direction {

    NORTH("north"),
    SOUTH("south"),
    WEST("west"),
    EAST("east");

    private final String direction;

    Direction(final String direction) {
        this.direction = direction;
    }

    public Direction turnClockwise() {
        switch (this) {
            case NORTH:
                return EAST;
            case SOUTH:
                return WEST;
            case WEST:
                return NORTH;
            case EAST:
                return SOUTH;
            default:
                throw new RuntimeException("Invalid direction: " + this);
        }
    }

    public Direction turnCounterClockwise() {
        switch (this) {
            case NORTH:
                return WEST;
            case SOUTH:
                return EAST;
            case WEST:
                return SOUTH;
            case EAST:
                return NORTH;
            default:
                throw new RuntimeException("Invalid direction: " + this);
        }
    }

    public Direction opposite() {
        switch (this) {
            case NORTH:
                return SOUTH;
            case SOUTH:
                return NORTH;
            case WEST:
                return EAST;
            case EAST:
                return WEST;
            default:
                throw new RuntimeException("Invalid direction: " + this);
        }
    }

    @Override
    public String toString() {
        return direction;
    }
}