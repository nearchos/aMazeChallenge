package org.inspirecenter.amazechallenge.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GameFullState extends GameLightState {

    private Map<String,Player> playerEmailsToPlayers = new HashMap<>();
    private Grid grid;
    private long lastUpdated = 0;
    private long counter = 0;

    public GameFullState(
            final Map<String,PlayerPositionAndDirection> playerEmailsToPositionAndDirections,
            final List<String> queuedPlayerEmails,
            final Map<String,Player> playerEmailsToPlayers,
            final Grid grid,
            final long lastUpdated,
            final long counter) {
        super(playerEmailsToPositionAndDirections, queuedPlayerEmails);

        this.playerEmailsToPlayers = new HashMap<>(playerEmailsToPlayers);
        this.grid = grid;
        this.lastUpdated = lastUpdated;
        this.counter = counter;
    }

    public Set<String> getAllPlayerEmails() {
        return playerEmailsToPlayers.keySet();
    }

    public Player getPlayer(final String playerEmail) {
        return playerEmailsToPlayers.get(playerEmail);
    }

    public Map<String,Player> getAllPlayers() {
        return new HashMap<>(playerEmailsToPlayers);
    }

    public Grid getGrid() {
        return grid;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public long getCounter() {
        return counter;
    }
}