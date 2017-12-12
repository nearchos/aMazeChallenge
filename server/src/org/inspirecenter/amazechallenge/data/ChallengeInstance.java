package org.inspirecenter.amazechallenge.data;

import org.inspirecenter.amazechallenge.model.AmazeColor;
import org.inspirecenter.amazechallenge.model.AmazeIcon;
import org.inspirecenter.amazechallenge.model.Player;
import org.inspirecenter.amazechallenge.model.Shape;

import java.util.*;

@com.googlecode.objectify.annotation.Entity
public class ChallengeInstance {

    @com.googlecode.objectify.annotation.Id
    public Long id;
    @com.googlecode.objectify.annotation.Index
    public Long challengeId;
    public Map<String,Player> playerEmailToPlayer = new HashMap<>();
    public Map<String, String> playerEmailToLatestSubmittedCode = new HashMap<>();
    public List<SubmissionDetails> sortedSubmissions = Collections.synchronizedList(new Vector<>());

    public ChallengeInstance() {
        super();
    }

    public ChallengeInstance(final long challengeId) {
        this();
        this.challengeId = challengeId;
    }

    public void addPlayer(final String playerEmail, final String playerName, final AmazeColor playerColor, final AmazeIcon playerIcon, final Shape playerShape) {
        final Player player = new Player(playerEmail, playerName, playerColor, playerIcon, playerShape);
        playerEmailToPlayer.put(playerEmail, player);
    }

    public boolean containsPlayer(final String playerEmail) {
        return playerEmailToPlayer.containsKey(playerEmail);
    }

    public boolean hasPendingPlayers() {
        return sortedSubmissions.size() > 0;
    }

    public String peekPendingPlayerEmail() {
        return sortedSubmissions.get(0).getEmail();
    }

    public SubmissionDetails pollPendingPlayer() {
        return sortedSubmissions.remove(0);
    }

    public Player getPlayer(final String playerEmail) {
        return playerEmailToPlayer.get(playerEmail);
    }

    public Set<String> getPlayerEmails() {
        return playerEmailToPlayer.keySet();
    }

    public String getLatestSubmittedCode(final String playerEmail) {
        return playerEmailToLatestSubmittedCode.get(playerEmail);
    }

    public void submitCode(final String playerEmail, final String code) {
        sortedSubmissions.add(new SubmissionDetails(System.currentTimeMillis(), playerEmail));
        playerEmailToLatestSubmittedCode.put(playerEmail, code);
    }

    public static class SubmissionDetails {
        private long timestamp;
        private String email;

        public SubmissionDetails() {
            super();
        }

        SubmissionDetails(final long timestamp, final String email) {
            this();
            this.timestamp = timestamp;
            this.email = email;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public String getEmail() {
            return email;
        }
    }
}