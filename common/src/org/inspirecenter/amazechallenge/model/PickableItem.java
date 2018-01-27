package org.inspirecenter.amazechallenge.model;

import java.io.Serializable;

/**
 * npaspallis on 16/01/2018.
 */
@com.googlecode.objectify.annotation.Entity
public class PickableItem implements Serializable {

    @com.googlecode.objectify.annotation.Id
    Long id;

    private Position position;
    private PickableType pickableType;
    private int state;

    public PickableItem() {
        super();
    }

    public PickableItem(Position position, PickableType pickableType) {
        this.position = position;
        this.pickableType = pickableType;
        state = pickableType.getDefaultState();
    }

    public Long getId() {
        return id;
    }

    public Position getPosition() {
        return position;
    }

    public PickableType getPickableType() { return pickableType; }

    public int getState() { return state; }

    public void reduceState() {
        if (state > 0) state--;
    }

}