package org.inspirecenter.amazechallenge.model.questionnaire;

public enum MultipleChoiceResponse {

    CHOICE_1("Choice 1"),
    CHOICE_2("Choice 2"),
    CHOICE_3("Choice 3"),
    CHOICE_4("Choice 4")

    ;

    private String name;

    MultipleChoiceResponse(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
