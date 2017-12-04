package org.inspirecenter.amazechallenge.model;

import org.inspirecenter.amazechallenge.algorithms.InterpretedMazeSolver;
import org.inspirecenter.amazechallenge.algorithms.MazeSolver;
import org.inspirecenter.amazechallenge.algorithms.PlayerMove;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import static java.util.Collections.EMPTY_MAP;

/**
 * @author Nearchos
 *         Created: 15-Aug-17
 */
@com.googlecode.objectify.annotation.Entity
public class Game implements Serializable {

    public static final long ONE_SECOND = 1000L;

    private static final Direction DEFAULT_STARTING_DIRECTION = Direction.NORTH;

    @com.googlecode.objectify.annotation.Id
    public Long id;

    @com.googlecode.objectify.annotation.Index
    public Long challengeId;

    private long lastUpdated = 0;
    private long counter = 0;
    private Grid grid;
    private Map<String,Player> playerEmailsToPlayers = new HashMap<>();
    private Map<String,String> playerEmailsToMazeSolverCodes = new HashMap<>();

    private Map<String,PlayerStatistics> playerEmailsToStatistics = new HashMap<>();

    public Game() {
        super();
    }

    public Game(final Long challengeId, final Grid grid) {
        this();
        this.challengeId = challengeId;
        this.grid = grid;
    }

    public Long getChallengeId() {
        return challengeId;
    }

    public long getCounter() {
        return counter;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public boolean hasBeenUpdatedInTheLastSecond() {
        return lastUpdated > System.currentTimeMillis() - ONE_SECOND;
    }

    /**
     * Called to indicate the completion of an update
     */
    public void touch() {
        lastUpdated = System.currentTimeMillis();
        counter++;
    }

    public int getGridWidth() {
        return grid.getWidth();
    }

    public int getGridHeight() {
        return grid.getHeight();
    }

    public int getCell(final int row, final int col) {
        return grid.getGridCell(row, col);
    }

    public Position getStartingPosition() {
        return grid.getStartingPosition();
    }

    public Position getTargetPosition() {
        return grid.getTargetPosition();
    }

    /**
     * Adds a {@link Player} to the {@link Game}. If a player with the given name exists, it is
     * replaced.
     * @param player the {@link Player} to be added to the {@link Game}.
     * @return true if the player was replaced, false if added for the first time
     */
    public boolean addPlayer(final Player player) {
        return addPlayer(player, "");
    }

    /**
     * Adds a {@link Player} to the {@link Game}. If a player with the given name exists, it is
     * replaced.
     * @param player the {@link Player} to be added to the {@link Game}.
     * @param code the javascript code to be used with the InterpretedMazeSolver
     * @return true if the player was replaced, false if added for the first time
     */
    public boolean addPlayer(final Player player, final String code) {
        final boolean replaced = playerEmailsToPlayers.containsKey(player.getEmail());
        player.init(grid.getStartingPosition(), DEFAULT_STARTING_DIRECTION);
        playerEmailsToPlayers.put(player.getEmail(), player);
        playerEmailsToMazeSolverCodes.put(player.getEmail(), code);
        playerEmailsToStatistics.put(player.getEmail(), new PlayerStatistics());
        return replaced;
    }

    private void removePlayer(final String playerEmail) {
        playerEmailsToPlayers.remove(playerEmail);
        playerEmailsToMazeSolverCodes.remove(playerEmail);
        playerEmailsToStatistics.remove(playerEmail);
    }

    public Collection<Player> getAllPlayers() {
        return playerEmailsToPlayers.values();
    }

    public void applyNextMove() {
        for(final Player player : getAllPlayers()) {
            final MazeSolver mazeSolver = new InterpretedMazeSolver(this, player.getEmail());
            mazeSolver.setParameter(InterpretedMazeSolver.PARAMETER_KEY_CODE, playerEmailsToMazeSolverCodes.get(player.getEmail()));
            final PlayerMove nextPlayerMove = mazeSolver.getNextMove();
            this.applyPlayerMove(nextPlayerMove, player);
            this.playerEmailsToStatistics.get(player.getEmail()).increaseMoves(nextPlayerMove);
        }
    }

    public boolean hasActivePlayers() {
        return !getAllPlayers().isEmpty();
    }

    public boolean hasSomeoneReachedTheTargetPosition() {
        final Position targetPosition = getTargetPosition();
        boolean someoneHasReachedTheTargetPosition = false;
        for(final Player player : getAllPlayers()) {
            if(targetPosition.equals(player.getPosition())) {
                someoneHasReachedTheTargetPosition = true;
            }
        }
        return someoneHasReachedTheTargetPosition;
    }

    public boolean removePlayersWhoHaveReachedTheTargetPosition() {
        final Position targetPosition = getTargetPosition();
        boolean someoneHasReachedTheTargetPosition = false;
        final List<String> emailsOfPlayersToBeRemoved = new Vector<>();
        for(final Player player : getAllPlayers()) {
            if(targetPosition.equals(player.getPosition())) {
                someoneHasReachedTheTargetPosition = true;
                emailsOfPlayersToBeRemoved.add(player.getEmail());
            }
        }

        for(final String playerEmail : emailsOfPlayersToBeRemoved) {
            removePlayer(playerEmail);
        }

        return someoneHasReachedTheTargetPosition;
    }

    private String getStatisticsDescription(final String playerEmail) {
        final StringBuilder stringBuilder = new StringBuilder();
        final PlayerStatistics playerStatistics = playerEmailsToStatistics.get(playerEmail);
        for(final PlayerMove playerMove : PlayerMove.values()) {
            stringBuilder
                    .append(playerMove.getCode())
                    .append(": ")
                    .append(playerStatistics.getNumOfMoves(playerMove))
                    .append(", ");
        }
        stringBuilder.append("T: ").append(playerStatistics.getNumOfMoves());
        return stringBuilder.toString();
    }

    public String getStatisticsDescription() {
        final StringBuilder stringBuilder = new StringBuilder();
        for(final String playerEmail : playerEmailsToStatistics.keySet()) {
            stringBuilder.append(getStatisticsDescription(playerEmail)).append("\n");
        }
        return stringBuilder.toString();
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
                // Log.d("grid-challenge", "move: " + playerMove);
                break;
            default:
                throw new RuntimeException("Invalid PlayerMove: " + playerMove);
        }
    }

