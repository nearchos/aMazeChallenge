package org.inspirecenter.amazechallenge.api;

public enum Status {

    OK("OK"),
    ERROR("ERROR");

    private String name;

    private Status(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}