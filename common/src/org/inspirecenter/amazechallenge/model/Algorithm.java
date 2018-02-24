package org.inspirecenter.amazechallenge.model;

public enum Algorithm {

    SINGLE_SOLUTION("Single Solution"),
    MANY_SOLUTIONS("Many Solutions"),
    SPARSE("Sparse"),
    EMPTY("Empty");

    private final String friendlyName;

    Algorithm(String friendlyName) {
        this.friendlyName = friendlyName;
    }

    public String getFriendlyName() {
        return friendlyName;
    }

    public static int getPosition(Algorithm algorithm) {
        final Algorithm [] algorithms = values();
        for (int i = 0; i < algorithms.length; i++) {
            if (algorithm == algorithms[i]) return i;
        }
        throw new RuntimeException("The algorithm '" + algorithm + "' is not a valid algorithm.");
    }
}