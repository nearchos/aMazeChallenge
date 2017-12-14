package org.inspirecenter.amazechallenge.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GameFullState extends GameLightState {

    private Map<String,Player> playerEmailsToPlayers = new HashMap<>();
    private Grid grid;

    public GameFullState(
            final Map<String,PlayerPositionAndDirection> playerEmailsToPositionAndDirections,
            final List<String> queuedPlayerEmails,
            final Map<String,Player> playerEmailsToPlayers,
            final Grid grid,
            final long lastUpdated,
            final long counter) {
        super(playerEmailsToPositionAndDirections, queuedPlayerEmails, lastUpdated, counter);

        this.playerEmailsToPlayers = new HashMap<>(playerEmailsToPlayers);
        this.grid = grid;
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
}