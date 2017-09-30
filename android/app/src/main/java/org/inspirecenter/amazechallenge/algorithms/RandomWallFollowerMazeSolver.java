package org.inspirecenter.amazechallenge.algorithms;

import org.inspirecenter.amazechallenge.model.Game;
import org.inspirecenter.amazechallenge.model.Player;

import java.util.Random;

/**
 * @author Nearchos
 *         Created: 19-Aug-17
 */

public class RandomWallFollowerMazeSolver extends AbstractMazeSolver {

    public static final int MIN_STEPS_BEFORE_DIRECTION_SWITCH = 10;
    public static final int MAX_STEPS_BEFORE_DIRECTION_SWITCH = 50;

    private Random random = new Random();

    public RandomWallFollowerMazeSolver(final Game game, final Player player) {
        super(game, player);
    }

    private boolean justTurned = false;

    private boolean directionLeft = true;
    private int stepsBeforeDirectionSwitch = MIN_STEPS_BEFORE_DIRECTION_SWITCH + random.nextInt(MAX_STEPS_BEFORE_DIRECTION_SWITCH - MIN_STEPS_BEFORE_DIRECTION_SWITCH);

    @Override
    public PlayerMove getNextMove() {
        if(stepsBeforeDirectionSwitch-- == 0) {
            stepsBeforeDirectionSwitch = random.nextInt(MAX_STEPS_BEFORE_DIRECTION_SWITCH);
            directionLeft = !directionLeft;
        }

        if(justTurned && canMoveForward()) {
            justTurned = false;
            return PlayerMove.MOVE_FORWARD;
        }

        final boolean directionLeft = Math.random() > 0.5d;
        if(directionLeft && canMoveLeft()) {
            justTurned = true;
            return PlayerMove.TURN_COUNTERCLOCKWISE;
        } else if(canMoveRight()) {
            justTurned = true;
            return PlayerMove.TURN_CLOCKWISE;
        } else if(canMoveForward()) {
            return PlayerMove.MOVE_FORWARD;
        } else {
            justTurned = true;
            return PlayerMove.TURN_CLOCKWISE;
        }
    }

    @Override
    public String toString() {
        return "Left Wall Follower";
    }
}
