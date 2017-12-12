package org.inspirecenter.amazechallenge.model;

public enum AmazeIcon {

    ICON_1("icon_1", "Cool name 1"),
    ICON_2("icon_2", "Cool name 2"),
    ICON_3("icon_3", "Cool name 3"),
    ICON_4("icon_4", "Cool name 4"),
    ICON_5("icon_5", "Cool name 5"),
    ICON_6("icon_6", "Cool name 6"),
    ICON_7("icon_7", "Cool name 7"),
    ICON_8("icon_8", "Cool name 8"),
    ICON_9("icon_9", "Cool name 9"),
    ICON_10("icon_10", "Cool name 10");

    private final String name;
    private final String description;

    public String getName() {
        return name;
    }

    public String getResourceName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    AmazeIcon(final String name, final String description) {
        this.name = name;
        this.description = description;
    }

    public static AmazeIcon getDefault() {
        return ICON_1;
    }

    public static AmazeIcon getByName(final String name) {
        for(final AmazeIcon amazeIcon : values()) {
            if(amazeIcon.name.equalsIgnoreCase(name)) return amazeIcon;
        }
        return ICON_1;
    }

    public static int getIndex(final AmazeIcon amazeIcon) {
        final AmazeIcon [] amazeIcons = values();
        for(int i = 0; i < amazeIcons.length; i++) {
            if(amazeIcons[i] == amazeIcon) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public String toString() {
        return name;
    }

}//end enum AmazeIcon