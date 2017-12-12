package org.inspirecenter.amazechallenge.algorithms;

import org.inspirecenter.amazechallenge.model.Direction;
import org.inspirecenter.amazechallenge.model.Player;
import org.inspirecenter.amazechallenge.model.Game;

import java.io.Serializable;

/**
 * @author Nearchos
 *         Created: 19-Aug-17
 */
public abstract class AbstractMazeSolver implements MazeSolver {

    private final Game game;
    private final String playerEmail;

    protected AbstractMazeSolver(final Game game, final String playerEmail) {
        this.game = game;
        this.playerEmail = playerEmail;
    }

    public void setParameter(String name, Serializable value) {
        // must be over-written
    }

    public boolean canMoveForward() {
        return game.canMoveForward(playerEmail);
    }

    public boolean canMoveLeft() {
        return game.canMoveLeft(playerEmail);
    }

    public boolean canMoveRight() {
        return game.canMoveRight(playerEmail);
    }

    public boolean canMoveBackward() { return game.canMoveBackward(playerEmail); }

    public Direction getDirection() {
        return game.getDirection(playerEmail);
    }

    protected String getPlayerEmail() { return playerEmail; }

    protected Game getGame() { return game; }
}