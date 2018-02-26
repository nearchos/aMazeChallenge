package org.inspirecenter.amazechallenge.model;

/**
 * @author Nearchos
 *         Created: 19-Aug-17
 */

public enum AmazeColor {

    BLACK("#000000", "color_black"),
    DARK_RED("#CC0000", "color_darkred"),
    RED("#ff0000", "color_red"),
    ORANGE("#ff6600", "color_orange"),
    GOLD("#996600", "color_gold"),
    YELLOW("#dddd00", "color_yellow"),
    LIME("#00ff00", "color_lime"),
    GREEN("#00cc00", "color_green"),
    DARK_GREEN("#006600", "color_darkgreen"),
    DARK_BLUE("#000066", "color_darkblue"),
    BLUE("#0000ff", "color_blue"),
    TEAL("#00cc99", "color_teal"),
    CYAN("#00ffff", "color_cyan"),
    PURPLE("#990099", "color_purple"),
    INDIGO("#6600cc", "color_indigo"),
    MAGENTA("#ff00ff", "color_magenta"),
    PINK("#ffb6c1", "color_pink"),
    LIGHT_GREY("#cccccc", "color_lightgray"),
    GREY("#666666", "color_darkgray"),
    DARK_GREY("#333333", "color_darkgray");

    private final String hexCode;
    private final String resourceIdAsString;

    AmazeColor(final String hexCode, final String resourceIdAsString) {
        this.hexCode = hexCode;
        this.resourceIdAsString = resourceIdAsString;
    }

    public String getHexCode() {
        return hexCode;
    }

    public String getResourceIdAsString() {
        return resourceIdAsString;
    }

    public static AmazeColor getDefault() {
        return BLACK;
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
        return name();
    }
}