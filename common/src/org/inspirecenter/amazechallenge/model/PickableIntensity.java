package org.inspirecenter.amazechallenge.model;

/**
 * @author Nearchos
 *         Created: 18-Feb-18
 */

public enum PickableIntensity {

    LOW(0, "low"),
    MEDIUM(1, "medium"),
    HIGH(2, "high")
    ;

    final int id;
    final String textID;

    PickableIntensity(int id, String textID) {
        this.id = id;
        this.textID = textID;
    }

    public int getID() { return id; }

    public String getTextID() { return textID; }

    public static PickableIntensity getOptionFromID(int id) {
        if (id == 0) return LOW;
        if (id == 1) return MEDIUM;
        if (id == 2) return HIGH;
        throw new RuntimeException("Invalid PickableIntensity id.");
    }

    public static PickableIntensity fromTextID(String textID) {
        for (final PickableIntensity pickableIntensity : values()) {
            if (pickableIntensity.textID.equals(textID)) return pickableIntensity;
        }
        throw new RuntimeException("Invalid PickableIntensity text ID -> " + textID);
    }
}