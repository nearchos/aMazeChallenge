package org.inspirecenter.amazechallenge.model;

import java.io.Serializable;

/**
 * Models a challenge by describing its properties (e.g. grid, num of players, whether its
 * repeatable, etc.)
 *
 * @author Nearchos
 *         Created: 23-Oct-17
 */

@com.googlecode.objectify.annotation.Entity
public class Challenge implements Serializable {

    private static final int DEFAULT_MIN_ACTIVE_PLAYERS = 1;
    private static final int DEFAULT_MAX_ACTIVE_PLAYERS = 10;

    @com.googlecode.objectify.annotation.Id
    public Long id;

    @com.googlecode.objectify.annotation.Index
    private String name;

    private int apiVersion; // used to determine if the client/player can 'support' this challenge
    private String description; // brief description of the challenge
    private boolean canRepeat; // whether a player can play again and again
    private boolean canJoinAfterStart; // whether a player can join after the start of the challenge
    private boolean canStepOnEachOther; // whether players can step on each other
    private int minActivePlayers = DEFAULT_MIN_ACTIVE_PLAYERS; // minimum number of players needed to start game
    private int maxActivePlayers = DEFAULT_MAX_ACTIVE_PLAYERS; // maximum number of players allowed to be active at a time (the rest are in a queue)
    private long startTimestamp; // when the challenge starts being available, or zero if available from the beginning of time (timestamp in UTC)
    private long endTimestamp; // when the challenge ends being available, or zero if available forever (timestamp in UTC)
    private boolean hasQuestionnaire; // true when the challenge requires answering a questionnaire before/after the game
    private Algorithm algorithm;

    private PickableIntensity rewards;
    private PickableIntensity penalties;

    private Grid grid;
    private String lineColor;
    private ChallengeDifficulty difficulty;
    private String createdBy;
    private long createdOn;
    private Audio backgroundAudio;
    private BackgroundImage backgroundImage;

    public Challenge() {
        super();
    }

    public Challenge(String name, int apiVersion, String description, boolean canRepeat,
                     boolean canJoinAfterStart, boolean canStepOnEachOther, int minActivePlayers,
                     int maxActivePlayers, long startTimestamp, long endTimestamp, boolean hasQuestionnaire,
                     PickableIntensity rewards, PickableIntensity penalties, Algorithm algorithm,
                     Grid grid, String lineColor, ChallengeDifficulty difficulty, String createdBy, long createdOn,
                     Audio backgroundAudio, BackgroundImage backgroundImage) {
        this();
        this.name = name;
        this.apiVersion = apiVersion;
        this.description = description;
        this.canRepeat = canRepeat;
        this.canJoinAfterStart = canJoinAfterStart;
        this.canStepOnEachOther = canStepOnEachOther;
        this.minActivePlayers = minActivePlayers;
        this.maxActivePlayers = maxActivePlayers;
        this.startTimestamp = startTimestamp;
        this.endTimestamp = endTimestamp;
        this.hasQuestionnaire = hasQuestionnaire;
        this.algorithm = algorithm;

        this.grid = grid;
        this.lineColor = lineColor;
        this.difficulty = difficulty;
        this.createdBy = createdBy;
        this.createdOn = createdOn;

        this.rewards = rewards;
        this.penalties = penalties;

        this.backgroundAudio = backgroundAudio;

        this.backgroundImage = backgroundImage;
    }

    public Challenge(long id, String name, int apiVersion, String description, boolean canRepeat,
                     boolean canJoinAfterStart, boolean canStepOnEachOther, long startTimestamp,
                     long endTimestamp, boolean hasQuestionnaire, PickableIntensity rewards,
                     PickableIntensity penalties, Algorithm algorithm,
                     Grid grid, String lineColor, ChallengeDifficulty difficulty, String createdBy,
                     long createdOn,
                     Audio backgroundAudio, BackgroundImage backgroundImage) {
        this(name, apiVersion, description, canRepeat, canJoinAfterStart, canStepOnEachOther,
                DEFAULT_MIN_ACTIVE_PLAYERS, DEFAULT_MAX_ACTIVE_PLAYERS, startTimestamp,
                endTimestamp, hasQuestionnaire, rewards, penalties, algorithm, grid, lineColor,
                difficulty, createdBy, createdOn, backgroundAudio, backgroundImage);
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getApiVersion() {
        return apiVersion;
    }

    public String getDescription() {
        return description;
    }

    public ChallengeDifficulty getDifficulty() {
        return difficulty;
    }

    public boolean canRepeat() {
        return canRepeat;
    }

    public boolean isCanJoinAfterStart() {
        return canJoinAfterStart;
    }

    public boolean isCanStepOnEachOther() {
        return canStepOnEachOther;
    }

    public int getMinActivePlayers() {
        return minActivePlayers;
    }

    public int getMaxActivePlayers() {
        return maxActivePlayers;
    }

    public long getStartTimestamp() {
        return startTimestamp;
    }

    public boolean hasStarted() {
        return System.currentTimeMillis() > startTimestamp;
    }

    public long getEndTimestamp() {
        return endTimestamp;
    }

    public boolean hasEnded() {
        return System.currentTimeMillis() > endTimestamp;
    }

    public boolean isActive() {
        return hasStarted() && !hasEnded();
    }

    public Grid getGrid() {
        return grid;
    }

    public String getLineColor() {
        return lineColor;
    }

    public BackgroundImage getBackgroundImage() {
        return backgroundImage;
    }

    public PickableIntensity getRewardsIntensity() {
        return rewards;
    }

    public PickableIntensity getPenaltiesIntensity() {
        return penalties;
    }

    public int getMaxRewards() {
        switch (rewards) {
            case LOW:
                return (getGrid().getHeight()) / 5;
            case MEDIUM:
                return (getGrid().getHeight()) / 3;
            case HIGH:
                return (getGrid().getHeight() * 2) / 2;
            default:
                return 0;
        }
    }

    public int getMaxPenalties() {
        switch (penalties) {
            case LOW:
                return (getGrid().getHeight()) / 5;
            case MEDIUM:
                return (getGrid().getHeight()) / 3;
            case HIGH:
                return (getGrid().getHeight());
            default:
                return 0;
        }
    }

    public long getCreatedOn() {
        return createdOn;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public Audio getBackgroundAudio() {
        return backgroundAudio;
    }

    public boolean getHasQuestionnaire() {
        return hasQuestionnaire;
    }

    public Algorithm getAlgorithm() {
        return algorithm;
    }

    @Override
    public String toString() {
        return "Challenge{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", apiVersion=" + apiVersion +
                ", description='" + description + '\'' +
                ", canRepeat=" + canRepeat +
                ", canJoinAfterStart=" + canJoinAfterStart +
                ", canStepOnEachOther=" + canStepOnEachOther +
                ", minActivePlayers=" + minActivePlayers +
                ", maxActivePlayers=" + maxActivePlayers +
                ", startTimestamp=" + startTimestamp +
                ", endTimestamp=" + endTimestamp +
                ", hasQuestionnaire=" + hasQuestionnaire +
                ", algorithm=" + algorithm +
                ", rewards=" + rewards +
                ", penalties=" + penalties +
                ", grid=" + grid +
                ", lineColor='" + lineColor + '\'' +
                ", difficulty=" + difficulty +
                ", createdBy='" + createdBy + '\'' +
                ", createdOn=" + createdOn +
                ", backgroundAudio=" + backgroundAudio +
                ", backgroundImage=" + backgroundImage +
                '}';
    }
}