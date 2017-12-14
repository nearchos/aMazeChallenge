package org.inspirecenter.amazechallenge.algorithms;

import org.inspirecenter.amazechallenge.controller.RuntimeController;
import org.inspirecenter.amazechallenge.model.*;

import java.io.Serializable;

/**
 * @author Nearchos
 *         Created: 19-Aug-17
 */
public abstract class AbstractMazeSolver implements MazeSolver {

    final String playerEmail;

    AbstractMazeSolver(final String playerEmail) {
        super();
        this.playerEmail = playerEmail;
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
}