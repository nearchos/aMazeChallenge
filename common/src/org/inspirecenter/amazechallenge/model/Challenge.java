package org.inspirecenter.amazechallenge.model;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

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

@Entity
public class Challenge implements Serializable {

    @Id private final Long id;
    private final String name;
    private final int apiVersion; // used to determine if the player can 'support' this challenge
    private final String description; // brief description of the challenge
    private final boolean canRepeat; // whether a player can play again and again
    private final boolean canJoinAfterStart; // whether a player can join after the start of the challenge
    private final boolean canStepOnEachOther; // whether players can step on each other
    private final long startTimestamp; // when the challenge starts being available, or zero if available from the beginning of time (timestamp in UTC)
    private final long endTimestamp; // when the challenge ends being available, or zero if available forever (timestamp in UTC)
    private final Grid grid;

    private Challenge(long id, String name, int apiVersion, String description, boolean canRepeat, boolean canJoinAfterStart, boolean canStepOnEachOther, long startTimestamp, long endTimestamp, Grid grid) {
        this.id = id;
        this.apiVersion = apiVersion;
        this.name = name;
        this.description = description;
        this.canRepeat = canRepeat;
        this.canJoinAfterStart = canJoinAfterStart;
        this.canStepOnEachOther = canStepOnEachOther;
        this.startTimestamp = startTimestamp;
        this.endTimestamp = endTimestamp;
        this.grid = grid;
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

    public Grid getGrid() {
        return grid;
    }

    @Override
    public String toString() {
        return "Challenge{" +
                "name='" + name + '\'' +
                ", apiVersion=" + apiVersion+
                ", description=" + description+
                ", canRepeat=" + canRepeat +
                ", canJoinAfterStart=" + canJoinAfterStart +
                ", canStepOnEachOther=" + canStepOnEachOther +
                ", startTimestamp=" + startTimestamp +
                ", endTimestamp=" + endTimestamp +
                ", grid=" + grid +
                '}';
    }

    public static Challenge parseJSON(final JSONObject jsonObject) throws JSONException {
        final long id = jsonObject.getLong("id");
        final String name = jsonObject.getString("name");
        final int apiVersion = jsonObject.getInt("apiVersion");
        final String description = jsonObject.getString("description");
        final boolean canRepeat = jsonObject.getBoolean("canRepeat");
        final boolean canJoinAfterStart = jsonObject.getBoolean("canJoinAfterStart");
        final boolean canStepOnEachOther = jsonObject.getBoolean("canStepOnEachOther");
        final long startTimestamp = jsonObject.getLong("startTimestamp");
        final long endTimestamp = jsonObject.getLong("endTimestamp");
        final Grid grid = Grid.parseJSON(jsonObject.getJSONObject("grid"));
        return new Challenge(id, name, apiVersion, description, canRepeat, canJoinAfterStart, canStepOnEachOther, startTimestamp, endTimestamp, grid);
    }
}