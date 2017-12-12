package org.inspirecenter.amazechallenge.controller;

import org.inspirecenter.amazechallenge.algorithms.MazeSolver;
import org.inspirecenter.amazechallenge.algorithms.PlayerMove;
import org.inspirecenter.amazechallenge.model.Direction;
import org.inspirecenter.amazechallenge.model.Game;
import org.inspirecenter.amazechallenge.model.GameLightState;
import org.inspirecenter.amazechallenge.model.Grid;
import org.inspirecenter.amazechallenge.model.Player;
import org.inspirecenter.amazechallenge.model.PlayerPositionAndDirection;
import org.inspirecenter.amazechallenge.model.Position;

import java.util.List;
import java.util.Vector;

import static org.inspirecenter.amazechallenge.model.Grid.SHAPE_ONLY_LEFT_SIDE;
import static org.inspirecenter.amazechallenge.model.Grid.SHAPE_ONLY_LOWER_SIDE;
import static org.inspirecenter.amazechallenge.model.Grid.SHAPE_ONLY_RIGHT_SIDE;
import static org.inspirecenter.amazechallenge.model.Grid.SHAPE_ONLY_UPPER_SIDE;

/**
 * @author Nearchos
 *         Created: 11-Dec-17
 */

public class RuntimeController {

    public static GameLightState makeMove(final Game game) {
        // then apply next move to active players
        for (final String playerEmail : game.getActivePlayers()) {
            final PlayerPositionAndDirection playerPositionAndDirection = game.getPlayerPositionAndDirection(playerEmail);
            final MazeSolver mazeSolver = game.getMazeSolver(playerEmail);
            final PlayerMove nextPlayerMove = mazeSolver == null ? PlayerMove.NO_MOVE : mazeSolver.getNextMove();
            applyPlayerMove(game, playerEmail, playerPositionAndDirection, nextPlayerMove);
            game.increasePlayerMoves(playerEmail, nextPlayerMove);
        }

        // last return the light state of the game
        return game.getLightState();
    }

    private static PlayerPositionAndDirection applyPlayerMove(final Game game, final String playerEmail, final PlayerPositionAndDirection playerPositionAndDirection, final PlayerMove playerMove) {
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
                if (canMoveForward(game, playerPositionAndDirection)) position = movePlayerForward(playerPositionAndDirection);
                break;
            case NO_MOVE:
                // Log.d("grid-challenge", "move: " + playerMove);
                break;
            default:
                throw new RuntimeException("Invalid PlayerMove: " + playerMove);
        }
        final PlayerPositionAndDirection updatedPlayerPositionAndDirection = new PlayerPositionAndDirection(position, direction);
        game.setPlayerPositionAndDirection1(playerEmail, updatedPlayerPositionAndDirection);
        return updatedPlayerPositionAndDirection;
    }

    public static boolean hasSomeoneReachedTheTargetPosition(final Game game) {
        final Position targetPosition = game.getTargetPosition();
        boolean someoneHasReachedTheTargetPosition = false;
        for (final String playerEmail : game.getActivePlayers()) {
            final PlayerPositionAndDirection playerPositionAndDirection = game.getPlayerPositionAndDirection(playerEmail);
            if (targetPosition.equals(playerPositionAndDirection.getPosition())) {
                someoneHasReachedTheTargetPosition = true;
            }
        }
        return someoneHasReachedTheTargetPosition;
    }

    public static boolean removePlayersWhoHaveReachedTheTargetPosition(final Game game) {
        final Position targetPosition = game.getTargetPosition();
        boolean someoneHasReachedTheTargetPosition = false;
        final List<String> emailsOfPlayersToBeRemoved = new Vector<>();
        for (final String playerEmail : game.getActivePlayers()) {
            final PlayerPositionAndDirection playerPositionAndDirection = game.getPlayerPositionAndDirection(playerEmail);
            if (targetPosition.equals(playerPositionAndDirection.getPosition())) {
                someoneHasReachedTheTargetPosition = true;
                emailsOfPlayersToBeRemoved.add(playerEmail);
            }
        }

        for (final String playerEmail : emailsOfPlayersToBeRemoved) {
            game.removePlayer(playerEmail);
        }

        return someoneHasReachedTheTargetPosition;
    }

    /**
     * Checks if the {@link Player} can move 'forward', i.e. along its {@link Direction}.
     *
     * @param playerEmail the email of the player to be checked
     * @return true iff the {@link Player} can move 'forward', i.e. along its {@link Direction}
     */
