package org.inspirecenter.amazechallenge.api;

import com.google.gson.Gson;
import org.inspirecenter.amazechallenge.model.Challenge;
import org.inspirecenter.amazechallenge.model.Game;

import java.util.List;
import java.util.Vector;

public class ReplyBuilder {

    static String createReplyWithError(final String errorMessage) {
        return "{ \"status\": \"error\", \"messages\": [ \"" + errorMessage + "\" ] }";
    }

    public static String createReplyWithErrors(final Vector<String> errorMessages) {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{ \"status\": \"error\", \"messages\": [");
        for(int i = 0; i < errorMessages.size(); i++) {
            final String errorMessage = errorMessages.get(i);
            stringBuilder.append("\"").append(errorMessage).append("\"").append(i < errorMessages.size() - 1 ? ", " : " ");
        }
        stringBuilder.append("] }");
        return stringBuilder.toString();
    }

    static String createReply() {
        return "{ \"status\": \"ok\" }";
    }

    static String createReplyWithOneParameter(final String name, final String value) {
        return "{ \"status\": \"ok\", \"" + name + "\": " + value + " }";
    }

    public static String createReplyWithGame(final Game game) {
        final Gson gson = new Gson();
        final String gameStateJson = gson.toJson(game);
        return "{ \"status\": \"ok\", \"game-state\": " + gameStateJson + " }";
    }
}