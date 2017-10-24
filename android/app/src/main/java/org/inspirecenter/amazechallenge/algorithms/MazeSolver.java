package org.inspirecenter.amazechallenge.algorithms;

import java.io.Serializable;

/**
 * @author Nearchos
 *         Created: 16-Aug-17
 */

public interface MazeSolver extends Serializable {

    void setParameter(final String name, final Serializable  value);

    PlayerMove getNextMove();
}