package org.inspirecenter.amazechallenge.api;

import org.inspirecenter.amazechallenge.model.GameFullState;

/**
 * @author Nearchos
 *         Created: 04-Dec-17
 */

public class ReplyWithGameFullState extends Reply {

    @com.google.gson.annotations.SerializedName("game-full-state")
    private GameFullState gameFullState;

    public ReplyWithGameFullState() { /* empty */ }

    public ReplyWithGameFullState(final GameFullState gameFullState) {
        super(Status.OK);
        this.gameFullState = gameFullState;
    }

    public GameFullState getGameFullState() {
        return gameFullState;
    }
}
