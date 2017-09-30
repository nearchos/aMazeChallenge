package org.inspirecenter.amazechallenge.algorithms;

/**
 * @author Nearchos
 *         Created: 16-Aug-17
 */

public enum PlayerMove {

    TURN_CLOCKWISE("turn_clockwise"),
    TURN_COUNTERCLOCKWISE("turn_counter_clockwise"),
    MOVE_FORWARD("move_forward"),
    NO_MOVE("no_move");

    private final String code;

    PlayerMove(final String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return code;
    }
}