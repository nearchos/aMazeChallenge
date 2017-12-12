package org.inspirecenter.amazechallenge.api;

import java.io.Serializable;

/**
 * @author Nearchos
 *         Created: 06-Nov-17
 */

public class Reply implements Serializable {

    private final Status status;

    Reply() {
        this(Status.OK);
    }

    Reply(final Status status) {
        this.status = status;
    }

    public Status getStatus() {
        return status;
    }

    public boolean isOk() {
        return Status.OK == status;
    }
}