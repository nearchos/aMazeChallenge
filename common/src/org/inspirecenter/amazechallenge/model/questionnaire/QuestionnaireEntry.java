package org.inspirecenter.amazechallenge.model.questionnaire;

/**
 * @author Nearchos
 *         Created: 24-Feb-18
 */

@com.googlecode.objectify.annotation.Entity
public class QuestionnaireEntry {

    @com.googlecode.objectify.annotation.Id
    String id; // device unique id

    Long challengeId;

    QuestionEntry [] questionEntries;

    public QuestionnaireEntry(String id, Long challengeId, QuestionEntry[] questionEntries) {
        this.id = id;
        this.challengeId = challengeId;
        this.questionEntries = questionEntries;
    }

    public String getId() {
        return id;
    }

    public Long getChallengeId() {
        return challengeId;
    }

    public QuestionEntry [] getQuestionEntries() {
        return questionEntries;
    }
}