package org.inspirecenter.amazechallenge.controller;

import org.inspirecenter.amazechallenge.algorithms.MazeSolver;
import org.inspirecenter.amazechallenge.algorithms.PlayerMove;
import org.inspirecenter.amazechallenge.model.*;

import java.util.Map;
import java.util.Random;

import static org.inspirecenter.amazechallenge.model.Grid.SHAPE_ONLY_LEFT_SIDE;
import static org.inspirecenter.amazechallenge.model.Grid.SHAPE_ONLY_LOWER_SIDE;
import static org.inspirecenter.amazechallenge.model.Grid.SHAPE_ONLY_RIGHT_SIDE;
import static org.inspirecenter.amazechallenge.model.Grid.SHAPE_ONLY_UPPER_SIDE;

/**
 * @author Nearchos
 *         Created: 11-Dec-17
 */

public class RuntimeController {

    public static void makeMove(final Challenge challenge, final Game game, final Map<String,MazeSolver> playerEmailToMazeSolvers) {
        final Grid grid = challenge.getGrid();

        // then apply next move to active players
        for (final String playerEmail : game.getActivePlayers()) {
            final PlayerPositionAndDirection playerPositionAndDirection = game.getPlayerPositionAndDirection(playerEmail);
            final Player player = game.getPlayer(playerEmail);
            final MazeSolver mazeSolver = playerEmailToMazeSolvers.get(playerEmail);
            final PlayerMove nextPlayerMove = mazeSolver == null ? PlayerMove.NO_MOVE : mazeSolver.getNextMove(game);
            applyPlayerMove(grid, game, playerEmail, playerPositionAndDirection, nextPlayerMove);

            //TODO REMOVE:
            player.getHealth().decreaseBy(5);
//            System.out.println("Health: " + player.getHealth().get());

            //Check the player's health:
            if (player.getHealth().isAtMin()) {
                game.resetPlayer(playerEmail);
                game.setPlayerPositionAndDirection(playerEmail, new PlayerPositionAndDirection(grid.getStartingPosition(), Direction.NORTH));
            }

            // todo add obstacles/rewards
            if(game.getNumOfObstacles(ObstacleType.OBSTACLE_HEALTH) < challenge.getMaxObstaclesHealth()) {
                final Random random = new Random();
                int row = random.nextInt(grid.getHeight());
                int col = random.nextInt(grid.getWidth());
                final Position position = new Position(row, col);
                game.addObstacle(new Obstacle(position, ObstacleType.OBSTACLE_HEALTH));
            }
        }
    }

    private static void applyPlayerMove(final Grid grid, final Game game, final String playerEmail, final PlayerPositionAndDirection playerPositionAndDirection, final PlayerMove playerMove) {
        Direction direction = playerPositionAndDirection.getDirection();
        Position position = playerPositionAndDirection.getPosition();
        switch (playerMove) {
            case TURN_CLOCKWISE:
                direction = playerPositionAndDirection.getDirection().turnClockwise();
                break;
            case TURN_COUNTERCLOCKWISE:
                direction = direction.turnCounterClockwise();
                break;
            case MOVE_FORWARD:
                if (canMoveForward(grid, playerPositionAndDirection.getPosition(), playerPositionAndDirection.getDirection())) {
                    position = movePlayerForward(playerPositionAndDirection);
                    // handle obstacles and rewards (i.e. add/substract health etc.)
                    //todo
                    for(final Obstacle obstacle : game.getObstacles()) {
                        if(obstacle.getPosition().equals(position)) {
                            // todo
                        }
                    }
                }
                break;
            case NO_MOVE:
                // Log.d("grid-challenge", "move: " + playerMove);
                break;
            default:
                throw new RuntimeException("Invalid PlayerMove: " + playerMove);
        }
        final PlayerPositionAndDirection updatedPlayerPositionAndDirection = new PlayerPositionAndDirection(position, direction);
        game.setPlayerPositionAndDirection(playerEmail, updatedPlayerPositionAndDirection);
    }

