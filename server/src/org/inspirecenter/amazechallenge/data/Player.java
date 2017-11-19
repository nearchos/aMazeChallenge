package org.inspirecenter.amazechallenge.data;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

import java.io.Serializable;

@Entity
public class Player implements Serializable {

    @Id private String email;
    private String name;
    private AmazeColor color;
    private Shape shape;

    public Player() {
        super();
    }

    public Player(String email, String name, AmazeColor color, Shape shape) {
        this.email = email;
        this.name = name;
        this.color = color;
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

    public Shape getShape() {
        return shape;
    }
}