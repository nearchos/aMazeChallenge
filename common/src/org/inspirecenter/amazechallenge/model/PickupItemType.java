package org.inspirecenter.amazechallenge.model;

/**
 * npaspallis on 16/01/2018.
 */

public enum PickupItemType {

    ITEM_REWARD_50_HEALTH("Reward +50 Health", Shape.PICKUPSHAPE_REWARD_50_HEALTH),
    ITEM_REWARD_DOUBLE_MOVES("Reward double moves for next 5 rounds", Shape.PICKUPSHAPE_REWARD_DOUBLE_MOVES),
    ITEM_REWARD_10_POINTS("Reward +10 Points", Shape.PICKUPSHAPE_REWARD_10_POINTS),

    ITEM_OBSTACLE_50_HEALTH("Lose -50 Health", Shape.PICKUPSHAPE_OBSTACLE_50_HEALTH),
    ITEM_OBSTACLE_SKIP_ROUND("Miss next round", Shape.PIKCUPSHAPE_OBSTACLE_SKIP_ROUND);

    private String name;
    private Shape shape;

    PickupItemType(final String name, final Shape shape) {
        this.name = name;
        this.shape = shape;
    }

    @Override
    public String toString() {
        return name;
    }

    public Shape getShape() { return shape; }

}
