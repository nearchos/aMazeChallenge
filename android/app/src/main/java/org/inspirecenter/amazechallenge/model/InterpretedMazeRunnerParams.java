package org.inspirecenter.amazechallenge.model;

import org.inspirecenter.amazechallenge.algorithms.PlayerMove;

/**
 * Project: MazeChallenge
 * Created by Nicos Kasenides on 29-Sep-17 for Code Cyprus.
 */
public class InterpretedMazeRunnerParams {

    private PlayerMove move;
    private boolean justTurned;

    public PlayerMove getMove() {
        return move;
    }

    public void setMove(PlayerMove move) {
        this.move = move;
    }

    public boolean isJustTurned() {
        return justTurned;
    }

    public void setJustTurned(boolean justTurned) {
        this.justTurned = justTurned;
    }

    public InterpretedMazeRunnerParams(PlayerMove move, boolean justTurned) {
        this.move = move; this.justTurned = justTurned;
    }
}