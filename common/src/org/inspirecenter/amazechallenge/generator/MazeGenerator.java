package org.inspirecenter.amazechallenge.generator;

import org.inspirecenter.amazechallenge.model.Position;

import java.util.Collections;
import java.util.Vector;

import static org.inspirecenter.amazechallenge.model.Grid.*;

public class MazeGenerator {

    private static final int MIN_MAZE_SIZE = 5;
    private static final int MAX_MAZE_SIZE = 30;

    public enum Algorithm {

        SINGLE_SOLUTION("single", "Single Solution"),
        MANY_SOLUTIONS("many", "Many Solutions"),
        SPARSE("sparse", "Sparse"),
        EMPTY("empty", "Empty");

        private final String name;
        private final String id;

        Algorithm(String id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }

        public String getID() { return id; }

        public static Algorithm fromID(String id) {
            for (final Algorithm a : values()) {
                if (a.id.equals(id)) return a;
            }
            throw new RuntimeException("The algorithm id found \"" + id + "\" is not a valid algorithm.");
        }

        public static int getPosition(Algorithm algorithm) {
            for (int i = 0; i < values().length; i++) {
                if (algorithm.getID().equals(values()[i].getID())) return i;
            }
            throw new RuntimeException("The algorithm id found \"" + algorithm.id + "\" is not a valid algorithm.");
        }

    }

    public static String generate(Algorithm algorithm, int gridSize, final Position startingPosition, final Position targetPosition) {

        if(gridSize < MIN_MAZE_SIZE) gridSize = MIN_MAZE_SIZE;
        if(gridSize > MAX_MAZE_SIZE) gridSize = MAX_MAZE_SIZE;

        switch (algorithm) {
            case SINGLE_SOLUTION:
                return generateSingleSolution(gridSize, startingPosition, targetPosition);
            case MANY_SOLUTIONS:
                return generateManySolutions(gridSize, startingPosition, targetPosition);
            case SPARSE:
                return generateSparse(gridSize, startingPosition, targetPosition);
            case EMPTY:
                return generateEmpty(gridSize);
            default:
                return generateSingleSolution(gridSize, startingPosition, targetPosition);
        }
    }

    private static String generateSingleSolution(int gridSize, final Position startingPosition, final Position targetPosition) {

        // generate
        Vector<Path> mazePaths = generateRandomMaze(1, startingPosition, targetPosition, gridSize);

        return getGrid(mazePaths, gridSize);
    }

    private static String generateManySolutions(int gridSize, final Position startingPosition, final Position targetPosition) {

        // generate
        final int numOfSolutions = gridSize / 2;
        Vector<Path> mazePaths = generateRandomMaze(numOfSolutions, startingPosition, targetPosition, gridSize);

        return getGrid(mazePaths, gridSize);
    }

    private static String generateSparse(int gridSize, final Position startingPosition, final Position targetPosition) {
        // generate
        Vector<Path> mazePaths = generateRandomMazeWithLoops(startingPosition, targetPosition, gridSize);

        return getGrid(mazePaths, gridSize);
    }

    private static String generateEmpty(int gridSize) {
        // calculate grid with only boundary walls
        final StringBuilder data = new StringBuilder();
        for(int row = 0; row < gridSize; row++) {
            for(int col = 0; col < gridSize; col++) {
                final Position position = new Position(row, col);

                final boolean hasWallToLeft = col == 0;
                final boolean hasWallToRight = col == gridSize - 1;
                final boolean hasWallToUp = row == 0;
                final boolean hasWallToDown= row == gridSize - 1;

                int shape = 0x0;

                if(hasWallToLeft)  shape |= SHAPE_ONLY_LEFT_SIDE;
                if(hasWallToRight) shape |= SHAPE_ONLY_RIGHT_SIDE;
                if(hasWallToUp)    shape |= SHAPE_ONLY_UPPER_SIDE;
                if(hasWallToDown)  shape |= SHAPE_ONLY_LOWER_SIDE;

                data.append(Integer.toHexString(shape));
            }
        }
        return data.toString();
    }

    private static String getGrid(Vector<Path> paths, final int gridSize) {
        // calculate grid based on paths
        final StringBuilder data = new StringBuilder();
        for(int row = 0; row < gridSize; row++) {
            for(int col = 0; col < gridSize; col++) {
                final Position position = new Position(row, col);

                final boolean existsPathToLeft = Path.checkPathLeft(position, paths);
                final boolean existsPathToRight = Path.checkPathRight(position, paths, gridSize);
                final boolean existsPathToUp = Path.checkPathUp(position, paths);
                final boolean existsPathToDown= Path.checkPathDown(position, paths, gridSize);

                int shape = 0xF;

                if(existsPathToLeft) shape &= ~SHAPE_ONLY_LEFT_SIDE;
                if(existsPathToRight) shape &= ~SHAPE_ONLY_RIGHT_SIDE;
                if(existsPathToUp) shape &= ~SHAPE_ONLY_UPPER_SIDE;
                if(existsPathToDown) shape &= ~SHAPE_ONLY_LOWER_SIDE;

                data.append(Integer.toHexString(shape));
            }
        }
        return data.toString();
    }

