package org.inspirecenter.amazechallenge.ui;

import org.inspirecenter.amazechallenge.R;

public enum MazeBackground {

    BACKGROUND_GRASS("texture_grass", R.drawable.texture_grass),
    BACKGROUND_METAL("texture_metal", R.drawable.texture_metal),
    BACKGROUND_LAVA("texture_lava", R.drawable.texture_lava)

    ;

    private String resourceName;
    private int resourceID;

    private MazeBackground(String resourceName, int resourceID) {
        this.resourceID = resourceID;
        this.resourceName = resourceName;
    }

    @Override
    public String toString() {
        return resourceName;
    }

    public int getResourceID() {
        return resourceID;
    }

    public String getResourceName() {
        return resourceName;
    }

    public static MazeBackground getByName(String backgroundName) {
        for (MazeBackground b : MazeBackground.values()) {
            if (b.getResourceName().equals(backgroundName)) return b;
        }
        throw new RuntimeException("The background you have tried to use called \"" + backgroundName + "\" is not being defined as a MazeBackground enum type. You must define maze backgrounds in the MazeBackground class under the UI Package.");
    }

}
