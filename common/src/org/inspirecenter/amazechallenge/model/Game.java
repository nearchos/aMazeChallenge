package org.inspirecenter.amazechallenge.model;

import org.inspirecenter.amazechallenge.controller.AudioEventListener;
import org.inspirecenter.amazechallenge.controller.GameEndListener;

import java.io.Serializable;
import java.util.*;

/**
 * @author Nearchos
 *         Created: 15-Aug-17
 */
@com.googlecode.objectify.annotation.Entity
public class Game implements Serializable {

    @com.googlecode.objectify.annotation.Id
    public Long id;

    @com.googlecode.objectify.annotation.Index
    Long challengeId;

    private List<String> finishedPlayers = new Vector<>();
    private List<String> queuedPlayers = new Vector<>();
    private List<String> waitingPlayers = new Vector<>();
    private Map<String,Player> allPlayerIDsToPlayers = new HashMap<>();
    private Map<String,PlayerPositionAndDirection> activePlayerIDsToPositionAndDirections = new HashMap<>();
    private List<Pickable> pickables = new Vector<>();
    private long lastExecutionTime = 0L;
    private long lastUpdated = 0L;
    private long counter = 0L;

    public Game() {
        super();
    }

    public Game(final Long challengeId) {
        this();
        this.challengeId = challengeId;
    }

    public Long getId() {
        return id;
    }

    public Long getChallengeId() {
        return challengeId;
    }

    public Map<String, Player> getAllPlayerIDsToPlayers() {
        return new HashMap<>(allPlayerIDsToPlayers);
    }

    public boolean containsPlayerById(final String playerId) {
        return allPlayerIDsToPlayers.containsKey(playerId);
    }

