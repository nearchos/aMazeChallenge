package org.inspirecenter.amazechallenge.api;

import java.util.Arrays;
import java.util.Vector;

public class ReplyWithErrors extends Reply {

    private Vector<String> errors = new Vector<>();

    public ReplyWithErrors(final String errorMessage) {
        errors.add(errorMessage);
    }

    public ReplyWithErrors(final Vector<String> errorMessages) {
        super(Status.ERROR);
        this.errors.addAll(errorMessages);
    }

    public ReplyWithErrors(final String [] errorMessages) {
        super(Status.ERROR);
        this.errors.addAll(Arrays.asList(errorMessages));
    }

    public Vector<String> getErrors() {
        return errors;
    }
}