package org.inspirecenter.amazechallenge.algorithms;

import java.util.Random;

/**
 * @author Nearchos
 *         Created: 16-Aug-17
 */

public enum PlayerMove {

    TURN_CLOCKWISE("tc", "turn_clockwise"),
    TURN_COUNTERCLOCKWISE("tcc", "turn_counter_clockwise"),
    MOVE_FORWARD("mf", "move_forward"),
    NO_MOVE("nm", "no_move");

    private final String code;
    private final String name;

    PlayerMove(final String code, final String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    @Override
    public String toString() {
        return name;
    }

    private static Random random = new Random();

    public static PlayerMove randomPlayerMove() {
        final PlayerMove [] allPlayerMoves = values();
        return allPlayerMoves[random.nextInt(allPlayerMoves.length)];
    }
}