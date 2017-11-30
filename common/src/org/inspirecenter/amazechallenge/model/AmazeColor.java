package org.inspirecenter.amazechallenge.model;

/**
 * @author Nearchos
 *         Created: 19-Aug-17
 */

public enum AmazeColor {

    COLOR_BLACK("black", "#000000"),
    COLOR_DARK_RED("dark red", "#CC0000"),
    COLOR_RED("red", "#ff0000"),
    COLOR_ORANGE("orange", "#ff6600"),
    COLOR_GOLD("gold", "#996600"),
    COLOR_YELLOW("yellow", "#dddd00"),
    COLOR_LIME("lime", "#00ff00"),
    COLOR_GREEN("green", "#00cc00"),
    COLOR_DARK_GREEN("dark green", "#006600"),
    COLOR_DARK_BLUE("dark blue", "#000066"),
    COLOR_BLUE("blue", "#0000ff"),
    COLOR_TEAL("teal", "#00cc99"),
    COLOR_CYAN("cyan", "#00ffff"),
    COLOR_PURPLE("purple", "#990099"),
    COLOR_INDIGO("indigo", "#6600cc"),
    COLOR_MAGENTA("magenta", "#ff00ff"),
    COLOR_PINK("pink", "#ffb6c1"),
    COLOR_LIGHT_GREY("light gray", "#cccccc"),
    COLOR_GREY("grey", "#666666"),
    COLOR_DARK_GREY("dark gray", "#333333");

    private final String name;
    private final String hexCode;

    AmazeColor(final String name, final String hexCode) {
        this.name = name;
        this.hexCode = hexCode;
    }

    public String getCode() {
        return hexCode;
    }

    public String getName() {
        return name;
    }

    public static AmazeColor getByName(final String name) {
        for(final AmazeColor amazeColor : values()) {
            if(amazeColor.name.equalsIgnoreCase(name)) return amazeColor;
        }
        return COLOR_BLACK;
    }

    public static AmazeColor getDefaultColor() {
        return COLOR_BLACK;
    }

    @Override
    public String toString() {
        return name;
    }
}