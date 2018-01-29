package org.inspirecenter.amazechallenge.model;

public enum Sound {

    SOUND_BOMB_1("Bomb Sound 1", "bomb1"),
    SOUND_BOMB_2("Bomb Sound 2", "bomb2")

    ;

    Sound(String name, String soundResourceName) {
        this.name = name;
        this.soundResourceName = soundResourceName;
    }

    private String name;
    private String soundResourceName;

    @Override
    public String toString() {
        return name;
    }
}
