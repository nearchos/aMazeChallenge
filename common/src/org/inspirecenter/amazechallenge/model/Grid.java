package org.inspirecenter.amazechallenge.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Nearchos
 *         Created: 14-Aug-17
 */
@com.googlecode.objectify.annotation.Entity
public class Grid implements Serializable { // consider renaming the class to Grid to accommodate more general grids (instead of just mazes)

    public static final int SHAPE_ONLY_UPPER_SIDE = 0x1; // -
    public static final int SHAPE_ONLY_LOWER_SIDE = 0x2; // _
    public static final int SHAPE_ONLY_LEFT_SIDE  = 0x4; // |
    public static final int SHAPE_ONLY_RIGHT_SIDE = 0x8; //  |

    @com.googlecode.objectify.annotation.Id
    public Long id;

    private int width;
    private int height;
    private List<Integer> grid; // all the cells, from top-left, rightwards and then next line, etc. until bottom-right
    private Position startingPosition;
    private Position targetPosition;

    public Grid() {
        super();
    }

    public Grid(int width, int height, List<Integer> grid, Position startingPosition, Position targetPosition) {
        this();
        this.width = width;
        this.height = height;
        this.grid = grid;
        this.startingPosition = startingPosition;
        this.targetPosition = targetPosition;
    }

    public Grid(int width, int height, String gridAsHex, int startingPositionX, int startingPositionY, int targetPositionX, int targetPositionY) {
        this(width, height, convertGridAsHexToInts(gridAsHex, width, height), new Position(startingPositionX, startingPositionY), new Position(targetPositionX, targetPositionY));
    }

    public Grid(int width, int height, String gridAsHex, Position startingPosition, Position targetPosition) {
        this(width, height, convertGridAsHexToInts(gridAsHex, width, height), startingPosition, targetPosition);
    }

    public Long getId() {
        return id;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getGridCell(final int row, final int col) throws IndexOutOfBoundsException {
        if(col < 0 || col > width) throw new IndexOutOfBoundsException("col not in bounds [0, " + width + ")");
        if(row < 0 || row > height) throw new IndexOutOfBoundsException("row not in bounds [0, " + height + ")");
        return grid.get(row * width + col);
    }

    public Position getStartingPosition() {
        return startingPosition;
    }

    public Position getTargetPosition() {
        return targetPosition;
    }

    boolean hasWall(final Position position, final Direction direction) {
        final int shape = getGridCell(position.getRow(), position.getCol());
        switch (direction) {
            case NORTH:
                return (shape & SHAPE_ONLY_UPPER_SIDE) != 0;
            case SOUTH:
                return (shape & SHAPE_ONLY_LOWER_SIDE) != 0;
            case WEST:
                return (shape & SHAPE_ONLY_LEFT_SIDE) != 0;
            case EAST:
                return (shape & SHAPE_ONLY_RIGHT_SIDE) != 0;
            default:
                throw new RuntimeException("Invalid direction: " + direction);
        }
    }

    @Override
    public String toString() {
        return "Grid " + width + "x" + height;
    }

    private static List<Integer> convertGridAsHexToInts(final String gridAsHex, final int width, final int height) {
        final List<Integer> grid = new ArrayList<>(width * height);
        for(int i = 0; i < width * height; i++) {
            final char c = gridAsHex.charAt(i);
            grid.add(Integer.parseInt(Character.toString(c), 16));
        }
        return grid;
    }
}