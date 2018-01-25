package org.inspirecenter.amazechallenge.model;

/**
 * @author Nearchos Paspallis
 * 17/08/2017.
 */
public enum Shape {

    //Player Shapes:
    CIRCLE("circle"),
    EMPTY_CIRCLE("empty_circle"),
    TRIANGLE("triangle"),

    //Pickup item shapes:
    PICKUPSHAPE_REWARD_50_HEALTH("Reward 50 health shape"),
    PICKUPSHAPE_REWARD_DOUBLE_MOVES("Double moves shape"),
    PICKUPSHAPE_REWARD_10_POINTS("Reward 10 points shape"),
    PICKUPSHAPE_OBSTACLE_50_HEALTH("Lose 50 health shape"),
    PIKCUPSHAPE_OBSTACLE_SKIP_ROUND("Skip next round shape")

    ;

    private final String code;

    Shape(final String code) {
        this.code = code;
    }

    //TODO: Is this needed??
    public static Shape getShapeByCode(final String shapeCode) {
        for(final Shape shape : values()) {
            if (shape.code.equals(shapeCode)) {
                return shape;
            }
        }
        return TRIANGLE; // return triangle by default
    }

    @Override
    public String toString() {
        return code;
    }
}
