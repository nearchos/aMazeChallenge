package org.inspirecenter.amazechallenge.ui;

import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.inspirecenter.amazechallenge.model.Challenge;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.ByteArrayInputStream;

import static org.junit.Assert.*;

/**
 */
@RunWith(AndroidJUnit4.class)
public class ChallengeParsingTest {

    public static final String TAG = "aMazeChallenge:test";

    public static final String gridTest = "{\n" +
            "    \"name\": \"Challenge 1 - Easy\",\n" +
            "    \"canRepeat\": true,\n" +
            "    \"canJoinAfterStart\": true,\n" +
            "    \"canStepOnEachOther\": true,\n" +
            "    \"startTimestamp\": 0,\n" +
            "    \"endTimestamp\": 0,\n" +
            "    \"grid\": {\n" +
            "        \"size\": 10,\n" +
            "        \"data\": \"533333395b\\n6953395acd\\n78695a69cc\\n523a69d6ac\\nedd5b6295a\\n522853128d\\nc596a587ac\\n684b5a4bdc\\n7a69e50968\\n7332bee63a\",\n" +
            "        \"startingPosition\": {\n" +
            "            \"row\": 9,\n" +
            "            \"col\": 0\n" +
            "        },\n" +
            "        \"targetPosition\": {\n" +
            "            \"row\": 0,\n" +
            "            \"col\": 9\n" +
            "        }\n" +
            "    }\n" +
            "}";

    @Test
    public void parseTest() throws Exception {

        final String data = TrainingActivity.convertStreamToString(new ByteArrayInputStream(gridTest.getBytes()));
        final Challenge challenge = Challenge.parseJSON(new JSONObject(data));
        System.out.println(challenge);
        Log.d(TAG, "challenge: " + challenge);
        assertTrue(true);

    }
}