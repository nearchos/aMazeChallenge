package org.inspirecenter.amazechallenge.algorithms;

import java.io.Serializable;
import java.util.Map;

/**
 * @author Nearchos
 *         Created: 16-Aug-17
 */

public interface MazeSolver extends Serializable {

    Map<String,Serializable> getState();

    void setState(Map<String,Serializable> stateMap);

    PlayerMove getNextMove();
}