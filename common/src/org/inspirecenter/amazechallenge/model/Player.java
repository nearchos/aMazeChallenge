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

//    private Position position;
//    private Direction direction;

    public Player() {
        super();
    }

    public Player(final String email, final String name, final AmazeColor color, final AmazeIcon icon, final Shape shape, final Position startingPosition) {
        this();
        this.email = email;
        this.name = name;
        this.color = color;
        this.icon = icon;
        this.shape = shape;
//        this.position = startingPosition;
//        this.direction = NORTH;
    }

    public Player(final String email, final String name, final AmazeColor color, final AmazeIcon icon, final Shape shape) {
        this(email, name, color, icon, shape, new Position(0, 0));
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

//    void init(final Position position, final Direction direction) {
//        this.position = position;
//        this.direction = direction;
//    }

//    public Position getPosition() {
//        return position;
//    }
//
//    public Direction getDirection() {
//        return direction;
//    }

//    public void turnClockwise() {
//        this.direction = direction.turnClockwise();
//    }

//    public void turnCounterClockwise() {
//        this.direction = direction.turnCounterClockwise();
//    }

//    public void moveForward() {
//        switch (direction) {
//            case NORTH:
//                position = position.moveNorth();
//                break;
//            case SOUTH:
//                position = position.moveSouth();
//                break;
//            case WEST:
//                position = position.moveWest();
//                break;
//            case EAST:
//                position = position.moveEast();
//                break;
//            default: throw new RuntimeException("Invalid direction: " + direction);
//        }
//    }

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