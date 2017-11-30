package org.inspirecenter.amazechallenge.data;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Parent;
import org.inspirecenter.amazechallenge.model.Position;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Grid {

    @Parent Key<Challenge> challengeKey;
    @Id public Long id;
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

    public long getId() {
        return id;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getGridCell(final int posX, final int posY) throws IndexOutOfBoundsException {
        if(posX < 0 || posX > width) throw new IndexOutOfBoundsException("posX not in bounds [0, " + width + ")");
        if(posY < 0 || posY > height) throw new IndexOutOfBoundsException("posY not in bounds [0, " + height + ")");
        return grid.get(posY * width + posX);
    }

    public String getData() {
        final StringBuilder stringBuilder = new StringBuilder();
        for(int col = 0; col < height; col++) {
            for(int row = 0; row < width; row++) {
                stringBuilder.append(String.format("%1x", getGridCell(row, col)));
            }
        }
        return stringBuilder.toString();
    }

    public Position getStartingPosition() {
        return startingPosition;
    }

    public Position getTargetPosition() {
        return targetPosition;
    }

    private static List<Integer> convertGridAsHexToInts(final String gridAsHex, final int width, final int height) {
        assert gridAsHex.length() == width * height;
        final List<Integer> grid = new ArrayList<>(width * height);
        for(int i = 0; i < width * height; i++) {
            final char c = gridAsHex.charAt(i);
            grid.add(Integer.parseInt(Character.toString(c), 16));
        }
        return grid;
    }

    @Override
    public String toString() {
        return width + "x" + height;
    }
}