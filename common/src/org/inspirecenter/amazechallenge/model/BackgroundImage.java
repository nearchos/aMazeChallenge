package org.inspirecenter.amazechallenge.model;

public enum BackgroundImage {

    TEXTURE_GRASS("Grass", "texture_grass", BackgroundImageType.JPG),
    TEXTURE_METAL("Metal", "texture_metal", BackgroundImageType.JPG),
    TEXTURE_LAVA("Lava", "texture_lava", BackgroundImageType.JPG)

    ;

    public enum BackgroundImageType {
        PNG("png"),
        GIF("gif"),
        JPG("jpg"),
        ;
        String typeName;
        BackgroundImageType(String typeName) { this.typeName = typeName; }
        @Override
        public String toString() { return typeName; }
    }

    String resourceName;
    String name;
    BackgroundImageType type;

    BackgroundImage(String name, String resourceName, BackgroundImageType type) {
        this.name = name;
        this.resourceName = resourceName;
        this.type = type;
    }

    public String getResourceName() { return resourceName; }

    public String getName() { return name; }

    public BackgroundImageType getType() { return type; }

    @Override
    public String toString() { return name; }

    public static BackgroundImage fromResourceText(String backgroundImageResourceName) {
        for (BackgroundImage i : BackgroundImage.values()) {
            if (i.getResourceName().equals(backgroundImageResourceName)) return i;
        }
        throw new RuntimeException("Invalid BackgroundImage provided: " + backgroundImageResourceName);
    }

}
