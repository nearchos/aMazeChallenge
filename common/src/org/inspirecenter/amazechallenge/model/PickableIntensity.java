package org.inspirecenter.amazechallenge.model;

/**
 * @author Nearchos
 *         Created: 18-Feb-18
 */

public enum PickableIntensity {

    NONE(0),
    LOW(1),
    MEDIUM(2),
    HIGH(3)

    ;

    final int id;

    PickableIntensity(int id) {
        this.id = id;
    }

    public int getID() { return id; }

    public static PickableIntensity getOptionFromID(int id) {
        if (id == 0) return NONE;
        if (id == 1) return LOW;
        if (id == 2) return MEDIUM;
        if (id == 3)  return HIGH;
        throw new RuntimeException("Invalid PickableIntensity id.");
    }
}