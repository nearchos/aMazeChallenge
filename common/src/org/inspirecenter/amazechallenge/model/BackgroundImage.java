package org.inspirecenter.amazechallenge.model;

public enum BackgroundImage {

    TEXTURE_GRASS("Grass", "texture_grass", BackgroundImageType.JPG),
    TEXTURE_METAL("Metal", "texture_metal", BackgroundImageType.JPG),
    TEXTURE_LAVA("Lava", "texture_lava", BackgroundImageType.JPG);

    public enum BackgroundImageType {
        PNG("png"),
        GIF("gif"),
        JPG("jpg");

        String typeName;
        BackgroundImageType(String typeName) { this.typeName = typeName; }

        @Override
        public String toString() {
            return typeName;
        }
    }

    private final String name;
    private final String resourceName;
    private final BackgroundImageType type;

    BackgroundImage(String name, String resourceName, BackgroundImageType type) {
        this.name = name;
        this.resourceName = resourceName;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getResourceName() {
        return resourceName;
    }

    public BackgroundImageType getType() {
        return type;
    }

    @Override
    public String toString() {
        return name + "." + type;
    }

    public static BackgroundImage fromString(String backgroundImageResourceName) {
        for (final BackgroundImage backgroundImage : BackgroundImage.values()) {
            if (backgroundImage.getResourceName().equals(backgroundImageResourceName)) return backgroundImage;
        }
        throw new RuntimeException("Invalid BackgroundImage provided: " + backgroundImageResourceName);
    }

    public static int getIDFromString(String backgroundImageResourceName) {
        for (int i = 0; i < BackgroundImage.values().length; i++) {
            if (BackgroundImage.values()[i].getResourceName().equals(backgroundImageResourceName)) return i;
        }
        throw new RuntimeException("Invalid BackgroundImage provided: " + backgroundImageResourceName);
    }
}