    /**
     * Returns the {@link Player}'s {@link Direction}, i.e. {@link Direction#NORTH},
     * {@link Direction#SOUTH}, {@link Direction#EAST}, {@link Direction#WEST}.
     *
     * @param playerEmail the email of the {@link Player} to be checked
     * @return the {@link Player}'s {@link Direction}
     */
    public Direction getDirection(final String playerEmail) {
        return getDirection(playerEmailsToPlayers.get(playerEmail));
    }

    /**
     * Returns the {@link Player}'s {@link Direction}, i.e. {@link Direction#NORTH},
     * {@link Direction#SOUTH}, {@link Direction#EAST}, {@link Direction#WEST}.
     *
     * @param player the {@link Player} to be checked
     * @return the {@link Player}'s {@link Direction}
     */
    private Direction getDirection(final Player player) {
        return player.getDirection();
    }

    /**
     * Checks if the {@link Player} can move 'forward', i.e. along its {@link Direction}.
     *
     * @param playerEmail the email of the player to be checked
     * @return true iff the {@link Player} can move 'forward', i.e. along its {@link Direction}
     */
    public boolean canMoveForward(final String playerEmail) {
        return canMoveForward(playerEmailsToPlayers.get(playerEmail));
    }

    /**
     * Checks if the {@link Player} can move 'forward', i.e. along its {@link Direction}.
     *
     * @param player the {@link Player} to be checked
     * @return true iff the {@link Player} can move 'forward', i.e. along its {@link Direction}
     */
    private boolean canMoveForward(final Player player) {
        final Position playerPosition = player.getPosition();
        final Direction playerDirection = player.getDirection();
        return !grid.hasWall(playerPosition, playerDirection);
    }

    /**
     * Checks if the {@link Player} can move 'forward', i.e. along its {@link Direction}.
     *
     * @param playerEmail the email of the player to be checked
     * @return true iff the {@link Player} can move 'forward', i.e. along its {@link Direction}
     */
    public boolean canMoveBackward(final String playerEmail) {
        return canMoveBackward(playerEmailsToPlayers.get(playerEmail));
    }

    /**
     * Checks if the {@link Player} can move 'backwards', i.e. opposite to its {@link Direction}.
     *
     * @param player the {@link Player} to be checked
     * @return true iff the {@link Player} can move 'backwards', i.e. opposite to its {@link Direction}
     */
    private boolean canMoveBackward(final Player player) {
        final Position playerPosition = player.getPosition();
        final Direction oppositeDirection = player.getDirection().opposite();
        return !grid.hasWall(playerPosition, oppositeDirection);
    }

    /**
     * Checks if the {@link Player} can move 'left', relative to its {@link Direction}.
     *
     * @param playerEmail the email of the player to be checked
     * @return true iff the {@link Player} can move 'left', relative to its {@link Direction}
     */
    public boolean canMoveLeft(final String playerEmail) {
        return canMoveLeft(playerEmailsToPlayers.get(playerEmail));
    }

    /**
     * Checks if the {@link Player} can move 'left', relative to its {@link Direction}.
     *
     * @param player the {@link Player} to be checked
     * @return true iff the {@link Player} can move 'left', relative to its {@link Direction}
     */
    private boolean canMoveLeft(final Player player) {
        final Position playerPosition = player.getPosition();
        final Direction leftDirection = player.getDirection().turnCounterClockwise();
        return !grid.hasWall(playerPosition, leftDirection);
    }

    /**
     * Checks if the {@link Player} can move 'right', relative to its {@link Direction}.
     *
     * @param playerEmail the email of the player to be checked
     * @return true iff the {@link Player} can move 'right', relative to its {@link Direction}
     */
    public boolean canMoveRight(final String playerEmail) {
        return canMoveRight(playerEmailsToPlayers.get(playerEmail));
    }

    /**
     * Checks if the {@link Player} can move 'right', relative to its {@link Direction}.
     *
     * @param player the {@link Player} to be checked
     * @return true iff the {@link Player} can move 'right', relative to its {@link Direction}
     */
    private boolean canMoveRight(final Player player) {
        final Position playerPosition = player.getPosition();
        final Direction rightDirection = player.getDirection().turnClockwise();
        return !grid.hasWall(playerPosition, rightDirection);
    }

    /**
     * Get {@link MazeSolver} implementation by instantiating {@link Class<MazeSolver>} implementing
     * the corresponding algorithm using reflection.
     * @param playerEmail the email of the {@link Player} to be used for applying the {@link MazeSolver}
     * @return the implementation of {@link MazeSolver} corresponding to the given {@link Player}
     */
    private MazeSolver getMazeSolver(final String playerEmail, final Class<? extends MazeSolver> clazz) {
        try {
            final Constructor<?> constructor = clazz.getConstructor(Game.class, String.class);
            return (MazeSolver) constructor.newInstance(this, playerEmail);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}