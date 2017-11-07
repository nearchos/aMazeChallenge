package org.inspirecenter.amazechallenge.api;

import org.inspirecenter.amazechallenge.model.Challenge;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collection;
import java.util.Vector;

/**
 * @author Nearchos
 *         Created: 06-Nov-17
 */

public class JsonParser {

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
                challenges.add(Challenge.parseJSON(challengesJsonArray.getJSONObject(i)));
            }
        }

        return new ChallengesReply(status, messages, challenges);
    }
}
