package org.inspirecenter.amazechallenge.model;

/**
 * @deprecated  todo remove
 */
public enum PickupItemImage {

    PICKUP_ITEM_IMAGE_BOMB("Bomb"),
    PICKUP_ITEM_IMAGE_APPLE("Apple"),
    PICKUP_ITEM_IMAGE_ORANGE("Orange"),
    PICKUP_ITEM_IMAGE_STRAWBERRY("Strawberry"),
    PICKUP_ITEM_IMAGE_BANANA("Banana"),
    PICKUP_ITEM_IMAGE_GRAPES("Grapes"),
    PICKUP_ITEM_IMAGE_WATERMELON("Watermelon"),
    PICKUP_ITEM_IMAGE_PEACH("Peach"),
    PICKUP_ITEM_IMAGE_GIFTBOX("Gift box"),
    PICKUP_ITEM_IMAGE_TRAP("Trap"),
    PICKUP_ITEM_IMAGE_DOUBLE_MOVES("Double moves"),
    PICKUP_ITEM_IMAGE_NONE("None")
    ;

    private String name;

    PickupItemImage(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
