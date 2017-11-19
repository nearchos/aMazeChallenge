package org.inspirecenter.amazechallenge.data;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

import java.util.HashMap;
import java.util.Map;

@Entity
public class GameState {

    @Id public Long id;
    public Key<ChallengeInstance> challengeInstanceKey; // parent
    public Map<String,PlayerState> playerEmailToState;

    public GameState() {
        super();
    }

    public GameState(Key<ChallengeInstance> challengeInstanceKey) {
        this.challengeInstanceKey = challengeInstanceKey;
        this.playerEmailToState = new HashMap<>();
    }

    public Long getId() {
        return id;
    }

    public void addPlayerState(final String playerEmail, final PlayerState playerState) {
        this.playerEmailToState.put(playerEmail, playerState);
    }

    public boolean containsPlayerState(final String playerEmail) {
        return this.playerEmailToState.containsKey(playerEmail);
    }

    public PlayerState getPlayerState(final String playerEmail) {
        return this.playerEmailToState.get(playerEmail);
    }
}