package org.inspirecenter.amazechallenge.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.istack.internal.NotNull;
import org.inspirecenter.amazechallenge.data.GameState;

import java.util.Vector;

public class ReplyBuilder {

    public static String createReplyWithError(@NotNull final String errorMessage) {
        return "{ \"status\": \"error\", \"messages\": [ \"" + errorMessage + "\" ] }";
    }

    public static String createReplyWithErrors(@NotNull final Vector<String> errorMessages) {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{ \"status\": \"error\", \"messages\": [");
        for(int i = 0; i < errorMessages.size(); i++) {
            final String errorMessage = errorMessages.get(i);
            stringBuilder.append("\"").append(errorMessage).append("\"");
        }
        stringBuilder.append("] }");
        return stringBuilder.toString();
    }

    public static String createReply() {
        return "{ \"status\": \"ok\" }";
    }

    public static String createReplyWithOneParameter(@NotNull final String name, final String value) {
        return "{ \"status\": \"ok\", \"" + name + "\": " + value + " }";
    }

    static String createReplyWithGameState(final GameState gameState) {
        final Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
        final String gameStateJson = gson.toJson(gameState);
        return "{ \"status\": \"ok\", \"game-state\": " + gameStateJson + " }";
    }
}