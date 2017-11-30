package org.inspirecenter.amazechallenge.data;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import org.inspirecenter.amazechallenge.model.Position;

@Entity
public class PlayerState {

    @Id public Long id;
    public Position position;
    // public Direction direction; // todo

    public PlayerState() {
        super();
    }

    public PlayerState(final Position position) {
        this.position = position;
    }

    public Long getId() {
        return id;
    }

    public Position getPosition() {
        return position;
    }
}