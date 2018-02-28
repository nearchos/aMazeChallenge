package org.inspirecenter.amazechallenge.model;

import java.io.Serializable;

/**
 * @author Nearchos Paspallis
 * 17/08/2017.
 */
@com.googlecode.objectify.annotation.Entity
public class Player implements Serializable {

    @com.googlecode.objectify.annotation.Id
    private String id;

    private String email;
    private String name;
    private AmazeColor color;
    private AmazeIcon icon;
    private Shape shape;
    private Health health; // todo must be part of game, not player
    private int points = 0; // todo must be part of game, not player

    public Player() {
        super();
    }

    public Player(final String id, final String email, final String name, final AmazeColor color, final AmazeIcon icon, final Shape shape) {
        this();
        this.id = id;
        this.email = email;
        this.name = name;
        this.color = color;
        this.icon = icon;
        this.shape = shape;
        health = new Health();
    }

    public String getId() {
        return id;
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

    public Health getHealth() { return health; }

    public void setHealth(Health health) { this.health = health; } // todo move to Game

    public void increaseHealth(int amount) {
        if (amount < 0) throw new RuntimeException("Cannot increase health by negative amount.");
        else {
            Health health = this.getHealth();
            health.increaseBy(amount);
            this.health = health;
        }
    }

    public void decreaseHealth(int amount) {
        if (amount < 0) throw new RuntimeException("Cannot decrease health by negative amount.");
        else {
            Health health = this.getHealth();
            health.decreaseBy(amount);
            this.health = health;
        }
    }

    @Override
    public String toString() {
        return "Player{" +
                "id='" + id + '\'' +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", color=" + color +
                ", icon=" + icon +
                ", shape=" + shape +
                ", health=" + health +
                '}';
    }

    public int getPoints() { return points; } // todo move to Game

    public void changePointsBy(int amount) {
        if (points + amount < 0) points = 0;
        else points += amount;
    }

    public void subtractPoints(int points) {
        if (points < 0) throw new RuntimeException("Cannot substract negative points.");
        else this.points -= points;
    }

    public void addPoints(int points) {
        if (points < 0) throw new RuntimeException("Cannot add negative points.");
        else this.points += points;
    }

    private void resetPoints() {
        points = 0;
    }

    public void reset() {
        health.resetHealth();
        resetPoints();
    }
}