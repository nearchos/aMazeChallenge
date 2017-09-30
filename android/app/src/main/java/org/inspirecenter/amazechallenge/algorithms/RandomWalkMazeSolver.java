package org.inspirecenter.amazechallenge.algorithms;

import org.inspirecenter.amazechallenge.model.Player;
import org.inspirecenter.amazechallenge.model.Game;

import java.util.Random;

/**
 * @author Nearchos
 *         Created: 16-Aug-17
 */
public class RandomWalkMazeSolver extends AbstractMazeSolver {

    private final Random random = new Random();

    public RandomWalkMazeSolver(final Game game, final Player player) {
        super(game, player);
    }

    private boolean justTurned = false;

    @Override
    public PlayerMove getNextMove() {
        if(justTurned) {
            justTurned = false;
            if(canMoveForward()) return PlayerMove.MOVE_FORWARD;
        }

        final int randomChoice = random.nextInt(3);
        switch (randomChoice) {
            case 0:
                justTurned = true;
                return PlayerMove.TURN_CLOCKWISE;
            case 1:
                justTurned = true;
                return PlayerMove.TURN_COUNTERCLOCKWISE;
            case 2:
                return PlayerMove.MOVE_FORWARD;
            default:
                throw new RuntimeException("Invalid random code (must be 0..2): " + randomChoice);
        }
    }

    @Override
    public String toString() {
        return "Random Walk ";
    }
}