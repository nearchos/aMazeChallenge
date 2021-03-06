package org.inspirecenter.amazechallenge.model;

import java.io.Serializable;

/**
 * @author Nearchos
 *         Created: 14-Aug-17
 */
public class Grid implements Serializable {

    private static final Direction DEFAULT_STARTING_DIRECTION = Direction.NORTH;

    public static final int SHAPE_ONLY_UPPER_SIDE = 0x1; // -
    public static final int SHAPE_ONLY_LOWER_SIDE = 0x2; // _
    public static final int SHAPE_ONLY_LEFT_SIDE  = 0x4; // |
    public static final int SHAPE_ONLY_RIGHT_SIDE = 0x8; //  |

    private int width;
    private int height;
    private String data; // all the cells, from top-left, rightwards and then next line, etc. until bottom-right (in hex)
    private Position startingPosition;
    private Position targetPosition;
    private Direction startingDirection = DEFAULT_STARTING_DIRECTION;

    public Grid() {
        super();
    }

    public Grid(int width, int height, String data, Position startingPosition, Position targetPosition) {
        this();
        this.width = width;
        this.height = height;
        this.data = data;
        this.startingPosition = startingPosition;
        this.targetPosition = targetPosition;
    }

    public Grid(int width, int height, String gridAsHex, int startingPositionX, int startingPositionY, int targetPositionX, int targetPositionY) {
        this(width, height, gridAsHex, new Position(startingPositionY, startingPositionX), new Position(targetPositionY, targetPositionX));
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public String getData() {
        return data;
    }

    public Position getStartingPosition() {
        return startingPosition;
    }

    public Direction getStartingDirection() {
        return startingDirection;
    }

    public Position getTargetPosition() {
        return targetPosition;
    }
}