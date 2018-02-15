package org.inspirecenter.amazechallenge.model.questionnaire;

public enum LikertResponse {

    STRONGLY_AGREE("Strongly Agree"),
    AGREE("Agree"),
    NEITHER_AGREE_OR_DISAGREE("Neither agree or disagree"),
    DISAGREE("Disagree"),
    STRONGLY_DISAGREE("Strongly Disagree")

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
