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
    private final Player player;

    protected AbstractMazeSolver(final Game game, final Player player) {
        this.game = game;
        this.player = player;
    }

    public void setParameter(String name, Serializable value) {
        // must be over-written
    }

    public boolean canMoveForward() {
        return game.canMoveForward(player);
    }

    public boolean canMoveLeft() {
        return game.canMoveLeft(player);
    }

    public boolean canMoveRight() {
        return game.canMoveRight(player);
    }

    public boolean canMoveBackward() { return game.canMoveBackward(player); }

    public Direction getDirection() {
        return game.getDirection(player);
    }

    protected Player getPlayer() { return player; }

    protected Game getGame() { return game; }

}