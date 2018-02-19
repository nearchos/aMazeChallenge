package org.inspirecenter.amazechallenge.model;

public enum Algorithm {

    SINGLE_SOLUTION,
    MANY_SOLUTIONS,
    SPARSE,
    EMPTY;

    public static int getPosition(Algorithm algorithm) {
        final Algorithm [] algorithms = values();
        for (int i = 0; i < algorithms.length; i++) {
            if (algorithm == algorithms[i]) return i;
        }
        throw new RuntimeException("The algorithm '" + algorithm + "' is not a valid algorithm.");
    }
}