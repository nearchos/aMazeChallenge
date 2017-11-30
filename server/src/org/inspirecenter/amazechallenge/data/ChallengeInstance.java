package org.inspirecenter.amazechallenge.data;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

@Entity
public class ChallengeInstance {

    @Id public Long id;
    @Index public Long challengeId;
    public Map<String, Player> playerEmailToPlayer; // holds the player details
    public Map<String, String> playerEmailToLatestSubmittedCode; // holds the latest submitted code for each player - a null value means no javascript was submitted yet
    public Map<String, Vector<Long>> playerEmailToSubmissionTimestamps; // holds for each player the ordered list of timestamps indicating when code was submitted
    GameState gameState;

    public ChallengeInstance() {
        super();
        playerEmailToPlayer = new HashMap<>();
        playerEmailToLatestSubmittedCode = new HashMap<>();
        playerEmailToSubmissionTimestamps = new HashMap<>();
        gameState = new GameState();
    }

    public ChallengeInstance(final long challengeId) {
        this();
        this.challengeId = challengeId;
    }

    public void addPlayer(final String playerEmail, final String playerName, final AmazeColor playerColor, final Shape playerShape) {
        assert !playerEmailToLatestSubmittedCode.containsKey(playerEmail);
        playerEmailToPlayer.put(playerEmail, new Player(playerEmail, playerName, playerColor, playerShape));
        playerEmailToLatestSubmittedCode.put(playerEmail, null);
        playerEmailToSubmissionTimestamps = new HashMap<>();
    }

    public boolean containsPlayer(final String playerEmail) {
        return playerEmailToPlayer.containsKey(playerEmail);
    }

    public Set<String> getPlayerEmails() {
        return playerEmailToLatestSubmittedCode.keySet();
    }

    public String getLatestSubmittedCode(final String playerEmail) {
        return playerEmailToLatestSubmittedCode.get(playerEmail);
    }

    public void submitCode(final String playerEmail, final String code) {
        playerEmailToLatestSubmittedCode.put(playerEmail, code);
        if(!playerEmailToSubmissionTimestamps.containsKey(playerEmail)) {
            playerEmailToSubmissionTimestamps.put(playerEmail, new Vector<>());
        }
        playerEmailToSubmissionTimestamps.get(playerEmail).add(System.currentTimeMillis());
    }

    public GameState getGameState() {
        return gameState;
    }
}