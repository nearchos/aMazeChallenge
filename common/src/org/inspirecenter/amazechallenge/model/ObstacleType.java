package org.inspirecenter.amazechallenge.model;

/**
 * npaspallis on 16/01/2018.
 */

public enum ObstacleType {

    OBSTACLE_HEALTH("health"),
    OBSTACLE_SKIP_ROUND("skip-round");

    private String name;

    ObstacleType(final String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
