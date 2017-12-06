package org.inspirecenter.amazechallenge.api;

import org.inspirecenter.amazechallenge.model.Game;

/**
 * @author Nearchos
 *         Created: 04-Dec-17
 */

public class ReplyWithGame {

    private String status;

    @com.google.gson.annotations.SerializedName("game-state")
    private Game game;

    public ReplyWithGame() {
    }

    public String getStatus() {
        return status;
    }

    public Game getGame() {
        return game;
    }
}
