package org.inspirecenter.amazechallenge.controller;

public interface GameEndListener {

    public void onPlayerHasWon(String playerID);

    public void onPlayerHasLost(String playerID);

}
