package org.inspirecenter.amazechallenge.model;

import org.inspirecenter.amazechallenge.R;

public enum AmazeIcon {

    ICON_1("Icon_1", R.drawable.icon_1),
    ICON_2("Icon_2", R.drawable.icon_2),
    ICON_3("Icon_3", R.drawable.icon_3),
    ICON_4("Icon_4", R.drawable.icon_4),
    ICON_5("Icon_5", R.drawable.icon_5),
    ICON_6("Icon_6", R.drawable.icon_6),
    ICON_7("Icon_7", R.drawable.icon_7),
    ICON_8("Icon_8", R.drawable.icon_8),
    ICON_9("Icon_9", R.drawable.icon_9),
    ICON_10("Icon_10", R.drawable.icon_10);

    private final String name;
    private final int resourceID;

    public String getName() {
        return name;
    }

    public int getResourceID() { return resourceID; }

    AmazeIcon(final String name, final int resourceID) {
        this.name = name;
        this.resourceID = resourceID;
    }//end AmazeIcon()

    public static AmazeIcon getByName(final String name) {
        for(final AmazeIcon amazeIcon : values()) {
            if(amazeIcon.name.equalsIgnoreCase(name)) return amazeIcon;
        }//end for
        return ICON_1;
    }//end getByName()

    @Override
    public String toString() {
        return name;
    }

}//end enum AmazeIcon
