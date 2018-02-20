package org.inspirecenter.amazechallenge.model;

public enum Audio {

    //None
    AUDIO_NONE("None","",AudioFormat.UNDEFINED_FORMAT, AudioType.NONE),

    //Ambient
    AMBIENT_ALPINE_FOREST("Alpine-Forest", "alpine_forest", AudioFormat.MP3, AudioType.AMBIENT),
    AMBIENT_CAVE("Cave", "cave", AudioFormat.MP3, AudioType.AMBIENT),
    AMBIENT_CITY("City", "city", AudioFormat.MP3, AudioType.AMBIENT),
    AMBIENT_FIRE("Fire", "fire", AudioFormat.MP3, AudioType.AMBIENT),
    AMBIENT_HIGHTECH("High-Tech", "high_tech", AudioFormat.MP3, AudioType.AMBIENT),
    AMBIENT_NIGHT("Night", "night", AudioFormat.MP3, AudioType.AMBIENT),
    AMBIENT_PRISON("Prison", "prison", AudioFormat.MP3, AudioType.AMBIENT),
    AMBIENT_RIVER("River", "river", AudioFormat.MP3, AudioType.AMBIENT),
    AMBIENT_SANDSTORM("Sandstorm", "sandstorm", AudioFormat.MP3, AudioType.AMBIENT),
    AMBIENT_SNOW("Snow", "snow", AudioFormat.MP3, AudioType.AMBIENT),
    AMBIENT_STORM("Storm", "storm", AudioFormat.MP3, AudioType.AMBIENT),
    AMBIENT_STREET("Street", "street", AudioFormat.MP3, AudioType.AMBIENT),
    AMBIENT_TROPICAL_FOREST("Tropical-Forest", "tropical_forest", AudioFormat.MP3, AudioType.AMBIENT),
    AMBIENT_UNDERWATER("Underwater", "underwater", AudioFormat.MP3, AudioType.AMBIENT),

    //Events
    EVENT_WIN("Win", "win", AudioFormat.WAV, AudioType.EVENT),
    EVENT_LOSE("Lose", "lose", AudioFormat.WAV, AudioType.EVENT),
    EVENT_FOOD("Food", "food", AudioFormat.WAV, AudioType.EVENT),
    EVENT_COIN5("Coin5", "coin5", AudioFormat.WAV, AudioType.EVENT),
    EVENT_COIN10("Coin10", "coin10", AudioFormat.WAV, AudioType.EVENT),
    EVENT_COIN20("Coin20", "coin20", AudioFormat.WAV, AudioType.EVENT),
    EVENT_GIFTBOX("Giftbox", "giftbox", AudioFormat.WAV, AudioType.EVENT),
    EVENT_SPEEDHACK("Speedhack", "speedhack", AudioFormat.WAV, AudioType.EVENT),
    EVENT_BOMB("Bomb", "bomb", AudioFormat.WAV, AudioType.EVENT),
    EVENT_TRAP("Trap", "trap", AudioFormat.WAV, AudioType.EVENT),


    ;

    public enum AudioFormat { WAV, MP3, MP4, OGG, UNDEFINED_FORMAT }

    public enum AudioType { AMBIENT, EVENT, NONE }

    private AudioFormat audioFormat;
    private AudioType audioType;
    private String name;
    private String soundResourceName;

    Audio(String name, String soundResourceName, AudioFormat audioFormat, AudioType audioType) {
        this.name = name;
        this.soundResourceName = soundResourceName;
        this.audioFormat = audioFormat;
        this.audioType = audioType;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

    public String getSoundResourceName() { return soundResourceName; }

    public AudioFormat getAudioFormat() { return audioFormat; }

    public AudioType getAudioType() { return audioType; }

    public static int getIdFromString(String resourceName) {
        for (int i = 0; i < Audio.values().length; i++) {
            if (Audio.values()[i].getSoundResourceName().equals(resourceName)) return i;
        }
        throw new RuntimeException("Invalid audio resource name provided.");
    }
}