    public static boolean hasSomeoneReachedTheTargetPosition(final Game game, final Grid grid) {
        final Position targetPosition = grid.getTargetPosition();
        boolean someoneHasReachedTheTargetPosition = false;
        for (final String playerEmail : game.getActivePlayers()) {
            final PlayerPositionAndDirection playerPositionAndDirection = game.getPlayerPositionAndDirection(playerEmail);
            if (targetPosition.equals(playerPositionAndDirection.getPosition())) {
                someoneHasReachedTheTargetPosition = true;
            }
        }
        return someoneHasReachedTheTargetPosition;
    }

    /**
     * Checks if the given player (specified by its {@link Position} and {@link Direction} can move forward in the
     * given {@link Grid}.
     *
     * @param grid the grid in which the {@link Player} operates
     * @param position the {@link Position} of the {@link Player}
     * @param direction the {@link Direction} of the {@link Player}
     * @return true iff the player at the given {@link Position} and {@link Direction} can move forward
     */
    public static boolean canMoveForward(final Grid grid, final Position position, final Direction direction) {
        return !RuntimeController.hasWall(grid, position, direction);
    }

    /**
     * Checks if the given player (specified by its {@link Position} and {@link Direction} can move forward in the
     * given {@link Grid}.
     *
     * @param grid the grid in which the {@link Player} operates
     * @param position the {@link Position} of the {@link Player}
     * @param direction the {@link Direction} of the {@link Player}
     * @return true iff the player at the given {@link Position} and {@link Direction} can move forward
     */
    public static boolean canMoveBackward(final Grid grid, final Position position, final Direction direction) {
        final Direction oppositeDirection = direction.opposite();
        return !hasWall(grid, position, oppositeDirection);
    }

    /**
     * Checks if the {@link Player} can move 'left' in the given {@link Grid}, relative to its {@link Position} and
     * {@link Direction}.
     *
     * @param grid the grid in which the {@link Player} operates
     * @param position the {@link Position} of the {@link Player}
     * @param direction the {@link Direction} of the {@link Player}
     * @return true iff the {@link Player} can move 'left', relative to {@link Position} and {@link Direction}
     */
    public static boolean canMoveLeft(final Grid grid, final Position position, final Direction direction) {
        final Direction leftDirection = direction.turnCounterClockwise();
        return !hasWall(grid, position, leftDirection);
    }

    /**
     * Checks if the {@link Player} can move 'right' in the given {@link Grid}, relative to its {@link Position} and
     * {@link Direction}.
     *
     * @param grid the grid in which the {@link Player} operates
     * @param position the {@link Position} of the {@link Player}
     * @param direction the {@link Direction} of the {@link Player}
     * @return true iff the {@link Player} can move 'right', relative to {@link Position} and {@link Direction}
     */
    public static boolean canMoveRight(final Grid grid, final Position position, final Direction direction) {
        final Direction rightDirection = direction.turnClockwise();
        return !hasWall(grid, position, rightDirection);
    }

    public static int getGridCell(final Grid grid, final int row, final int col) throws IndexOutOfBoundsException {
        if(col < 0 || col > grid.getWidth()) throw new IndexOutOfBoundsException("col not in bounds [0, " + grid.getWidth() + ")");
        if(row < 0 || row > grid.getHeight()) throw new IndexOutOfBoundsException("row not in bounds [0, " + grid.getHeight() + ")");
        final char c = grid.getData().charAt(row * grid.getWidth() + col);
        return Integer.parseInt(Character.toString(c), 16);
    }

    private static boolean hasWall(final Grid grid, final Position position, final Direction direction) {
        final int shape = getGridCell(grid, position.getRow(), position.getCol());
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

    private static Position movePlayerForward(final PlayerPositionAndDirection playerPositionAndDirection) {
        switch (playerPositionAndDirection.getDirection()) {
            case NORTH:
                return playerPositionAndDirection.getPosition().moveNorth();
            case SOUTH:
                return playerPositionAndDirection.getPosition().moveSouth();
            case WEST:
                return playerPositionAndDirection.getPosition().moveWest();
            case EAST:
                return playerPositionAndDirection.getPosition().moveEast();
            default: throw new RuntimeException("Invalid direction: " + playerPositionAndDirection.getDirection());
        }
    }

    public static boolean allPlayersHaveLost(final Game game) {
        if (game.getActivePlayers().isEmpty()) return true;
        return false;
    }

}