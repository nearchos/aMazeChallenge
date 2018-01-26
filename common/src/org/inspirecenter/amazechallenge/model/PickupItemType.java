package org.inspirecenter.amazechallenge.model;

/**
 * npaspallis on 16/01/2018.
 */

public enum PickupItemType {

    ITEM_REWARD_50_HEALTH("Reward +50 Health"),
    ITEM_REWARD_DOUBLE_MOVES("Reward double moves for next 5 rounds"),
    ITEM_REWARD_10_POINTS("Reward +10 Points"),

    ITEM_OBSTACLE_50_HEALTH("Lose -50 Health"),
    ITEM_OBSTACLE_SKIP_ROUND("Miss next round"),

    ITEM_NONE("No item")

    ;

    private String name;

    PickupItemType(final String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }


}
