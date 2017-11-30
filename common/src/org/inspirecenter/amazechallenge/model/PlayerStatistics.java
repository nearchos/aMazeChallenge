package org.inspirecenter.amazechallenge.model;

import org.inspirecenter.amazechallenge.algorithms.PlayerMove;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * "author Nearchos Paspallis
 * 24/11/2017.
 */
public class PlayerStatistics implements Serializable {

    private Map<PlayerMove,Integer> numOfMoves;

    PlayerStatistics() {
        super();

        this.numOfMoves = new HashMap<>();
        for(final PlayerMove playerMove : PlayerMove.values()) {
            numOfMoves.put(playerMove,0);
        }
    }

    void increaseMoves(final PlayerMove playerMove) {
        numOfMoves.put(playerMove, numOfMoves.get(playerMove) + 1);
    }

    public int getNumOfMoves() {
        int totalNumOfMoves = 0;
        for(final PlayerMove playerMove : PlayerMove.values()) {
            totalNumOfMoves += this.numOfMoves.get(playerMove);
        }
        return totalNumOfMoves;
    }

    public int getNumOfMoves(final PlayerMove playerMove) {
        return numOfMoves.get(playerMove);
    }
}