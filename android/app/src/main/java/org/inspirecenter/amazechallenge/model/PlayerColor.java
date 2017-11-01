package org.inspirecenter.amazechallenge.model;

import android.graphics.Color;

/**
 * Created by Nicos on 31-Oct-17.
 */

public class PlayerColor {

    private String name;
    private String hexCode;

    private PlayerColor(String name, String hexCode) {
        this.name = name;
        this.hexCode = hexCode;
    }//end PlayerColor()

    public String getName() { return name; }

    public String getHex() { return hexCode; }

    public int toRGB() { return Color.parseColor(hexCode); }

    public static final PlayerColor[] PLAYER_COLORS = {
            new PlayerColor("Dark Red", "#CC0000"),
            new PlayerColor("Red", "#FF0000"),
            new PlayerColor("Orange", "#FF6600"),
            new PlayerColor("Gold", "#996600"),
            new PlayerColor("Yellow", "#DDDD00"),
            new PlayerColor("Lime", "#00FF00"),
            new PlayerColor("Green", "#00CC00"),
            new PlayerColor("Dark Green", "#006600"),
            new PlayerColor("Dark Blue", "#000066"),
            new PlayerColor("Blue", "#0000FF"),
            new PlayerColor("Teal", "#00CC99"),
            new PlayerColor("Cyan", "#00FFFF"),
            new PlayerColor("Purple", "#990099"),
            new PlayerColor("Indigo", "#6600CC"),
            new PlayerColor("Magenta", "#FF00FF"),
            new PlayerColor("Pink", "#FFB6C1"),
            new PlayerColor("Light Grey", "#CCCCCC"),
            new PlayerColor("Grey", "#666666"),
            new PlayerColor("Dark Grey", "#333333")
    };

}//end class PlayerColor