//    public static boolean canMoveForward(final Game game, final String playerEmail) {
//        return canMoveForward(game, game.getPlayer(playerEmail));
//    }

    /**
     * Checks if the {@link Player} can move 'forward', i.e. along its {@link Direction}.
     *
     * @param playerPositionAndDirection the {@link PlayerPositionAndDirection} to be checked
     * @return true iff the {@link Player} can move 'forward', i.e. along its {@link Direction}
     */
    private static boolean canMoveForward(final Game game, final PlayerPositionAndDirection playerPositionAndDirection) {
        final Position playerPosition = playerPositionAndDirection.getPosition();
        final Direction playerDirection = playerPositionAndDirection.getDirection();
        return !game.hasWall(playerPosition, playerDirection);
    }

    /**
     * Checks if the {@link Player} can move 'forward', i.e. along its {@link Direction}.
     *
     * @param playerEmail the email of the player to be checked
     * @return true iff the {@link Player} can move 'forward', i.e. along its {@link Direction}
     */
//    public static boolean canMoveBackward(final Game game, final String playerEmail) {
//        return canMoveBackward(game, game.getPlayer(playerEmail));
//    }

    /**
     * Checks if the {@link Player} can move 'backwards', i.e. opposite to its {@link Direction}.
     *
     * @param playerPositionAndDirection the {@link PlayerPositionAndDirection} to be checked
     * @return true iff the {@link Player} can move 'backwards', i.e. opposite to its {@link Direction}
     */
    private static boolean canMoveBackward(final Game game, final PlayerPositionAndDirection playerPositionAndDirection) {
        final Position playerPosition = playerPositionAndDirection.getPosition();
        final Direction oppositeDirection = playerPositionAndDirection.getDirection().opposite();
        return !game.hasWall(playerPosition, oppositeDirection);
    }

    /**
     * Checks if the {@link Player} can move 'left', relative to its {@link Direction}.
     *
     * @param playerEmail the email of the player to be checked
     * @return true iff the {@link Player} can move 'left', relative to its {@link Direction}
     */
//    public static boolean canMoveLeft(final Game game, final String playerEmail) {
//        return canMoveLeft(game, game.getPlayer(playerEmail));
//    }

    /**
     * Checks if the {@link Player} can move 'left', relative to its {@link Direction}.
     *
     * @param playerPositionAndDirection the {@link PlayerPositionAndDirection} to be checked
     * @return true iff the {@link Player} can move 'left', relative to its {@link Direction}
     */
    private static boolean canMoveLeft(final Game game, final PlayerPositionAndDirection playerPositionAndDirection) {
        final Position playerPosition = playerPositionAndDirection.getPosition();
        final Direction leftDirection = playerPositionAndDirection.getDirection().turnCounterClockwise();
        return !game.hasWall(playerPosition, leftDirection);
    }

    /**
     * Checks if the {@link Player} can move 'right', relative to its {@link Direction}.
     *
     * @param playerEmail the email of the player to be checked
     * @return true iff the {@link Player} can move 'right', relative to its {@link Direction}
     */
//    public static boolean canMoveRight(final Game game, final String playerEmail) {
//        return canMoveRight(game, game.getPlayer(playerEmail));
//    }

    /**
     * Checks if the {@link Player} can move 'right', relative to its {@link Direction}.
     *
     * @param playerPositionAndDirection the {@link PlayerPositionAndDirection} to be checked
     * @return true iff the {@link Player} can move 'right', relative to its {@link Direction}
     */
    private static boolean canMoveRight(final Game game, final PlayerPositionAndDirection playerPositionAndDirection) {
        final Position playerPosition = playerPositionAndDirection.getPosition();
        final Direction rightDirection = playerPositionAndDirection.getDirection().turnClockwise();
        return !game.hasWall(playerPosition, rightDirection);
    }

    public static int getGridCell(final Grid grid, final int row, final int col) throws IndexOutOfBoundsException {
        if(col < 0 || col > grid.getWidth()) throw new IndexOutOfBoundsException("col not in bounds [0, " + grid.getWidth() + ")");
        if(row < 0 || row > grid.getHeight()) throw new IndexOutOfBoundsException("row not in bounds [0, " + grid.getHeight() + ")");
        final char c = grid.getData().charAt(row * grid.getWidth() + col);
        return Integer.parseInt(Character.toString(c), 16);
    }

    public static boolean hasWall(final Grid grid, final Position position, final Direction direction) {
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

    public static Position movePlayerForward(final PlayerPositionAndDirection playerPositionAndDirection) {
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
}