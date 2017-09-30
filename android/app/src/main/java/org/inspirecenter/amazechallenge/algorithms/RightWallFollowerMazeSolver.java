package org.inspirecenter.amazechallenge.algorithms;

import org.inspirecenter.amazechallenge.model.Player;
import org.inspirecenter.amazechallenge.model.Game;

import static org.inspirecenter.amazechallenge.algorithms.PlayerMove.MOVE_FORWARD;
import static org.inspirecenter.amazechallenge.algorithms.PlayerMove.TURN_CLOCKWISE;
import static org.inspirecenter.amazechallenge.algorithms.PlayerMove.TURN_COUNTERCLOCKWISE;

/**
 * (left) wall follower: https://en.wikipedia.org/wiki/Maze_solving_algorithm#Wall_follower
 * @author Nearchos
 *         Created: 16-Aug-17
 */
public class RightWallFollowerMazeSolver extends AbstractMazeSolver {

    public RightWallFollowerMazeSolver(final Game game, final Player player) {
        super(game, player);
    }

    private boolean justTurned = false;

    @Override
    public PlayerMove getNextMove() {
        if(justTurned && canMoveForward()) {
            justTurned = false;
            return MOVE_FORWARD;
        }

        if(canMoveRight()) {
            justTurned = true;
            return TURN_CLOCKWISE;
        } else if(canMoveForward()) {
            return MOVE_FORWARD;
        } else {
            justTurned = true;
            return TURN_COUNTERCLOCKWISE;
        }
    }

    @Override
    public String toString() {
        return "Right Wall Follower";
    }
}