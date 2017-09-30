package org.inspirecenter.amazechallenge.algorithms;

import java.io.Serializable;

/**
 * @author Nearchos
 *         Created: 16-Aug-17
 */

public interface MazeSolver extends Serializable {

    public void setParameter(final String name, final Serializable  value);

    public PlayerMove getNextMove();
}