package org.inspirecenter.amazechallenge.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.Serializable;
import java.util.Scanner;

/**
 * @author Nearchos
 *         Created: 14-Aug-17
 */
public class Grid implements Serializable { // consider renaming the class to Grid to accommodate more general grids (instead of just mazes)

    public static final int SHAPE_ONLY_UPPER_SIDE = 0x1; // -
    public static final int SHAPE_ONLY_LOWER_SIDE = 0x2; // _
    public static final int SHAPE_ONLY_LEFT_SIDE  = 0x4; // |
    public static final int SHAPE_ONLY_RIGHT_SIDE = 0x8; //  |

    private final int width;
    private final int height;
    private final int[][] grid;
    private Position startingPosition;
    private Position targetPosition;

    public Grid(final int width, final int height, final int[][] grid, final Position startingPosition, final Position targetPosition) {
        this.width = width;
        this.height = height;
        this.grid = grid;
        this.startingPosition = startingPosition;
        this.targetPosition = targetPosition;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    int get(final int row, final int col) {
        return grid[row][col];
    }

    public Position getStartingPosition() {
        return startingPosition;
    }

    public Position getTargetPosition() {
        return targetPosition;
    }

    boolean hasWall(final Position position, final Direction direction) {
        final int shape = get(position.getRow(), position.getCol());
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

    public static Grid parseJSON(final JSONObject jsonObject) throws JSONException {
        final int width = jsonObject.getInt("width");
        final int height = jsonObject.getInt("height");
        final int[][] grid = new int[width][height];
        final String data = jsonObject.getString("data");
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                grid[row][col] = Integer.parseInt(Character.toString(data.charAt(row * width + col)), 16);
            }
        }
        final Position startingPosition = Position.parseJSON(jsonObject.getJSONObject("startingPosition"));
        final Position targetPosition = Position.parseJSON(jsonObject.getJSONObject("targetPosition"));
        return new Grid(width, height, grid, startingPosition, targetPosition);
    }
}