package org.inspirecenter.amazechallenge.model.questionnaire;

public enum LikertResponse {

    VERY_POSITIVE("Very Positive"),
    POSITIVE("Positive"),
    NEUTRAL("Neutral"),
    NEGATIVE("Negative"),
    VERY_NEGATIVE("Very Negative")

    ;

    private String name;

    LikertResponse(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
