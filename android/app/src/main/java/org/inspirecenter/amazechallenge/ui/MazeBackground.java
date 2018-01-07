package org.inspirecenter.amazechallenge.ui;

import org.inspirecenter.amazechallenge.R;

public enum MazeBackground {

    BACKGROUND_GRASS("texture_grass", R.drawable.texture_grass),
    BACKGROUND_METAL("texture_metal", R.drawable.texture_metal)

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
        return null; //TODO IF NOT FOUND.
    }

}
