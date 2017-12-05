package org.inspirecenter.amazechallenge.api;

import org.inspirecenter.amazechallenge.model.Game;

import java.io.Serializable;

/**
 * @author npaspallis
 * 05/12/2017
 */

public class ReplyWithGame implements Serializable {

    private String status;
    private Game game;

    public ReplyWithGame(String status, Game game) {
        this.status = status;
        this.game = game;
    }

    public String getStatus() {
        return status;
    }

    public Game getGame() {
        return game;
    }
}