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

    private Map<String,PlayerPositionAndDirection> activePlayerPositionsAndDirections = new HashMap<>();
    private List<String> queuedPlayerEmails = new Vector<>();

    GameLightState(final Map<String,PlayerPositionAndDirection> playerEmailsToPositionsAndDirections, final List<String> queuedPlayerEmails) {
        super();

        activePlayerPositionsAndDirections.putAll(playerEmailsToPositionsAndDirections);

        this.queuedPlayerEmails.addAll(queuedPlayerEmails);
    }

    public Map<String, PlayerPositionAndDirection> getActivePlayerPositionsAndDirections() {
        return activePlayerPositionsAndDirections;
    }

    public List<String> getActivePlayerEmails() {
        return new Vector<>(activePlayerPositionsAndDirections.keySet());
    }

    public List<String> getQueuedPlayerEmails() {
        return queuedPlayerEmails;
    }
}
