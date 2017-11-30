package org.inspirecenter.amazechallenge.model;

public enum AmazeIcon {

    ICON_1("Cool name 1", "icon_1"),
    ICON_2("Cool name 2", "icon_2"),
    ICON_3("Cool name 3", "icon_3"),
    ICON_4("Cool name 4", "icon_4"),
    ICON_5("Cool name 5", "icon_5"),
    ICON_6("Cool name 6", "icon_6"),
    ICON_7("Cool name 7", "icon_7"),
    ICON_8("Cool name 8", "icon_8"),
    ICON_9("Cool name 9", "icon_9"),
    ICON_10("Cool name 10", "icon_10");

    private final String name;
    private final String resourceName;

    public String getName() {
        return name;
    }

    public String getResourceName() { return resourceName; }

    AmazeIcon(final String name, final String resourceName) {
        this.name = name;
        this.resourceName = resourceName;
    }

    @Override
    public String toString() {
        return name;
    }

}//end enum AmazeIcon
