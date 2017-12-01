package org.inspirecenter.amazechallenge.util;

import org.inspirecenter.amazechallenge.api.ChallengesReply;
import org.inspirecenter.amazechallenge.model.Challenge;
import org.inspirecenter.amazechallenge.model.Grid;
import org.inspirecenter.amazechallenge.model.Position;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * @author npaspallis on 01/12/2017.
 */
public class JsonParsers {

    public static Challenge parseChallenge(final String json) throws JSONException {
        final JSONObject jsonObject = new JSONObject(json);
        return parseChallenge(jsonObject);
    }

    public static Challenge parseChallenge(final JSONObject jsonObject) throws JSONException {
        final long id = jsonObject.getLong("id");
        final String name = jsonObject.getString("name");
        final int apiVersion = jsonObject.getInt("apiVersion");
        final String description = jsonObject.getString("description");
        final boolean canRepeat = jsonObject.getBoolean("canRepeat");
        final boolean canJoinAfterStart = jsonObject.getBoolean("canJoinAfterStart");
        final boolean canStepOnEachOther = jsonObject.getBoolean("canStepOnEachOther");
        final long startTimestamp = jsonObject.getLong("startTimestamp");
        final long endTimestamp = jsonObject.getLong("endTimestamp");
        final Grid grid = parseGrid(jsonObject.getJSONObject("grid"));
        return new Challenge(id, name, apiVersion, description, canRepeat, canJoinAfterStart, canStepOnEachOther, startTimestamp, endTimestamp, grid);
    }

    private static Grid parseGrid(final JSONObject jsonObject) throws JSONException {
        final int width = jsonObject.getInt("width");
        final int height = jsonObject.getInt("height");
        final List<Integer> grid = new ArrayList<>(width * height);
        final String data = jsonObject.getString("data");
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                grid.add(Integer.parseInt(Character.toString(data.charAt(row * width + col)), 16));
            }
        }
        final JSONObject startingPositionJsonObject = jsonObject.getJSONObject("startingPosition");
        final JSONObject targetPositionJsonObject = jsonObject.getJSONObject("targetPosition");
        final Position startingPosition = new Position(startingPositionJsonObject.getInt("row"), startingPositionJsonObject.getInt("col"));
        final Position targetPosition = new Position(targetPositionJsonObject.getInt("row"), targetPositionJsonObject.getInt("col"));
        return new Grid(width, height, grid, startingPosition, targetPosition);
    }

    public static ChallengesReply parseChallengesMessage(final String json) throws JSONException {
        final JSONObject jsonObject = new JSONObject(json);
        final String status = jsonObject.getString("status");

        final String [] messages;
        if(jsonObject.has("messages")) {
            final JSONArray messagesJsonArray = jsonObject.getJSONArray("messages");
            messages = new String[messagesJsonArray.length()];
            for(int i = 0; i < messagesJsonArray.length(); i++) {
                messages[i] = messagesJsonArray.getString(i);
            }
        } else {
            messages = new String[0];
        }

        final Vector<Challenge> challenges = new Vector<>();
        final JSONArray challengesJsonArray = jsonObject.getJSONArray("challenges");
        if(challengesJsonArray != null) {
            for(int i = 0; i < challengesJsonArray.length(); i++) {
                challenges.add(JsonParsers.parseChallenge(challengesJsonArray.getJSONObject(i)));
            }
        }

        return new ChallengesReply(status, messages, challenges);
    }
}