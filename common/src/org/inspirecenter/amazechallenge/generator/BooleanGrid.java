package org.inspirecenter.amazechallenge.generator;

import org.inspirecenter.amazechallenge.model.Position;

import java.util.Random;

class BooleanGrid {

    private final int size;
    private final boolean [][] grid; // [col][row]

    BooleanGrid(final int size) {
        this.size = size;
        this.grid = new boolean[size][size]; // initially all are false
    }

    /**
     * Sets the grid to 'true' for the points on the given path
     * @param path contains the points to be 'set'
     */
    void setGrid(final Path path) {
        for(int i = 0; i < path.getSize(); i++) {
            final Position position = path.getPosition(i);
            grid[position.getCol()][position.getRow()] = true;
        }
    }

    /**
     * Returns true iff all cells are set (i.e. true).
     * @return true iff all cells are set (i.e. true)
     */
    boolean isFull() {
        for(int c = 0; c < size; c++) {
            for(int r = 0; r < size; r++) {
                if(!grid[c][r]) return false;
            }
        }

        return true;
    }

    /**
     * Returns true iff all cells are unset (i.e. false).
     * @return true iff all cells are unset (i.e. false)
     */
    boolean isEmpty() {
        for(int c = 0; c < size; c++) {
            for(int r = 0; r < size; r++) {
                if(grid[c][r]) return false;
            }
        }

        return true;
    }

    Position randomUnsetPosition() {
        if(isFull()) throw new RuntimeException("Grid is already full!");

        final Random random = new Random();
        while(true) {
            final int randomRow = random.nextInt(size);
            final int randomCol = random.nextInt(size);
            if(!grid[randomCol][randomRow]) return new Position(randomRow, randomCol);
        }
    }

    Position randomSetPosition() {
        if(isEmpty()) throw new RuntimeException("Grid is empty!");

        final Random random = new Random();
        while(true) {
            final int randomRow = random.nextInt(size);
            final int randomCol = random.nextInt(size);
            if(grid[randomCol][randomRow]) return new Position(randomRow, randomCol);
        }
    }
}