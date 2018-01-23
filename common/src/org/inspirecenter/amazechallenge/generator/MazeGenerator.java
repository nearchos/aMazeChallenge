package org.inspirecenter.amazechallenge.generator;

import org.inspirecenter.amazechallenge.model.Position;

import java.util.Collections;
import java.util.Vector;

public class MazeGenerator {

    private static final int MIN_MAZE_SIZE = 5;
    private static final int MAX_MAZE_SIZE = 30;

    public static String generate(int gridSize, final org.inspirecenter.amazechallenge.model.Position startingPosition, final org.inspirecenter.amazechallenge.model.Position targetPosition) {

        if(gridSize < MIN_MAZE_SIZE) gridSize = MIN_MAZE_SIZE;
        if(gridSize > MAX_MAZE_SIZE) gridSize = MAX_MAZE_SIZE;

        // generate
        Vector<Path> mazePaths = generateRandomMaze(startingPosition, targetPosition, gridSize);

        return getGrid(mazePaths, gridSize);
    }

    // zeroes
    public static final int SHAPE_EMPTY             = 0x0; //

    // todo replace with Grid.SHAPE_ONLY_UPPER_SIDE etc.
    // ones
    public static final int SHAPE_ONLY_UPPER_SIDE   = 0x1; // -
    public static final int SHAPE_ONLY_LOWER_SIDE   = 0x2; // _
    public static final int SHAPE_ONLY_LEFT_SIDE    = 0x4; // |
    public static final int SHAPE_ONLY_RIGHT_SIDE   = 0x8; //  |

    private static String getGrid(Vector<Path> paths, final int gridSize) {
        // calculate grid based on paths
        final StringBuilder data = new StringBuilder();
        for(int row = 0; row < gridSize; row++) {
            for(int col = 0; col < gridSize; col++) {
                final org.inspirecenter.amazechallenge.model.Position position = new org.inspirecenter.amazechallenge.model.Position(row, col);

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

    private static Vector<Path> generateRandomMaze(final org.inspirecenter.amazechallenge.model.Position sourcePosition, final org.inspirecenter.amazechallenge.model.Position targetPosition, final int gridSize) {

        final Vector<Path> allPaths = new Vector<>();

        final BooleanGrid grid = new BooleanGrid(gridSize);

        // first generate the exit path
        {
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

    private static Path generateRandomPath(final org.inspirecenter.amazechallenge.model.Position fromPosition, final org.inspirecenter.amazechallenge.model.Position toPosition, final int gridSize) {
        final Path path = new Path();
        while (path.isEmpty()) {
            path.addNextPoint(fromPosition);
            while(!Path.areIdentical(path.getTargetPosition(), toPosition)) { // repeat until the last added position matches the toPosition
                final org.inspirecenter.amazechallenge.model.Position currentPosition = path.getTargetPosition();
                final Vector<org.inspirecenter.amazechallenge.model.Position> shuffledAdjacentPositions = getShuffledAdjacentPositions(currentPosition, gridSize);
                boolean added = false;
                for(final org.inspirecenter.amazechallenge.model.Position position : shuffledAdjacentPositions) {
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