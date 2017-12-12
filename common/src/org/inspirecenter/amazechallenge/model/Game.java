package org.inspirecenter.amazechallenge.model;

import org.inspirecenter.amazechallenge.algorithms.InterpretedMazeSolver;
import org.inspirecenter.amazechallenge.algorithms.MazeSolver;
import org.inspirecenter.amazechallenge.algorithms.PlayerMove;
import org.inspirecenter.amazechallenge.controller.RuntimeController;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

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
    private List<String> activePlayers = new Vector<>();
    private List<String> queuedPlayers = new Vector<>();
    private Map<String,Player> playerEmailsToPlayers = new HashMap<>();
    private Map<String,PlayerPositionAndDirection> playerEmailsToPositionAndDirections = new HashMap<>();
    @com.googlecode.objectify.annotation.Ignore
    private Map<String,String> playerEmailsToMazeSolverCodes = new HashMap<>();
    @com.googlecode.objectify.annotation.Ignore
    private Map<String,MazeSolver> playerEmailsToMazeSolvers = new HashMap<>();

    private Map<String,PlayerStatistics> playerEmailsToStatistics = new HashMap<>();

    public Game() {
        super();
    }

    public Game(final Long challengeId, final Grid grid) {
        this();
        this.challengeId = challengeId;
        this.grid = grid;
    }

    public Map<String, Player> getPlayerEmailsToPlayers() {
        return playerEmailsToPlayers;
    }

    public Long getChallengeId() {
        return challengeId;
    }

    public long getCounter() {
        return counter;
    }

    public Grid getGrid() {
        return grid;
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
        return RuntimeController.getGridCell(grid, row, col);
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
        final String playerEmail = player.getEmail();
        final boolean replaced = playerEmailsToPlayers.containsKey(playerEmail);
        playerEmailsToPositionAndDirections.put(playerEmail, new PlayerPositionAndDirection(grid.getStartingPosition(), DEFAULT_STARTING_DIRECTION));
        activePlayers.remove(playerEmail);
        queuedPlayers.add(playerEmail);
        playerEmailsToPlayers.put(playerEmail, player);
        playerEmailsToMazeSolvers.remove(playerEmail); // clear old maze solver if there
        playerEmailsToMazeSolverCodes.put(playerEmail, code);
        playerEmailsToStatistics.put(playerEmail, new PlayerStatistics());
        return replaced;
    }

    public void removePlayer(final String playerEmail) {
        activePlayers.remove(playerEmail);
        queuedPlayers.remove(playerEmail);
        playerEmailsToPlayers.remove(playerEmail);
        playerEmailsToMazeSolvers.remove(playerEmail);
        playerEmailsToMazeSolverCodes.remove(playerEmail);
        playerEmailsToStatistics.remove(playerEmail);
    }

    public void activateNextPlayer() {
        assert !queuedPlayers.isEmpty();
        final String nextPlayerEmail = queuedPlayers.remove(0); // get first in line from 'queued'
        activePlayers.add(nextPlayerEmail);
    }

    public Collection<Player> getAllPlayers() {
        return playerEmailsToPlayers.values();
    }

    public List<String> getActivePlayers() {
        return new Vector<>(activePlayers);
    }

    public List<String> getQueuedPlayers() {
        return new Vector<>(queuedPlayers);
    }

    public GameLightState getLightState() {
        return new GameLightState(playerEmailsToPositionAndDirections, queuedPlayers);
    }

    public Map<String,PlayerPositionAndDirection> getPlayerPositionAndDirections() {
        return new HashMap<>(playerEmailsToPositionAndDirections);
    }

    public PlayerPositionAndDirection getPlayerPositionAndDirection(final String playerEmail) {
        return playerEmailsToPositionAndDirections.get(playerEmail);
    }

    public void setPlayerPositionAndDirection1(final String playerEmail, final PlayerPositionAndDirection playerPositionAndDirection) {
        playerEmailsToPositionAndDirections.put(playerEmail, playerPositionAndDirection);
    }

    public Player getPlayer(final String playerEmail) {
        return playerEmailsToPlayers.get(playerEmail);
    }

    public MazeSolver getMazeSolver(final String playerEmail) {
        // just-in-time-instantiation of the MazeSolver
        MazeSolver mazeSolver = playerEmailsToMazeSolvers.get(playerEmail);
        if(mazeSolver == null) {
            mazeSolver = new InterpretedMazeSolver(this, playerEmail);
            final String code = playerEmailsToMazeSolverCodes.get(playerEmail);
            if(code != null) {
                mazeSolver.setParameter(InterpretedMazeSolver.PARAMETER_KEY_CODE, code);
                playerEmailsToMazeSolvers.put(playerEmail, mazeSolver);
            }
        }

        return mazeSolver;
    }

    public void increasePlayerMoves(final String playerEmail, final PlayerMove playerMove) {
        playerEmailsToStatistics.get(playerEmail).increaseMoves(playerMove);
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

    public boolean hasActivePlayers() {
        return !activePlayers.isEmpty();
    }

    public int getNumberOfActivePlayers() {
        return activePlayers.size();
    }

    public boolean hasQueuedPlayers() {
        return !queuedPlayers.isEmpty();
    }

    public boolean hasWall(final Position position, final Direction direction) {
        return RuntimeController.hasWall(grid, position, direction);
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
        return playerEmailsToPositionAndDirections.get(player.getEmail()).getDirection();
    }

    /**
     * Checks if the {@link Player} can move 'forward', i.e. along its {@link Direction}.
     *
     * @param playerEmail the email of the player to be checked
     * @return true iff the {@link Player} can move 'forward', i.e. along its {@link Direction}
     */
    public boolean canMoveForward(final String playerEmail) {
        final PlayerPositionAndDirection playerPositionAndDirection = playerEmailsToPositionAndDirections.get(playerEmail);
        return !RuntimeController.hasWall(grid, playerPositionAndDirection.getPosition(), playerPositionAndDirection.getDirection());
    }

//    /**
//     * Checks if the {@link Player} can move 'forward', i.e. along its {@link Direction}.
//     *
//     * @param player the {@link Player} to be checked
//     * @return true iff the {@link Player} can move 'forward', i.e. along its {@link Direction}
//     */
//    private boolean canMoveForward(final Player player) {
//        return canMoveForward(playerEmailsToPlayers.get(player.getEmail()));
//    }

    /**
     * Checks if the {@link Player} can move 'forward', i.e. along its {@link Direction}.
     *
     * @param playerEmail the email of the player to be checked
     * @return true iff the {@link Player} can move 'forward', i.e. along its {@link Direction}
     */
    public boolean canMoveBackward(final String playerEmail) {
        final PlayerPositionAndDirection playerPositionAndDirection = playerEmailsToPositionAndDirections.get(playerEmail);
        return !RuntimeController.hasWall(grid, playerPositionAndDirection.getPosition(), playerPositionAndDirection.getDirection());
    }

//    /**
//     * Checks if the {@link Player} can move 'backwards', i.e. opposite to its {@link Direction}.
//     *
//     * @param player the {@link Player} to be checked
//     * @return true iff the {@link Player} can move 'backwards', i.e. opposite to its {@link Direction}
//     */
//    private boolean canMoveBackward(final Player player) {
//        return canMoveBackward(player.getEmail());
//    }

    /**
     * Checks if the {@link Player} can move 'left', relative to its {@link Direction}.
     *
     * @param playerEmail the email of the player to be checked
     * @return true iff the {@link Player} can move 'left', relative to its {@link Direction}
     */
    public boolean canMoveLeft(final String playerEmail) {
        final PlayerPositionAndDirection playerPositionAndDirection = playerEmailsToPositionAndDirections.get(playerEmail);
        final Direction leftDirection = playerPositionAndDirection.getDirection().turnCounterClockwise();
        return !RuntimeController.hasWall(grid, playerPositionAndDirection.getPosition(), leftDirection);
    }

//    /**
//     * Checks if the {@link Player} can move 'left', relative to its {@link Direction}.
//     *
//     * @param player the {@link Player} to be checked
//     * @return true iff the {@link Player} can move 'left', relative to its {@link Direction}
//     */
//    private boolean canMoveLeft(final Player player) {
//        return canMoveLeft(player.getEmail());
//    }

    /**
     * Checks if the {@link Player} can move 'right', relative to its {@link Direction}.
     *
     * @param playerEmail the email of the player to be checked
     * @return true iff the {@link Player} can move 'right', relative to its {@link Direction}
     */
    public boolean canMoveRight(final String playerEmail) {
        final PlayerPositionAndDirection playerPositionAndDirection = playerEmailsToPositionAndDirections.get(playerEmail);
        final Direction rightDirection = playerPositionAndDirection.getDirection().turnClockwise();
        return !RuntimeController.hasWall(grid, playerPositionAndDirection.getPosition(), rightDirection);
    }

//    /**
//     * Checks if the {@link Player} can move 'right', relative to its {@link Direction}.
//     *
//     * @param player the {@link Player} to be checked
//     * @return true iff the {@link Player} can move 'right', relative to its {@link Direction}
//     */
//    private boolean canMoveRight(final Player player) {
//        return canMoveRight(player.getEmail());
//    }

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