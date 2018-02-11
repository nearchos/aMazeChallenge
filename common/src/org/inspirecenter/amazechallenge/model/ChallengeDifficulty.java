package org.inspirecenter.amazechallenge.model;

public enum ChallengeDifficulty {

    VERY_EASY("Very Easy"),
    EASY("Easy"),
    MEDIUM("Medium"),
    HARD("Hard"),
    VERY_HARD("Very Hard");

    private String difficultyName;

    ChallengeDifficulty(String difficultyName) {
        this.difficultyName = difficultyName;
    }

    @Override
    public String toString() {
        return difficultyName;
    }

    public static ChallengeDifficulty getChallengeDifficulty(String difficulty) {
        if (difficulty.equals(VERY_EASY.difficultyName)) return VERY_EASY;
        else if (difficulty.equals(EASY.difficultyName)) return EASY;
        else if (difficulty.equals(MEDIUM.difficultyName)) return MEDIUM;
        else if (difficulty.equals(HARD.difficultyName)) return HARD;
        else if (difficulty.equals(VERY_HARD.difficultyName)) return VERY_HARD;
        else throw new RuntimeException("The difficulty detected with value \"" + difficulty + "\" is not a valid difficulty.");
    }

}
