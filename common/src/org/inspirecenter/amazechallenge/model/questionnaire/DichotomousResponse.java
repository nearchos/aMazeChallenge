package org.inspirecenter.amazechallenge.model.questionnaire;

public enum DichotomousResponse {

    YES("Yes"),
    NO("No"),
    MAYBE("Maybe"),

    ;

    private String name;

    DichotomousResponse(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
