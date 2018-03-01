package org.inspirecenter.amazechallenge.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * This is the minimum state needed to be communicated over the network to allow the clients to visualize the game
 * state. It includes the list of players (active and queued) as well as their positions and directions.
 */
public class GameLightState implements Serializable {

    private Map<String,PlayerPositionAndDirection> activeIDsToPlayerPositionsAndDirections = new HashMap<>();
    private List<String> queuedPlayerIDs = new Vector<>();
    private List<String> waitingPlayerIDs = new Vector<>();
    private List<Pickable> pickables = new Vector<>();

    private long lastUpdated = 0;
    private long counter = 0;

    GameLightState(final Map<String,PlayerPositionAndDirection> playerIDsToPositionsAndDirections,
                   final List<String> queuedPlayerIDs,
                   final List<Pickable> pickables,
                   final long lastUpdated,
                   final long counter) {
        super();

        activeIDsToPlayerPositionsAndDirections.putAll(playerIDsToPositionsAndDirections);
        this.queuedPlayerIDs.addAll(queuedPlayerIDs);
        this.pickables.addAll(pickables);

        this.lastUpdated = lastUpdated;
        this.counter = counter;
    }

    public Map<String, PlayerPositionAndDirection> getActiveIDsToPlayerPositionsAndDirections() {
        return activeIDsToPlayerPositionsAndDirections;
    }

    public List<String> getActivePlayerIDs() {
        return new Vector<>(activeIDsToPlayerPositionsAndDirections.keySet());
    }

    public List<String> getQueuedPlayerIDs() {
        return queuedPlayerIDs;
    }

    public List<String> getWaitingPlayerIDs() {
        return waitingPlayerIDs;
    }

    public List<Pickable> getPickables() {
        return pickables;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public long getCounter() {
        return counter;
    }

    @Override
    public String toString() {
        return "GameLightState{" +
                "activeIDsToPlayerPositionsAndDirections=" + activeIDsToPlayerPositionsAndDirections +
                ", queuedPlayerIDs=" + queuedPlayerIDs +
                ", waitingPlayerIDs=" + waitingPlayerIDs +
                ", pickables=" + pickables +
                ", lastUpdated=" + lastUpdated +
                ", counter=" + counter +
                '}';
    }
}