package org.inspirecenter.amazechallenge.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Models a challenge by describing its properties (e.g. grid, num of players, whether its
 * repeatable, etc.)
 *
 * @author Nearchos
 *         Created: 23-Oct-17
 */
public class Challenge implements Serializable {

    private int apiVersion; // used to determine if the player can 'support' this challenge
    private String name;
    private boolean canRepeat; // whether a player can play again and again
    private boolean canJoinAfterStart; // whether a player can join after the start of the challenge
    private boolean canStepOnEachOther; // whether players can step on each other
    private long startTimestamp; // when the challenge starts being available, or zero if available from the beginning of time (timestamp in UTC)
    private long endTimestamp; // when the challenge ends being available, or zero if available forever (timestamp in UTC)
    private Maze grid;

    public Challenge(String name, boolean canRepeat, boolean canJoinAfterStart, boolean canStepOnEachOther, long startTimestamp, long endTimestamp, Maze grid) {
        this.name = name;
        this.canRepeat = canRepeat;
        this.canJoinAfterStart = canJoinAfterStart;
        this.canStepOnEachOther = canStepOnEachOther;
        this.startTimestamp = startTimestamp;
        this.endTimestamp = endTimestamp;
        this.grid = grid;
    }

    public String getName() {
        return name;
    }

    public boolean isCanRepeat() {
        return canRepeat;
    }

    public boolean isCanJoinAfterStart() {
        return canJoinAfterStart;
    }

    public boolean isCanStepOnEachOther() {
        return canStepOnEachOther;
    }

    public long getStartTimestamp() {
        return startTimestamp;
    }

    public long getEndTimestamp() {
        return endTimestamp;
    }

    public Maze getGrid() {
        return grid;
    }

    @Override
    public String toString() {
        return "Challenge{" +
                "name='" + name + '\'' +
                ", canRepeat=" + canRepeat +
                ", canJoinAfterStart=" + canJoinAfterStart +
                ", canStepOnEachOther=" + canStepOnEachOther +
                ", startTimestamp=" + startTimestamp +
                ", endTimestamp=" + endTimestamp +
                ", grid=" + grid +
                '}';
    }

    public static Challenge parseJSON(final JSONObject jsonObject) throws JSONException {
        final String name = jsonObject.getString("name");
        final boolean canRepeat = jsonObject.getBoolean("canRepeat");
        final boolean canJoinAfterStart = jsonObject.getBoolean("canJoinAfterStart");
        final boolean canStepOnEachOther = jsonObject.getBoolean("canStepOnEachOther");
        final long startTimestamp = jsonObject.getLong("startTimestamp");
        final long endTimestamp = jsonObject.getLong("endTimestamp");
        final Maze grid = Maze.parseJSON(jsonObject.getJSONObject("grid"));
        return new Challenge(name, canRepeat, canJoinAfterStart, canStepOnEachOther, startTimestamp, endTimestamp, grid);
    }
}