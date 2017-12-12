package org.inspirecenter.amazechallenge.model;

import java.io.Serializable;

/**
 * @author Nearchos
 *         Created: 11-Dec-17
 */

public class PlayerPositionAndDirection implements Serializable {

    private Position position;
    private Direction direction;

    public PlayerPositionAndDirection() {
        super();
        /* empty */
    }

    public PlayerPositionAndDirection(final Position position, final Direction direction) {
        this();
        this.position = position;
        this.direction = direction;
    }

    public Position getPosition() {
        return position;
    }

    public Direction getDirection() {
        return direction;
    }

    @Override
    public String toString() {
        return "[" + position + "/" + direction + "]";
    }
}
