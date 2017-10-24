package org.inspirecenter.amazechallenge.model;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import org.inspirecenter.amazechallenge.algorithms.MazeSolver;
import org.inspirecenter.amazechallenge.algorithms.PlayerMove;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Nearchos
 *         Created: 15-Aug-17
 */
public class Game implements Serializable {

    private static final Direction DEFAULT_STARTING_DIRECTION = Direction.NORTH;
    private final Maze maze;
    private Map<String,Player> players = new HashMap<>();
    private Map<String,MazeSolver> playerNamesToMazeSolvers = new HashMap<>();

    public Game(final Maze maze) {
        this.maze = maze;
    }

    public int getGridSize() {
        return maze.getGridSize();
    }

    public int getCell(final int row, final int col) {
        return maze.get(row, col);
    }

    public Position getStartingPosition() {
        return maze.getStartingPosition();
    }

    public Position getTargetPosition() {
        return maze.getTargetPosition();
    }

    /**
     * Adds a {@link Player} to the {@link Game}. If a player with the given name exists, it is
     * replaced.
     * @param player the {@link Player} to be added to the {@link Game}.
     * @return true if the player was replaced, false if added for the first time
     */
    public boolean addPlayer(@NonNull final Player player) {
        return addPlayer(player, Collections.EMPTY_MAP);
    }

    /**
     * Adds a {@link Player} to the {@link Game}. If a player with the given name exists, it is
     * replaced.
     * @param player the {@link Player} to be added to the {@link Game}.
     * @param parametersMap a map of {@link String} to {@link Serializable} objects, as parameters
     * @return true if the player was replaced, false if added for the first time
     */
    public boolean addPlayer(@NonNull final Player player, final Map<String, Serializable> parametersMap) {
        final boolean replaced = players.containsKey(player.getName());
        player.init(maze.getStartingPosition(), DEFAULT_STARTING_DIRECTION);
        players.put(player.getName(), player);
        final MazeSolver mazeSolver = getMazeSolver(player);
        for(final Map.Entry<String,Serializable> entry : parametersMap.entrySet()) {
            mazeSolver.setParameter(entry.getKey(), entry.getValue());
        }
        playerNamesToMazeSolvers.put(player.getName(), mazeSolver);
        return replaced;
    }

    public Collection<Player> getAllPlayers() {
        return players.values();
    }

    public void applyNextMove(final Activity activity) {
        for(final Player player : players.values()) {
            final MazeSolver mazeSolver = playerNamesToMazeSolvers.get(player.getName());
            final PlayerMove nextPlayerMove = mazeSolver.getNextMove();
            this.applyPlayerMove(nextPlayerMove, player);
        }
        final Position targetPosition = getTargetPosition();
        boolean someoneWon = false;
        for(final Player player : players.values()) {
            if(targetPosition.equals(player.getPosition())) {
                someoneWon = true;
                Toast.makeText(activity, "Player " + player.getName() + " won!", Toast.LENGTH_SHORT).show();
            }
        }
        if(someoneWon) activity.finish();//todo perhaps show ranking
    }

    private void applyPlayerMove(final PlayerMove playerMove, final Player player) {
        switch (playerMove) {
            case TURN_CLOCKWISE:
                player.turnClockwise();
                break;
            case TURN_COUNTERCLOCKWISE:
                player.turnCounterClockwise();
                break;
            case MOVE_FORWARD:
                if(canMoveForward(player)) player.moveForward();
                break;
            case NO_MOVE:
                Log.d("maze-challenge", "move: " + playerMove);
                break;
            default:
                throw new RuntimeException("Invalid PlayerMove: " + playerMove);
        }
    }

    /**
     * Returns the {@link Player}'s {@link Direction}, i.e. {@link Direction#NORTH},
     * {@link Direction#SOUTH}, {@link Direction#EAST}, {@link Direction#WEST}.
     *
     * @param player the {@link Player} to be checked
     * @return the {@link Player}'s {@link Direction}
     */
    public Direction getDirection(final Player player) {
        return player.getDirection();
    }

    /**
     * Checks if the {@link Player} can move 'forward', i.e. along its {@link Direction}.
     *
     * @param player the {@link Player} to be checked
     * @return true iff the {@link Player} can move 'forward', i.e. along its {@link Direction}
     */
    public boolean canMoveForward(final Player player) {
        final Position playerPosition = player.getPosition();
        final Direction playerDirection = player.getDirection();
        return !maze.hasWall(playerPosition, playerDirection);
    }

    /**
     * Checks if the {@link Player} can move 'backwards', i.e. opposite to its {@link Direction}.
     *
     * @param player the {@link Player} to be checked
     * @return true iff the {@link Player} can move 'backwards', i.e. opposite to its {@link Direction}
     */
    public boolean canMoveBackward(final Player player) {
        final Position playerPosition = player.getPosition();
        final Direction oppositeDirection = player.getDirection().opposite();
        return !maze.hasWall(playerPosition, oppositeDirection);
    }

    /**
     * Checks if the {@link Player} can move 'left', relative to its {@link Direction}.
     *
     * @param player the {@link Player} to be checked
     * @return true iff the {@link Player} can move 'left', relative to its {@link Direction}
     */
    public boolean canMoveLeft(final Player player) {
        final Position playerPosition = player.getPosition();
        final Direction leftDirection = player.getDirection().turnCounterClockwise();
        return !maze.hasWall(playerPosition, leftDirection);
    }

    /**
     * Checks if the {@link Player} can move 'right', relative to its {@link Direction}.
     *
     * @param player the {@link Player} to be checked
     * @return true iff the {@link Player} can move 'right', relative to its {@link Direction}
     */
    public boolean canMoveRight(final Player player) {
        final Position playerPosition = player.getPosition();
        final Direction rightDirection = player.getDirection().turnClockwise();
        return !maze.hasWall(playerPosition, rightDirection);
    }

    /**
     * Get {@link MazeSolver} implementation by instantiating {@link Class<MazeSolver>} implementing
     * the corresponding algorithm using reflection.
     * @param player the {@link Player} from which the {@link MazeSolver} class is retrieved
     * @return the implementation of {@link MazeSolver} corresponding to the given {@link Player}
     */
    private MazeSolver getMazeSolver(final Player player) {
        final Class<? extends MazeSolver> clazz = player.getMazeSolverClass();
        try {
            final Constructor<?> constructor = clazz.getConstructor(Game.class, Player.class);
            return (MazeSolver) constructor.newInstance(this, player);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            Log.e("challenges", "Error while instantiating object for algorithm: " + clazz, e);
            throw new RuntimeException(e);
        }
    }
}