package org.inspirecenter.amazechallenge.model;

/**
 * @author Nearchos Paspallis
 * 17/08/2017.
 */
public enum Shape {
    CIRCLE("circle"),
    EMPTY_CIRCLE("empty_circle"),
    TRIANGLE("triangle");

    private final String code;

    Shape(final String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return code;
    }
}
