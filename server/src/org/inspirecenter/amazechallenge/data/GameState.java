package org.inspirecenter.amazechallenge.data;

import com.google.gson.annotations.SerializedName;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import org.inspirecenter.amazechallenge.model.Position;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Entity
public class GameState implements Serializable {

    @Id
    @SerializedName("id")
    public Long id;

    public transient Key<ChallengeInstance> challengeInstanceKey; // parent

    @SerializedName("playerEmailToState")
    public Map<String, PlayerState> playerEmailToState;

    public GameState() {
        super();
        this.playerEmailToState = new HashMap<>();
    }

    public GameState(Key<ChallengeInstance> challengeInstanceKey) {
        this();
        this.challengeInstanceKey = challengeInstanceKey;
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

    public static void main(String[] args) {
        GameState gameState = new GameState();
        gameState.addPlayerState("nearchos@test.com", new PlayerState(new Position(1, 2)));
        final com.google.gson.Gson gson = new com.google.gson.GsonBuilder().enableComplexMapKeySerialization().create();
        System.out.println("gson -> "+ gson.toJson(gameState));
    }
}