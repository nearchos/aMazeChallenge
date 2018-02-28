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
    final String playerID;

    AbstractMazeSolver(final Challenge challenge, final Game game, final String playerID) {
        super();
        init(challenge, game);
        this.playerID = playerID;
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

    public Direction onMove(PlayerMove playerMove) {
        switch (getDirection()) {
            case NORTH:
                switch (playerMove) {
                    case TURN_CLOCKWISE:
                        return Direction.EAST;
                    case TURN_COUNTERCLOCKWISE:
                        return Direction.WEST;
                    case MOVE_FORWARD:
                    case NO_MOVE:
                        return getDirection();
                }
                break;
            case SOUTH:
                switch (playerMove) {
                    case TURN_CLOCKWISE:
                        return Direction.WEST;
                    case TURN_COUNTERCLOCKWISE:
                        return Direction.EAST;
                    case MOVE_FORWARD:
                    case NO_MOVE:
                        return getDirection();
                }
                break;
            case WEST:
                switch (playerMove) {
                    case TURN_CLOCKWISE:
                        return Direction.NORTH;
                    case TURN_COUNTERCLOCKWISE:
                        return Direction.SOUTH;
                    case MOVE_FORWARD:
                    case NO_MOVE:
                        return getDirection();
                }
                break;
            case EAST:
                switch (playerMove) {
                    case TURN_CLOCKWISE:
                        return Direction.SOUTH;
                    case TURN_COUNTERCLOCKWISE:
                        return Direction.NORTH;
                    case MOVE_FORWARD:
                    case NO_MOVE:
                        return getDirection();
                }
                break;
        }
        return Direction.NORTH;
    }

}