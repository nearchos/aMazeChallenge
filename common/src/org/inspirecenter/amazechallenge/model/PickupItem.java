package org.inspirecenter.amazechallenge.model;

import java.io.Serializable;

/**
 * npaspallis on 16/01/2018.
 */
@com.googlecode.objectify.annotation.Entity
public class PickupItem implements Serializable { // todo rename Pickable

    @com.googlecode.objectify.annotation.Id
    Long id;

    private Position position;
    private PickupItemType type;
    private PickupItemImage image;
    private int state;

    public PickupItem() {
        super();
    }

    public PickupItem(Position position, PickupItemType type, PickupItemImage image) {
        this.position = position;
        this.type = type;
        this.image = image;
    }

    public Long getId() {
        return id;
    }

    public Position getPosition() {
        return position;
    }

    public PickupItemType getType() { return type; }

    public PickupItemImage getImage() { return image; }

}