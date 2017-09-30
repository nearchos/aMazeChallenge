package org.inspirecenter.amazechallenge.model;

import java.io.Serializable;

/**
 * @author Nearchos
 *         Created: 15-Aug-17
 */

public class Position implements Serializable {

    private final int row;
    private final int col;

    Position(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    Position moveNorth() {
        return new Position(row - 1, col);
    }

    Position moveSouth() {
        return new Position(row + 1, col);
    }

    Position moveWest() {
        return new Position(row, col - 1);
    }

    Position moveEast() {
        return new Position(row, col + 1);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Position position = (Position) o;

        if (row != position.row) return false;
        return col == position.col;

    }

    @Override
    public int hashCode() {
        int result = row;
        result = 31 * result + col;
        return result;
    }

    @Override
    public String toString() {
        return "Position{row=" + row + ", col=" + col + "}";
    }
}
