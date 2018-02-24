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

}