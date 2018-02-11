package org.inspirecenter.amazechallenge.algorithms;

import org.inspirecenter.amazechallenge.controller.RuntimeController;
import org.inspirecenter.amazechallenge.model.*;

/**
 * @author Nearchos
 *         Created: 19-Aug-17
 */
public abstract class AbstractMazeSolver implements MazeSolver {

    protected transient Challenge challenge;
    protected transient Game game;
    final String playerEmail;

    AbstractMazeSolver(final Challenge challenge, final Game game, final String playerEmail) {
        super();
        init(challenge, game);
        this.playerEmail = playerEmail;
    }

    @Override
    public void init(Challenge challenge, Game game) {
        this.challenge = challenge;
        this.game = game;
    }

    abstract Grid getGrid();
    abstract Direction getDirection();
    abstract Position getPosition();

    public boolean canMoveForward() {
        return RuntimeController.canMoveForward(getGrid(), getPosition(), getDirection());
    }

    public boolean canMoveBackward() {
        return RuntimeController.canMoveBackward(getGrid(), getPosition(), getDirection());
    }

    public boolean canMoveLeft() {
        return RuntimeController.canMoveLeft(getGrid(), getPosition(), getDirection());
    }

    public boolean canMoveRight() {
        return RuntimeController.canMoveRight(getGrid(), getPosition(), getDirection());
    }

    public PickableType.Bias look(Direction direction) {
        return RuntimeController.look(game, getGrid(), getPosition(), direction);
    }

    public Direction compass() {
        return RuntimeController.compass(getGrid().getTargetPosition(), getPosition());
    }
}