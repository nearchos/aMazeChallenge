package org.inspirecenter.amazechallenge.algorithms;

import com.sun.org.apache.xpath.internal.operations.Bool;
import org.inspirecenter.amazechallenge.model.*;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class LeftWallFollower extends AbstractMazeSolver {

    private final Challenge challenge;
    private final Game game;

    public LeftWallFollower(final Challenge challenge, final Game game, final String playerEmail) {
        super(playerEmail);
        this.challenge = challenge;
        this.game = game;
    }

    @Override
    Grid getGrid() {
        return challenge.getGrid();
    }

    @Override
    Direction getDirection() {
        return game.getDirection(playerEmail);
    }

    @Override
    Position getPosition() {
        return game.getPosition(playerEmail);
    }

    @Override
    public Map<String, Serializable> getState() {
        final Map<String, Serializable> state = new HashMap<>();
        state.put("justTurned", justTurned);
        return state;
    }

    @Override
    public void setState(Map<String, Serializable> stateMap) {
        justTurned = stateMap.containsKey("justTurned") ? (Boolean) stateMap.get("justTurned") : false;
    }

    private boolean justTurned = false;

    @Override
    public PlayerMove getNextMove() {
        if(justTurned) {
            justTurned = false;
            return PlayerMove.MOVE_FORWARD;
        } else {
            if(canMoveLeft()) {
                justTurned = true;
                return PlayerMove.TURN_COUNTERCLOCKWISE;
            } else if(canMoveForward()) {
                return PlayerMove.MOVE_FORWARD;
            } else {
                justTurned = true;
                return PlayerMove.TURN_CLOCKWISE;
            }
        }
    }
}