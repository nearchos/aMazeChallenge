package org.inspirecenter.amazechallenge.api;

import org.inspirecenter.amazechallenge.model.Challenge;

import java.util.Arrays;
import java.util.Collection;

/**
 * @author Nearchos
 *         Created: 06-Nov-17
 */

public class ChallengesReply extends Reply {

    private final Collection<Challenge> challenges;

    public ChallengesReply(final String status, final String [] messages, final Collection<Challenge> challenges) {
        this.status = status;
        this.messages = messages;
        this.challenges = challenges;
    }

    public Collection<Challenge> getChallenges() {
        return challenges;
    }

    @Override
    public String toString() {
        return "ChallengesReply{" +
                "status='" + status + '\'' +
                ", messages=" + Arrays.toString(messages) +
                ", challenges=" + challenges +
                '}';
    }
}