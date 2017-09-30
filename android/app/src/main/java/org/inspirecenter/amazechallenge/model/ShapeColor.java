package org.inspirecenter.amazechallenge.model;

import android.graphics.Color;

/**
 * @author Nearchos
 *         Created: 19-Aug-17
 */

public enum ShapeColor {

    PLAYER_COLOR_BLACK(Color.rgb(0, 0, 0), "black"),
    PLAYER_COLOR_DARK_GRAY(Color.rgb(64, 64, 64), "gray"),
    PLAYER_COLOR_GRAY(Color.rgb(128, 128, 128), "dark gray"),
    PLAYER_COLOR_RED(Color.rgb(128, 0, 0), "red"),
    PLAYER_COLOR_GREEN(Color.rgb(0, 128, 0), "green"),
    PLAYER_COLOR_BLUE(Color.rgb(0, 0, 128), "blue"),
    PLAYER_COLOR_LIGHT_RED(Color.rgb(192, 64, 64), "light red"),
    PLAYER_COLOR_LIGHT_GREEN(Color.rgb(64, 192, 64), "light green"),
    PLAYER_COLOR_LIGHT_BLUE(Color.rgb(64, 64, 192), "light blue");

    private final int code;
    private final String name;

    ShapeColor(final int code, final String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

    public static ShapeColor[] getAll() {
        return new ShapeColor[] {
                PLAYER_COLOR_BLACK,
                PLAYER_COLOR_GRAY,
                PLAYER_COLOR_RED,
                PLAYER_COLOR_GREEN,
                PLAYER_COLOR_BLUE,
                PLAYER_COLOR_LIGHT_RED,
                PLAYER_COLOR_LIGHT_GREEN,
                PLAYER_COLOR_LIGHT_BLUE
        };
    }
}