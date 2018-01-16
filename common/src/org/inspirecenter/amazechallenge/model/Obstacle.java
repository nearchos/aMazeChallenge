package org.inspirecenter.amazechallenge.model;

import java.io.Serializable;

/**
 * npaspallis on 16/01/2018.
 */
@com.googlecode.objectify.annotation.Entity
public class Obstacle implements Serializable {

    @com.googlecode.objectify.annotation.Id
    Long id;

    private Position position;
    private ObstacleType type;

    public Obstacle() {
        super();
    }

    public Obstacle(Position position, ObstacleType type) {
        this.position = position;
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public Position getPosition() {
        return position;
    }

    public ObstacleType getType() {
        return type;
    }
}