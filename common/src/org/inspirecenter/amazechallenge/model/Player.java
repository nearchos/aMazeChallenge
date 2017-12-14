package org.inspirecenter.amazechallenge.model;

import java.io.Serializable;

/**
 * @author Nearchos Paspallis
 * 17/08/2017.
 */
@com.googlecode.objectify.annotation.Entity
public class Player implements Serializable {

    @com.googlecode.objectify.annotation.Id
    private String email;

    private String name;
    private AmazeColor color;
    private AmazeIcon icon;
    private Shape shape;

    public Player() {
        super();
    }

    public Player(final String email, final String name, final AmazeColor color, final AmazeIcon icon, final Shape shape) {
        this();
        this.email = email;
        this.name = name;
        this.color = color;
        this.icon = icon;
        this.shape = shape;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public AmazeColor getColor() {
        return color;
    }

    public AmazeIcon getIcon() {
        return icon;
    }

    public Shape getShape() {
        return shape;
    }

    @Override
    public String toString() {
        return "Player{" +
                "email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", color=" + color +
                ", icon=" + icon +
                ", shape=" + shape +
                '}';
    }
}