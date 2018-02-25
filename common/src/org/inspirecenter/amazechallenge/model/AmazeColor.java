package org.inspirecenter.amazechallenge.model;

import org.inspirecenter.amazechallenge.R;

/**
 * @author Nearchos
 *         Created: 19-Aug-17
 */

public enum AmazeColor {

    COLOR_BLACK("black", "#000000", R.string.color_black),
    COLOR_DARK_RED("dark-red", "#CC0000", R.string.color_darkred),
    COLOR_RED("red", "#ff0000", R.string.color_red),
    COLOR_ORANGE("orange", "#ff6600", R.string.color_orange),
    COLOR_GOLD("gold", "#996600", R.string.color_gold),
    COLOR_YELLOW("yellow", "#dddd00", R.string.color_yellow),
    COLOR_LIME("lime", "#00ff00", R.string.color_lime),
    COLOR_GREEN("green", "#00cc00", R.string.color_green),
    COLOR_DARK_GREEN("dark-green", "#006600", R.string.color_darkgreen),
    COLOR_DARK_BLUE("dark-blue", "#000066", R.string.color_darkblue),
    COLOR_BLUE("blue", "#0000ff", R.string.color_blue),
    COLOR_TEAL("teal", "#00cc99", R.string.color_teal),
    COLOR_CYAN("cyan", "#00ffff", R.string.color_cyan),
    COLOR_PURPLE("purple", "#990099", R.string.color_purple),
    COLOR_INDIGO("indigo", "#6600cc", R.string.color_indigo),
    COLOR_MAGENTA("magenta", "#ff00ff", R.string.color_magenta),
    COLOR_PINK("pink", "#ffb6c1", R.string.color_pink),
    COLOR_LIGHT_GREY("light-gray", "#cccccc", R.string.color_lightgray),
    COLOR_GREY("grey", "#666666", R.string.color_darkgray),
    COLOR_DARK_GREY("dark-gray", "#333333", R.string.color_darkgray);

    private final String name;
    private final int friendlyNameResource;
    private final String hexCode;

    AmazeColor(final String name, final String hexCode, final int friendlyNameResource) {
        this.name = name;
        this.hexCode = hexCode;
        this.friendlyNameResource = friendlyNameResource;
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

    public int getFriendlyNameResource() { return friendlyNameResource; }

    public static AmazeColor getDefault() {
        return COLOR_BLACK;
    }

    public static int getIndex(final AmazeColor amazeColor) {
        final AmazeColor [] amazeColors = values();
        for(int i = 0; i < amazeColors.length; i++) {
            if(amazeColors[i] == amazeColor) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public String toString() {
        return name;
    }
}