    private static Vector<Path> generateRandomMazeWithLoops(final Position sourcePosition, final Position targetPosition, final int gridSize) {

        final Vector<Path> allPaths = new Vector<>();

        final BooleanGrid grid = new BooleanGrid(gridSize);

        // first generate the exit path
        {
            final Path path = generateRandomPath(sourcePosition, targetPosition, gridSize);
            allPaths.add(path);
            grid.setGrid(path);
        }

        // then add more paths--possibly with loops--until all cells are 'busy'
        while(!grid.isFull()) {
            final Position position = grid.randomSetPosition();
            final Path path = generateRandomPathWithLoops(position, allPaths, gridSize);
            allPaths.add(path);
            grid.setGrid(path);
        }

        return allPaths;
    }

    private static Vector<Path> generateRandomMaze(final int numOfSolutions, final Position sourcePosition, final Position targetPosition, final int gridSize) {

        final Vector<Path> allPaths = new Vector<>();

        final BooleanGrid grid = new BooleanGrid(gridSize);

        // first generate the exit paths
        for(int i = 0; i < numOfSolutions; i++) {
            final Path path = generateRandomPath(sourcePosition, targetPosition, gridSize);
            allPaths.add(path);
            grid.setGrid(path);
        }

        // then add more paths until all cells are 'busy'
        while(!grid.isFull()) {
            final Position position = grid.randomUnsetPosition();
            final Path path = generateRandomPath(position, allPaths, gridSize);
            allPaths.add(path);
            grid.setGrid(path);
        }

        return allPaths;
    }

    private static Path generateRandomPath(final Position startingPosition, final Vector<Path> allPaths, final int gridSize) {
        final Path path = new Path();
        while (path.isEmpty()) {
            path.addNextPoint(startingPosition);
            while(true) {
                final Position currentPosition = path.getTargetPosition();
                final Vector<Position> shuffledAdjacentPositions = getShuffledAdjacentPositions(currentPosition, gridSize);
                boolean added = false;
                for(final Position position : shuffledAdjacentPositions) {
                    if(!Path.positionCrossesPath(position, path)) {
                        path.addNextPoint(position);
                        added = true;
                        break;
                    }
                }
                if(!added) { // no adjacent point - we need to restart
                    path.clear();
                    System.out.println("Failed! Trying again...");
                    break;
                }
                if(Path.positionCrossesAnyPath(path.getTargetPosition(), allPaths)) {
                    // we are done!
                    break;
                }
            }
        }
        return path;
    }

    private static Path generateRandomPathWithLoops(final Position startingPosition, final Vector<Path> allPaths, final int gridSize) {
        final Path path = new Path();
        while (path.isEmpty()) {
            path.addNextPoint(startingPosition);
            while(true) {
                final Position currentPosition = path.getTargetPosition();
                final Vector<Position> shuffledAdjacentPositions = getShuffledAdjacentPositions(currentPosition, gridSize);
                boolean added = false;
                for(final Position position : shuffledAdjacentPositions) {
                    if(Path.positionCrossesPath(position, path)) {

                    } else {
                        path.addNextPoint(position);
                        added = true;
                        break;
                    }
                }
                if(!added) { // no adjacent point - we need to restart
                    path.clear();
                    System.out.println("Failed! Trying again...");
                    break;
                }
                if(Path.positionCrossesAnyPath(path.getTargetPosition(), allPaths)) {
                    // we are done!
                    break;
                }
            }
        }
        return path;
    }

    private static Path generateRandomPath(final Position fromPosition, final Position toPosition, final int gridSize) {
        final Path path = new Path();
        while (path.isEmpty()) {
            path.addNextPoint(fromPosition);
            while(!Path.areIdentical(path.getTargetPosition(), toPosition)) { // repeat until the last added position matches the toPosition
                final Position currentPosition = path.getTargetPosition();
                final Vector<Position> shuffledAdjacentPositions = getShuffledAdjacentPositions(currentPosition, gridSize);
                boolean added = false;
                for(final Position position : shuffledAdjacentPositions) {
                    if(!Path.positionCrossesPath(position, path)) {
                        path.addNextPoint(position);
                        added = true;
                        break;
                    }
                }
                if(!added) { // no adjacent point - we need to restart
                    path.clear();
//                    System.out.println("Failed! Trying again...");
                    break;
                }
            }
        }
        return path;
    }

    private static Vector<Position> getShuffledAdjacentPositions(final Position fromPosition, final int gridSize) {
        final Vector<Position> randomAdjacentPositions = new Vector<>();
        final int col = fromPosition.getCol();
        final int row = fromPosition.getRow();
        if(col > 0) randomAdjacentPositions.add(new Position(row, col - 1));
        if(row > 0) randomAdjacentPositions.add(new Position(row  - 1, col));
        if(col < gridSize-1) randomAdjacentPositions.add(new Position(row, col + 1));
        if(row < gridSize-1) randomAdjacentPositions.add(new Position(row + 1, col));
        Collections.shuffle(randomAdjacentPositions); // shuffle

        return randomAdjacentPositions;
    }
}