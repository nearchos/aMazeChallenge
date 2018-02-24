package org.inspirecenter.amazechallenge.model.questionnaire;

import java.io.Serializable;

/**
 * @author Nearchos
 *         Created: 24-Feb-18
 */

public class QuestionEntry implements Serializable {

    String questionText;
    String answerText;

    public QuestionEntry(String questionText, String answerText) {
        this.questionText = questionText;
        this.answerText = answerText;
    }

    public String getQuestionText() {
        return questionText;
    }

    public String getAnswerText() {
        return answerText;
    }
}