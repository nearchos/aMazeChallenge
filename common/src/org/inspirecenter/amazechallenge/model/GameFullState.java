package org.inspirecenter.amazechallenge.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GameFullState extends GameLightState {

    private final Map<String,Player> playerIDsToPlayers;
    private final Grid grid;

    public GameFullState(
            final Map<String,PlayerPositionAndDirection> playerIDsToPositionAndDirections,
            final List<String> queuedPlayerIds,
            final List<Pickable> pickables,
            final Map<String,Player> playerIDsToPlayers,
            final Grid grid,
            final long lastUpdated,
            final long counter) {
        super(playerIDsToPositionAndDirections, queuedPlayerIds, pickables, lastUpdated, counter);

        this.playerIDsToPlayers = new HashMap<>(playerIDsToPlayers);
        this.grid = grid;
    }

    public Set<String> getAllPlayerIDs() {
        return playerIDsToPlayers.keySet();
    }

    public Player getPlayerById(final String playerId) {
        return playerIDsToPlayers.get(playerId);
    }

    public Map<String,Player> getAllIDsToPlayers() {
        return new HashMap<>(playerIDsToPlayers);
    }

    public Grid getGrid() {
        return grid;
    }

    @Override
    public String toString() {
        return "GameFullState{" +
                "playerIDsToPlayers=" + playerIDsToPlayers +
                ", grid=" + grid +
                "} extends: " + super.toString();
    }
}