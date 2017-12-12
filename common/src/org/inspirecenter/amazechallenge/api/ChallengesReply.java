package org.inspirecenter.amazechallenge.api;

import org.inspirecenter.amazechallenge.model.Challenge;

import java.util.Collection;

/**
 * @author Nearchos
 *         Created: 06-Nov-17
 */

public class ChallengesReply extends Reply {

    private final Collection<Challenge> challenges;

    public ChallengesReply(final Collection<Challenge> challenges) {
        super();
        this.challenges = challenges;
    }

    public Collection<Challenge> getChallenges() {
        return challenges;
    }
}