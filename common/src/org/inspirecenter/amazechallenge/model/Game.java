package org.inspirecenter.amazechallenge.model;

import org.inspirecenter.amazechallenge.controller.AudioEventListener;

import java.io.Serializable;
import java.util.*;

/**
 * @author Nearchos
 *         Created: 15-Aug-17
 */
@com.googlecode.objectify.annotation.Entity
public class Game implements Serializable {

    public static final long ONE_SECOND = 1000L;

    @com.googlecode.objectify.annotation.Id
    public Long id; // todo check if can convert to private -- i.e. if objectify allows it

    @com.googlecode.objectify.annotation.Index
    public Long challengeId; // todo check if can convert to private

    private List<String> activePlayers = new Vector<>();
    private List<String> queuedPlayers = new Vector<>();
    private List<String> waitingPlayers = new Vector<>();
    private Map<String,Player> allPlayerEmailsToPlayers = new HashMap<>();
    private Map<String,PlayerPositionAndDirection> activePlayerEmailsToPositionAndDirections = new HashMap<>();
    private List<PickupItem> pickupItems = new Vector<>();
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

    public Map<String, Player> getAllPlayerEmailsToPlayers() {
        return new HashMap<>(allPlayerEmailsToPlayers);
    }

    public boolean containsPlayer(final String playerEmail) {
        return allPlayerEmailsToPlayers.containsKey(playerEmail);
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
    public boolean addPlayer(final Player player) {
        final String playerEmail = player.getEmail();
        allPlayerEmailsToPlayers.put(playerEmail, player);
        return resetPlayer(playerEmail);
    }

    public boolean resetPlayer(final String playerEmail) {
        boolean existed = false;
        if(activePlayers.remove(playerEmail)) existed = true;
        if(queuedPlayers.remove(playerEmail)) existed = true;
        waitingPlayers.add(playerEmail);
        //New
        Player player = getPlayer(playerEmail);
        player.setInactive();
        player.resetPoints();
        return existed;
    }

    public boolean queuePlayer(final String playerEmail) {
        if(waitingPlayers.contains(playerEmail)) {
            waitingPlayers.remove(playerEmail);
            queuedPlayers.add(playerEmail); // adds to the end of the 'queue'
            return true;
        } else {
            return false;
        }
    }

    public boolean activateNextPlayer(final Grid grid) {
        if(!queuedPlayers.isEmpty()) {
            final String nextPlayerEmail = queuedPlayers.remove(0); // get first in line from 'queued'
            activePlayers.add(nextPlayerEmail);
            activePlayerEmailsToPositionAndDirections.put(nextPlayerEmail, new PlayerPositionAndDirection(grid.getStartingPosition(), grid.getStartingDirection()));
            Player player = getPlayer(nextPlayerEmail);
            player.setActive();
            player.setHealth(new Health());
            return true;
        } else {
            return false;
        }
    }

    public Player getPlayer(final String playerEmail) {
        return allPlayerEmailsToPlayers.get(playerEmail);
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

    public boolean hasWaitingPlayers() {
        return !waitingPlayers.isEmpty();
    }

    public Collection<Player> getAllPlayers() {
        return allPlayerEmailsToPlayers.values();
    }

    public List<String> getActivePlayers() {
        return new Vector<>(activePlayers);
    }

    public List<String> getQueuedPlayers() {
        return new Vector<>(queuedPlayers);
    }

    public List<String> getWaitingPlayers() { return new Vector<>(waitingPlayers); }

    public boolean hasActiveOrQueuedPlayers() {
        return hasActivePlayers() || hasQueuedPlayers();
    }

    public boolean hasAnyPlayers() {
        return hasActivePlayers() || hasQueuedPlayers() || hasWaitingPlayers();
    }

    public int getNumOfObstacles(final PickupItemType obstacleType) {
        int count = 0;
        for(final PickupItem pickupItem : pickupItems) {
            if(pickupItem.getType() == obstacleType) count++;
        }
        return count;
    }

    public List<PickupItem> getPickupItems() {
        return pickupItems;
    }

    public void addPickupItem(final PickupItem pickupItem) {
        this.pickupItems.add(pickupItem);
    }


    public void removePickupItem(int i) { pickupItems.remove(i); }

    public GameLightState getLightState() {
        return new GameLightState(activePlayerEmailsToPositionAndDirections, queuedPlayers, lastUpdated, counter);
    }

    public GameFullState getFullState(final Grid grid) {
        return new GameFullState(activePlayerEmailsToPositionAndDirections, queuedPlayers, allPlayerEmailsToPlayers, grid, lastUpdated, counter);
    }

    public Map<String,PlayerPositionAndDirection> getPlayerPositionAndDirections() {
        return new HashMap<>(activePlayerEmailsToPositionAndDirections);
    }

    public PlayerPositionAndDirection getPlayerPositionAndDirection(final String playerEmail) {
        return activePlayerEmailsToPositionAndDirections.get(playerEmail);
    }

    public void setPlayerPositionAndDirection(final String playerEmail, final PlayerPositionAndDirection playerPositionAndDirection) {
        activePlayerEmailsToPositionAndDirections.put(playerEmail, playerPositionAndDirection);
    }

    public void increasePlayerPoints(final String playerEmail, final int value) {
        allPlayerEmailsToPlayers.get(playerEmail).addPoints(value);
    }

    public void decreasePlayerPoints(final String playerEmail, final int value) {
        allPlayerEmailsToPlayers.get(playerEmail).substractPoints(value);
    }

    /**
     * Returns the {@link Player}'s {@link Position} in the {@link Game}'s {@link Grid}.
     *
     * @param playerEmail the email of the {@link Player} to be checked
     * @return the {@link Player}'s {@link Position}
     */
    public Position getPosition(final String playerEmail) {
        return activePlayerEmailsToPositionAndDirections.get(playerEmail).getPosition();
    }

    /**
     * Returns the {@link Player}'s {@link Direction}, i.e. {@link Direction#NORTH},
     * {@link Direction#SOUTH}, {@link Direction#EAST}, {@link Direction#WEST}.
     *
     * @param playerEmail the email of the {@link Player} to be checked
     * @return the {@link Player}'s {@link Direction}
     */
    public Direction getDirection(final String playerEmail) {
        return activePlayerEmailsToPositionAndDirections.get(playerEmail).getDirection();
    }

    @Override
    public String toString() {
        final StringBuilder stringBuilder = new StringBuilder("GAME - id: ").append(id).append(" ~~ challengeId: ").append(challengeId).append(" @").append(hashCode());
        stringBuilder.append(" $ players[a/q/w]: ").append(activePlayers.size()).append("/").append(queuedPlayers.size()).append("/").append(waitingPlayers.size()).append("\n");
        stringBuilder.append("*active: ").append(activePlayers).append("\n");
        stringBuilder.append("*queued: ").append(queuedPlayers).append("\n");
        stringBuilder.append("*waiting: ").append(waitingPlayers).append("\n");
        return stringBuilder.toString();
    }

    public void reset() {
        pickupItems = new Vector<>();
    }

    private AudioEventListener audioEventListener = null;

    public void setOnAudioEventListener(AudioEventListener audioEventListener) {
        this.audioEventListener = audioEventListener;
    }

    public AudioEventListener getAudioEventListener() {
        return audioEventListener;
    }
}