package org.inspirecenter.amazechallenge.model;

/**
 * @author Nearchos Paspallis
 * 17/08/2017.
 */
public enum Shape {

    //Player Shapes:
    CIRCLE("circle"),
    EMPTY_CIRCLE("empty_circle"),
    TRIANGLE("triangle"),

    NONE("No shape")

    ;

    private final String code;

    Shape(final String code) {
        this.code = code;
    }

    //TODO: Is this needed??
    public static Shape getShapeByCode(final String shapeCode) {
        for(final Shape shape : values()) {
            if (shape.code.equals(shapeCode)) {
                return shape;
            }
        }
        return TRIANGLE; // return triangle by default
    }

    @Override
    public String toString() {
        return code;
    }
}