    public long getCounter() {
        return counter;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    /**
     * Called to indicate the completion of a round
     */
    public void touch(final long lastExecutionTime) {
        this.lastExecutionTime = lastExecutionTime;
        lastUpdated = System.currentTimeMillis();
        counter++;
    }

    public long getLastExecutionTime() {
        return lastExecutionTime;
    }

    /**
     * Adds a {@link Player} to the 'waiting' list of the {@link Game}. If a player with the given email exists, it is
     * replaced.
     * @param player the {@link Player} to be added to the {@link Game}.
     * @return true iff the specified player email existed in either the 'active' or 'queued' list (the 'waiting' list is not checked)
     */
    public void addPlayer(final Player player) {
        final String playerId = player.getId();
        allPlayerIDsToPlayers.put(playerId, player);
        finishedPlayers.remove(playerId);
        activePlayerIDsToPositionAndDirections.remove(playerId);
        queuedPlayers.remove(playerId);
        if(!waitingPlayers.contains(playerId)) waitingPlayers.add(playerId);
    }

    public boolean resetPlayerById(final String playerId) {
        boolean existed = false;
        if(finishedPlayers.remove(playerId)) existed = true;
        if(activePlayerIDsToPositionAndDirections.remove(playerId) != null) existed = true;
        if(queuedPlayers.remove(playerId)) existed = true;
        if(!waitingPlayers.contains(playerId)) waitingPlayers.add(playerId);

        final Player player = getPlayerById(playerId);
        player.reset();
        return existed;
    }

    public boolean queuePlayerById(final String playerId) {
        if(waitingPlayers.contains(playerId)) {
            waitingPlayers.remove(playerId);
            queuedPlayers.add(playerId); // adds to the end of the 'queue'
            return true;
        } else {
            return false;
        }
    }

    public boolean activateNextPlayer(final Grid grid) {
        if(!queuedPlayers.isEmpty()) {
            final String nextPlayerId = queuedPlayers.remove(0); // get first in line from 'queued'
            activePlayerIDsToPositionAndDirections.put(nextPlayerId, new PlayerPositionAndDirection(grid.getStartingPosition(), grid.getStartingDirection()));
            final Player player = getPlayerById(nextPlayerId);
            player.setHealth(new Health());
            return true;
        } else {
            return false;
        }
    }

    public void setPlayerAsFinished(final String playerId) {
        if(activePlayerIDsToPositionAndDirections.containsKey(playerId)) {
            activePlayerIDsToPositionAndDirections.remove(playerId);
            finishedPlayers.add(playerId);
        }
    }

    public Player getPlayerById(final String playerId) {
        return allPlayerIDsToPlayers.get(playerId);
    }

    public boolean hasFinishedPlayers() {
        return !finishedPlayers.isEmpty();
    }

    public boolean hasActivePlayers() {
        return !activePlayerIDsToPositionAndDirections.isEmpty();
    }

    public int getNumberOfActivePlayers() {
        return activePlayerIDsToPositionAndDirections.size();
    }

    public boolean hasQueuedPlayers() {
        return !queuedPlayers.isEmpty();
    }

    public boolean hasWaitingPlayers() {
        return !waitingPlayers.isEmpty();
    }

    public Collection<Player> getAllPlayers() {
        return allPlayerIDsToPlayers.values();
    }

    public List<String> getFinishedPlayers() {
        return new Vector<>(finishedPlayers);
    }

    public List<String> getActivePlayerIDs() {
        return new Vector<>(activePlayerIDsToPositionAndDirections.keySet());
    }

    public List<String> getQueuedPlayerIDs() {
        return new Vector<>(queuedPlayers);
    }

    public List<String> getWaitingPlayerIDs() { return new Vector<>(waitingPlayers); }

    public boolean hasActiveOrQueuedPlayers() {
        return hasActivePlayers() || hasQueuedPlayers();
    }

    public int getNumOfBiasType(final PickableType.Bias biasType) {
        int count = 0;
        for(final Pickable pickable : pickables) {
            if(pickable.getPickableType().getBias() == biasType) count++;
        }
        return count;
    }

    public List<Pickable> getPickables() {
        return pickables;
    }

    public void addPickableItem(final Pickable pickable) {
        this.pickables.add(pickable);
    }

    public void removePickupItem(int i) {
        pickables.remove(i);
    }

    public GameFullState getFullState(final Grid grid) {
        return new GameFullState(activePlayerIDsToPositionAndDirections, queuedPlayers, pickables, allPlayerIDsToPlayers, grid, lastUpdated, counter);
    }

    public Map<String,PlayerPositionAndDirection> getPlayerIDsToPositionAndDirections() {
        return new HashMap<>(activePlayerIDsToPositionAndDirections);
    }

    public PlayerPositionAndDirection getPlayerPositionAndDirectionById(final String playerId) {
        return activePlayerIDsToPositionAndDirections.get(playerId);
    }

    public void setPlayerPositionAndDirectionById(final String playerId, final PlayerPositionAndDirection playerPositionAndDirection) {
        activePlayerIDsToPositionAndDirections.put(playerId, playerPositionAndDirection);
    }

    /**
     * Returns the {@link Player}'s {@link Position} in the {@link Game}'s {@link Grid}.
     *
     * @param playerId the ID of the {@link Player} to be checked
     * @return the {@link Player}'s {@link Position}
     */
    public Position getPositionById(final String playerId) {
        final PlayerPositionAndDirection playerPositionAndDirection = activePlayerIDsToPositionAndDirections.get(playerId);
        return playerPositionAndDirection != null ? playerPositionAndDirection.getPosition() : null;
    }

    /**
     * Returns the {@link Player}'s {@link Direction}, i.e. {@link Direction#NORTH},
     * {@link Direction#SOUTH}, {@link Direction#EAST}, {@link Direction#WEST}.
     *
     * @param playerID the email of the {@link Player} to be checked
     * @return the {@link Player}'s {@link Direction}
     */
    public Direction getDirectionByID(final String playerID) {
        return activePlayerIDsToPositionAndDirections.get(playerID).getDirection();
    }

    @Override
    public String toString() {
        final StringBuilder stringBuilder = new StringBuilder("GAME - id: ").append(id).append(" ~~ challengeId: ").append(challengeId).append(" @").append(hashCode());
        stringBuilder.append(" $ players[a/q/w]: ").append(activePlayerIDsToPositionAndDirections.size()).append("/").append(queuedPlayers.size()).append("/").append(waitingPlayers.size()).append("\n");
        stringBuilder.append("*finished: ").append(finishedPlayers).append("\n");
        stringBuilder.append("*active: ").append(activePlayerIDsToPositionAndDirections).append("\n");
        stringBuilder.append("*queued: ").append(queuedPlayers).append("\n");
        stringBuilder.append("*waiting: ").append(waitingPlayers).append("\n");
        return stringBuilder.toString();
    }

    public void resetPickables() {
        pickables.clear();
    }

    private AudioEventListener audioEventListener = null;
    private GameEndListener gameEndListener = null;

    public void setOnAudioEventListener(AudioEventListener audioEventListener) {
        this.audioEventListener = audioEventListener;
    }

    public AudioEventListener getAudioEventListener() {
        return audioEventListener;
    }

    public void setGameEndListener(GameEndListener gameEndListener) {
        this.gameEndListener = gameEndListener;
    }

    public GameEndListener getGameEndListener() { return gameEndListener; }

}