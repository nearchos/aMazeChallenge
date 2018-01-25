package org.inspirecenter.amazechallenge.model;

public enum Sound {

    SOUND_BOMB_1("Bomb Sound 1"),
    SOUND_BOMB_2("Bomb Sound 2")

    ;

    Sound(String name) {
        this.name = name;
    }

    private String name;

    @Override
    public String toString() {
        return name;
    }
}
